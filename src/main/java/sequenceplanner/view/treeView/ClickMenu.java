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

import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;

import sequenceplanner.view.AbstractView;
import sequenceplanner.view.Actions.InsertVariable;
import sequenceplanner.view.Actions.OpenOperationsRealizedBy;
import sequenceplanner.view.Actions.OpenResourceView;
import sequenceplanner.view.Actions.RemoveNode;

/**
 * Click menu for tree view.<br/>
 * @author Erik Ohlson
 */
public class ClickMenu extends JPopupMenu {

    protected TreeNode node;
    protected Model model;
    protected GUIController mGUIController = null;

    public ClickMenu(TreeNode node, Model model) {
        this.node = node;
        this.model = model;
    }

    public ClickMenu(TreeNode node, Model model, GUIController mGUIController) {
        this(node, model);
        this.mGUIController = mGUIController;
    }

    public void show(Component invoker, MouseEvent e) {
        Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), invoker);
        AbstractView av = (AbstractView) invoker;

        boolean draw = false;

        final JMenuItem rem = new JMenuItem(av.createAction("Remove",
                new RemoveNode(node), "resources/icons/close.png"));

        //Operations-------------------------------------------------------------
        if (model.isOperationRoot(node)) {
            add(new JMenuItem(av.createAction("Insert Operation",
                    new TreeViewController.InsertOperation(model), "resources/icons/sop.png")));
            draw = true;
        }
        if (Model.isOperation(node.getNodeData())) {
            add(new JMenuItem(av.createAction("Get attributes",
                    new TreeViewController.GetOperationAttributes(node, mGUIController), "resources/icons/op.png")));
            add(new JMenuItem(av.createAction("Remove Operation",
                    new TreeViewController.RemoveOperation(node, mGUIController), "resources/icons/close.png")));
            draw = true;
        }//----------------------------------------------------------------------

        //Resources--------------------------------------------------------------
        if (model.isResourceRoot(node)) {
            add(new JMenuItem(av.createAction("Insert Resource",
                    new InsertVariable(node, Data.RESOURCE), "resources/icons/robot.png")));
            add(new JMenuItem(av.createAction("Open Resource View",
                    new OpenResourceView(node, mGUIController), "resources/icons/res.png")));
            draw = true;
        }
        if (Model.isResource(node.getNodeData())) {
            add(new JMenuItem(av.createAction("Insert Resource",
                    new InsertVariable(node, Data.RESOURCE), "resources/icons/robot.png")));

            add(new JMenuItem(av.createAction("Insert Variable",
                    new InsertVariable(node, Data.RESOURCE_VARIABLE), "resources/icons/variable.png")));
            add(rem);
            draw = true;
        }
        if (Model.isVariable(node.getNodeData())) {
            add(rem);
            draw = true;
        }//----------------------------------------------------------------------

        //Views------------------------------------------------------------------
        if (model.isViewRoot(node)) {
            add(new JMenuItem(av.createAction("Insert SOP",
                    new TreeViewController.InsertOperationView(mGUIController), "resources/icons/sop_1.png")));
            draw = true;
        }
        if (Model.isView(node.getNodeData())) {
            add(new JMenuItem(av.createAction("Remove SOP",
                    new TreeViewController.RemoveOperationView(mGUIController, node), "resources/icons/close.png")));
            draw = true;
        }//----------------------------------------------------------------------


//        //Extra------------------------------------------------------------------
//        if (Model.isResource(node.getNodeData()) || model.isResourceRoot(node)) {
//            JMenu innerMenu = new JMenu("Views");
//            innerMenu.add(new JMenuItem(av.createAction("Open treeview",
//                    new OpenResourceView(node), "resources/icons/res.png")));
//            add(innerMenu);
//
//            if (Model.isResource(node.getNodeData())) {
//                innerMenu.add(new JMenuItem(av.createAction("Open operations realized by",
//                        new OpenOperationsRealizedBy(node), "resources/icons/res.png")));
//            }
//        }//----------------------------------------------------------------------

        //Liason-----------------------------------------------------------------
        /*} else if (model.isLiasonRoot(node) || Model.isLiason(d)) {
        first.setAction(av.createAction("Insert Liason",
        new InsertVariable(node, Data.LIASON), "resources/icons/min.png"));

        add(first,0);
        if (Model.isLiason(d)) {
        add(rem);
        }
        draw = true;
         */
        //-----------------------------------------------------------------------

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

        int[] polX = {0, 0, 14};
        int[] polY = {0, 14, 0};

        g.setColor(Color.BLACK);
        g.fillPolygon(polX, polY, 3);
    }
}
