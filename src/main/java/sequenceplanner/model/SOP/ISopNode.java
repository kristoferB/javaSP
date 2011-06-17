package sequenceplanner.model.SOP;

import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 * Interface for a SOP node<br/>
 * @author patrik
 */
public interface ISopNode {
    String getTypeAsString();

    OperationData getOperation();

    Set<ISopNode> getFirstNodesInSequencesAsSet();

    void addNodeToSequenceSet(ISopNode iNode);

    ISopNode getSuccessorNode();

    void setSuccessorNode(ISopNode iSuccessor);

    int getSuccessorRelation();

    void setSuccessorRelation(int iRelation);

    boolean sequenceSetIsEmpty();

    String typeToString();

    String inDepthToString();

    String inDepthToString(String prefix);

    public int getUniqueId();
    
}
