package sequenceplanner.multiProduct.summer2011;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.condition.ConditionElement;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
abstract class AOperation {

    OperationData mOperationData;
    Set<Resource> mResourceSet;

    public AOperation(final OperationData iOperationData) {
        this.mOperationData = iOperationData;
        mResourceSet = new HashSet<Resource>();
    }

    abstract Set<AOperation> getPredecessors();

    abstract ConditionElement inOperation();

    private String getInstanceResourceName(final Resource iResource) {
        return iResource.mName + "_";
    }

    @Override
    public boolean equals(Object iObj) {
        if (iObj instanceof AOperation) {
            final AOperation op = (AOperation) iObj;
            if (this.mOperationData.equals(op.mOperationData)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.mOperationData != null ? this.mOperationData.hashCode() : 0);
        hash = 37 * hash + (this.mResourceSet != null ? this.mResourceSet.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return mOperationData.getName();
    }


}
