package sequenceplanner.IO.optimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.supremica.external.assemblyOptimizer.*;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionExpression;
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
        
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                ops.add(convertOperationDataProtoOperation((OperationData)n.getNodeData(),variableList));
            }
        }
        
        return ops;
    }
    
    public List<AssemblyStructureProtos.Resource> convertModelToProtoResource(Model m){
        List<AssemblyStructureProtos.Resource> resources = new ArrayList<AssemblyStructureProtos.Resource>();
        
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                OperationData od = (OperationData) n.getNodeData();
                resources.add(MessageFactory.createResource(od.resource, "1"));
            }
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
                                                                                List<AssemblyStructureProtos.Variable> variableList) {
        return MessageFactory.createOperation(
                od.getName(),
                this.convertPreGuard(od),
                this.convertPreAction(od),
                this.convertPostActions(od),
                this.convertResources(od),
                od.timecost,
                isThisOperationMarked(od),
                new ArrayList<String>(),
                variableList);
        
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
    
    private String convertPreGuard(OperationData od){
        List<ConditionExpression> guards = getGuards(ConditionType.PRE,od);
        for
        return "";
    }
    
    private List<String> convertPreAction(OperationData od){
        return new ArrayList<String>();
    }
    
    private List<String> convertPostActions(OperationData od){
        return new ArrayList<String>();
    }
    
    private List<String> convertResources(OperationData od){
        List<String> res = new ArrayList<String>();
        res.add(od.resource);
        return res;
    }

    private boolean isThisOperationMarked(OperationData od) {
        return true;
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
    
  
}


