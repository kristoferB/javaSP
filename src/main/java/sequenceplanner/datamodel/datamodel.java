package sequenceplanner.datamodel;

import sequenceplanner.datamodel.operation.Operation;

/**
 * Interface for the DataModel that stores all data in SP. Will be extended
 * 
 * 
 * @author kbe
 */
public interface DataModel {
    
    public Operation getOperation(String name);
    
    
    
}
