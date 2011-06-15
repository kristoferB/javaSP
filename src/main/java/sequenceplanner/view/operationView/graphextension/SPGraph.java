package sequenceplanner.view.operationView.graphextension;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

import sequenceplanner.model.Model;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.Constants;
import sequenceplanner.view.operationView.autoSOP.SequenceCreator;
import sequenceplanner.view.operationView.autoSOP.SopNode;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxEdgeStyle.mxEdgeStyleFunction;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import com.mxgraph.view.mxGraphView;
import com.mxgraph.view.mxPerimeter.mxPerimeterFunction;

/**
 *
 * @author Erik Ohlson
 */
public class SPGraph extends mxGraph {

   /**
    * Global graph options
    */
   protected boolean OPTION_SHOW_STARTCONDITION = true;
   protected boolean OPTION_SHOW_STOPCONDITION = true;
   private final static int TOP = 0;
   private final static int BOTTOM = 1;
   private final static int GRP_TOP = 3;
   private final static int GRP_BOT = 4;
   private final static int GRP_INSIDE = 5;
   private final static int GRP_MARK = 10;
   
   /**
    * fontGraphics used to calculate fontsizes for autosizeing.
    */
   protected transient static Graphics fontGraphics;
   // Logging for this class
   private static Logger logger = Logger.getLogger(SPGraph.class);


   static {
      try {
         fontGraphics = new BufferedImage(30, 20, BufferedImage.TYPE_BYTE_GRAY).getGraphics();
      } catch (Exception e) {
      }
   }
   //Global options for drawing of operations
   public static final Insets opInset = new Insets(7, 10, 10, 7);
   // How many signs should be displayed for stop and start condition
   public static final int cutOff = 100;

   private boolean showPath = true;

   public SPGraph(mxIGraphModel model) {
      super(model);
      setAutoSizeCells(true);

      setGridSize(20);
      setCellsEditable(false);
      setLabelsVisible(false);
      setCellsBendable(false);
      setCellsDisconnectable(false);


      addListener(mxEvent.MOVE_CELLS, new mxIEventListener() {

         @Override
         public void invoke(Object source, mxEventObject evt) {
            updateParentsSize((Object[]) evt.getArgAt(0));
         }
      });

      getModel().addListener(mxEvent.ADD_CELLS, new mxIEventListener() {

         @Override
         public void invoke(Object source, mxEventObject evt) {
            updateParentsSize((Object[]) evt.getArgAt(0));
         }
      });





      mxIEventListener update = new mxIEventListener() {

         @Override
         public void invoke(Object source, mxEventObject evt) {
            majorUpdate();
         }
      };
      addListener(mxEvent.CELLS_ADDED, update);
      addListener(mxEvent.CELLS_REMOVED, update);



      selectionModel = new mxGraphSelectionModel(this) {

         public Object[] selectSequence(Object[] cells) {
            ArrayList<Object> tCells = new ArrayList<Object>();

            for (int i = 0; i < cells.length; i++) {
               mxCell object = (mxCell) cells[i];
               if (object != null && !object.isEdge()) {
                  tCells.add(object);
               }
            }

            LinkedList<LinkedList<mxCell>> input = getSortedSequences(tCells, false);

            tCells = new ArrayList<Object>();


            for (Iterator<LinkedList<mxCell>> iterator = input.iterator(); iterator.hasNext();) {
               LinkedList<mxCell> linkedList = iterator.next();

               if (linkedList.getFirst().isEdge()) {
                  linkedList.removeFirst();
               }

               if (linkedList.getLast().isEdge()) {
                  linkedList.removeLast();
               }

               tCells.addAll(linkedList);
            }

            return tCells.toArray();
         }

         @Override
         public void setCells(Object[] cells) {
            if (cells != null) {
               cells = selectSequence(cells);
               super.setCells(cells);
            }
         }

         @Override
         public void addCells(Object[] cells) {
            HashSet<Object> tmpArray = new HashSet<Object>();

            for (int i = 0; i < cells.length; i++) {
               tmpArray.add(cells[i]);
            }

            //Need to insert the already selected cells to select edges
            tmpArray.addAll(this.cells);


            cells = selectSequence(tmpArray.toArray());
            super.addCells(cells);
         }

         @Override
         public void removeCells(Object[] cells) {
            LinkedList<Object> tCells = new LinkedList<Object>();

            for (int i = 0; i < cells.length; i++) {


               mxCell cell = ((mxCell) cells[i]);

               for (int j = 0; j < cell.getEdgeCount(); j++) {
                  if (isSelected(cell.getEdgeAt(j))) {
                     tCells.add(cell.getEdgeAt(j));
                  }
               }
               tCells.add(cells[i]);

            }
            super.removeCells(tCells.toArray());
         }
      };
   }

   public void setShowPath(boolean showPath) {
      this.showPath = showPath;
   }

   public boolean isShowPath() {
      return showPath;
   }

   

    @Override
    public mxIGraphModel getModel() {
        return super.getModel();
    }


   public void majorUpdate() {
      getGraphModel().reloadNamesCache();
      getGraphModel().updatePreconditions(getGraphModel().getGraphRoot(), isShowPath());
      updateSizeOfOperations();
   }

   @Override
   public String getToolTipForCell(Object cell) {
      if (cell instanceof Cell) {
         Cell c = (Cell) cell;

         if (c.isOperation() || c.isSOP()) {
            OperationData d = (OperationData) c.getValue();
            return "Operation id: " + Integer.toString(d.getId());
         }
      }

      return "";
   }

   @Override
   protected mxGraphView createGraphView() {
      mxGraphView gv = new mxGraphView(this) {

         @Override
         public mxEdgeStyleFunction getEdgeStyle(mxCellState edgeState, List points, Object source, Object target) {
            if (source instanceof mxCell && target instanceof mxCell) {
               mxCell s = (mxCell) source;
               mxCell t = (mxCell) target;
               mxCell e = (mxCell) edgeState.getCell();

               if ((((Cell) s).isGroup() && e.getParent() == s) || ((Cell) t).isGroup() && e.getParent() == t) {
                  return super.getEdgeStyle(edgeState, points, source, target);
               }

               if (s.getGeometry().getY() + s.getGeometry().getHeight() < t.getGeometry().getY()) {
                  return mxEdgeStyle.ElbowConnector;
               } else {
                  return PerimeterFunctions.ElbowAbove;
               }
            } else {
               return super.getEdgeStyle(edgeState, points, source, target);
            }
         }

         @Override
         public mxPerimeterFunction getPerimeterFunction(mxCellState state) {
            Cell c = (Cell) state.getCell();
            if (c.isParallel() || c.isArbitrary()) {
               return PerimeterFunctions.parallelPerimeter;
            } else if (c.isAlternative()) {
               return PerimeterFunctions.alternativePerimeter;
            } else {
               return PerimeterFunctions.operationPerimeter;
            }
         }

         @Override
         public void scaleAndTranslate(double scale, double dx, double dy) {
            if (scale > 1) {
               scale = 1;
               dx = dy = 0;
            }
            super.scaleAndTranslate(scale, dx, dy);
         }

         @Override
         public void setScale(double value) {
            if (value > 1) {
               value = 1;
            }
            super.setScale(value);
         }
      };

      return gv;
   }

   public SPGraphModel getGraphModel() {
      return ((SPGraphModel) model);
   }

   public void updateSizeOfOperations() {
      Stack<Cell> toDo = new Stack<Cell>();

      toDo.push(getGraphModel().getGraphRoot());


      while (!toDo.isEmpty()) {
         Cell[] sops = getGraphModel().getChildSOP(toDo.pop());

         for (int i = 0; i < sops.length; i++) {
            if (sops[i].isOperation()) {
               updateCellSize(sops[i], true);
            }

            toDo.push(sops[i]);
         }
      }
   }

   public boolean isOperationIdPresent(int id) {
      Stack<Cell> toDo = new Stack<Cell>();

      toDo.push(getGraphModel().getGraphRoot());


      while (!toDo.isEmpty()) {
         Cell[] sops = getGraphModel().getChildSOP(toDo.pop());

         for (int i = 0; i < sops.length; i++) {
            if (sops[i].getUniqueId() == id) {
               return true;
            }

            toDo.push(sops[i]);
         }
      }

      return false;
   }

   @Override
   public boolean isCellFoldable(Object cell, boolean collapse) {
      if (isSOP(cell)) {
         return true;
      }

      return super.isCellFoldable(cell, collapse);
   }

   @Override
   public boolean isCellResizable(Object cell) {
      if (isOperation(cell)) {
         return false;
      }

      return super.isCellResizable(cell);
   }

   @Override
   public void updateAlternateBounds(Object cell, mxGeometry g, boolean willCollapse) {

      if (cell != null && g != null) {
         Cell c = (Cell) cell;
         mxRectangle bounds = new mxRectangle();

         if (c.isSOP() && willCollapse) {
            double center = g.getCenterX();

            bounds = getPreferredSizeForCell(cell);
            bounds.setY(g.getY());
            bounds.setX(center - bounds.getWidth() / 2);

         } else if (g.getAlternateBounds() != null) {
            double center = g.getCenterX();
            bounds.setX(center - g.getAlternateBounds().getWidth() / 2);
            bounds.setY(g.getY());
            bounds.setWidth(g.getAlternateBounds().getWidth());
            bounds.setHeight(g.getAlternateBounds().getHeight());

         } else {
            bounds = (mxRectangle) g.clone();
         }

         if (bounds == null) {
            logger.debug("SPGraph.updateAlternateBouds: b = null");
         }

         g.setAlternateBounds(bounds);
      }
   }

   @Override
   public mxRectangle getPreferredSizeForCell(Object cell) {

      Cell c = (Cell) cell;

      Object o = c.getValue();


      if (o instanceof OperationData && (c.isOperation() || (c.isSOP() && c.isCollapsed()))) {
         //TODO insert check for collapsed SOP or OP
         OperationData userFile = (OperationData) o;


         Font lf = custom2DCanvas.labelFont;
         Font sf = custom2DCanvas.syncFont;

         double height = 0, width = 0;

         String value = userFile.getPrecondition();
         if (value.length() > cutOff) {
            value = value.substring(0, cutOff) + "...";
         }

         Rectangle2D rsf = fontGraphics.getFontMetrics(sf).getStringBounds(value, fontGraphics);

         if (OPTION_SHOW_STARTCONDITION && !value.isEmpty()) {
            height += rsf.getHeight() + 5;
            width = width < rsf.getWidth() ? rsf.getWidth() : width;
         }


         value = userFile.getName();
         if (value.length() > cutOff) {
            value = value.substring(0, cutOff) + "...";
         }

         Rectangle2D rlf = fontGraphics.getFontMetrics(lf).getStringBounds(value, fontGraphics);

         if (true) {
            height += rlf.getHeight();
            width = width < rlf.getWidth() ? rlf.getWidth() : width;
         }

         value = userFile.getPostcondition();
         if (value.length() > cutOff) {
            value = value.substring(0, cutOff) + "...";
         }

         Rectangle2D rsf2 = fontGraphics.getFontMetrics(sf).getStringBounds(value, fontGraphics);

         if (OPTION_SHOW_STOPCONDITION && !value.isEmpty()) {
            height += rsf2.getHeight() + 5;
            width = width < rsf2.getWidth() ? rsf2.getWidth() : width;
         }

         width += opInset.left + opInset.right;

         mxRectangle r = new mxRectangle(0, 0, width + 3, height + opInset.top + opInset.bottom + 3);

         return r;
      } else if (c.isSOP()) {
         return c.getGeometry();
      }
      return null;
   }

   public static mxRectangle getSizeForOperation(Object cell) {
      Cell c = (Cell) cell;

      Object o = c.getValue();

      if (o instanceof OperationData && (c.isOperation() || (c.isSOP() && c.isCollapsed()))) {
         OperationData userFile = (OperationData) o;

         double height = 0, width = 0;

         String value = userFile.getName();
         if (value.length() > cutOff) {
            value = value.substring(0, cutOff) + "...";
         }

         Rectangle2D rlf = fontGraphics.getFontMetrics(custom2DCanvas.labelFont).getStringBounds(value, fontGraphics);

         height += rlf.getHeight();
         width = width < rlf.getWidth() ? rlf.getWidth() : width;

         width += opInset.left + opInset.right;
         mxRectangle r = new mxRectangle(0, 0, width + 3, height + opInset.top + opInset.bottom + 3);

         return r;
      }
      return null;
   }

   public void setPreferenceValue(Cell cell, Object value) {
      setSelectionCell(null);

      if (value instanceof OperationData) {

         model.beginUpdate();
         try {
            OperationData d = (OperationData) value;
            getModel().setValue(cell, value);
              
         } finally {
            model.endUpdate();
         }
      }
      
      setSelectionCell(cell);
   }

   public void setValue(Object cell, Object value) {
      if (value instanceof OperationData) {

         model.beginUpdate();
         try {
            OperationData d = (OperationData) value;
            getModel().setValue(cell, value);
            d.setPrecondition(Model.updateCondition(((SPGraphModel) getModel()).getNameCache(),
                  d.getSequenceCondition(), d.getResourceBooking()));
            updateCellSize(cell, true);
  
  
         } finally {
            model.endUpdate();
         }
      }
   }

   @Override
   public void drawStateWithLabel(mxICanvas canvas, mxCellState state, String label) {
      Object cell = (state != null) ? state.getCell() : null;


      if (cell != null && cell != view.getCurrentRoot() && cell != model.getRoot()) {

         if (state != null) {

            Object obj = null;
            Object lab = null;

            if (model.isVertex(cell)) {


               if (canvas instanceof custom2DCanvas) {

                  ((custom2DCanvas) canvas).drawVertex(state);

               } else if (canvas instanceof mxImageCanvas && ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof custom2DCanvas) {

                  ((custom2DCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas()).drawVertex(state);
               }

            } else if (model.isEdge(cell)) {
               canvas.drawEdge(state.getAbsolutePoints(), state.getStyle());
            }

            // Invokes the cellDrawn callback with the object which was created
            // by the canvas to represent the parent graphically
            if (obj != null) {
               cellDrawn(cell, obj, lab);
            }

         }
      }
   }

   public void insertOperation(Object parent, Point insertPoint, int id) {
      mxCell c = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION, insertPoint);

      mxRectangle ps = getPreferredSizeForCell(c);
      mxGeometry geo = c.getGeometry();
      geo.setHeight(ps.getHeight());
      geo.setWidth(ps.getWidth());

      boolean canBeParent = true;
      if (parent != null) {
         canBeParent = acceptDrop(parent, c);

         mxRectangle r = getCellBounds(parent);
         geo.setX(geo.getX() - r.getX());
         geo.setY(geo.getY() - r.getY());
      } else {
         parent = getDefaultParent();
      }

      if (canBeParent) {
         addCell(c, parent);
      }
   }

   public static String getShapefromString(Object input) {
      if (input instanceof mxCell) {
         mxCell cell = (mxCell) input;

         String style = cell.getStyle();
         String[] pairs = style.split(";");

         for (int j = 0; j < pairs.length; j++) {
            String[] p2 = pairs[j].split("=");

            if (p2.length > 1 && p2[0].equals(mxConstants.STYLE_SHAPE)) {
               return p2[1];
            }
         }
      }

      return "";
   }

   public boolean acceptDrop(Object parent, Object child) {


//      if(parent instanceof mxCell && child instanceof mxCell) {
//         return isSOP(parent);
//      }

      return true;
   }

   @Override
   public boolean isDropEnabled() {
      return true;
   }

   public boolean isSOP(Object cell) {
      return isType(cell, false, true, false, false, false);
   }

   public boolean isOperation(Object cell) {
      return isType(cell, true, false, false, false, false);
   }

   public boolean isType(Object cell, boolean operation,
         boolean sop, boolean parallel, boolean alternative, boolean arbitrary) {
      Cell c = (Cell) cell;

      return (c.isSOP() && sop) || (c.isOperation() && operation) || (c.isAlternative() && alternative) || (c.isParallel() && parallel) || (c.isArbitrary() && arbitrary);
   }

   /**
    *
    * @param cell
    * @return All cells that is in sequence with the inputed parent and
    * 			in the same parent.
    */
   public List<mxCell> getSequence(mxCell cell) {
      ArrayList<mxCell> ar = new ArrayList<mxCell>();
      ar.addAll(getCellsHereTo(cell, true));
      ar.addAll(getCellsHereTo(cell, false));
      return ar;
   }

   /**
    *
    * @param start
    * @param first
    * @return The last or the first parent in the sequence where the input is included
    */
   public mxCell getTerminalCell(mxCell start, boolean first) {
      List<mxCell> c = getCellsHereTo(start, first);
      return c.get(c.size() - 1);
   }

   public List<mxCell> getCellsHereTo(mxCell start, boolean first) {
      return getCellsHereTo(start, null, first);
   }

   /**
    *
    * @param start
    * @param stop
    * @param first
    *
    * @return List of all cells and vertices from start parent to start/end of sequence
    */
   public List<mxCell> getCellsHereTo(mxCell start, mxCell stop, boolean first) {

      ArrayList<mxCell> cells = new ArrayList<mxCell>();

      mxCell tempCell = start;

      while (tempCell != stop) {
         cells.add(tempCell);
         tempCell = getNextCell(tempCell, first);
      }
      return cells;
   }

   public mxCell getEdge(mxICell cell, Object parent, boolean incomming) {

      for (int i = 0; i < cell.getEdgeCount(); i++) {
         mxCell edge = (mxCell) cell.getEdgeAt(i);

         if (edge.getTerminal(!incomming) == cell && edge.getParent() == parent) {
            return edge;
         }
      }

      return null;
   }

   public LinkedList<mxCell> getSortedSequence(Object[] cells) {

      ArrayList<Object> c = new ArrayList<Object>();

      cells = filterVertices(cells);

      for (int i = 0; i < cells.length; i++) {
         c.add(cells[i]);
      }

      LinkedList<LinkedList<mxCell>> output = getSortedSequences(c, false);

      if (output.size() > 1) {
         return null;
      } else {
         return output.getFirst();
      }
   }

   public LinkedList<LinkedList<mxCell>> getSortedSequences(Object[] cells) {
      ArrayList list = new ArrayList();
      for (int i = 0; i < cells.length; i++) {
         list.add(cells[i]);
      }

      return getSortedSequences(list, false);
   }

   /**
    *
    * @param cells
    * @param straightSequence
    * @return a list of ordered sequences.
    */
   public LinkedList<LinkedList<mxCell>> getSortedSequences(ArrayList<Object> cells,
         boolean straightSequence) {

      if (cells.isEmpty()) {
         return new LinkedList<LinkedList<mxCell>>();
      }

      LinkedList<LinkedList<mxCell>> output = new LinkedList<LinkedList<mxCell>>();

      LinkedList<mxCell> list = new LinkedList<mxCell>();

      mxCell cell = null;


      cell = (mxCell) cells.get(0);

      mxCell c2 = null;

      while (cells.contains(cell)) {
         list.addFirst(cell);
         c2 = getNextCell(cell, true);

         if (c2 != null) {
            list.addFirst((mxCell) getEdgesBetween(c2, cell)[0]);
         } else {
            mxCell ed = getEdge(cell, cell.getParent(), true);

            if (ed != null) {
               list.addFirst(ed);
            }
         }

         cells.remove(cell);
         cell = c2;
      }

      cell = list.getLast();
      mxCell tempCell = cell;
      cells.add(cell);

      while (cells.contains(cell)) {


         list.addLast(cell);


         c2 = getNextCell(cell, false);

         if (c2 != null) {
            list.addLast((mxCell) getEdgesBetween(cell, c2)[0]);
         } else {
            mxCell ed = getEdge(cell, cell.getParent(), false);

            if (ed != null) {
               list.addLast(ed);
            }
         }

         cells.remove(cell);
         cell = c2;
      }
      list.remove(tempCell);

      HashSet<mxCell> set = new HashSet<mxCell>();
      set.addAll(list);

      output.addLast(list);

      if (!cells.isEmpty()) {
         output.addAll(getSortedSequences(cells, straightSequence));
      }

      return output;
   }

   public Cell getNextCell(mxCell cell, boolean before) {
      return getNextCell((Cell) cell, before, false);
   }

   public Cell getNextCell(Cell cell, boolean before, boolean parentNext) {
      mxCell edge = getEdge(cell, cell.getParent(), before);

      if (edge != null) {
         Cell term = (Cell) edge.getTerminal(before);

         boolean next = term.equals(cell.getParent());
         next = parentNext ? next : !next;

         if (next) {
            return term;
         }
      }

      return null;
   }

   public Cell getAlwaysNextCell(mxCell cell, boolean before) {
      mxCell edge = getEdge(cell, cell.getParent(), before);

      if (edge != null) {
         return (Cell) edge.getTerminal(before);
      }

      return null;
   }

   public Cell getPreviousOperation(Cell cell) {
      Cell prevCell = getAlwaysNextCell(cell, true);

      if (prevCell == null) {
         return null;
      } else if (prevCell.isOperation() || prevCell.isSOP()) {
         return prevCell;
      } else if (prevCell.isAlternative() || prevCell.isArbitrary() || prevCell.isParallel()) {
         return getPreviousOperation(prevCell);
      } else {
         logger.error("getPreviousOperation has an previous cell that is other than valid cell");
      }

      return null;

   }

   /**
    *
    * @param operation has to be an group cell
    * @return
    */
   public LinkedList<Cell> getPreviousOperations(Cell operation) {
      LinkedList<Cell> output = new LinkedList<Cell>();

      Object[] edges = SPGraphModel.getIncomingEdges(getModel(), operation);


      for (int i = 0; i < edges.length; i++) {
         if (edges[i] instanceof mxCell && ((mxCell) edges[i]).getParent() == operation) {
            mxCell edge = (mxCell) edges[i];

            Cell tempCell = (Cell) edge.getSource();

            if (tempCell.isGroup()) {
               output.addAll(getPreviousOperations(tempCell));
            } else if (tempCell.isOperation() || tempCell.isSOP()) {
               output.add(tempCell);
            }
         }
      }
      return output;
   }

   public Object[] filterVertices(Object[] cells) {
      ArrayList<Object> output = new ArrayList<Object>();

      if (cells != null) {
         for (int i = 0; i < cells.length; i++) {
            if (getModel().isVertex(cells[i])) {
               output.add(cells[i]);
            }
         }
      }
      return output.toArray();
   }

   public void insertNewCell(Cell oldCell, Cell newCell, boolean before) {
      getModel().beginUpdate();
      try {
         getModel().beginUpdate();
         try {
            Object arch = null;
            arch = getEdge(oldCell, oldCell.getParent(), before);

            mxGeometry oldGeo = oldCell.getGeometry();


            mxPoint p = new mxPoint();

            p.setX(oldGeo.getCenterX() - newCell.getGeometry().getWidth() / 2);

            Object[] cells = new Object[2];
            
            if (before) {
               p.setY(oldGeo.getY() - Constants.BEFORE_CELL - newCell.getGeometry().getHeight());
            } else {
               p.setY(oldGeo.getY() + oldGeo.getHeight() + Constants.AFTER_CELL);
            }

            if (p.getY() < 0) {
               p.setY(0);
            }

            if (p.getX() < 0) {
               p.setX(0);
            }


            newCell.getGeometry().setX(p.getX());
            newCell.getGeometry().setY(p.getY());

            
            mxCell edge = CellFactory.getInstance().getEdge(true,false);
            getModel().add(oldCell.getParent(), edge, 0);

            getModel().setTerminal(edge, oldCell, !before);
            getModel().setTerminal(edge, newCell, before);
             System.out.println(newCell.getType());
                                 System.out.println(oldCell.getType());

            if (arch != null) {
               getModel().setTerminal(arch, newCell, !before);
            }

            cells[0] = ((SPGraphModel) getModel()).add(oldCell.getParent(), newCell, 0);


         } finally {
            getModel().endUpdate();
         }
         updateCellSize(newCell);
         updateParentSize(oldCell.getParent());
      } finally {
         getModel().endUpdate();
      }

   }

   public void insertGroupNode(mxCell parent, mxPoint clickPoint, mxCell insertCell) {
      mxCell edge1 = CellFactory.getInstance().getEdge(false,false);
      mxCell edge2 = CellFactory.getInstance().getEdge(false,false);

//      Object[] cells = new Object[3];

      model.beginUpdate();
      try {
         addCell(edge1, parent, 0, parent, insertCell);
         addCell(edge2, parent, 0, insertCell, parent);
         addCell(insertCell, parent);
      } finally {
         model.endUpdate();
      }
   }

   public void cellTypeChanged(Object cell, int type) {
      model.beginUpdate();
      try {
         ((SPGraphModel) model).setCellType(cell, type);
      } finally {
         model.endUpdate();
      }
   }

   @Override
   public boolean isSwimlane(Object cell) {
      return isSOP(cell);
   }

   @Override
   public void extendParent(Object cell) {
      if (cell instanceof mxCell) {
         updateParentSize(((mxCell) cell).getParent());
      }
   }

   public void updateParentsSize(Object[] input) {
      HashSet<Object> parents = new HashSet<Object>();

      if (input != null) {

         for (int i = 0; i < input.length; i++) {
            if (input[i] instanceof mxCell) {
               parents.add(((mxCell) input[i]).getParent());
            }
         }

         for (Iterator<Object> iterator = parents.iterator(); iterator.hasNext();) {
            updateParentSize(iterator.next());
         }
      }
   }

   /**
    * Only update the parentsise if it is a sop and not collapsed
    * @param input
    */
   public void updateParentSize(Object input) {
      updateParentSize(input, false);
   }

   //TODO Finish autosize of groups
   public void updateParentSize(Object input, boolean updateAlternate) {
      model.beginUpdate();
      try {

         if (input instanceof mxCell && !input.equals(getDefaultParent()) && !getModel().isCollapsed(input)) {
            Cell cell = (Cell) input;

            Object[] children = getChildVertices(cell);

            Rectangle re = null;
            if (children.length > 0) {
               re = new Rectangle(((mxCell) children[0]).getGeometry().getRectangle());
            }

            for (int j = 1; j < children.length; j++) {
               mxCell child = (mxCell) children[j];

               if (child.isVertex()) {
                  re = re.union(child.getGeometry().getRectangle());
               }
            }

            mxRectangle te = new mxRectangle();
            if (re != null) {
               te = new mxRectangle(re);


               Insets group = new Insets(0, 0, 0, 0);
               if (cell.isAlternative()) {
                  group = Constants.ALTERNATIVE_INSET;
               } else if (cell.isArbitrary()) {
                  group = Constants.ARBITRARY_INSET;
               } else if (cell.isSOP()) {
                  group = Constants.SOP_INSET;
               } else if (cell.isParallel()) {
                  group = Constants.PARALLEL_INSET;
               }

               te.setX(te.getX() - group.left);
               te.setWidth(te.getWidth() + group.left + group.right);
               te.setY(te.getY() - group.top);
               te.setHeight(te.getHeight() + group.top + group.bottom);

               double dx = te.getX();
               double dy = te.getY();

               mxGeometry geo = cell.getGeometry();

               te.setX(geo.getX() + dx);
               te.setY(geo.getY() + dy);

               if (updateAlternate) {
                  cell.getGeometry().setAlternateBounds(te);
               } else {
                  resizeCell(cell, te);
               }

               for (int j = 0; j < children.length; j++) {
                  mxCell child = (mxCell) children[j];

                  if (child.isVertex()) {

                     mxGeometry geoChild = model.getGeometry(child);
                     geoChild.setX(geoChild.getX() - dx);
                     geoChild.setY(geoChild.getY() - dy);
                     model.setGeometry(child, geoChild);

                  }
               }

               if (!updateAlternate) {
                  updateParentSize(getModel().getParent(cell));
               }
            }
         }
      } finally {
         getModel().endUpdate();
      }
   }

   public boolean isNamePresent(String name, Cell node) {

      int id = ((Data) node.getValue()).getId();

      Cell tempN = (Cell) node.getParent();

      while (!(tempN.isSOP() || node != getDefaultParent())) {
         tempN = (Cell) tempN.getParent();
      }

      if (((Data) tempN.getValue()).getId() != -1) {
         Cell[] childs = ((SPGraphModel) getModel()).getChildSOP(tempN);

         for (int i = 0; i < childs.length; i++) {
            Data d = (Data) childs[i].getValue();
            if (d.getId() != id && name.equals(d.getName())) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Not really recursive becaus autoArrange has to be called bottom up.
    * @param parent
    * @return
    */
   public boolean recursiveAutoArrange(Cell parent) {
      Cell[] vert = ((SPGraphModel) getModel()).getChildGroupCells(parent);

      for (int i = 0; i < vert.length; i++) {
         Cell cell = vert[i];
         recursiveAutoArrange(cell);
      }

      autoArrange(parent);

      return true;
   }

   public boolean autoArrange(Cell parent) {

      model.beginUpdate();
      try {

         Object[] vert = SPGraphModel.getChildVertices(model, parent);

         LinkedList<LinkedList<mxCell>> out = filterVertices(getSortedSequences(vert));


         double seqX = 0;

         while (!out.isEmpty()) {
            seqX += Constants.SEQUENCE_DISTANCE;


            //Find out what of the sequences is farthest to the right.
            int selected = 0;
            double minX = 9999999;

            for (int i = 0; i < out.size(); i++) {
               LinkedList<mxCell> l = out.get(i);
               if (l.getFirst().getGeometry().getX() < minX) {
                  minX = l.getFirst().getGeometry().getX();
                  selected = i;
               }
            }

            LinkedList<mxCell> l = out.get(selected);

            double width = 0;
            for (mxCell child : l) {
               if (child.isVertex() && child.getGeometry().getWidth() > width) {
                  width = child.getGeometry().getWidth();
               }
            }

            double center = snap(seqX + width / 2);
            double height = 0;

            if (parent == getDefaultParent()) {
               height = Constants.ROOTBORDER_DISTANCE.top;
            }

            for (mxCell cell : l) {
               mxGeometry geo = (mxGeometry) cell.getGeometry().clone();
               geo.setY(height);
               height += geo.getHeight() + Constants.AFTER_CELL;
               geo.setX(center - geo.getWidth() / 2);
               ((SPGraphModel) model).setGeometry(cell, geo);
            }
            out.remove(selected);
            seqX += (center - seqX + width / 2);
         }


         //TODO this could be done only once per low level child.
         updateParentSize(parent);

      } finally {
         model.endUpdate();
      }

      return true;
   }

   public void selectSequences() {
      Object[] cells = getSelectionCells();

      HashSet<Object> selecedCells = new HashSet<Object>();

      for (int i = 0; i < cells.length; i++) {
         mxCell cell = (mxCell) cells[i];

         if (!cell.isEdge() && !selecedCells.contains(cell)) {
            List<mxCell> t = getSequence(cell);

            for (Iterator<mxCell> iterator = t.iterator(); iterator.hasNext();) {
               Object object = (Object) iterator.next();
               selecedCells.add(object);
            }

         }
      }

      getSelectionModel().setCells(selecedCells.toArray());
   }

   public void selectGroup() {
      Object sel = getSelectionCell();
      clearSelection();
      selectVertices(sel);
   }

   /**
    * Delete marked cells if they are in a sequence.
    */
   public void deleteMarkedCells() {
      Object[] cells = filterVertices(this.getSelectionCells());

      if (cells.length > 0) {

         ArrayList<LinkedList<mxCell>> tempCells = this.getSortedSequences(cells, false);

         model.beginUpdate();
         try {
            for (Iterator<LinkedList<mxCell>> iterator = tempCells.iterator(); iterator.hasNext();) {
               LinkedList<mxCell> list = iterator.next();

               disconnectSequence(list);


               removeCells(list.toArray());
            }
         } finally {
            model.endUpdate();
         }
      }
   }

   protected LinkedList<mxCell> disconnectSequence(LinkedList<mxCell> seq) {
      if (seq.getFirst().isEdge() && seq.getLast().isEdge()) {
         Object tar = seq.getLast().getTarget();
         Object src = seq.getFirst().getSource();
         Object par = getModel().getParent(seq.getFirst());

         if (tar != par) {
            connectCell(seq.getFirst(), tar, false);
            seq.removeFirst();
         } else if (tar == par && src != par) {
            connectCell(seq.getLast(), src, true);

            seq.removeLast();
         }
      }

      if (seq.getFirst().isEdge()) {
         removeCells(new Object[]{seq.getFirst()});
         seq.removeFirst();
      }

      if (seq.getLast().isEdge()) {
         removeCells(new Object[]{seq.getLast()});
         seq.removeLast();
      }

      return seq;
   }


   //TODO Implement hashCode for mxCell for more consistent solution.
   /**
    * if not in sequcen it returns null.
    *
    * @param cells
    * @param haltOnMultilayer
    * @return
    */
   public ArrayList<LinkedList<mxCell>> getSortedSequences(Object[] cells, boolean haltOnMultilayer) {
      HashMap<mxCell, LinkedList<mxCell>> seq = new HashMap<mxCell, LinkedList<mxCell>>();

      cells = filterVertices(cells);

      mxCell parent = null;

      if (cells.length > 0) {
         parent = (mxCell) cells[0];
      }

      for (int i = 0; i < cells.length; i++) {
         mxCell cell = (mxCell) cells[i];

         if (haltOnMultilayer && parent.equals(getModel().getParent(cell))) {
            // The marked cells does not have the same parent
            return null;
         }

         mxCell terminal = getTerminalCell(cell, true);
         if (!seq.containsKey(terminal)) {
            seq.put(terminal, new LinkedList<mxCell>());
         }

         seq.get(terminal).add(cell);
      }

      Set<mxCell> keys = seq.keySet();

      ArrayList<LinkedList<mxCell>> output = new ArrayList<LinkedList<mxCell>>(seq.size());

      for (Iterator<mxCell> iterator = keys.iterator(); iterator.hasNext();) {
         LinkedList<mxCell> temp = seq.get(iterator.next());

         temp = getSortedSequence(temp.toArray());

         if (temp == null) {
            // Several operations in the same sequence is selected with
            // unselected operations in between.
            return null;
         } else {
            output.add(temp);
         }
      }

      return output;
   }

   @Override
   public Object[] moveCells(Object[] cells, double dx, double dy,
         boolean clone, Object target, Point location) {
      if (cells != null && (dx != 0 || dy != 0 || clone || target != null)) {
         model.beginUpdate();
         try {
            if (clone) {
               cells = cloneCells(cells, isCloneInvalidEdges());

               if (target == null) {
                  target = getDefaultParent();
               }
            }

            cellsMoved(cells, dx, dy, !clone && isDisconnectOnMove() && isAllowDanglingEdges(), target == null);



            if (target != null) {
               Integer index = model.getChildCount(target);
               cellsAdded(cells, target, index, location);
            }
         } finally {
            model.endUpdate();
         }

         // Dispatches a move event
         fireEvent(mxEvent.MOVE_CELLS, new mxEventObject(new Object[]{
                  cells, dx, dy, clone, target, location}));
      }

      return cells;
   }

   @Override
   public void cellsMoved(Object[] cells, double dx, double dy,
         boolean disconnect, boolean constrain) {
      if (cells != null && (dx != 0 || dy != 0)) {
         model.beginUpdate();
         try {
            if (disconnect) {
               disconnectGraph(cells);
            }

            for (int i = 0; i < cells.length; i++) {
               mxGeometry geo = model.getGeometry(cells[i]);

               if (geo != null) {
                  if (gridEnabled) {
                     dx += geo.getCenterX();
                     dx = snap(dx);
                     dx -= geo.getCenterX();
                  }

                  geo = geo.translate(dx, dy);

                  if (geo.isRelative() && !model.isEdge(cells[i])) {
                     if (geo.getOffset() == null) {
                        geo.setOffset(new mxPoint(dx, dy));
                     } else {
                        mxPoint offset = geo.getOffset();

                        offset.setX(offset.getX() + dx);
                        offset.setY(offset.getY() + dx);
                     }
                  }

                  model.setGeometry(cells[i], geo);

               }
            }

            if (isResetEdgesOnMove()) {
               resetEdges(cells);
            }

            fireEvent(mxEvent.CELLS_MOVED, new mxEventObject(new Object[]{
                     cells, dx, dy, disconnect}));
         } finally {
            model.endUpdate();
         }
      }
   }

   public void cellsAdded(Object[] cells, Object parent, Integer index, Point location) {

      if (cells != null && parent != null && index != null) {
         ArrayList<LinkedList<mxCell>> sequences = getSortedSequences(cells, false);

         model.beginUpdate();
         try {
            if (model.isEdge(parent)) {
               LinkedList<mxCell> seq = null;
               if (sequences != null && sequences.size() == 1) {
                  seq = sequences.get(0);
                  disconnectSequence(seq);


               } else {
                  System.out.println("Only one edge can be added");
               }

            } else if (model.isVertex(parent) || parent == getDefaultParent()) {

               Cell pare = (Cell) parent;

               mxCellState tState = getView().getState(pare);

               //If released on the background, mark the movment as inside group.
               int type;

               if (!pare.equals(getDefaultParent())) {
                  type = getClickArea(pare, location, tState);
               } else {
                  type = GRP_INSIDE;
               }

               // Insert before or after
               if (type <= 4) {
                  if (sequences != null && sequences.size() == 1) {
                     insertSequence(sequences.get(0), pare, type, false);
                  } else {
                     System.out.println("Can only have one sequence markerked when attaching.");
                  }
               } else if (sequences != null) {
                  boolean par = pare.isParallel() || pare.isAlternative();


                  for (Iterator<LinkedList<mxCell>> iterator = sequences.iterator(); iterator.hasNext();) {

                     LinkedList<mxCell> seq = iterator.next();
                     Cell oldParent = (Cell) seq.getFirst().getParent();
                     insertSequence(seq, pare, type, par);


                     updateParentSize(oldParent);
                  }

               }

            }

            fireEvent(mxEvent.CELLS_ADDED, new mxEventObject(new Object[]{
                     cells, parent, index, null, null, true}));

         } finally {
            model.endUpdate();
         }
      }

   }

   public int getClickArea(Cell c, Point p, mxCellState marked) {

      if (c.isOperation() || (c.isSOP() && c.isCollapsed())) {
         if ((p.getY() - marked.getY()) < marked.getHeight() / 2) {
            return TOP;
         } else {
            return BOTTOM;
         }

      } else {

         if ((p.getY() - marked.getY()) < GRP_MARK) {
            return GRP_TOP;
         } else if (marked.getY() + marked.getHeight() - p.getY() < GRP_MARK) {
            return GRP_BOT;
         } else {
            return GRP_INSIDE;
         }

      }
   }

   public void insertSequence(LinkedList<mxCell> seq, mxCell target, int placement, boolean parType) {

      seq = disconnectSequence(seq);

      Object targetParent = null;
      Object sequenceParent = getModel().getParent(seq.getFirst());

      if (placement == TOP || placement == GRP_TOP) {
         targetParent = getModel().getParent(target);

         mxCell inEdge = getEdge(target, targetParent, true);

         if (inEdge != null) {
            connectCell(inEdge, seq.getFirst(), false);
         }

         mxCell outEdge = CellFactory.getInstance().getEdge(true,false);

         addEdge(outEdge, targetParent, seq.getLast(), target, null);



      } else if (placement == BOTTOM || placement == GRP_BOT) {
         targetParent = getModel().getParent(target);


         mxCell outEdge = getEdge(target, targetParent, false);

         if (outEdge != null) {
            connectCell(outEdge, seq.getLast(), true);
         }

         mxCell inEdge = CellFactory.getInstance().getEdge(true,false);

         addEdge(inEdge, targetParent, target, seq.getFirst(), null);



      } else if (placement == GRP_INSIDE && parType) {
         targetParent = target;

         mxCell parEdge = CellFactory.getInstance().getEdge(false,false);
         addEdge(parEdge, targetParent, target, seq.getFirst(), null);

         parEdge =
               CellFactory.getInstance().getEdge(false,false);
         addEdge(parEdge, targetParent, seq.getLast(), target, null);


      } else if (placement == GRP_INSIDE) {
         targetParent = target;
      }

//Add cells and move them to right position
      Object[] cells = seq.toArray();

      for (int i = 0; i < cells.length; i++) {
         if (cells[i] != targetParent) {

            int index = model.getChildCount(targetParent);

            if (targetParent != sequenceParent) {
               mxPoint o1 = view.getState(targetParent).getOrigin();
               mxCellState s1 = view.getState(sequenceParent);

               mxPoint o2 = null;

               if (s1 == null && sequenceParent == null) {
                  o2 = new mxPoint();
               } else {
                  o2 = s1.getOrigin();
               }


               mxGeometry geo = model.getGeometry(cells[i]);

               if (geo != null) {
                  double dx = o2.getX() - o1.getX();
                  double dy = o2.getY() - o1.getY();

                  model.setGeometry(cells[i], geo.translate(dx, dy));
               }

            } else {
               index--;
            }

            model.add(targetParent, cells[i], index);
         }
      }
   }

   @Override
   public boolean isValidDropTarget(Object cell, Object[] cells) {
      if (cell == getDefaultParent()) {
         return true;
      }


      LinkedList<LinkedList<mxCell>> sorted = getSortedSequences(filterVertices(cells));

      //What kind of cells can be last and first in dropped sequences
      List<Integer> allowedDrops = new LinkedList<Integer>();
      Cell target = ((Cell) cell);

      if (target.isSOP()) {
         allowedDrops.add(Constants.OP);
         allowedDrops.add(Constants.SOP);
         allowedDrops.add(Constants.ALTERNATIVE);
         allowedDrops.add(Constants.ARBITRARY);
         allowedDrops.add(Constants.PARALLEL);

      } else if (target.isAlternative()) {
         allowedDrops.add(Constants.OP);
         allowedDrops.add(Constants.SOP);
      } else if (target.isParallel()) {
         allowedDrops.add(Constants.OP);
         allowedDrops.add(Constants.SOP);
         allowedDrops.add(Constants.PARALLEL);
      } else if (target.isOperation()) {
         allowedDrops.add(Constants.OP);
         allowedDrops.add(Constants.SOP);
         allowedDrops.add(Constants.ALTERNATIVE);
         allowedDrops.add(Constants.ARBITRARY);
         allowedDrops.add(Constants.PARALLEL);
      } else {
         return false;
      }



      for (LinkedList<mxCell> sequence : sorted) {


         if (sequence.getFirst().isEdge()) {
            sequence.removeFirst();
         }

         if (sequence.getLast().isEdge()) {
            sequence.removeLast();
         }

         boolean sequenceAllowed = allowedDrops.contains(((Cell) sequence.getFirst()).getType()) && allowedDrops.contains(((Cell) sequence.getLast()).getType());

         if (!sequenceAllowed) {
            return false;
         }
      }

      return true;
   }

   public boolean hasGroupCellConnectingEdges(boolean top, Cell groupCell) {

      for (int i = 0; i < groupCell.getEdgeCount(); i++) {
         mxICell edge = groupCell.getEdgeAt(i);

         if (edge.getParent() == groupCell && edge.getTerminal(top) == groupCell || edge.getParent() != groupCell && edge.getTerminal(!top) == groupCell) {
            return true;
         }
      }

      return false;
   }

   // TOOD Fix the fix. This can be done by never putting the edges in the sequences in sorted sequences.
   public LinkedList<LinkedList<mxCell>> filterVertices(LinkedList<LinkedList<mxCell>> cells) {

      for (LinkedList<mxCell> list : cells) {
         List<mxCell> edges = new LinkedList<mxCell>();

         for (mxCell cell : list) {
            if (cell.isEdge()) {
               edges.add(cell);
            }
         }

         list.removeAll(edges);
      }

      return cells;
   }



    /**
    * AutoConnects operations in a view or a group
    * 
    * Only unconnected in a sequence
    * @param Cell parent
    * @return true
    */
   public boolean autoSequence(Cell parent) {

       this.getModel().beginUpdate();
       try {

      // Retrive all operations
      Cell[] sops = getGraphModel().getChildSOP(parent);

      // Find the operations last in Sequence, ie operations not
      // in other operations sequence condition...
      ArrayList<Cell> lastCells = new ArrayList<Cell>();
      for (int i = 0; i < sops.length; i++) {
         if (isLastInSOP(sops[i], sops)){
             lastCells.add(sops[i]);
         }
      }


      // Trace up in sequence from each last operation
      // Will not handle if they merge!
      for (Cell c : lastCells){
              addGraphicalPrecond(c,sops);
      }

      // this.autoArrange(parent);

      } finally {
            this.autoArrange(parent);
            this.majorUpdate();
            this.getModel().endUpdate();
            
      }
      return true;
   }


    /**
    * Recursive autoconnector
    * Will only connected previously unconnected operations in a sequence.
    * Will only connect the first found previous operation.
    * No parallel or alternativ! 
    * @param Cell c
    * @param Cell[] sops
    * @return true when complete
    */
   public void addGraphicalPrecond(Cell c, Cell[] sops){
       // Stop if operation already has connection. 
       // Will not trace further


       // Test SopNode parser
       System.out.println("NEW -----------------");
       HashMap<Integer,OperationData> m = new HashMap<Integer,OperationData>();
       SequenceCreator sc = new SequenceCreator();
       for (int i = 0; i < sops.length; i++) {
           if (sops[i].isOperation()){
               OperationData d = (OperationData) sops[i].getValue();
               System.out.println("OP:" + d.getName());
               m.put(d.getId(), d);
           }
       }

       SopNode rootNode = null;

       if (c.isOperation()){
           OperationData root = (OperationData) c.getValue();
           rootNode = sc.getSequence(root, m);
       }

       HashMap<Integer, Cell> branches = new HashMap<Integer, Cell>();

       System.out.println("Draw sequence -----------------");
       sequenceDrawing(rootNode,sops, branches);
       System.out.println("Completed Draw sequence -----------------");

       this.majorUpdate();
   }


   private boolean sequenceDrawing(SopNode n, Cell[] sops, HashMap<Integer, Cell> branches){

/*       // Println
       System.out.println("New SeqDraw---------------------------------" );
       if (n==null) System.out.println("SopNode is null");
       if (n!=null && n.getPred() == null) System.out.println("SopNode Pred is null");
*/
       if (n == null || sops == null) return true;
       if (n.getPred() == null) return true;

 /*
       if (n.getData()!= null) System.out.println("SopNode name:" + n.getData().getName());
       System.out.println(n.toString());
       

       System.out.println("Seq Predecessor -----" );
       System.out.println(n.getPred().toString());
       if (n.getPred().getData()!= null) System.out.println("Pred node name:" + n.getPred().getData().getName());

*/


       if (n.isOperation() || branches.containsKey(n.getId())){
           Cell nCell = (n.isOperation() ? getCell(n,sops) : branches.get(n.getId()));
           if (n.getPred().isOperation()){
               createSeqConnection(nCell,getCell(n.getPred(),sops));
               return sequenceDrawing(n.getPred(),sops, branches);
           } else if (n.getPred().isBranch()){
               Cell br = createBranch(n.getPred(), sops, branches);
               branches.put(n.getPred().getId(), br);
               createSeqConnection(nCell, br);
               return sequenceDrawing(n.getPred(),sops,branches);
           }
       }
           
       return false;
   }

   private Cell createBranch(SopNode n, Cell[] sops, HashMap<Integer, Cell> branches){

       System.out.println("Create Branch -----" );
       if (n!=null) System.out.println(n.toString());


       Cell result = null;
       if (n.isBranch()){
           if (n.isParallel()){
               result = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_PARALLEL);
           } else if (n.isAlternative()){
               result = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ALTERNATIVE);
               // Add arbitary when supported
           } else result = null;


           ArrayList<Cell> nBr = new ArrayList<Cell>();
           for (SopNode br : n.getBranches()){
               if (br.isOperation()){
                   Cell brCell = getCell(br,sops);
                   //this.updateCellSize(brCell);

                   //this.insertGroupNode(result, minimumGraphSize, brCell);
                   addCellToBranch(brCell,result);
                   this.sequenceDrawing(br, sops, branches);

               } else if (br.isBranch()){
                   if (!branches.containsKey(br.getId())){
                        Cell brCell = createBranch(br,sops, branches);
                        branches.put(br.getId(), brCell);
                        //this.insertGroupNode(result, minimumGraphSize, brCell);
                        addCellToBranch(brCell,result);
                        this.sequenceDrawing(br, sops, branches);
                   }
               }
           }
           
            
       } else result = null;


       if (result != null){
           for (SopNode connectNode : n.getBranches()){
               mxCell edge1 = CellFactory.getInstance().getEdge(false,false);
               mxCell edge2 = CellFactory.getInstance().getEdge(false,true);               

               SopNode top = connectNode;
               while (top.getPred() != null){
                   top = top.getPred();
               }

               if (connectNode.isOperation()){
                   this.addEdge(edge1, result, getCell(connectNode, sops), result, null);
               } else {
                   this.addEdge(edge1, result, branches.get(connectNode.getId()), result, null);
               }

               if (top.isOperation()){
                   this.addEdge(edge2, result, result, getCell(top, sops), null);
               } else {
                   this.addEdge(edge2, result, result, branches.get(top.getId()), null);
               }

               this.autoArrange(result);


           }
           //mxCell edge1 = CellFactory.getInstance().getEdge(false);
           //this.addEdge(edge1, result, getCell(n.getBranches().get(0), sops), result,0);
       }




       return result;
   }

   private Cell getCell(SopNode n, Cell[] sops){

       if (!n.isOperation()) return null;

       for (int i = 0; i < sops.length; i++) {
           if (n.getData().equals(sops[i].getValue())){
               return sops[i];
           }
       }
       return null;
   }

   private void createSeqConnection(Cell op, Cell pred){
       if (op!=null && pred != null){
           if (this.getAlwaysNextCell(op,true) == null && getAlwaysNextCell(pred, false) == null && noCircularSeq(op,pred)){
                //insertNewCell(pred,op,false);
               insertNewCell(op,pred,true);
           }
       }
   }

   private void addCellToBranch(Cell c, Cell parent){
       if (c!=null && parent != null){
           if (this.getAlwaysNextCell(c,true) == null && this.getPreviousOperation(c) == null){
               this.addCell(c, parent);
           }
       }
   }



   private boolean isOpInSeq(Cell c, Cell prev){
       if (c.isOperation() && prev.isOperation()){
           OperationData d = ((OperationData) c.getValue());
           OperationData dPrev = ((OperationData) prev.getValue());
           return d.isPredecessor(dPrev.getId());
       }
       return false;
   }

   private boolean isOpInSeq(OperationData d, Cell prev){
       if (prev.isOperation()){
           OperationData dPrev = ((OperationData) prev.getValue());
           return d.isPredecessor(dPrev.getId());
       }
       return false;
   }

    /**
    * Finds the last operations in a SOP
    * @param Cell c
    * @param Cell[] sops
    * @return boolean
    */
   private boolean isLastInSOP(Cell c, Cell[] sops){
       for (int i = 0; i < sops.length; i++) {
           if (this.isOpInSeq(sops[i], c)){
               return false;
           }
       }
       return true;
   }

    /**
    * Returns true if a cell prevOP, that should be added into seq,
    * is not already present in seq with c
    * @param Cell c
    * @param Cell prevOP
    * @return boolean
    */
   private boolean noCircularSeq(Cell c, Cell prevOP){
       List<mxCell> seqCells = this.getSequence(c);
       for (mxCell n : seqCells){
           if (prevOP.equals(n)){
                return false;
           }
       }
       return true;
   }

private ArrayList<Cell> getOpInSeq(Cell c, Cell[] sops){
       ArrayList<Cell> result = new ArrayList<Cell>();
       if (this.getPreviousOperation(c) == null && c.isOperation()){
          OperationData cellData = (OperationData) c.getValue();
          for (int i = 0; i < sops.length; i++) {
              if (this.isOpInSeq(cellData, sops[i])){

                  // Stop if previous operation already have connections
                  if (getAlwaysNextCell(sops[i], false) == null){
                      result.add(sops[i]);
                  }
              }
          }
       }
       return result;
}

private ArrayList<ArrayList<Cell>> getOpTrace(Cell c, Cell[] sops){

    ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> seqCells = this.getOpInSeq(c, sops);

    if (seqCells.size()==1){

    }


    return result;
}



}
