package sequenceplanner.datamodel.product;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author kbe
 */
public final class Seam {
    private final String name;
    private final Set<String> blocks;

    public Seam(String name, Set<String> blocks) {
        this.name = name;
        this.blocks = new HashSet();
        for (String s : blocks)
            this.blocks.add(s);
    }
    
    public String getName(){
        return name;
    }
    
    public Set<String> getBlocks(){
        Set<String> result = new HashSet<String>();
        for (String s: blocks){
            result.add(s);
        }
        return result;
    }
    
}
