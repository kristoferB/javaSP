/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2;

import java.util.HashMap;
import java.util.LinkedList;
import sequenceplanner.efaconverter2.condition.*;
import sequenceplanner.efaconverter2.condition.ConditionStatment.Operator;
import sequenceplanner.efaconverter2.efamodel.*;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author shoaei
 */
public class ModelParser {
    private final Model model;
    private HashMap<Integer, TreeNode> variables;
    private HashMap<Integer, TreeNode> operations;
    private HashMap<Integer, SpVariable> spVars;
    private HashMap<Integer, SpEFA> spOps;
    
    private SpEFAutomata automata;

    public ModelParser(Model model) {
        this.model = model;
        
        if(model != null)
            init();
        else 
            throw new NullPointerException("ModelParser: Null input model.");
        
    }
    
    private void init() {
        this.operations = new HashMap<Integer, TreeNode>();
        this.variables = new HashMap<Integer, TreeNode>();        
        recursiveVarFinder(model.getResourceRoot());
        recursiveOpFinder(model.getOperationRoot());
    }

    private void recursiveVarFinder(TreeNode var) {
       if (var.getChildCount() > 0) {
         for (int i = 0; i < var.getChildCount(); i++) {
            TreeNode child = (TreeNode) var.getChildAt(i);
            if (Model.isResource(child.getNodeData())) {
               recursiveVarFinder(child);
            } else if (Model.isVariable(child.getNodeData())){
               this.variables.put(child.getNodeData().getId(), child);
            }
         }
      }        
    }

    private void recursiveOpFinder(TreeNode op) {
        for (int i = 0; i < op.getChildCount(); i++) {
            TreeNode child = (TreeNode) op.getChildAt(i);
            if (Model.isOperation(child.getNodeData())){
                this.operations.put(child.getNodeData().getId(), child);
                recursiveOpFinder(child);
            }
        }        
    }

    public SpEFAutomata getSpEFAutomata(){
        this.automata = new SpEFAutomata();
        createSpVariables(automata);
        createSpEFAs(automata);
        return automata;
    }
    
    private void createSpVariables(SpEFAutomata automata) {
        this.spVars = new HashMap<Integer, SpVariable>();
        for(TreeNode node : variables.values()){
            SpVariable newvar = createSpVariable(node);
            if(newvar != null){
                spVars.put(node.getId(), newvar);
                automata.addVariable(newvar);
            }
        }
    }
    
    public SpVariable createSpVariable(TreeNode variable){
        if(Model.isVariable(variable.getNodeData())){
            ResourceVariableData varData = (ResourceVariableData) variable.getNodeData();
            return new SpVariable(createName(varData), varData.getMin(), varData.getMax(), varData.getInitialValue());
        }
        return null;
    }

    private void createSpEFAs(SpEFAutomata automata) {
        this.spOps = new HashMap<Integer, SpEFA>();
        for(TreeNode op : operations.values()){
            SpEFAutomata temp = createSpEFA(op);
            for(SpEFA efa : temp.getAutomatons())
                automata.addAutomaton(efa);
            for(SpVariable var : temp.getVariables())
                automata.addVariable(var);
        }
    }
    
    public SpEFAutomata createSpEFA(TreeNode operation){
        OperationData opData = (OperationData)operation.getNodeData();

        SpEFAutomata output = new SpEFAutomata("Operation " + operation.getId() + " Automata");
        String opName = createName(opData);
        SpEFA efa = new SpEFA(opName);
        
        SpLocation iL = new SpLocation(opName + EFAVariables.STATE_INITIAL_POSTFIX);
        iL.setInitialLocation();
        iL.setAccepting();
        SpLocation eL = new SpLocation(opName + EFAVariables.STATE_EXECUTION_POSTFIX);
        SpLocation fL = new SpLocation(opName + EFAVariables.STATE_FINAL_POSTFIX);
        fL.setAccepting();
        
        efa.addLocation(iL);
        efa.addLocation(eL);
        efa.addLocation(fL);
        
        SpEvent startE = new SpEvent(EFAVariables.EFA_START_EVENT_PREFIX + opName, true);
        SpEvent stopE = new SpEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + opName, true);
        
        SpTransition startT = new SpTransition(startE, iL, eL, createPreCondition(operation)); 
        SpTransition stopT = new SpTransition(stopE, eL, fL, createPostCondition(operation)); 
        
        SpVariable var = new SpVariable(opName, new Integer(0), new Integer(2), new Integer(0));

        output.addVariable(var);
        output.addAutomaton(efa);
        
        return output;
    }

    private Condition createPreCondition(TreeNode operation){
        OperationData od = (OperationData) operation.getNodeData();
        Condition c = createCondition(od.getSequenceCondition(),od.getResourceBooking(),od.getActions());
        if (hasParent(operation)){
            c.getGuard().appendElement(ConditionOperator.Type.AND, new ConditionStatment(createName(operation.getParent().getId()),Operator.Equal,"1"));
        }
        return c;
     }

     private Condition createPostCondition(TreeNode operation){
         //if (!isOpNodeOk(operation)) return null;
         OperationData od = (OperationData) operation.getNodeData();
         Condition c = createCondition(od.getPSequenceCondition(),od.getPResourceBooking(),new LinkedList<OperationData.Action>());
//         if (hasChildren(operation)){
//             for (TreeNode child : findLastOperations(operation)){
//                 c.getGuard().appendElement(ConditionOperator.Type.AND, new ConditionStatment(createName(child.getId()),Operator.Equal,"2"));
//             }
//         }
         return c;
     }
     
     
    private Condition createCondition(LinkedList<LinkedList<OperationData.SeqCond>> seqCond,
                                       LinkedList<Integer[]> rAlloc,
                                       LinkedList<OperationData.Action> actions){
        
        ConditionExpression guard = new ConditionExpression();
        ConditionExpression action = new ConditionExpression();

        // Parse seq condition
        for (LinkedList<OperationData.SeqCond> orConds : seqCond){
            if (orConds.size() > 1){
                ConditionExpression orExpression = new ConditionExpression();
                for (OperationData.SeqCond orC : orConds){
                    orExpression.appendElement(ConditionOperator.Type.OR, createConditionStatment(orC));
                }
                guard.appendElement(ConditionOperator.Type.AND, orExpression);
            } else {
                for (OperationData.SeqCond orC : orConds){
                    guard.appendElement(ConditionOperator.Type.AND, createConditionStatment(orC));
                    break;
                }
            }
        }

        // parse resource allocation
        for (Integer[] resource : rAlloc){
            guard.appendElement(ConditionOperator.Type.AND, createGuardConditionStatment(resource));
            action.appendElement(ConditionOperator.Type.AND, createActionConditionStatment(resource));
        }

        // parse actions
        for (OperationData.Action a : actions){
            action.appendElement(ConditionOperator.Type.AND, createActionConditionStatment(a));
        }

        return new Condition(guard, action);
     }



     private ConditionStatment createConditionStatment(OperationData.SeqCond cond){
         return new ConditionStatment(createName(cond.id), getConditionOperator(cond), Integer.toString(cond.value));
     }

     private ConditionStatment createGuardConditionStatment(Integer[] resourceAlloc){
         TreeNode n = model.getNode(resourceAlloc[0]);
         if (n != null && Model.isVariable(n.getNodeData())) {
             ResourceVariableData var = (ResourceVariableData) n.getNodeData();
             if (resourceAlloc[1] == 1) { // increase variable
                 return new ConditionStatment(createName(var.getId()), Operator.Less, Integer.toString(var.getMax()));
             } else if (resourceAlloc[1] == 0) { // decrease variable
                 return new ConditionStatment(createName(var.getId()), Operator.Less, Integer.toString(var.getMax()));
             }
         }
         return null;
     }

     private ConditionStatment createActionConditionStatment(Integer[] resourceAlloc){
             if (resourceAlloc[1] == 1) { // increase variable
                 return new ConditionStatment(createName(resourceAlloc[0]), Operator.Inc, "");
             } else if (resourceAlloc[1] == 0) { // decrease variable
                 return new ConditionStatment(createName(resourceAlloc[0]), Operator.Dec, "");
             }
         return null;
     }

     private ConditionStatment createActionConditionStatment(OperationData.Action action){
         return new ConditionStatment(createName(action.id), getActionOperator(action), Integer.toString(action.value));
     }

     private Operator getConditionOperator(OperationData.SeqCond cond){
         if (cond.isOperationCheck()) {
             return Operator.Equal;
         }
         if (cond.isVariableCheck()) {
             if (cond.state == 0) {
                 return Operator.Equal;
             } else if (cond.state == 1) {
                 return Operator.LessEq;
             } else if (cond.state == 2) {
                 return Operator.GreaterEq;
             } else if (cond.state == 3) {
                 return Operator.Less;
             } else if (cond.state == 4) {
                 return Operator.Greater;
             }
         }
         return Operator.Equal;
     }
     
     private Operator getActionOperator(OperationData.Action a){
         if (a.state == OperationData.ACTION_ADD){
             return Operator.Inc;
         } else if (a.state == OperationData.ACTION_DEC){
             return Operator.Dec;
         }else if (a.state == OperationData.ACTION_EQ){
             return Operator.Assign;
         }
         return Operator.Assign;
     }

    private boolean hasParent(TreeNode op){
        return !op.getParent().equals(model.getOperationRoot());
    }
    
    private String createName(int id){
        TreeNode n = model.getNode(Integer.valueOf(id).intValue());
        if (n != null){
            return createName(n.getNodeData());
        }
        return "";
    }
    
    private String createName(Data d){
        if (Model.isOperation(d)){
            return createOpName((OperationData) d);
        }
        if (Model.isVariable(d)){
            return createVarName((ResourceVariableData) d);
        }
        return "";
    }

    private String createOpName(OperationData od){
        //return od.getName();
        return "OP_" + Integer.toString(od.getId());
        //return od.getName().replace(' ', '_') + EFAVariables.ID_PREFIX + od.getId() + EFAVariables.COST_STRING + od.getCost();
   }

    private String createVarName(ResourceVariableData var){
        return "Var_" + Integer.toString(var.getId());
        //return EFAVariables.VARIABLE_NAME_PREFIX + var.getNodeData().getName().replace(' ', '_') + EFAVariables.ID_PREFIX + var.getId();
    }

    
}
