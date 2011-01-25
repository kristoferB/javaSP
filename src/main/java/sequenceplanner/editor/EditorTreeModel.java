/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Evelina
 */
public class EditorTreeModel implements TreeModel{

    private DefaultMutableTreeNode root;
    private LinkedList<GlobalProperty> globalProperties = new LinkedList();
    private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    EditorTreeModel(){
        root = new DefaultMutableTreeNode("Global properties");

        String[] values = {"red", "green","blue"};
        GlobalProperty gp = new GlobalProperty("Colour", values);
        globalProperties.add(gp);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if(parent.equals(root)){
            return((Object) globalProperties.get(index).getName());
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
        System.out.println("Path changed");
        Object o = path.getLastPathComponent();

        if(o instanceof DefaultMutableTreeNode){
            DefaultMutableTreeNode r = (DefaultMutableTreeNode) o;
            r.setUserObject(newValue);
        }

//This does not work
        else if(o instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) o;
            gp.setName((String) newValue);
        }
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
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }



}
