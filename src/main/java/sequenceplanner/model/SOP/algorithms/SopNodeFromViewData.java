package sequenceplanner.model.SOP.algorithms;

import sequenceplanner.model.SOP.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.model.data.ViewData;

/**
 * For import from xml file.<br/>
 * Creates a sop structure based on CellData in {@link ViewData} object.<br/>
 * Result is given as root to sop structure in {@link ViewData} object.<br/>
 * @author patrik
 */
public class SopNodeFromViewData {

    private final ViewData mViewData;
    private final SopNode mRootSopNode;
    private Map<Integer, SopNode> mIdSopNodeMap;
    private Set<SopNode> mInSequenceSetForRootSopNode;

    public SopNodeFromViewData(ViewData mViewData) {
        this(mViewData,new SopNodeEmpty());
    }

    public SopNodeFromViewData(ViewData mViewData, SopNode mRootSopNode) {
        this.mViewData = mViewData;
        this.mRootSopNode = mRootSopNode;
        run();
    }


    private void run() {

        //int--------------------------------------------------------------------
        mIdSopNodeMap = new HashMap<Integer, SopNode>();
        mInSequenceSetForRootSopNode = new HashSet<SopNode>();

        //Create map (mIdSopNodeMap) between local ids in graph and SopNodes-----
        for (final SopNode sopNode: mViewData.mNodeCellDataMap.keySet()) {
            final ViewData.CellData cellData = mViewData.mNodeCellDataMap.get(sopNode);
            final Integer id = cellData.mRefId;
            putToIdSopNodeMap(id, sopNode);
        }

        //Set sequence set and successor for sop nodes based on mIdIdMap---------
        for (final SopNode sopNode: mViewData.mNodeCellDataMap.keySet()) {
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
        for (final SopNode childNode : mInSequenceSetForRootSopNode) {
            mRootSopNode.addNodeToSequenceSet(childNode);
        }
    }

    private SopNode getSopNodeFromIdSopNodeMap(final Integer iId) {
        final SopNode node = mIdSopNodeMap.get(iId);
        mInSequenceSetForRootSopNode.remove(node);
        return node;
    }

    private void putToIdSopNodeMap(final Integer iId, final SopNode iSopNode) {
        mIdSopNodeMap.put(iId, iSopNode);
        mInSequenceSetForRootSopNode.add(iSopNode);
    }

    public SopNode getRootSopNode() {
        return mRootSopNode;
    }
}
