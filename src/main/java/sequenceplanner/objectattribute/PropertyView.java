package sequenceplanner.objectattribute;

import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sequenceplanner.editor.EditorTreeModel;
import sequenceplanner.editor.IGlobalProperty;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;

/**
 * View for setting properties for operations.
 *
 * @author Evelina
 */
public class PropertyView extends JScrollPane implements CellEditorListener {

    //The treeModel for the global properties created in Editor view
    EditorTreeModel model;
    //The tree for properties in Object attribute view
    JTree tree;
    //True if a operation is selected in a SOP-view
    Boolean operationIsChosen;
    Cell currentOperation;
    OperationView currentOpView;
    CheckBoxNodeEditor nodeEditor;

    public PropertyView(EditorTreeModel m){
        model = m;
        operationIsChosen = false;
        updateTree();
    }

    /**
     * Draws a checkbox node tree according to the current treemodel
     *
     */
    public void updateTree(){
        Object root = model.getRoot();
        int noProperties = model.getChildCount(root);
        UniqueVector[] properties = new UniqueVector[noProperties];
        IGlobalProperty p;
        //for each property
        for(int i = 0; i < noProperties; i++){
            p = (IGlobalProperty) model.getChild(root, i);
            CheckBoxNode[] values = new CheckBoxNode[model.getChildCount(p)];
            //for each value
            for(int j = 0; j < model.getChildCount(p); j++){
                boolean selected = false;
                //check if value is set for operation
                if(operationIsChosen){
                    SPGraph graph = currentOpView.getGraph();
                    OperationData d = (OperationData) graph.getModel().getValue(currentOperation);
                    //check if value is selected for current operation
                    if(d.isPropertySet(p.getValue(j).getId())){
                        selected = true;
                    }
                }

            values[j] = new CheckBoxNode(p.getValue(j).getName(), p.getValue(j).getId(), selected);
            }
            properties[i] = new UniqueVector(p.getName(), p.getId(), values);
        }

        Vector rootVector = new NamedVector("Root", properties);
        tree = new JTree(rootVector);
        for(int i=0;i<tree.getRowCount();i++){
            tree.expandRow(i);
        }

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        tree.setCellRenderer(renderer);
        nodeEditor = new CheckBoxNodeEditor(tree);
        nodeEditor.addCellEditorListener(this);
        tree.setCellEditor(nodeEditor);
        tree.setEditable(true);
        setViewportView(tree);
        tree.setVisible(operationIsChosen);
    }

    public void setOperation(){
        if(currentOpView != null){
            SPGraph graph = currentOpView.getGraph();
            Object cell = graph.getSelectionCell();

            if (graph.getSelectionCount() == 1 && cell instanceof Cell) {
                currentOperation = (Cell) cell;
                operationIsChosen = true;
            }
            else{
                operationIsChosen = false;           
            }
            tree.setVisible(operationIsChosen);
            updateTree();
        }
    }

    public void setOpView(OperationView opView){
        currentOpView = opView;
    }
    
    public void saveSettings(){
        if(operationIsChosen){
            SPGraph graph = currentOpView.getGraph();
            OperationData d = (OperationData) graph.getModel().getValue(currentOperation);

            TreeModel mod = tree.getModel();
            Object root = mod.getRoot();
            int noProperties = mod.getChildCount(root);
            DefaultMutableTreeNode o;

            //For each property, check if any value is set (and thereby also if the property is set)
            for(int i = 0; i < noProperties; i++){               
                o = (DefaultMutableTreeNode) mod.getChild(root, i);
                if(o.getUserObject() instanceof UniqueVector){
                    UniqueVector property = (UniqueVector) o.getUserObject();

                    boolean propertySelected = false;
                    boolean valueSelected = false;
                    //For each value
                    for(int j = 0; j < property.size(); j++){
                        if(property.get(j) instanceof CheckBoxNode){
                            CheckBoxNode node = (CheckBoxNode) property.get(j);                        
                            if(node.isSelected()){
                                valueSelected = true;
                                if(!propertySelected){
                                    propertySelected = true;
                                }
                            }
                            //Save to operation
                            d.savePropertySetting(node.getId(), valueSelected);
                        }
                    }
                    d.savePropertySetting(property.getId(), propertySelected);
                }
            }
            graph.setValue(currentOperation, d);
        }
    }

    public void clear(){
        //not supported yet
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        Object o = tree.getCellEditor().getCellEditorValue();
        
        if(o instanceof CheckBoxNode){
            CheckBoxNode lastUpdatedNode = (CheckBoxNode) o;

            TreeModel mod = tree.getModel();
            Object root = mod.getRoot();
            int noProperties = mod.getChildCount(root);
            DefaultMutableTreeNode propertyNode;

            for(int i = 0; i < noProperties; i++){
                propertyNode = (DefaultMutableTreeNode) mod.getChild(root, i);
                if(propertyNode.getUserObject() instanceof UniqueVector){
                    UniqueVector property = (UniqueVector) propertyNode.getUserObject();
                    for(int j = 0; j < property.size(); j++){
                        CheckBoxNode node = (CheckBoxNode) property.get(j);
                        if(lastUpdatedNode.getId() == node.getId()){
                            //update selection of last edited node
                            node.setSelected(lastUpdatedNode.isSelected());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}


class CheckBoxNodeRenderer implements TreeCellRenderer {
  private UniqueCheckBox leafRenderer = new UniqueCheckBox();

  private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

  Color selectionBorderColor, selectionForeground, selectionBackground,
      textForeground, textBackground;

  protected UniqueCheckBox getLeafRenderer() {
    return leafRenderer;
  }

  public CheckBoxNodeRenderer() {
    Font fontValue;
    fontValue = UIManager.getFont("Tree.font");
    if (fontValue != null) {
      leafRenderer.setFont(fontValue);
    }
    Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
    leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));

    selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
    selectionForeground = UIManager.getColor("Tree.selectionForeground");
    selectionBackground = UIManager.getColor("Tree.selectionBackground");
    textForeground = UIManager.getColor("Tree.textForeground");
    textBackground = UIManager.getColor("Tree.textBackground");
  }


  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
          boolean expanded, boolean leaf, int row, boolean hasFocus) {
     
    Component returnValue;
    if (leaf) {

      String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
      leafRenderer.setText(stringValue);
      leafRenderer.setSelected(false);

      leafRenderer.setEnabled(tree.isEnabled());

      if (selected) {
        leafRenderer.setForeground(selectionForeground);
        leafRenderer.setBackground(selectionBackground);
      } else {
        leafRenderer.setForeground(textForeground);
        leafRenderer.setBackground(textBackground);
      }


      if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
        if (userObject instanceof CheckBoxNode) {
          CheckBoxNode node = (CheckBoxNode) userObject;
          leafRenderer.setText(node.getText());
          leafRenderer.setId(node.getId());
          leafRenderer.setSelected(node.isSelected());
        }
      }
      returnValue = leafRenderer;
    } else {
      returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
          value, selected, expanded, leaf, row, hasFocus);
    }

    return returnValue;
  }

}

class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

  CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
  ChangeEvent changeEvent = null;
  JTree tree;

  public CheckBoxNodeEditor(){

  }

  public CheckBoxNodeEditor(JTree tree) {
    this.tree = tree;
  }

  public void setTree(JTree tree){
    this.tree = tree;
  }

    @Override
  public Object getCellEditorValue() {
    UniqueCheckBox checkbox = renderer.getLeafRenderer();
    CheckBoxNode checkBoxNode = new CheckBoxNode(checkbox.getText(), checkbox.getId(), checkbox.isSelected());
    return checkBoxNode;
  }

    @Override
  public boolean isCellEditable(EventObject event) {
    boolean returnValue = false;
    if (event instanceof MouseEvent) {
      MouseEvent mouseEvent = (MouseEvent) event;
      TreePath path = tree.getPathForLocation(mouseEvent.getX(),
          mouseEvent.getY());
      if (path != null) {
        Object node = path.getLastPathComponent();
        if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
          DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
          Object userObject = treeNode.getUserObject();
          returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
        }
      }
    }
    return returnValue;
  }

    @Override
  public Component getTreeCellEditorComponent(JTree tree, Object value,
    boolean selected, boolean expanded, boolean leaf, int row) {
    Component editor = renderer.getTreeCellRendererComponent(tree, value,
        true, expanded, leaf, row, true);
    
    ItemListener itemListener = new ItemListener() {
            @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if(itemEvent.paramString().indexOf("invalid") == -1){
                fireEditingStopped();
            }
            
        }
    };
    if (editor instanceof UniqueCheckBox) {
        UniqueCheckBox unc = (UniqueCheckBox) editor;
        unc.addItemListener(itemListener);
    }

    return editor;
  }
}

class CheckBoxNode {
  String text;
  int id;
  boolean selected;

  public CheckBoxNode(String text, int id, boolean selected) {
    this.text = text;
    this.id = id;
    this.selected = selected;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean newValue) {
    selected = newValue;
  }

  public String getText() {
    return text;
  }

  public void setText(String newValue) {
    text = newValue;
  }

  public int getId(){
    return id;
  }
 

  @Override
  public String toString() {
    return getClass().getName() + "[" + text + "/" + selected + "]";
  }
}

class NamedVector extends Vector {
  String name;

  public NamedVector(String name) {
    this.name = name;
  }

  public NamedVector(String name, Object[] elements) {
    this.name = name;
    for (int i = 0, n = elements.length; i < n; i++) {
      add(elements[i]);
    }
  }

  @Override
  public String toString() {
    return name;
  }
}

class UniqueVector extends NamedVector{
    private int id;

    public UniqueVector(String name, int id, Object[] elements) {
        super(name, elements);
        this.id = id;
    }

    public int getId(){
        return id;
    }
}

class UniqueCheckBox extends JCheckBox{
    private int id;

    public UniqueCheckBox(){
        super();
    }

    public UniqueCheckBox(String name, int id, boolean selected){
        super(name, selected);
        this.id = id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

}