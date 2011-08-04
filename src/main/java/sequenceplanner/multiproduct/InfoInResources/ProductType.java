package sequenceplanner.multiproduct.InfoInResources;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author patrik
 */
class ProductType {

    String mName;
    Set<AOperation> mOperationSet;

    public ProductType(final String iName) {
        this.mName = iName;
        mOperationSet = new HashSet<AOperation>();
    }

    @Override
    public String toString() {
        return mName;
    }

    public Set<AOperation> getFirstOperations() {
        final Set<AOperation> returnSet = new HashSet<AOperation>();
        for (final AOperation op : mOperationSet) {
            if (op.mPredecessorSet.isEmpty()) {
                returnSet.add(op);
            }
        }
        return returnSet;
    }

    public Set<AOperation> getLastOperations() {
        
        //Find operations that are in preconditions
        final Set<AOperation> invReturnSet = new HashSet<AOperation>();
        for (final AOperation op : mOperationSet) {
                invReturnSet.addAll(op.mPredecessorSet);
        }

        //Only operations from this product type
        invReturnSet.retainAll(mOperationSet);

        //Return operations from this product type not found in any precondition
        final Set<AOperation> returnSet = new HashSet<AOperation>(mOperationSet);
        returnSet.removeAll(invReturnSet);
        return returnSet;
    }
}
