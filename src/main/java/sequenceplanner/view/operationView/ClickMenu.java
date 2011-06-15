package sequenceplanner.view.operationView;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import sequenceplanner.SequencePlanner;
import sequenceplanner.view.operationView.OperationActions.AddOperation;
import sequenceplanner.view.operationView.OperationActions.AlternateSOP;
import sequenceplanner.view.operationView.OperationActions.AutoSeqGroup;
import sequenceplanner.view.operationView.OperationActions.AutoarrangeGroup;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

/**
 *
 * @author Erik Ohlson
 */
public class ClickMenu extends JPopupMenu {

   public ClickMenu() {
   }

   public void show(Component invoker, MouseEvent e) {

      Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), invoker);
      OperationView av = (OperationView) invoker;

      Object c = av.getGraphComponent().getCellAt(e.getX(), e.getY());

      JMenu sub = new JMenu("Insert");
      boolean into = false;
      add(sub);

      if (c != null && c instanceof Cell && ((Cell) c).isEdge()) {
         this.add(av.createAction("Remove edge",
               new OperationActions.Delete((Cell) c), "resources/icons/sop.png"));
      }

      if (c != null && c instanceof Cell) {
         Cell cell = (Cell) c;

         if (av.getGraph().isOperation(c)) {
            this.add(av.createAction("Transfer to SOP",
                  new AlternateSOP(cell), "resources/icons/sop.png"));

            this.add(av.createAction("Add Graphical precondition",
                  new AutoSeqGroup(cell), "resources/icons/sop.png"));

         } else if (av.getGraph().isSOP(c)) {
            this.add(av.createAction("Transform to Operation",
                  new AlternateSOP(cell), "resources/icons/remSOP.png"));

         }





         JMenu bef = new JMenu("Before");
         bef.setIcon(new ImageIcon(SequencePlanner.class.getResource("resources/icons/addBefore.png")));

         JMenu aft = new JMenu("After");
         aft.setIcon(new ImageIcon(SequencePlanner.class.getResource("resources/icons/addAfter.png")));

         bef.add(new JMenuItem(av.createAction("Operation",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_OPERATION, true, false), "resources/icons/addOperation.png")));
         bef.add(new JMenuItem(av.createAction("Parallel",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_PARALLEL, true, false), "resources/icons/addParallel.png")));
         bef.add(new JMenuItem(av.createAction("Alternative",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_ALTERNATIVE, true, false), "resources/icons/addAlternative.png")));
         bef.add(new JMenuItem(av.createAction("Arbitrary",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_ARBITRARY, true, false), "resources/icons/addArbitrary.png")));
         sub.add(bef);

         aft.add(new JMenuItem(av.createAction("Operation",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_OPERATION, false, false), "resources/icons/addOperation.png")));
         aft.add(new JMenuItem(av.createAction("Parallel",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_PARALLEL, false, false), "resources/icons/addParallel.png")));
         aft.add(new JMenuItem(av.createAction("Alternative",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_ALTERNATIVE, false, false), "resources/icons/addAlternative.png")));
         aft.add(new JMenuItem(av.createAction("Arbitrary",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_ARBITRARY, false, false), "resources/icons/addArbitrary.png")));
         sub.add(aft);

         if (!cell.isCollapsed() && av.getGraph().isType(cell, false, true, false, false, false)) {
            // The cell is a SOP
            into = true;
            this.add(av.createAction("Autoarrange cell",
                  new AutoarrangeGroup(cell), "resources/icons/sop.png"));

            this.add(av.createAction("Add Graphical precondition",
                  new AutoSeqGroup(cell), "resources/icons/sop.png"));

         } else if (!cell.isCollapsed() && av.getGraph().isType(cell, false, false, true, false, false)) {
            // The cell is a parallel

            this.add(av.createAction("Autoarrange cell",
                  new AutoarrangeGroup(cell), "resources/icons/sop.png"));

            this.add(av.createAction("Add Graphical precondition",
                  new AutoSeqGroup(cell), "resources/icons/sop.png"));

            sub.add(new JMenuItem(av.createAction("Operation",
                  new AddOperation(e.getPoint(), SPGraphModel.TYPE_OPERATION, false, true), "resources/icons/addOperation.png")));

            sub.add(new JMenuItem(av.createAction("Parallel",
                  new AddOperation(e.getPoint(), SPGraphModel.TYPE_PARALLEL, false, true), "resources/icons/addParallel.png")));

         } else if (!cell.isCollapsed() && av.getGraph().isType(cell, false, false, false, true, false)) {
            // The cell is an alternative

            this.add(av.createAction("Autoarrange cell",
                  new AutoarrangeGroup(cell), "resources/icons/sop.png"));

            this.add(av.createAction("Add Graphical precondition",
                  new AutoSeqGroup(cell), "resources/icons/sop.png"));

            sub.add(new JMenuItem(av.createAction("Operation",
                  new AddOperation(e.getPoint(), SPGraphModel.TYPE_OPERATION, false, true), "resources/icons/addOperation.png")));

         } else if (!cell.isCollapsed() && av.getGraph().isType(cell, false, false, false, false, true)) {
            // The cell is an arbitrary

            this.add(av.createAction("Autoarrange cell",
                  new AutoarrangeGroup(cell), "resources/icons/sop.png"));

            this.add(av.createAction("Add Graphical precondition",
               new AutoSeqGroup(cell), "resources/icons/sop.png"));

            sub.add(new JMenuItem(av.createAction("Operation",
                  new AddOperation(e.getPoint(), SPGraphModel.TYPE_OPERATION, false, true), "resources/icons/addOperation.png")));

         }

      } else {
         into = true;
         this.add(av.createAction("Autoarrange cell",
               new AutoarrangeGroup(null), "resources/icons/sop.png"));

         this.add(av.createAction("Add Graphical precondition",
               new AutoSeqGroup(null), "resources/icons/sop.png"));
      }


      if (into) {
         sub.add(new JMenuItem(av.createAction("Operation",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_OPERATION, false, true), "resources/icons/addOperation.png")));
         sub.add(new JMenuItem(av.createAction("Parallel",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_PARALLEL, false, true), "resources/icons/addParallel.png")));
         sub.add(new JMenuItem(av.createAction("Alternative",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_ALTERNATIVE, false, true), "resources/icons/addAlternative.png")));
         sub.add(new JMenuItem(av.createAction("Arbitrary",
               new AddOperation(e.getPoint(), SPGraphModel.TYPE_ARBITRARY, false, true), "resources/icons/addArbitrary.png")));
      }

      //add(sub);




      boolean draw = true;

      if (draw) {
         show(invoker, p.x, p.y);
      }
   }

   @Override
   public void show(Component invoker, int x, int y) {

      super.show(invoker, x, y);
   }
}
