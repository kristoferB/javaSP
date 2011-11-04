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
import sequenceplanner.model.data.OperationData;
import sequenceplanner.visualization.algorithms.VisualizationAlgorithm;

/**
 *
 * @author kbe
 */
public class IntentionalWithoutWindowExecuter implements IAlgorithmListener{

    public IntentionalWithoutWindowExecuter(String fileToLoad, String fileToSave) {
        sequenceplanner.IO.XML.IntentionalXML.parseIntentionalXML parser =
                new sequenceplanner.IO.XML.IntentionalXML.parseIntentionalXML(fileToLoad,null);
                
        Model m = parser.getModel();
        
        // Algorithms:
        createVisualizationForAllOperations(m);
        
        // save
        sequenceplanner.IO.XML.IntentionalXML.saveIntentionalXML save=
                new sequenceplanner.IO.XML.IntentionalXML.saveIntentionalXML(fileToSave,m);
        
    }
    
    private void createVisualizationForAllOperations(Model m){
        //Call Visualization Algorithm---------------------------------------
            VisualizationAlgorithm mVisualizationAlgorithm = new VisualizationAlgorithm("Intentional", this);

            final ISopNode allOperationsNode = new SopNode();
            final ISopNode hasToFinishNode = new SopNode();
            final ISopNode operationsToViewNode = new SopNode();
            
            Set<SopNodeOperation> allopsAsNodes = convertOperationsToNodes(m);
            allOperationsNode.getFirstNodesInSequencesAsSet().addAll(allopsAsNodes);
            operationsToViewNode.getFirstNodesInSequencesAsSet().addAll(allopsAsNodes);
            
            
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

            newMessageFromAlgorithm("...drawing...", null);

            //Translate datastructure to graph in operation view.
            //mOpView.drawGraph(sopNode);

            newMessageFromAlgorithm("...finished", null);
        }
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        //
    }


    
}
