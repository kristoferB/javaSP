package sequenceplanner.datamodel.operation;

import sequenceplanner.datamodel.structure.DAGStructure;

/**
 *
 * @author kbe
 */
public class OperationsStructureDAG {
    
    private DAGStructure<Operation> productOperations;
    private DAGStructure<Operation> resourceOperations; 
    
    
    public OperationsStructureDAG(){
        productOperations = new DAGStructure<Operation>();
        resourceOperations = new DAGStructure<Operation>();
    }
    
    
    
}
