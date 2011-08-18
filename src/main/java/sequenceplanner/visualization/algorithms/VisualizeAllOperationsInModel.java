package sequenceplanner.visualization.algorithms;

import java.util.ArrayList;
import java.util.List;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;

/**
 * To visualize all operations in model in a new operation view
 * @author patrik
 */
public class VisualizeAllOperationsInModel implements IAlgorithmListener {

    private GUIController mController;

    public VisualizeAllOperationsInModel(GUIController mController) {
        this.mController = mController;

        init();
    }
    
    void init() {
        final Model model = mController.getModel();
        //Get operations
        final ISopNode sopNode = new SopNode();
        final List<TreeNode> operaitonList= model.getAllOperations();
        for(final TreeNode tn : operaitonList) {
            final OperationData opData = (OperationData) tn.getNodeData();
            sopNode.addNodeToSequenceSet(new SopNodeOperation(opData));
        }

        //Visualization algorithm------------------------------------------------
        final VisualizationAlgorithm va = new VisualizationAlgorithm("FromAllOperationsInModel", this);

        //init
        final List<Object> list = new ArrayList<Object>();
        list.add(sopNode);
        list.add(sopNode);
        list.add(sopNode);
        list.add(model.getAllConditions());
        va.init(list);

        va.start();
        //-----------------------------------------------------------------------
    }

    @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
        if (iList.get(0) instanceof ISopNode) {
            final ISopNode sopNode = (ISopNode) iList.get(0);

            final OperationView opView = mController.mOpViewController.createOperationView();
            
            opView.drawGraph(sopNode);
        }
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        System.out.println("Visualization algorithm: " + iMessage);
    }
}
