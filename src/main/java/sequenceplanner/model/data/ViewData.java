package sequenceplanner.model.data;

import org.apache.log4j.Logger;

import com.mxgraph.model.mxGeometry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.algorithms.SopNodeFromSPGraphModel;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

/**
 *
 * @author Erik, Patrik
 */
public class ViewData extends Data {

    static Logger logger = Logger.getLogger(ViewData.class);

    public ConditionData mConditionData;
    public SopNodeForGraphPlus mSopNodeForGraphPlus;
    public Map<ISopNode, CellDataLayout> mNodeCellDataLayoutMap = new HashMap<ISopNode, CellDataLayout>();
    public Map<ISopNode, CellData> mNodeCellDataMap = new HashMap<ISopNode, CellData>();
    private boolean isClosed;

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }
    private boolean isHidden;

    public ViewData(String name, int id) {
        super(name, id);
        setHidden(false);
        setClosed(false);
        mConditionData = new ConditionData(name);
    }

    public void setSpGraphModel(final SPGraphModel iSPGraphModel) {
        mSopNodeForGraphPlus = new SopNodeForGraphPlus(iSPGraphModel);
    }

    /**
     * Called when a {@link OperationView} is saved.<br/>
     * @param iMap
     */
    public void storeCellData() {
        if(mSopNodeForGraphPlus == null) {
            return;
        }
        final Map<ISopNode, Cell> map = mSopNodeForGraphPlus.getNodeCellMap(false);

        mNodeCellDataMap.clear();
        mNodeCellDataLayoutMap.clear();
        Integer refIdCounter = 0;

        for (final ISopNode node : map.keySet()) {
            final Cell cell = map.get(node);

            //Create new CellData
            final CellData cellData = new CellData(refIdCounter++);
            mNodeCellDataMap.put(node, cellData);
            
            final CellDataLayout cellDataLayout = new CellDataLayout(cell.getGeometry(), !cell.isCollapsed());
            mNodeCellDataLayoutMap.put(node, cellDataLayout);
        }
    }

    public static class CellDataLayout {

        public final mxGeometry mGeo;
        public final boolean mExpanded;

        public CellDataLayout(mxGeometry mGeo, boolean mExpanded) {
            this.mGeo = mGeo;
            this.mExpanded = mExpanded;
        }
    }

    /**
     * Inner class to extend {@link ISopNode} objects in view with GUI data
     */
    public static class CellData {

        public final List<Integer> mSequenceSet;
        public final Integer mSuccessor;
        public final Integer mRefId;

        /**
         * Used when create data from xml file
         * @param mSopNode
         * @param mSequenceSet
         * @param mRefId
         * @param mGeo
         * @param mExpanded
         */
        public CellData(List<Integer> mSequenceSet, Integer mSuccessor, Integer mRefId) {
            this.mSequenceSet = mSequenceSet;
            this.mSuccessor = mSuccessor;
            this.mRefId = mRefId;
        }

        /**
         * Used when save to Model
         * @param mSopNode
         * @param mRefId
         * @param mGeo
         * @param mExpanded
         */
        public CellData(Integer mRefId) {
            this.mSequenceSet = null;
            this.mSuccessor = null;
            this.mRefId = mRefId;
        }

        @Override
        public String toString() {
            String returnString = "";
            returnString += " mRefId: " + mRefId;
            if (mSequenceSet != null) {
                returnString += " mSequenceSet: " + mSequenceSet;
            }
            if (mSuccessor != null) {
                returnString += " mSuccessor: " + mSuccessor;
            }
            return returnString;
        }
    }

    /**
     * To store {@link ISopNode} structure info for this {@link OperationView}
     */
    public class SopNodeForGraphPlus {

        private ISopNode mRootSopNode = null;
        private Map<ISopNode, Cell> mNodeCellMap = null;
        private SPGraphModel mSPGraphModel;

        public SopNodeForGraphPlus(final SPGraphModel iSPGraphModel) {
            this.mSPGraphModel = iSPGraphModel;
            this.getSopNodeForGraphPlus();
        }

        public ISopNode getRootSopNode(final boolean iUpDateBeforeReturn) {
            if (iUpDateBeforeReturn) {
                this.getSopNodeForGraphPlus();
            }
            return mRootSopNode;
        }

        public Map<ISopNode, Cell> getNodeCellMap(final boolean iUpDateBeforeReturn) {
            if (iUpDateBeforeReturn) {
                this.getSopNodeForGraphPlus();
            }
            return mNodeCellMap;
        }

        /**
         * Convert graph from {@link SPGraphModel} to {@link ISopNode} structure.<br/>
         * @param ioMap key: {@link ISopNode} object, value: {@link Cell} for node
         * @return the root {@link ISopNode}
         */
        private void getSopNodeForGraphPlus() {
            //Create a new sop node root aka theSopNode
            final SopNodeFromSPGraphModel snfspgm = new SopNodeFromSPGraphModel(mSPGraphModel, new SopNode());
            //get root sop
            mRootSopNode = snfspgm.getSopNodeRoot();
            //get Cell for each node
            mNodeCellMap = snfspgm.getNodeCellMap();

        }
    }
}
