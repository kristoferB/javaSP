package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.visualization.algorithms.RelateTwoOperations;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class ASopNode implements SopNode {

    private String mTypeAsString = "";
    /**
     * Counter for unique ids
     */
    public static int mUniqueIdCounter = 1;
    private int mUniqueid;
    /**
     * Set containing the first SopNode in all sequences that are children to this node.<br/>
     */
    private Set<SopNode> mSequenceSet = null;
    private SopNode mSuccessor = null;
    private int mSuccessorRelation = -1;

    public ASopNode(final String iTypeAsString) {
        mSequenceSet = new HashSet<SopNode>();
        this.mTypeAsString = iTypeAsString;
        this.mUniqueid = mUniqueIdCounter++;
    }

    @Override
    public int getUniquefId() {
        return mUniqueid;
    }

    @Override
    public Set<SopNode> getFirstNodesInSequencesAsSet() {
        return mSequenceSet;
    }

    @Override
    public SopNode getSuccessorNode() {
        return mSuccessor;
    }

    @Override
    public void addNodeToSequenceSet(SopNode iNode) {
        mSequenceSet.add(iNode);
    }

    @Override
    public boolean removeFromSequenceSet(SopNode iNodeToRemove) {
        return getFirstNodesInSequencesAsSet().remove(iNodeToRemove);
    }

    @Override
    public void setSuccessorNode(SopNode iSuccessor) {
        mSuccessor = iSuccessor;
    }

    @Override
    public int getSuccessorRelation() {
        return mSuccessorRelation;
    }

    @Override
    public void setSuccessorRelation(int iRelation) {
        if (iRelation == IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12 ||
                iRelation == IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12) {
            this.mSuccessorRelation = iRelation;
        }
    }

    @Override
    public boolean sequenceSetIsEmpty() {
        if (getFirstNodesInSequencesAsSet().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Is only overwritten by {@link SopNodeOperation}
     * @return
     */
    @Override
    public OperationData getOperation() {
        return null;
    }

    @Override
    public String typeToString() {
        String returnString = "";
        if (this instanceof SopNodeOperation) {
            returnString += getOperation().getName();
        } else if (this instanceof SopNodeEmpty || this instanceof SopNodeAlternative || this instanceof SopNodeArbitrary || this instanceof SopNodeParallel) {
            returnString += mTypeAsString;
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
        returnString += typeToString();
        returnString += "\n";
        //-----------------------------------------------------------------------
        if (!sequenceSetIsEmpty()) {
            returnString += iNewLinePrefix + "Sequence set: {";
            for (final SopNode node : getFirstNodesInSequencesAsSet()) {
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
        for (SopNode node : getFirstNodesInSequencesAsSet()) {
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
        for (SopNode node : getFirstNodesInSequencesAsSet()) {
            while (node != null) {
                returnString += node.inDepthToString(iPrefix + "..");
                node = node.getSuccessorNode();
            }
        }
        return returnString;
    }
}
