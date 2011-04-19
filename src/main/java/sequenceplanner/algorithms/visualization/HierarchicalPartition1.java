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

    private IRelationContainer mRC = null;
    private SopNodeToolboxSetOfOperations mSNToolbox = new SopNodeToolboxSetOfOperations();
    private RelationContainerToolbox mRCToolbox = new RelationContainerToolbox();

    public HierarchicalPartition1(IRelationContainer iRC) {
        setmRC(iRC);
        mRC.setRootNode(mRC.getOsubsetSopNode());

        
        if(partition(iRC)) {
//            System.out.println("HierarchicalPartition1 klar");
//            System.out.println(iRC.getOsubsetSopNode().inDepthToString());
//            System.out.println("HierarchicalPartition1 klar");
//            iRC.setRootNode(iRC.getOsubsetSopNode());
//            System.out.println(iRC.getRootNode().inDepthToString());
//            System.out.println("HierarchicalPartition1 klar");
        }
    }

    public boolean partition(IRelationContainer iRC) {
        final ISopNode root = mRC.getRootNode();
        final Set<OperationData> allOpSet = new SopNodeToolboxSetOfOperations().getOperations(root);
//        final Set<IROperation> allOpSet = iSNWR.getOpSetFromSopNode(root);
        if (root == null || allOpSet == null) {
            System.out.println("HierarchicalPartition.partition: root or allOpSet is null");
            return false;
        }

        System.out.println("pointers: " + mRC.getOsubsetSopNode().equals(root));
        System.out.println("contains: " + mRC.getOsubsetSopNode().getFirstNodesInSequencesAsSet().contains(root));

        //find operations with no parents----------------------------------------
        Map<OperationData, Set<OperationData>> hasNoParentWithChildrenMap = new HashMap<OperationData, Set<OperationData>>();
        for (final OperationData op : allOpSet) {
            if (!mRCToolbox.hasRelation(op, mRC, RelateTwoOperations.HIERARCHY_21)) {
                //->op has no parent in set
                hasNoParentWithChildrenMap.put(op, new HashSet<OperationData>());
            }
        }
        //-----------------------------------------------------------------------

        //add children to operations without parents (children are candidates at this stage)
        for (final OperationData childCandidateOp : allOpSet) {
            for (final OperationData parentCandidateOp : hasNoParentWithChildrenMap.keySet()) {
                if (mRCToolbox.getRelation(parentCandidateOp, childCandidateOp, mRC) == RelateTwoOperations.HIERARCHY_12) {
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
                    if (mRCToolbox.getRelation(localParentOp, childOp, mRC) == IRelateTwoOperations.HIERARCHY_12) {
                        subset.remove(localParentOp);
                    }
                }
                subset.removeAll(hasNoParentWithChildrenMap.get(parentOp)); //Remove all children that have parentOp as parent
                if (!hasStrictHierarchicalRelation(parentOp, childOp, subset, mRC)) {
                    hasNoParentWithChildrenMap.get(parentOp).remove(childOp);
                } else {
                    updateHierarchicalRelation(parentOp, root, childOp, mRC);
                }
            }
        }
        //-----------------------------------------------------------------------

        //Recursive calls--------------------------------------------------------
        for (final OperationData parentOp : hasNoParentWithChildrenMap.keySet()) {
            final ISopNode parentNode = mRCToolbox.getSopNode(parentOp, mRC);
            final Set<OperationData> childSet = hasNoParentWithChildrenMap.get(parentOp);
            if (!childSet.isEmpty()) {

                mRC.setRootNode(parentNode);
                System.out.println("Recursive calls " + mRC.getRootNode().toString());
                partition(iRC);
            }
        }
        //-----------------------------------------------------------------------

        return true;
    }

    private boolean updateHierarchicalRelation(final OperationData iNewParent, final ISopNode iOldParent, final OperationData iChild, final IRelationContainer iRC) {
        final ISopNode childNode = mRCToolbox.getSopNode(iChild, mRC);
        mSNToolbox.removeNode(childNode, iOldParent);

        final ISopNode parentNode = mRCToolbox.getSopNode(iNewParent, mRC);
        parentNode.addNodeToSequenceSet(childNode);

        System.out.println("updateHierarchicalRelation");
        System.out.println("child: " + iChild.getName());
        System.out.println("child node: " + childNode.toString());
        System.out.println("oldparent node: " + iOldParent.toString());
        System.out.println("newparent: " + iNewParent.getName());
        System.out.println("newparent node: " + parentNode.toString());

        return true;
    }

    public IRelationContainer getmRC() {
        return mRC;
    }

    public void setmRC(IRelationContainer mRC) {
        this.mRC = mRC;
    }
    private boolean hasStrictHierarchicalRelation(final OperationData iParent, final OperationData iChild, final Set<OperationData> iSet, final IRelationContainer iRC) {
        for (final OperationData op : iSet) {
            final Integer parentRelation = mRCToolbox.getRelation(iParent, op, mRC);
            final Integer childRelation = mRCToolbox.getRelation(iChild, op, mRC);
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
