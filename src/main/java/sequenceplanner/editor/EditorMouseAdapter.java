package sequenceplanner.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens for MouseEvents and creates EditorClickMenus
 *
 * @author Evelina
 */
public class EditorMouseAdapter extends MouseAdapter{

    private JTree tree;
    private EditorTreeModel treeModel;
    private Object clickedComponent;
    private TreePath lastPath;

    public EditorMouseAdapter(JTree t, EditorTreeModel m){
        tree = t;
        treeModel = m;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        popup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        popup(e);
    }

    /**
     * Creates a EditorClickMenu for clicked node
     *
     * @param e a MouseEvent
     */
    private void popup(MouseEvent e) {
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
            lastPath = tree.getPathForLocation(e.getX(), e.getY());
            if (lastPath != null) {
                tree.setSelectionPath(lastPath);

                clickedComponent = lastPath.getLastPathComponent();
                EditorClickMenu menu = new EditorClickMenu(clickedComponent, new MenuListener());

                if(clickedComponent instanceof DefaultMutableTreeNode){
                    menu.showRootMenu(e);
                }
                else if(clickedComponent instanceof IGlobalProperty){
                    menu.showPropertyMenu(e);
                }
                else{
                    menu.showValueMenu(e);
                }
            }
        }
    }

    /**
     * Listens for actions in EditorClickMenu
     */
    public class MenuListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if(command.equals("INSERT_PROPERTY")){
                treeModel.addProperty("New property");
            }
            if(command.equals("INSERT_VALUE")){
                treeModel.addValue(clickedComponent, "new value");
            }
            if(command.equals("REMOVE_PROPERTY")){
                treeModel.removeProperty(clickedComponent);
            }
            if(command.equals("RENAME_PROPERTY")){
                tree.scrollPathToVisible(lastPath);
                tree.startEditingAtPath(lastPath);
            }
            if(command.equals("REMOVE_VALUE")){
                TreePath pathOfValue = tree.getSelectionPath();
                Object property = pathOfValue.getPathComponent(pathOfValue.getPathCount()-2);
                treeModel.removeValue(property, clickedComponent);
            }
            if(command.equals("RENAME_VALUE")){
                tree.scrollPathToVisible(lastPath);
                tree.startEditingAtPath(lastPath);
            }
        }

    }

}

