package sequenceplanner.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Model for the global properties which are presented in a JTree
 *
 * @author Evelina
 */
public class EditorTreeModel implements TreeModel{

    private DefaultMutableTreeNode root;
    private LinkedList<IGlobalProperty> globalProperties = new LinkedList();
    private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    public EditorTreeModel(){
        root = new DefaultMutableTreeNode("Global properties");
    }

    /**
     * Creates and adds a new property to the model
     *
     * @param name the name of the new property
     */
    public TreePath addProperty(String name){
        GlobalProperty newProperty = new GlobalProperty(name);
        globalProperties.add(newProperty);

        Object[] path = {root};
        int[] childIndex = {globalProperties.indexOf(newProperty)};
        Object[] child = {newProperty};
        TreeModelEvent e = new TreeModelEvent(this, path, childIndex, child);
        fireTreeNodesInserted(e);

        Object[] newPath = {root,newProperty};
        TreePath treePath = new TreePath(newPath);
        return treePath;

    }

    /**
     * Removes specified property from the model
     *
     * @param property the property to be removed
     */
    public void removeProperty(Object property){
        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            int oldIndex = globalProperties.indexOf(gp);
            globalProperties.remove(gp);

            Object[] path = {root};
            int[] childIndex = {oldIndex};
            Object[] child = {gp};
            TreeModelEvent e = new TreeModelEvent(this, path, childIndex, child);
            fireTreeNodesRemoved(e);
        }
    }

    /**
     * Adds a new value to a property
     *
     * @param property the parent of the new value
     * @param newValue the value to be added
     */
    public TreePath addValue(Object property, String newValue){

        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            Value v = new Value(newValue);
            gp.addValue(v);
        
            Object[] path = {root,gp};
            int[] childIndex = {gp.indexOfValue(v)};
            Object[] child = {v};
            TreeModelEvent e = new TreeModelEvent(this, path, childIndex, child);
            fireTreeNodesInserted(e);
            Object[] newPath = {root,gp,v};
            TreePath treePath = new TreePath(newPath);
            return treePath;
        
        }
        return null;
    }

    /**
     * Removes specified value from the model
     *
     * @param property the parent of the value
     * @param value the value to be removed
     */
    public void removeValue(Object property, Object value){
        if(property instanceof IGlobalProperty){
            IGlobalProperty gp = (IGlobalProperty) property;
            if(value instanceof Value){
                Value v = (Value) value;
                int oldIndex = gp.indexOfValue(v);
                gp.removeValue(oldIndex);

                Object[] path = {root,gp};
                int[] childIndex = {oldIndex};
                Object[] child = {v};
                TreeModelEvent e = new TreeModelEvent(this, path, childIndex, child);
                fireTreeNodesRemoved(e);
            }
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
        return null;
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

    public LinkedList<IGlobalProperty> getAllProperties(){
        return globalProperties;
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

            Object[] eventPath = {root};
            int[] childIndex = {globalProperties.indexOf(gp)};
            Object[] child = {gp};
            TreeModelEvent e = new TreeModelEvent(this, eventPath, childIndex, child);
            fireTreeNodesChanged(e);
        }
        else if(o instanceof Value){
            Value v = (Value) o;
            Object parent = path.getPathComponent(path.getPathCount()-2);
            if(parent instanceof IGlobalProperty){
                IGlobalProperty gp = (GlobalProperty) parent;
                int index = getIndexOfChild(parent, o);
                v.setName((String) newValue);

                Object[] eventPath = {root,gp};
                int[] childIndex = {index};
                Object[] child = {v};
                TreeModelEvent e = new TreeModelEvent(this, eventPath, childIndex, child);
                fireTreeNodesChanged(e);
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
            if(child instanceof Value){
                Value value = (Value) child;
                return gp.indexOfValue(value);
            }

        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener list) {
        listeners.add(list);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener list) {
        listeners.remove(list);
    }

    /**
     * Notifies all listeners that node has been inserted
     *
     * @param e the TreeModelEvent
     */
    public void fireTreeNodesInserted( TreeModelEvent e ){
        for (TreeModelListener treeModelListener : listeners) {
            treeModelListener.treeNodesInserted(e);
        }
    }
    /**
     * Notifies all listeners that node has been changed
     *
     * @param e the TreeModelEvent
     */
    public void fireTreeNodesChanged( TreeModelEvent e ){
        for (TreeModelListener treeModelListener : listeners) {
            treeModelListener.treeNodesChanged(e);
        }
    }
    /**
     * Notifies all listeners that node has been removed
     *
     * @param e the TreeModelEvent
     */
    public void fireTreeNodesRemoved( TreeModelEvent e ) {
        for (TreeModelListener treeModelListener : listeners) {
            treeModelListener.treeNodesRemoved(e);
        }
    }
    /**
     * Notifies all listeners that tree structure has changed
     *
     * @param e the TreeModelEvent
     */
    public void fireTreeStructureChanged( TreeModelEvent e ){
        for (TreeModelListener treeModelListener : listeners) {
            treeModelListener.treeStructureChanged(e);
        }
    }

}
