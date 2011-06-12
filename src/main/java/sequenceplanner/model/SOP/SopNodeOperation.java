package sequenceplanner.model.SOP;

import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class SopNodeOperation extends ASopNode{

    private OperationData mOperation = null;

    private SopNodeOperation() {
        super("operation");
    }

    public SopNodeOperation(final OperationData iOperation) {
        this();
        setOperation(iOperation);
    }

    @Override
    public OperationData getOperation() {
        return mOperation;
    }

    public void setOperation(OperationData mOperation) {
        this.mOperation = mOperation;
    }
}
