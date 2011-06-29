
package sequenceplanner.efaconverter2.reduction;

import java.util.Iterator;
import java.util.LinkedList;
import sequenceplanner.efaconverter2.EFA.EFAVariables;
import sequenceplanner.efaconverter2.SpEFA.*;
import sequenceplanner.efaconverter2.condition.*;
import sequenceplanner.model.Model;

/**
 *
 * @author Mohammad Reza Shoaei
 * @version 21062011
 */

public class Reduction {
    
    private SpEFAutomata automata;
    
    /**
     * Constructor for the Reduction class. The method is based on the Reduced-Order Synthesis of Operation Sequences paper (Shoaei et al., ETFA 2011)
     * @param model SequencePlanner model of the system
     */
    public Reduction(Model model){
        this.automata = null;
        DefaultModelParser parser = new DefaultModelParser(model);
        automata = parser.getSpEFAutomata();
    }
    
    /**
     * Constructor for the Reduction class. The method is based on the Reduced-Order Synthesis of Operation Sequences paper (Shoaei et al., ETFA 2011)
     * @param automata SpEFAutomata of the system
     */
    public Reduction(SpEFAutomata automata){
        this.automata = automata;
    }
    
    /**
     * Return the reduced model in SpEFAutomata
     * @return SpEFAutomta of the reduced model
     */
    public SpEFAutomata getReducedModel(){
        
        if(automata == null || automata.getAutomatons().isEmpty()) return automata;
        
        synchronizeModel();
        reduceModel();
        return automata;
    }
    
    private void synchronizeModel(){
        RelationGraph graph = new RelationGraph(automata);
        LinkedList<LinkedList<String>> paths = graph.getSequentialPaths();
            
        for(LinkedList<String> path : paths){
            SpEFA seq = null;
            for(String operation : path){
                SpEFA current = automata.getSpEFA(operation);
                if(current == null)
                    throw new NullPointerException(operation + " > No such operation in the graph.");

                seq = appendSpEFA(seq, current);
            }

            for(String operation : path)
                automata.removeAutomaton(operation);

            automata.addAutomaton(seq);
        }
    }

    private SpEFA appendSpEFA(SpEFA efa1, SpEFA efa2){
        if(efa1 == null)
            return efa2;

        if(efa2 == null)
            return efa1;

        SpEFA efa = new SpEFA(efa1.getName() + EFAVariables.EFA_NAME_DIVIDER + efa2.getName());
        
        int state = 0;
        SpLocation last = null;
        Iterator<SpTransition> itr = null;
        /*
         * map[0]: Old variable name
         * map[1]: New variable name
         * map[2]: Old variable value
         * map[3]: New variable value
         */
        
        LinkedList<String[]> map = new LinkedList<String[]>();
        LinkedList<ConditionStatment> conditions = new LinkedList<ConditionStatment>();
        
        itr = efa1.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
            conditions.addAll(current.getConditionGuard().getAllConditionStatments());
            map.add(new String[] {efa1.getName(), efa.getName(), Integer.toString(current.getFrom().getValue()), Integer.toString(current.getFrom().getValue())});
            if(!itr.hasNext()){
                map.add(new String[] {efa1.getName(), efa.getName(), Integer.toString(current.getTo().getValue()), Integer.toString(current.getTo().getValue())});
                last = current.getTo();
                state = last.getValue();
            }
            efa.addTransition(current);
        }
        
        itr = efa2.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
            if(current.getFrom().isInitialLocation()){
                map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getFrom().getValue()), Integer.toString(last.getValue())});
                
                if(!current.getFrom().isAccepting())
                    last.clearAccepting();
                
                ConditionExpression newGuard = current.getConditionGuard();
                newGuard.removeElement(new ConditionStatment(efa1.getName(), ConditionStatment.Operator.Equal, Integer.toString(last.getValue())));
                newGuard.removeElement(new ConditionStatment(efa1.getName(), ConditionStatment.Operator.GreaterEq, Integer.toString(last.getValue())));
                
                for(ConditionStatment s : conditions)
                    if(s.isHierarchicalStatement())
                        newGuard.removeElement(s);
                
                Condition newc = new Condition(newGuard, current.getConditionAction());
                SpTransition newtran = new SpTransition(current.getEvent(), last, current.getTo(), newc);
                
                efa.addTransition(newtran);
                
                last.setName(last + EFAVariables.EFA_LOCATION_DIVIDER + current.getFrom());
            } else {
                map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getFrom().getValue()), Integer.toString(++state)});
                current.getFrom().setValue(state);
                if(!itr.hasNext()){
                    map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getTo().getValue()), Integer.toString(++state), Boolean.toString(current.getTo().isAccepting())});
                    current.getTo().setValue(state);
                }
                efa.addTransition(current);
            }
        }
        updateGlobalGuards(map);
        return efa;
    }
    
    private void updateGlobalGuards(LinkedList<String[]> map) {
        for(SpEFA efa : automata.getAutomatons()){
            for(SpTransition tran : efa.getTransitions()){
                ConditionExpression c = tran.getConditionGuard();
                updateGuard(map, c);
            }
        }
    }

    private void updateGuard(LinkedList<String[]> map, ConditionExpression expression) {
        if(expression.isEmpty() || map.isEmpty())
             return;
        for(Iterator<ConditionElement> itr = expression.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            if(e.isExpression()){
                updateGuard(map,(ConditionExpression)e);
            } else {
                ConditionStatment st = (ConditionStatment)e;
                for(String[] value : map){
                    String oldName = value[0];
                    String newName = value[1];
                    String oldValue = value[2];
                    String newValue = value[3];
                    if(oldName.equals(newName) && oldValue.equals(newValue))
                        continue;
                    
                    if(st.getVariable().equals(oldName) && st.getValue().equals(oldValue)){
                        switch(st.getOperator()){
                            case Equal:
                                st.setVariable(newName);
                                st.setValue(newValue);
                                if(!st.isHierarchicalStatement())
                                    st.setOperator(ConditionStatment.Operator.GreaterEq);
                            default:
                                st.setVariable(newName);
                                st.setValue(newValue);
                        }
                    }
                }
            }
        }
    }

    private void reduceModel() {
        checkLocations();
        for(SpEFA efa : automata.getAutomatons()){
            reduceEFA(efa);
        }
    }

    private void reduceEFA(SpEFA efa) {
        boolean finish = false;
        LinkedList<String[]> map = new LinkedList<String[]>();
        while(!finish){
            for(Iterator<SpTransition> itr = efa.iterateSequenceTransitions(); itr.hasNext();){
                SpTransition currentTran = itr.next();
                SpLocation currentTo = currentTran.getTo();
                SpLocation currentFrom = currentTran.getFrom();
                if(isSOLT(currentTran) && !currentFrom.isVisited()){
                    if(currentFrom.isInitialLocation()){
                        currentTo.setInitialLocation();
                        efa.setInitialLocation(currentTo.getName());
                    }
                    currentTo.getInTransitions().remove(currentTran);
                    for(SpTransition tran : currentFrom.getInTransitions()){
                        tran.setTo(currentTo);
                        currentTo.addInTransition(tran);
                    }
                    currentTo.setName(currentFrom + EFAVariables.EFA_LOCATION_DIVIDER + currentTo);
                    map.add(new String[]{efa.getName(), efa.getName(), Integer.toString(currentTo.getValue()), Integer.toString(currentFrom.getValue())});
                    currentTo.setValue(currentFrom.getValue());
                    itr.remove();
                    efa.removeLocation(currentFrom.getName());
                    break;
                }
                
                if(!itr.hasNext())
                    finish = true;
            }
        }
        updateGlobalGuards(map);
        
        if(!efa.getTransitions().isEmpty()){
            LinkedList<String[]> map2 = new LinkedList<String[]>();
            int count = 0;
            for(Iterator<SpTransition> itr = efa.iterateSequenceTransitions(); itr.hasNext();){
                SpTransition current = itr.next();
                SpLocation l = current.getFrom();
                map2.add(new String[]{efa.getName(), efa.getName(), Integer.toString(l.getValue()), Integer.toString(count)});
                l.setValue(count++);
                if(!itr.hasNext()){
                    l = current.getTo();
                    map2.add(new String[]{efa.getName(), efa.getName(), Integer.toString(l.getValue()), Integer.toString(count)});
                    l.setValue(count);
                }
            }
            updateGlobalGuards(map2);
        }        
    }

    private boolean isSOLT(SpTransition transition) {
        return transition.getFrom().getOutTransitions().size() == 1 && transition.getCondition().isEmpty();
    }

    private void checkLocations() {
        for(SpEFA efa : automata.getAutomatons())
                for(SpTransition tran : efa.getTransitions())
                    if(!tran.getConditionGuard().isEmpty())
                        checkLocation(tran.getConditionGuard());
    }

    private void checkLocation(ConditionExpression expression) {
        LinkedList<ConditionStatment> statments = expression.getAllConditionStatments();
        for(ConditionStatment st : statments){
            if(isLocationVariable(st.getVariable())){
                setLocationVisited(st);
            }
        }
    }

    private boolean isLocationVariable(String variable) {
        for(SpVariable var : automata.getVariables())
            if(var.getName().equals(variable))
                return false;
        return true;
    }
    
    private void setLocationVisited(ConditionStatment st) {
        String variable = st.getVariable();
        ConditionStatment.Operator op = st.getOperator();
        String value = st.getValue();
        
        SpEFA efa = automata.getSpEFA(variable);
        boolean flag = false;
        boolean finish = false;
        SpLocation current = efa.getInitialLocation();
        while(!finish){
            switch (op){
                case Equal:
                    if(value.equals(Integer.toString(current.getValue()))){
                        current.setVisited();
                        return;
                    }
                    break;
                    
                case Greater:
                    if(value.equals(Integer.toString(current.getValue()))){
                        flag = true;
                        continue;
                    }
                    if(flag){
                        current.setVisited();
                    }
                    break;
                    
                case GreaterEq:
                    if(value.equals(Integer.toString(current.getValue()))){
                        current.setVisited();
                        return;
                    }
                    break;
                    
                case Less:
                    current.setVisited();
                    if(value.equals(Integer.toString(current.getValue()))){
                        current.clearVisited();
                        return;
                    }
                    break;
                    
                case LessEq:
                    current.setVisited();
                    if(Integer.toString(current.getValue()).equals(value)) return;
                    break;
            }
            
            if(!current.getOutTransitions().isEmpty())
                current = current.getOutTransitions().iterator().next().getTo();
            else
                finish=true;
            
        }
    }     
}
