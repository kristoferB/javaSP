package sequenceplanner;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class SplitNodeTest {

   private JScrollPane getContent() {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
      DefaultMutableTreeNode root1 = new DefaultMutableTreeNode(new SplitNode("Node 1", false));
      DefaultMutableTreeNode root2 = new DefaultMutableTreeNode(new SplitNode("Node 2", false));
      root1.add(root2);
      root.add(root1);
// root.add(new DefaultMutableTreeNode(new SplitNode("Node 1", false)));
// root.add(new DefaultMutableTreeNode(new SplitNode("Node 2", true)));
      JTree tree = new JTree(new DefaultTreeModel(root));
      tree.setEditable(true);
      tree.setCellRenderer(new SplitNodeRenderer());
      tree.setCellEditor(new SplitNodeEditor(tree, root));
      return new JScrollPane(tree);
   }

   public static void main(String[] args) {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.getContentPane().add(new SplitNodeTest().getContent());
      f.setSize(360, 300);
      f.setLocation(200, 200);
      f.setVisible(true);
   }
}

class SplitNode extends DefaultMutableTreeNode {

   String name;
   boolean value;
   protected boolean isSelected;

   public SplitNode(String s, boolean isSelected) {
      name = s;
      this.isSelected = isSelected;
   }

   public void setSelected(boolean isSelected) {
      this.isSelected = isSelected;

      if(children != null) {
         Enumeration enum2 = children.elements();


         while (enum2.hasMoreElements()) {
            SplitNode node = (SplitNode)enum2.nextElement();
            node.setSelected(isSelected);
         }
      }
   }

   public boolean isSelected() {
      return isSelected;
   }
}

class SplitNodeRenderer implements TreeCellRenderer {

   JLabel label;
   JCheckBox checkBox;
   JTextField textField;
   JPanel panel;

   public SplitNodeRenderer() {
      label = new JLabel();
      checkBox = new JCheckBox();
      checkBox.setBackground(UIManager.getColor("Tree.background"));
      checkBox.setBorder(null);
      textField = new JTextField();
      textField.setEditable(false);
      textField.setBackground(UIManager.getColor("Tree.background"));
      textField.setBorder(null);
      panel = new JPanel();
      panel.setOpaque(false);
      panel.add(checkBox);
      panel.add(textField);
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value,
         boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

      if(node.getUserObject() instanceof SplitNode) {
         SplitNode splitNode = (SplitNode)node.getUserObject();
         checkBox.setSelected(selected);
         textField.setText(splitNode.name);
         return panel;
      } else {
         label.setText(node.toString());
         return label;
      }
   }
}

class SplitNodeEditor extends AbstractCellEditor implements TreeCellEditor, ActionListener {

   JLabel label;
   JCheckBox checkBox;
   JTextField textField;
   SplitNode splitNode;
   JComponent editedComponent;
   JPanel panel;
   final JTree Tree;
   Object value1;

   public SplitNodeEditor(JTree tree, Object value) {
      label = new JLabel();
      checkBox = new JCheckBox();
      checkBox.addActionListener(this);
      checkBox.setBackground(UIManager.getColor("Tree.background"));
      checkBox.setBorder(null);
      this.Tree = tree;

      value1 = value;
      checkBox.addMouseListener(new MouseAdapter() {

         public void mousePressed(MouseEvent e) {
            System.out.println("hello");
            int x = e.getX();
            int y = e.getY();
            int row = Tree.getRowForLocation(x, y);

            TreePath path = Tree.getPathForRow(1);

            DefaultMutableTreeNode node1 = (DefaultMutableTreeNode)value1;

            if(path != null && node1.getUserObject() instanceof SplitNode) {
               SplitNode node = (SplitNode)path.getLastPathComponent();
               boolean isSelected = !(node.isSelected());
               System.out.println(isSelected);
               node.setSelected(isSelected);
               ((DefaultTreeModel)Tree.getModel()).nodeChanged(node);
            }
         }

         public void mouseReleased(MouseEvent e) {
            checkBox.setBorder(null);
         }
      });
      textField = new JTextField();
      textField.addActionListener(this);
      textField.setBackground(UIManager.getColor("Tree.background"));
      textField.setBorder(null);
      panel = new JPanel();
      panel.setOpaque(false);
      panel.add(checkBox);
      panel.add(textField);
   }

   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
      if(node.getUserObject() instanceof SplitNode) {
         splitNode = (SplitNode)node.getUserObject();
         checkBox.setSelected(splitNode.isSelected());
         textField.setText(splitNode.name);
         return panel;
      } else {
         label.setText(node.toString());
         return label;
      }
   }

   public Object getCellEditorValue() {
      if(editedComponent == textField) {
         splitNode.name = textField.getText();
      } else {
         splitNode.value = checkBox.isSelected();
      }
      return splitNode;
   }

   public boolean isCellEditable(EventObject anEvent) {
      if(anEvent instanceof MouseEvent) {
         /*Point p= ((MouseEvent)anEvent).getPoint();
         JTree tree =(JTree)anEvent.getSource();
         TreePathpath = tree.getPathForLocation(p.x,p.y);
         DefaultMutableTreeNodenode =(DefaultMutableTreeNode)path.getLastPathComponent();
         int clickCountToStart=(node.getUserObject() instanceof SplitNode) ? 1 : 2;*/
//return((MouseEvent)anEvent).getClickCount() >= clickCountToStart;
      }
      return true;
   }

   public void actionPerformed(ActionEvent e) {
      editedComponent = (JComponent)e.getSource();
      super.stopCellEditing();
   }
}

