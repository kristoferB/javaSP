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
    }

    private ISopNode sequence(final Set<ISopNode> iNodes, final ISopNode iSop) {
        if (iNodes.isEmpty()) {
            return null;
        }

        final ISopNode root = iNodes.iterator().next();
        final Set<ISopNode> remainingSet = new HashSet<ISopNode>(iNodes);
        remainingSet.remove(root);

        processGroupNode(root);

        final Set<ISopNode> rootPredSet = new HashSet<ISopNode>();
        final Set<ISopNode> rootSuccSet = new HashSet<ISopNode>();
        final Map<ISopNode, Integer> relationMap = new HashMap<ISopNode, Integer>();
        for (final ISopNode n : remainingSet) {
            int relation = -1;

            relation = checkSequenceRelation(n, root);
            if (relation != -1) {
                rootPredSet.add(n);
                relationMap.put(n, relation);
            }
            relation = checkSequenceRelation(root, n);
            if (relation != -1) {
                rootSuccSet.add(n);
                relationMap.put(n, relation);
            }

            processGroupNode(n);
        }

        final ISopNode topPred = sequence(rootPredSet, iSop);
        final ISopNode topSucc = sequence(rootSuccSet, iSop);

        updatePointers(root, topPred, topSucc, iSop, relationMap);

        return structure(root, topPred);
    }

    private ISopNode structure(final ISopNode iRoot, final ISopNode iTopPred) {
        if (iTopPred != null) {
            return iTopPred;
        } else {
            return iRoot;
        }
    }

    private void updatePointers(final ISopNode iRoot, final ISopNode iTopPred, final ISopNode iTopSucc, final ISopNode iSop, final Map<ISopNode, Integer> iRelationMap) {
        final ISopNode bottomPred = mSNToolbox.getBottomSuccessor(iTopPred);

        if (bottomPred != null) {
            bottomPred.setSuccessorNode(iRoot);
            if (iRelationMap.get(bottomPred) == IRelateTwoOperations.ALWAYS_IN_SEQUENCE_21) {
                bottomPred.setSuccessorRelation(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12);
            } else {
                bottomPred.setSuccessorRelation(IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12);
            }
            mSNToolbox.removeNode(iRoot, iSop);
        }

        if (iTopSucc != null) {
            iRoot.setSuccessorNode(iTopSucc);
            iRoot.setSuccessorRelation(iRelationMap.get(iTopSucc));
        }
        mSNToolbox.removeNode(iTopSucc, iSop);

    }

    private void processGroupNode(final ISopNode iNode) {
        if (!(iNode.getNodeType() instanceof OperationData)) {
            //Root is group
            sequence(mSNToolbox.getNodes(iNode, false), iNode);
        }
    }

    private int checkSequenceRelation(final ISopNode iPossiblePredNode, final ISopNode iPossibleSuccNode) {
        Set<OperationData> opPredSet = new HashSet<OperationData>();
        addOperationsToSet(iPossiblePredNode, opPredSet);

        Set<OperationData> opSuccSet = new HashSet<OperationData>();
        addOperationsToSet(iPossibleSuccNode, opSuccSet);

        Set<Integer> relationsToCheckSet = new HashSet<Integer>();
        relationsToCheckSet.add(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12);
        relationsToCheckSet.add(IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12);

        //Operations can be related with any of the relations in the relation set.
//        for (final OperationData opPred : opPredSet) {
//            for (final OperationData opSucc : opSuccSet) {
//                boolean inSequence = false;
//                for (final Integer relationInt : relationsToCheckSet) {
//                    if (mRC.getOperationRelationMap(opPred).get(opSucc).toString().equals(relationInt.toString())) {
//                        inSequence = true;
//                        break;
//                    }
//                }
//                if (!inSequence) {
//                    return -1;
//                }
//            }
//        }
//        return true; //hum?

        //Operations have to be related with the same relation.
        //All relations from the relation set are tested.
        for (final Integer relationInt : relationsToCheckSet) {
            boolean inSequence = true;
            for (final OperationData opPred : opPredSet) {
                for (final OperationData opSucc : opSuccSet) {
                    if (!mRC.getOperationRelationMap(opPred).get(opSucc).toString().equals(relationInt.toString())) {
                        inSequence = false;
                        break;
                    }
                }
                if (!inSequence) {
                    break;
                }
            }
            if (inSequence) {
                return relationInt;
            }
        }
        return -1;
    }

    private void addOperationsToSet(final ISopNode iNode, final Set<OperationData> iSet) {
        if (iNode.getNodeType() instanceof OperationData) {
            final OperationData opData = (OperationData) iNode.getNodeType();
            iSet.add(opData);
        } else {
            final Set<OperationData> opDataSet = mSNToolbox.getOperations(iNode, true);
            iSet.addAll(opDataSet);
        }
    }
}
