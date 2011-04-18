package sequenceplanner.algorithms.visualization;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.OperationData;

/**
 * To store an {@link ISopNode} as root <br/>
 * and the relations among its operations ({@link OperationData}) as a set of {@link IROperation}.<br/>
 * @author patrik
 */
public class SopNodeWithRelations {

    private ISopNode mRootSop = null;
    private Set<IROperation> mRelationSet = null;
    private Set<ISopNode> mSopSet = null;

    public SopNodeWithRelations(final ISopNode iRootSop, final Set<IROperation> iSet) {
        setmRootSop(iRootSop);
        setmRelationSet(iSet);

        initmSopSet();
    }

    public SopNodeWithRelations(final Set<ISopNode> iSopSet, final Set<IROperation> iOpSet) {
        setmRelationSet(iOpSet);
        setmSopSet(iSopSet);
    }

    public Set<IROperation> getmRelationSet() {
        return mRelationSet;
    }

    public void setmRelationSet(Set<IROperation> mRelationSet) {
        this.mRelationSet = mRelationSet;
    }

    public ISopNode getmRootSop() {
        return mRootSop;
    }

    public void setmRootSop(ISopNode mRootSop) {
        this.mRootSop = mRootSop;
    }

    private void initmSopSet() {
        setmSopSet(new HashSet<ISopNode>());
        for (final ISopNode node : getmRootSop().getFirstNodesInSequencesAsSet()) {
            getmSopSet().add(node);
        }
    }

    public Set<ISopNode> getmSopSet() {
        return mSopSet;
    }

    public void setmSopSet(Set<ISopNode> mSopSet) {
        this.mSopSet = mSopSet;
    }

    /**
     * To get the corresponing set of {@link IROperation}s to the children of an {@link ISopNode}.<br/>
     * The returned set is a subset of the {@link IROperaiton} set previously added to this object.<br/>
     * @param iRootNode root to children
     * @return set of {@link IROperation}s to children
     */
    public Set<IROperation> getOpSetFromSopNode(final ISopNode iRootNode) {
        Set<IROperation> returnSet = new HashSet<IROperation>();
        Set<ISopNode> setToLookIn = new HashSet<ISopNode>(iRootNode.getFirstNodesInSequencesAsSet());

        if (getmRelationSet() == null || setToLookIn == null) {
            return null;
        }

        //Get operations---------------------------------------------------------
        Set<OperationData> operations = new HashSet<OperationData>();
        for (final ISopNode node : iRootNode.getFirstNodesInSequencesAsSet()) {
            if (node.getNodeType() instanceof OperationData) {
                final OperationData opData = (OperationData) node.getNodeType();
                operations.add(opData);
            }
        }
        //-----------------------------------------------------------------------

        for (final IROperation op : getmRelationSet()) {
            final OperationData opData = op.getSelfContainedOperation();
            if (operations.contains(opData)) {
                returnSet.add(op);
                operations.remove(opData); //This operation will not show up again
            }
//            final ISopNode opNode = op.getNode();
//            if (setToLookIn.contains(opNode)) {
//                returnSet.add(op);
//                setToLookIn.remove(opNode); //This node will not show up again
//            }
        }

        return returnSet;
    }

    /**
     * Get {@link ISopNode} for an {@link IROperation}.<br/>
     * The parameter has to been in the set of {@link IROperation}s given for this object.<br/>
     * @param iOperation the operation to get the {@link ISopNode} for
     * @return the {@link ISopNode} if found, else null
     */
    public ISopNode getSopNode(final IROperation iOperation) {

        if (!getmRelationSet().contains(iOperation)) {
            return null;
        }

        for (final ISopNode node : getmSopSet()) {
            if (node.getNodeType() instanceof OperationData) {
                final OperationData nodeOpData = (OperationData) node.getNodeType();
                if (nodeOpData == iOperation.getSelfContainedOperation()) {
                    return node;
                }
            }
        }
        return null;
    }
}
