package sequenceplanner.model.SOP;

import java.util.Set;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.model.data.OperationData;

/**
 * Interface for a SOP node<br/>
 * @author patrik
 */
public interface ISopNode {

    public SopNodeInfoPointer getNodeInfo();
    public Set<ISopNode> getSequencesAsSet();
    public ISopNode getPredecessorNode();
    public ISopNode getSuccessorNode();
    public ISopNode createNode();
    public ISopNode addNode(OperationData iOperationData);
    public ISopNode addNode(OpNode iOpNode);
}
