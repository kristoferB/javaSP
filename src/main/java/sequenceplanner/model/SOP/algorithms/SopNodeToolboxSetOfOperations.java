package sequenceplanner.model.SOP.algorithms;

import sequenceplanner.model.SOP.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData.CellDataLayout;
import sequenceplanner.view.operationView.graphextension.SPGraph;

/**
 * 
 * @author patrik
 */
public class SopNodeToolboxSetOfOperations implements ISopNodeToolbox {

    @Override
    public void drawNode(SopNode iRootNode, SPGraph iGraph, Map<SopNode, CellDataLayout> iCellDataMap) {
        new DrawSopNode(iRootNode, iGraph, iCellDataMap);
    }

    @Override
    public void drawNode(SopNode iRootNode, SPGraph iGraph) {
        new DrawSopNode(iRootNode, iGraph);
    }

    @Override
    public Set<OperationData> getOperations(SopNode iRootNode, boolean iGoDeep) {
        final Set<OperationData> opSet = new HashSet<OperationData>();
        for (final SopNode node : getNodes(iRootNode, iGoDeep)) {
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
    public boolean operationsAreSubset(SopNode iSubsetToTest, SopNode iSet) {
        final Set<OperationData> opSet = getOperations(iSet, true);
        final Set<OperationData> opSubset = getOperations(iSubsetToTest, true);
        return opSet.containsAll(opSubset);
    }

    @Override
    public Map<OperationData, Map<ConditionType, Condition>> relationsToSelfContainedOperations(SopNode iRootNode) {
        final ConditionsFromSopNode cfsn = new ConditionsFromSopNode(iRootNode);
        cfsn.printOperationsWithConditions();
        return cfsn.getmOperationConditionMap();
    }

    @Override
    public boolean removeNode(SopNode iNodeToRemove, SopNode iRootNode) {
        //Init relations for node to remove
        final SopNode parentNode = getParentIfNodeIsInSequenceSet(iNodeToRemove, iRootNode);
        final SopNode predecessorNode = getPredecessor(iNodeToRemove, iRootNode);
        final SopNode successorNode = iNodeToRemove.getSuccessorNode();

        //Has a parent
        if (parentNode != null) {
            parentNode.removeFromSequenceSet(iNodeToRemove);
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
    public void resolve(SopNode iRootNode) {
        new ResolveSopNode(iRootNode);
    }

    @Override
    public Set<SopNode> getNodes(SopNode iRootNode, boolean iGoDeep) {
        Set<SopNode> returnSet = new HashSet<SopNode>();

        //Go through children
        for (SopNode node : iRootNode.getFirstNodesInSequencesAsSet()) {

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
    public SopNode getBottomSuccessor(final SopNode iNode) {
        if (iNode == null) {
            return null;
        }

        SopNode succ = iNode;

        while (succ.getSuccessorNode() != null) {
            succ = succ.getSuccessorNode();
        }

        return succ;
    }

    @Override
    public SopNode getPredecessor(SopNode iSuccessorNode, SopNode iRootNode) {
        final Set<SopNode> nodeSet = getNodes(iRootNode, true);
        for (final SopNode node : nodeSet) {
            final SopNode successorNode = node.getSuccessorNode();
            if (successorNode != null) {
                if (iSuccessorNode.equals(successorNode)) {
                    return node;
                }
            }
        }
        return null;
    }

    @Override
    public SopNode getParentIfNodeIsInSequenceSet(SopNode iNode, SopNode iRoot) {
        for (SopNode node : iRoot.getFirstNodesInSequencesAsSet()) {
            //Check node, childnode to root
            if (node.equals(iNode)) {
                return iRoot;
            }

            //Go trough successor (first node included)
            while (node != null) {

                //Check children to childnode
                final SopNode possibleReturnNode = getParentIfNodeIsInSequenceSet(iNode, node);
                if (possibleReturnNode != null) {
                    return possibleReturnNode;
                }

                node = node.getSuccessorNode(); //Successor to node
            }

        }
        return null;
    }
}
