package sequenceplanner.model.data;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.mxgraph.model.mxGeometry;


/**
 *
 * @author Erik
 */
public class ViewData extends Data {
    static Logger logger = Logger.getLogger(ViewData.class);
    protected boolean isClosed = false;

//    private LinkedList<CellData> cells;
    private final LinkedList<CellData> rows;

    private int root = -1;

    public ViewData(String name, int id) {
        super(name, id);

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

    /**
     * Boolean for checking if containing operationview is closed.
     * @return
     */
    public boolean isClosed() {
        return isClosed;
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