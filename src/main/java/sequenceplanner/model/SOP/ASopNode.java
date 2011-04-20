package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class ASopNode implements ISopNode {

    private Object mType = null;
    /**
     * Set containing the first ISopNode in all sequences that are children to this node.<br/>
     */
    private Set<ISopNode> mSequenceSet = null;
    private ISopNode mPredecessor = null;
    private ISopNode mSuccessor = null;

    public ASopNode() {
        mSequenceSet = new HashSet<ISopNode>();
    }

    @Override
    public Set<ISopNode> getFirstNodesInSequencesAsSet() {
        return mSequenceSet;
    }

    @Override
    public Object getNodeType() {
        return mType;
    }

    @Override
    public ISopNode getPredecessorNode() {
        return mPredecessor;
    }

    @Override
    public ISopNode getSuccessorNode() {
        return mSuccessor;
    }

    @Override
    public void addNodeToSequenceSet(ISopNode iNode) {
        mSequenceSet.add(iNode);
    }

    @Override
    public void setNodeType(Object iType) {
        mType = iType;
    }

    @Override
    public void setPredecessorNode(ISopNode iPredecessor) {
        mPredecessor = iPredecessor;
    }

    @Override
    public void setSuccessorNode(ISopNode iSuccessor) {
        mSuccessor = iSuccessor;
    }

    @Override
    public String typeToString() {
        String returnString = "";
        if (getNodeType() instanceof OperationData) {
            OperationData opData = (OperationData) getNodeType();
            returnString += opData.getName();
        } else if (getNodeType() instanceof String) {
            String s = (String) getNodeType();
            returnString += s;
        } else {
            returnString += null;
        }
        return returnString;
    }

    @Override
    public String toString() {
        String returnString = "";
        //-----------------------------------------------------------------------
        returnString += "Node type: ";
        if (getNodeType() != null) {
            returnString += typeToString();
        } else {
            returnString += null;
        }
        returnString += "\n";
        //-----------------------------------------------------------------------
        returnString += "Sequence set: {";
        for (final ISopNode node : getFirstNodesInSequencesAsSet()) {
            if (!returnString.endsWith("{")) {
                returnString += ",";
            }
            if(node != null) {
            returnString += node.typeToString();
            } else {
                return returnString + "\n" + typeToString() + " contains null child...";
            }
        }
        returnString += "}\n";
        //-----------------------------------------------------------------------
        returnString += "Predecessor: ";
        if (getPredecessorNode() != null) {
            returnString += getPredecessorNode().typeToString();
        } else {
            returnString += null;
        }
        returnString += "\n";
        //-----------------------------------------------------------------------
        returnString += "Successor: ";
        if (getSuccessorNode() != null) {
            returnString += getSuccessorNode().typeToString();
        } else {
            returnString += null;
        }
        returnString += "\n";
        //-----------------------------------------------------------------------
        return returnString;
    }

    @Override
    public String inDepthToString() {

        if (getFirstNodesInSequencesAsSet().isEmpty()) {
            return typeToString() + " is sink \n";
        }

        String returnString = "";
        returnString += toString();
        for (ISopNode node : getFirstNodesInSequencesAsSet()) {
            returnString += node.inDepthToString();
        }
        return returnString;
    }
}
