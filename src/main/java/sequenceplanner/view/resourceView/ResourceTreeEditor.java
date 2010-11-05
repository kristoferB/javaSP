package sequenceplanner.view.resourceView;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;

import org.apache.log4j.Logger;

import sequenceplanner.model.TreeNode;
import sequenceplanner.view.resourceView.Editors.TreeEditor;

/**
 *
 * @author Erik Ohlson
 */
public class ResourceTreeEditor implements TreeCellEditor {

   protected LinkedList<CellEditorListener> listeners;

   protected HashMap<String, TreeEditor> editors;
   
   protected TreeEditor usedEditor;

   protected Logger logger = Logger.getLogger( ResourceTreeEditor.class );

   public ResourceTreeEditor() {
      editors = new HashMap<String, TreeEditor>();
      listeners = new LinkedList<CellEditorListener>();
   }


   //TODO Handle those who whant to move up and down in hiarki whithout starting a new View.
   public Component getTreeCellEditorComponent(JTree tree, Object value,
         boolean isSelected, boolean expanded, boolean leaf, int row) {

      TreeNode node = ((TreeNode)value);

      TreeEditor editor = editors.get(node.getNodeData().getClass().getName());

      if (editor != null) {
         editor.setValue(node);
         usedEditor = editor;

      } else {
         logger.error("No editor registred for " + node.getNodeData().getClass().getName());
         Set<String> s = editors.keySet();
         for (String string : s) {
            System.out.println(string);
         }
      }

      return editor;
   }

   public void registerEditor(Class type, TreeEditor editor) {
      editor.setEditor(this);
      editors.put(type.getName(), editor);
   }

   public void addCellEditorListener(CellEditorListener l) {
      listeners.add(l);
   }

    public void removeCellEditorListener(CellEditorListener l) {
      listeners.remove(l);
   }

   public void cancelCellEditing() {
      ChangeEvent e = new ChangeEvent(this);

      for (CellEditorListener l : listeners) {
         l.editingCanceled(e);
      }
   }

   public Object getCellEditorValue() {
      System.out.println("getCellEditorValue");
      return "Hello!";
   }

   public boolean isCellEditable(EventObject anEvent) {
      if (anEvent instanceof MouseEvent && anEvent.getSource() instanceof JTree) {
         MouseEvent e = (MouseEvent)anEvent;
         JTree t = (JTree) anEvent.getSource();
         
         Object o = t.getPathForLocation(e.getX(), e.getY()).getLastPathComponent();
         if (o instanceof TreeNode) {
            String name = ((TreeNode)o).getNodeData().getClass().getName();

            return editors.containsKey(name);
            
         }
      }
      
      return false;
   }

   public boolean shouldSelectCell(EventObject anEvent) {
      return true;
   }

   public boolean stopCellEditing() {
       ChangeEvent e = new ChangeEvent(this);

      for (CellEditorListener l : listeners) {
         l.editingStopped(e);
      }

      return false;
   }

 




}
