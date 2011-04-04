package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.OperationData;

/**
 * Interface for how to relate a subset of operations with respect to set.<br/>
 * @author patrik
 */
public interface IOperationRelations {

    public OperationData getOperationData(final IROperation iOperation);

    public OperationData getOperationData(final Integer iId);

    public boolean identifyRelations();

    public ISopNode getRelationOperationSetAsSOPNode();
}