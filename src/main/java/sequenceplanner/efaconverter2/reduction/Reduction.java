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
    private SpEFAutomata automata;
    
    public Reduction(Model model){
        this.model = model;
        this.automata = null;
    }
    
    public SpEFAutomata getReducedModel(){
        DefaultModelParser parser = new DefaultModelParser(model);
        automata = parser.getSpEFAutomata();
        
        if(automata.getAutomatons().isEmpty()) return automata;
        
        System.out.println("synchronizeModel()");
        synchronizeModel();
        System.out.println("reduceModel()");
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
         * map[4]: Is current location accepting?
         */
        
        LinkedList<String[]> map = new LinkedList<String[]>();
//        LinkedList<ConditionStatment> hierarchicalGuard = null;
        itr = efa1.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
//            if(current.getFrom().isInitialLocation()){
//                hierarchicalGuard = getHierarchicalGuard(current.getConditionGuard());
//            }
            map.add(new String[] {efa1.getName(), efa.getName(), Integer.toString(current.getFrom().getValue()), Integer.toString(current.getFrom().getValue()), Boolean.toString(current.getFrom().isAccepting())});
            if(!itr.hasNext()){
                map.add(new String[] {efa1.getName(), efa.getName(), Integer.toString(current.getTo().getValue()), Integer.toString(current.getTo().getValue()),Boolean.toString(current.getTo().isAccepting())});
                last = current.getTo();
                state = last.getValue();
                removeProjectGuard(efa1.getName(), Integer.toString(state));
            }
            efa.addTransition(current);
        }
        
        itr = efa2.iterateSequenceTransitions();
        while(itr.hasNext()){
            SpTransition current = itr.next();
            if(current.getFrom().isInitialLocation()){
                map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getFrom().getValue()), Integer.toString(last.getValue()), Boolean.toString(current.getFrom().isAccepting())});
                
                if(!current.getFrom().isAccepting())
                    last.clearAccepting();
                
                ConditionExpression newGuard = current.getConditionGuard();
//                if(!hierarchicalGuard.isEmpty()){
////                    ConditionStatment first = hierarchicalGuard.getFirst();
//                    for(ConditionStatment first : hierarchicalGuard){
//                    System.out.println(">>>>>>> FIRST:" + first);
//                    newGuard = removeGuard(first.getVariable(), first.getValue(), newGuard);
//                    }
//                }
                newGuard = removeGuard(efa1.getName(), Integer.toString(last.getValue()), newGuard);
                Condition newc = new Condition(newGuard, current.getConditionAction());
                SpTransition newtran = new SpTransition(current.getEvent(), last, current.getTo(), newc);
                
                efa.addTransition(newtran);
                
                last.setName(last + EFAVariables.EFA_LOCATION_DIVIDER + current.getFrom());
            } else {
                map.add(new String[] {efa2.getName(), efa.getName(), Integer.toString(current.getFrom().getValue()), Integer.toString(++state), Boolean.toString(current.getFrom().isAccepting())});
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
                    boolean accepting = Boolean.valueOf(value[4]);
                    if(oldName.equals(newName) && oldValue.equals(newValue))
                        continue;
                    
                    if(st.getVariable().equals(oldName) && st.getValue().equals(oldValue)){
                        switch(st.getOperator()){
                            case Equal:
                                System.out.println("FROM: "+st);
                                st.setVariable(newName);
                                st.setValue(newValue);
                                if(Integer.parseInt(newValue) % 2 == 0 && accepting)
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
        while(!finish){
            for(Iterator<SpTransition> itr = efa.iterateSequenceTransitions(); itr.hasNext();){
                SpTransition currentTran = itr.next();
                SpLocation currentTo = currentTran.getTo();
                SpLocation currentFrom = currentTran.getFrom();
                System.out.println("Checking: " + currentTran);
                if(isSOLT(currentTran) && !currentTo.isVisited()){
                    if(itr.hasNext()){
                        SpTransition nextTran = currentTo.getOutTransitions().iterator().next();
                        if(currentTo.isAccepting())
                            currentFrom.setAccepting();

                        currentFrom.getOutTransitions().remove(currentTran);
                        currentFrom.getOutTransitions().add(nextTran);
                        currentFrom.setName(currentFrom + EFAVariables.EFA_LOCATION_DIVIDER + currentTo);
                        nextTran.setFrom(currentFrom);
                        efa.removeTransition(currentTran);
                        
                        System.out.println("Redirecting: " + nextTran);
                        efa.removeLocation(currentTo.getName());
                        
                        System.out.println("Removing: " + currentTo.getName());
                        
                        break;
                    } else {
                        if(currentTo.isAccepting())
                            currentFrom.setAccepting();
                        currentFrom.getOutTransitions().remove(currentTran);
                        currentFrom.setName(currentFrom + EFAVariables.EFA_LOCATION_DIVIDER + currentTo);
                        efa.removeTransition(currentTran);
                        efa.removeLocation(currentTo.getName());
                        System.out.println("Removing: " + currentTo.getName());
                        break;
                    }
                }
                if(!itr.hasNext())
                    finish = true;
            }
            
        }
        
        if(!efa.getTransitions().isEmpty()){
            LinkedList<String[]> map = new LinkedList<String[]>();
            int count = 0;
            for(Iterator<SpTransition> itr = efa.iterateSequenceTransitions(); itr.hasNext();){
                SpTransition current = itr.next();
                SpLocation l = current.getFrom();
                System.out.println("##################");
                System.out.println("UPDATE FROM: " + l.getValue());
                map.add(new String[]{efa.getName(), efa.getName(), Integer.toString(l.getValue()), Integer.toString(count), Boolean.toString(l.isAccepting())});
                l.setValue(count++);
                System.out.println("UPDATE TO: " + l.getValue());
                if(!itr.hasNext()){
                    l = current.getTo();
                    System.out.println("UPDATE FROMx: " + l.getValue());
                    map.add(new String[]{efa.getName(), efa.getName(), Integer.toString(l.getValue()), Integer.toString(count), Boolean.toString(l.isAccepting())});
                    l.setValue(count);
                    System.out.println("UPDATE TOx: " + l.getValue());
                }
            }
            updateGlobalGuards(map);
        }        
    }

    private boolean isSOLT(SpTransition transition) {
        return transition.getFrom().getOutTransitions().size() == 1 && transition.getCondition().isEmpty();
    }

    private void checkLocations() {
        for(SpEFA efa : automata.getAutomatons()){
            //if(!efa.getName().equals(automata.getEFAProjectName()))
                for(SpTransition tran : efa.getTransitions())
                    if(!tran.getConditionGuard().isEmpty()){
                        System.out.println(">>> " + tran.getConditionGuard());
                        checkLocation(tran.getConditionGuard());
                    }
        }
    }

    private void checkLocation(ConditionExpression expression) {
        for(Iterator<ConditionElement> itr = expression.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            if(e.isExpression()){
                checkLocation((ConditionExpression)e);
            } else {
                ConditionStatment st = (ConditionStatment)e;
                if(isLocationVariable(st.getVariable())){
                    System.out.println("<<< " + st.getVariable()+ st.getOperator()+ st.getValue());
                    setLocationVisited(st.getVariable(), st.getOperator(), st.getValue());
                }
            }
        }
    }

    private boolean isLocationVariable(String variable) {
        for(SpVariable var : automata.getVariables())
            if(var.getName().equals(variable))
                return false;
        return true;
    }
    
    private void setLocationVisited(String variable, ConditionStatment.Operator op, String value) {
        SpEFA efa = automata.getSpEFA(variable);
        System.out.println("EFA NAME: " + efa.getName());
        boolean flag = false;
        boolean finish = false;
        SpLocation current = efa.getInitialLocation();
        while(!finish){
            if(op.equals(ConditionStatment.Operator.Equal)){
                if(value.equals(Integer.toString(current.getValue()))){
                    current.setVisited();
                    System.out.println("SET VISITED: " + current.getName());
                    return;
                }
            } else if(op.equals(ConditionStatment.Operator.GreaterEq)){
                System.out.println("CHECK VISITED: " + value + " -- "+Integer.toString(current.getValue()));
                if(value.equals(Integer.toString(current.getValue()))){
                    current.setVisited();
                    return;
                }
            } else if(op.equals(ConditionStatment.Operator.Greater)){
                if(value.equals(Integer.toString(current.getValue()))){
                    flag = true;
                    continue;
                }
                if(flag){
                    current.setVisited();
                    System.out.println("SET VISITED: " + current.getName());
                }
            } else if(op.equals(ConditionStatment.Operator.Less)){
                System.out.println("LESS VISITED: " + current.getName());
                current.setVisited();
                if(value.equals(Integer.toString(current.getValue()))){
                    current.clearVisited();
                    return;
                }
            } else if(op.equals(ConditionStatment.Operator.LessEq)){
                System.out.println("LESSEq VISITED: " + current.getName());
                current.setVisited();
                if(Integer.toString(current.getValue()).equals(value)) return;
            }
            
            if(!current.getOutTransitions().isEmpty())
                current = current.getOutTransitions().iterator().next().getTo();
            else
                finish=true;
            
        }
        
//        for(Iterator<SpTransition> itr = efa.iterateSequenceTransitions(); itr.hasNext();){
//            SpLocation l = itr.next().getFrom();
//            switch (op){
//                    case Equal:
//                        if(value.equals(Integer.toString(l.getValue()))){
//                            l.setVisited();
//                            System.out.println("SET VISITED: " + l.getName());
//                            return;
//                        }
//                        break;
//                        
//                    case GreaterEq:
//                        if(value.equals(Integer.toString(l.getValue()))) flag = true;
//                        if(flag){
//                            l.setVisited();
//                            System.out.println("SET VISITED: " + l.getName());
//                        }
//                        break;
//                        
//                    case Greater:
//                        if(value.equals(Integer.toString(l.getValue()))){
//                            flag = true;
//                            continue;
//                        }
//                        if(flag){
//                            l.setVisited();
//                            System.out.println("SET VISITED: " + l.getName());
//                        }
//                        break;
//                    
//                    case Less:
//                        System.out.println("LESS VISITED: " + l.getName());
//                        l.setVisited();
//                        if(value.equals(Integer.toString(l.getValue()))){
//                            l.clearVisited();
//                            return;
//                        }
//                        break;
//                    case LessEq:
//                        System.out.println("VISITED: " + l.getName());
//                        l.setVisited();
//                        if(Integer.toString(l.getValue()).equals(value)) return;
//                        break;
//                        
//            }
//        }
    }

    private void removeProjectGuard(String name, String value) {
        SpEFA project = automata.getSpEFA(automata.getEFAProjectName());
        SpTransition stop = null;
        
        for(Iterator<SpTransition> itr = project.iterateSequenceTransitions(); itr.hasNext();)
            stop = itr.next();
        
        ConditionExpression newGuard = removeGuard(name, value, stop.getConditionGuard());
        System.out.println("Project guard: " + newGuard);
        Condition newc = new Condition(newGuard, stop.getConditionAction());
        stop.setCondition(newc);
    }

    private LinkedList<ConditionStatment> getHierarchicalGuard(ConditionExpression conditionGuard) {
        LinkedList<ConditionStatment> statment = new LinkedList<ConditionStatment>();
        for(Iterator<ConditionElement> itr = conditionGuard.iterator(); itr.hasNext();){
            ConditionElement e = itr.next();
            if(e.isExpression()){
                statment.addAll(getHierarchicalGuard((ConditionExpression)e));
            } else {
                ConditionStatment st = (ConditionStatment)e;
                if(st.getOperator() == ConditionStatment.Operator.Equal && st.getValue().equals(EFAVariables.VARIABLE_EXECUTION_STATE))
                    statment.add(st);
                }
            }
        return statment;
        }
}
