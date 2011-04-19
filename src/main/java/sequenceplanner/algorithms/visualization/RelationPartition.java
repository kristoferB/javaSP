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
public class RelationPartition {

    private Integer mRelationInt = null;
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();
    private IRelationContainerToolbox mRCToolbox = new RelationContainerToolbox();
//    Map<Set<OperationData>, ISopNode> mOpSetOfSetsForThisLevel = null;
    Map<ISopNode, Set<OperationData>> mNodeOperationSetMap = null;

    public RelationPartition(final IRelationContainer iRC, final Integer iRelationInt) {
        iRC.setRootNode(iRC.getOsubsetSopNode());
        partition(iRC, iRelationInt);
    }

    public void partition(final IRelationContainer iRC, final Integer iRelationInt) {
        if (iRC == null) {
            return;
        }
        mRelationInt = iRelationInt;

        final ISopNode root = iRC.getRootNode();
        System.out.println("root: " + root.typeToString());

        //-----------------------------------------------------------------------
        mNodeOperationSetMap = new HashMap<ISopNode, Set<OperationData>>();
        Set<ISopNode> sopNodesForThisLevel = mSNToolbox.getNodes(root, false);
        for (final ISopNode node : sopNodesForThisLevel) {
            System.out.println(node.typeToString());
            Set<OperationData> set = new HashSet<OperationData>();

            if (node.getNodeType() instanceof OperationData) {
                final OperationData opData = (OperationData) node.getNodeType();
                set.add(opData);
            } else {
                Set<OperationData> opDataSet = mSNToolbox.getOperations(node, true);
                set.addAll(opDataSet);
            }
            mNodeOperationSetMap.put(node, set);
        }
        //-----------------------------------------------------------------------

//        final Set<OperationData> operationsOnThisLevel = mSNToolbox.getOperations(root, false);
//        if (root == null || operationsOnThisLevel == null) {
//            return;
//        }

        //Recursive calls to children (root.set)---------------------------------
        //The recusive calls are done in the beginning because new nodes are introduced as children and some nodes are remove in the remainder of this method.
//        for (final ISopNode node : mSNToolbox.getNodes(root, false)) {
//            //Check if more children exists
//            if (!mSNToolbox.getNodes(node, false).isEmpty()) {
//                IRelationContainer newRC = new RelationContainer();
//                newRC.setRootNode(node);
//                partition(newRC, iRelationInt);
//            }
//        }
        //-----------------------------------------------------------------------

        Map<ISopNode, Set<ISopNode>> nodeRelationMap = null;
        //Find operation with largest relation set (nbr of elements)-------------
        nodeRelationMap = getLargestRelationSet(mNodeOperationSetMap, mNodeOperationSetMap, iRC);
        //-----------------------------------------------------------------------

        System.out.println("Contains2: " + mNodeOperationSetMap.keySet().contains(nodeRelationMap.keySet().iterator().next()));
        for (final ISopNode externalNode : nodeRelationMap.keySet()) {
            for (final OperationData opData : mNodeOperationSetMap.get(externalNode)) {
                System.out.println(opData.getName());
            }
            System.out.println("-------");
            final Set<ISopNode> internalNodeSet = nodeRelationMap.get(externalNode);
            for (final ISopNode internalNode : internalNodeSet) {
                for (final OperationData opData : mNodeOperationSetMap.get(internalNode)) {
                    System.out.println(opData.getName());
                }
                System.out.println("----");
            }
        }

        //Return if no relations have been found---------------------------------
//        if (opRelationMap.isEmpty()) {
//            return;
//        }
        if (nodeRelationMap.isEmpty()) {
            return;
        }
        //-----------------------------------------------------------------------

        //Possible movearound of operations between the three partition sets-----
        //MasterSet: the opSet with largest relation set
        //RelationSet: the largest relation set
        //RemainingElementsSet: operationsOnThisLevel - masterSet - relationSet
        ISopNode opSetWithLargestRelationSet = nodeRelationMap.keySet().iterator().next();
        Set<ISopNode> masterSet = new HashSet<ISopNode>();
        masterSet.add(opSetWithLargestRelationSet);

        Set<ISopNode> relationSet = new HashSet<ISopNode>();
        relationSet.addAll(nodeRelationMap.get(opSetWithLargestRelationSet));

        Set<ISopNode> remainingElementsSet = new HashSet<ISopNode>(mNodeOperationSetMap.keySet());
        remainingElementsSet.removeAll(masterSet);
        remainingElementsSet.removeAll(relationSet);
//        getLargestRelationSet(remainingElementsSet, relationSet, iRC);

//        while (possibleChangeInPartition(masterSet, relationSet, remainingElementsSet, iRC)) {
//        }
        //-----------------------------------------------------------------------

        //Update ISopNode--------------------------------------------------------
        //Add relation type node to root
//        System.out.println(root.inDepthToString());
        ISopNode relationTypeNode = mSNToolbox.createNode(mRelationInt.toString(), root);
//        System.out.println(root.inDepthToString());
        //Move nodes in master set from root, to relation node
        ISopNode masterSetNode = moveNodeSetToSopNode(masterSet, root, relationTypeNode);
        //Move nodes in relation set from root, to relation node
        ISopNode relationSetNode = moveNodeSetToSopNode(relationSet, root, relationTypeNode);
        //-----------------------------------------------------------------------

        //Recursive calls to partitioned sets------------------------------------
        //Do partition on elements in MasterSet, RelationSet and, RemainingElementsSet
//        if (masterSetNode != null) {
//            System.out.println("masterSet");
//            System.out.println(iRC.getRootNode().inDepthToString());
//            IRelationContainer newRC = new RelationContainer();
//            newRC.setRootNode(masterSetNode);
//            partition(newRC, iRelationInt);
//        }

//        if (relationSetNode != null) {
//            System.out.println("relationSet");
//            IRelationContainer newRC = new RelationContainer();
//            newRC.setRootNode(relationSetNode);
//            partition(newRC, iRelationInt);
//        }

//        if (!remainingElementsSet.isEmpty()) {
//            System.out.println("remainingSet");
//            ISopNode remainingElementsAsSop = null;
//            if (remainingElementsSet.size() == 1) {
//                Set<OperationData> singleOp = remainingElementsSet.iterator().next();
////                remainingElementsAsSop = mRCToolbox.getSopNode(singleOp, root);
//                remainingElementsAsSop = mOpSetOfSetsForThisLevel.get(singleOp);
//            } else { //remainingElementsSet.size() > 1
//                remainingElementsAsSop = mSNToolbox.createNode("SOP", root);
//            }
//            //Move nodes in remaining set from root, to sop node
//            moveNodeSetToSopNode(remainingElementsSet, root, remainingElementsAsSop);
//
//            IRelationContainer newRC = new RelationContainer();
//            newRC.setRootNode(root);
//            partition(newRC, iRelationInt);
//        }
        //-----------------------------------------------------------------------
    }

    private ISopNode moveNodeSetToSopNode(final Set<ISopNode> iSetToMove, final ISopNode iRoot, final ISopNode iNewNode) {
        if (iSetToMove.size() > 1) {
            ISopNode groupNode = mSNToolbox.createNode("SOP", iNewNode);
            for (final ISopNode node : iSetToMove) {
                groupNode.addNodeToSequenceSet(node);
                mSNToolbox.removeNode(node, iRoot);
            }
            return groupNode;
        } else if (iSetToMove.size() == 1) {
            ISopNode opNode = iSetToMove.iterator().next();
            iNewNode.addNodeToSequenceSet(opNode);
            mSNToolbox.removeNode(opNode, iRoot);
//            System.out.println(iRoot.inDepthToString());
            return iRoot;
        }
        return null;
    }
//
//    private boolean possibleChangeInPartition(Set<ISopNode> ioMasterSet, Set<ISopNode> ioRelationSet, Set<ISopNode> ioRemainingSet, final IRelationContainer iRC) {
//        Map<ISopNode,Set<OperationData>> ioRemainingMap = new HashMap<ISopNode, Set<OperationData>>();
//        for(final ISopNode node : ioMasterSet) {
//
//        }
//
//        Map<ISopNode, Set<ISopNode>> opRelationMap = null;
//        opRelationMap = getLargestRelationSet2(ioRemainingSet, ioRelationSet, iRC);
//
//        //No relations were found
//        if (opRelationMap.isEmpty()) {
//            return false;
//        }
//        Set<OperationData> opWithLargestRelationSubset = opRelationMap.keySet().iterator().next();
//
//        Set<Set<OperationData>> newMasterSet = new HashSet<Set<OperationData>>(ioMasterSet);
//        newMasterSet.add(opWithLargestRelationSubset);
//        Set<Set<OperationData>> newRelationSet = opRelationMap.get(opWithLargestRelationSubset);
//
//        int oldPartitionSize = ioMasterSet.size() + ioRelationSet.size();
//        int newPartitionSize = newMasterSet.size() + newRelationSet.size();
//        if (newPartitionSize > oldPartitionSize) {
//            ioMasterSet.addAll(newMasterSet);
//            ioRelationSet = newRelationSet;
//            ioRemainingSet.addAll(ioRelationSet); //put back
//            ioRemainingSet.removeAll(ioMasterSet); //remove new partition
//            ioRemainingSet.removeAll(ioRelationSet); //remove new partition
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * To get the operation (OP) with largest relation set (RS) (nbr of elements)
     * @param iOpSetToCheck set where OP can be found
     * @param iSetToLookIn RS is a subset to this set
     * @param iRelationInt relation type
     * @return OP as key and RS as value. The map is empty if no relation sets can be found
     */
    private Map<ISopNode, Set<ISopNode>> getLargestRelationSet(final Map<ISopNode, Set<OperationData>> iOpSetToCheck, final Map<ISopNode, Set<OperationData>> iSetToLookIn, final IRelationContainer iRC) {
        Map<ISopNode, Set<ISopNode>> opRelationMap = new HashMap<ISopNode, Set<ISopNode>>(); //Only to store op with largest relation set
        int nbrOfElementsInLargestRelationSet = 0;
        for (final ISopNode node : iOpSetToCheck.keySet()) {
            final Set<OperationData> opSet = iOpSetToCheck.get(node);
            for (final OperationData op : opSet) {
                final Set<ISopNode> localRelationSet = getRelationSet(op, iSetToLookIn, iRC);
                if (localRelationSet.size() > nbrOfElementsInLargestRelationSet) {
                    nbrOfElementsInLargestRelationSet = localRelationSet.size();
                    opRelationMap.clear();
                    opRelationMap.put(node, localRelationSet);
                }
            }
        }

        return opRelationMap;
    }

    private Set<ISopNode> getRelationSet(final OperationData iOp, final Map<ISopNode, Set<OperationData>> iSetToLookIn, final IRelationContainer iRC) {
        Set<ISopNode> returnSet = new HashSet<ISopNode>();
        for (final ISopNode node : iSetToLookIn.keySet()) {
            final Set<OperationData> opSet = iSetToLookIn.get(node);
            boolean toAdd = true;
            //Check if all operations in set follows the relation
            for (final OperationData op : opSet) {
                if (!mRCToolbox.getRelation(iOp, op, iRC).toString().equals(mRelationInt.toString())) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                returnSet.add(node);
            }
        }
        return returnSet;
    }
}
