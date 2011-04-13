package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;

/**
 *
 * @author patrik
 */
public class HierarchicalPartition {

    private SopNodeWithRelations mSNWR = null;

    public HierarchicalPartition(SopNodeWithRelations iSNWR) {
        setmSNWR(iSNWR);
        partition(getmSNWR());
    }

    public void partition(SopNodeWithRelations iSNWR) {
        final ISopNode root = iSNWR.getmRootSop();
        final Set<IROperation> allOpSet = iSNWR.getmRelationSet();
        if (iSNWR == null) {
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

        //add children (children are candidates at this stage)-------------------
        for (final IROperation childCandidateOp : allOpSet) {
            for (final IROperation parentCandidateOp : hasNoParentWithChildrenMap.keySet()) {
                if (parentCandidateOp.getRelationToIOperation(childCandidateOp) == RelateTwoOperations.HIERARCHY_12) {
                    hasNoParentWithChildrenMap.get(parentCandidateOp).add(childCandidateOp);
                }
            }
        }
        //-----------------------------------------------------------------------

        //remove children without strict hierarchial relations to parents--------
        //-> children that remain are not candidates anymore
        for (final IROperation parentOp : hasNoParentWithChildrenMap.keySet()) {
            Set<IROperation> subset = allOpSet;
            subset.remove(parentOp);
            subset.removeAll(hasNoParentWithChildrenMap.values());
            final Set<IROperation> childSet = new HashSet<IROperation>(hasNoParentWithChildrenMap.get(parentOp));
            for (final IROperation childOp : childSet) {
                if (!hasStrictHierarchicalRelation(parentOp, childOp, subset)) {
                    hasNoParentWithChildrenMap.get(parentOp).remove(childOp);
                    System.out.println("parent: " + parentOp.getIdAsString() + " child: " + childOp.getIdAsString());
                }
            }
        }
        //-----------------------------------------------------------------------

        //Loop children to parents
        for (final IROperation parentOp : hasNoParentWithChildrenMap.keySet()) {
        }

        return;
    }

    public SopNodeWithRelations getmSNWR() {
        return mSNWR;
    }

    public void setmSNWR(SopNodeWithRelations mSNWR) {
        this.mSNWR = mSNWR;
    }

    private boolean hasStrictHierarchicalRelation(final IROperation iParent, final IROperation iChild, final Set<IROperation> iSet) {
        for (final IROperation op : iSet) {
            final Integer parentRelation = iParent.getRelationToIOperation(op);
            final Integer childRelation = iChild.getRelationToIOperation(op);
            if (parentRelation < IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12 || parentRelation > IRelateTwoOperations.OTHER ||
                    childRelation < IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12 || childRelation > IRelateTwoOperations.OTHER) {
                return false;
            }
            if (parentRelation != childRelation) {
                return false;
            }
        }
        return true;
    }
}
