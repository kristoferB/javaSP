package sequenceplanner.algorithms.visualization;

import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class ARelationContainerToolbox implements IRelationContainerToolbox {

    private SopNodeToolboxSetOfOperations mSopNodeToolbox = new SopNodeToolboxSetOfOperations();

    @Override
    public boolean hasRelation(OperationData iOpData, IRelationContainer iRC, Integer iRelation, boolean iGoDeep) {
        Set<OperationData> setToLoop = mSopNodeToolbox.getOperations(iRC.getRootNode(), iGoDeep);
        for (final OperationData opData : setToLoop) {
            if (getRelation(iOpData, opData, iRC) == iRelation) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getRelation(OperationData iOpData1, OperationData iOpData2, IRelationContainer iRC) {
        Map<OperationData, Integer> map = iRC.getOperationRelationMap(iOpData1);
        if (map == null) {
            return -1;
        }
        if (!map.containsKey(iOpData2)) {
            return -1;
        }
        return map.get(iOpData2);
    }

    @Override
    public ISopNode getSopNode(OperationData iOpData, ISopNode iRoot) {
        for (final ISopNode node : mSopNodeToolbox.getNodes(iRoot, true)) {
            if (node.getNodeType() instanceof OperationData) {
                final OperationData internalOpData = (OperationData) node.getNodeType();
                if (internalOpData.equals(iOpData)) {
                    return node;
                }
            }
        }
        return null;
    }
}
