package sequenceplanner.multiproduct.InfoInResources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class AOperation {

    OperationData mOperationData;
    Set<Resource> mResourceSet;
    Set<AOperation> mPredecessorSet;

    public AOperation(final OperationData iOperationData) {
        this.mOperationData = iOperationData;
        mResourceSet = new HashSet<Resource>();
        mPredecessorSet = new HashSet<AOperation>();
    }

    abstract Set<String> getPredecessors();

    abstract ConditionElement inOperation();

    private String getInstanceResourceName(final Resource iResource) {
        final int index = iResource.indexOf(this);
        return iResource.mName + "_";
    }

    public Map<String, String> statusOfResourcesAtFinish() {
        final Map<String, String> map = new HashMap<String, String>();
        for (final Resource r : mResourceSet) {
            map.put(LocalModel.variableName(r, this), Integer.toString(r.indexOf(this)));
        }
        return map;
    }

    public boolean equals(Object iObj) {
        if (iObj instanceof AOperation) {
            final AOperation op = (AOperation) iObj;
            if (this.mOperationData.equals(op.mOperationData)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.mOperationData != null ? this.mOperationData.hashCode() : 0);
        hash = 37 * hash + (this.mResourceSet != null ? this.mResourceSet.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return mOperationData.getName();
    }
}
