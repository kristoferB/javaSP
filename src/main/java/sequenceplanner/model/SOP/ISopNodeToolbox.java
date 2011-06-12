package sequenceplanner.model.SOP;

import java.util.Set;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;

/**
 * Functions to be performed on a {@link ISopNode}
 * @author patrik
 */
public interface ISopNodeToolbox {

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
    public void drawNode(ISopNode iRootNode, OperationView iView);

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
     * Get all nodes of type {@link OperationData} recursively from sequences to iRootNode.<br/>
     * E.g.:---------------<br/>
     * node1: type operation, sequence set {node2}<br/>
     * node2: type parallel, sequence set {node3,node6}<br/>
     * node6: type operation, sequence set {}<br/>
     * node3: type parallel, sequence set {node4,node5}<br/>
     * node4: type operation, sequence set {}<br/>
     * node5: type operation, sequence set {}<br/>
     * getOperations(node1,true) gives:<br/>
     * {operation in node5, op in node4, op in node6}<br/>
     * --------------------<br/>
     * @param iRootNode container for sequences where operation should be picked
     * @param iGoDeep true = check all children, false = check no children
     * @return the set of operations found
     */
    Set<OperationData> getOperations(ISopNode iRootNode, boolean iGoDeep);

    /**
     * Relations in SOP added to as conditions for (selfcontained) operation data.<br/>
     * @param iRootNode container for sequences where conditions should be found
     */
    public void relationsToSelfContainedOperations(ISopNode iRootNode);

    /**
     * Get all {@link ISopNode} nodes recursively from sequences to iRootNode.<br/>
     * E.g.:---------------<br/>
     * node1: type operation, sequence set {node2}<br/>
     * node2: type parallel, sequence set {node3,node6}<br/>
     * node6: type operation, sequence set {}<br/>
     * node3: type parallel, sequence set {node4,node5}<br/>
     * node4: type operation, sequence set {}<br/>
     * node5: type operation, sequence set {}<br/>
     * getNodes(node1,false) gives:<br/>
     * {node2}<br/>
     * --------------------<br/>
     * @param iRootNode container for sequences where operation should be picked
     * @param iGoDeep true = check all children, false = check no children
     * @return the set of nodes found
     */
    Set<ISopNode> getNodes(ISopNode iRootNode, boolean iGoDeep);

    /**
     * To get the last {@link ISopNode} in a successor sequence.<br/>
     * @param iNode start node
     * @return last {@link ISopNode} in the successor sequence (this can be parameter iNode), or null if 1) parameter iNode ==null
     */
    ISopNode getBottomSuccessor(ISopNode iNode);
}
