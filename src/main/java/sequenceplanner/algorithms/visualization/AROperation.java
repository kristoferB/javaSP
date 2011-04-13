package sequenceplanner.algorithms.visualization;

import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.OperationData;

/**
 * Wrapper to operation {@link ISopNode} to hold information about relations to other operations.
 * @author patrik
 */
public abstract class AROperation implements IROperation {

    /**
     * Relations to other operations (their locations).<br/>
     * Outside keyset = {"up" (init->exec), "down" (exec->finish)}.<br/>
     * Inside keyset = {{@link IROperation}} for all operations in project.<br/>
     * Inside valueset \subseteq {0,1,2,01,02,12,012}
     */
    private Map<String, Map<IROperation, Set<String>>> mEventOperationLocationSetMap = null;

    /**
     * Key: other {@link IROperation} operation, Value: Relation between this operation and key operation.<br/>
     * See {@link IRelateTwoOperations} for map between value and Integer.
     */
    private Map<IROperation, Integer> mOperationRelationMap = null;

    /**
     * The {@link ISopNode} that is wrapped
     */
    private ISopNode mNode = null;

    public AROperation(ISopNode iNode) {
        setNode(iNode);
    }

    @Override
    public int getId() {
        if (mNode != null) {
            OperationData opData = (OperationData) mNode.getNodeType();
            return opData.getId();
        }
        return -1;
    }

    @Override
    public String getIdAsString() {
        return Integer.toString(getId());
    }

    @Override
    public Integer getRelationToIOperation(IROperation iOperation) {
        if (!getmOperationRelationMap().containsKey(iOperation)) {
            return -1;
        }
        return getmOperationRelationMap().get(iOperation);
    }

    @Override
    public boolean containsRelation(Set<IROperation> iSet, Integer iRelation) {
        for(final IROperation otherOp : iSet) {
            if(getRelationToIOperation(otherOp) == iRelation) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Map<IROperation, Set<String>>> getmEventOperationLocationSetMap() {
        return mEventOperationLocationSetMap;
    }

    public void setmEventOperationLocationSetMap(Map<String, Map<IROperation, Set<String>>> mEventOperationLocationSetMap) {
        this.mEventOperationLocationSetMap = mEventOperationLocationSetMap;
    }

    public Map<IROperation, Integer> getmOperationRelationMap() {
        return mOperationRelationMap;
    }

    public void setmOperationRelationMap(Map<IROperation, Integer> mOperationRelationMap) {
        this.mOperationRelationMap = mOperationRelationMap;
    }

    @Override
    public ISopNode getNode() {
        return mNode;
    }

    @Override
    public boolean setNode(ISopNode iNode) {
        if (iNode.getNodeType() instanceof OperationData) {
            mNode = iNode;
            return true;
        }
        return false;
    }

    public OperationData getOperationData() {
        if (mNode == null) {
            return null;
        }
        return (OperationData) mNode.getNodeType();
    }
}
