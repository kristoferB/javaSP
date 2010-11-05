
package sequenceplanner.efaconverter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sequenceplanner.model.TreeNode;

/**
 *
 * @author kbe
 */
public class VarNode {

    private final String name;
    private final TreeNode variable;
    private Set<OpNode> setters;
    private Set<OpNode> getters;
    private boolean hide = false;

    /**
     * Creates the VarNode object
     * @param name The name of the Variable
     * @param variable The ThreeNode object representing the variable from the model
     */
    public VarNode(String name, TreeNode variable) {
        this.variable = variable;
        this.name = name;
        this.setters = new HashSet<OpNode>();
        this.getters = new HashSet<OpNode>();
    }

    /**
     * 
     * @return The name of the variable
     */
    public String getName(){
        return this.name;
    }

    public TreeNode getTreeNode(){
        return variable;
    }

    public void addGetterOperation(OpNode op){
        getters.add(op);
    }

    public void addSetterOperation(OpNode op){
        setters.add(op);
    }

    public Set<OpNode> getGetters(){
        return getters;
    }

    public Set<OpNode> getSetters(){
        return setters;
    }

    public boolean isHidden() {
        return hide;
    }

    public void setHidden(boolean hide) {
        this.hide = hide;
    }

    @Override
    public String toString(){
        String s = hide ? ('L' + name + ':') : (name + ':');
        String ops = toStringOp(this.getGetters());
        s = ops.equals("")
                ? s
                : s + "g" + ops;
        ops = toStringOp(this.getSetters());
        s = ops.equals("")
                ? s
                : s + " s" + ops;

        return s;
    }

    private String toStringOp(Collection<OpNode> c){
        String s = new String();
        Iterator<OpNode> iGet = c.iterator();
        if (iGet.hasNext()){
            s = "{";
        }
        while(iGet.hasNext()){
            OpNode n = iGet.next();
            s = iGet.hasNext()
                    ? s + n.getName() + ','
                    : s + n.getName() + '}';
        }
        return s;
    }



}
