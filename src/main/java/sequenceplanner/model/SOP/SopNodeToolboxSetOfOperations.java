package sequenceplanner.model.SOP;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 * DOES NOT FOLLOW DESCRIPTIONS FOR METHODS GIVEN IN INTERFACE!!!<br/>
 * To store operation sets with a SOP.<br/>
 * Each operation is added as first node in sequence set.<br/>
 * @author patrik
 */
public class SopNodeToolboxSetOfOperations implements ISopNodeToolbox {

    /**
     * Can only add to sequence for iWhere node;
     * @param iNodeType
     * @param iWhere
     * @return the created node or null if problem
     */
    @Override
    public ISopNode createNode(Object iNodeType, Object iWhere) {

        if (iWhere instanceof ISopNode) {
            ISopNode rootNode = (ISopNode) iWhere;
            ISopNode node = new SopNode();
            node.setNodeType(iNodeType);
            rootNode.addNodeToSequenceSet(node);
            return node;
        }
        return null;
    }

    @Override
    public void drawNode(ISopNode iRootNode, Object iView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Object> getOperations(ISopNode iRootNode) {
        Set<Object> opSet = new HashSet<Object>();
        for (final ISopNode node : iRootNode.getFirstNodesInSequencesAsSet()) {
            if (node.getNodeType() instanceof OperationData) {
                opSet.add(node.getNodeType());
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
        final Set<Object> opSet = getOperations(iSet);
        final Set<Object> opSubset = getOperations(iSubsetToTest);
        return opSet.containsAll(opSubset);
    }

    @Override
    public void preRelationsToSelfContainedOperations(ISopNode iRootNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeNode(ISopNode iNodeToRemove, ISopNode iRootNode) {
        iRootNode.getFirstNodesInSequencesAsSet().remove(iNodeToRemove);
    }

    @Override
    public void resolve(ISopNode iRootNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
