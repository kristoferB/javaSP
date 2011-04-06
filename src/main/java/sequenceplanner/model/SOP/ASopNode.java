package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class ASopNode implements ISopNode {

    String mType = "";
    Set<ISopNode> mSequenceSet = null;
    ISopNode mPredecessor = null;
    ISopNode mSuccessor = null;

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
    public void setNodeType(Object iType) {
        if (iType instanceof OperationData) {
            iType = ((OperationData) iType).getName();
        }
        mType = iType.toString();
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
    public String toString() {
        String returnString = "";
        returnString += "Node type: " + getNodeType().toString() + "\n";
        returnString += "{";
        for (final ISopNode node : mSequenceSet) {
            if (!returnString.endsWith("{")) {
                returnString += ",";
            }
            returnString += node.getNodeType().toString();
        }
        returnString += "}\n";
        returnString += "Predecessor: " + mPredecessor.getNodeType().toString() + "\n";
        returnString += "Successor: " + mSuccessor.getNodeType().toString() + "\n";
        return returnString;
    }
}
