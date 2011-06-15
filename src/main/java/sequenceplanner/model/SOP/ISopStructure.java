package sequenceplanner.model.SOP;

import sequenceplanner.view.operationView.graphextension.Cell;

/**
 * 
 * @author Qw4z1
 */
public interface ISopStructure {
    
    
    
    /**
     * Adds a node to the SOP
     * @param node 
     */
    //void addNode(ISopNode node);
    
    /**
     * Adds a node to the root of the SOP
     * @param node 
     */
    //void addNodeToRoot(ISopNode node);
    
    /**
     * Adds a node to the end of the sequence
     * @param node 
     */
    //void addNodeToSequence(ISopNode node);

    void setSopSequence(ASopNode sopNode);

    void setSopSequence(Cell cell, ASopNode sopNode, boolean before);

    }
