package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class RelationPartition3 {

    private Integer mRelationInt = null;
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();
    private IRelationContainerToolbox mRCToolbox = new RelationContainerToolbox();

    public RelationPartition3(final IRelationContainer iRC, final Integer iRelationInt) {
        iRC.setRootNode(iRC.getOsubsetSopNode());
        partition(iRC, iRelationInt);
    }

    public void partition(final IRelationContainer iRC, final Integer iRelationInt) {
        if (iRC == null) {
            return;
        }
        mRelationInt = iRelationInt;

        final ISopNode root = iRC.getRootNode();
        final Set<OperationData> operationsOnThisLevel = mSNToolbox.getOperations(root, false);
        if (root == null || operationsOnThisLevel == null) {
            return;
        }

        //Recursive calls to children (root.set)---------------------------------
        //The recusive calls are done in the beginning because new nodes are introduced as children and some nodes are remove in the remainder of this method.
        for (final ISopNode node : mSNToolbox.getNodes(root, false)) {
            //Check if more children exists
            if (!mSNToolbox.getNodes(node, false).isEmpty()) {
                IRelationContainer newRC = new RelationContainer();
                newRC.setRootNode(node);
                partition(newRC, iRelationInt);
            }
        }
        //-----------------------------------------------------------------------

        Map<OperationData, Set<OperationData>> opRelationMap = null;

        //Find operation with largest relation set (nbr of elements)-------------
        opRelationMap = getLargestRelationSet(operationsOnThisLevel, operationsOnThisLevel, iRC);
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
        OperationData opWithLargestRelationSet = opRelationMap.keySet().iterator().next();
        Set<OperationData> masterSet = new HashSet<OperationData>();
        masterSet.add(opWithLargestRelationSet);

        Set<OperationData> relationSet = new HashSet<OperationData>();
        relationSet.addAll(opRelationMap.get(opWithLargestRelationSet));

        Set<OperationData> remainingElementsSet = new HashSet<OperationData>(operationsOnThisLevel);
        remainingElementsSet.removeAll(masterSet);
        remainingElementsSet.removeAll(relationSet);
//        getLargestRelationSet(remainingElementsSet, relationSet, iRC);

        while (possibleChangeInPartition(masterSet, relationSet, remainingElementsSet, iRC)) {
        }
        //-----------------------------------------------------------------------

        //Update ISopNode--------------------------------------------------------
        //Add relation type node to root
        ISopNode relationTypeNode = mSNToolbox.createNode(mRelationInt.toString(), root);
        //Move nodes in master set from root, to relation node
        ISopNode masterSetNode = moveNodeSetToSopNode(masterSet, root, relationTypeNode);
        //Move nodes in relation set from root, to relation node
        ISopNode relationSetNode = moveNodeSetToSopNode(relationSet, root, relationTypeNode);
        //-----------------------------------------------------------------------

        //Recursive calls to partitioned sets------------------------------------
        //Do partition on elements in MasterSet, RelationSet and, RemainingElementsSet
        if (masterSetNode != null) {
            IRelationContainer newRC = new RelationContainer();
            newRC.setRootNode(masterSetNode);
            partition(newRC, iRelationInt);
        }

        if (relationSetNode != null) {
            IRelationContainer newRC = new RelationContainer();
            newRC.setRootNode(relationSetNode);
            partition(newRC, iRelationInt);
        }
        if (!remainingElementsSet.isEmpty()) {
            ISopNode remainingElementsAsSop = null;
            if (remainingElementsSet.size() == 1) {
                OperationData singleOp = remainingElementsSet.iterator().next();
                remainingElementsAsSop = mRCToolbox.getSopNode(singleOp, root);
            } else { //remainingElementsSet.size() > 1
                remainingElementsAsSop = mSNToolbox.createNode("SOP", root);
                //Move nodes in remaining set from root, to sop node
                moveNodeSetToSopNode(remainingElementsSet, root, remainingElementsAsSop);
            }
            IRelationContainer newRC = new RelationContainer();
            newRC.setRootNode(root);
            partition(newRC, iRelationInt);
        }
        //-----------------------------------------------------------------------
    }

    private ISopNode moveNodeSetToSopNode(final Set<OperationData> iSetToMove, final ISopNode iRoot, final ISopNode iNewNode) {
//        for (final OperationData op : iSetToMove) {
//            ISopNode opNode = mRCToolbox.getSopNode(op, iRoot);
//            mSNToolbox.removeNode(opNode, iRoot);
//        }
        if (iSetToMove.size() > 1) {
            ISopNode groupNode = mSNToolbox.createNode("SOP", iNewNode);
            for (final OperationData op : iSetToMove) {
                ISopNode opNode = mRCToolbox.getSopNode(op, iRoot);
                groupNode.addNodeToSequenceSet(opNode);
                mSNToolbox.removeNode(opNode, iRoot);
            }
            return groupNode;
        } else if (iSetToMove.size() == 1) {
            ISopNode opNode = mRCToolbox.getSopNode(iSetToMove.iterator().next(), iRoot);
            iNewNode.addNodeToSequenceSet(opNode);
            mSNToolbox.removeNode(opNode, iRoot);
            return iRoot;
        }
        return null;
    }

    private boolean possibleChangeInPartition(Set<OperationData> ioMasterSet, Set<OperationData> ioRelationSet, Set<OperationData> ioRemainingSet, final IRelationContainer iRC) {
        Map<OperationData, Set<OperationData>> opRelationMap = null;
        opRelationMap = getLargestRelationSet(ioRemainingSet, ioRelationSet, iRC);

        //No relations were found
        if (opRelationMap.isEmpty()) {
            return false;
        }
        OperationData opWithLargestRelationSubset = opRelationMap.keySet().iterator().next();

        Set<OperationData> newMasterSet = new HashSet<OperationData>(ioMasterSet);
        newMasterSet.add(opWithLargestRelationSubset);
        Set<OperationData> newRelationSet = opRelationMap.get(opWithLargestRelationSubset);

        int oldPartitionSize = ioMasterSet.size() + ioRelationSet.size();
        int newPartitionSize = newMasterSet.size() + newRelationSet.size();
        if (newPartitionSize > oldPartitionSize) {
            ioMasterSet.addAll(newMasterSet);
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
    private Map<OperationData, Set<OperationData>> getLargestRelationSet(final Set<OperationData> iOpSetToCheck, final Set<OperationData> iSetToLookIn, final IRelationContainer iRC) {
        Map<OperationData, Set<OperationData>> opRelationMap = new HashMap<OperationData, Set<OperationData>>(); //Only to store op with largest relation set
        int nbrOfElementsInLargestRelationSet = 0;

        for (final OperationData op : iOpSetToCheck) {
            final Set<OperationData> localRelationSet = getRelationSet(op, iSetToLookIn, iRC);
            if (localRelationSet.size() > nbrOfElementsInLargestRelationSet) {
                nbrOfElementsInLargestRelationSet = localRelationSet.size();
                opRelationMap.clear();
                opRelationMap.put(op, localRelationSet);
            }
        }

        return opRelationMap;
    }

    private Set<OperationData> getRelationSet(final OperationData iOp, final Set<OperationData> iSetToLookIn, final IRelationContainer iRC) {
        Set<OperationData> set = new HashSet<OperationData>();
        for (final OperationData op : iSetToLookIn) {
            if (mRCToolbox.getRelation(iOp, op, iRC).toString().equals(mRelationInt.toString())) {
                set.add(op);
            }
        }
        return set;
    }
}
