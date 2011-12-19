package sequenceplanner.datamodel.operation;

import java.util.UUID;

/**
 * Each operation has a unique identity represented by this interface. 
 * Impl must be immutable.
 * 
 * @author kbe
 */
public interface OperationID {    
    
    // 
    /**
     * Equal if it has the same ID
     * @param o The object to compare
     * @return true if o has the same id
     */
    @Override
    public boolean equals(Object o);
    
    // The String are also unique
    @Override
    public String toString();
    
    @Override
    public int hashCode();
    
}
