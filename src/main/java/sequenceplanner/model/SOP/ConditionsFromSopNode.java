package sequenceplanner.model.SOP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 * Creates conditions for {@link OperationData}s based on {@link ISopNode}.
 * @author patrik
 */
public class ConditionsFromSopNode {

    private final ISopNodeToolbox mSopNodeToolbox = new SopNodeToolboxSetOfOperations();

    public ConditionsFromSopNode() {
    }

    public void run() {
        //Go through root node
        //Find first operations for each node
        //Find finish condition for each node
        //Go through root node
        //
    }

    private void loopNode(final ISopNode iRoot) {
        for (ISopNode node : iRoot.getFirstNodesInSequencesAsSet()) {

            //Successor(s)-------------------------------------------------------
            while (node != null) {

                //Add condition based on node type-------------------------------
                nodeTypeToCondition(node);
                //---------------------------------------------------------------

                //Go through children--------------------------------------------
                loopNode(node);
                //---------------------------------------------------------------

                final ISopNode successorNode = node.getSuccessorNode();
                if (successorNode != null) {

                    //Add condition from node to successor node------------------
                    String condition = "";
                    getFinishConditionForNode(node, condition);

                    final Set<OperationData> operationSet = new HashSet<OperationData>();
                    findFirstOperationsForNode(successorNode, operationSet);

                    for (final OperationData opData : operationSet) {
                        //Add condition to opData
                    }
                    //-----------------------------------------------------------
                }

                //Update for next round
                node = successorNode;
            }//------------------------------------------------------------------
        }
    }

    private void nodeTypeToCondition(final ISopNode iNode) {
        // if node is operation
        //do nothing

        //else if node is SOP
        final ISopNode parentNode = iNode; //is an operation
        for (final ISopNode childNode : mSopNodeToolbox.getNodes(parentNode, true)) {
            //if childNode is of type operation
            //set relation between parent and child operations
        }

        //else if node is parallel
        //do nothing

        //else if node is alternative--------------------------------------------
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
                for(final OperationData altOperation : nodeOperationSetMap.get(altNode)) {
                    for(final OperationData otherOperation : nodeOperationSetMap.get(otherNode)) {
                        //add precondition to altOperation that otherOperation has to be _i
                    }
                }
            }
        }
        //-----------------------------------------------------------------------

        //else if node is arbitrary order----------------------------------------
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
        //-----------------------------------------------------------------------


    }

    private void findFirstOperationsForNode(final ISopNode iNode, Set<OperationData> returnSet) {
        // if iNode is operation or SOP -> add operation to returnSet

        //else
        for (final ISopNode node : iNode.getFirstNodesInSequencesAsSet()) {
            findFirstOperationsForNode(node, returnSet);
        }
    }

    private void getFinishConditionForNode(final ISopNode iNode, String returnCondition) {
        // if iNode is operation -> returnCondition == iNode/operation _f

        //else
        for (final ISopNode node : iNode.getFirstNodesInSequencesAsSet()) {
            ISopNode lastNode = mSopNodeToolbox.getBottomSuccessor(node);
            String localReturnCondition = "";
            getFinishConditionForNode(lastNode, localReturnCondition);
            //if iNode is parallel or arbitrary order
            //and localReturnCondition to returnCondition
            //else
            //or localReturnCondition to returnCondition
        }
    }
}
