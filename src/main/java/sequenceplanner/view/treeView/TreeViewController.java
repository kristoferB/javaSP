package sequenceplanner.view.treeView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;

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
                        //TODO q... fix controller to check for illegal multiple views
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
                    ClickMenu c = new ClickMenu((TreeNode) path.getLastPathComponent(), view.getModel());
                    c.show(view, e);
                }
            }
        }
    }
}
