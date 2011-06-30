package sequenceplanner.multiProduct.summer2011;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author patrik
 */
class LocalModel {

    Set<ProductType> mProductTypeSet;
    Set<Resource> mResourceSet;

    public LocalModel() {
        mProductTypeSet = new HashSet<ProductType>();
        mResourceSet = new HashSet<Resource>();
    }

    public boolean addOperationToProductType(final AOperation iOp, final String iProductTypeName) {
        //Check if new operation
        if (operationExists(iOp) || iProductTypeName == null) {
            return false;
        }

        //Get product type
        final ProductType pt = getProductType(iProductTypeName);

        //Add operation
        pt.mOperationSet.add(iOp);

        return true;
    }

    public void addResourcesToOperation(final Set<String> iResourceSet, final AOperation iOp) {
        for (final String resource : iResourceSet) {
            //Get resource
            final Resource r = getResource(resource);

            //Add resource
            iOp.mResourceSet.add(r);

            //Add operation
            r.mOperationList.add(iOp);
        }
    }

    public void printProductTypesAndOperations() {
        for (final ProductType pt : mProductTypeSet) {
            System.out.println(pt);
            for(final AOperation o : pt.mOperationSet) {
                System.out.println(o);
            }
        }
    }

    private ProductType getProductType(final String iName) {
        for (final ProductType pt : mProductTypeSet) {
            if (iName.equals(pt.mName)) {
                return pt;
            }
        }
        return addProductType(iName);
    }

    private ProductType addProductType(final String iName) {
        final ProductType pt = new ProductType(iName);
        mProductTypeSet.add(pt);
        return pt;
    }

    private Resource getResource(final String iName) {
        for (final Resource r : mResourceSet) {
            if (iName.equals(r.mName)) {
                return r;
            }
        }
        return addResource(iName);
    }

    private Resource addResource(final String iName) {
        final Resource r = new Resource(iName);
        mResourceSet.add(r);
        return r;
    }

    private boolean operationExists(final AOperation iOp) {
        for (final ProductType pt : mProductTypeSet) {
            for (final AOperation op : pt.mOperationSet) {
                if (op.mOperationData.equals(iOp.mOperationData)) {
                    return true;
                }
            }
        }
        return false;
    }
}
