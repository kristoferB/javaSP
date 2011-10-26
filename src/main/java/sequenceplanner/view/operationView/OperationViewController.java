package sequenceplanner.view.operationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.gui.controller.CellNameObserver;
import sequenceplanner.visualization.algorithms.VisualizationAlgorithm;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;

import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.graphextension.Cell;

/**
 * Observes the operations in the model and updates all existing operation views when the model is changed
 *
 * @author Evelina
 */
public class OperationViewController implements Observer{

    //All exsting operation views
    private LinkedList<OperationView> views;
//    private static Map<ViewData,OperationView> mViewViewMap;
    private final GUIController mGUIController;


    public OperationViewController() {
        this(null);
    }

    public OperationViewController(GUIController iGUIController) {
        mGUIController = iGUIController;
        views = new LinkedList();
    }

    /**
     * Add an operation view to observe
     *
     */
    public void addOperationView(OperationView v) {
        views.add(v);
    }

    public OperationView createOperationView() {
        final int id = Model.newId();
        return createOperationView(new ViewData("Sop View " + id, id));
    }

    public OperationView createOperationView(final ViewData iViewData) {
        final OperationView opView = mGUIController.getGUIModel().createNewOpView(iViewData);
        mGUIController.addNewOpTab(opView);
        return opView;
    }

    @Override
    public void update(Observable o, Object arg) {
        //System.out.println("OVC");
        if (arg instanceof OperationData) {
            OperationData od = (OperationData) arg;
            //System.out.println("operation update: " + od.getName());
            for (OperationView operationView : views) {
                //if operation view contains od with this id, update od
                Map cells = operationView.getGraphModel().getCells();
                for (Object c : cells.values()){
                    if (operationView.getGraph().isOperation(c) || operationView.getGraph().isSOP(c)){
                        OperationData data = (OperationData) operationView.getGraphModel().getValue(c);
                        if (data.getId() == od.getId()) {
                            //replace old operation data with the updated version
                            operationView.getGraph().setValue(c, od);
                        }
                    }
                }
                //operationView.redrawGraph();
            }
        }
    }

    /**
     * To update view with operations from model. Update based on ids.</br>
     * Conditions from this view are updated.
     */
    public static class SaveOperationView implements ActionListener {

        private OperationView mOpView;

        public SaveOperationView(final OperationView iOpView) {
            this.mOpView = iOpView;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            save(mOpView);

        }
    }

    public static void save(final OperationView iOpView) {
        if (iOpView == null) {
            return;
        }
        final ViewData viewData = iOpView.mViewData;
        if (viewData != null) {

            iOpView.model.removeConditions(viewData.mConditionData);

            iOpView.model.saveView(viewData);

            iOpView.model.updateSopNodeStructureWithObjectsInModel(viewData.mSopNodeForGraphPlus.getRootSopNode(true));

            viewData.storeCellData();

            //Add observers to change name in cell when operation change name.
            final Map<ISopNode, Cell> map = viewData.mSopNodeForGraphPlus.getNodeCellMap(false);
            for (final ISopNode node : map.keySet()) {
                final Cell cell = map.get(node);
                if (node instanceof SopNodeOperation) {
                    cell.setValue(node.getOperation());
                    node.getOperation().addObserver(new CellNameObserver(cell, iOpView.getGraph()));
                    System.out.println("Name observer added for: " + node.getOperation().getName());
                }
            }

            //Update name that is showed for user.
            viewData.mConditionData.setName(viewData.getName());

            iOpView.model.setConditions(viewData.mSopNodeForGraphPlus.getRootSopNode(false), viewData.mConditionData);

//            System.out.println("SOP structure that is saved");
//            System.out.println(viewData.mSopNodeForGraphPlus.getRootSopNode(false).toString());

            System.out.println("save was done: " + viewData.getName());
        }
    }

    public static class VisualizeOperationView implements ActionListener {

        private OperationView mOpView;
        private boolean mLocal;

        /**
         *
         * @param iOpView {@link OperationView} that contains the operations to visualize
         * @param iLocal true = is not working..., false = take all operations in Model into consideration for calculation.
         */
        public VisualizeOperationView(final OperationView iOpView, boolean iLocal) {
            this.mOpView = iOpView;
            this.mLocal = iLocal;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            save(mOpView);
            startVisualization(mOpView, mLocal);
        }
    }

    private static void startVisualization(final OperationView iOpView, final boolean iLocal) {
        if (iOpView == null) {
            return;
        }
        ISopNode rootForOperationsToInclude = new SopNode();
        final ISopNode rootForOperationsToView = new SopNode();
        final Set<OperationData> operationsToView = new SopNodeToolboxSetOfOperations().getOperations(iOpView.mViewData.mSopNodeForGraphPlus.getRootSopNode(true), true);

        //To store id (as string) for operations to visualize
        final Set<String> stringIdSet = new HashSet<String>();
        for (final OperationData opData : operationsToView) {
            final String id = Integer.toString(opData.getId());
            stringIdSet.add(id);
        }

        final List<TreeNode> allOperations = iOpView.model.getAllOperations();

        for (final TreeNode tn : allOperations) {
            if (Model.isOperation(tn.getNodeData())) {
                final OperationData opData = (OperationData) tn.getNodeData();
                final ISopNode opNode = new SopNodeOperation(opData);

                //Add to rootForOperationsToInclude
                rootForOperationsToInclude.addNodeToSequenceSet(opNode);

                if (stringIdSet.contains(Integer.toString(opData.getId()))) {
                    rootForOperationsToView.addNodeToSequenceSet(opNode);
                }
            }
        }

        if (iLocal) {
            rootForOperationsToInclude = rootForOperationsToView;
        }

        //Remove old graph-------------------------------------------------------
        iOpView.getGraph().selectAll();
        iOpView.getGraph().deleteMarkedCells();

//        //Start visualization
//        final VisualizationAlgorithm sv = new VisualizationAlgorithm("FromOperationViewController", this);
//
//        final List<Object> list = new ArrayList<Object>();
//        list.add(rootForOperationsToInclude);
//        list.add(rootForOperationsToView);
//        list.add(new SopNode());
//        list.add(null);
//        sv.init(list);
//
//        sv.start();
//
//        mCurretOperationView = iOpView;
    }


}
