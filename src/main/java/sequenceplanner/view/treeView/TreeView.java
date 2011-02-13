package sequenceplanner.view.treeView;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.AbstractView;
import sequenceplanner.view.operationView.Editors;

/**
 *
 * @author Erik Ohlson, erik.a.ohlson@gmail.com
 */
public class TreeView extends AbstractView {

    protected final JTree tree;
    private TreeDragSource drag;
    public static Cursor resPointer = Toolkit.getDefaultToolkit().createCustomCursor(Editors.resIcon.getImage(), new Point(0, 0), "ResPointer");
    public static Cursor productPointer = Toolkit.getDefaultToolkit().createCustomCursor(Editors.productLiason.getImage(), new Point(0, 0), "ResPointer");

    public TreeView(final Model model) {
        super(model, "ProjectView");

        ProjecTreeModel pModel = new ProjecTreeModel(model) {

            @Override
            public void valueForPathChanged(TreePath path, Object newValue) {
                Object o = path.getLastPathComponent();

                if (o instanceof TreeNode) {
                    model.setName((TreeNode) o, (String) newValue);
                }
            }
        };

        tree = new JTree(pModel) {

            @Override
            public boolean isPathEditable(TreePath path) {
                Object p = Model.getParent(path.getLastPathComponent());

                if (p != model.getRoot()) {

                    return true;
                }

                return false;
            }

            @Override
            public String getToolTipText(MouseEvent event) {

                TreePath path = tree.getPathForLocation(event.getX(), event.getY());

                if (path != null) {
                    Object t = path.getLastPathComponent();

                    if (t instanceof TreeNode) {
                        return "Id: " + Integer.toString(((TreeNode) t).getId());

                    }


                }
                return "";
            }

            ;
        };

        ToolTipManager.sharedInstance().registerComponent(tree);

        Renderer r = new Renderer();
        tree.setCellRenderer(r);
        tree.setRootVisible(
                false);
        tree.setToggleClickCount(
                1);
        tree.setShowsRootHandles(
                true);
        tree.setEditable(
                true);





        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();

        JTextField comboBox = new JTextField();
        comboBox.setEditable(
                true);
        DefaultCellEditor comboEditor = new DefaultCellEditor(comboBox);
        comboEditor.setClickCountToStart(
                99);

        TreeCellEditor editor = new DefaultTreeCellEditor(tree, renderer, comboEditor);


        tree.setCellEditor(editor);
        tree.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {




                        if (e.getKeyCode() == KeyEvent.VK_F2) {
                            tree.startEditingAtPath(tree.getSelectionPath());
                        }
                    }
                });






        this.setLayout(
                new BorderLayout());


        this.add(
                new JScrollPane(tree), BorderLayout.CENTER);


        tree.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        popup(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        popup(e);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() >= 2) {
                            TreePath path = tree.getPathForLocation(e.getX(), e.getY());

                            if (path != null) {
                                TreeNode t = (TreeNode) path.getLastPathComponent();
                                if (Model.isView(t.getNodeData())) {
                                    //   container.createOperationView((ViewData) t.getNodeData());
                                } else if (Model.isOperation(t.getNodeData())) {
                                    OperationData data = (OperationData) t.getNodeData();
                                    //  container.createOperationView(model.getOperationView(data.getId()));
                                }

                            }


                        }
                    }

                    private void popup(MouseEvent e) {

                        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {

                            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                            if (path != null) {
                                tree.setSelectionPath(path);
                                ClickMenu c = new ClickMenu((TreeNode) path.getLastPathComponent(), TreeView.this.model);
                                c.show(TreeView.this, e);
                            }
                        }
                    }
                });
        tree.setDragEnabled(false);
//      expandListener();
        drag = new TreeDragSource(tree, DnDConstants.ACTION_COPY_OR_MOVE);

    }

//   private void expandListener() {
//      tree.addTreeWillExpandListener(new TreeWillExpandListener() {
//
//         public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
//            ((JSplitPane) TreeView.this.getParent()).resetToPreferredSizes();
//         }
//
//         public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
//
//         }
//      });
//   }
    @Override
    public Action createAction(String name, ActionListener usedAction, String icon, Object source) {
        return super.createAction(name, usedAction, icon, this);
    }

    @Override
    public boolean closeView() {
        return true;
    }

    class TreeDragSource implements DragSourceListener, DragGestureListener {

        DragSource source;
        DragGestureRecognizer recognizer;
        TransferableNode transferable;
        TreeNode node;
        JTree tree;

        public TreeDragSource(JTree tree, int actions) {
            this.tree = tree;
            source = new DragSource();
            recognizer = source.createDefaultDragGestureRecognizer(this.tree,
                    actions, this);

        }

        /*
         * Drag Gesture Handler
         */
        public void dragGestureRecognized(DragGestureEvent dge) {
            TreePath path = tree.getSelectionPath();


            if ((path == null) || (path.getPathCount() <= 1)) {
                return;
            }


            node = (TreeNode) path.getLastPathComponent();
            node.getNodeData().setCopy(false);
            transferable = new TransferableNode(node);

            if (Model.isResource(node.getNodeData()) || Model.isLiason(node.getNodeData())) {
                source.startDrag(dge, DragSource.DefaultLinkDrop, transferable, this);
            } else {
                source.startDrag(dge, DragSource.DefaultMoveDrop, transferable, this);
            }
        }
        Cursor defaultCursor = null;
        /*
         * Drag Event Handlers
         */

        @Override
        public void dragEnter(DragSourceDragEvent ddd) {
            ddd.getDragSourceContext().getCursor();

            if (Model.isResource(node.getNodeData())) {
                ddd.getDragSourceContext().setCursor(resPointer);
            } else if (Model.isLiason(node.getNodeData())) {
                ddd.getDragSourceContext().setCursor(productPointer);
            }
        }

        public void dragExit(DragSourceEvent d) {
            d.getDragSourceContext().setCursor(defaultCursor);
        }

        public void dragOver(DragSourceDragEvent dd) {
        }

        public void dropActionChanged(DragSourceDragEvent dd) {
            //Can maby change the cursor?

            if (Model.isOperation(node.getNodeData())) {

                if (dd.getUserAction() == TransferHandler.COPY) {
                    node.getNodeData().setCopy(true);
                    dd.getDragSourceContext().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                } else if (dd.getUserAction() == TransferHandler.MOVE) {
                    node.getNodeData().setCopy(false);
                    dd.getDragSourceContext().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
        }

        public void dragDropEnd(DragSourceDropEvent dsd) {
            System.out.println("End; TargetAction: " + dsd.getDropAction());

            if (dsd.getDropAction() == TransferHandler.MOVE) {
                node.getNodeData().setCopy(false);
            } else if (dsd.getDropAction() == TransferHandler.COPY) {
                node.getNodeData().setCopy(true);
            }
        }
    }
}
