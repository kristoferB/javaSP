package sequenceplanner.IO.XML.IntentionalXML;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sequenceplanner.IO.optimizer.Optimizer;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeEmpty;
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
        // removed since the allocation generation is not working correct 
        //createVisualizationForAllOperations();
        
        runOptimizer();
        
        CreateBooking.INSTANCE.createBookingForSeams(parser.getModel());
        CreateBooking.INSTANCE.createBookingForResources(parser.getModel());
        
        createVisualizationForAllOperations();
        
        saveFile();
        
    }
    
    private void runOptimizer(){
        Optimizer.optimizeOperations(model);     
    }
    
    private void createVisualizationForAllOperations(){
        //Call Visualization Algorithm---------------------------------------
       
        VisualizationAlgorithm mVisualizationAlgorithm = new VisualizationAlgorithm("Intentional", this);

        final SopNode allOperationsNode = new SopNodeEmpty();
        final SopNode hasToFinishNode = new SopNodeEmpty();
        final SopNode operationsToViewNode = new SopNodeEmpty();

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

        if (iList.get(0) instanceof SopNode) {
            final SopNode sopNode = (SopNode) iList.get(0);
            model.sops.add(sopNode);          
        }
        
        saveFile();
        
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        //
    }
    
    private void saveFile(){
        // save
        sequenceplanner.IO.XML.IntentionalXML.SaveIntentionalXML save=
                new sequenceplanner.IO.XML.IntentionalXML.SaveIntentionalXML(fileToSave,model);
    }


    
}
