package sequenceplanner.model.SOP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;
import sequenceplanner.model.data.OperationData;

/**
 * Creates conditions for {@link OperationData}s based on {@link ISopNode}.
 * @author patrik
 */
public class ConditionsFromSopNode {

    private final ISopNodeToolbox mSopNodeToolbox = new SopNodeToolboxSetOfOperations();

    public ConditionsFromSopNode(final ISopNode iRoot) {
        run(iRoot);
    }

    public boolean run(final ISopNode iRoot) {
        if (!loopNode(iRoot)) {
            return false;
        }
        return true;
    }

    private boolean loopNode(final ISopNode iRoot) {
        for (ISopNode node : iRoot.getFirstNodesInSequencesAsSet()) {

            //Successor(s)-------------------------------------------------------
            while (node != null) {

                //Add condition based on node type-------------------------------
                if (!nodeTypeToCondition(node)) {
                    return false;
                }
                //---------------------------------------------------------------

                //Go through children--------------------------------------------
                if (!loopNode(node)) {
                    return false;
                }
                //---------------------------------------------------------------

                final ISopNode successorNode = node.getSuccessorNode();
                if (successorNode != null) {

                    //Add condition from node to successor node------------------
                    LocalCondition condition = new LocalCondition();
                    if (!getFinishConditionForNode(node, condition)) {
                        return false;
                    }

                    final Set<OperationData> operationSet = new HashSet<OperationData>();
                    if (!findFirstOperationsForNode(successorNode, operationSet)) {
                        return false;
                    }

                    for (final OperationData opData : operationSet) {
                        //Add condition to opData
                        System.out.println(opData.getName() + " precon: " + condition.getmCondition());
                    }
                    //-----------------------------------------------------------
                }

                //Update for next round
                node = successorNode;
            }//------------------------------------------------------------------
        }
        return true;
    }

    private boolean nodeTypeToCondition(final ISopNode iNode) {

        final Object nodeType = iNode.getNodeType();
        final boolean sequenceSetIsEmpty = iNode.getFirstNodesInSequencesAsSet().isEmpty();

        if (nodeType instanceof OperationData) {
            if (sequenceSetIsEmpty) {
                //do nothing
            } else { //node is SOP
                final ISopNode parentNode = iNode; //is an operation
                final OperationData parentOperation = (OperationData) nodeType;
                for (final ISopNode childNode : mSopNodeToolbox.getNodes(parentNode, true)) {

                    final Object childNodeType = childNode.getNodeType();
                    if (childNodeType instanceof OperationData) {
                        final OperationData childOperation = (OperationData) childNodeType;
                        //set relation between parent and child operations
                        System.out.println(parentOperation.getName() + " precon: " + childOperation.getName() + " _i");
                        System.out.println(parentOperation.getName() + " postcon: " + childOperation.getName() + " _f");
                        System.out.println(childOperation.getName() + " precon: " + parentOperation.getName() + " _e");
                        System.out.println(childOperation.getName() + " postcon: " + parentOperation.getName() + " _e");
                    }
                }
            }
        } else if (nodeType instanceof String) {
            final String nodeTypeString = (String) nodeType;
            final String alternative = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", "");
            final String arbitraryOrder = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER, "", "");
            final String parallel = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL, "", "");

            if (nodeTypeString.equals(alternative)) {//--------------------------
                //find operations that are first in each sequence.
                Map<ISopNode, Set<OperationData>> nodeOperationSetMap = new HashMap<ISopNode, Set<OperationData>>();
                for (final ISopNode node : iNode.getFirstNodesInSequencesAsSet()) {
                    final Set<OperationData> operationSet = new HashSet<OperationData>();
                    findFirstOperationsForNode(node, operationSet);
                    nodeOperationSetMap.put(node, operationSet);
                }
                //add condition
                for (final ISopNode altNode : iNode.getFirstNodesInSequencesAsSet()) {
                    final Set<ISopNode> nodesInAlternativeSet = iNode.getFirstNodesInSequencesAsSet();

                    for (final ISopNode otherNode : nodesInAlternativeSet) {
                        for (final OperationData altOperation : nodeOperationSetMap.get(altNode)) {
                            for (final OperationData otherOperation : nodeOperationSetMap.get(otherNode)) {
                                //add precondition to altOperation that otherOperation has to be _i
                                if (!otherNode.equals(altNode)) {
                                    System.out.println(altOperation.getName() + " precon: " + otherOperation.getName() + " _i");
                                }
                            }
                        }
                    }
                }
                //---------------------------------------------------------------
            } else if (nodeTypeString.equals(arbitraryOrder)) {//----------------
                //find operations in each sequence -> Set<Set<OperationData>> externalSet
                final Set<Set<ISopNode>> sequenceNodesSet = new SopNodeToolboxSetOfOperations().getNodesInEachSequence(iNode, true);
                final Set<Set<OperationData>> sequenceOperationSet = new HashSet<Set<OperationData>>();
                for (final Set<ISopNode> nodeSet : sequenceNodesSet) {
                    sequenceOperationSet.add(new SopNodeToolboxSetOfOperations().getOperationsAsSetFromSopNodeSet(nodeSet));
                }
                //add condition
                for (final Set<OperationData> internalSet : sequenceOperationSet) {
                    Set<Set<OperationData>> localSet = new HashSet<Set<OperationData>>(sequenceOperationSet);
                    localSet.remove(internalSet);
                    for (final OperationData opData : internalSet) {
                        for (final Set<OperationData> localLocalSet : localSet) {
                            for (final OperationData localOpData : localLocalSet) {
                                //add precondition to opData that localOpData has to be _i or _f
                                //add postcondition to opData that localOpData has to be _i or _f
                                System.out.println(opData.getName() + " precon: " + localOpData.getName() + " _i OR _f");
                                System.out.println(opData.getName() + " postcon: " + localOpData.getName() + " _i OR _f");
                            }
                        }
                    }
                }
                //---------------------------------------------------------------
            } else if (nodeTypeString.equals(parallel)) {
                //do nothing
            } else {
                System.out.println("nodeTypeToCondition SOP node found is that good?");
                //return false;
            }
        }

        return true;
    }

    private boolean findFirstOperationsForNode(final ISopNode iNode, Set<OperationData> returnSet) {
        final Object nodeType = iNode.getNodeType();

        if (nodeType instanceof OperationData) {
            final OperationData opData = (OperationData) nodeType;
            returnSet.add(opData);
        } else if (nodeType instanceof String) {

            for (final ISopNode node : iNode.getFirstNodesInSequencesAsSet()) {
                if (!findFirstOperationsForNode(node, returnSet)) {
                    return false;
                }
            }
        } else {
            System.out.println("findFirstOperationsForNode Node type in not known");
            return false;
        }
        return true;
    }

    private boolean getFinishConditionForNode(final ISopNode iNode, final LocalCondition returnCondition) {
        final Object nodeType = iNode.getNodeType();

        if (nodeType instanceof OperationData) {
            final OperationData opData = (OperationData) nodeType;
            returnCondition.setmCondition(opData.getName() + "_f");
        } else if (nodeType instanceof String) {
            final String nodeTypeString = (String) nodeType;
            final String alternative = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", "");
            final String arbitraryOrder = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER, "", "");
            final String parallel = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL, "", "");

            for (final ISopNode node : iNode.getFirstNodesInSequencesAsSet()) {
                ISopNode lastNode = mSopNodeToolbox.getBottomSuccessor(node);
                LocalCondition localReturnCondition = new LocalCondition();
                getFinishConditionForNode(lastNode, localReturnCondition);

                if (!returnCondition.isEmpty()) {
                    if (nodeTypeString.equals(parallel) || nodeTypeString.equals(arbitraryOrder)) {
                        returnCondition.addCondition(" AND ");
                    } else if (nodeTypeString.equals(alternative)) {
                        returnCondition.addCondition(" OR ");
                    } else {
                        System.out.println("getFinishConditionForNode String Node type in not known");
                        return false;
                    }
                }
                returnCondition.addCondition(localReturnCondition.getmCondition());
            }
        } else {
            System.out.println("getFinishConditionForNode Node type in not known");
            return false;
        }
        return true;
    }

    public class LocalCondition {

        private String mCondition = "";

        public LocalCondition() {
        }

        public LocalCondition(final String iS) {
            setmCondition(iS);
        }

        public String getmCondition() {
            return mCondition;
        }

        public void setmCondition(String mCondition) {
            this.mCondition = mCondition;
        }

        public void addCondition(String iConditionToAdd) {
            setmCondition(getmCondition() + iConditionToAdd);
        }

        public boolean isEmpty() {
            if (getmCondition().length() > 1) {
                return false;
            }
            return true;
        }
    }
}
