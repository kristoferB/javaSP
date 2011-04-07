/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.LinkedList;
import java.util.Stack;
import sequenceplanner.efaconverter.EFAVariables;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;

/**
 *
 * @author shoaei
 */
public class DefaultEFAConverter implements IEFAConverter{


//    private Model model;
//    private Module module;
//    private String name;
    private DefaultEFAutomata automata;

    public DefaultEFAConverter(){
    }

    @Override
    public IEFAutomata getEFAutomata(Model iModel) {
        automata = new DefaultEFAutomata(iModel.getOperationRoot().toString());
        LinkedList<TreeNode> operationList = createOperationList(iModel);
        for(TreeNode op : operationList)
             automata.addEFAutomata((DefaultEFAutomata)getEFAutomata(op));

        automata.addEFAutomaton((DefaultEFAutomaton)getProjectEFAutomaton(iModel));
        return automata;
    }


    @Override
    public IEFAutomata getEFAutomata(TreeNode iOperation) {
        DefaultEFAutomata oAutomata = new DefaultEFAutomata(Integer.toString(iOperation.getId()));
        DefaultEFAutomaton oAutomaton = new DefaultEFAutomaton(createEFAName(iOperation), automata);
        return oAutomata;
    }

    @Override
    public IEFAutomaton getProjectEFAutomaton(Model model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String createEFAName(TreeNode iOperation) {
        String opName = EFAVariables.OPERATION_NAME_PREFIX + iOperation.getId() + EFAVariables.EFA_NAME_POSTFIX;
        return opName;
    }


    private LinkedList<TreeNode> createOperationList(Model model) {
        LinkedList<TreeNode> operationList = new LinkedList<TreeNode>();
        Stack<TreeNode> stack = new Stack<TreeNode>();
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i){
            stack.add(model.getOperationRoot().getChildAt(i));
        }
        while(!stack.isEmpty()){
            TreeNode node = stack.peek();
            if(node.getChildCount() > 0){
                for (int i = 0; i < node.getChildCount(); ++i){
                    stack.add(node.getChildAt(i));
                }
            }
            operationList.add(stack.pop());
        }
        return operationList;
    }

//    private LinkedList<String> locations;
//    private LinkedList<String> events;
//    private LinkedList<String> variable;
//    private LinkedList<LinkedList<String>> transitions;
//
//    public DefaultEFAConverter(Model model, String name){
//        this.model = model;
//        this.name = name;
//        automata = new DefaultEFAutomata(name);
//        this.module = automata.getModule();
//
////        automaton = new DefaultEFAutomaton(name);
////        locations = new LinkedList<String>();
////        events = new LinkedList<String>();
////        variable = new LinkedList<String>();
////        transitions = new LinkedList<LinkedList<String>>();
////        createEFAutomaton();
//    }
////
//    public ExtendedAutomaton getEFAutomaton(String name){
//    }
//
////
////    public DefaultEFAutomaton getDefaultEFAutomaton(TreeNode operation, String name){
////        DefaultEFAutomaton automaton = createEFAutomaton(operation);
////        return automaton;
////    }
////
//    private void createEFAutomaton(TreeNode operation) {
//
//        String opName = createOperationName(Integer.toString(operation.getId()));
//        DefaultEFAutomaton automaton = new DefaultEFAutomaton(opName, module);
//
//        String initL = opName + EFAVariables.STATE_INITIAL_POSTFIX;
//        String execL = opName + EFAVariables.STATE_EXECUTION_POSTFIX;
//        String finiL = opName + EFAVariables.STATE_FINAL_POSTFIX;
//
//        String startE = EFAVariables.EFA_START_EVENT_PREFIX + opName;
//        String stopE = EFAVariables.EFA_STOP_EVENT_PREFIX + opName;
//
//        OperationData opData = (OperationData) operation.getNodeData();
//
//        automaton.addLocation(initL, true, true);
//        automaton.addLocation(execL, false, false);
//        automaton.addLocation(finiL, true, false);
//
//        String gStart = getConditionGuard(opData.getSequenceCondition());
//        gStart += getResourceBookingGuard(opData.getResourceBooking());
//
//        automaton.addTransition(initL, execL, startE, , );
//        automaton.addTransition(execL, finiL, stopE, , null);
//
//        return automaton;
//    }
//
//    private String createOperationName(String operation) {
//        String opName = EFAVariables.OPERATION_NAME_PREFIX + operation + EFAVariables.EFA_NAME_POSTFIX;
//        return opName;
//    }
//
//    private String[] createGuardAndAction(String rawPrecondition) {
//        String g = rawPrecondition;
//        String a = "";
//        final String SP_OPEERATION_PATTERN = "(\\d+)";
//        final String SP_INIT_PATTERN = "(" + EFAVariables.STATE_INITIAL_POSTFIX + ")";
//        final String SP_EXEC_PATTERN = "("+ EFAVariables.STATE_EXECUTION_POSTFIX +")";
//        final String SP_FINI_PATTERN = "("+ EFAVariables.STATE_FINAL_POSTFIX +")";
//        final String SP_AND_PATTERN = "\\sA{1}\\s";
//        final String SP_OR_PATTERN = "\\sV{1}\\s";
//        final String SP_BOOKIN_RESOURCE_PATTERN = "(\\w)(\\+)";
//        final String SP_UNBOOKIN_RESOURCE_PATTERN = "(\\w)(\\-)";
//
//        g.replaceAll(SP_AND_PATTERN, "&");
//        g.replaceAll(SP_OR_PATTERN, "|");
//        g.replaceAll(SP_OPEERATION_PATTERN, "Op$1_state==");
//        g.replaceAll(SP_INIT_PATTERN, "0");
//        g.replaceAll(SP_EXEC_PATTERN, "1");
//        g.replaceAll(SP_FINI_PATTERN, "2");
//
//        String[] ga = new String[2];
//        ga[1]=g;
//        ga[2]=a;
//        return ga;
//    }
//
//    private String createAction(String[] rawActions) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
    
}
