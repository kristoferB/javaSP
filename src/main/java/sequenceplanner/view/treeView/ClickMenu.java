package sequenceplanner.view.treeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.multiProduct.TypeVar;
import sequenceplanner.view.AbstractView;
import sequenceplanner.view.Actions.DeleteOperation;
import sequenceplanner.view.Actions.InsertOperation;
import sequenceplanner.view.Actions.InsertVariable;
import sequenceplanner.view.Actions.OpenOperationsRealizedBy;
import sequenceplanner.view.Actions.OpenResourceView;
import sequenceplanner.view.Actions.RemoveNode;
/**
 *
 * @author Erik Ohlson
 */
public class ClickMenu extends JPopupMenu {

   protected TreeNode node;
   protected Model model;

   public ClickMenu(TreeNode node, Model model) {
      this.node = node;
      this.model = model;
   }

   public void show(Component invoker, MouseEvent e) {
      Point p =SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), invoker);
      AbstractView av = (AbstractView) invoker;

      boolean draw = false;

       JMenuItem first = new JMenuItem("Insert") {
         @Override
         public void paint(Graphics g) {
            super.paint(g);
            ClickMenu.this.repaint();
         }
      };

      JMenuItem rem = new JMenuItem(av.createAction("Remove",
         new RemoveNode(node), "resources/icons/variable.png") );


      Data d = node.getNodeData();
      

      if (model.isResourceRoot(node)) {
         first.setAction(av.createAction("Insert Resource",
               new InsertVariable(node, Data.RESOURCE), "resources/icons/robot.png"));
         add(first,0);
         draw = true;
         
         //If clicked node is OperationRoot create new InsertOperation Action
        } else if (model.isOperationRoot(node)){
            first.setAction(av.createAction("Insert Operation",
               new InsertOperation(node, Data.OPERATION), "resources/icons/robot.png"));
            add(first,0);
            draw = true;
        }else if (model.isOperationRoot(node.getParent())){
            first.setAction(av.createAction("Delete Operation",
               new DeleteOperation(node), "resources/icons/robot.png"));
            add(first,0);
            draw = true;
        }
         /* else if (model.isLiasonRoot(node) || Model.isLiason(d)) {
         first.setAction(av.createAction("Insert Liason",
               new InsertVariable(node, Data.LIASON), "resources/icons/min.png"));

         add(first,0);
         if (Model.isLiason(d)) {
            add(rem);
         }
            
         draw = true;

        }*/ else if (Model.isResource(d)) {
         first.setAction(av.createAction("Insert Resource",
               new InsertVariable(node, Data.RESOURCE), "resources/icons/robot.png") );

         add(new JMenuItem(av.createAction("Insert Variable",
               new InsertVariable(node, Data.RESOURCE_VARIABLE), "resources/icons/variable.png") ) );
         add(rem);
         add(first,0);
         draw = true;

      } else if (Model.isVariable(d)) {
         add(rem,0);
         draw = true;
      }
      else if (Model.isOperation(d) && d.getName().contains(TypeVar.SEPARATION+TypeVar.TRANSPORT+TypeVar.SEPARATION)) { //Added PM 101130
          add(rem,0);
          draw = true;
      }
      
      
      if (Model.isResource(d) || Model.isLiason(d)
            || model.isResourceRoot(node) || model.isLiasonRoot(node) ) {
         JMenu menu = new JMenu("Views");
         menu.add(new JMenuItem(av.createAction("Open treeview",
            new OpenResourceView(node), "resources/icons/res.png") ) );
         add(menu);

         if (Model.isResource(d)) {
            menu.add(new JMenuItem(av.createAction("Open operations realized by",
                  new OpenOperationsRealizedBy(node), "resources/icons/res.png") ) );
         }


      }
      

      if (Model.isView(d)) {
         add(rem);
         draw = true;
      }
     
      if (draw) {
         show(invoker, p.x, p.y);
      }
   }

   @Override
   public void show(Component invoker, int x, int y) {
     
      super.show(invoker, x, y);
   }

   @Override
   public void paint(Graphics g) {
      super.paint(g);

      int[] polX = { 0, 0, 14 };
      int[] polY = { 0, 14, 0 };

      g.setColor(Color.BLACK);
      g.fillPolygon(polX, polY, 3);
   }
}
