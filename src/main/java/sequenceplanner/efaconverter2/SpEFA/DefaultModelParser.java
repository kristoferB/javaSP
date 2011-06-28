
package sequenceplanner.efaconverter2.SpEFA;

import java.util.HashMap;
import java.util.LinkedList;
import sequenceplanner.efaconverter2.EFA.EFAVariables;
import sequenceplanner.efaconverter2.condition.*;
import sequenceplanner.efaconverter2.condition.ConditionStatment.Operator;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author Mohammad Reza Shoaei
 * @version 21062011
 */

public class DefaultModelParser implements IModelParser{
    private final Model model;
    private HashMap<Integer, TreeNode> variables;
    private HashMap<Integer, TreeNode> operations;
    
    private SpEFAutomata automata;
    
    /**
     * Default parser for the SequencePlanner model to SpEFAutomata
     * @param model SequencePlanner model
     */
    public DefaultModelParser(Model model) {
        this.model = model;
        
        if(model == null) throw new NullPointerException("ModelParser: Null input model.");
        
        init();

        
    }
    
    private void init() {
        this.operations = new HashMap<Integer, TreeNode>();
        this.variables = new HashMap<Integer, TreeNode>();
        
        initPSequenceCondition(model.getOperationRoot());
        
        for(TreeNode n : model.getAllOperations()){
            initPSequenceCondition(n);
            operations.put(n.getNodeData().getId(), n);
        }
        
        for(TreeNode n : model.getAllVariables())
            variables.put(n.getNodeData().getId(), n);
    }
    
    private void initPSequenceCondition(TreeNode operation){
        
        OperationData od = null;
        if(operation.getId() == model.getOperationRoot().getId()){
            od = new OperationData("Project", operation.getId());
            operation.setNodeData(od);
        } else {
            od = (OperationData) operation.getNodeData();
        }
        
      LinkedList<TreeNode> listOfChildren = new LinkedList<TreeNode>();
      if (operation.getChildCount() > 0) {
         for (int i = 0; i < operation.getChildCount(); i++) {
            TreeNode subOperation = (TreeNode) operation.getChildAt(i);
            listOfChildren.add(subOperation);
         }
      }

      for(TreeNode op : listOfChildren){
          boolean write = true;
          OperationData opData = (OperationData)op.getNodeData();
          for(TreeNode po : listOfChildren){
              if(po.getNodeData() != null){
                  OperationData poData = (OperationData)po.getNodeData();
                  for(LinkedList<SeqCond> cons : poData.getSequenceCondition()){
                      for(SeqCond con : cons){
                          if(con.id == opData.getId() && Integer.toString(con.state).equals(EFAVariables.VARIABLE_FINAL_STATE))
                              write = false;
                      }
                  }
              }
          }
         if (write)
             od.addPAnd(opData.getId(), Integer.parseInt(EFAVariables.VARIABLE_FINAL_STATE));
      }
    }
        
    
    /**
     * Get the SpEFAutomata model of all the operations and variables
     * @return SpEFAutomata of the corresponding SequencePlanner model
     */
    @Override
    public SpEFAutomata getSpEFAutomata(){
        this.automata = new SpEFAutomata();
        if(operations.isEmpty()) return this.automata;
        createSpVariables();
        createSpEFAs();
        return this.automata;
    }

    private void createSpVariables() {
        for(TreeNode node : variables.values()){
            SpVariable newvar = getSpVariable(node);
            if(newvar != null){
                automata.addVariable(newvar);
            }
        }
    }
    
    /**
     * Return the corresponding SpVariable of the input SequencePlanner variable
     * @param variable SP variable
     * @return SpVariable if the input is an SequencePlanner variable otherwise Null
     */
    @Override
    public SpVariable getSpVariable(TreeNode variable){
        if(Model.isVariable(variable.getNodeData())){
            ResourceVariableData varData = (ResourceVariableData) variable.getNodeData();
            return new SpVariable(createName(varData), varData.getMin(), varData.getMax(), varData.getInitialValue());
        }
        return null;
    }

    private void createSpEFAs() {
        for(TreeNode op : operations.values()){
            SpEFA temp = getSpEFA(op);
            if(!temp.getLocations().isEmpty())
                automata.addAutomaton(getSpEFA(op));
        }
        createSpProject();
    }
    
    /**
     * Create the corresponding SpEFA model
     * @param operation Operation to convert to SpEFA
     * @return corresponding SpEFA model
     */
    @Override
    public SpEFA getSpEFA(TreeNode operation){
        OperationData opData = (OperationData)operation.getNodeData();
        String opName = createName(opData);
        SpEFA efa = new SpEFA(opName);
        
        SpLocation iL = new SpLocation(opName + EFAVariables.STATE_INITIAL_POSTFIX);
        iL.setInitialLocation();
        iL.setAccepting();
        iL.setValue(Integer.parseInt(EFAVariables.VARIABLE_INITIAL_STATE));
        
        SpLocation eL = new SpLocation(opName + EFAVariables.STATE_EXECUTION_POSTFIX);
        eL.setAccepting();
        eL.setValue(Integer.parseInt(EFAVariables.VARIABLE_EXECUTION_STATE));
        
        SpLocation fL = new SpLocation(opName + EFAVariables.STATE_FINAL_POSTFIX);
        fL.setAccepting();
        fL.setValue(Integer.parseInt(EFAVariables.VARIABLE_FINAL_STATE));
        
        efa.addLocation(iL);
        efa.addLocation(eL);
        efa.addLocation(fL);
        
        SpEvent startE = new SpEvent(EFAVariables.EFA_START_EVENT_PREFIX + opName, true);
        SpEvent stopE = new SpEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + opName, true);
        
        SpTransition startT = new SpTransition(startE, iL, eL, createPreCondition(operation)); 
        SpTransition stopT = new SpTransition(stopE, eL, fL, createPostCondition(operation)); 
        
        efa.addTransition(startT);
        efa.addTransition(stopT);
        
        return efa;
    }

    private Condition createPreCondition(TreeNode operation){
         if (operation.getNodeData() == null)
             return new Condition();

        OperationData od = (OperationData) operation.getNodeData();
        Condition c = createCondition(od.getSequenceCondition(),od.getResourceBooking(),od.getActions());
        if (hasParent(operation)){
            ConditionStatment s = new ConditionStatment(createName(operation.getParent().getId()),Operator.Equal,EFAVariables.VARIABLE_EXECUTION_STATE);
            s.setHierarchicalStatement();
            c.getGuard().appendElement(ConditionOperator.Type.AND, s);
        } else {
            ConditionStatment s = new ConditionStatment(getProjectName(),Operator.Equal,EFAVariables.VARIABLE_EXECUTION_STATE);
            s.setHierarchicalStatement();
            c.getGuard().appendElement(ConditionOperator.Type.AND, s);
        }
        return c;
     }

     private Condition createPostCondition(TreeNode opData){
         if (opData.getNodeData() == null)
             return new Condition();
         
         OperationData od = (OperationData) opData.getNodeData();
         Condition c = createCondition(od.getPSequenceCondition(),od.getPResourceBooking(),new LinkedList<OperationData.Action>());
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
         return new ConditionStatment(createName(cond.id), getConditionOperator(cond), Integer.toString(cond.state));
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
                 return new ConditionStatment(createName(resourceAlloc[0]), Operator.Inc, Integer.toString(1));
             } else if (resourceAlloc[1] == 0) { // decrease variable
                 return new ConditionStatment(createName(resourceAlloc[0]), Operator.Dec, Integer.toString(1));
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
        return Integer.toString(id);
    }
    
    private String createName(Data d){
        return Integer.toString(d.getId());
    }
    
    private String getProjectName() {
        return createName(model.getOperationRoot().getId());
    }
    
    private void createSpProject() {
        
        String opName = getProjectName();
        SpEFA efa = new SpEFA(opName);
        
        SpLocation iL = new SpLocation(opName + EFAVariables.STATE_INITIAL_POSTFIX);
        iL.setInitialLocation();
        iL.setValue(Integer.parseInt(EFAVariables.VARIABLE_INITIAL_STATE));
        SpLocation eL = new SpLocation(opName + EFAVariables.STATE_EXECUTION_POSTFIX);
        eL.setValue(Integer.parseInt(EFAVariables.VARIABLE_EXECUTION_STATE));
        SpLocation fL = new SpLocation(opName + EFAVariables.STATE_FINAL_POSTFIX);
        fL.setAccepting();
        fL.setValue(Integer.parseInt(EFAVariables.VARIABLE_FINAL_STATE));
        
        efa.addLocation(iL);
        efa.addLocation(eL);
        efa.addLocation(fL);
        
        SpEvent startE = new SpEvent(EFAVariables.EFA_START_EVENT_PREFIX + opName, true);
        SpEvent stopE = new SpEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + opName, true);
        
        SpTransition startT = new SpTransition(startE, iL, eL, new Condition()); 
        SpTransition stopT = new SpTransition(stopE, eL, fL, createPostCondition(model.getOperationRoot())); 

        iL.addOutTransition(startT);
        eL.addInTransition(startT);
        eL.addOutTransition(stopT);
        fL.addInTransition(stopT);
        
        efa.addTransition(startT);
        efa.addTransition(stopT);
        
        automata.addAutomaton(efa);
        automata.setEFAProjectName(opName);
    }
        
}
