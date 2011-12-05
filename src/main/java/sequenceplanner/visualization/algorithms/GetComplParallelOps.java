package sequenceplanner.visualization.algorithms;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.OperationData;

/**
 * Temporary solution to remove compl paralell ops
 * @author kbe
 */
public class GetComplParallelOps {

    public GetComplParallelOps() {
    }
    
    public Set<String> getParallelOps(final SopNode AllOps){
        Set<OperationData> ops = new HashSet<OperationData>();
        for (SopNode n : AllOps.getFirstNodesInSequencesAsSet())
            if (n.getOperation() !=null) ops.add(n.getOperation());
             
        return findParallelOps(createConnections(ops));
    }
    
    private Map<String,Node> createConnections(Set<OperationData> ops){
        Map<String,Node> allConnectedNodes = new HashMap<String,Node>();
        
        //Fill with operations
        for (OperationData op : ops){
            Node n = new Node("id"+op.getId(),false);
            allConnectedNodes.put(n.ID, n);
        }
        
        // create connections
        for (OperationData op : ops){
            Set<String> idInCond = getIDInConditions(op);
            Node thisOP = allConnectedNodes.get("id"+op.getId());
            for (String id : idInCond){
                Node interactNode = null;
                if (!allConnectedNodes.containsKey(id)){
                    Node n = new Node(id,true);
                    allConnectedNodes.put(id, n);
                    interactNode = n;
                } else {
                    interactNode = allConnectedNodes.get(id);
                }
                interactNode.setInteracter(thisOP);
                thisOP.setInteracter(interactNode);                
            }
        }
        
        return allConnectedNodes;
    }

    private Set<String> getIDInConditions(OperationData op) {
        Set<String> condIds = new HashSet<String>();
        for (Map<ConditionType, Condition> map : op.getConditions().values()){
            for (Condition c : map.values()){
                condIds.addAll(getIdsInExpression(c.getGuard()));
                condIds.addAll(getIdsInExpression(c.getAction()));
            }
        }
        return condIds;
    }
    
    private Set<String> getIdsInExpression(ConditionExpression expr){
        Set<String> ids = new HashSet<String>();
        if (expr.isEmpty()) return ids;
        
        ArrayDeque<ConditionElement> stack = new ArrayDeque();
        stack.push(expr.getExpressionRoot());
        if (expr.hasNextElement()) stack.push(expr.getNextElement());
        
        Pattern p = Pattern.compile("id\\d+");
        while (!stack.isEmpty()){
            ConditionElement element = stack.pop();
            if (element.isStatement()){
                Matcher mValue = p.matcher(((ConditionStatement)element).getValue());
                while (mValue.find())
                    ids.add(mValue.group());              
                
                Matcher mVariable = p.matcher(((ConditionStatement)element).getVariable());
                while (mVariable.find())
                    ids.add(mVariable.group());
                
            } else if (element.isExpression()){
                stack.push(((ConditionExpression)element).getExpressionRoot());
            }
            if (element.hasNextElement()) stack.push(element.getNextElement());
        }
        
        return ids;
    }
    
    private Set<String> findParallelOps(Map<String,Node> map){
        Set<String> ids = new HashSet<String>();
        for (Node n : map.values()){
            if (!n.isVar && !n.hasInteracter()) {
                ids.add(n.ID);
            } else if (!n.isVar && n.onlyVarInteracters()) {
                for (Node var : n.getVars()){
                    if (var.hasOnlyInteractor(n)){
                        ids.add(n.ID);
                    }
                }
            }
        }
        
        return ids;
    }
    

    private class Node{
        public final String ID;
        public final boolean isVar;
        
        private Set<Node> interacters = new HashSet<Node>();
        
        public Node(String id, boolean isVar){
            this.ID = id;
            this.isVar = isVar;
        }
        
        public void setInteracter(Node n){
            interacters.add(n);        
        }
        
        public boolean hasInteracter(){
            return !interacters.isEmpty();
        }
        
        public boolean onlyVarInteracters(){
            for (Node n : this.interacters){
                if (!n.isVar) return false;
            }
            return true;
        }
        
        public Set<Node> getVars(){
            Set<Node> vars = new HashSet<Node>();
            for (Node n : this.interacters){
                if (n.isVar) vars.add(n);
            }
            return vars;
        }
        
        public boolean hasOnlyInteractor(Node interactor){
            for (Node n : this.interacters){
                if (!n.equals(interactor)) return false;
            }
            return true;
        }
        
        
    }
    
    
    
}
