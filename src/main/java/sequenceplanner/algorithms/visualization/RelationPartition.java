package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;

/**
 *
 * @author patrik
 */
public class RelationPartition {

    private Integer mRelationInt = null;
    private ISopNodeToolbox mToolbox = new SopNodeToolboxSetOfOperations();

    public RelationPartition(final SopNodeWithRelations iSNWR, final Integer iRelationInt) {
        partition(iSNWR, iRelationInt);
    }

    public void partition(final SopNodeWithRelations iSNWR, final Integer iRelationInt) {
        if (iSNWR == null) {
            return;
        }
        mRelationInt = iRelationInt;

        final ISopNode root = iSNWR.getmRootSop();
        final Set<IROperation> operationsOnThisLevel = iSNWR.getOpSetFromSopNode(root);
        if (root == null || operationsOnThisLevel == null) {
            return;
        }

        //Recursive calls to children (root.set)---------------------------------
        //The recusive calls are done in the beginning because new nodes are introduced as children and some nodes are remove in the remainder of this method.
        for (final ISopNode node : root.getFirstNodesInSequencesAsSet()) {
            if (!node.getFirstNodesInSequencesAsSet().isEmpty()) {
                SopNodeWithRelations newSNWR = new SopNodeWithRelations(node, iSNWR.getOpSetFromSopNode(node));
                partition(newSNWR, iRelationInt);
            }
        }
        //-----------------------------------------------------------------------

        Map<IROperation, Set<IROperation>> opRelationMap = null;

        //Find operation with largest relation set (nbr of elements)-------------
        opRelationMap = getLargestRelationSet(operationsOnThisLevel, operationsOnThisLevel);
        //-----------------------------------------------------------------------

        //Return if no relations have been found---------------------------------
        if (opRelationMap.isEmpty()) {
            return;
        }
        //-----------------------------------------------------------------------

        //Possible movearound of operations between the three partition sets-----
        //MasterSet: the op with largest relation set
        //RelationSet: the largest relation set
        //RemainingElementsSet: operationsOnThisLevel - masterSet - relationSet
        IROperation opWithLargestRelationSet = opRelationMap.keySet().iterator().next();
        Set<IROperation> masterSet = new HashSet<IROperation>();
        masterSet.add(opWithLargestRelationSet);

        Set<IROperation> relationSet = new HashSet<IROperation>();
        relationSet.addAll(opRelationMap.get(opWithLargestRelationSet));

        Set<IROperation> remainingElementsSet = new HashSet<IROperation>(operationsOnThisLevel);
        remainingElementsSet.removeAll(masterSet);
        remainingElementsSet.removeAll(relationSet);
        getLargestRelationSet(remainingElementsSet, relationSet);

        while (possibleChangeInPartition(masterSet, relationSet, remainingElementsSet)) {
        }
        //-----------------------------------------------------------------------

        //Update ISopNode--------------------------------------------------------
        //Add relation type node to root
        ISopNode relationTypeNode = mToolbox.createNode(mRelationInt.toString(), root);
        //Move nodes in master set from root, to relation node
        ISopNode masterSetNode = moveNodeSetToSopNode(masterSet, root, relationTypeNode);
        //Move nodes in relation set from root, to relation node
        ISopNode relationSetNode = moveNodeSetToSopNode(relationSet, root, relationTypeNode);
        //-----------------------------------------------------------------------

        //Recursive calls to partitioned sets------------------------------------
        //Do partition on elements in MasterSet, RelationSet and, RemainingElementsSet
        if (masterSetNode != null) {
            SopNodeWithRelations masterGroup = new SopNodeWithRelations(masterSetNode, iSNWR.getOpSetFromSopNode(masterSetNode));
            partition(masterGroup, iRelationInt);
        }

        if (relationSetNode != null) {
            SopNodeWithRelations relationGroup = new SopNodeWithRelations(relationSetNode, iSNWR.getOpSetFromSopNode(relationSetNode));
            partition(relationGroup, iRelationInt);
        }
        if (!remainingElementsSet.isEmpty()) {
            ISopNode remainingElementsAsSop = null;
            if (remainingElementsSet.size() == 1) {
                IROperation singleOp = remainingElementsSet.iterator().next();
                remainingElementsAsSop = singleOp.getNode();
            } else { //remainingElementsSet.size() > 1
                remainingElementsAsSop = mToolbox.createNode("SOP", root);
                //Move nodes in remaining set from root, to sop node
                moveNodeSetToSopNode(remainingElementsSet, root, remainingElementsAsSop);
            }
            SopNodeWithRelations remainingElementsSNWR = new SopNodeWithRelations(remainingElementsAsSop, iSNWR.getOpSetFromSopNode(remainingElementsAsSop));
            partition(remainingElementsSNWR, iRelationInt);
        }
        //-----------------------------------------------------------------------
    }

    private ISopNode moveNodeSetToSopNode(final Set<IROperation> iSetToMove, final ISopNode iRoot, final ISopNode iNewNode) {
        for (final IROperation op : iSetToMove) {
            mToolbox.removeNode(op.getNode(), iRoot);
        }
        if (iSetToMove.size() > 1) {
            ISopNode groupNode = mToolbox.createNode("SOP", iNewNode);
            for (final IROperation op : iSetToMove) {
                groupNode.addNodeToSequenceSet(op.getNode());
            }
            return groupNode;
        } else if (iSetToMove.size() == 1) {
            ISopNode master = iSetToMove.iterator().next().getNode();
            iNewNode.addNodeToSequenceSet(master);
            return iRoot;
        }
        return null;
    }

    private boolean possibleChangeInPartition(Set<IROperation> ioMasterSet, Set<IROperation> ioRelationSet, Set<IROperation> ioRemainingSet) {
        Map<IROperation, Set<IROperation>> opRelationMap = null;
        opRelationMap = getLargestRelationSet(ioRemainingSet, ioRelationSet);

        if (opRelationMap.isEmpty()) {
            return false;
        }
        IROperation opWithLargestRelationSubset = opRelationMap.keySet().iterator().next();

        Set<IROperation> newMasterSet = new HashSet<IROperation>(ioMasterSet);
        newMasterSet.add(opWithLargestRelationSubset);
        Set<IROperation> newRelationSet = opRelationMap.get(opWithLargestRelationSubset);

        int oldPartitionSize = ioMasterSet.size() + ioRelationSet.size();
        int newPartitionSize = newMasterSet.size() + newRelationSet.size();
        if (newPartitionSize > oldPartitionSize) {
            ioMasterSet = newMasterSet;
            ioRelationSet = newRelationSet;
            ioRemainingSet.addAll(ioRelationSet); //put back
            ioRemainingSet.removeAll(ioMasterSet); //remove new partition
            ioRemainingSet.removeAll(ioRelationSet); //remove new partition
            return true;
        } else {
            return false;
        }
    }

    /**
     * To get the operation (OP) with largest relation set (RS) (nbr of elements)
     * @param iOpSetToCheck set where OP can be found
     * @param iSetToLookIn RS is a subset to this set
     * @param iRelationInt relation type
     * @return OP as key and RS as value. The map is empty if no relation sets can be found
     */
    private Map<IROperation, Set<IROperation>> getLargestRelationSet(final Set<IROperation> iOpSetToCheck, final Set<IROperation> iSetToLookIn) {
        Map<IROperation, Set<IROperation>> opRelationMap = new HashMap<IROperation, Set<IROperation>>(); //Only to store op with largest relation set
        int nbrOfElementsInLargestRelationSet = 0;

        for (final IROperation op : iOpSetToCheck) {
            final Set<IROperation> localRelationSet = getRelationSet(op, iSetToLookIn);
            if (localRelationSet.size() > nbrOfElementsInLargestRelationSet) {
                nbrOfElementsInLargestRelationSet = localRelationSet.size();
                opRelationMap.clear();
                opRelationMap.put(op, localRelationSet);
            }
        }

        return opRelationMap;
    }

    private Set<IROperation> getRelationSet(final IROperation iOp, final Set<IROperation> iSetToLookIn) {
        Set<IROperation> set = new HashSet<IROperation>();
        for (final IROperation op : iSetToLookIn) {
            if (iOp.getRelationToIOperation(op).toString().equals(mRelationInt.toString())) {
                set.add(op);
            }
        }
        return set;
    }
}
