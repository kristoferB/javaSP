/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
 *
 * @author Evelina
 */
public class EditorMouseAdapter extends MouseAdapter{

    private JTree tree;
    private EditorTreeModel treeModel;
    private Object clickedComponent;

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

    private void popup(MouseEvent e) {
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                tree.setSelectionPath(path);

                clickedComponent = path.getLastPathComponent();
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

    public class MenuListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if(command.equals("INSERT_PROPERTY")){
                treeModel.addProperty("New property");
                tree.updateUI();
            }
            if(command.equals("INSERT_VALUE")){
                treeModel.addValue(clickedComponent, "new value");
                tree.updateUI();
            }
            if(command.equals("REMOVE_PROPERTY")){
                treeModel.removeProperty(clickedComponent);
                tree.updateUI();
            }
            if(command.equals("RENAME_PROPERTY")){
                System.out.println("rename property");
// How to enable user to write new name?
//                String name = ?
//                treeModel.renameProperty(clickedComponent, name);
                tree.updateUI();
            }
            if(command.equals("REMOVE_VALUE")){
                TreePath pathOfValue = tree.getSelectionPath();
                Object property = pathOfValue.getPathComponent(pathOfValue.getPathCount()-2);
                treeModel.removeValue(property, clickedComponent);
                tree.updateUI();
            }
            if(command.equals("RENAME_VALUE")){
                System.out.println("rename value");
                TreePath pathOfValue = tree.getSelectionPath();
                Object property = pathOfValue.getPathComponent(pathOfValue.getPathCount()-2);
// How to enable user to write new name?
//                String name = ?
//                treeModel.renameValue(property, clickedComponent, name);
                tree.updateUI();
            }
        }

    }

}

