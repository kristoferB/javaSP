package sequenceplanner.IO.optimizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.supremica.external.assemblyOptimizer.*;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.product.Seam;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author kbe
 */
public enum ModelAssemblyOptimizerConverter {
    INSTANCE;
    
    public List<AssemblyStructureProtos.Operation> convertModelToProtoOperations(Model m){
        List<AssemblyStructureProtos.Operation> ops = new ArrayList<AssemblyStructureProtos.Operation>();
        List<AssemblyStructureProtos.Variable> variableList = new ArrayList<AssemblyStructureProtos.Variable>(); 
        
        for (TreeNode n : m.getAllVariables()){
            if (n.getNodeData() instanceof ResourceVariableData){
                variableList.add(convertVariableDataProtoVariable((ResourceVariableData)n.getNodeData()));
            }
        }
        
        //List<OperationData> markedOps = getMarkedOperations(m);
        String terminationExpression = createTerminationExpression(m);
        
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                ops.add(convertOperationDataProtoOperation((OperationData)n.getNodeData(),variableList,terminationExpression));
            }
        }
        
        return ops;
    }
    
    public List<AssemblyStructureProtos.Resource> convertModelToProtoResource(Model m){
        List<AssemblyStructureProtos.Resource> resources = new ArrayList<AssemblyStructureProtos.Resource>();
        HashSet<String> resourcesToAdd = new HashSet<String>();
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                OperationData od = (OperationData) n.getNodeData();
                resourcesToAdd.add(od.resource);
            }
        }
        
        for (String r : resourcesToAdd){
            resources.add(MessageFactory.createResource(r, "1"));
        }
        
        return resources;
    }
    
    public void convertProtoOperationsToModel(Model m, List<AssemblyStructureProtos.Operation> ops){
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                OperationData od = (OperationData)n.getNodeData();
                for (AssemblyStructureProtos.Operation o : ops){
                    if (od.getName().equals(o.getName())){                        
                        od.startTime = Integer.toString(o.getStartTime());
                        if (o.getStartTime() >= 0)
                            od.stopTime = Integer.toString(o.getStartTime()+o.getCostTime());
                        else
                            od.stopTime = "-1";
                        break;
                    }
                }
            }
        }
        
        
    }
    
    
    private AssemblyStructureProtos.Variable convertVariableDataProtoVariable(ResourceVariableData vd){
        return MessageFactory.createVariable(
                "id"+vd.getId(), 
                AssemblyStructureProtos.VariableType.INT32, 
                Integer.toString(vd.getInitialValue()));
    }

    private AssemblyStructureProtos.Operation convertOperationDataProtoOperation(OperationData od, 
                                                                                List<AssemblyStructureProtos.Variable> variableList,
                                                                                String terminationExpression) {
        return MessageFactory.createOperation(
                od.getName(),
                this.convertGuard(ConditionType.PRE,od),
                this.convertActions(ConditionType.PRE,od),
                this.convertActions(ConditionType.POST,od),
                this.convertResources(od),
                od.timecost,
                isThisOperationMarked(od),
                new ArrayList<String>(),
                variableList,
                terminationExpression);
        
    }
    
      /*
     * Operation operation = MessageFactory.createOperation(
                name, guard, enter_action, exit_action, use_resource,
		cost_time, terminal, start_after_operations, varibleList);
     * 
     * public static Operation createOperation(String name, String guard, 
	List<String> enter_action, List<String> exit_action, 
	List<String> use_resource, 
	int cost_time, boolean terminal,
	List<String> start_after_operations,
	List<Variable> variableList)
     * 
     */
    
    private String convertGuard(ConditionType type,OperationData od){
        List<ConditionExpression> guards = getGuards(ConditionType.PRE,od);
        String result ="";
        for (ConditionExpression ce : guards)
            result = ExpressionToJavaConverter.INSTANCE.appendExpression(result, ce);     
        
        result = ExpressionToJavaConverter.INSTANCE.appendStringExpression(result, od.resource + ".isAvailable()");
        return result;
    }
    
    private List<String> convertActions(ConditionType type,OperationData od){
        List<ConditionExpression> actions = getActions(type,od);
        List<String> result = new ArrayList<String>();
        for (ConditionExpression ce : actions)
            result.addAll(ExpressionToJavaConverter.INSTANCE.convertActionExpressions(ce));
        
        if (type.equals(ConditionType.PRE))
            result.add(od.resource + ".allocate()");
        if (type.equals(ConditionType.POST)){
            result.add(od.resource + ".deallocate()");
            result.add("setFinished()");
        }
        
        
        return result;
    }

    
    private List<String> convertResources(OperationData od){
        List<String> res = new ArrayList<String>();
        res.add(od.resource);
        return res;
    }



    private List<ConditionExpression> getGuards(ConditionType type, OperationData od) {
        Map<ConditionData, Map<ConditionType, Condition>> conds = od.getConditions();
        List<ConditionExpression> result = new ArrayList<ConditionExpression>();
        for (Map<ConditionType, Condition> map : conds.values()){
            result.add(map.get(type).getGuard());          
        }
        return result;
    }
    
    private List<ConditionExpression> getActions(ConditionType type,OperationData od){
        Map<ConditionData, Map<ConditionType, Condition>> conds = od.getConditions();
        List<ConditionExpression> result = new ArrayList<ConditionExpression>();
        for (Map<ConditionType, Condition> map : conds.values()){
            result.add(map.get(type).getAction());          
        }
        return result;
    }

    private List<OperationData> getMarkedOperations(Model m) {
        List<OperationData> ops = new ArrayList<OperationData>();
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData)
                ops.add((OperationData)n.getNodeData());
        }
        List<OperationData> result = new ArrayList<OperationData>();
        for (int i = 0 ; i<ops.size()-1;i++){
            boolean addIt = true;
            for (int j = i+1 ; j<ops.size();j++){
                if (ops.get(i).seam.equals(ops.get(j).seam)){
                    addIt = false;
                    break;
                }
            }
            if (addIt) result.add(ops.get(i));
        }
        
        return result;
    }

    private String createTerminationExpression(Model m){
        String result = "";
        for (Seam s : m.seams){
            result = ExpressionToJavaConverter.INSTANCE.appendExpression(result, s.getCompleteCondition());
        }
                
        return result;
    }
    
    private boolean isThisOperationMarked(OperationData od) {
  
        if (od.getName().equals("Weld")) return true;
        else return false;
    }
    
  
}


