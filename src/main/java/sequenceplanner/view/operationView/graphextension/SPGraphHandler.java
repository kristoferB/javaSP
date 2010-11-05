package sequenceplanner.view.operationView.graphextension;

import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.TransferHandler;

/**
 *
 * @author Erik Ohlson
 */
public class SPGraphHandler extends mxGraphHandler {

   public SPGraphHandler(final mxGraphComponent graphComponent) {
      super(graphComponent);
      setRemoveCellsFromParent(false);

      marker = new IntoCellMarker(graphComponent, Color.RED) {

         @Override
         public boolean isEnabled() {
            return graphComponent.getGraph().isDropEnabled();
         }

         @Override
         public Object getCell(MouseEvent e) {
            TransferHandler th = graphComponent.getTransferHandler();
            boolean isLocal = th instanceof mxGraphTransferHandler && ((mxGraphTransferHandler) th).isLocalDrag();

            mxGraph graph = graphComponent.getGraph();
            Object cell = super.getCell(e);
            Object[] cells = (isLocal) ? graph.getSelectionCells()
                  : dragCells;
            cell = graph.getDropTarget(cells, e.getPoint(), cell);
            boolean clone = graphComponent.isCloneEvent(e) && cloneEnabled;

            cs = cells;

            if (isLocal && cell != null && cells.length > 0 && !clone && (graph.isCellSelected(cell) || graph.getModel().getParent(cells[0]) == cell)) {
               cell = null;
            }

            return cell;
         }
      };

      Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

         @Override
         public void eventDispatched(AWTEvent event) {
            KeyEvent e = ((KeyEvent) event);

            SPGraphComponent doc = (SPGraphComponent) graphComponent;

            if (e.isShiftDown() && !doc.getMoveInto()) {
               doc.setMoveInto(true);

            }

            if (!e.isShiftDown() && doc.getMoveInto()) {
               doc.setMoveInto(false);
            }
         }
      }, AWTEvent.KEY_EVENT_MASK);

   }

   @Override
   public SPGraphComponent getGraphComponent() {
      return (SPGraphComponent) graphComponent;
   }

   @Override
   protected void moveCells(Object[] cells, double dx, double dy,
         Object target, MouseEvent e) {
      mxGraph graph = graphComponent.getGraph();
      boolean clone = e.isControlDown() && isCloneEnabled();

      if (getGraphComponent().getMoveInto() || clone) {
         getGraphComponent().setMoveInto(false);

         if (target == null) {
            target = graph.getDefaultParent();
         }

      } else {
         target = null;
      }


      // Removes cells from parent
      if (target == null && isRemoveCellsFromParent() && shouldRemoveCellFromParent(graph.getModel().getParent(cell),
            cells, e)) {
         target = graph.getDefaultParent();
      }

      Object[] tmp = graph.moveCells(cells, dx, dy, clone, target, e.getPoint());

      if (clone && tmp.length == cells.length) {
         graph.setSelectionCells(tmp);
      }
   }

//    @Override
//    protected Point getPreviewLocation(MouseEvent e, boolean gridEnabled) {
//        int x = 0;
//        int y = 0;
//
//        if (first != null && previewBounds != null) {
//            mxGraph graph = graphComponent.getGraph();
//            double scale = graph.getView().getScale();
//            mxPoint trans = graph.getView().getTranslate();
//
//            // LATER: Drag image _size_ depends on the initial position and may sometimes
//            // not align with the grid when dragging. This is because the rounding of the width
//            // and height at the initial position may be different than that at the current
//            // position as the left and bottom side of the shape must align to the grid lines.
//            // Only fix is a full repaint of the drag cells at each new mouse location.
//            double dx = e.getX() - first.x;
//            double dy = e.getY() - first.y;
//
//            double dxg = ((previewBounds.getX() + dx) / scale) - trans.getX();
//            double dyg = ((previewBounds.getY() + dy) / scale) - trans.getY();
//
//            double mid = previewBounds.getWidth() / scale;
//
//            dxg += mid;
//            if (gridEnabled) {
//                dxg = graph.snap(dxg);
//                dyg = graph.snap(dyg);
//            }
//            dxg -= mid;
//
//            x = (int) Math.round((dxg + trans.getX()) * scale) + (int) Math.round(previewBbox.getX()) - (int) Math.round(previewBounds.getX());
//            y = (int) Math.round((dyg + trans.getY()) * scale) + (int) Math.round(previewBbox.getY()) - (int) Math.round(previewBounds.getY());
//        }
//
//        return new Point(x, y);
//    }
}
