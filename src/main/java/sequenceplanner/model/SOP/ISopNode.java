package sequenceplanner.model.SOP;

import java.util.Set;
import sequenceplanner.algorithms.visualization.IRelationContainer;

/**
 * Interface for a SOP node<br/>
 * @author patrik
 */
public interface ISopNode {

    Object getNodeType();

    void setNodeType(Object iType);

    Set<ISopNode> getFirstNodesInSequencesAsSet();

    void addNodeToSequenceSet(ISopNode iNode);

    ISopNode getPredecessorNode();

    void setPredecessorNode(ISopNode iPredecessor);

    ISopNode getSuccessorNode();

    void setSuccessorNode(ISopNode iSuccessor);

    int getSuccessorRelation();

    void setSuccessorRelation(int iRelation);

    String typeToString();

    String inDepthToString();

    String inDepthToString(String prefix, IRelationContainer iRC);
}
