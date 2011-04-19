package sequenceplanner.algorithms.visualization;

import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class ARelationContainerToolbox implements IRelationContainerToolbox{

    private SopNodeToolboxSetOfOperations mSopNodeToolbox = new SopNodeToolboxSetOfOperations();

    @Override
    public boolean hasRelation(OperationData iOpData, IRelationContainer iRC, Integer iRelation) {
        Set<OperationData> setToLoop = mSopNodeToolbox.getOperations(iRC.getOsubsetSopNode());
        for(final OperationData opData : setToLoop) {
            if(getRelation(iOpData, opData, iRC) == iRelation) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getRelation(OperationData iOpData1, OperationData iOpData2, IRelationContainer iRC) {
        return iRC.getOperationRelationMap(iOpData1).get(iOpData2);
    }

    @Override
    public ISopNode getSopNode(OperationData iOpData, IRelationContainer iRC) {
        for(final ISopNode node : iRC.getOsubsetSopNode().getFirstNodesInSequencesAsSet()) {
            if(node.getNodeType() instanceof OperationData) {
                final OperationData internalOpData = (OperationData) node.getNodeType();
                if(internalOpData.equals(iOpData)) {
                    return node;
                }
            }
        }
        return null;
    }
}