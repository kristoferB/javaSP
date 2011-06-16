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

    private String mTypeAsString = "";

    /**
     * Set containing the first ISopNode in all sequences that are children to this node.<br/>
     */
    private Set<ISopNode> mSequenceSet = null;
    private ISopNode mSuccessor = null;
    private int mSuccessorRelation = -1;

    public ASopNode(final String iTypeAsString) {
        mSequenceSet = new HashSet<ISopNode>();
        this.mTypeAsString = iTypeAsString;
    }

    @Override
    public Set<ISopNode> getFirstNodesInSequencesAsSet() {
        return mSequenceSet;
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
    public boolean sequenceSetIsEmpty() {
        if(getFirstNodesInSequencesAsSet().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public OperationData getOperation() {
        return null;
    }

    @Override
    public String getTypeAsString() {
        return mTypeAsString;
    }



    @Override
    public String typeToString() {
        String returnString = "";
        if (this instanceof SopNodeOperation) {
            returnString += getOperation().getName();
        } else if (this instanceof SopNode || this instanceof SopNodeAlternative || this instanceof SopNodeArbitrary || this instanceof SopNodeParallel) {
            returnString += getTypeAsString();
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
        returnString += getTypeAsString();
//        if (getNodeType() != null) {
//            returnString += typeToString();
//        } else {
//            returnString += null;
//        }
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
