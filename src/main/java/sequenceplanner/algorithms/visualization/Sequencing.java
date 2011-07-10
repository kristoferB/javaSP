package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * Structures that elements in an {@link ISopNode} sequentially.<br/>
 * @author patrik
 */
public class Sequencing {

    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();
    private IRelationContainer mRC = null;

    public Sequencing(final IRelationContainer iRC) {
        this.mRC = iRC;
        final ISopNode startNode = mRC.getOsubsetSopNode();
        sequence(mSNToolbox.getNodes(startNode, false), startNode);
        removeUnnecessarySOPNodes(mRC.getOsubsetSopNode());
    }

    /**
     * Main method in class.<br/>
     * Structures that elements in parameter iNodes sequentially.<br/>
     * The sequence relations are given in {@link IRelationContainer} object set in constructor.<br/>
     * It is required that parameter iSop has all nodes in parameter iNodes in it's sequence set.<br/>
     * @param iNodes nodes to structure sequentially
     * @param iSop current parent to iNodes. Used for update of pointers.
     * @return The node in iNodes that occurs first sequentially, or one such node if iNodes contains "subsets" without sequentially relations.
     */
    private ISopNode sequence(final Set<ISopNode> iNodes, final ISopNode iSop) {
        if (iNodes.isEmpty()) {
            return null;
        }

        //Extract one node as root and create set to loop from remaining nodes
        final ISopNode root = iNodes.iterator().next();

        //Take action if selected root is group
        processGroupNode(root);

        final Set<ISopNode> rootPredSet = new HashSet<ISopNode>();
        final Set<ISopNode> rootSuccSet = new HashSet<ISopNode>();
        final Map<ISopNode, Integer> relationMap = new HashMap<ISopNode, Integer>();
        for (final ISopNode n : iNodes) {
            int relation = -1;

            //Check if n is predecessor to root
            relation = checkSequenceRelation(n, root);
            if (relation != -1) {
                rootPredSet.add(n);
                relationMap.put(n, relation);
            }

            //Check if root is predecessor to n
            relation = checkSequenceRelation(root, n);
            if (relation != -1) {
                rootSuccSet.add(n);
                relationMap.put(n, relation);
            }
        }

        //It is possible that current root has no sequential relation to some nodes.
        //But the nodes can have sequential relations to each other.
        //-> Test their internal relations later.
        final Set<ISopNode> remainingSet = new HashSet<ISopNode>(iNodes);
        remainingSet.remove(root);
        remainingSet.removeAll(rootPredSet);
        remainingSet.removeAll(rootSuccSet);
        sequence(remainingSet, iSop);

        //Sequence predecessor and successor nodes to root
        final ISopNode topPred = sequence(rootPredSet, iSop);
        final ISopNode topSucc = sequence(rootSuccSet, iSop);

        //Possible move of root and topSucc from iSop to successor lists
        updatePointers(root, topPred, topSucc, iSop, relationMap);

        //Return first node in sequence list
        return structure(root, topPred);
    }

    /**
     * Finds the node that occurs first in a sequence to iSop
     * @param iRoot
     * @param iTopPred
     * @return iTopPred if iTopPred != null else iRoot
     */
    private ISopNode structure(final ISopNode iRoot, final ISopNode iTopPred) {
        if (iTopPred != null) {
            return iTopPred;
        } else {
            return iRoot;
        }
    }

    /**
     * Move nodes in node structure.<br/>
     * @param iRoot
     * @param iTopPred
     * @param iTopSucc
     * @param iSop current parent node
     * @param iRelationMap
     */
    private void updatePointers(final ISopNode iRoot, final ISopNode iTopPred, final ISopNode iTopSucc, final ISopNode iSop, final Map<ISopNode, Integer> iRelationMap) {
        final ISopNode bottomPred = mSNToolbox.getBottomSuccessor(iTopPred);

        if (bottomPred != null) {
            bottomPred.setSuccessorNode(iRoot);
            bottomPred.setSuccessorRelation(iRelationMap.get(bottomPred));
            iSop.removeFromSequenceSet(iRoot);
        }

        if (iTopSucc != null) {
            iRoot.setSuccessorNode(iTopSucc);
            iRoot.setSuccessorRelation(iRelationMap.get(iTopSucc));
            iSop.removeFromSequenceSet(iTopSucc);
        }
    }

    /**
     * Recursive call to sequence method if given parameter is group, i.e. not {@link OperationData}.<br/>
     * The sequence method is called with the child nodes to iNode
     * @param iNode Node to check if group
     */
    private void processGroupNode(final ISopNode iNode) {
        if (!(iNode instanceof SopNodeOperation)) {
            //node is group
            sequence(mSNToolbox.getNodes(iNode, false), iNode);
        }
    }

    /**
     * Collects operations from parameter nodes and checks if one out of a set of (in method) given sequence relations is fulfilled.<br/>
     * @param iPossiblePredNode
     * @param iPossibleSuccNode
     * @return sequence relation found, or -1 if no relation was found
     */
    private int checkSequenceRelation(final ISopNode iPossiblePredNode, final ISopNode iPossibleSuccNode) {
        final Set<OperationData> opPredSet = new HashSet<OperationData>();
        addOperationsToSet(iPossiblePredNode, opPredSet);

        final Set<OperationData> opSuccSet = new HashSet<OperationData>();
        addOperationsToSet(iPossibleSuccNode, opSuccSet);

        //Set what relations to check for
        final Set<Integer> relationsToCheckSet = new HashSet<Integer>();
        relationsToCheckSet.add(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12);
        relationsToCheckSet.add(IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12);

        //Check sequentially relations between operations.
//        return strictRelation(relationsToCheckSet, opPredSet, opSuccSet);
        return semiStrictRelation(opPredSet, opSuccSet);
    }

    /**
     * All operations from pred and succ set have to be related with at least SOMETIMES_IN_SEQUENCE relation.<br/>
     * @param iOpPredSet
     * @param iOpSuccSet
     * @return ALWAYS_IN_SEQUENCE_12 if all operation pairs have an ALWAYS_IN_SEQUENCE_12 relation.<br/>
     * SOMETIMES_IN_SEQUENCE_12 if all operation paris have an ALWAYS_IN_SEQUENCE_12 or a SOMETIMES_IN_SEQUENCE relation.<br/>
     * else -1
     */
    private int semiStrictRelation(final Set<OperationData> iOpPredSet, final Set<OperationData> iOpSuccSet) {
        boolean strictSequence = true;
        for (final OperationData opPred : iOpPredSet) {
            for (final OperationData opSucc : iOpSuccSet) {
                final String relationString = mRC.getOperationRelationMap(opPred).get(opSucc).toString();
                //Check if operation pair is related ALWAYS_IN_SEQUENCE
                if (!relationString.equals(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12.toString())) {
                    //No
                    //Check if operation pair is related SOMETIMES_IN_SEQUENCE
                    if (!relationString.toString().equals(IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12.toString())) {
                        //Operation pair is neither related ALWAYS_IN_SEQUENCE nor related SOMETIMES_IN_SEQUENCE
                        return -1;
                    }
                    strictSequence = false; //Operation pair is related SOMETIMES_IN_SEQUENCE
                }
            }
        }
        if (strictSequence) {
            return IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12;
        } else {
            return IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12;
        }
    }

    /**
     * All operations from pred and succ set have to be related with the same relation.<br/>
     * All relations from the relation set are tested.<br/>
     * @param iRelationsToCheckSet
     * @param iOpPredSet
     * @param iOpSuccSet
     * @return the relation all operations agree on, else -1
     */
    private int strictRelation(final Set<Integer> iRelationsToCheckSet, final Set<OperationData> iOpPredSet, final Set<OperationData> iOpSuccSet) {
        for (final Integer relationInt : iRelationsToCheckSet) {
            boolean inSequence = true;
            for (final OperationData opPred : iOpPredSet) {
                for (final OperationData opSucc : iOpSuccSet) {
                    if (!mRC.getOperationRelationMap(opPred).get(opSucc).toString().equals(relationInt.toString())) {
                        //The given opPred opSucc pair was the first pair that did not have the relation.
                        //-> break and test next relation for all pair.
                        inSequence = false;
                        break;
                    }
                }
                if (!inSequence) {
                    break;
                }
            }
            if (inSequence) { //A relation has been found, the method can terminate
                return relationInt;
            }
        }
        return -1; //All relations to check have been checked, the operations can not agree on a relation
    }

    /**
     * If iNode wrapps operation -> operation is added to iSet
     * Else all child operations to iNode are added to iSet
     * @param iNode
     * @param ioSet
     */
    private void addOperationsToSet(final ISopNode iNode, final Set<OperationData> ioSet) {
        if (iNode instanceof SopNodeOperation) {
//            final OperationData opData = (OperationData) iNode.getNodeType();
            ioSet.add(iNode.getOperation());
        } else {
            final Set<OperationData> opDataSet = mSNToolbox.getOperations(iNode, true);
            ioSet.addAll(opDataSet);
        }
    }

    /**
     * Remove SOPnode if it only contains single sequence.<br/>
     * I.e. move child to SOPnode to iRoot.<br/>
     * @param iRoot
     */
    private void removeUnnecessarySOPNodes(final ISopNode iRoot) {
        //Do remove
        final Set<ISopNode> setToLoop = new HashSet<ISopNode>(mSNToolbox.getNodes(iRoot, false));
        for (final ISopNode child : setToLoop) {
            if (child instanceof SopNode) {
                if (child.getFirstNodesInSequencesAsSet().size() == 1) {
                    final ISopNode childChild = child.getFirstNodesInSequencesAsSet().iterator().next();
                    //Move node one level up
                    iRoot.addNodeToSequenceSet(childChild);
                    iRoot.removeFromSequenceSet(child);
                    //Set successor relations
                    final ISopNode lastNodeInChildChildSequence = mSNToolbox.getBottomSuccessor(childChild);
                    final ISopNode firstNodeAfterChild = child.getSuccessorNode();
                    lastNodeInChildChildSequence.setSuccessorNode(firstNodeAfterChild);
                    final int successorRelationType = child.getSuccessorRelation();
                    lastNodeInChildChildSequence.setSuccessorRelation(successorRelationType);
                }
            }
        }

        //Loop children
        final Set<ISopNode> setToLoop2 = new HashSet<ISopNode>(mSNToolbox.getNodes(iRoot, false));
        for (final ISopNode child : setToLoop2) {
            removeUnnecessarySOPNodes(child);
        }
    }
}
