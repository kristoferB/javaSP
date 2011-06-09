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
                    String condition = "";
                    if (!getFinishConditionForNode(node, condition)) {
                        return false;
                    }

                    final Set<OperationData> operationSet = new HashSet<OperationData>();
                    if(!findFirstOperationsForNode(successorNode, operationSet)) {
                        return false;
                    }

                    for (final OperationData opData : operationSet) {
                        //Add condition to opData
                        System.out.println(opData.getName() + " precon: " + condition);
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
                    nodesInAlternativeSet.remove(altNode);

                    for (final ISopNode otherNode : nodesInAlternativeSet) {
                        for (final OperationData altOperation : nodeOperationSetMap.get(altNode)) {
                            for (final OperationData otherOperation : nodeOperationSetMap.get(otherNode)) {
                                //add precondition to altOperation that otherOperation has to be _i
                                System.out.println(altOperation.getName() + " precon: " + otherOperation.getName() + " _i");
                            }
                        }
                    }
                }
                //---------------------------------------------------------------
            } else if (nodeTypeString.equals(arbitraryOrder)) {//----------------
                //find operations in each sequence -> Set<Set<OperationData>> externalSet
                //add condition
                //for each operation in a set
                //for (final Set<OperationData> internalSet : externalSet)
                //Set<Set<OperationData>> localSet = new HashSet<Set<OperationData>>(externalSet)
                //localSet.remove(internalSet)
                //for (final OperationData opData : internalSet)
                //for (final Set<OperationData> localLocalSet : localSet)
                //for (final OperationData localOpData : localLocalSet)
                //add precondition to opData that localOpData has to be _i or _f
                //add postcondition to opData that localOpData has to be _i or _f
                System.out.println("arbitraryOrder");
                //---------------------------------------------------------------
            } else if (nodeTypeString.equals(parallel)) {
                //do nothing
            } else {
                System.out.println("error in  nodeTypeToCondition");
                return false;
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

    private boolean getFinishConditionForNode(final ISopNode iNode, String returnCondition) {
        final Object nodeType = iNode.getNodeType();

        if (nodeType instanceof OperationData) {
            final OperationData opData = (OperationData) nodeType;
            returnCondition = opData.getName() + "_f";
        } else if (nodeType instanceof String) {
            final String nodeTypeString = (String) nodeType;
            final String alternative = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", "");
            final String arbitraryOrder = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER, "", "");
            final String parallel = RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL, "", "");

            for (final ISopNode node : iNode.getFirstNodesInSequencesAsSet()) {
                ISopNode lastNode = mSopNodeToolbox.getBottomSuccessor(node);
                String localReturnCondition = "";
                getFinishConditionForNode(lastNode, localReturnCondition);

                if (returnCondition.length() > 1) {
                    if (nodeTypeString.equals(parallel) || nodeTypeString.equals(arbitraryOrder)) {
                        returnCondition += "AND";
                    } else if (nodeTypeString.equals(alternative)) {
                        returnCondition += "OR";
                    } else {
                        System.out.println("getFinishConditionForNode String Node type in not known");
                        return false;
                    }
                }
                returnCondition += localReturnCondition;
            }
        } else {
            System.out.println("getFinishConditionForNode Node type in not known");
            return false;
        }
        return true;
    }
}
