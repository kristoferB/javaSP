package sequenceplanner.IO.XML.IntentionalXML;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.visualization.algorithms.VisualizationAlgorithm;

/**
 *
 * @author kbe
 */
public class IntentionalWithoutWindowExecuter implements IAlgorithmListener{
    
    Model model;
    String fileToSave;

    public IntentionalWithoutWindowExecuter(String fileToLoad, String fileToSave) {
        sequenceplanner.IO.XML.IntentionalXML.ParseIntentionalXML parser =
                new sequenceplanner.IO.XML.IntentionalXML.ParseIntentionalXML(fileToLoad,null);
                
        this.model = parser.getModel();
        this.fileToSave = fileToSave;
        
        // Algorithms:
        createVisualizationForAllOperations();
        
        
        
    }
    
    private void createVisualizationForAllOperations(){
        //Call Visualization Algorithm---------------------------------------
       
        VisualizationAlgorithm mVisualizationAlgorithm = new VisualizationAlgorithm("Intentional", this);

        final ISopNode allOperationsNode = new SopNode();
        final ISopNode hasToFinishNode = new SopNode();
        final ISopNode operationsToViewNode = new SopNode();

        Set<SopNodeOperation> allOpsAsNodes = convertOperationsToNodes(model);
        allOperationsNode.getFirstNodesInSequencesAsSet().addAll(allOpsAsNodes);
        operationsToViewNode.getFirstNodesInSequencesAsSet().addAll(allOpsAsNodes);
        Set<ConditionData> conditionNameToIncludeSet = new HashSet<ConditionData>();

        for (SopNodeOperation op : allOpsAsNodes){
            if (op.getOperation().hasToFinish){
                hasToFinishNode.addNodeToSequenceSet(op);
            }
            conditionNameToIncludeSet.addAll(op.getOperation().getConditions().keySet());
        }

        Set<ResourceVariableData> resources = new HashSet<ResourceVariableData>();
        for (TreeNode n : model.getAllVariables()){
            if (n.getNodeData() instanceof ResourceVariableData){
                resources.add((ResourceVariableData)n.getNodeData());
            }
        }


        //init
        final List<Object> list = new ArrayList<Object>();
        list.add(allOperationsNode);
        list.add(operationsToViewNode);
        list.add(hasToFinishNode);
        list.add(conditionNameToIncludeSet);
        list.add(resources);
        mVisualizationAlgorithm.init(list);

        //start
        mVisualizationAlgorithm.start();
    }
    
    private void createVisualizationForEachViewInModel(Model m){
        
    }
    
    private Set<SopNodeOperation> convertOperationsToNodes(Model m){
        Set<SopNodeOperation> nodes = new HashSet<SopNodeOperation>();
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                nodes.add( new SopNodeOperation((OperationData)n.getNodeData()) );
            }
        }
        
        return nodes;
    }
    
    
        @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {

        if (iList.get(0) instanceof ISopNode) {
            final ISopNode sopNode = (ISopNode) iList.get(0);
            model.sops.add(sopNode);          
        }
        
        // save
        sequenceplanner.IO.XML.IntentionalXML.SaveIntentionalXML save=
                new sequenceplanner.IO.XML.IntentionalXML.SaveIntentionalXML(fileToSave,model);
        
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        //
    }


    
}
