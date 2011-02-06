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

        LinkedList<String> values = new LinkedList();
        values.addLast("red");
        values.addLast("green");
        values.addLast("blue");
        
        IGlobalProperty gp = new GlobalProperty("Colour", values);
        globalProperties.add(gp);
    }

    public void addProperty(String name){
        globalProperties.add(new GlobalProperty(name));

    }

    public void removeProperty(Object property){
        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            globalProperties.remove(gp);
        }
    }

    public void renameProperty(Object property, String name){
        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            gp.setName(name);
        }

    }

    public void addValue(Object property, String newValue){

        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            gp.addValue(newValue);
        }
    }

    public void removeValue(Object property, Object value){
        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            int index = gp.indexOfValue(value);
            gp.removeValue(index);
        }
    }

    public void renameValue(Object property, Object value, String name){
        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            int index = gp.indexOfValue(value);
            gp.setValue(index, value);
        }
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

        if(o instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) o;
            String nv = (String) newValue;
            gp.setName(nv);
        }
        else if(o instanceof String){
            Object parent = path.getPathComponent(path.getPathCount()-2);
            if(parent instanceof IGlobalProperty){
                IGlobalProperty gp = (GlobalProperty) parent;
                int index = getIndexOfChild(parent, o);
                String nv = (String) newValue;
                gp.setValue(index, nv);
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
    public void addTreeModelListener(TreeModelListener list) {
        listeners.add(list);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener list) {
        listeners.remove(list);
    }
/*
    protected void fireTreeModel(TreeModelEvent event) {
        TreeModelListener[] list = listeners.toArray();

        for (int i = 0; i < list.length; i ++) {
            if (list[i] instanceof EditorTreeModelListener) {
                EditorTreeModelListener treeModelListener = (EditorTreeModelListener) list[i];

                (treeModelListener.treeNodesChanged(new TreeModelEvent()));
          }

}
*/

}
