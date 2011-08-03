package sequenceplanner.multiproduct.summer2011;

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
}
