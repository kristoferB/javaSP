package sequenceplanner.view.operationView.graphextension;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.handler.mxPanningHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author Erik Ohlson
 */
public class SPGraphComponent extends mxGraphComponent {

   private mxUndoManager undoManager;
   private final OperationView view;
   private boolean moveInto;
   private int zoomCounter = 0;
   public SPGraphComponent(mxGraph graph, final OperationView view) {
      super(graph);
      this.view = view;
      setToolTips(true);

      setDoubleBuffered(true);
      undoManager = new mxUndoManager() {

         @Override
         public void undo() {
            super.undo();
            view.getGraph().majorUpdate();
         }

         @Override
         public void redo() {
            super.redo();
            view.getGraph().majorUpdate();
         }
      };
      // Adds the command history to the model and view
      graph.getModel().addListener(mxEvent.UNDO, undoHandler);
      graph.getView().addListener(mxEvent.UNDO, undoHandler);


      new mxPanningHandler(this);

      setDragEnabled(true);


      setGridStyle(GRID_STYLE_DASHED);
      setGridColor(Color.gray);
      setBackground(Color.WHITE);

      installHandlers();
      installListeners();


   }


   protected mxGraphTransferHandler createTransferhandler() {
      return new OperationViewTransferhandler();
   }

   @Override
   public boolean isPanningEvent(MouseEvent event) {

      return event != null ? (event.getButton() == MouseEvent.BUTTON2) || event.isAltDown() : false;
         
   }

   private void installHandlers() {
      new mxRubberband(this);
   }

   private void installListeners() {

      addKeyListener(new KeyAdapter() {

         private boolean up = false;
         private boolean down = false;

         @Override
         public void keyReleased(KeyEvent e) {
            boolean tempUp = e.getKeyCode() == KeyEvent.VK_UP;
            boolean tempDown = e.getKeyCode() == KeyEvent.VK_DOWN;

            if (tempUp) {
               up = false;
            } else if (tempDown) {
               down = false;
            }
         }

         @Override
         public void keyPressed(KeyEvent e) {

            boolean tempUp = e.getKeyCode() == KeyEvent.VK_UP;
            boolean tempDown = e.getKeyCode() == KeyEvent.VK_DOWN;
            boolean insert = e.getKeyCode() == KeyEvent.VK_INSERT;

            if (tempUp) {
               up = true;
            } else if (tempDown) {
               down = true;
            }

            if (insert && up) {
               insert(true);
            } else if (insert && down) {
               insert(false);
            }

         }

         private void insert(boolean before) {
            SPGraph SPgraph = (SPGraph) getGraph();
             System.out.println("insert 1");
            if (SPgraph.getSelectionCount() == 1 && SPgraph.getSelectionCell() instanceof Cell) {
               Cell cell = (Cell) SPgraph.getSelectionCell();

               Cell insertedCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
               SPgraph.updateCellSize(insertedCell);


               SPgraph.insertNewCell(cell, insertedCell, before);
               SPgraph.setSelectionCell(insertedCell);
            }

         }
      });

      getGraphControl().addMouseWheelListener(new MouseWheelListener() {

         int scrollFactor = 100;

         @Override
         public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
               int rot = e.getWheelRotation();
               Double zoom = zoomFactor;
               zoomFactor *= zoomFactor * Math.abs(rot);
                System.out.println("Zoom: "+rot + "Counter: " + zoomCounter );
               if (rot > 0 && zoomCounter <2 ) {
                  SPGraphComponent.this.zoomOut();
                  zoomCounter++;
               } else if (rot < 0 && zoomCounter > -5) {
                  SPGraphComponent.this.zoomIn();
                  zoomCounter--;
               //zoomIn function is broken, this is a modification that
               //resets the zoom when it is close enough to seem natural
               }else if(zoomCounter <=-5){
                   SPGraphComponent.this.zoomActual();
               }
               zoomFactor = zoom;
            } else {
               Rectangle r = getViewport().getViewRect();
               int rot = 0;
               if (Math.abs(e.getWheelRotation()) == 1) {
                  rot = scrollFactor * e.getWheelRotation();
               } else if (Math.abs(e.getWheelRotation()) == 2) {
                  rot = 5 * scrollFactor * e.getWheelRotation();
               } else if (Math.abs(e.getWheelRotation()) >= 3) {
                  rot = 15* scrollFactor * e.getWheelRotation();
               }

               if (e.isShiftDown()){
                   int newX = (r.x + rot > 0) ? (r.x + rot) : 0;
                   r = new Rectangle(newX, r.y, r.width, r.height);
               } else {
                   int newY = (r.y + rot > 0) ? (r.y + rot) : 0;
                   r = new Rectangle(r.x, newY, r.width, r.height);
               }
               getGraphControl().scrollRectToVisible(r);
            }
         }
      });


   }
   protected mxIEventListener undoHandler = new mxIEventListener() {

      @Override
      public void invoke(Object source, mxEventObject evt) {
         undoManager.undoableEditHappened((mxUndoableEdit) evt.getArgAt(0));
      }
   };

   public mxUndoManager getUndoManager() {
      return undoManager;
   }

   @Override
   protected mxGraphHandler createGraphHandler() {
      return new SPGraphHandler(this);
   }

   @Override
   public mxInteractiveCanvas createCanvas() {
      return new custom2DCanvas();
   }

   @Override
   public boolean canImportCell(Object cell) {
      if (cell instanceof Cell && ((Cell) cell).getValue() instanceof OperationData) {
         OperationData data = (OperationData) ((Cell) cell).getValue();

         return !((SPGraph) getGraph()).isOperationIdPresent(data.getId()) ||
               data.getCopy();

      }

      return false;
   }

   @Override
   public void selectCellForEvent(Object cell, MouseEvent e) {
      boolean isSelected = graph.getSelectionModel().isSelected(cell);



      if (isToggleEvent(e)) {
         if (isSelected) {
            graph.getSelectionModel().removeCell(cell);
         } else {
            graph.getSelectionModel().addCell(cell);
         }

      } else if (e.isShiftDown() && graph.getSelectionCount() == 1) {

         Cell selectedCell = (Cell) graph.getSelectionCell();

         List<Cell> cells = ((SPGraph) graph).getCellsHereTo((Cell) cell, true);
         int i = cells.indexOf(selectedCell);

         if (i == -1) {
            cells = ((SPGraph) graph).getCellsHereTo((Cell) cell, false);
            i = cells.indexOf(selectedCell);

         }

         if (i != -1) {
            Object[] c = cells.subList(0, i + 1).toArray();
            graph.setSelectionCells(c);
         }



      } else if (!isSelected || graph.getSelectionCount() != 1) {
         graph.setSelectionCell(cell);
      }
   }

   public void toogleMoveInto() {
      moveInto = !moveInto;
   }

   public void setMoveInto(boolean shift) {
      moveInto = shift;
   }

   public boolean getMoveInto() {
      return moveInto;
   }

   @Override
   public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {


      if (cells != null && cells.length == 1 && cells[0] instanceof Cell) {
         boolean copy = ((OperationData) ((Cell) cells[0]).getValue()).getCopy();
         cells = super.importCells(cells, dx, dy, target, location);

         //Todo it will crash if this is a copy
         Cell cell = (Cell) cells[0];
         if (!copy) {
//            cell = view.open(view.getModel().getOperationView(cell.getUniqueId()), cell);
         }
         view.getGraph().updateSizeOfOperations();
         view.getGraph().majorUpdate();


         return new Object[]{cell};
      }
      return cells;
   }
}
