package sequenceplanner.view.operationView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

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

import sequenceplanner.SPContainer;
import sequenceplanner.model.IModel.AsyncModelListener;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.utils.SPToolBar;
import sequenceplanner.view.AbstractView;
import sequenceplanner.view.IView;
import sequenceplanner.view.operationView.OperationActions.Delete;
import sequenceplanner.view.operationView.OperationActions.Redo;
import sequenceplanner.view.operationView.OperationActions.Select;
import sequenceplanner.view.operationView.OperationActions.Undo;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;
import sequenceplanner.view.operationView.graphextension.SPGraphComponent;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;

//TODO Change name to SOPView
public class OperationView extends AbstractView implements IView, AsyncModelListener {

   // Logging for this class
   private static Logger logger = Logger.getLogger(OperationView.class);
   protected SPGraph graph;
   protected SPGraphComponent graphComponent;
   protected ViewData openedView;
   private boolean changed = false;
   private String startName;
   protected mxGraphOutline outline = null;
   JSplitPane pane;

   //TODO refactor name to SOPView
   public OperationView(SPContainer spc, String name) {
      super(spc, name);
      startName = name;
      updateName();

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


      graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

         @Override
         public void mousePressed(MouseEvent e) {
            createPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            createPopup(e);
         }

         protected void createPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {

               ClickMenu c = new ClickMenu();
               c.show(OperationView.this, e);

            }
         }
      });
   }

   public OperationView(SPContainer spc, ViewData view) {
      this(spc, view.getName());
      open(view);
   }

   public JSplitPane getPane() {
      return pane;
   }

   public void change(Integer[] changedNodes) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

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
         cell.setStyle("perimeter=custom.operationPerimeter;fillColor=#FFFF00");
         cell.setConnectable(false);
         mxGeometry geo = new mxGeometry();

         if (node.getChildCount() > 0) {
            cell.setType(Cell.SOP);
         } else {
            cell.setType(Cell.OP);
         }

         mxRectangle rect = SPGraph.getSizeForOperation(cell);
         geo.setHeight(rect.getHeight());
         geo.setWidth(rect.getWidth());
         cell.setGeometry(geo);

      }

      return cell;
   }

   public void open(ViewData view) {
      OperationData in = null;
      if (view.getRoot() != -1) {
         TreeNode d = model.getOperation(view.getRoot());
         if (d != null) {
            in = (OperationData) d.getNodeData();
         }
      } else {
         in = new OperationData("Bottom", view.getRoot());
      }

      Cell opRoot = open(view, new Cell(in));
      Cell root = new Cell("Root");
      root.insert(opRoot, 0);

      getGraphModel().setRoot(root);
      getGraph().majorUpdate();
      setChanged(false);
   }

   public Cell open(ViewData view, Cell cell) {

      if (view == null) {
         return cell;
      }

      ConvertToCell c = new ConvertToCell();

      c.ConvertToCell(view, model, this);
      Cell opRoot = c.getRoot();

      Object[] o = SPGraphModel.getChildren(getGraphModel(), opRoot);

      for (int i = 0; i < o.length; i++) {
         cell.insert((mxICell) o[i]);
      }

      Cell[] cells = getGraphModel().getChildSOP(cell);

      for (int i = 0; i < cells.length; i++) {
         Cell child = cells[i];
         if (child.isSOP()) {
            ViewData data = model.getOperationView(child.getUniqueId());
            open(data, child);
         }
      }


      return cell;
   }

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

      String tempName = "";

      if (saveView) {
         tempName = getSaveName(newSave);
      } else {
         tempName = "Temporary View";
      }



      if (!tempName.isEmpty()) {
         startName = tempName;

         SPGraphModel gModel = ((SPGraphModel) graph.getModel());

         Object o = gModel.getChildAt(gModel.getRoot(), 0);
         Cell cell = (Cell) o;

         //This will only return the topView, the rest is saved in
         //TODO maby error with id = -1;
         LinkedList<ViewData> viewData = convertToViewData(cell);
         TreeNode[] data = convertToTreeData(cell);

         if (viewData.getFirst().getRoot() == -1 && saveView) {
            viewData.getFirst().setName(startName);
            model.saveView(viewData.removeFirst());
         }
         
         model.saveOperationViews(viewData.toArray(new ViewData[0]));
         model.saveOperationData(data);

         setChanged(false);
         updateName();

 //        saveBackup();
      } else {
         logger.debug("Save was called but with a empty name");
      }


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

   protected TreeNode[] convertToTreeData(Cell cell) {
      System.out.println("Is free view: " + isFreeView());
      TreeNode node = convertCelltoTreeNode(cell);
      // is this view free or do just show the inside of a SOP
      if (!isFreeView()) {
         return new TreeNode[]{node};
      } else {
         //If this view does visualize sops with different parents.
         return model.getChildren(node);
      }
   }

   protected TreeNode convertCelltoTreeNode(Cell root) {
      SPGraphModel gModel = ((SPGraphModel) graph.getModel());
      Data out;
      //TODO maby should check root.isSOP || root.isOP
      if (root.getValue() instanceof OperationData) {
         OperationData oldData = (OperationData) root.getValue();

         OperationData d = (OperationData) oldData.clone();
         d = getPrecond(root, d);

         out = d;
      } else {
         out = new Data("", -1);
      }

      TreeNode modelCell = new TreeNode(out);
      Cell[] cells = gModel.getChildSOP(root);

      for (int i = 0; i < cells.length; i++) {
         modelCell.insert(convertCelltoTreeNode(cells[i]));
      }

      return modelCell;
   }

   /**
    *
    * @param root
    * @return first is view of root.
    */
   private LinkedList<ViewData> convertToViewData(Cell root) {
      LinkedList<ViewData> list = new LinkedList<ViewData>();

      Stack<Cell> cells = new Stack<Cell>();
      cells.push(root);

      while (!cells.isEmpty()) {
         Cell cell = cells.pop();

         ViewData data = new ViewData(((Data) cell.getValue()).getName(), -1);
         data.setRoot(root.getUniqueId());
         list.addLast(convertToViewData(data, cell));


         Cell[] children = getGraphModel().getChildSOP(cell);

         for (int i = 0; i < children.length; i++) {
            if (children[i].isSOP()) {
               cells.add(children[i]);
            }
         }

      }

      return list;
   }

   private ViewData convertToViewData(ViewData viewData, Cell root) {
      Stack<Cell> finalCells = new Stack<Cell>();
      Stack<Cell> temp = new Stack<Cell>();
      temp.push(root);

      while (!temp.isEmpty()) {
         Cell it = temp.pop();

         for (int i = 0; i < it.getChildCount(); i++) {
            if (it.getChildAt(i).isVertex()) {
               Cell c = (Cell) it.getChildAt(i);

               if (c.isParallel() || c.isAlternative() || c.isArbitrary()) {
                  temp.push(c);
                  finalCells.push(c);
               } else if (c.isSOP() || c.isOperation()) {
                  finalCells.push(c);
               }
            }

         }
      }

      //Handle all the cells
      while (!finalCells.isEmpty()) {
         Cell cell = finalCells.pop();
         Cell parent = (Cell) cell.getParent();

         //Use unique id
         int id = cell.getUniqueId();

         // If this has a precond, write it
         Cell prev = getGraph().getAlwaysNextCell(cell, true);


         int previousCell = prev != null ? prev.getUniqueId() : -1;

         //Save type
         int type = cell.getType();

         //Is this in a relation
         int relation = -1;
         boolean lastInRelation = false;
         if (parent.isAlternative() || parent.isArbitrary() || parent.isParallel()) {
            relation = parent.getUniqueId();

            Cell next = getGraph().getNextCell(cell, false, true);
            if (next != null && (next.isParallel() || next.isAlternative())) {
               lastInRelation = true;
            }
         } else if (parent.isSOP() || parent == getGraphModel().getGraphRoot()) {
            relation = parent.getUniqueId();
         }

         mxGeometry geo = cell.getGeometry();

         viewData.addRow(id, previousCell, type, relation, lastInRelation, geo, !cell.isCollapsed());
      }
      viewData.setRoot(root.getUniqueId());

      System.out.println("Root: " + root.getUniqueId() + "\n " + viewData.toString());

      return viewData;

   }

   protected OperationData getPrecond(Cell cell, OperationData d) {
      Cell previousOperation = graph.getPreviousOperation(cell);
      Cell previousCell = graph.getNextCell(cell, true);
      Cell preGrpCell = graph.getNextCell(cell, true, true);

      try {

         if (previousCell != null && previousCell.isGroup()) {
            LinkedList<Cell> cells = graph.getPreviousOperations(previousCell);

            if (previousCell.isParallel()) {
               for (Iterator<Cell> it = cells.iterator(); it.hasNext();) {
                  Cell cg = it.next();
                  d.addAnd(cg.getUniqueId(), 2);
               }

            } else if (previousCell.isAlternative()) {
               LinkedList<OperationData.SeqCond> cond = new LinkedList<OperationData.SeqCond>();

               for (Iterator<Cell> it = cells.iterator(); it.hasNext();) {
                  Cell cg = it.next();
                  cond.add(new SeqCond(cg.getUniqueId(), 2));
               }

               d.addOr(cond);

            }
         } else if (preGrpCell != null && preGrpCell.isAlternative()) {
            getModel().createGroupVariable(preGrpCell.getUniqueId());
            d.addResourceBooking(preGrpCell.getUniqueId());
            if (previousOperation != null){
                d.addAnd(previousOperation.getUniqueId(), 2);     
            }

         } else if (previousOperation != null) {

            d.addAnd(previousOperation.getUniqueId(), 2);
         }

      } catch (NullPointerException e) {
         logger.error("An null pointer was passed in getGraphicalPrecond, probably" +
               " was viewData = null");

      }


      return d;
   }

   /**
    *
    * Convert this view to a datastructure that extends the class ViewData.
    * This function is to be locally implemented for each new view.
    *
    * @param name the name of the returned ViewData
    * @return A viewdata representing this view
    */
   protected ViewData convertToViewData(String name) {
      return null;
   }

   private void initPanels() {

      this.setLayout(new BorderLayout());

      AttributeEditor edit = new AttributeEditor(this);
      edit.registerEditor(OperationData.class,
            new Editors.OperationConditionEditor(this));

      edit.registerEditor(OperationData.class,
            new Editors.SequenceConditionEditor(this, true, "Preconditions"));
      edit.registerEditor(OperationData.class,
            new Editors.SequenceConditionEditor(this, false, "Postconditions"));
      edit.registerEditor(OperationData.class,
            new Editors.ActionEditor(this, "Actions"));


      graph.getSelectionModel().addListener(mxEvent.CHANGE, edit);


      pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);

      pane.setResizeWeight(1.0);
      pane.setDividerSize(3);


      pane.setTopComponent(graphComponent);
      pane.setBottomComponent(edit);

      this.add(pane, BorderLayout.CENTER);


      SPToolBar bar = new SPToolBar();
      Action a = createAction("Save",
            new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                  save(false, true);
               }
            }, "resources/icons/save.png");
      bar.add(a);
      a = createAction("Save as",
            new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                  save(true, true);
               }
            }, "resources/icons/document-save.png");

      bar.setFloatable(false);
      bar.add(a);

      a = createAction("SaveData",
            new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                  save(true, false);
               }
            }, "resources/icons/saveOnlyData.png");
      bar.add(a);

      bar.add(createAction("Undo", new Undo(), "resources/icons/edit-undo.png"));
      bar.add(createAction("Redo", new Redo(), "resources/icons/edit-redo.png"));
      bar.addSeparator();
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
         outline.setPreferredSize(new Dimension(30, 100));
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
      //Validate precondtions.
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
}