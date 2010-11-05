package sequenceplanner.model;

import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author Erik Ohlson, erik.a.ohlson@gmail.com
 */
public class NameCacheMap {

    private TreeMap<Integer, String[]> nameCache;

    private NameCacheMap parent = null;

    public NameCacheMap() {
        nameCache = new TreeMap<Integer, String[]>();
    }

    public void put(Integer id, String path, String name) {
        nameCache.put(id, new String[] { path, name } );
    }

    public void remove(Integer id) {
        nameCache.remove(id);
    }

    public String[] get(Integer id) {
        String[] out = nameCache.get(id);

        if (out == null && parent != null) {
            out = parent.get(id);
        }
        return out;
    }

    public void setParent(NameCacheMap parent) {
        this.parent = parent;
    }

    public NameCacheMap getParent() {
        return parent;
    }

    public void clearMap() {
       nameCache.clear();
    }

    @Override
    public String toString() {
        String output = "";
        
        Iterator it = nameCache.keySet().iterator();

        while (it.hasNext()) {
            String[] st = nameCache.get(it.next());
            output +=  "\n\t" + "Path: " + st[0] + "\t Name: " + st[1];
        }

        return output;
    }



}
