package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * Class to perform hierarchical partition.<br/>
 * Partitions a set of operations according to <b>strict hierarchical relation</b>.<br/>
 * The {@link ISopNode} in the {@link SopNodeWithRelations} object has to have all operations as first nodes in its set.
 * @author patrik
 */
public class HierarchicalPartition1 {

    private SopNodeWithRelations mSNWR = null;
//    private IRelationContainer mRC = null;
    private SopNodeToolboxSetOfOperations mToolbox = new SopNodeToolboxSetOfOperations();
    private RelationContainerToolbox mRCToolbox = new RelationContainerToolbox();


    public HierarchicalPartition1(IRelationContainer iRC) {
//        setmRC(mRC);
        partition(iRC);
    }

    public void partition(IRelationContainer iRC) {
        final ISopNode root = iRC.getOsubsetSopNode();
        final Set<OperationData> allOpSet = new SopNodeToolboxSetOfOperations().getOperations(root);
//        final Set<IROperation> allOpSet = iSNWR.getOpSetFromSopNode(root);
        if (root == null || allOpSet == null) {
            return;
        }
        
        //find operations with no parents----------------------------------------
        Map<OperationData, Set<OperationData>> hasNoParentWithChildrenMap = new HashMap<OperationData, Set<OperationData>>();
        for (final OperationData op : allOpSet) {
            if(!mRCToolbox.hasRelation(op, iRC, RelateTwoOperations.HIERARCHY_21)) {
                //->op has no parent in set
                hasNoParentWithChildrenMap.put(op, new HashSet<OperationData>());
            }
        }
        //-----------------------------------------------------------------------

        //add children to operations without parents (children are candidates at this stage)
        for (final OperationData childCandidateOp : allOpSet) {
            for (final OperationData parentCandidateOp : hasNoParentWithChildrenMap.keySet()) {
                if (mRCToolbox.getRelation(parentCandidateOp, childCandidateOp, iRC) == RelateTwoOperations.HIERARCHY_12) {
                    hasNoParentWithChildrenMap.get(parentCandidateOp).add(childCandidateOp);
                }
            }
        }
        //-----------------------------------------------------------------------

        //remove child parent pairs without a strict hierarchial relation--------
        //-> children that remain are not candidates anymore, they are true children
        for (final OperationData parentOp : hasNoParentWithChildrenMap.keySet()) {
            final Set<OperationData> childSet = new HashSet<OperationData>(hasNoParentWithChildrenMap.get(parentOp));
            for (final OperationData childOp : childSet) {
                Set<OperationData> subset = new HashSet<OperationData>(allOpSet);
                for (final OperationData localParentOp : hasNoParentWithChildrenMap.keySet()) { //Remove all parents to child
                    if (mRCToolbox.getRelation(localParentOp, childOp, iRC) == IRelateTwoOperations.HIERARCHY_12) {
                        subset.remove(localParentOp);
                    }
                }
                subset.removeAll(hasNoParentWithChildrenMap.get(parentOp)); //Remove all children that have parentOp as parent
                if (!hasStrictHierarchicalRelation(parentOp, childOp, subset, iRC)) {
                    hasNoParentWithChildrenMap.get(parentOp).remove(childOp);
                } else {
                    updateHierarchicalRelation(parentOp, root, childOp, iRC);
                }
            }
        }
        //-----------------------------------------------------------------------

        //Recusive calls---------------------------------------------------------
        for (final OperationData parentOp : hasNoParentWithChildrenMap.keySet()) {
            final ISopNode parentNode = mRCToolbox.getSopNode(parentOp, iRC);
            final Set<OperationData> childSet = hasNoParentWithChildrenMap.get(parentOp);
            if (!childSet.isEmpty()) {

                SopNodeWithRelations newSNWR = new SopNodeWithRelations(parentNode, childSet);
                partition(newSNWR);
            }
        }
        //-----------------------------------------------------------------------

        return;
    }

    private boolean updateHierarchicalRelation(final OperationData iNewParent, final ISopNode iOldParent, final OperationData iChild, final IRelationContainer iRC) {
        final ISopNode childNode = mRCToolbox.getSopNode(iChild, iRC);
        mToolbox.removeNode(childNode, iOldParent);

        final ISopNode parentNode = mRCToolbox.getSopNode(iNewParent, iRC);
        parentNode.addNodeToSequenceSet(childNode);
        
        return true;
    }

    public SopNodeWithRelations getmSNWR() {
        return mSNWR;
    }

    public void setmSNWR(SopNodeWithRelations mSNWR) {
        this.mSNWR = mSNWR;
    }

//    public IRelationContainer getmRC() {
//        return mRC;
//    }
//
//    public void setmRC(IRelationContainer mRC) {
//        this.mRC = mRC;
//    }

    private boolean hasStrictHierarchicalRelation(final OperationData iParent, final OperationData iChild, final Set<OperationData> iSet, final IRelationContainer iRC) {
        for (final OperationData op : iSet) {
            final Integer parentRelation = mRCToolbox.getRelation(iParent, op, iRC);
            final Integer childRelation = mRCToolbox.getRelation(iChild, op, iRC);
            if (parentRelation < IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12 || parentRelation > IRelateTwoOperations.OTHER ||
                    childRelation < IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12 || childRelation > IRelateTwoOperations.OTHER) {
                return false;
            }
            if (!parentRelation.toString().equals(childRelation.toString())) {
                return false;
            }
        }
        return true;
    }
}
