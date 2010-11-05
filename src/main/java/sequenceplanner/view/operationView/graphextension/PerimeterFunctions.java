package sequenceplanner.view.operationView.graphextension;

import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxEdgeStyle.mxEdgeStyleFunction;
import com.mxgraph.view.mxPerimeter.mxPerimeterFunction;

public class PerimeterFunctions {

   public static mxPerimeterFunction parallelPerimeter = new ParallelPerimeter(6);
   public static mxPerimeterFunction alternativePerimeter = new ParallelPerimeter(0);
   public static mxPerimeterFunction operationPerimeter = new OperationPerimeter();

   public static class OperationPerimeter implements mxPerimeterFunction {

      public mxPoint apply(mxRectangle bounds, mxCellState edgeState,
            mxCellState terminalState, boolean isSource, mxPoint next) {
         mxPoint p = new mxPoint();
         if (isSource) {
            p.setX(terminalState.getCenterX());
            p.setY(terminalState.getY() + terminalState.getHeight());
         } else {
            p.setX(terminalState.getCenterX());
            p.setY(terminalState.getY());
         }

         return p;
      }
   };
   public static mxEdgeStyleFunction ElbowAbove = new mxEdgeStyleFunction() {

      //TODO Make this more general so it do not cross any sequences.
      public void apply(mxCellState state, mxCellState source, mxCellState target, List points, List result) {

         if (source != null && target != null) {
            double y1 = source.getY() + source.getHeight() + 15;



            result.add(new mxPoint(source.getCenterX(), y1));

            SPGraph graph = (SPGraph) source.getView().getGraph();

            double center = source.getCenterX() + (target.getX() - source.getX())/2;

            result.add(new mxPoint(center, y1 ));

            double y2 = target.getY()-15;

            result.add(new mxPoint(center, y2 ));

            result.add(new mxPoint(target.getCenterX(), y2 ));
         }

      }
   };

   public static class ParallelPerimeter implements mxPerimeterFunction {

      mxCellState source = null;
      mxCellState target = null;
      int d;
      int d2;

      public ParallelPerimeter(int d) {
         this.d = d;
         if (d == 6) {
            d2 = 8;
         } else {
            d2 = 0;
         }
      }

      public mxPoint apply(mxRectangle bounds, mxCellState edgeState,
            mxCellState terminalState, boolean isSource, mxPoint next) {
         mxPoint p = new mxPoint();

         mxICell parent = ((mxICell) edgeState.getCell()).getParent();
         mxICell vertex = (mxICell) terminalState.getCell();
         target = edgeState.getView().getState(((mxCell) edgeState.getCell()).getTarget());
         source = edgeState.getView().getState(((mxCell) edgeState.getCell()).getSource());

         if (edgeState != null) {
            double scale = edgeState.getView().getScale();
            d = (int) (d * scale);
            d2 = (int) (d2 * scale);
         }

         if (parent == vertex) {
            if (isSource && target != null) {
               p.setX(target.getCenterX());
               p.setY(terminalState.getY() + d);
            } else if (!isSource && source != null) {
               p.setX(source.getCenterX());
               p.setY(terminalState.getY() + terminalState.getHeight() - d2);
            }
         } else {
            if (isSource) {
               p.setX(terminalState.getCenterX());
               p.setY(terminalState.getY() + terminalState.getHeight());
            } else {
               p.setX(terminalState.getCenterX());
               p.setY(terminalState.getY());
            }
         }
         return p;
      }

      public void setSourceTarget(mxCellState source, mxCellState target) {
         this.source = source;
         this.target = target;
      }
   };
}
