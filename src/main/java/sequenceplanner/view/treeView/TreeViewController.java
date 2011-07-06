package sequenceplanner.view.treeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.DrawSopNode;
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

        public RemoveOperation(final Model iModel, final TreeNode iNode) {
            this.mModel = iModel;
            this.mTreeNode = iNode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Remove operation: " + mTreeNode.toString());
        }
    }

    public static class TestTemp implements ActionListener {

        private Model mModel;
        private TreeNode mTreeNode;
        private GUIController mGUIController;

        public TestTemp(final Model iModel, final TreeNode iNode, final GUIController iGUIController) {
            this.mModel = iModel;
            this.mTreeNode = iNode;
            this.mGUIController = iGUIController;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Model.isView(mTreeNode.getNodeData())) {
                //Add new view
                final OperationView opView = mGUIController.getGUIModel().createNewOpView();
                mGUIController.addNewOpTab(opView);
                final ViewData vd = (ViewData) mTreeNode.getNodeData();
                
                new DrawSopNode(vd.mSopNodeRoot, opView.getGraph());

            }
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
            final OperationView opView = mGUIController.getGUIModel().createNewOpView();
            mGUIController.addNewOpTab(opView);
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
                        controller.addNewOpTab((ViewData) t.getNodeData());
                    } else if (Model.isOperation(t.getNodeData())) {
                        OperationData data = (OperationData) t.getNodeData();
                        controller.addNewOpTab(view.getModel().getOperationView(data.getId()));
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
