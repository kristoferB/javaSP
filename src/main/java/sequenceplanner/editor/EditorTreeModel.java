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
    private LinkedList<IGlobalProperty> globalProperties = new LinkedList();
    private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    EditorTreeModel(){
        root = new DefaultMutableTreeNode("Global properties");

        String[] values = {"red", "green","blue"};
        IGlobalProperty gp = new GlobalProperty("Colour", values);
        globalProperties.add(gp);
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
        Object o = path.getLastPathComponent();

        if(o instanceof DefaultMutableTreeNode){
            DefaultMutableTreeNode r = (DefaultMutableTreeNode) o;
            r.setUserObject(newValue);
        }
        else if(o instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) o;
            String nv = (String) newValue;
            gp.setName(nv);
        }
        else if(o instanceof String){
            Object parent = path.getPathComponent(path.getPathCount()-2);
            System.out.println(parent.toString());
            if(parent instanceof IGlobalProperty){
                IGlobalProperty gp = (GlobalProperty) parent;
                int index = getIndexOfChild(parent, o);
                String nv = (String) newValue;
                gp.setValue(index, nv);
                System.out.println("new value in editortreemodel: " + globalProperties.get(0).getValue(index));
            }
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
