package sequenceplanner.view.treeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import net.infonode.docking.View;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.DrawSopNode;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;

/**
 * Class for handeling calls from the TreeView.
 * Work in progress...
 * @author QW4z1
 */
public class TreeViewController {
    //Main guiControlller

    private GUIController controller;
    private TreeView view;

    public TreeViewController(GUIController controller, TreeView view) {
        this.controller = controller;
        this.view = view;
        view.addTreeMouseListener(new TreeMouseAdapter());
    }

    /**
     * To insert an operation in {@link Model}.
     */
    public static class InsertOperation implements ActionListener {

        private Model mModel;

        public InsertOperation(final Model iModel) {
            this.mModel = iModel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mModel.createModelOperationNode();
        }
    }

    /**
     * Open the attribute panel for an operation.
     */
    public static class GetOperationAttributes implements ActionListener {

        private TreeNode mTreeNode;
        private GUIController mGUIController;

        public GetOperationAttributes(TreeNode mTreeNode, GUIController mGUIController) {
            this.mTreeNode = mTreeNode;
            this.mGUIController = mGUIController;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mTreeNode == null || mGUIController == null) {
                return;
            }
            if (Model.isOperation(mTreeNode.getNodeData())) {
                final OperationData opData = (OperationData) mTreeNode.getNodeData();
                mGUIController.addPropertyPanelView(opData);
            }
        }
    }

    /**
     * To remove an operation from {@link Model}.
     */
    public static class RemoveOperation implements ActionListener {

        private Model mModel;
        private TreeNode mTreeNode;
        private GUIController mGUIController;

        public RemoveOperation(final TreeNode iNode, final GUIController iGUIController) {
            this.mModel = iGUIController.getModel();
            this.mTreeNode = iNode;
            this.mGUIController = iGUIController;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //Remove attribute tab
            mGUIController.getView().removeOperationObjectView(mTreeNode);

            //Loop views and remove operation if present
            for (final OperationView opView : mGUIController.getGUIModel().getOperationViews()) {
                if (opView.removeOperationInGraph(mTreeNode.getId())) {
                    //Operation was removed
                }
            }

            //Remove operation from model
            mModel.removeChild(mModel.getOperationRoot(), mTreeNode);

            System.out.println("Remove operation: " + mTreeNode.toString());
        }
    }

    /**
     * To insert an operation view in {@link Model}.
     */
    public static class InsertOperationView implements ActionListener {

        private GUIController mGUIController;

        public InsertOperationView(final GUIController iGUIController) {
            this.mGUIController = iGUIController;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mGUIController == null) {
                return;
            }
            mGUIController.mOpViewController.createOperationView();
        }
    }

    /**
     * To remove an operation view in {@link Model}.
     */
    public static class RemoveOperationView implements ActionListener {

        private TreeNode mTreeNode;
        private GUIController mGUIController;

        public RemoveOperationView(final GUIController iGUIController, final TreeNode iTreeNode) {
            this.mGUIController = iGUIController;
            this.mTreeNode = iTreeNode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mGUIController == null) {
                return;
            }

            if (Model.isView(mTreeNode.getNodeData())) {
                final ViewData viewData = (ViewData) mTreeNode.getNodeData();
                //Remove conditions based on view
                mGUIController.getModel().removeConditions(viewData.getName());

                //Remove Operation view
                mGUIController.getView().removeOperationView(mTreeNode);

                //Remove viewdata from Model
                mGUIController.getModel().removeChild(mGUIController.getModel().getViewRoot(), mTreeNode);
            }



        }
    }

    /**
     * Custom MouseAdapter. Handles clicks on the SOPs in the treeview.
     */
    class TreeMouseAdapter extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            popup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            popup(e);
        }

        /**
         * Double click calls the main controller and opens a new tab
         * with the SOPview.
         * @param e the MouseEvent
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                TreePath path = view.tree.getPathForLocation(e.getX(), e.getY());

                if (path != null) {
                    TreeNode t = (TreeNode) path.getLastPathComponent();
                    if (Model.isView(t.getNodeData())) {
                        controller.getView().setOperationViewFocus(t);

//                        controller.addNewOpTab((ViewData) t.getNodeData());
//                    } else if (Model.isOperation(t.getNodeData())) {
//                        OperationData data = (OperationData) t.getNodeData();
//                        controller.addNewOpTab(view.getModel().getOperationView(data.getId()));
                    }

                }


            }
        }

        public void popup(MouseEvent e) {
            if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {

                TreePath path = view.tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    view.tree.setSelectionPath(path);
                    ClickMenu c = new ClickMenu((TreeNode) path.getLastPathComponent(), view.getModel(), controller);
                    c.show(view, e);
                }
            }
        }
    }
}
