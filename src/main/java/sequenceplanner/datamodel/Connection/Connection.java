package sequenceplanner.datamodel.Connection;

import java.util.UUID;

/**
 *
 * @author kbe
 */
public interface Connection {
    
    /**
     * Returns the unique identifier of the connection
     * @return {@link UUID}
     */
    public UUID getID();
    
    /**
     * Two Connections are equal if the unique identifier is the same and 
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
