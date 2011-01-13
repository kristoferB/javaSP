
package sequenceplanner.multiProduct;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author patrik
 */
public class InternalOpDatas extends HashSet<InternalOpData>{

    public Set<String> opNames() {
        Set<String> objects = new HashSet<String>();
        for(InternalOpData iData : this) {
            objects.add(iData.getName());
        }
        return objects;
    }
    public Set<Integer> opIDs() {
        Set<Integer> objects = new HashSet<Integer>();
        for(InternalOpData iData : this) {
            objects.add(iData.getId());
        }
        return objects;
    }

    public InternalOpData getOpWithID(Integer id) {
        for(InternalOpData iData : this) {
            if(iData.getId() == id) {
                return iData;
            }
        }
        return null;
    }

}
