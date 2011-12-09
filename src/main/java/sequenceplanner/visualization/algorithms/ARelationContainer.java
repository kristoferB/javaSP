package sequenceplanner.visualization.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * Class used as container for relations between {@link OperationData} operations.<br/>
 * @author patrik
 */
public abstract class ARelationContainer implements IRelationContainer {

    /**
     * Relations to other operations (their locations).<br/>
     * Externl keyset = {{@link OperationData}} for all operations in Osubset.<br/>
     * Outside keyset = {"up" (init->exec), "down" (exec->finish)}.<br/>
     * Inside keyset = {{@link OperationData}} for all operations in project - external key.<br/>
     * Inside valueset \subseteq {0,1,2,01,02,12,012}
     */
    private Map<OperationData, Map<String, Map<OperationData, Set<String>>>> mEventOperationLocationSetMap = null;
    /**
     * Externl keyset = {{@link OperationData}} for all operations in Osubset.<br/>
     * Key: other {@link OperationData} operation, Value: Relation between this operation and key operation.<br/>
     * See {@link IRelateTwoOperations} for map between value and Integer.
     */
    private Map<OperationData, Map<OperationData, Integer>> mOperationRelationMap = null;
    private SopNode mSopNodeOset = null;
    private SopNode mSopNodeOsubset = null;
    private SopNode mSopNodeOfinish = null;
    /**
     * A pointer to the current root node.<br/>
     * Used in {@link HierarchicalPartition} and {@link RelationPartition}.<br/>
     */
    private SopNode mRoot = null;

    public ARelationContainer() {
        mEventOperationLocationSetMap = new HashMap<OperationData, Map<String, Map<OperationData, Set<String>>>>();
        mOperationRelationMap = new HashMap<OperationData, Map<OperationData, Integer>>();
    }

    @Override
    public Map<String, Map<OperationData, Set<String>>> getEventOperationLocationSetMap(OperationData iOpData) {
        if (!mEventOperationLocationSetMap.containsKey(iOpData)) {
            return null;
        }
        return mEventOperationLocationSetMap.get(iOpData);
    }

    @Override
    public SopNode getOfinishsetSopNode() {
        return mSopNodeOfinish;
    }

    @Override
    public Map<OperationData, Integer> getOperationRelationMap(OperationData iOpData) {
        if (!mOperationRelationMap.containsKey(iOpData)) {
            return null;
        }
        return mOperationRelationMap.get(iOpData);
    }

    @Override
    public SopNode getOsetSopNode() {
        return mSopNodeOset;
    }

    @Override
    public SopNode getOsubsetSopNode() {
        return mSopNodeOsubset;
    }

    @Override
    public void setEventOperationLocationSetMap(Map<OperationData, Map<String, Map<OperationData, Set<String>>>> iEventOperationLocationSetMap) {
        if (iEventOperationLocationSetMap != null) {
            this.mEventOperationLocationSetMap = iEventOperationLocationSetMap;
        }
    }

    @Override
    public boolean setOfinishsetSopNode(SopNode iSopNode) {
        if (iSopNode == null || getOsetSopNode() == null) {
            return false;
        }

        if (new SopNodeToolboxSetOfOperations().operationsAreSubset(iSopNode, getOsetSopNode())) {
            this.mSopNodeOfinish = iSopNode;
            return true;
        }

        return false;
    }

    @Override
    public void setOperationRelationMap(Map<OperationData, Map<OperationData, Integer>> iOperationRelationMap) {
        if(iOperationRelationMap != null) {
            this.mOperationRelationMap = iOperationRelationMap;
        }
    }

    @Override
    public boolean setOsetSopNode(SopNode iSopNode) {
        if(iSopNode == null) {
            return false;
        }
        this.mSopNodeOset = iSopNode;
        return true;
    }

    @Override
    public boolean setOsubsetSopNode(SopNode iSopNode) {
        if (iSopNode == null || getOsetSopNode() == null) {
            return false;
        }

        if (new SopNodeToolboxSetOfOperations().operationsAreSubset(iSopNode, getOsetSopNode())) {
            this.mSopNodeOsubset = iSopNode;
            return true;
        }

        return false;
    }

    @Override
    public SopNode getRootNode() {
        return mRoot;
    }

    @Override
    public void setRootNode(SopNode iSopNode) {
        this.mRoot = iSopNode;
    }

}
