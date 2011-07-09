package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.condition.Condition;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData.CellData;
import sequenceplanner.view.operationView.OperationView;

/**
 * 
 * @author patrik
 */
public class SopNodeToolboxSetOfOperations implements ISopNodeToolbox {

    @Override
    public void drawNode(ISopNode iRootNode, OperationView iView, Set<CellData> iCellDataSet) {
        new DrawSopNode(iRootNode, iView.getGraph(), iCellDataSet);
    }

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
    public Map<OperationData, Map<ConditionType, Condition>> relationsToSelfContainedOperations(ISopNode iRootNode) {
        final ConditionsFromSopNode cfsn = new ConditionsFromSopNode(iRootNode);
        cfsn.printOperationsWithConditions();
        return cfsn.getmOperationConditionMap();
    }

    @Override
    public boolean removeNode(ISopNode iNodeToRemove, ISopNode iRootNode) {
        //Init relations for node to remove
        final ISopNode parentNode = getParentIfNodeIsInSequenceSet(iNodeToRemove, iRootNode);
        final ISopNode predecessorNode = getPredecessor(iNodeToRemove, iRootNode);
        final ISopNode successorNode = iNodeToRemove.getSuccessorNode();

        //Has a parent
        if (parentNode != null) {
            parentNode.getFirstNodesInSequencesAsSet().remove(iNodeToRemove);
            if (successorNode != null) {
                parentNode.addNodeToSequenceSet(successorNode);
            }
            return true;
        }

        //Has a predecessor
        if (predecessorNode != null) {
            predecessorNode.setSuccessorNode(null);
            predecessorNode.setSuccessorRelation(-1);
            if (successorNode != null) {
                predecessorNode.setSuccessorNode(successorNode);
                predecessorNode.setSuccessorRelation(IRelateTwoOperations.OTHER);
            }
            return true;
        }

        //Had nothing
        return false;
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

    @Override
    public ISopNode getPredecessor(ISopNode iSuccessorNode, ISopNode iRootNode) {
        final Set<ISopNode> nodeSet = getNodes(iRootNode, true);
        for (final ISopNode node : nodeSet) {
            final ISopNode successorNode = node.getSuccessorNode();
            if (successorNode != null) {
                if (iSuccessorNode.equals(successorNode)) {
                    return node;
                }
            }
        }
        return null;
    }

    @Override
    public ISopNode getParentIfNodeIsInSequenceSet(ISopNode iNode, ISopNode iRoot) {
        for (ISopNode node : iRoot.getFirstNodesInSequencesAsSet()) {
            //Check node, childnode to root
            if (node.equals(iNode)) {
                return iRoot;
            }

            //Go trough successor (first node included)
            while (node != null) {

                //Check children to childnode
                final ISopNode possibleReturnNode = getParentIfNodeIsInSequenceSet(iNode, node);
                if (possibleReturnNode != null) {
                    return possibleReturnNode;
                }

                node = node.getSuccessorNode(); //Successor to node
            }

        }
        return null;
    }
}
