package sequenceplanner.model.SOP;

import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class SopNodeOperation extends ASopNode{

    private OperationData mOperation = null;

    private SopNodeOperation() {
        super("operation",0);
    }

    public SopNodeOperation(final OperationData iOperation) {
        super("operation",iOperation.getId());
        this.mOperation = iOperation;
    }

    @Override
    public OperationData getOperation() {
        return mOperation;
    }

    public void setOperation(OperationData mOperation) {
        this.mOperation = mOperation;
    }
    
    
    @Override
    public String toString(){
        return mOperation.getName();
    }
}
