package sequenceplanner.utils;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author QW4z1
 */
public class Devel {
    private static final Devel instance = new Devel();
    private Devel(){
        
    }
    
    public Devel getInstance(){
        return instance;
    }
    /**
     * Method for printing all keys and values in a map.
     * @param mp Map to print
     */
    public static void dumpMap(Map mp) {
    Iterator it = mp.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        System.out.println(pairs.getKey() + " = " + pairs.getValue());
    }
}
}
