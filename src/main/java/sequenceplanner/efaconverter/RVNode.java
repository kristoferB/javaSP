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

    OpNode mOpNode = null;
    Set<RVNode> mChildren = new HashSet<RVNode>();
    RVNode mParent = null;
    String mNodeType = "";
    Cell mCell = null;
    /**
     * (R)elation (V)iew NODE<br/>
     * Relations to other operations (their locations).<br/>
     * Outside keyset = {"up" (init->exec), "down" (exec->finish)}.<br/>
     * Inside keyset = {{@link RVNode}} for all operations in project.<br/>
     * Inside valueset = {0,1,2,01,02,12,012}
     */
    HashMap<String, HashMap<RVNode, Set<String>>> mEventOperationLocationSetMap = new HashMap<String, HashMap<RVNode, Set<String>>>(2);

    HashMap<RVNode, Integer> mOperationRelationMap = new HashMap<RVNode, Integer>();

    public RVNode(OpNode iOpNode) {
        mOpNode = iOpNode;
    }

    public HashMap<RVNode, Integer> getOperationRelationSubSetMap(Set<RVNode> iSubSet) {
        HashMap<RVNode, Integer> operationRelationSubSetMap = new HashMap<RVNode, Integer>();
        for (RVNode rvNode : iSubSet) {
            operationRelationSubSetMap.put(rvNode, mOperationRelationMap.get(rvNode));
        }
        return operationRelationSubSetMap;
    }

    public OperationData getOpData() {
        return (OperationData) mOpNode.getTreeNode().getNodeData();
    }

    public RVNode getChildWithStringId(String iId) {
        for (RVNode rvNode : mChildren) {
            if(rvNode.mOpNode.getStringId().equals(iId)) {
                return rvNode;
            }
            if (!rvNode.mChildren.isEmpty()) {
                RVNode tempNode = rvNode.getChildWithStringId(iId);
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
        mCell = CellFactory.getInstance().getOperation(mNodeType);
        mCell.setValue(iOpData);
        return mCell;
    }

    public Integer getRelationToNode(RVNode iRvNode) {
        RelateTwoOperations r = new RelateTwoOperations(this, iRvNode);
        return r.getOperationRelation();
    }

    public boolean isParent() {
        if(!mNodeType.equals(RVNodeToolbox.OPERATION)) {
            return false;
        }
        if(!mOperationRelationMap.containsValue(RelateTwoOperations.HIERARCHY_12)) {
            return false;
        }
        return true;
    }

    public String getName() {
        if(mOpNode == null) {
            return null;
        }
        return mOpNode.getName();
    }

    @Override
    public String toString() {
        return getOpData().getName();
    }
}
