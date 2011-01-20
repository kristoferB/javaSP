package sequenceplanner.multiProduct;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author patrik
 */
public class InternalOpDatas extends HashSet<InternalOpData> {

    public Set<String> opNames() {
        Set<String> objects = new HashSet<String>();
        for (InternalOpData iData : this) {
            objects.add(iData.getName());
        }
        return objects;
    }

    public Set<Integer> opIDs() {
        Set<Integer> objects = new HashSet<Integer>();
        for (InternalOpData iData : this) {
            objects.add(iData.getId());
        }
        return objects;
    }

    public InternalOpData getOpWithID(Integer id) {
        for (InternalOpData iData : this) {
            if (iData.getId() == id) {
                return iData;
            }
        }
        return null;
    }

    public void setParentChildrenRelations() {
        Set<Integer> idSet = opIDs();
        for (InternalOpData iData : this) {
            if (idSet.contains(iData.parentId)) {
                InternalOpData parent = getOpWithID(iData.parentId);

                iData.parent = parent;
                parent.children.add(iData);
            }
        }
    }

    public InternalOpDatas getChildOperations() {
        InternalOpDatas set = new InternalOpDatas();
        for (InternalOpData iData : this) {
            if (!iData.isParent()) {
                set.add(iData);
            }
        }
        return set;
    }

    public InternalOpDatas getMovers() {
        InternalOpDatas set = new InternalOpDatas();
        for (InternalOpData iData : this) {
            if (iData.attributes.get(TypeVar.ED_MOVER) != null) {
                set.add(iData);
            }
        }
        return set;
    }
}
