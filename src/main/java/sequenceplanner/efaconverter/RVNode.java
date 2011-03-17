package sequenceplanner.efaconverter;

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
    int nodeType;
    Cell mCell = null;

    public RVNode() {
    }

    public RVNode(OpNode iOpNode) {
        mOpNode = iOpNode;
    }

    public OperationData getOpData() {
        return (OperationData) mOpNode.getTreeNode().getNodeData();
    }

    public Cell setCell() {
        return setCell(getOpData());
    }
    public Cell setCell(OperationData iOpData) {
        mCell = CellFactory.getInstance().getOperation("operation");
        mCell.setValue(iOpData);
        return mCell;
    }
}
