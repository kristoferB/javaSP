/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2.reduction;

import java.util.Iterator;
import java.util.LinkedList;
import sequenceplanner.efaconverter2.DefaultModelParser;
import sequenceplanner.efaconverter2.EFAVariables;
import sequenceplanner.efaconverter2.SpEFA.*;
import sequenceplanner.efaconverter2.condition.*;
import sequenceplanner.model.Model;

/**
 *
 * @author shoaei
 */
public class Reduction {
    
    private Model model;
    private SpEFAutomata automata;
    
    public Reduction(Model model){
        this.model = model;
        this.automata = null;
    }
    
    public SpEFAutomata getReducedModel(){
        DefaultModelParser parser = new DefaultModelParser(model);
        automata = parser.getSpEFAutomata();
        
        if(automata.getAutomatons().isEmpty()) return automata;
        
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
                if(current == null){
                    System.out.println("Null return: <"+operation+">");
                    continue;
                }
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
                System.out.println("VALUE: "+current.getFrom()+" <> "+current.getFrom().getValue());
                if(!itr.hasNext()){
                    map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getTo().getValue()), Integer.toString(++state), Boolean.toString(current.getTo().isAccepting())});
                    current.getTo().setValue(state);
                    System.out.println("VALUE: "+current.getTo()+" <> "+current.getTo().getValue());
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
                                System.out.println("FROM: "+st);
                                st.setVariable(newName);
                                st.setValue(newValue);
                                st.setOperator(ConditionStatment.Operator.GreaterEq);
                                System.out.println("TO: "+st);
                            default:
                                System.out.println("FROM: "+st);
                                st.setVariable(newName);
                                st.setValue(newValue);
                                System.out.println("TO: "+st);
                        }
                    }
                }
            }
        }
    }

    private void reduceModel() {
        checkLocations();
        for(SpEFA efa : automata.getAutomatons()){
            if(!efa.getName().equals(automata.getEFAProjectName()))
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
                System.out.println("Checking: " + currentTran);
                if(isSOLT(currentTran) && !currentFrom.isVisited()){
                    if(itr.hasNext()){
                        SpTransition nextTran = currentTo.getOutTransitions().iterator().next();
                        if(currentTo.isAccepting())
                            currentFrom.setAccepting();
                        else
                            currentFrom.clearAccepting();

                        currentFrom.getOutTransitions().remove(currentTran);
                        currentFrom.getOutTransitions().add(nextTran);
                        currentFrom.setName(currentFrom + EFAVariables.EFA_LOCATION_DIVIDER + currentTo);
                        nextTran.setFrom(currentFrom);
                        map.add(new String[]{efa.getName(), efa.getName(), Integer.toString(currentTo.getValue()), Integer.toString(currentFrom.getValue())});
                        efa.removeTransition(currentTran);
                        System.out.println("Redirecting: " + nextTran);
                        efa.removeLocation(currentTo.getName());
                        System.out.println("Removing: " + currentTo.getName());
                        break;
                    } else {
                        if(currentTo.isAccepting())
                            currentFrom.setAccepting();
                        else
                            currentFrom.clearAccepting();

                        currentFrom.getOutTransitions().remove(currentTran);
                        currentFrom.setName(currentFrom + EFAVariables.EFA_LOCATION_DIVIDER + currentTo);
                        map.add(new String[]{efa.getName(), efa.getName(), Integer.toString(currentTo.getValue()), Integer.toString(currentFrom.getValue())});                    
                        efa.removeTransition(currentTran);
                        efa.removeLocation(currentTo.getName());
                        System.out.println("Removing: " + currentTo.getName());
                    }
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
                System.out.println("##################");
                System.out.println("UPDATE FROM: " + l.getValue());
                map2.add(new String[]{efa.getName(), efa.getName(), Integer.toString(l.getValue()), Integer.toString(count)});
                l.setValue(count++);
                System.out.println("UPDATE TO: " + l.getValue());
                if(!itr.hasNext()){
                    l = current.getTo();
                    System.out.println("UPDATE FROMx: " + l.getValue());
                    map2.add(new String[]{efa.getName(), efa.getName(), Integer.toString(l.getValue()), Integer.toString(count)});
                    l.setValue(count);
                    System.out.println("UPDATE TOx: " + l.getValue());
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
                System.out.println("<<< " + st.getVariable()+ st.getOperator()+ st.getValue());
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
        System.out.println("EFA NAME: " + efa.getName());
        boolean flag = false;
        boolean finish = false;
        SpLocation current = efa.getInitialLocation();
        while(!finish){
            switch (op){
                case Equal:
                    if(value.equals(Integer.toString(current.getValue()))){
                        current.setVisited();
                        System.out.println("SET VISITED: " + current.getName());
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
                        System.out.println("SET VISITED: " + current.getName());
                    }
                    break;
                    
                case GreaterEq:
                    if(value.equals(Integer.toString(current.getValue()))){
                        current.setVisited();
                        System.out.println("SET VISITED: " + current.getName());
                        return;
                    }
                    break;
                    
                case Less:
                    System.out.println("LESS VISITED: " + current.getName());
                    current.setVisited();
                    if(value.equals(Integer.toString(current.getValue()))){
                        current.clearVisited();
                        return;
                    }
                    break;
                    
                case LessEq:
                    System.out.println("LESSEq VISITED: " + current.getName());
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
