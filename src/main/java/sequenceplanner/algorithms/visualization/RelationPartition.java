package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNode;
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
    Map<Set<OperationData>,ISopNode> mOpSetOfSetsForThisLevel = null;

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
//        Map<Set<OperationData>,ISopNode> opSetOfSetsForThisLevel = new HashMap<Set<OperationData>, ISopNode>();
        mOpSetOfSetsForThisLevel = new HashMap<Set<OperationData>, ISopNode>();
        Set<ISopNode> sopNodesForThisLevel = mSNToolbox.getNodes(root, false);
        for (final ISopNode node : sopNodesForThisLevel) {
            System.out.println(node.typeToString());
            Set<OperationData> set = new HashSet<OperationData>();
            mOpSetOfSetsForThisLevel.put(set,node);
            if (node.getNodeType() instanceof OperationData) {
                final OperationData opData = (OperationData) node.getNodeType();
                set.add(opData);
            } else {
                Set<OperationData> opDataSet = mSNToolbox.getOperations(node, true);
                set.addAll(opDataSet);
            }
        }
        //-----------------------------------------------------------------------

        final Set<OperationData> operationsOnThisLevel = mSNToolbox.getOperations(root, false);
        if (root == null || operationsOnThisLevel == null) {
            return;
        }

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

        Map<Set<OperationData>, Set<Set<OperationData>>> opRelationMap = null;

        //Find operation with largest relation set (nbr of elements)-------------
        opRelationMap = getLargestRelationSet(mOpSetOfSetsForThisLevel.keySet(), mOpSetOfSetsForThisLevel.keySet(), iRC);
        //-----------------------------------------------------------------------

        for(final Set<OperationData> opSet : opRelationMap.keySet()) {
            
            for(final OperationData op : opSet) {
                System.out.println(op.getName());
                System.out.println("-------");
                for(final Set<OperationData> opDataSet : opRelationMap.get(opSet)) {
                    System.out.println("----");
                    for(final OperationData opIn : opDataSet) {
                        System.out.println(opIn.getName());
                    }
                }
            }
        }

        //Return if no relations have been found---------------------------------
        if (opRelationMap.isEmpty()) {
            return;
        }
        //-----------------------------------------------------------------------

        //Possible movearound of operations between the three partition sets-----
        //MasterSet: the opSet with largest relation set
        //RelationSet: the largest relation set
        //RemainingElementsSet: operationsOnThisLevel - masterSet - relationSet
        Set<OperationData> opSetWithLargestRelationSet = opRelationMap.keySet().iterator().next();
        Set<Set<OperationData>> masterSet = new HashSet<Set<OperationData>>();
        masterSet.add(opSetWithLargestRelationSet);

        Set<Set<OperationData>> relationSet = new HashSet<Set<OperationData>>();
        relationSet.addAll(opRelationMap.get(opSetWithLargestRelationSet));

        Set<Set<OperationData>> remainingElementsSet = new HashSet<Set<OperationData>>(mOpSetOfSetsForThisLevel.keySet());
        remainingElementsSet.removeAll(masterSet);
        remainingElementsSet.removeAll(relationSet);
//        getLargestRelationSet(remainingElementsSet, relationSet, iRC);

        while (possibleChangeInPartition(masterSet, relationSet, remainingElementsSet, iRC)) {
        }
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
        if (masterSetNode != null) {
            System.out.println("masterSet");
            System.out.println(iRC.getRootNode().inDepthToString());
            IRelationContainer newRC = new RelationContainer();
            newRC.setRootNode(masterSetNode);
            partition(newRC, iRelationInt);
        }

        if (relationSetNode != null) {
            System.out.println("relationSet");
            IRelationContainer newRC = new RelationContainer();
            newRC.setRootNode(relationSetNode);
            partition(newRC, iRelationInt);
        }
        if (!remainingElementsSet.isEmpty()) {
            System.out.println("remainingSet");
            ISopNode remainingElementsAsSop = null;
            if (remainingElementsSet.size() == 1) {
                Set<OperationData> singleOp = remainingElementsSet.iterator().next();
//                remainingElementsAsSop = mRCToolbox.getSopNode(singleOp, root);
                remainingElementsAsSop = mOpSetOfSetsForThisLevel.get(singleOp);
            } else { //remainingElementsSet.size() > 1
                remainingElementsAsSop = mSNToolbox.createNode("SOP", root);
            }
                //Move nodes in remaining set from root, to sop node
                moveNodeSetToSopNode(remainingElementsSet, root, remainingElementsAsSop);
            
            IRelationContainer newRC = new RelationContainer();
            newRC.setRootNode(root);
            partition(newRC, iRelationInt);
        }
        //-----------------------------------------------------------------------
    }

    private ISopNode moveNodeSetToSopNode(final Set<Set<OperationData>> iSetToMove, final ISopNode iRoot, final ISopNode iNewNode) {
        System.out.println("moveNodeSetToSopNode iSetToMove.size: " + iSetToMove.size());
        if (iSetToMove.size() > 1) {
            ISopNode groupNode = mSNToolbox.createNode("SOP", iNewNode);
            for (final Set<OperationData> opSet : iSetToMove) {
//                ISopNode opNode = mRCToolbox.getSopNode(op, iRoot);
                ISopNode opNode = mOpSetOfSetsForThisLevel.get(opSet);
                groupNode.addNodeToSequenceSet(opNode);
                mSNToolbox.removeNode(opNode, iRoot);
            }
            return groupNode;
        } else if (iSetToMove.size() == 1) {
//Skapas null pointer här. mOpset... ger tillbaka en null!
//            ISopNode opNode = mRCToolbox.getSopNode(iSetToMove.iterator().next(), iRoot);
            ISopNode opNode = mOpSetOfSetsForThisLevel.get(iSetToMove.iterator().next());
            iNewNode.addNodeToSequenceSet(opNode);
            mSNToolbox.removeNode(opNode, iRoot);
            System.out.println(iRoot.inDepthToString());
            return iRoot;
        }
        return null;
    }

    private boolean possibleChangeInPartition(Set<Set<OperationData>> ioMasterSet, Set<Set<OperationData>> ioRelationSet, Set<Set<OperationData>> ioRemainingSet, final IRelationContainer iRC) {
        Map<Set<OperationData>, Set<Set<OperationData>>> opRelationMap = null;
        opRelationMap = getLargestRelationSet(ioRemainingSet, ioRelationSet, iRC);

        //No relations were found
        if (opRelationMap.isEmpty()) {
            return false;
        }
        Set<OperationData> opWithLargestRelationSubset = opRelationMap.keySet().iterator().next();

        Set<Set<OperationData>> newMasterSet = new HashSet<Set<OperationData>>(ioMasterSet);
        newMasterSet.add(opWithLargestRelationSubset);
        Set<Set<OperationData>> newRelationSet = opRelationMap.get(opWithLargestRelationSubset);

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
    private Map<Set<OperationData>, Set<Set<OperationData>>> getLargestRelationSet(final Set<Set<OperationData>> iOpSetToCheck, final Set<Set<OperationData>> iSetToLookIn, final IRelationContainer iRC) {
        Map<Set<OperationData>, Set<Set<OperationData>>> opRelationMap = new HashMap<Set<OperationData>, Set<Set<OperationData>>>(); //Only to store op with largest relation set
        int nbrOfElementsInLargestRelationSet = 0;

        for (final Set<OperationData> opSet : iOpSetToCheck) {
            for (final OperationData op : opSet) {
                final Set<Set<OperationData>> localRelationSet = getRelationSet(op, iSetToLookIn, iRC);
                if (localRelationSet.size() > nbrOfElementsInLargestRelationSet) {
                    nbrOfElementsInLargestRelationSet = localRelationSet.size();
                    opRelationMap.clear();
                    opRelationMap.put(opSet, localRelationSet);
                }
            }
        }

        return opRelationMap;
    }

    private Set<Set<OperationData>> getRelationSet(final OperationData iOp, final Set<Set<OperationData>> iSetToLookIn, final IRelationContainer iRC) {
        Set<Set<OperationData>> returnSet = new HashSet<Set<OperationData>>();
        for (final Set<OperationData> opSet : iSetToLookIn) {
            boolean toAdd = true;
            //Check if all operations in set follows the relation
            for (final OperationData op : opSet) {
                if (!mRCToolbox.getRelation(iOp, op, iRC).toString().equals(mRelationInt.toString())) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                returnSet.add(opSet);
            }
        }
        return returnSet;
    }
}
