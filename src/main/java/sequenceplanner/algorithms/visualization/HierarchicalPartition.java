package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;

/**
 * Class to perform hierarchical partition.<br/>
 * Partitions a set of operations according to <b>strict hierarchical relation</b>.<br/>
 * The {@link ISopNode} in the {@link SopNodeWithRelations} object has to have all operations as first nodes in its set.
 * @author patrik
 */
public class HierarchicalPartition {

    private SopNodeWithRelations mSNWR = null;
    private IRelationContainer mRC = null;
    private SopNodeToolboxSetOfOperations mToolbox = new SopNodeToolboxSetOfOperations();

    public HierarchicalPartition(SopNodeWithRelations iSNWR) {
        setmSNWR(iSNWR);
        partition(getmSNWR());
    }

    public HierarchicalPartition(IRelationContainer iRC) {
        setmRC(mRC);
        partition(mSNWR);
    }

    public void partition(SopNodeWithRelations iSNWR) {
        if (iSNWR == null) {
            return;
        }
        final ISopNode root = iSNWR.getmRootSop();
        final Set<IROperation> allOpSet = iSNWR.getOpSetFromSopNode(root);
        if (root == null || allOpSet == null) {
            return;
        }
        
        //find operations with no parents----------------------------------------
        Map<IROperation, Set<IROperation>> hasNoParentWithChildrenMap = new HashMap<IROperation, Set<IROperation>>();
        for (final IROperation op : allOpSet) {
            if (!op.containsRelation(allOpSet, RelateTwoOperations.HIERARCHY_21)) {
                //->op has no parent in set
                hasNoParentWithChildrenMap.put(op, new HashSet<IROperation>());
            }
        }
        //-----------------------------------------------------------------------

        //add children to operations without parents (children are candidates at this stage)
        for (final IROperation childCandidateOp : allOpSet) {
            for (final IROperation parentCandidateOp : hasNoParentWithChildrenMap.keySet()) {
                if (parentCandidateOp.getRelationToIOperation(childCandidateOp) == RelateTwoOperations.HIERARCHY_12) {
                    hasNoParentWithChildrenMap.get(parentCandidateOp).add(childCandidateOp);
                }
            }
        }
        //-----------------------------------------------------------------------

        //remove child parent pairs without a strict hierarchial relation--------
        //-> children that remain are not candidates anymore, they are true children
        for (final IROperation parentOp : hasNoParentWithChildrenMap.keySet()) {
            final Set<IROperation> childSet = new HashSet<IROperation>(hasNoParentWithChildrenMap.get(parentOp));
            for (final IROperation childOp : childSet) {
                Set<IROperation> subset = new HashSet<IROperation>(allOpSet);
                for (final IROperation localParentOp : hasNoParentWithChildrenMap.keySet()) { //Remove all parents to child
                    if (localParentOp.getRelationToIOperation(childOp).toString().equals(IRelateTwoOperations.HIERARCHY_12.toString())) {
                        subset.remove(localParentOp);
                    }
                }
                subset.removeAll(hasNoParentWithChildrenMap.get(parentOp)); //Remove all children that have parentOp as parent
                if (!hasStrictHierarchicalRelation(parentOp, childOp, subset)) {
                    hasNoParentWithChildrenMap.get(parentOp).remove(childOp);
                } else {
                    updateHierarchicalRelation(parentOp, root, childOp);
                }
            }
        }
        //-----------------------------------------------------------------------

        //Recusive calls---------------------------------------------------------
        for (final IROperation parentOp : hasNoParentWithChildrenMap.keySet()) {
            final ISopNode parentNode = mSNWR.getSopNode(parentOp);
            final Set<IROperation> childSet = hasNoParentWithChildrenMap.get(parentOp);
            if (!childSet.isEmpty()) {
                SopNodeWithRelations newSNWR = new SopNodeWithRelations(parentNode, childSet);
                partition(newSNWR);
            }
        }
        //-----------------------------------------------------------------------

        return;
    }

    private boolean updateHierarchicalRelation(final IROperation iNewParent, final ISopNode iOldParent, final IROperation iChild) {
        final ISopNode childNode = mSNWR.getSopNode(iChild);
        mToolbox.removeNode(childNode, iOldParent);

        final ISopNode parentNode = mSNWR.getSopNode(iNewParent);
        parentNode.addNodeToSequenceSet(childNode);
        
        return true;
    }

    public SopNodeWithRelations getmSNWR() {
        return mSNWR;
    }

    public void setmSNWR(SopNodeWithRelations mSNWR) {
        this.mSNWR = mSNWR;
    }

    public IRelationContainer getmRC() {
        return mRC;
    }

    public void setmRC(IRelationContainer mRC) {
        this.mRC = mRC;
    }

    private boolean hasStrictHierarchicalRelation(final IROperation iParent, final IROperation iChild, final Set<IROperation> iSet) {
        for (final IROperation op : iSet) {
            final Integer parentRelation = iParent.getRelationToIOperation(op);
            final Integer childRelation = iChild.getRelationToIOperation(op);
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
