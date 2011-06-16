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
import sequenceplanner.efaconverter2.condition.ConditionOperator.Type;
import sequenceplanner.model.Model;

/**
 *
 * @author shoaei
 */
public class Reduction {
    
    private Model model;
    SpEFAutomata automata;
    
    public Reduction(Model model){
        this.model = model;
        this.automata = null;
    }
    
    public SpEFAutomata getReducedModel(){
        DefaultModelParser parser = new DefaultModelParser(model);
        automata = parser.getSpEFAutomata();
        synchronizeModel();
        reduceModel();
        return automata;
    }
    
    private void synchronizeModel(){
        RelationGraph graph = new RelationGraph(automata);
        LinkedList<LinkedList<String>> paths = graph.getSequentialPaths();
        for(LinkedList<String> path : paths){
            System.out.println("++++++++++++++++++");
            for(String p : path)
                System.out.println(p);
        }
            
        for(LinkedList<String> path : paths){
            SpEFA seq = null;
            for(String operation : path){
                SpEFA current = automata.getSpEFA(operation);
                if(current == null){
                    System.out.println("Null: <"+operation+">");
                    continue;
                }
                System.out.println("OK: <"+operation+">");
                seq = appendSpEFA(seq, current);

                //automata.removeAutomaton(operation);
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
        
        itr = efa1.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
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
                    last.setNotAccepting();
                
                ConditionExpression newGuard = removeGuard(efa1.getName(), Integer.toString(last.getValue()), current.getConditionGuard());
                Condition newc = new Condition(newGuard, current.getConditionAction());
                SpTransition newtran = new SpTransition(current.getEvent(), last, current.getTo(), newc);
                
                efa.addTransition(newtran);
                
                last.setName(last + EFAVariables.EFA_NAME_DIVIDER + current.getFrom());
            } else {
                map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getFrom().getValue()), Integer.toString(++state)});
                current.getFrom().setValue(state);

                if(!itr.hasNext()){
                    map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getTo().getValue()), Integer.toString(++state)});
                    current.getTo().setValue(state);
                }
                efa.addTransition(current);
            }
        }
        updateGlobalGuards(map);
        return efa;
    }
    
    private ConditionExpression removeGuard(String variable, String value, ConditionExpression expression){
        
        ConditionStatment s1 = new ConditionStatment(variable, ConditionStatment.Operator.Equal, value);
        ConditionStatment s2 = new ConditionStatment(variable, ConditionStatment.Operator.GreaterEq, value);
        System.out.println("remove: " + s1 + " ~~~ " + s2);
        if(!expression.containsElement(s1) && !expression.containsElement(s2))
            return expression;
        
        ConditionExpression newex = new ConditionExpression();
        
        for(Iterator<ConditionElement> itr = expression.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            System.out.println("check: " + e);
            Type type = Type.AND;
            if(e.getPreviousOperator() != null)
                type = e.getPreviousOperator().getOperatorType();
            
            if(e.isExpression()){
                ConditionExpression result = removeGuard(variable, value, (ConditionExpression)e);
                if(!result.isEmpty())
                    newex.appendElement(type, result);
            } else {
                ConditionStatment st = (ConditionStatment)e;
                if(st.getVariable().equals(variable) 
                        && (st.getOperator() == ConditionStatment.Operator.Equal || st.getOperator() == ConditionStatment.Operator.GreaterEq)
                        && st.getValue().equals(value)){
                    System.out.println("remove now: " + st);
                    continue;
                }
                /*
                 * Remove project execution guard from the rest of conditions
                 */
                if(st.getVariable().equals(automata.getEFAProjectName()) 
                        && (st.getOperator() == ConditionStatment.Operator.Equal)
                        && st.getValue().equals(EFAVariables.VARIABLE_EXECUTION_STATE)){
                    System.out.println("remove now: " + st);
                    continue;
                }
                
                newex.appendElement(type, new ConditionStatment(st.getVariable(), st.getOperator(), st.getValue()));
            }
        }
        System.out.println("return: " + newex);
        return newex;
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
                    if(st.getVariable().equals(oldName) && st.getValue().equals(oldValue)){
                        switch(st.getOperator()){
                            case Equal:
                                st.setVariable(newName);
                                st.setValue(newValue);
                                if(Integer.parseInt(newValue) % 2 == 0)
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
        for(SpEFA efa : automata.getAutomatons()){
            reduceEFA(efa);
        }
    }

    private void reduceEFA(SpEFA efa) {
        Iterator<SpTransition> itr = efa.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
            if(isSOLT(current) && isFreeLocation(efa.getName(), current.getTo().getValue())){
                removeSOLT(current, efa);
            }
        }
    }

    private void removeSOLT(SpTransition transition, SpEFA efa){
        for(Iterator<SpTransition> itr = efa.iterateSequenceTransitions(); itr.hasNext();){
            SpTransition t = itr.next();
            if(t.equals(transition)){
                
            }
        }
    }
    
    private boolean isSOLT(SpTransition transition) {
        return transition.getFrom().getOutTransitions().size() == 1 && transition.getCondition().isEmpty();
    }

    private boolean isFreeLocation(String efaName, Integer value) {
        for(SpEFA efa : automata.getAutomatons()){
            for(SpTransition tran : efa.getTransitions()){
                ConditionExpression c = tran.getConditionGuard();
                if(c == null || c.isEmpty())
                    continue;
                if(hasGuardOnLocation(efaName, value, c))
                    return false;
            }
        }
        return true;
    }
    
    /*
     * OPERATION STATE < value and OPERATION STATE > value, i.e. all locations < value is not handeled and should be implemented
     */
    private boolean hasGuardOnLocation(String efaName, Integer value, ConditionExpression expression) {
        for(Iterator<ConditionElement> itr = expression.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            if(e.isExpression()){
                if(hasGuardOnLocation(efaName, value, (ConditionExpression)e))
                    return true;
            } else {
                ConditionStatment st = (ConditionStatment)e;
                if(st.getVariable().equals(efaName) && st.getValue().equals(Integer.toString(value)))
                    return true;
            }
        }
        return false;
    }
}
