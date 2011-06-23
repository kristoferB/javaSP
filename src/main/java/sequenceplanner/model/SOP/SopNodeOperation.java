package sequenceplanner.model.SOP;

import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class SopNodeOperation extends ASopNode{

    private OperationData mOperation = null;

    public SopNodeOperation(final OperationData iOperation) {
        super("operation");
        this.mOperation = iOperation;
    }

    @Override
    public OperationData getOperation() {
        return mOperation;
    }

    public void setOperation(OperationData mOperation) {
        this.mOperation = mOperation;
    }
}
