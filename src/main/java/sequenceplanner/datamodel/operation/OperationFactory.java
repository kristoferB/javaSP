package sequenceplanner.datamodel.operation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import sequenceplanner.datamodel.Attribute.Attribute;
import sequenceplanner.datamodel.Connection.Connection;
//import sequenceplanner.datamodel.DataModel;
import sequenceplanner.datamodel.condition.Condition;

/**
 * 
 * @author kbe
 */
public enum OperationFactory {
    INSTANCE;    
    
//    public Operation createOperation(String name, DataModel dm){
//        // will also add the operation to the datamodel
//        return new OperationImpl(name,this.getNewOperationID());
//    }
//    
//
//    
//    private void addOperation(Operation o){
//        
//    }
//    
//    private OperationID getNewOperationID(){
//        return new OperationIDimpl(UUID.randomUUID());
//    }
//    
//    private OperationID getOperationID(UUID id, DataModel dm){
//        // should also check if already in dm! Should be impl
//        return new OperationIDimpl(id);
//    }
//
//    private class OperationImpl  implements Operation {
//        
//        private final OperationID id;
//        private String name;
//
//        private final Set<Connection> connections = new HashSet<Connection>(); 
//        private final Set<Condition> conditions = new HashSet<Condition>();
//        private final Set<Attribute> attributes = new HashSet<Attribute>();
//        
//        public OperationImpl(String name, OperationID id){
//            this.name = name;
//            this.id = id;
//        }
//        
//    
//        @Override
//        public OperationID getID() {
//            return id;
//        }
//
//        @Override
//        public Set<Connection> getConnections() {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//
//        @Override
//        public Set<Condition> getConditions() {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//
//
//        @Override
//        public Set<Attribute> getAttributes() {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//
//    }
//    
//    
//    // May need to be more sofisticated later on...
//    private class OperationIDimpl implements OperationID{    
//        private final UUID id;
//
//        public OperationIDimpl(UUID id){
//            this.id = id;
//        }
//
//        public UUID getID(){
//            return id;
//        }
    
    
//}
    
    
}
