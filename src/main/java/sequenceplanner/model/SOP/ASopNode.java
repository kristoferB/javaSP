package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;
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
    private int mSuccessorRelation = -1;

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
    public int getSuccessorRelation() {
        return mSuccessorRelation;
    }

    @Override
    public void setSuccessorRelation(int iRelation) {
        if(iRelation == IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12 ||
                iRelation == IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12) {
            this.mSuccessorRelation = iRelation;
        }
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
        return inDepthToString("");
    }

    private String toString(final String iNewLinePrefix) {
        String returnString = "";
        //-----------------------------------------------------------------------
        returnString += iNewLinePrefix + "Node type: ";
        if (getNodeType() != null) {
            returnString += typeToString();
        } else {
            returnString += null;
        }
        returnString += "\n";
        //-----------------------------------------------------------------------
        if (!getFirstNodesInSequencesAsSet().isEmpty()) {
            returnString += iNewLinePrefix + "Sequence set: {";
            for (final ISopNode node : getFirstNodesInSequencesAsSet()) {
                if (!returnString.endsWith("{")) {
                    returnString += ",";
                }
                if (node != null) {
                    returnString += node.typeToString();
                } else {
                    return returnString + "\n" + typeToString() + " contains null child...";
                }
            }
            returnString += "}\n";
        }
        //-----------------------------------------------------------------------
        if (getPredecessorNode() != null) {
            returnString += iNewLinePrefix + "Predecessor: ";
            returnString += getPredecessorNode().typeToString();
            returnString += "\n";
        }
        //-----------------------------------------------------------------------
        if (getSuccessorNode() != null) {
            returnString += iNewLinePrefix + "Successor: ";
            returnString += RelateTwoOperations.relationIntegerToString(getSuccessorRelation(), "", " ");
            returnString += getSuccessorNode().typeToString();
            returnString += "\n";
        }
        //-----------------------------------------------------------------------
        return returnString;
    }

    @Override
    public String inDepthToString() {
        String returnString = "";
        returnString += toString();
        for (ISopNode node : getFirstNodesInSequencesAsSet()) {
            while (node != null) {
                returnString += node.inDepthToString();
                node = node.getSuccessorNode();
            }
        }
        return returnString;
    }

    @Override
    public String inDepthToString(final String iPrefix) {
        String returnString = "";
        returnString += toString(iPrefix);
        for (ISopNode node : getFirstNodesInSequencesAsSet()) {
            while (node != null) {
                returnString += node.inDepthToString(iPrefix + "..");
                node = node.getSuccessorNode();
            }
        }
        return returnString;
    }
}
