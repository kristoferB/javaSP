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
        
        SpEFA efa = new SpEFA(efa1.getName() + EFAVariables.EFA_NAME_DIVIDER + efa2.getName());
        int state = 0;
        SpLocation lastLocInEFA1 = null;
        SpLocation firstInEFA2 = null;
        Iterator<SpTransition> itr = null;
        HashMap<String,Integer> map = new HashMap<String, Integer>();
        
        itr = efa1.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
            current.getFrom().setValue(state++);
            if(!itr.hasNext()){
                lastLocInEFA1 = current.getTo();
                lastLocInEFA1.setValue(state);
            }
            efa.addTransition(current);
        }
        
        itr = efa2.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
            if(current.getFrom().isInitialLocation()){
                firstInEFA2 = current.getFrom();
                removeGuard(efa1.getName(), ConditionStatment.Operator.Equal, Integer.toString(state), current.getConditionGuard());
                lastLocInEFA1.setName(lastLocInEFA1.getName() + EFAVariables.EFA_NAME_DIVIDER + firstInEFA2.getName());
                if(!firstInEFA2.isAccepting())
                    lastLocInEFA1.setNotAccepting();
                current.setFrom(lastLocInEFA1);
                efa.addTransition(current);
            } else {
                current.getFrom().setValue(state++);
                efa.addTransition(current);
            }
            if(!itr.hasNext()){
                current.getTo().setValue(state);
            }
        }
        
        updateGlobalGuards();
        
        return efa;
    }
    
    private void removeGuard(String variable, ConditionStatment.Operator operator, String value, ConditionExpression ex){
        if(ex.isEmpty())
             return;
        
        for(Iterator<ConditionElement> itr = ex.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            if(e.isExpression()){
                removeGuard(variable, operator, value, (ConditionExpression)e);
            } else {
                ConditionStatment st = (ConditionStatment)e;
                if(st.getVariable().equals(variable) && st.getOperator() == operator && st.getValue().equals(value))
                    itr.remove();
            }
        }
    }

}
