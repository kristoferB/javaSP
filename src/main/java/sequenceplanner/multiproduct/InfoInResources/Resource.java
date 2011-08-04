package sequenceplanner.multiproduct.InfoInResources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
*
* @author patrik
*/
class Resource {

    String mName;
    List<AOperation> mOperationList;

    public Resource(String iName) {
        this.mName = iName;
        mOperationList = new ArrayList<AOperation>();
    }

    /**
     *
     * @param iOperation
     * @return -1 if not in list
     */
    public int indexOf(final AOperation iOperation) {
        return mOperationList.indexOf(iOperation);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Resource(mName);
    }

    @Override
    public boolean equals(Object iObj) {
        if (iObj instanceof Resource) {
            final Resource resource = (Resource) iObj;
            if (this.mName.equals(resource.mName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.mName != null ? this.mName.hashCode() : 0);
        return hash;
    }

    public static Set<Resource> cloneSet(final Set<Resource> iSet) {
        final Set<Resource> cloneSet = new HashSet<Resource>();
        for (final Resource r : iSet) {
            try {
                cloneSet.add((Resource) r.clone());
            } catch (CloneNotSupportedException c) {
                System.out.println(c);
                return cloneSet;
            }
        }
        return cloneSet;
    }

    @Override
    public String toString() {
        return mName;
    }


}