package sequenceplanner.view.operationView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import sequenceplanner.model.IModel.AsyncModelListener;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;

import sequenceplanner.model.data.ViewData;
import sequenceplanner.utils.SPToolBar;
import sequenceplanner.view.AbstractView;
import sequenceplanner.view.IView;
import sequenceplanner.view.operationView.OperationActions.Delete;
import sequenceplanner.view.operationView.OperationActions.Redo;
import sequenceplanner.view.operationView.OperationActions.Select;
import sequenceplanner.view.operationView.OperationActions.Undo;
import sequenceplanner.view.operationView.graphextension.SPGraph;
import sequenceplanner.view.operationView.graphextension.SPGraphComponent;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

import com.mxgraph.model.mxGeometry;

import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.view.operationView.graphextension.Cell;

//TODO Change name to SOPView
public class OperationView extends AbstractView implements IView, AsyncModelListener {

    // Logging for this class
    private static Logger logger = Logger.getLogger(OperationView.class);
    protected SPGraph graph;
    protected SPGraphComponent graphComponent;
    private boolean changed = false;
    private String startName;
    protected mxGraphOutline outline = null;
    JSplitPane pane;
//    private boolean isClosed;
//    private boolean isHidden;
    public ViewData mViewData;

    //TODO refactor name to SOPView
    public OperationView(Model model, String name) {
        super(model, name);
        startName = name;
        initVariables();
        SPGraphModel graphModel = new SPGraphModel();
        graphModel.setCacheParent(this.model.getNameCache());
        graph = new SPGraph(graphModel);
        graphComponent = new SPGraphComponent(graph, this);
        graphComponent.setGridVisible(true);
        graphModel.addListener(mxEvent.CHANGE, new mxIEventListener() {

            @Override
            public void invoke(Object source, mxEventObject evt) {
                setChanged(true);
            }
        });

        initPanels();
        registerKeystrokes(graphComponent.getInputMap(), graphComponent.getActionMap());



    }

    public OperationView(Model model, ViewData iViewData) {
        this(model, iViewData.getName());
        this.mViewData = iViewData;
        this.mViewData.setSpGraphModel(getGraphModel());
//        open(iViewData);
    }

    public void addGraphComponentListener(MouseAdapter ma) {
        graphComponent.getGraphControl().addMouseListener(ma);
    }

//    public OperationView(Model model, ViewData view) {
//        this(model, view.getName());
//        open(view);
//    }
    public JSplitPane getPane() {
        return pane;
    }

    @Override
    public void change(Integer[] changedNodes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Method for checking if the view is hidden or not
     * @return boolean true if hidden else false
     */
//    public boolean isHidden() {
//        return isHidden;
//    }
    /**
     * Method for checking if the view is closed or not
     * @return boolean true if closed else false
     */
//    public boolean isClosed() {
//        return isClosed;
//    }
    /**
     * Sets the isClosed variable to true or false
     * @param closed true if closed else false
     */
//    public void setClosed(boolean closed) {
//        isClosed = closed;
//    }
    /**
     * Sets the isHidden variable to true or false
     * @param closed true if hidden closed else false
     */
//    public void setHidden(boolean hidden) {
//        isHidden = hidden;
//    }
    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        boolean oldValue = this.changed;
        this.changed = changed;

        firePropertyChange("modified", oldValue, changed);

        if (oldValue != changed) {
            updateName();
        }
    }

    // GM chef Susanne
    public void updateName() {
        if (isChanged()) {
            setName(startName + "*");
        } else {
            setName(startName);
        }
    }

    public void open(TreeNode[] nodes) {
        int x = 0;
        int y = 0;

        for (int i = 0; i < nodes.length; i++) {
            Cell cell = nodeToCell(nodes[i]);
            Point p = new Point(x, y);
            graphComponent.importCells(new Object[]{cell}, x, y, graph.getDefaultParent(), p);
            x += cell.getGeometry().getWidth();
            y += cell.getGeometry().getHeight();
        }
    }

    private Cell nodeToCell(TreeNode node) {

        Cell cell = null;
        if (node.getNodeData() instanceof OperationData) {
            cell = new Cell(node.getNodeData());
            cell.setCollapsed(true);
            cell.setVertex(true);
            cell.setStyle("perimeter=custom.operationPerimeter;fillColor=red");
            cell.setConnectable(false);
            mxGeometry geo = new mxGeometry();

            if (node.getChildCount() > 0) {
                cell.setType(Constants.SOP);
            } else {
                cell.setType(Constants.OP);
            }

            mxRectangle rect = SPGraph.getSizeForOperation(cell);
            geo.setHeight(rect.getHeight());
            geo.setWidth(rect.getWidth());
            cell.setGeometry(geo);

        }

        return cell;
    }

//    private void open(ViewData view) {
////        OperationData in = null;
////        if (view.getRoot() != -1) {
////            TreeNode d = model.getOperation(view.getRoot());
////            if (d != null) {
////                in = (OperationData) d.getNodeData();
////            }
////        } else {
////            in = new OperationData("Bottom", view.getRoot());
////        }
//
////        Cell opRoot = open(view, new Cell(in));
////        Cell root = new Cell("Root");
////        root.insert(opRoot, 0);
////
////        getGraphModel().setRoot(root);
//        getGraph().majorUpdate();
//        setChanged(false);
//    }
//    public Cell open(ViewData view, Cell cell) {
//
//        if (view == null) {
//            return cell;
//        }
//
//        ConvertToCell c = new ConvertToCell();
//
//        c.ConvertToCell(view, model, this);
//        Cell opRoot = c.getRoot();
//
//        Object[] o = SPGraphModel.getChildren(getGraphModel(), opRoot);
//
//        for (int i = 0; i < o.length; i++) {
//            System.out.println("operation: " + o[i].toString());
//            cell.insert((mxICell) o[i]);
//        }
//
//        Cell[] cells = getGraphModel().getChildSOP(cell);
//
//        for (int i = 0; i < cells.length; i++) {
//            Cell child = cells[i];
//            if (child.isSOP()) {
//                ViewData data = model.getOperationView(child.getUniqueId());
//                open(data, child);
//            }
//        }
//
//
//        return cell;
//    }
    @Override
    public boolean closeView() {
        if (isChanged()) {
            int a = JOptionPane.showConfirmDialog(this, "You are about to close a view with unsaved changes \n Do you want to progress?");

            if (a > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void save(boolean newSave, boolean saveView) {
//
//        String tempName = "";
//
//        if (saveView) {
//            tempName = getSaveName(newSave);
//        } else {
//            tempName = "Temporary View";
//        }
//
//        if (!tempName.isEmpty()) {
//            startName = tempName;
//
//            SPGraphModel gModel = ((SPGraphModel) graph.getModel());
//
//            Object o = gModel.getChildAt(gModel.getRoot(), 0);
//            Cell cell = (Cell) o;
//            //This will only return the topView, the rest is saved in
//            LinkedList<ViewData> viewData = convertToViewData(cell);
//            TreeNode[] data = convertToTreeData(cell);
//
//            System.out.println("start remove");
//            model.removeConditions(startName);
//            System.out.println("end remove");
//
//
//            if (viewData.getFirst().getRoot() == -1 && saveView) {
//                viewData.getFirst().setName(startName);
//                model.saveView(viewData.getFirst());
//            }
//
//            model.saveOperationViews(viewData.toArray(new ViewData[0]));
//            model.saveOperationData(data);
//
//            //Save sop nodes
////            final Map<ISopNode, Map<ISopNode, Cell>> map = getSopNodeForGraphPlus();
////
////            final ISopNode sopNodeRoot = map.keySet().iterator().next();
////            final Map<ISopNode, Cell> nodeCellMap = map.get(sopNodeRoot);
////
////            System.out.println("save nodeCellMap: " + nodeCellMap);
//
//            model.updateSopNodeStructureWithObjectsInModel(mSopNodeForGraphPlus.getRootSopNode(true));
////            viewData.getFirst().storeCellData(mSopNodeForGraphPlus.getNodeCellMap(false)); //for xml
//            System.out.println("start set conditions");
//            model.setConditions(mSopNodeForGraphPlus.getRootSopNode(false), startName);
//            System.out.println("end set conditions");
//
//            setChanged(false);
//            updateName();
//
//        } else {
//            logger.debug("Save was called but with a empty name");
//        }
//
    }

    /**
     *
     * @param newSave set to true if this view needs a new same (e.g. saveAs)
     * @return the name to save the view under or "" if the view should not be saved
     */
    private String getSaveName(boolean newSave) {

        String tempName = startName;

        if (newSave || tempName == null) {
            tempName = JOptionPane.showInputDialog("Please, input a name for the view");
            if (tempName == null) {
                tempName = Integer.toString(Model.newId());
            }
        }

        boolean replace = model.isViewPresent(tempName);

        if (replace) {
            Object[] options = {"Yes", "No", "No, set a new name"};

            // (Parent, Message, Title, Option, Type, Icon, Options, initialValue)
            int n = JOptionPane.showOptionDialog(this,
                    "Do you want to replace the view ",
                    "Replace View " + getName(),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[1]);

            // 0 = yes, does not to be handled. Just return tempName
            // 1 = no, return empty String -> return an empty string.
            // 2 = no, set a new name. Just call the function again with the new name on.

            if (n == 1) {
                return "";
            } else if (n == 2) {
                tempName = getSaveName(true);
            }
        }

        return tempName;
    }

    private void initPanels() {

        this.setLayout(new BorderLayout());

        //Formerly used by the AttributeEditor. Could be used to listen to changes
        //in the cells.
        //graph.getSelectionModel().addListener(mxEvent.CHANGE, edit);

        pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);

        pane.setResizeWeight(1.0);
        pane.setDividerSize(3);


        pane.add(graphComponent);

        this.add(pane, BorderLayout.CENTER);
        final SPToolBar bar = new SPToolBar();
        bar.setFloatable(false);
        //External Listener------------------------------------------------------
        bar.add(createAction("SavePM", new OperationViewController.SaveOperationView(this), "resources/icons/save.png"));
//        bar.add(createAction("LocalVisualization", new OperationViewController.VisualizeOperationView(this,true), "resources/icons/local_visualization.png"));
//        bar.add(createAction("GlobalVisualization", new OperationViewController.VisualizeOperationView(this,false), "resources/icons/global_visualization.png"));
        //-----------------------------------------------------------------------
//        Action a = createAction("Save",
//                new ActionListener() {
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        save(false, true);
//                    }
//                }, "resources/icons/save.png");
//        bar.add(a);
//        a = createAction("Save as",
//                new ActionListener() {
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        save(true, true);
//                    }
//                }, "resources/icons/document-save.png");
//
//        bar.add(a);
//
//        a = createAction("SaveData",
//                new ActionListener() {
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        save(true, false);
//                    }
//                }, "resources/icons/saveOnlyData.png");
//        bar.add(a);
//
//        bar.add(createAction("Undo", new Undo(), "resources/icons/edit-undo.png"));
//        bar.add(createAction("Redo", new Redo(), "resources/icons/edit-redo.png"));
//        bar.addSeparator();
//      a = createAction("ShowCache",
//            new ActionListener() {
//
//               @Override
//               public void actionPerformed(ActionEvent e) {
//                  Cell cell = (Cell)getGraph().getSelectionCell();
//                  OperationData d = (OperationData)cell.getValue();
//                  System.out.println("Precondition: " + d.getRawPrecondition() );
//                  System.out.println("Postcondition: " + d.getPostcondition() );
//               }
//            }, "resources/icons/save.png");
//      bar.add(a);

        JPopupMenu preferenceMenu = new JPopupMenu("Visibility");

        JToggleButton showGrid = new JToggleButton("Show grid");
        showGrid.setSelected(getGraphComponent().isGridVisible());
        showGrid.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    getGraphComponent().setGridVisible(true);
                    getGraph().setGridEnabled(true);

                } else {
                    getGraphComponent().setGridVisible(false);
                    getGraph().setGridEnabled(false);

                }
                getGraphComponent().refresh();

            }
        });
//      bar.add(showGrid);
        preferenceMenu.add(showGrid);

        JToggleButton showPath = new JToggleButton("Show Path");
        showPath.setSelected(getGraph().isShowPath());
        showPath.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    getGraph().setShowPath(true);
                    getGraph().majorUpdate();

                } else {
                    getGraph().setShowPath(false);
                    getGraph().majorUpdate();

                }
                getGraphComponent().refresh();

            }
        });
//      bar.add(showPath);
        preferenceMenu.add(showPath);
        bar.addSeparator();
        bar.add(preferenceMenu);




        this.add(bar, BorderLayout.NORTH);
    }

    @Override
    public Action createAction(String name, ActionListener usedAction, String icon) {
        return createAction(name, usedAction, icon, this);
    }

    @Override
    public JComponent getOutline() {

        if (outline == null) {
            outline = new mxGraphOutline(graphComponent);
            outline.setPreferredSize(new Dimension(500, 100));
        }

        return outline;
    }

    public SPGraph getGraph() {
        return graph;
    }

    public SPGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public SPGraphModel getGraphModel() {
        return (SPGraphModel) getGraph().getModel();
    }

    public boolean isFreeView() {
        Data d = (Data) (((SPGraphModel) getGraph().getModel()).getGraphRoot()).getValue();
        return d.getId() == -1;
    }

    protected void validateResources() {
        //Validate preconditions.
    }

    public String getCellName(String name, Cell node) {

        Data d = ((Data) node.getValue());

        if (name == null || name.isEmpty()) {
            name = JOptionPane.showInputDialog("Please, input a name for the view");
        }

        //Is this ok according to the SPGraph
        boolean replace = graph.isNamePresent(name, node);

        //If this is a free view then top level name has to be checked against model.
        if (isFreeView()) {
            Cell[] gChilds = getGraphModel().getChildSOP(getGraphModel().getGraphRoot());

            for (int i = 0; i < gChilds.length; i++) {
                if (node == gChilds[i]) {
                    replace = getModel().isNamePresent(
                            model.getViewRoot(),
                            name,
                            true);
                }
            }

        }

        if (replace) {

            String s = (String) JOptionPane.showInputDialog(this,
                    "This name is already taken ",
                    "Set new name for " + node.getValue().toString(),
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    name + "_" + d.getId());

            if (s == null) {
                return "";
            }

            name = getCellName(s, node);

        }

        return name;
    }

    public void addmxIEventListener(mxIEventListener l) {
        graph.getSelectionModel().addListener(mxEvent.CHANGE, l);
    }

    private void registerKeystrokes(InputMap iMap, ActionMap aMap) {
        //Select all (Ctrl + a)
        iMap.put(KeyStroke.getKeyStroke("control A"), "selectAll");
        aMap.put("selectAll", createAction("selectAll", new Select("all"), ""));

        //Empty selection (Ctrl + shift + a)
        iMap.put(KeyStroke.getKeyStroke("control shift A"), "selectNone");
        aMap.put("selectNone", createAction("selectNone", new Select("none"), ""));

        //Select all in group (Ctrl + g)
        iMap.put(KeyStroke.getKeyStroke("control G"), "SelectGrp");
        aMap.put("SelectGrp",
                createAction("SelectGrp", new Select("group"), ""));

        //Select sequence (Ctrl + s)
        iMap.put(KeyStroke.getKeyStroke("control S"), "selectSequence");
        aMap.put("selectSequence", createAction("selectAll", new Select("sequence"), ""));

        //Delete (Delete)
        iMap.put(KeyStroke.getKeyStroke("DELETE"), "delete");
        aMap.put("delete", createAction("Delete", new Delete(), ""));

        //Undo (Ctrl + Z)
        iMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
        aMap.put("undo", createAction("Undo", new Undo(), ""));

        //Redo (Ctrl + Y)
        iMap.put(KeyStroke.getKeyStroke("control Y"), "redo");
        aMap.put("redo", createAction("redo", new Redo(), ""));

        //Copy (Ctrl + C)
        iMap.put(KeyStroke.getKeyStroke("control C"), "copy");
        aMap.put("copy",
                createAction("copy", TransferHandler.getCopyAction(), "", true));

        //Paste (Ctrl + V)
        iMap.put(KeyStroke.getKeyStroke("control V"), "paste");
        aMap.put("paste",
                createAction("paste", TransferHandler.getPasteAction(), "", true));

        //Cut (Ctrl + X)
        iMap.put(KeyStroke.getKeyStroke("control X"), "cut");
        aMap.put("cut",
                createAction("cut", TransferHandler.getCutAction(), "", true));
    }

    private void initVariables() {
//        mViewData.setClosed(false);
//        mViewData.setHidden(false);
//        setClosed(false);
//        setHidden(false);
        updateName();
    }

    /**
     * To remove operation from this graph/operation view.
     * @param iIdOfOperationToRemove id of operation to remove
     * @return always true
     */
    public boolean removeOperationInGraph(final Integer iIdOfOperationToRemove) {
        final ISopNodeToolbox snToolbox = new SopNodeToolboxSetOfOperations();

        final Set<ISopNode> sopNodeSet = snToolbox.getNodes(mViewData.mSopNodeForGraphPlus.getRootSopNode(false), true);

        //Loop node set and remove all instances of node to remove---------------
        for (final ISopNode node : sopNodeSet) {
            if (node instanceof SopNodeOperation) {
                final OperationData opData = node.getOperation();
                final int opDataId = opData.getId();

                //Check if operation in loop is the operation of interest
                if (Integer.toString(opDataId).equals(iIdOfOperationToRemove.toString())) {

                    //Remove node from sop node data structure
                    snToolbox.removeNode(node, mViewData.mSopNodeForGraphPlus.getRootSopNode(false));
                }
            }
        }

        //Remove old graph-------------------------------------------------------
        getGraph().selectAll();
        getGraph().deleteMarkedCells();

        //draw new graph---------------------------------------------------------
        mViewData.storeCellData();
        redrawGraph();

        System.out.println("SopStructure after remove of operation:");
        System.out.println("Model:");
        System.out.println(mViewData.mSopNodeForGraphPlus.getRootSopNode(false).toString());
        System.out.println("Graph:");
        System.out.println(mViewData.mSopNodeForGraphPlus.getRootSopNode(true).toString());

        return true;

    }

    public boolean redrawGraph() {
        new SopNodeToolboxSetOfOperations().drawNode(mViewData.mSopNodeForGraphPlus.getRootSopNode(false), getGraph(), mViewData.mNodeCellDataLayoutMap);
        return true;
    }
}
