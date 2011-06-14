/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2.reduction;

import java.util.HashMap;
import java.util.HashSet;
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
    SpEFAutomata automata;
        
    public Reduction(Model model){
        this.model = model;
        this.automata = null;
    }
    
    public SpEFAutomata getReducedModel(){
        DefaultModelParser parser = new DefaultModelParser(model);
        automata = parser.getSpEFAutomata();
        synchronizeModel();
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
                    continue;
                
                seq = appendSpEFA(seq, current);
                automata.removeAutomaton(operation);
            }
            automata.addAutomaton(seq);
        }
    }

    private SpEFA appendSpEFA(SpEFA efa1, SpEFA efa2){
        if(efa1 == null)
            return efa2;

        if(efa2 == null)
            return efa1;
        
//        SpEFA efa = new SpEFA(efa1.getName() + EFAVariables.EFA_NAME_DIVIDER + efa2.getName());
        int state = 0;
        SpLocation last = null;
        SpLocation first = null;
        Iterator<SpTransition> itr = null;
        Integer lastValue = null;
        itr = efa1.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
//            Integer oldFromValue = new Integer(current.getFrom().getValue());
//            current.getFrom().setValue(state++);
//            Integer newFromValue = new Integer(current.getFrom().getValue());
//            if(oldFromValue != newFromValue)
//                updateGlobalGuards(efa1.getName(), oldFromValue, newFromValue);
//            
            if(!itr.hasNext()){
                last = current.getTo();
                lastValue = new Integer(last.getValue());
                state = last.getValue();
                Integer newToValue = new Integer(last.getValue());
                if(lastValue != newToValue)
                    updateGlobalGuards(efa1.getName(), lastValue, newToValue);
            }
//            efa.addTransition(current);
        }
        
        itr = efa2.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
            if(current.getFrom().isInitialLocation()){
                first = current.getFrom();
                Integer oldFirstValue = new Integer(first.getValue());
                Integer newFirstValue = new Integer(last.getValue());
                if(!first.isAccepting()){
                    last.setNotAccepting();
                }
                last.setName(last + EFAVariables.EFA_NAME_DIVIDER + first);
                removeGuard(efa1.getName(), Integer.toString(lastValue), current.getConditionGuard());
                updateGlobalGuards(efa2.getName(), oldFirstValue, newFirstValue);
                current.setFrom(last);
                efa1.addTransition(current);
            } else {
                Integer oldFromValue = new Integer(current.getFrom().getValue());
                current.getFrom().setValue(state++);
                Integer newFromValue = new Integer(current.getFrom().getValue());
                if(oldFromValue != newFromValue)
                    updateGlobalGuards(efa1.getName(), oldFromValue, newFromValue);

                if(!itr.hasNext()){
                    last = current.getTo();
                    lastValue = new Integer(last.getValue());
                    last.setValue(state);
                    Integer newToValue = new Integer(last.getValue());
                    if(lastValue != newToValue)
                        updateGlobalGuards(efa1.getName(), lastValue, newToValue);
                }
                efa1.addTransition(current);
            }
        }
        automata.removeAutomaton(efa2.getName());
        return efa1;
    }
    
    private void removeGuard(String variable, String value, ConditionExpression ex){
        if(ex.isEmpty() || value == null || variable == null)
             return;
        
        for(Iterator<ConditionElement> itr = ex.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            if(e.isExpression()){
                removeGuard(variable, value, (ConditionExpression)e);
            } else {
                ConditionStatment st = (ConditionStatment)e;
                if(st.getVariable().equals(variable) 
                        && (st.getOperator() == ConditionStatment.Operator.Equal || st.getOperator() == ConditionStatment.Operator.GreaterEq) 
                        && st.getValue().equals(value)){
                    itr.remove();
                }
            }
        }
    }

    private void updateGlobalGuards(String variable, Integer oldValue, Integer newValue) {
        for(SpEFA efa : automata.getAutomatons()){
            for(SpTransition tran : efa.getTransitions()){
                ConditionExpression c = tran.getConditionGuard();
                updateGuard(variable, oldValue, newValue, c);
            }
        }
    }

    private void updateGuard(String variable, Integer oldValue, Integer newValue, ConditionExpression condition) {
        if(condition.isEmpty())
             return;
        
        for(Iterator<ConditionElement> itr = condition.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            if(e.isExpression()){
                updateGuard(variable, oldValue, newValue, (ConditionExpression)e);
            } else {
                ConditionStatment st = (ConditionStatment)e;
                if(st.getVariable().equals(variable) && st.getValue().equals(Integer.toString(oldValue))){
                    switch(st.getOperator()){
                        case Equal:
                            st.setOperator(ConditionStatment.Operator.GreaterEq);
                            st.setValue(Integer.toString(newValue));
                        default:
                            st.setValue(Integer.toString(newValue));
                    }
                }
            }
        }
    }

}
