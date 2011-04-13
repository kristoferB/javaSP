package sequenceplanner.model.SOP;

import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 * Functions to be performed on a {@link ISopNode}
 * @author patrik
 */
public interface ISopNodeToolbox {

    /**
     * To create a new node<br/>
     * Not sure on parameter iWhere, modify for better solution.<br/>
     * No node should be created if some other node in the sequence where this node should be added already points to parameter iOperation.<br/>
     * E.g.:---------------<br/>
     * node1->node2. node3 should be created after node2.<br/>
     * node1 points to some operation op1 and node3 also points to operation op1.<br/>
     * This gives: the sequence should NOT be extended to node1->node2->node3.<br/>
     * null is returned.<br/>
     * --------------------<br/>
     * @param iNodeType see {@link ISopNodeType}
     * @param iWhere Before or after som other node, or as the first node in a sequence, CHANGE TO FIT WHAT IS BEST
     * @return the created {@link ISopNode} or null if no node was created
     */
    public ISopNode createNode(Object iNodeType, Object iWhere);

    /**
     * Removes a node and all sequence nodes to this node.<br/>
     * E.g.:---------------<br/>
     * node1 has sequence set {node2,node3} and node6 as predecessor.<br/>
     * node2 has sequecne set {node4} and node5 as successor.<br/>
     * removeNode(node2) gives:<br/>
     * node1 has sequence set {node5,node3} and node6 as prececessor.<br/>
     * --------------------<br/>
     * @param iNodeToRemove node to remove
     * @param iRootNode container for sequences where node can be found
     */
    public void removeNode(ISopNode iNodeToRemove, ISopNode iRootNode);

    /**
     * Draw all nodes that are in any of iRootNode's sequences and iteratively sequences for nodes in the sequences.<br/>
     * Use mxGraph/JGraph.<br/>
     * Each node type has its own representation. see SP1.0<br/>
     * @param iRootNode container for sequences to be drawn, CHANGE TO FIT WHAT IS BEST
     * @param iView a view already exists or null if a new view should be created.
     */
    public void drawNode(ISopNode iRootNode, Object iView);

    /**
     * Remove unnecessary nodes recursively in sequences to iRootNode.<br/>
     * E.g.:---------------<br/>
     * node1: type operation, sequence set {node2,node3,node4}<br/>
     * node2: type alternative, sequence set {node5,node6}, node8 as successor<br/>
     * node3: type alternative, sequence set {node6,node7}, node8 as successor<br/>
     * node4: type alternative, sequence set {node7,node5}, node8 as successor<br/>
     * resolve(node1) gives:<br/>
     * node1: type operation, sequence set {node2}<br/>
     * node2: type alternative, sequence set {node5,node6,node7}, node8 as successor<br/>
     * --------------------<br/>
     * E.g.:---------------<br/>
     * node1: type operation, sequence set {node2,node3,node4}<br/>
     * node2: type alternative, sequence set {node5,node6}, node8 as successor<br/>
     * node3: type alternative, sequence set {node6,node7}, node9 as successor<br/>
     * node4: type alternative, sequence set {node7,node5}, node10 as successor<br/>
     * resolve(node1) gives:<br/>
     * no change<br/>
     * --------------------<br/>
     * E.g.:---------------<br/>
     * node1: type operation, sequence set {node2}<br/>
     * node2: type parallel, sequence set {node3,node6}<br/>
     * node6: type operation, sequence set {}<br/>
     * node3: type parallel, sequence set {node4,node5}<br/>
     * node4: type operation, sequence set {}<br/>
     * node5: type operation, sequence set {}<br/>
     * resolve(node1) gives:<br/>
     * node1: type operation, sequence set {node2}<br/>
     * node2: type parallel, sequence set {node6,node4,node5}<br/>
     * node4: type operation, sequence set {}<br/>
     * node5: type operation, sequence set {}<br/>
     * node6: type operation, sequence set {}<br/>
     * --------------------<br/>
     * @param iRootNode container for sequences to be resolved
     */
    public void resolve(ISopNode iRootNode);

    /**
     * Get all nodes of type operation recursively from sequences to iRootNode.<br/>
     * E.g.:---------------<br/>
     * node1: type operation, sequence set {node2}<br/>
     * node2: type parallel, sequence set {node3,node6}<br/>
     * node6: type operation, sequence set {}<br/>
     * node3: type parallel, sequence set {node4,node5}<br/>
     * node4: type operation, sequence set {}<br/>
     * node5: type operation, sequence set {}<br/>
     * getOperations(node1) gives:<br/>
     * {node5,node4,node6}<br/>
     * --------------------<br/>
     * @param iRootNode container for sequences where operation should be picked
     * @return the set of operations found
     */
    public Set<OperationData> getOperations(ISopNode iRootNode);

    /**
     * Relations in SOP added to as conditions for (selfcontained) operation data.<br/>
     * @param iRootNode container for sequences where conditions should be found
     */
    public void relationsToSelfContainedOperations(ISopNode iRootNode);
}
