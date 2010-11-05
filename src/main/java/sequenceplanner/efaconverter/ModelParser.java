
package sequenceplanner.efaconverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionOperator;
import sequenceplanner.condition.ConditionStatment;
import sequenceplanner.condition.ConditionStatment.Operator;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * This class is used to parse and understand the Model
 * to have a better separations bwteen the model and the algorithms. 
 * In SP2 this should be handled in a better way!
 *
 * @author kbe
 */
public class ModelParser {
    
    private final Model model;
    private HashMap<String, VarNode> variables;
    private HashMap<String, OpNode> operations;

    /**
     * Creates the model parser.
     * @param model The model that contains the operations and resources
     */
    public ModelParser(Model model) {
        this.model = model;
        initParser();
    }

    /**
     * Returns all variables as VarNode from the model
     * @return a hashmap where the variable name is the key to the VarNode object
     */
    public Collection<VarNode> getVariables(){
        return this.variables.values();
    }

    /**
     * Returns all operations as OpNode from the model
     * @return a hashmap where the operation name is the key to the VarNode object
     */
    public Collection<OpNode> getOperations(){
        return this.operations.values();
    }

    /**
     * Returns all variables included in a guard, that the operation is related to via its
     * preconditions. (Do not know what to do with reset for now)
     * @param operation The operation node that has the related varibles
     * @return A Set containing the VarNodes representing related variables.
     */
    public Set<VarNode> getPreRelatedToGuardVariables(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<VarNode>();
        }

        OperationData data = (OperationData)operation.getTreeNode().getNodeData();
        Set<Integer> vars = data.getPreCondVariableGuards();
        return findVarNodes(vars);
    }

    /**
     * Returns all variables included in an action, that the operation is related to via its
     * preconditions. (Do not know what to do with reset for now)
     * @param operation The operation node that has the related varibles
     * @return A Set containing the VarNodes representing related variables.
     *
     */
    public Set<VarNode> getPreRelatedToActionVariables(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<VarNode>();
        }

        OperationData data = (OperationData)operation.getTreeNode().getNodeData();
        Set<Integer> vars = data.getPreCondVariableActions();
        return findVarNodes(vars);
    }

    /**
     * Returns all operations that the operation is related to via its
     * preconditions. (Do not know what to do with reset for now)
     * @param operation The operation node that has the related varibles
     * @return A Set containing the OpNodes representing related variables.
     *
     */
    public Set<OpNode> getPreRelatedToOperations(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<OpNode>();
        }

        OperationData data = (OperationData)operation.getTreeNode().getNodeData();
        Set<Integer> ops = data.getPreCondOperations();
        return findOpNodes(ops);
    }


        /**
     * Returns all variables included in a guard, that the operation is related to via its
     * post-conditions. (Do not know what to do with reset for now)
     * @param operation The operation node that has the related varibles
     * @return A Set containing the VarNodes representing related variables.
     */
    public Set<VarNode> getPostRelatedToGuardVariables(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<VarNode>();
        }

        OperationData data = (OperationData)operation.getTreeNode().getNodeData();
        Set<Integer> vars = data.getPostCondVariableGuards();
        return findVarNodes(vars);
    }

    /**
     * Returns all variables included in an action, that the operation is related to via its
     * post-conditions. (Do not know what to do with reset for now)
     * @param operation The operation node that has the related varibles
     * @return A Set containing the VarNodes representing related variables.
     *
     */
    public Set<VarNode> getPostRelatedToActionVariables(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<VarNode>();
        }

        OperationData data = (OperationData)operation.getTreeNode().getNodeData();
        Set<Integer> vars = data.getPostCondVariableActions();
        return findVarNodes(vars);
    }

    /**
     * Returns all operations that the operation is related to via its
     * post-conditions. (Do not know what to do with reset for now)
     * @param operation The operation node that has the related varibles
     * @return A Set containing the OpNodes representing related variables.
     *
     */
    public Set<OpNode> getPostRelatedToOperations(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<OpNode>();
        }

        OperationData data = (OperationData)operation.getTreeNode().getNodeData();
        Set<Integer> ops = data.getPostCondOperations();
        return findOpNodes(ops);
    }



    /**
     * Returns all preceding operations to this operation
     * @param operation The operation node that wants predecessors
     * @return A Set containing the OpNodes representing preceding operations.
     *
     */
    public Set<OpNode> getPrecedingOperations(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<OpNode>();
        }
       HashSet<OpNode> pred = new HashSet<OpNode>();
       for (OpNode n : operation.getRelatesToOperations()){
           if (Model.isOperation(n.getTreeNode().getNodeData())){
               OperationData od = (OperationData) operation.getTreeNode().getNodeData();
               OperationData n_od = (OperationData) n.getTreeNode().getNodeData();
               if (od.isPredecessor(n_od.getId())){
                   pred.add(n);
               }
           }
       }

       return pred;
    }


    public Set<OpNode> getOperationsRelatedToExecute(OpNode operation){
        return getOperationsRelatedToState(operation,1);
    }

    public Set<OpNode> getOperationsRelatedToInit(OpNode operation){
        return getOperationsRelatedToState(operation,0);
    }

    public Set<OpNode> getOperationsRelatedToFinished(OpNode operation){        
        return getOperationsRelatedToState(operation,2);
    }

     public Set<OpNode> getOperationsRelatedToState(OpNode operation, int state){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return new HashSet<OpNode>();
        }
        Set<OpNode> exRel = new HashSet<OpNode>();
        for (OpNode n : operation.getRelatedByOperations()){
            if (Model.isOperation(n.getTreeNode().getNodeData())){
               OperationData od = (OperationData) operation.getTreeNode().getNodeData();
               OperationData n_od = (OperationData) n.getTreeNode().getNodeData();
               if (n_od.isRelatingToState(od.getId(),state)){
                   exRel.add(n);
               }
            }
        }
        return exRel;
    }


    public OpNode getParent(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return null;
        }
        TreeNode tn = operation.getTreeNode();
        if (tn.getParent().equals(model.getOperationRoot())) return null;

        return getOpNode(tn.getParent());
    }

    public Set<OpNode> getChildren(OpNode operation){
        if (!Model.isOperation(operation.getTreeNode().getNodeData())){
            return null;
        }
        Set<OpNode> children = new HashSet<OpNode>();
        TreeNode tn = operation.getTreeNode();

        for (int i = 0; i < tn.getChildCount() ; i++){
            children.add(getOpNode(tn.getChildAt(i)));
        }
        
        return children;
    }


    public Set<OpNode> getSometimesInSeq(OpNode operation, Set<OpNode> predecessors){
        Set<OpNode> orOps = new HashSet<OpNode>();
        for (OpNode pred1 : predecessors){
            for (OpNode pred2 : predecessors){
                if (pred1.equals(pred2)) break;
                if (Model.isOperation(pred1.getTreeNode().getNodeData()) &&
                    Model.isOperation(pred2.getTreeNode().getNodeData()) &&
                    Model.isOperation(operation.getTreeNode().getNodeData()))
                {
                    OperationData nd1 = (OperationData) pred1.getTreeNode().getNodeData();
                    OperationData nd2 = (OperationData) pred2.getTreeNode().getNodeData();
                    OperationData ndOp = (OperationData) operation.getTreeNode().getNodeData();
                    if (ndOp.isPredecessorsOR(nd1.getId(), nd2.getId())){
                        orOps.add(pred1);
                        orOps.add(pred2);
                    }
                }
            }
        }
        return orOps;

    }


    public int getVariableMin(VarNode variable){
        // Should fix error handling here!
        ResourceVariableData vd = (ResourceVariableData) variable.getTreeNode().getNodeData();
        return vd.getMin();
    }

     public int getVariableMax(VarNode variable){
        // Should fix error handling here!
        ResourceVariableData vd = (ResourceVariableData) variable.getTreeNode().getNodeData();
        return vd.getMax();
    }

    public int getVariableInit(VarNode variable){
        // Should fix error handling here!
        ResourceVariableData vd = (ResourceVariableData) variable.getTreeNode().getNodeData();
        return vd.getInitialValue();
    }
    
    public boolean isOpNodeOk(OpNode op){
        if (op == null
                || op.getTreeNode() == null
                || op.getTreeNode().getNodeData() == null
                || !Model.isOperation(op.getTreeNode().getNodeData())){
            return false;
        }
        return true;
    }


    /**
     * Will create a Pre-Condition object based on the SP1 datastructure with
     * linkedlists.
     */
     public Condition createPreCondition(OpNode operation){
         if (!isOpNodeOk(operation)) return null;
         OperationData od = (OperationData) operation.getTreeNode().getNodeData();
         Condition c = createCondition(od.getSequenceCondition(),od.getResourceBooking(),od.getActions());
         if (operation.hasParent()){
             c.getGuard().appendElement(ConditionOperator.Type.AND, new ConditionStatment(createName(operation.getParent().getId()),Operator.Equal,"1"));
         }
         return c;
     }

     
    /**
     * Will create a Post-Condition object based on the SP1 datastructure with
     * linkedlists.
     */
     public Condition createPostCondition(OpNode operation){
         if (!isOpNodeOk(operation)) return null;
         OperationData od = (OperationData) operation.getTreeNode().getNodeData();
         Condition c = createCondition(od.getPSequenceCondition(),od.getPResourceBooking(),new LinkedList<OperationData.Action>());
         if (operation.hasChildren()){
             for (OpNode child : findLastOperations(operation.getChildren())){
                 c.getGuard().appendElement(ConditionOperator.Type.AND, new ConditionStatment(createName(child.getId()),Operator.Equal,"2"));
             }
         }
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




     private Set<OpNode> findLastOperations(Collection<OpNode> theOperations){
        HashSet<OpNode> lastNodes = new HashSet<OpNode>();
        for (OpNode n : theOperations){
            if (n.getRelatedByOperationsFinished().isEmpty()){
                lastNodes.add(n);
            } else {
                boolean onlyParent = true;
                for (OpNode relOp : n.getRelatedByOperations()){
                    if (!relOp.equals(n.getParent())){
                        onlyParent = false;
                        break;
                    }
                }
                if (onlyParent) lastNodes.add(n);
            }
        }
        return lastNodes;
    }






    /**
     *  Returns the VarNodes based on their id in the model
     */
    private Set<VarNode> findVarNodes(Set<Integer> ids){
        Set<VarNode> nodes = new HashSet<VarNode>();
        for (Integer id : ids){
            VarNode var = this.variables.get(createName(id));
            if (var != null){
                nodes.add(var);
            }        
        }
        return nodes;
    }

    /**
     *  Returns the OpNodes based on their id in the model
     */
    private Set<OpNode> findOpNodes(Set<Integer> ids){
        Set<OpNode> nodes = new HashSet<OpNode>();
        for (Integer id : ids){
            OpNode op = this.operations.get(createName(id));
            if (op != null){
                nodes.add(op);
            }
        }
        return nodes;
    }

    public OpNode getOpNode(int id){
        return this.operations.get(createName(id));
    }

    public OpNode getOpNode(String name){
        return this.operations.get(name);
    }


    public VarNode getVarNode(int id){
        return this.variables.get(createName(id));
    }

    public VarNode getVarNode(String name){
        return this.variables.get(name);
    }


    /**
     *  Find all operations and variables in the model
     */
    private void initParser(){
        this.variables = new HashMap<String, VarNode>();
        this.operations = new HashMap<String, OpNode>();

        recursiveVarFinder(model.getResourceRoot());
        recursiveOpFinder(model.getOperationRoot());

    }

    // Finds and stores the variables from the model
    private void recursiveVarFinder(TreeNode var){
       if (var.getChildCount() > 0) {
         for (int i = 0; i < var.getChildCount(); i++) {
            TreeNode subObject = (TreeNode) var.getChildAt(i);
            if (Model.isResource(subObject.getNodeData())) {
               recursiveVarFinder(subObject);
            } else if (Model.isVariable(subObject.getNodeData())){
               VarNode vn = new VarNode(createName(subObject.getNodeData()), subObject);
               this.variables.put(vn.getName(), vn);
            }
         }
      }
    }

    private void recursiveOpFinder(TreeNode op){
        for (int i = 0; i < op.getChildCount(); i++) {
            TreeNode subObject = (TreeNode) op.getChildAt(i);
            if (Model.isOperation(subObject.getNodeData())){
                OpNode on = new OpNode(createName(subObject.getNodeData()), subObject.getId() ,subObject);
                if (!subObject.getParent().equals(model.getOperationRoot())){
                    on.setParent(getOpNode(subObject.getParent()));
                }

                this.operations.put(on.getName(), on);
                recursiveOpFinder(subObject);
            }
        }
    }

    private OpNode getOpNode(TreeNode treenode){
        return operations.get(createName(treenode.getNodeData()));
    }





   private String createName(int id){
      TreeNode n = model.getNode(Integer.valueOf(id).intValue());
      String result = "";
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
       return "OP_" + Integer.toString(od.getId());
       //return od.getName().replace(' ', '_') + EFAVariables.ID_PREFIX + od.getId() + EFAVariables.COST_STRING + od.getCost();
   }

   private String createVarName(ResourceVariableData var){
       return "Var_" + Integer.toString(var.getId());
       //return EFAVariables.VARIABLE_NAME_PREFIX + var.getNodeData().getName().replace(' ', '_') + EFAVariables.ID_PREFIX + var.getId();
   }





}
