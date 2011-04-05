package sequenceplanner.model.SOP;

import java.util.Set;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.model.data.OperationData;

/**
 * Interface for a SOP node<br/>
 * @author patrik
 */
public interface ISopNode {

    //Node types
    String OPERATION = "operation";
    String GROUP = "group";
    String HIERARCHY = "hirearchy";
    String ALTERNATIVE = "alternative";
    String ARBITRARY = "arbitrary";
    String PARALLEL = "parallel";

    public SopNodeInfoPointer getNodeInfo();
    public Set<ISopNode> getSequencesAsSet();
    public ISopNode getPredecessorNode();
    public ISopNode getSuccessorNode();
    
    public ISopNode addNode(OperationData iOperationData);
    public ISopNode addNode(OpNode iOpNode);

    public ISopNode createNode(final String iType);

    public boolean insertNode(final ISopNode iNode, final Object iWhere);

    /**
     * Checks if all operations given in iSopNode are present in this SOP node.<br/>
     * @param iSopNode operations to test
     * @return true if operations in iSopNode \subseteq operation in this SOPnode
     */
    public boolean containsAllOperations(final ISopNode iSopNode);

    public boolean toSelfContainedOperations();

    /**
     * Remove unnecessary nodes in node.<br/>
     * 
     * @return
     */
    public boolean resolve();

    public boolean generateView();
}
