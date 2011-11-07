package sequenceplanner.datamodel.operation;

import sequenceplanner.datamodel.Connection.*;
import java.util.Set;
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
     * Returns the unique identifier of the operation
     * @return {@link UUID}
     */
    public UUID getID();
    
    /**
     * Returns a set including the operation connections. If no connections 
     * exists, the empty set is returned.
     * 
     * @return a {@link Set<Connection>}
     */
    public Set<Connection> getConnections();
    
    /**
     * Adds a {@link Set<Connection>} of connections. If connections
     * already exists, these are replaced.
     * 
     * @param connections isa {@link Set<Connection>}
     */
    public void setConnections(Set<Connection> connections);    
    
    /**
     * Adds a {@link Connection} to the operation
     * 
     * @param c isa {@link Connection}
     * @return true if connection was added, false otherwise (dublicated?)
     */
    public boolean setConnection(Connection c);
       
    /**
     * Returns a set including the operation conditions. If no conditions 
     * exists, the empty set is returned
     * 
     * @return a {@link Set<Condition>}
     */
    public Set getConditions();
    
    /**
     * Adds a {@link Set<Connection>} of connections. If connections
     * already exists, these are replaced.
     * 
     * @param conditions isa {@link Set<Condition>}
     */
    public void setConditions(Set<Condition> conditions);    
    
    /**
     * Adds a {@link Condition} to the operation
     * @param c isa {@link Condition}
     * @return true if condition was added, false otherwise (dublicated?)
     */
    public boolean setCondition(Condition c);
      
    /**
     * Returns a set including the operation attributes. If no Attributes 
     * exists, the empty set is returned.
     * 
     * @return a {@link Set<Connection>}
     */
    public Set<Attribute> getAttributes();
    
    /**
     * Adds a {@link Set<Attribute>} of Attributes. If Attributes
     * already exists, these are replaced.
     * 
     * @param attributes isa {@link Set<Attribute>}
     */
    public void setAttributes(Set<Attribute> attributes); 
        
    /**
     * Adds a {@link Attribute} to the operation
     * @param a isa {@link Attribute}
     * @return true if attribute was added, false otherwise (dublicated?)
     */
    public boolean setAttribute(Attribute a);    
    
    
    /**
     * Two operations are equal if the unique identifier is the same.  
     * 
     * Two operations can have the same id but differeing contents, e.g. on of the 
     * operations has been modified in an external program or changed
     * in a sop-view. To find these, use contentEquals. 
     * 
     * @param obj 
     * @return true if they have the same id
     */
    @Override
    public boolean equals(Object obj);
    
    
    /**
     * Two operations are content equal if the content in the connection, 
     * condition and attribute sets are the same. But the operation id does
     * not need to be the same.
     * 
     * @param o isa  {@link Operation}
     * @return true if they are content equal
     */
    public boolean contentEquals(Operation o);    
    
    /**
     * A hash code.
     * 
     * @return the hashcode
     */
    @Override
    public int hashCode();
}
