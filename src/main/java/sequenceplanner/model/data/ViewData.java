package sequenceplanner.model.data;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.mxgraph.model.mxGeometry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeFromSPGraphModel;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

/**
 *
 * @author Erik, Patrik
 */
public class ViewData extends Data {

    static Logger logger = Logger.getLogger(ViewData.class);
//    public ISopNode mRootSopNode;
    public SopNodeForGraphPlus mSopNodeForGraphPlus;
    public Map<ISopNode, CellData3> mNodeCellDat3aMap = new HashMap<ISopNode, CellData3>();
    public Map<ISopNode, CellData2> mNodeCellDataMap = new HashMap<ISopNode, CellData2>();
    /**
     * To store data from xml file
     */
    public Set<CellData2> mCellDataSet = new HashSet<CellData2>();
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
//    private LinkedList<CellData> cells;
    private final LinkedList<CellData> rows;
    private int root = -1;

    public ViewData(String name, int id) {
        super(name, id);
        setHidden(false);
        setClosed(false);
        rows = new LinkedList<CellData>();
        
//        mRootSopNode = new SopNode();
    }

    public void setSpGraph(final SPGraphModel iSPGraphModel) {
        mSopNodeForGraphPlus = new SopNodeForGraphPlus(iSPGraphModel);
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public int getRoot() {
        return this.root;
    }

    public void addRow(int id, int previousCell, int type, int relation,
            boolean lastInRelation, mxGeometry geo, boolean expanded) {
        rows.add(new CellData(id, previousCell, type, relation, lastInRelation, geo, expanded));

    }

    public LinkedList<CellData> getData() {
        return rows;
    }

    public Iterator<CellData> getIterator() {
        return rows.iterator();
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
        mCellDataSet.clear();
        mNodeCellDataMap.clear();
        Integer refIdCounter = 0;

        for (final ISopNode node : map.keySet()) {
            final Cell cell = map.get(node);

            System.out.println("cell.getGeometry(): " + cell.getGeometry() != null);
            System.out.println("cell.isCollapsed(): " + cell.isCollapsed() != null);

            //Create new CellData
            final CellData2 cellData = new CellData2(node, refIdCounter++, cell.getGeometry(), !cell.isCollapsed());
            mCellDataSet.add(cellData);
            mNodeCellDataMap.put(node, cellData);
        }
    }

    public static class CellData3 {

        public final mxGeometry mGeo;
        public final boolean mExpanded;

        public CellData3(mxGeometry mGeo, boolean mExpanded) {
            this.mGeo = mGeo;
            this.mExpanded = mExpanded;
        }
    }

    /**
     * Inner class to extend {@link ISopNode} objects in view with GUI data
     */
    public static class CellData2 {

        public final ISopNode mSopNode;
        public final List<Integer> mSequenceSet;
        public final Integer mSuccessor;
        public final Integer mRefId;
        public final CellData3 mCellData;
        public final mxGeometry mGeo;
        public final boolean mExpanded;

        /**
         * Used when create data from xml file
         * @param mSopNode
         * @param mSequenceSet
         * @param mRefId
         * @param mGeo
         * @param mExpanded
         */
        public CellData2(ISopNode mSopNode, List<Integer> mSequenceSet, Integer mSuccessor, Integer mRefId, mxGeometry mGeo, boolean mExpanded) {
            this.mSopNode = mSopNode;
            this.mSequenceSet = mSequenceSet;
            this.mSuccessor = mSuccessor;
            this.mRefId = mRefId;
            mCellData = new CellData3(mGeo, mExpanded);
            this.mGeo = mGeo;
            this.mExpanded = mExpanded;
        }

        /**
         * Used when save to Model
         * @param mSopNode
         * @param mRefId
         * @param mGeo
         * @param mExpanded
         */
        public CellData2(ISopNode mSopNode, Integer mRefId, mxGeometry mGeo, boolean mExpanded) {
            this.mSopNode = mSopNode;
            this.mSequenceSet = null;
            this.mSuccessor = null;
            this.mRefId = mRefId;
            mCellData = new CellData3(mGeo, mExpanded);
            this.mGeo = mGeo;
            this.mExpanded = mExpanded;
        }

        @Override
        public String toString() {
            String returnString = "";
            returnString += "mSopNode: " + mSopNode.typeToString();
            if (mSequenceSet != null) {
                returnString += " mSequenceSet: " + mSequenceSet;
            }
            if (mSuccessor != null) {
                returnString += " mSuccessor: " + mSuccessor;
            }
            returnString += " mRefId: " + mRefId;
            return returnString;
        }
    }

    /**
     * Inner class to describe each "row" in the table
     */
    public static class CellData implements Cloneable {

        public final int id;
        public final int previousCell;
        public final int type;
        public final int relation;
        public final boolean lastInRelation;
        public final mxGeometry geo;
        public final boolean expanded;

        public CellData(int id, int previousCell, int type, int relation,
                boolean lastInRelation, mxGeometry geo, boolean expanded) {
            this.id = id;
            this.previousCell = previousCell;
            this.type = type;
            this.relation = relation;
            this.lastInRelation = lastInRelation;
            this.expanded = expanded;
            this.geo = geo;
        }

        @Override
        public Object clone() {
            return new CellData(id, previousCell, type, relation, lastInRelation, (mxGeometry) geo.clone(), expanded);
        }

        @Override
        public String toString() {
            return "| " + id + " | " + previousCell + " | " + type + " | " + relation + " | " + lastInRelation + " | " + geo.getX() + " | " + geo.getY() + " | " + expanded + " | ";
        }
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public Object clone() {
        ViewData clone = new ViewData(getName(), getId());
        clone.setRoot(root);

        LinkedList<CellData> cloneData = new LinkedList<CellData>();

        for (CellData cellData : rows) {
            cloneData.add((CellData) cellData.clone());
        }

        return clone;
    }

    @Override
    public String toString() {
        String output = "";
        output += ("=== name = " + getName() + "====== Root = " + Integer.toString(getRoot()) + "=====");

        for (CellData cellData : rows) {
            output += "\n" + cellData.toString();
        }

        output += "\n====================================================================D";

        return output;
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
