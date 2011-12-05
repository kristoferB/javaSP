package sequenceplanner.visualization.algorithms;

import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.data.OperationData;

/**
 * To store relations between {@link OperationData} operations.
 * @author patrik
 */
public interface IRelationContainer {

    boolean setOsetSopNode(SopNode iSopNode);

    SopNode getOsetSopNode();

    boolean setOsubsetSopNode(SopNode iSopNode);

    SopNode getOsubsetSopNode();

    boolean setOfinishsetSopNode(SopNode iSopNode);

    SopNode getOfinishsetSopNode();

    Map<String, Map<OperationData, Set<String>>> getEventOperationLocationSetMap(OperationData iOpData);

    void setEventOperationLocationSetMap(Map<OperationData, Map<String, Map<OperationData, Set<String>>>> iEventOperationLocationSetMap);

    Map<OperationData, Integer> getOperationRelationMap(OperationData iOpData);

    void setOperationRelationMap(Map<OperationData, Map<OperationData, Integer>> iOperationRelationMap);

    SopNode getRootNode();

    void setRootNode(SopNode iSopNode);
}
