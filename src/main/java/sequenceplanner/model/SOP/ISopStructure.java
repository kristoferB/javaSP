package sequenceplanner.model.SOP;

/**
 * 
 * @author Qw4z1
 */
public interface ISopStructure {
    
    
    
    /**
     * Adds a node to the SOP
     * @param node 
     */
    void addNode(ISopNode node);
    
    /**
     * Adds a node to the root of the SOP
     * @param node 
     */
    void addNodeToRoot(ISopNode node);
    
    /**
     * Adds a node to the end of the sequence
     * @param node 
     */
    void addNodeToSequence(ISopNode node);
}
