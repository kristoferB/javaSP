package sequenceplanner.algorithms.visualization;

import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.OperationData;

/**
 * Interface for wrapper to {@link ISopNode}, to also contain information about relations to other operations.
 * @author patrik
 */
public interface IROperation {

    public int getId();

    public String getIdAsString();

    OperationData getSelfContainedOperation();

    void setSelfContainedOperation(OperationData iOpData);

    ISopNode getNode();
//
//    ISopNode getNode(ISopNode iRoot);

    /**
     *
     * @param iOperation the operation to get relation to
     * @return relation integer, see {@link IRelateTwoOperations}, or -1 if no relation has been calculated
     */
    public Integer getRelationToIOperation(final IROperation iOperation);

    /**
     * See if operation contians a given relation.<br/>
     * @param iSet set to look in
     * @param iRelation the relation to look for
     * @return true if relation is found else falses
     */
    public boolean containsRelation(final Set<IROperation> iSet, final Integer iRelation);
}
