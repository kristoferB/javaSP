package sequenceplanner.model.SOP;

import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.model.data.OperationData;

/**
 * A {@link ISopNode} points to several classes for information about the node.<br/>
 * These pointers are collected in this class<br/>
 * @author patrik
 */
public class SopNodeInfoPointer {

    public String mNodeType = "";

    public OperationData mOperationData = null;
    public OpNode mOpNode = null;

    public SopNodeInfoPointer() {
    }

    public String getmNodeType() {
        return mNodeType;
    }

    public void setmNodeType(final String iNodeType) {
        this.mNodeType = iNodeType;
    }

    public OperationData getmOperationData() {
        return mOperationData;
    }

    public void setmOperationData(final OperationData iOperationData) {
        this.mOperationData = iOperationData;
    }

    public OpNode getmOpNode() {
        return mOpNode;
    }

    public void setmOpNode(final OpNode iOpNode) {
        this.mOpNode = iOpNode;
    }



}
