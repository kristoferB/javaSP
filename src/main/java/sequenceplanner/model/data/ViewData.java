package sequenceplanner.model.data;

import org.apache.log4j.Logger;

import com.mxgraph.model.mxGeometry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeEmpty;
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
    public Map<SopNode, CellDataLayout> mNodeCellDataLayoutMap = new HashMap<SopNode, CellDataLayout>();
    public Map<SopNode, CellData> mNodeCellDataMap = new HashMap<SopNode, CellData>();
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
        final Map<SopNode, Cell> map = mSopNodeForGraphPlus.getNodeCellMap(false);

        mNodeCellDataMap.clear();
        mNodeCellDataLayoutMap.clear();
        Integer refIdCounter = 0;

        for (final SopNode node : map.keySet()) {
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
     * Inner class to extend {@link SopNode} objects in view with GUI data
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
     * To store {@link SopNode} structure info for this {@link OperationView}
     */
    public class SopNodeForGraphPlus {

        private SopNode mRootSopNode = null;
        private Map<SopNode, Cell> mNodeCellMap = null;
        private SPGraphModel mSPGraphModel;

        public SopNodeForGraphPlus(final SPGraphModel iSPGraphModel) {
            this.mSPGraphModel = iSPGraphModel;
            this.getSopNodeForGraphPlus();
        }

        public SopNode getRootSopNode(final boolean iUpDateBeforeReturn) {
            if (iUpDateBeforeReturn) {
                this.getSopNodeForGraphPlus();
            }
            return mRootSopNode;
        }

        public Map<SopNode, Cell> getNodeCellMap(final boolean iUpDateBeforeReturn) {
            if (iUpDateBeforeReturn) {
                this.getSopNodeForGraphPlus();
            }
            return mNodeCellMap;
        }

        /**
         * Convert graph from {@link SPGraphModel} to {@link SopNode} structure.<br/>
         * @param ioMap key: {@link SopNode} object, value: {@link Cell} for node
         * @return the root {@link SopNode}
         */
        private void getSopNodeForGraphPlus() {
            //Create a new sop node root aka theSopNode
            final SopNodeFromSPGraphModel snfspgm = new SopNodeFromSPGraphModel(mSPGraphModel, new SopNodeEmpty());
            //get root sop
            mRootSopNode = snfspgm.getSopNodeRoot();
            //get Cell for each node
            mNodeCellMap = snfspgm.getNodeCellMap();

        }
    }
}
