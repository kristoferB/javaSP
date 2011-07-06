package sequenceplanner.view.operationView.graphextension;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import sequenceplanner.model.Model;
import sequenceplanner.model.NameCacheMap;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.Constants;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;

public class SPGraphModel extends mxGraphModel {

   //Constants that describe the different cells
   final public static String TYPE_OPERATION = "operation";
   final public static String TYPE_SOP = "sop";
   final public static String TYPE_PARALLEL = "parallel";
   final public static String TYPE_ALTERNATIVE = "alternative";
   final public static String TYPE_ARBITRARY = "arbitrary";
   //Cache for cell paths and names.
   private NameCacheMap nameCache = new NameCacheMap();

   public SPGraphModel(Object root) {
      super(root);
      setMaintainEdgeParent(false);
   }

   public SPGraphModel() {
      super();
      setMaintainEdgeParent(false);
   }

   public void setCacheParent(NameCacheMap parent) {
      nameCache.setParent(parent);
   }

   public NameCacheMap getNameCache() {
      return nameCache;
   }

   @Override
   public Object createRoot() {
      Cell root = new Cell("Process");
      root.insert(new Cell(new Data("root", -1)));
      return root;
   }

   public void reloadNamesCache() {
      Cell c = getGraphRoot();
      reloadNameChache(c, Constants.VIEW);
   }

   protected void reloadNameChache(Cell node, String path) {
      Cell[] sops = getChildSOP(node);

      for (int i = 0; i < sops.length; i++) {
         String name = ((Data) sops[i].getValue()).getName();
         nameCache.put(sops[i].getUniqueId(), path, name);

         String newPath = name;
         if (!path.isEmpty()) {
            newPath = path + "." + name;
         }

         reloadNameChache(sops[i], newPath);
      }
   }

   public void updatePreconditions(Cell node, boolean showPath) {
      Cell[] sops = getChildSOP(node);

      for (int i = 0; i < sops.length; i++) {
         OperationData d = ((OperationData) sops[i].getValue());

//         d.setPrecondition(Model.updateCondition(nameCache,
//               d.getSequenceCondition(), d.getResourceBooking(), showPath));
         
         updatePreconditions(sops[i], showPath);
      }
   }

   //TODO verify function
   public Cell[] getChildSOP(Object parent) {
      Object[] vert = mxGraphModel.getChildVertices(this, parent);


      Stack<Object> v = new Stack<Object>();
      for (int i = 0; i < vert.length; i++) {
         v.push(vert[i]);
      }
      ArrayList<Object> sops = new ArrayList<Object>();

      if (vert.length == 1 && vert[0].equals(this.getChildAt(this.getRoot(), 0))) {
         return new Cell[]{(Cell) vert[0]};
      } else {

         while (!v.isEmpty()) {
            Cell cell = (Cell) v.pop();



            if (cell.isOperation() || cell.isSOP()) {
               sops.add(cell);

            } else if (cell.isParallel() || cell.isArbitrary() || cell.isAlternative()) {

               vert = mxGraphModel.getChildVertices(this, cell);
               for (int i = 0; i < vert.length; i++) {
                  v.push(vert[i]);
               }
            }
         }
      }




      Cell[] cells = new Cell[sops.size()];
      int i = 0;

      for (Iterator<Object> it = sops.iterator(); it.hasNext();) {
         cells[i++] = (Cell) it.next();
      }
      return cells;
   }

   public Cell getGraphRoot() {
      return (Cell) getChildAt(getRoot(), 0);
   }

   public Object[] cloneCells(Object[] cells, boolean includeChildren, boolean cloneId) {
      Map mapping = new Hashtable();
      Object[] clones = new Object[cells.length];

      for (int i = 0; i < cells.length; i++) {
         clones[i] = cloneCell(cells[i], mapping, includeChildren, cloneId);
      }

      for (int i = 0; i < cells.length; i++) {
         restoreClone(clones[i], cells[i], mapping);
      }

      return clones;
   }

   protected Object cloneCell(Object cell, Map mapping, boolean includeChildren,
         boolean cloneID) {
      if (cell instanceof mxICell) {

         try {
            mxICell mxc = (mxICell) ((mxICell) cell).clone();


            if (cloneID) {
               mxc.setId(((mxICell) cell).getId());
            }
            mapping.put(cell, mxc);



            if (includeChildren) {
               int childCount = getChildCount(cell);

               for (int i = 0; i < childCount; i++) {


                  Object clone = cloneCell(getChildAt(cell, i), mapping, true);
                  mxc.insert((mxICell) clone);



               }
            }

            return mxc;

         } catch (Exception e) {
         }
      }

      return null;
   }

   public int setCellType(Object cell, int type) {
      execute(new CellTypeChanged(this, cell, type));

      return type;
   }

   protected int getType(Object cell) {
      if (cell instanceof Cell) {
         return ((Cell) cell).getType();
      }
      return -1;
   }

   protected int cellTypeChange(Object cell, int type) {
      int previous = getType(cell);
      ((Cell) cell).setType(type);

      return previous;
   }

   public Cell[] getChildGroupCells(Object parent) {
      int childCount = getChildCount(parent);
      List<Cell> result = new ArrayList(childCount);

      for (int i = 0; i < childCount; i++) {
         Object child = getChildAt(parent, i);

         if (child instanceof Cell && !((Cell) child).isCollapsed() && (((Cell) child).isGroup() || (((Cell) child).isSOP()))) {
            result.add((Cell) child);
         }
      }

      return result.toArray(new Cell[0]);
   }

   public static class CellTypeChanged extends mxAtomicGraphModelChange {

      /**
       *
       */
      protected Object cell;
      /**
       *
       */
      protected int type,  previousType;

      /**
       *
       */
      public CellTypeChanged(mxGraphModel model, Object cell, int type) {
         super(model);
         this.cell = cell;
         this.type = type;
         this.previousType = this.type;
      }

      /**
       * @return the cell
       */
      public Object getCell() {
         return cell;
      }

      public int getType() {
         return type;
      }

      public int getPrevious() {
         return previousType;
      }

      /**
       * Changes the root of the model.
       */
      public void execute() {
         type = previousType;
         previousType = ((SPGraphModel) model).cellTypeChange(cell, previousType);
      }
   }
}
