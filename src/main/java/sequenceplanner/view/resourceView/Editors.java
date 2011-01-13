package sequenceplanner.view.resourceView;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.tree.TreeCellEditor;

import org.apache.log4j.Logger;

import sequenceplanner.SPIcon.IconHandler;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.view.Actions.InsertVariable;

/**
 *
 * @author Erik Ohlson
 */
public class Editors {

   static final ImageIcon saveIcon = IconHandler.getNewIcon("/sequenceplanner/resources/icons/save.png");



   static abstract class TreeEditor extends JPanel {

      protected ResourceView view;
      protected TreeNode node;
      protected TreeCellEditor editor;


      public TreeEditor(ResourceView view) {
         this.view = view;
      }

      public void setEditor(TreeCellEditor editor) {
         this.editor = editor;
      }

      public void setValue(TreeNode node) {
         this.node = node;
      }

      public TreeNode getValue() {
         return node;
      }

      abstract protected void saveNode();
   }

   /**
    * Variable editor
    */
   static class VariableEditor extends TreeEditor {

      private JTextField name;
      private JComboBox type;
      private JTextField min;
      private JTextField max;
      private JTextField init;
      private JLabel initLabel;
      private JLabel mid;
      private JButton save;
      static Logger logger = Logger.getLogger(VariableEditor.class);

      /**
       * Holds the state for this editor
       */
      private ResourceVariableData data;
      private TreeNode node;

      public VariableEditor(ResourceView view) {
         super(view);
         initiateGraphics();
      }

      private void initiateGraphics() {
         name = new JTextField(10);
         type = new JComboBox(new Object[]{"Binary", "Integer"});
         min = new JTextField(2);
         max = new JTextField(2);
         mid = new JLabel("..");
         init = new JTextField(2);
         initLabel = new JLabel("Initial value: ");
         save = new JButton(saveIcon);
         save.setMargin(new Insets(0, 0, 0, 0));

          ActionListener saveList = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               saveNode();
            }
         };

         save.addActionListener(saveList);
         save.setMnemonic(KeyEvent.VK_S);
         

         name.setBorder(BorderFactory.createTitledBorder("Name"));
         name.setBackground(getBackground());

         this.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createBevelBorder(BevelBorder.LOWERED),
               BorderFactory.createBevelBorder(BevelBorder.RAISED)));

         GroupLayout l = new GroupLayout(this);
         this.setLayout(l);

         JPanel t = new JPanel(new FlowLayout());
         t.add(min);
         t.add(mid);
         t.add(max);
         t.add(initLabel);
         t.add(init);
         JPanel t3 = new JPanel(new BorderLayout());
         t3.add(t, BorderLayout.SOUTH);


         JPanel t2 = new JPanel(new BorderLayout());
         t2.add(type, BorderLayout.SOUTH);

         l.setHorizontalGroup(l.createSequentialGroup()
               .addComponent(name)
               .addGap(5)
               .addComponent(t2)
               .addGap(20)
               .addComponent(t3)
               .addGap(5)
               .addComponent(save)
               .addGap(3));

         l.setVerticalGroup(l.createParallelGroup(GroupLayout.Alignment.TRAILING)
               .addComponent(name)
               .addComponent(t2)
               .addComponent(t3)
               .addComponent(save));


		
		

      }

      protected void saveNode() {
         int maxInt;
         int minInt;
         int initInt;

         try {
            maxInt = Integer.parseInt(max.getText());
            minInt = Integer.parseInt(min.getText());
            initInt = Integer.parseInt(init.getText());

         } catch(Exception ex) {
            maxInt = 1;
            minInt = 0;
            initInt = 0;
         }

         if(minInt < 0){
             logger.error("Variable min value has to be >= 0. Min value is set to 0");
             minInt = 0;
         }
         if(minInt > maxInt){
             logger.error("Variable max value has to be > variable min value. Min value is set to 0, max is set to 1 and initial value is set to 1");
             minInt = 0;
             maxInt = 1;
             initInt = 1;
         }

         if(initInt < minInt){
             logger.error("Initial value has to be <= min value. Initial value is set to min value");
             initInt = minInt;


         }else if(initInt > maxInt){
             logger.error("Initial value has to be <= max value. Initial value is set to max value");
             initInt = maxInt; 
         }
        

         data.setMax(maxInt);
         data.setMin(minInt);
         data.setInitialValue(initInt);
         data.setName(name.getText());
         data.setType(type.getSelectedIndex());

         view.getModel().setValue(node, data);

         editor.cancelCellEditing();
         editor.stopCellEditing();
      }

      public void setValue(TreeNode node) {
         this.node = node;
         this.data = (ResourceVariableData)node.getNodeData();

         name.setText(data.getName());
         max.setText(Integer.toString(data.getMax() ) );
         min.setText(Integer.toString(data.getMin() ) );
         init.setText(Integer.toString(data.getInitialValue() ) );
         type.setSelectedIndex(data.getType());
      }

      public TreeNode getValue() {
         return node;
      }
   }

   /**
    *  Resource editor
    */
   static class ResourceEditor extends TreeEditor {

      protected JTextField name;
      protected JTextArea description;
      protected InsertVariable newVariable;
      protected InsertVariable newResource;

      private ResourceData data;
      private TreeNode node;


      public ResourceEditor(ResourceView view) {
         super(view);

         initiateGraphics();
      }

      private void initiateGraphics() {
         name = new JTextField(10);

         //Create the actions for the inserts
         newVariable = new InsertVariable(null, Data.RESOURCE_VARIABLE);
         newResource = new InsertVariable(null, Data.RESOURCE);

         //Create buttons that is connected with this view.
         JButton insertSubResource = new JButton(
               view.createAction("Insert resource", newResource, "resources/icons/robot.png"));
         JButton insertVariable = new JButton(
               view.createAction("Insert variable", newVariable, "resources/icons/variable.png"));


         JButton save = new JButton(saveIcon);
         save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               saveNode();
            }
         });
         save.setMnemonic(KeyEvent.VK_S);

         save.setMargin(new Insets(0, 0, 0, 0));


         description = new JTextArea(2, 30);

         name.setBorder(BorderFactory.createTitledBorder("Name"));
         description.setBorder(BorderFactory.createTitledBorder("Description"));

         name.setBackground(getBackground());
         description.setBackground(getBackground());

         this.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createBevelBorder(BevelBorder.LOWERED),
               BorderFactory.createBevelBorder(BevelBorder.RAISED)));

         GroupLayout l = new GroupLayout(this);
         this.setLayout(l);

         l.setHorizontalGroup(
               l.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(l.createSequentialGroup().addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(l.createSequentialGroup().addComponent(description).addGap(10).addComponent(save).addGap(3)).addGroup(l.createSequentialGroup().addComponent(name).addGap(10).addComponent(insertSubResource).addGap(5).addComponent(insertVariable).addGap(3)))));
         l.setVerticalGroup(
               l.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(l.createSequentialGroup().addGroup(l.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(name).addComponent(insertSubResource).addComponent(insertVariable)).addGroup(l.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(description).addComponent(save))));

      }

      public void setValue(TreeNode node) {
         this.node = node;
         this.data = (ResourceData)node.getNodeData();

         name.setText(data.getName());
         description.setText(data.getDescription());


         newResource.setParent(node);
         newVariable.setParent(node);
      }

      public TreeNode getValue() {
         return node;
      }

      protected void saveNode() {

         //When data is saved it should be to somewhere where a listener can be triggered.
        

         data.setName(name.getText());
         data.setDescription(description.getText());

         view.getModel().setValue(node, data);

         editor.cancelCellEditing();      
      }
   }
}
