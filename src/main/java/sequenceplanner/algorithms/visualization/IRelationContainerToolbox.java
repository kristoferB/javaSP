package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public interface IRelationContainerToolbox {

    boolean hasRelation(OperationData iOpData, IRelationContainer iRC, Integer iRelation);

    Integer getRelation(OperationData iOpData1, OperationData iOpData2, IRelationContainer iRC);

    ISopNode getSopNode(OperationData iOpData, ISopNode iRoot);
}
