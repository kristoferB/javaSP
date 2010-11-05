package sequenceplanner.model.data;


import com.mxgraph.model.mxGeometry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.Logger;


/**
 *
 * @author Erik
 */
public class ViewData_1 extends Data {
    static Logger logger = Logger.getLogger(ViewData.class);

//    private LinkedList<CellData> cells;
    private final LinkedList<CellData> cells;

    private int root = -1;

    public ViewData_1(String name, int id) {
        super(name, id);

        cells = new LinkedList<CellData>();
        
    }



    public void setRoot(int root) {
      this.root = root;
    }

    public int getRoot() {
      return this.root;
    }

    public void addRow(int id, int parent, Integer[] sequence, mxGeometry geo, String p, int type) {
       cells.add( new CellData( id, parent, sequence, geo, p, type ) );
    }

    public void removeRow(int id) {
       // Remove all precond that has id
       // Remove all that has id as parent
       for (Iterator<CellData> it = cells.iterator(); it.hasNext();) {
          CellData cellData = it.next();
          if (cellData.parent == id) {
             removeRow(id);
          }

          for (Integer i : cellData.sequence) {
             if (i == id) {
                cellData.sequence.remove(i);
                break;
             }
          }
       }

       
    }

    public int getSize() {
       return cells.size();
    }

    public int getId(int row) {
       return cells.get(row).id;
    }

    public int getParent(int row) {
       return cells.get(row).parent;
    }

    public ArrayList<Integer> getSequence(int row) {
       return cells.get(row).sequence;
    }



    public mxGeometry getPos(int row) {
       return cells.get(row).geo;
    }

   /**
    * Inner class to describe each "row" in the table
    */
   public class CellData {
      public final mxGeometry geo;
      public final int parent;
      public final ArrayList<Integer> sequence = new ArrayList<Integer>();
      public final int id;
      public final int type;
      public final String p;


      public CellData(int id, int parent, Integer[] sequence, 
            mxGeometry geo, String graphicalParent, int type) {
         this.geo = geo;
         this.parent = parent;
         for (Integer integer : sequence) {
            this.sequence.add(integer);
         }

         this.id = id;
         this.p = graphicalParent;
         this.type = type;
      }
      
   }

   public LinkedList<CellData> getLevel(int level) {
      LinkedList<CellData> result = new LinkedList<CellData>();

      for (Iterator<CellData> it = cells.iterator(); it.hasNext();) {
         CellData cellData = it.next();

         if(cellData.parent == level) {
            result.add(cellData);
         }
      }

      return result;
   }


   public int getRow(int parent) {
      return 0;
   }




   @Override
   public boolean equals(Object obj) {
      return false;
   }

   @Override
   public Object clone() {
      return super.clone();
   }


}