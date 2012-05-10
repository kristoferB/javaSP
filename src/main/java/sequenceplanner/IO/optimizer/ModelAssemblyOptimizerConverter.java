package sequenceplanner.IO.optimizer;

import java.util.*;
import org.supremica.external.assemblyOptimizer.*;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.product.Seam;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.visualization.algorithms.ISupremicaInteractionForVisualization.Type;
import sequenceplanner.visualization.algorithms.RelationContainer;

/**
 *
 * @author kbe
 */
public enum ModelAssemblyOptimizerConverter {
    INSTANCE;
    
    public List<AssemblyStructureProtos.Operation> convertModelToProtoOperations(Model m, RelationContainer rc){
        List<AssemblyStructureProtos.Operation> ops = new ArrayList<AssemblyStructureProtos.Operation>();
        List<AssemblyStructureProtos.Variable> variableList = new ArrayList<AssemblyStructureProtos.Variable>(); 
        
        for (TreeNode n : m.getAllVariables()){
            if (n.getNodeData() instanceof ResourceVariableData){
                variableList.add(convertVariableDataProtoVariable((ResourceVariableData)n.getNodeData()));
            }
        }
        
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                variableList.add(convertOperationDataProtoVariable((OperationData)n.getNodeData()));
            }
        }
        
        //List<OperationData> markedOps = getMarkedOperations(m);
        String terminationExpression = createTerminationExpression(m, rc);
        sequenceplanner.IO.optimizer.ProductLocker.INSTANCE.initializeLocker(createSeamBlockMap(m));
        
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
                if (!od.resource.isEmpty())
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
    
    
   public void convertToRelationContainer(RelationIdentifier ri,RelationContainer rc, Model m,List<AssemblyStructureProtos.Operation> ops) {
        if (ri == null) return;
        Map<OperationData, Map<OperationData, Integer>> map = new HashMap<OperationData, Map<OperationData, Integer>>();
        List<OperationData> operations = getAllOperations(m);
        for (OperationData od : operations){          
            Map<OperationData, Integer> relMap = new HashMap<OperationData, Integer>();
            for (OperationData relOp : operations){  
                relMap.put(relOp, ri.getRelation(od.getName(),relOp.getName()));
            } 
            map.put(od, relMap);                                             
        }
        //fixMissedRelations(map);
        rc.setOperationRelationMap(map);
   }

   public void convertToRelationContainer(RelationIdentifier3StateOps ri,RelationContainer rc, Model m,List<AssemblyStructureProtos.Operation> ops) {
        if (ri == null) return;
        Map<OperationData, Map<OperationData, Integer>> map = new HashMap<OperationData, Map<OperationData, Integer>>();
        List<OperationData> operations = getAllOperations(m);
        for (OperationData od : operations){          
            Map<OperationData, Integer> relMap = new HashMap<OperationData, Integer>();
            for (OperationData relOp : operations){  
                relMap.put(relOp, ri.getRelation(od.getName(),relOp.getName()));
            } 
            map.put(od, relMap);                                             
        }
        //fixMissedRelations(map);
        rc.setOperationRelationMap(map);
   }
   
   private void fixMissedRelations(Map<OperationData, Map<OperationData, Integer>> map){
       for (Map.Entry<OperationData, Map<OperationData, Integer>> e : map.entrySet()){
           for (Map.Entry<OperationData, Integer> g : e.getValue().entrySet()){
               if (g.getValue().equals(IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12)){
                   if (!operationHasAlternative(e.getKey(),map)){
                       g.setValue(new Integer(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12));
                   }
               } else if (g.getValue().equals(IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_21)){
                   if (!operationHasAlternative(g.getKey(),map)){
                       g.setValue(new Integer(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_21));
                   }
               }
           }
       }
   }
   
   private boolean operationHasAlternative(OperationData od,Map<OperationData, Map<OperationData, Integer>> map){
       Map<OperationData, Integer> relToOp = map.get(od);
       for (Map.Entry<OperationData, Integer> e : relToOp.entrySet()){
           if (!e.getKey().equals(od))
             if (e.getValue().equals(IRelateTwoOperations.ALTERNATIVE)) return true;
       }
       return false;
   }
        
/*
 * public interface IRelateTwoOperations {

    //Possible relations
    Integer ALWAYS_IN_SEQUENCE_12 = 0;
    Integer ALWAYS_IN_SEQUENCE_21 = 1;
    Integer SOMETIMES_IN_SEQUENCE_12 = 2;
    Integer SOMETIMES_IN_SEQUENCE_21 = 3;
    Integer PARALLEL = 4;
    Integer ALTERNATIVE = 5;
    Integer ARBITRARY_ORDER = 6;
    Integer HIERARCHY_12 = 7;
    Integer HIERARCHY_21 = 8;
    Integer SOMETIMES_IN_HIERARCHY_12 = 9;
    Integer SOMETIMES_IN_HIERARCHY_21 = 10;
    Integer OTHER = 11;
 * 
 */
        
             
    
            
   private List<OperationData> getAllOperations(Model m){
       List<OperationData> ops = new ArrayList<OperationData>(m.getAllOperations().size());
       for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                ops.add((OperationData) n.getNodeData());
            }
       }
       return ops;
   }
    
    
    private AssemblyStructureProtos.Variable convertVariableDataProtoVariable(ResourceVariableData vd){
        return MessageFactory.createVariable(
                "id"+vd.getId(), 
                AssemblyStructureProtos.VariableType.INT32, 
                Integer.toString(vd.getInitialValue()));
    }
    
    private AssemblyStructureProtos.Variable convertOperationDataProtoVariable(OperationData od){
        return MessageFactory.createVariable(
                Type.OPERATION_VARIABLE_PREFIX.toString()+od.getId(), 
                AssemblyStructureProtos.VariableType.INT32, 
                "0");
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
                false,  // this is if an operation is marked. But we should only use expression
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
        List<ConditionExpression> guards = getGuards(type,od);
        String result ="";
        for (ConditionExpression ce : guards)
            result = ExpressionToJavaConverter.INSTANCE.appendExpression(result, ce);     
        
        if (!od.resource.isEmpty())
            result = ExpressionToJavaConverter.INSTANCE.appendStringExpression(result, od.resource + ".isAvailable()");
        result = ExpressionToJavaConverter.INSTANCE.appendStringExpression
                (result, "sequenceplanner.IO.optimizer.ProductLocker.INSTANCE.isSeamAvailible(\""+od.seam+"\")");
     
        return result;
    }
    
    private List<String> convertActions(ConditionType type,OperationData od){
        List<ConditionExpression> actions = getActions(type,od);
        List<String> result = new ArrayList<String>();
        for (ConditionExpression ce : actions)
            result.addAll(ExpressionToJavaConverter.INSTANCE.convertActionExpressions(ce));
        
        if (type.equals(ConditionType.PRE)){
            if (!od.resource.isEmpty())
                result.add(od.resource + ".allocate()");
            result.add("sequenceplanner.IO.optimizer.ProductLocker.INSTANCE.lockSeam(\""+od.seam+"\",\""+od.getName()+"\")");
            result.add(Type.OPERATION_VARIABLE_PREFIX.toString()+od.getId()+ " = 1 ");
        } if (type.equals(ConditionType.POST)){
            if (!od.resource.isEmpty())
                result.add(od.resource + ".deallocate()");
            result.add("sequenceplanner.IO.optimizer.ProductLocker.INSTANCE.unLockSeam(\""+od.seam+"\",\""+od.getName()+"\")");
            result.add(Type.OPERATION_VARIABLE_PREFIX.toString()+od.getId()+" = 2 ");
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
            if (map.containsKey(type))
                result.add(map.get(type).getGuard());          
        }
        return result;
    }
    
    private List<ConditionExpression> getActions(ConditionType type,OperationData od){
        Map<ConditionData, Map<ConditionType, Condition>> conds = od.getConditions();
        List<ConditionExpression> result = new ArrayList<ConditionExpression>();
        for (Map<ConditionType, Condition> map : conds.values()){
            if (map.containsKey(type))
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

    private String createTerminationExpression(Model m, RelationContainer rc){
        String result = "";
        for (Seam s : m.seams){
            result = ExpressionToJavaConverter.INSTANCE.appendExpression(result, s.getCompleteCondition());
        }
        
        if (result.isEmpty() && rc != null){
            for (SopNode n : rc.getOfinishsetSopNode().getFirstNodesInSequencesAsSet()){
                if (n instanceof SopNodeOperation && n.getOperation() != null){
                    String expr = Type.OPERATION_VARIABLE_PREFIX.toString()+n.getOperation().getId()+" == 2 ";
                    result = ExpressionToJavaConverter.INSTANCE.appendStringExpression(result, expr);
                }
            }
        }
        
                
        return result;
    }
    
    private boolean isThisOperationMarked(OperationData od) {
  
        if (od.getName().equals("Weld")) return true;
        else return false;
    }
    
    private Map<String,Set<String>> createSeamBlockMap(Model m){
        Map<String,Set<String>> result = new HashMap<String,Set<String>>();
        for (Seam s : m.seams){
            result.put(s.getName(), s.getBlocks());
        }
        
        return result;
    }

 
    
    
    
  
}


