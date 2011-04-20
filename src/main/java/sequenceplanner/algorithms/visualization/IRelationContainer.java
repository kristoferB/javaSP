package sequenceplanner.algorithms.visualization;

import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.OperationData;

/**
 * To store relations between {@link OperationData} operations.
 * @author patrik
 */
public interface IRelationContainer {

    boolean setOsetSopNode(ISopNode iSopNode);

    ISopNode getOsetSopNode();

    boolean setOsubsetSopNode(ISopNode iSopNode);

    ISopNode getOsubsetSopNode();

    boolean setOfinishsetSopNode(ISopNode iSopNode);

    ISopNode getOfinishsetSopNode();

    Map<String, Map<OperationData, Set<String>>> getEventOperationLocationSetMap(OperationData iOpData);

    void setEventOperationLocationSetMap(Map<OperationData, Map<String, Map<OperationData, Set<String>>>> iEventOperationLocationSetMap);

    Map<OperationData, Integer> getOperationRelationMap(OperationData iOpData);

    void setOperationRelationMap(Map<OperationData, Map<OperationData, Integer>> iOperationRelationMap);

    ISopNode getRootNode();

    void setRootNode(ISopNode iSopNode);
}
