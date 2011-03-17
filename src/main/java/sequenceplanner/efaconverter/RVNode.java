package sequenceplanner.efaconverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;

/**
 * Wrapper class for operations and groups used for visualization.<br/>
 * A tree is successive built up to handle <i>hierachy</i>, <i>alternative</i>,
 * <i>arbitrary order</i>, <i>parallel</i>, and <i>sequencing</i> between the tree nodes.<br/>
 * Modifications on tree is down through {@link RVNodeToolbox}
 * @author patrik
 */
public class RVNode {

    OpNode mOpNode;
    Set<RVNode> mChildren = new HashSet<RVNode>();
    RVNode mParent;
    String nodeType = "";
    Cell mCell = null;
    /**
     * (R)elation (V)iew NODE<br/>
     * Relations to other operations (their locations).<br/>
     * Outside keyset = {"up" (init->exec), "down" (exec->finish)}.<br/>
     * Inside keyset = {"o" + operation id} for all operations in project.<br/>
     * Inside valueset = {0,1,2,01,02,12,012}
     */
    HashMap<String, HashMap<String, Set<String>>> mEventOperationLocationSetMap = new HashMap<String, HashMap<String, Set<String>>>(2);

    public RVNode() {
    }

    public RVNode(OpNode iOpNode) {
        mOpNode = iOpNode;
    }

    public OperationData getOpData() {
        return (OperationData) mOpNode.getTreeNode().getNodeData();
    }

    public RVNode getChildWithId(Integer iId) {
        for (RVNode rvNode : mChildren) {
            if(rvNode.mOpNode.getStringId().equals(iId.toString())) {
                return rvNode;
            }
            if (!rvNode.mChildren.isEmpty()) {
                RVNode tempNode = rvNode.getChildWithId(iId);
                if (tempNode != null) {
                    return tempNode;
                }
            }
        }
        return null;
    }

    public Cell setCell() {
        return setCell(getOpData());
    }
    public Cell setCell(OperationData iOpData) {
        mCell = CellFactory.getInstance().getOperation(nodeType);
        mCell.setValue(iOpData);
        return mCell;
    }
}
