package sequenceplanner.model;

/**
 *
 * @author Erik Ohlson
 */
public interface ITreeNode {

    /**
     *
     * @param i index of the child you want to get
     * @return child object with index i
     */
    public ITreeNode getChildAt(int i);

    /**
     *
     * @return number of children this node has
     */
    public int getChildCount();

    /**
     *
     * @param child
     * @return index of child, if not present it returns -1
     */
    public int getIndex(ITreeNode child);

    /**
     *
     * @param child insert as child to this node
     */
    public void insert(ITreeNode child);

    /**
     *
     * @param child remove this child if present.
     */
    public void remove(ITreeNode child);

    /**
     *
     * @param parent new parent of this node
     */
    public void setParent(ITreeNode parent);

    /**
     *
     * @return the parent of this node
     */
    public ITreeNode getParent();
}


//~ Formatted by Jindent --- http://www.jindent.com
