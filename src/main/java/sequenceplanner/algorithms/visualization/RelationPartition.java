package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeAlternative;
import sequenceplanner.model.SOP.SopNodeArbitrary;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.SopNodeParallel;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * To perform partion based on an inital given relation.<br/>
 * The partition that affects the most nbr of nodes is selected.<br/>
 * The partition is then done recursivly on all sets in a partition.<br/>
 * @author patrik
 */
public class RelationPartition {

    private Integer mRelationInt = null;
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();
    private IRelationContainerToolbox mRCToolbox = new RelationContainerToolbox();
    Map<ISopNode, Set<OperationData>> mNodeOperationSetMap = null;

    public RelationPartition(final IRelationContainer iRC, final Integer iRelationInt) {
        iRC.setRootNode(iRC.getOsubsetSopNode());
        this.mRelationInt = iRelationInt;
        partition(iRC);
        mSNToolbox.resolve(iRC.getOsubsetSopNode());
    }

    public void partition(final IRelationContainer iRC) {
        if (iRC == null) {
            return;
        }

        final ISopNode root = iRC.getRootNode();
//        System.out.println("root: " + root.typeToString());

        //Collect all child operations for each node that are children to root---
        //This is in order to see that a relation is between all operations in different sets
        mNodeOperationSetMap = new HashMap<ISopNode, Set<OperationData>>();
        final Set<ISopNode> sopNodesForThisLevel = mSNToolbox.getNodes(root, false);
        for (final ISopNode node : sopNodesForThisLevel) {

            Set<OperationData> set = new HashSet<OperationData>();
            if (node instanceof SopNodeOperation) {
                set.add(node.getOperation());
            } else {
                final Set<OperationData> opDataSet = mSNToolbox.getOperations(node, true);
                set.addAll(opDataSet);
            }

            mNodeOperationSetMap.put(node, set);
        }
        //-----------------------------------------------------------------------

        //Find node with largest relation set (nbr of other nodes)---------------
        final Map<ISopNode, Set<ISopNode>> nodeRelationMap = getLargestRelationSet(mNodeOperationSetMap, mNodeOperationSetMap, iRC);
        //-----------------------------------------------------------------------

        //If no relations have been found----------------------------------------
        if (nodeRelationMap.isEmpty()) {
            //Recursive calls to children
            for (final ISopNode node : mNodeOperationSetMap.keySet()) {
                iRC.setRootNode(node);
                partition(iRC);
            }
            return;
        }
        //-----------------------------------------------------------------------

        //Possible movearound of operations between the three partition sets-----
        //MasterSet: the set with largest relation set
        //RelationSet: the largest relation set
        //RemainingElementsSet: nodesOnThisLevel - masterSet - relationSet
        ISopNode setWithLargestRelationSet = nodeRelationMap.keySet().iterator().next();
        Set<ISopNode> masterSet = new HashSet<ISopNode>();
        masterSet.add(setWithLargestRelationSet);

        Set<ISopNode> relationSet = new HashSet<ISopNode>();
        relationSet.addAll(nodeRelationMap.get(setWithLargestRelationSet));

        Set<ISopNode> remainingElementsSet = new HashSet<ISopNode>(mNodeOperationSetMap.keySet());
        remainingElementsSet.removeAll(masterSet);
        remainingElementsSet.removeAll(relationSet);

        while (possibleChangeInPartition(masterSet, relationSet, remainingElementsSet, iRC)) {
        }
        //-----------------------------------------------------------------------

        //Update Root------------------------------------------------------------
        //Add relation type node to root
        ISopNode relationTypeNode = null;
        final String relationAsString = RelateTwoOperations.relationIntegerToString(mRelationInt, "", "");
        if(relationAsString.equals(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE,"",""))) {
            relationTypeNode = new SopNodeAlternative();
        } else if(relationAsString.equals(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER,"",""))) {
            relationTypeNode = new SopNodeArbitrary();
        } else if(relationAsString.equals(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL,"",""))) {
            relationTypeNode = new SopNodeParallel();
        }
        root.addNodeToSequenceSet(relationTypeNode);
        //Move nodes in master set from root, to relation node
        ISopNode masterSetNode = moveNodeSetToSopNode(masterSet, root, relationTypeNode);
        //Move nodes in relation set from root, to relation node
        ISopNode relationSetNode = moveNodeSetToSopNode(relationSet, root, relationTypeNode);
        //-----------------------------------------------------------------------

        //Recursive calls to partitioned sets------------------------------------
        //Do partition on elements in MasterSet, RelationSet and, RemainingElementsSet
        if (masterSetNode != null) {
            iRC.setRootNode(masterSetNode);
            partition(iRC);
        }

        if (relationSetNode != null) {
            iRC.setRootNode(relationSetNode);
            partition(iRC);
        }

        if (!remainingElementsSet.isEmpty()) {
            ISopNode remainingElementsNode = null;
            if (remainingElementsSet.size() == 1) {
                remainingElementsNode = remainingElementsSet.iterator().next();
            } else { //remainingElementsSet.size() > 1
                remainingElementsNode = new SopNode();
                root.addNodeToSequenceSet(remainingElementsNode);
                //Move nodes in remaining set from root, to new node
                moveNodeSetToSopNode(remainingElementsSet, root, remainingElementsNode);
            }

            iRC.setRootNode(remainingElementsNode);
            partition(iRC);
        }
        //-----------------------------------------------------------------------
    }

    /**
     * To move a set of nodes between two nodes
     * @param iSetToMove nodes to move
     * @param iOldNode from node
     * @param iNewNode to node
     * @return the moved node if iSetToMode.size()=1, else a new SOP node that contains iSetToMove
     */
    private ISopNode moveNodeSetToSopNode(final Set<ISopNode> iSetToMove, final ISopNode iOldNode, final ISopNode iNewNode) {
        if (iSetToMove.size() > 1) {
            final ISopNode groupNode = new SopNode();
            iNewNode.addNodeToSequenceSet(groupNode);
            for (final ISopNode node : iSetToMove) {
                groupNode.addNodeToSequenceSet(node);
                mSNToolbox.removeNode(node, iOldNode);
            }
            return groupNode;
        } else if (iSetToMove.size() == 1) {
            ISopNode opNode = iSetToMove.iterator().next();
            iNewNode.addNodeToSequenceSet(opNode);
            mSNToolbox.removeNode(opNode, iOldNode);
            return opNode;
        }
        return null;
    }

    /**
     * Change the partitioned sets if master set can be increased and relation set can be remained.<br/>
     * @param ioMasterSet master set
     * @param ioRelationSet relation set
     * @param ioRemainingSet remaining set
     * @param iRC relation container
     * @return true if partition has been changed, else false
     */
    private boolean possibleChangeInPartition(Set<ISopNode> ioMasterSet, Set<ISopNode> ioRelationSet, Set<ISopNode> ioRemainingSet, final IRelationContainer iRC) {
        Map<ISopNode, Set<OperationData>> remainingMap = new HashMap<ISopNode, Set<OperationData>>();
        for (final ISopNode node : ioRemainingSet) {
            remainingMap.put(node, mNodeOperationSetMap.get(node));
        }
        Map<ISopNode, Set<OperationData>> relationMap = new HashMap<ISopNode, Set<OperationData>>();
        for (final ISopNode node : ioRelationSet) {
            relationMap.put(node, mNodeOperationSetMap.get(node));
        }

        Map<ISopNode, Set<ISopNode>> nodeRelationMap = null;
        nodeRelationMap = getLargestRelationSet(remainingMap, relationMap, iRC);

        //No relations were found
        if (nodeRelationMap.isEmpty()) {
            return false;
        }
        ISopNode opWithLargestRelationSubset = nodeRelationMap.keySet().iterator().next();

        Set<ISopNode> newMasterSet = new HashSet<ISopNode>(ioMasterSet);
        newMasterSet.add(opWithLargestRelationSubset);
        Set<ISopNode> newRelationSet = nodeRelationMap.get(opWithLargestRelationSubset);

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
     * To get the node (N) with largest relation set (RS) (nbr of elements)
     * @param iMapToCheck set where N can be found
     * @param iMapToLookIn RS is a subset to this set
     * @param iRC relation container
     * @return N as key and RS as value. The map is empty if no relation sets can be found
     */
    private Map<ISopNode, Set<ISopNode>> getLargestRelationSet(final Map<ISopNode, Set<OperationData>> iMapToCheck, final Map<ISopNode, Set<OperationData>> iMapToLookIn, final IRelationContainer iRC) {
        Map<ISopNode, Set<ISopNode>> opRelationMap = new HashMap<ISopNode, Set<ISopNode>>(); //Only to store op with largest relation set
        int nbrOfElementsInLargestRelationSet = 0;
        for (final ISopNode node : iMapToCheck.keySet()) {
            final Set<OperationData> opSet = iMapToCheck.get(node);
            Set<ISopNode> localRelationSet = null;
            for (final OperationData op : opSet) {
                final Set<ISopNode> tempRelationSet = getRelationSet(op, iMapToLookIn, iRC);
                if (localRelationSet == null) {
                    localRelationSet = tempRelationSet;
                } else {
                    localRelationSet.retainAll(tempRelationSet);
                }
            }
            if (localRelationSet.size() > nbrOfElementsInLargestRelationSet) {
//                    System.out.println("new LargestRelationSet " + op.getName());
                nbrOfElementsInLargestRelationSet = localRelationSet.size();
                opRelationMap.clear();
                opRelationMap.put(node, localRelationSet);
            }

        }

        return opRelationMap;
    }

    /**
     * To get the nodes (keys) whose operations (values) that a given operation has relation to
     * @param iOp operation to test
     * @param iSetToLookIn key: node, value: set of operations
     * @param iRC relation container
     * @return set of keys. The set is empty if no relations were found.
     */
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
