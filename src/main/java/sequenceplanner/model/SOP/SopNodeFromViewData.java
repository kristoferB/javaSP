package sequenceplanner.model.SOP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.model.data.ViewData;

/**
 * For import from xml file.<br/>
 * Creates a sop structure based on CellData in {@link ViewData} object.<br/>
 * Result is given as root to sop structure in {@link ViewData} object.<br/>
 * @author patrik
 */
public class SopNodeFromViewData {

    private final ViewData mViewData;
    private final ISopNode mRootSopNode;
    private Map<Integer, ISopNode> mIdSopNodeMap;
    private Set<ISopNode> mInSequenceSetForRootSopNode;

    public SopNodeFromViewData(ViewData mViewData) {
        this(mViewData,new SopNode());
    }

    public SopNodeFromViewData(ViewData mViewData, ISopNode mRootSopNode) {
        this.mViewData = mViewData;
        this.mRootSopNode = mRootSopNode;
        run();
    }


    private void run() {

        //int--------------------------------------------------------------------
        mIdSopNodeMap = new HashMap<Integer, ISopNode>();
        mInSequenceSetForRootSopNode = new HashSet<ISopNode>();

        //Create map (mIdSopNodeMap) between local ids in graph and SopNodes-----
        for (final ISopNode sopNode: mViewData.mNodeCellDataMap.keySet()) {
            final ViewData.CellData cellData = mViewData.mNodeCellDataMap.get(sopNode);
            final Integer id = cellData.mRefId;
            putToIdSopNodeMap(id, sopNode);
        }

        //Set sequence set and successor for sop nodes based on mIdIdMap---------
        for (final ISopNode sopNode: mViewData.mNodeCellDataMap.keySet()) {
            final ViewData.CellData cellData = mViewData.mNodeCellDataMap.get(sopNode);

            //Sequence set
            if (cellData.mSequenceSet != null) {
                for (final Integer childId : cellData.mSequenceSet) {
                    sopNode.addNodeToSequenceSet(getSopNodeFromIdSopNodeMap(childId));
                }
            }

            //Successor
            if (cellData.mSuccessor != null) {
                sopNode.setSuccessorNode(getSopNodeFromIdSopNodeMap(cellData.mSuccessor));
                sopNode.setSuccessorRelation(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12);
            }
        }

        //Set sequence set for root sopnode--------------------------------------
        for (final ISopNode childNode : mInSequenceSetForRootSopNode) {
            mRootSopNode.addNodeToSequenceSet(childNode);
        }
    }

    private ISopNode getSopNodeFromIdSopNodeMap(final Integer iId) {
        final ISopNode node = mIdSopNodeMap.get(iId);
        mInSequenceSetForRootSopNode.remove(node);
        return node;
    }

    private void putToIdSopNodeMap(final Integer iId, final ISopNode iSopNode) {
        mIdSopNodeMap.put(iId, iSopNode);
        mInSequenceSetForRootSopNode.add(iSopNode);
    }

    public ISopNode getRootSopNode() {
        return mRootSopNode;
    }
}
