package sequenceplanner.view.operationView;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import sequenceplanner.SequencePlanner;
import sequenceplanner.view.operationView.Editors.OperationEditor;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

/**
 *
 * @author Erik Ohlson
 */
public class AttributeEditor extends JPanel
      implements mxEventSource.mxIEventListener {

   static final ImageIcon saveIcon = SequencePlanner.getNewIcon("/sequenceplanner/resources/icons/save.png");

   static Logger logger = Logger.getLogger(AttributeEditor.class);
   protected HashMap<String, LinkedList<OperationEditor>> editors;
   protected OperationView view;
   JTabbedPane tabbedPane;
   private Cell editedNode;
   private Cell clonedCell;

   JButton save;
   Cell selectedCell;


   public AttributeEditor(OperationView ov) {
      this.editors = new HashMap<String, LinkedList<OperationEditor>>();
      this.view = ov;



      tabbedPane = new JTabbedPane(JTabbedPane.TOP);

         save = new JButton(saveIcon);
         save.setMargin(new Insets(0, 0, 0, 0));
         save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               save();
            }
         });
         save.setVisible(false);

      this.setLayout(new BorderLayout());
      this.add(tabbedPane, BorderLayout.CENTER);

      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      p.add(save, BorderLayout.NORTH);
      this.add(p, BorderLayout.WEST);
   }

   private void save() {
      Set keys = editors.keySet();


      for (Object object : keys) {
         LinkedList<OperationEditor> list = editors.get(object);

         for (OperationEditor operationEditor : list) {
            operationEditor.saveNode();
         }
      }
      view.getGraph().setValue(selectedCell, clonedCell.getValue());
   }

   public void registerEditor(Class type, OperationEditor editor) {
      String name = type.getName();

      LinkedList<OperationEditor> l = editors.get(name);
      if (l != null) {
         l.add(editor);
      } else {
         l = new LinkedList<OperationEditor>();
         l.add(editor);
         editors.put(name, l);
      }
   }

   private void clear() {
      tabbedPane.removeAll();

      Set keys = editors.keySet();

      for (Object object : keys) {
         LinkedList<OperationEditor> list = editors.get(object);

         for (OperationEditor operationEditor : list) {
            operationEditor.clear();
         }
      }

      ((JSplitPane) getParent()).resetToPreferredSizes();
      save.setVisible(false);
      selectedCell = null;
      clonedCell = null;
   }

   protected void setCell(Cell cell, LinkedList<OperationEditor> l) {
      clear();

      save.setVisible(true);
      this.selectedCell = cell;
      this.clonedCell = (Cell) cell.clone();

      for (OperationEditor edit : l) {
         edit.setValue(clonedCell);
         tabbedPane.add(edit, edit.getName());
      }

      ((JSplitPane) getParent()).resetToPreferredSizes();
   }

   public void invoke(Object source, mxEventObject evt) {
      SPGraph graph = view.getGraph();
      Object cell = graph.getSelectionCell();

      if (graph.getSelectionCount() == 1 && cell instanceof Cell) {
         Object d = graph.getModel().getValue(cell);

         LinkedList<OperationEditor> l = editors.get(d.getClass().getName());


         if (l != null) {
            setCell((Cell) cell, l);
         } else {
            logger.debug("Unregistred type: " + d.getClass().getName());
         }


      } else {
         clear();
      }
   }
}
