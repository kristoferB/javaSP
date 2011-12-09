package sequenceplanner.visualization.algorithms;

import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.data.OperationData;

/**
 * Work with {@link IRelationContainer} objects.<br/>
 * @author patrik
 */
public interface IRelationContainerToolbox {
    
    boolean hasRelation(OperationData iOpData, IRelationContainer iRC, Integer iRelation, boolean iGoDeep);

    Integer getRelation(OperationData iOpData1, OperationData iOpData2, IRelationContainer iRC);

    SopNode getSopNode(OperationData iOpData, SopNode iRoot);
}
