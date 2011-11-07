package sequenceplanner.datamodel.structure;

import java.util.Set;

/**
 * The generic structure interface is used to store products, resources, 
 * and operations. Different types of Structure implementations can be used
 * depending on the purpose of the structure.
 * 
 * All structures though, must have a set of root-nodes. Each root can
 * have a set of children, or groups of children. No loops or circularities are 
 * allowed.
 * 
 * @param <T> A type of the nodes in the structure
 * @param <K> A type describing different child groups
 * @author kbe
 */
public interface Structure<K,T> {
    
    public Set<T> getObjects();
    
    
}
