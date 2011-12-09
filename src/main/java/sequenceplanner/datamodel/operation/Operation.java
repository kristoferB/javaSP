package sequenceplanner.datamodel.operation;

import java.util.List;
import java.util.Set;
import sequenceplanner.datamodel.Connection.*;
import sequenceplanner.datamodel.condition.*;
import sequenceplanner.datamodel.Attribute.*;
import java.util.UUID;

/**
 * This interface defines an operation including conditions, connections
 * and attributes.
 * 
 * @author kbe
 */
public interface Operation {
   
    
    /**
     * Returns the ID object of the operation
     * @return {@link OperationID}
     */
    public OperationID getID();
    
    /**
     * Returns an unmodifiable set of the operation connections. If no connections 
     * exists, the empty set is returned.
     * 
     * @return a {@link Set<Connection>}
     */
    public Set<Connection> getConnections();
      
   
       
    /**
     * Returns an unmodifiable set of the operation conditions. If no conditions 
     * exists, the empty set is returned
     * 
     * @return a {@link Set<Condition>}
     */
    public Set<Condition> getConditions();
    
      
    /**
     * Returns a unmodifiable set of the operation attributes. If no Attributes 
     * exists, the empty set is returned.
     * 
     * @return a {@link Set<Connection>}
     */
    public Set<Attribute> getAttributes();
     
    

}
