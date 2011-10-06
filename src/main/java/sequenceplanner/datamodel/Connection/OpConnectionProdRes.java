package sequenceplanner.datamodel.Connection;

import java.util.Set;
import java.util.UUID;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.operation.Operation;

/**
 *
 * @author kbe
 */
public class OpConnectionProdRes implements Connection {

    private final UUID id;
    private ConditionExpression matchingOperations;
    
    public OpConnectionProdRes(){
        id =  UUID.randomUUID();
    }
    
    public void addRealizingOperationsOr(Set<Operation> realizers){
        
    }

    
    /**
     * Returns the unique identifier of the connection
     * @return {@link UUID}
     */
    @Override
    public UUID getID(){
        return id;
    }
    
    /**
     * Two Connections are equal if the unique identifier is the same and 
     * the contents are the same. 
     * 
     * 
     * @param obj 
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof this.getClass()) return false;
        
    }
    
   
    
    /**
     * A hash code.
     * 
     * @return the hashcode
     */
    @Override
    public int hashCode(){
        
    }
    
}
