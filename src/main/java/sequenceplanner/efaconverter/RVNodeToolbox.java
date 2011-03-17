package sequenceplanner.efaconverter;

/**
 * Methods to enhance a tree built up from {@link RVNode}s.
 * @author patrik
 */
public class RVNodeToolbox {
    final static int OPERATION = 0;

    final RVNode mRoot = new RVNode();

    public RVNodeToolbox() {
    }

    public RVNode addOperation(OpNode iOpNode) {
        //Create new node
        RVNode newNode = new RVNode(iOpNode);
        //set parent
        newNode.mParent = mRoot;
        //set node type;
        newNode.nodeType = OPERATION;
        //set child relation
        mRoot.mChildren.add(newNode);
        
        return newNode;
    }
}
