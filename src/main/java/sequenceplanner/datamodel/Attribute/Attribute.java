package sequenceplanner.datamodel.Attribute;

import java.util.UUID;

/**
 *
 * @author kbe
 */
public interface Attribute {
    
    /**
     * Returns the unique identifier of the attribute
     * @return {@link UUID}
     */
    public UUID getID();
    
    
    /**
     * Two Attributes are equal if the unique identifier is the same and 
     * the contents are the same. 
     * 
     * 
     * @param obj 
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj);
    
   
    
    /**
     * A hash code.
     * 
     * @return the hashcode
     */
    @Override
    public int hashCode();
    
}
