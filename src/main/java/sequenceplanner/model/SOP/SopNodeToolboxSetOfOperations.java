package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;

/**
 * DOES NOT FOLLOW DESCRIPTIONS FOR METHOD "REMOVE NODE" GIVEN IN INTERFACE!!!<br/>
 * To store operation sets with a SOP.<br/>
 * Each operation is added as first node in sequence set.<br/>
 * @author patrik
 */
public class SopNodeToolboxSetOfOperations implements ISopNodeToolbox {

    @Override
    public void drawNode(ISopNode iRootNode, OperationView iView) {
        new DrawSopNode(iRootNode, iView.getGraph());
    }

    @Override
    public Set<OperationData> getOperations(ISopNode iRootNode, boolean iGoDeep) {
        final Set<OperationData> opSet = new HashSet<OperationData>();
        for (final ISopNode node : getNodes(iRootNode, iGoDeep)) {
            if (node instanceof SopNodeOperation) {
                opSet.add(node.getOperation());
            }
        }
        return opSet;
    }

    /**
     *
     * @param iSubsetToTest
     * @param iSet
     * @return true if "operations in iSubsetToTest" \subseteq "operations in iSet" else false
     */
    public boolean operationsAreSubset(ISopNode iSubsetToTest, ISopNode iSet) {
        final Set<OperationData> opSet = getOperations(iSet, true);
        final Set<OperationData> opSubset = getOperations(iSubsetToTest, true);
        return opSet.containsAll(opSubset);
    }

    @Override
    public void relationsToSelfContainedOperations(ISopNode iRootNode) {
        new ConditionsFromSopNode(iRootNode);
    }

    @Override
    public void removeNode(ISopNode iNodeToRemove, ISopNode iRootNode) {
        if (iNodeToRemove != null && iRootNode != null) {
            iRootNode.getFirstNodesInSequencesAsSet().remove(iNodeToRemove);
        }
    }

    @Override
    public void resolve(ISopNode iRootNode) {
        new ResolveSopNode(iRootNode);
    }

    @Override
    public Set<ISopNode> getNodes(ISopNode iRootNode, boolean iGoDeep) {
        Set<ISopNode> returnSet = new HashSet<ISopNode>();

        //Go through children
        for (ISopNode node : iRootNode.getFirstNodesInSequencesAsSet()) {

            //Go trough successor (first node included)
            while (node != null) {
                returnSet.add(node);

                //Go deep
                if (iGoDeep && !node.sequenceSetIsEmpty()) {
                    returnSet.addAll(getNodes(node, iGoDeep));
                }

                node = node.getSuccessorNode(); //Successor to node
            }

        }

        return returnSet;
    }

    @Override
    public ISopNode getBottomSuccessor(final ISopNode iNode) {
        if (iNode == null) {
            return null;
        }

        ISopNode succ = iNode;

        while (succ.getSuccessorNode() != null) {
            succ = succ.getSuccessorNode();
        }

        return succ;
    }
}
