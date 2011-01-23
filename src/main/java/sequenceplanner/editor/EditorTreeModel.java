/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import java.util.LinkedList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sequenceplanner.editor.GlobalProperty;

/**
 *
 * @author Evelina
 */
public class EditorTreeModel implements TreeModel{

    private DefaultMutableTreeNode root;
    private LinkedList<GlobalProperty> globalProperties = new LinkedList();

    EditorTreeModel(){
        root = new DefaultMutableTreeNode("Global properties");
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if(parent.equals(root)){
            return((Object) globalProperties.get(index));
        }
        if(parent instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) parent;
            return((Object) gp.getValue(index));
        }

//throw exception instead
        else {
            return null;
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if(parent.equals(root)){
            return(globalProperties.size());
        }
        if(parent instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) parent;
            return(gp.getNumberOfValues());
        }
        else {
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        if(node.equals(root) || node instanceof IGlobalProperty){
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {

        if(parent.equals(root)){
            return globalProperties.indexOf(child);
        }
        if(parent instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) parent;
            return gp.indexOfValue(child);

        }
//throw exception instead
        else {
            return -1;
        }

    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
