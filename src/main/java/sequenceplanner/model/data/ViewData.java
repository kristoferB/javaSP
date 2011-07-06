package sequenceplanner.model.data;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.mxgraph.model.mxGeometry;
import java.util.HashMap;
import java.util.Map;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.view.operationView.graphextension.Cell;


/**
 *
 * @author Erik
 */
public class ViewData extends Data {
    static Logger logger = Logger.getLogger(ViewData.class);

    public ISopNode mSopNodeRoot = new SopNode();
    public Map<ISopNode,CellData2> mNodeCellDataMap = new HashMap<ISopNode, CellData2>();

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
    
    public void storeCellData(final Map<ISopNode,Cell> iMap) {
        mNodeCellDataMap.clear();
        int refIdCounter = 0;

        for(final ISopNode node : iMap.keySet()) {
            final Cell cell = iMap.get(node);
            //Create new CellData
            final CellData2 cellData= new CellData2(refIdCounter++, cell.getGeometry(), !cell.isCollapsed());
            mNodeCellDataMap.put(node, cellData);
        }
    }

    /**
     * Inner class to extend {@link ISopNode} objects in view with GUI data
     */
    public static class CellData2 {

        public final int mRefId;
        public final mxGeometry mGeo;
        public final boolean mExpanded;

        public CellData2(int mRefId, mxGeometry mGeo, boolean mExpanded) {
            this.mRefId = mRefId;
            this.mGeo = mGeo;
            this.mExpanded = mExpanded;
        }
    }

   /**
    * Inner class to describe each "row" in the table
    */
   public static class CellData  implements Cloneable {
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
            return new CellData(id, previousCell, type, relation, lastInRelation,(mxGeometry) geo.clone(), expanded);
        }

      @Override
      public String toString() {
      return "| " + id + " | "
                    + previousCell + " | "
                    + type + " | "
                    + relation + " | "
                    + lastInRelation + " | "
                    + geo.getX() + " | "
                    + geo.getY() + " | "
                    + expanded + " | ";
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
       output += ( "=== name = " + getName() + "====== Root = " + Integer.toString(getRoot()) + "=====");

       for (CellData cellData : rows) {
         output += "\n" + cellData.toString();
      }

       output += "\n====================================================================D";

      return output;
   }


}