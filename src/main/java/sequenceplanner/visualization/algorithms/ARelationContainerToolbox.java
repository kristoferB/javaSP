package sequenceplanner.visualization.algorithms;

import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.algorithms.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * To work on {@link IRelationContainer} objects.<br/>
 * @author patrik
 */
public abstract class ARelationContainerToolbox implements IRelationContainerToolbox {

    private SopNodeToolboxSetOfOperations mSopNodeToolbox = new SopNodeToolboxSetOfOperations();

    /**
     * To see if operation has given relation in given set.<br/>
     * @param iOpData the operation of interest
     * @param iRC the set is given as the operations in iRC.getRootNode
     * @param iRelation relation to look for
     * @param iGoDeep see {@link ISopNodeToolbox} getOperations
     * @return true if iOpData has iRelation to operations in iRC.getRootNode(), else false
     */
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

    /**
     * To get the relation between two {@link OperationData}.<br/>
     * @param iOpData1
     * @param iOpData2
     * @param iRC Container for relations
     * @return iOpData1's relation to iOpData2 or -1 if iRC lacks one/both of the operations.
     */
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

    /**
     * Translate a {@link OperationData} to a {@link ISopNode}.<br/>
     * The first node found that wrapps operation is returned.<br/>
     * @param iOpData operation to translate
     * @param iRoot look in children to this node
     * @return the first node found that wrapps operation is returned, null is returned if no node is found.
     */
    @Override
    public ISopNode getSopNode(OperationData iOpData, ISopNode iRoot) {
        for (final ISopNode node : mSopNodeToolbox.getNodes(iRoot, true)) {
            if(node instanceof SopNodeOperation) {
                if(node.getOperation().equals(iOpData)) {
                    return node;
                }
            }
        }
        return null;
    }
}
