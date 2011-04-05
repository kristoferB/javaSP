package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class SopNode implements ISopNode{

    private SopNodeInfoPointer mInfo = new SopNodeInfoPointer();
    private Set<ISopNode> mSequenceSet = new HashSet<ISopNode>();
    private SopNode mPredecessor = null;
    private SopNode mSuccessor = null;

    public SopNode() {
    }

    @Override
    public SopNodeInfoPointer getNodeInfo() {
        return mInfo;
    }

    @Override
    public Set<ISopNode> getSequencesAsSet() {
        return mSequenceSet;
    }

    @Override
    public SopNode getPredecessorNode() {
        return mPredecessor;
    }

    @Override
    public SopNode getSuccessorNode() {
        return mSuccessor;
    }

    public SopNode createNode() {
        SopNode node = new SopNode();
        mSequenceSet.add(node);

        return node;
    }

    @Override
    public ISopNode createNode(String iType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean insertNode(ISopNode iNode, Object iWhere) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean resolve() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean toSelfContainedOperations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SopNode addNode(final OperationData iOperationData) {
        SopNode node = createNode();
        node.mInfo.setmNodeType(OPERATION);
        node.mInfo.setmOperationData(iOperationData);

        return node;
    }

    @Override
    public SopNode addNode(final OpNode iOpNode) {
        SopNode node = createNode();
        node.mInfo.setmNodeType(OPERATION);
        node.mInfo.setmOpNode(iOpNode);

        return node;
    }

    @Override
    public boolean containsAllOperations(final ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
