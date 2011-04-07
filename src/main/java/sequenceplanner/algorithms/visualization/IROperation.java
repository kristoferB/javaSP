package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.ISopNode;

/**
 * Interface for wrapper to {@link ISopNode}, to also contain information about relations to other operations.
 * @author patrik
 */
public interface IROperation {

    public int getId();

    public String getIdAsString();

    ISopNode getNode();

    boolean setNode(ISopNode iNode);

    public Integer getRelationToIOperation(final IROperation iOperation);
}
