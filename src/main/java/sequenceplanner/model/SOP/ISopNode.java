package sequenceplanner.model.SOP;

import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 * Interface for a SOP node<br/>
 * @author patrik
 */
public interface ISopNode {

    OperationData getOperation();

    Set<ISopNode> getFirstNodesInSequencesAsSet();

    void addNodeToSequenceSet(ISopNode iNode);

    ISopNode getSuccessorNode();

    void setSuccessorNode(ISopNode iSuccessor);

    int getSuccessorRelation();

    void setSuccessorRelation(int iRelation);

    boolean sequenceSetIsEmpty();

    /**
     * Only works this node.
     * @return operation name or node type as symbol
     */
    String typeToString();

    /**
     * For internal use only
     * @return
     */
    String inDepthToString();

    /**
     * For internal use only
     * @return
     */
    String inDepthToString(String prefix);
    
}
