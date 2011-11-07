package sequenceplanner.IO.XML.IntentionalXML;

import java.util.ArrayDeque;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionOperator;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.datamodel.product.Seam;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;

/**
 * This class should prbably be moved to a general place, but for now 
 * it is specific for when parsing interntional XML
 * 
 * @author kbe
 */
public class CreateBooking {

   


    

    private CreateBooking() {}
    
    public static void createBookingForSeams(Model model){
        for (ISopNode sop : model.sops){
            createSeamBooking(sop,model);
            break;
        }
    }
    
    public static void createBookingForResources(Model model){
        for (ISopNode sop : model.sops){
            createResourceBooking(sop,model);
            break;
        }
    }
        
    private static void createResourceBooking(ISopNode sop,Model model) {
        if (sop == null) return;
        
        if (sop.getOperation() != null && !sop.getOperation().resource.isEmpty() 
                                       && childrenHaveSameResource(sop,sop.getOperation().resource)){
            addBookingConditions(sop.getOperation().resource, sop.getOperation(), model);
        } else {
            for (ISopNode n : sop.getFirstNodesInSequencesAsSet()){
                createResourceBooking(n,model);
            }
        }
        
        createResourceBooking(sop.getSuccessorNode(),model);        
    }
    
    private static boolean childrenHaveSameResource(ISopNode sop, String resource) {
        ArrayDeque<ISopNode> stack = new ArrayDeque();
        for (ISopNode n : sop.getFirstNodesInSequencesAsSet())
            stack.push(n);
               
        while (!stack.isEmpty()){
            ISopNode n = stack.pop();
            if (n.getOperation() != null && !n.getOperation().resource.isEmpty()
                    && !n.getOperation().resource.equals(resource)){
                return false;
            } else {
                for (ISopNode k : n.getFirstNodesInSequencesAsSet())
                    stack.push(k);        
            }                
        }
        return true;
    }

    private static void createSeamBooking(ISopNode sop,Model model) {
        // For now, seams should not be on children
        if (sop == null) return;
        for (ISopNode n : sop.getFirstNodesInSequencesAsSet()){
            if (n.getOperation() != null && !n.getOperation().seam.isEmpty()){
                for (Seam seam : model.seams){
                    if (seam.getName().equals(n.getOperation().seam)){
                        for (String s : seam.getBlocks()){
                            addBookingConditions(s,n.getOperation(),model);
                        }
                    }
                }               
            } else {
                createSeamBooking(n,model);                
            }
        }
        createSeamBooking(sop.getSuccessorNode(),model);
    }
    
    // Refactor following methods

    private static void addBookingConditions(String varName, OperationData op,Model model) {
        int id = getVariableId(varName,model);
        Condition pre = new Condition();
        Condition post = new Condition();
        ConditionStatement preGuard = new ConditionStatement("id" + id, ConditionStatement.Operator.Equal,"0");
        ConditionStatement preAction = new ConditionStatement("id" + id, ConditionStatement.Operator.Assign,"1");
        ConditionStatement postAction = new ConditionStatement("id" + id, ConditionStatement.Operator.Assign,"0");
        
        pre.getGuard().appendElement(ConditionOperator.Type.AND, preGuard);
        pre.getAction().appendElement(ConditionOperator.Type.SEMIKOLON, preAction);
        post.getAction().appendElement(ConditionOperator.Type.SEMIKOLON, postAction);
        
        ConditionData cd = new ConditionData("Booking");
        op.addCondition(cd, ConditionType.PRE, pre);
        op.addCondition(cd, ConditionType.POST, post);
        
    }
    
    private static void addBookingPreCondition(String varName, OperationData op,Model model){
        int id = getVariableId(varName,model);
        Condition pre = new Condition();
        ConditionStatement preGuard = new ConditionStatement("id" + id, ConditionStatement.Operator.Equal,"0");
        ConditionStatement preAction = new ConditionStatement("id" + id, ConditionStatement.Operator.Assign,"1");
        
        pre.getGuard().appendElement(ConditionOperator.Type.AND, preGuard);
        pre.getAction().appendElement(ConditionOperator.Type.SEMIKOLON, preAction);
        
        ConditionData cd = new ConditionData("Booking");
        op.addCondition(cd, ConditionType.PRE, pre);
    }
    
    private static void addBookingPostCondition(String varName, OperationData op,Model model){
        int id = getVariableId(varName,model);
        Condition post = new Condition();
        ConditionStatement postGuard = new ConditionStatement("id" + id, ConditionStatement.Operator.Equal,"1");
        ConditionStatement postAction = new ConditionStatement("id" + id, ConditionStatement.Operator.Assign,"0");
        
        post.getGuard().appendElement(ConditionOperator.Type.AND, postGuard);
        post.getAction().appendElement(ConditionOperator.Type.SEMIKOLON, postAction);
        
        ConditionData cd = new ConditionData("Booking");
        op.addCondition(cd, ConditionType.POST, post);
    }
    
    private static int getVariableId(String name, Model model){
        for (TreeNode n : model.getAllVariables()){
            if (n.getNodeData().getName().equals(name)){
                return n.getNodeData().getId();
            }
        }
        return -1;
    }
    
    // Not a good implementation. Must think this trough!
    private static boolean sequenceHasSameResource(ISopNode n, String resource) {
        return false;
//        if (n.getSuccessorNode() == null) return false;
//        ISopNode next = n.getSuccessorNode();
//               
//        while (next != null){
//                    
//        }
//        return true;
    }
    
}
