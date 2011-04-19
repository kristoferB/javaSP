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
public class HierarchicalPartition {

    private SopNodeToolboxSetOfOperations mSNToolbox = new SopNodeToolboxSetOfOperations();
    private RelationContainerToolbox mRCToolbox = new RelationContainerToolbox();

    public HierarchicalPartition(IRelationContainer iRC) {
        iRC.setRootNode(iRC.getOsubsetSopNode());
        partition(iRC);
    }

    public boolean partition(IRelationContainer iRC) {
        final ISopNode root = iRC.getRootNode();
        final Set<OperationData> allOpSet = mSNToolbox.getOperations(root);
        if (root == null || allOpSet == null) {
            System.out.println("HierarchicalPartition.partition: root or allOpSet is null");
            return false;
        }

        //find operations with no parents----------------------------------------
        Map<OperationData, Set<OperationData>> hasNoParentWithChildrenMap = new HashMap<OperationData, Set<OperationData>>();
        for (final OperationData op : allOpSet) {
            if (!mRCToolbox.hasRelation(op, iRC, RelateTwoOperations.HIERARCHY_21)) {
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

        //Recursive calls--------------------------------------------------------
        for (final OperationData parentOp : hasNoParentWithChildrenMap.keySet()) {
            final ISopNode parentNode = mRCToolbox.getSopNode(parentOp, iRC.getOsubsetSopNode());
            final Set<OperationData> childSet = hasNoParentWithChildrenMap.get(parentOp);
            if (!childSet.isEmpty()) {
                iRC.setRootNode(parentNode);
                partition(iRC);
            }
        }
        //-----------------------------------------------------------------------

        return true;
    }

    private boolean updateHierarchicalRelation(final OperationData iNewParent, final ISopNode iOldParent, final OperationData iChild, final IRelationContainer iRC) {
        final ISopNode childNode = mRCToolbox.getSopNode(iChild, iOldParent);
        mSNToolbox.removeNode(childNode, iOldParent);

        final ISopNode parentNode = mRCToolbox.getSopNode(iNewParent, iOldParent);
        mSNToolbox.createNode(iChild, parentNode);

        return true;
    }

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
