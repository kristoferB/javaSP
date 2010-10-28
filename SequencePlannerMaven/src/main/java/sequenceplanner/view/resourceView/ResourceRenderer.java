package sequenceplanner.view.resourceView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;


import javax.swing.tree.TreeCellRenderer;
import sequenceplanner.SequencePlanner;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.view.resourceView.Renderers.TreeRenderer;

/**
 *
 * @author erik
 */
public class ResourceRenderer extends JPanel
      implements TreeCellRenderer {

   private JLabel reg;
   protected HashMap<String, TreeRenderer> editors;
   private JLabel icon;
   private JPanel p;

   //Icons
   private final ImageIcon robot = SequencePlanner.getNewIcon("resources/icons/robot.png");
   private final ImageIcon variable = SequencePlanner.getNewIcon("resources/icons/variable.png");

   public ResourceRenderer() {
      editors = new HashMap<String, TreeRenderer>();
      reg = new JLabel();
      icon = new JLabel();
      p = new JPanel(new BorderLayout());

      p.add(icon, BorderLayout.WEST);

   }

   public Component getTreeCellRendererComponent(JTree tree, Object value,
         boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      Data d = ((TreeNode)value).getNodeData();


      TreeNode node = ((TreeNode)value);

      TreeRenderer renderer = editors.get(d.getClass().getName());

      if(renderer != null) {
         renderer.setValue(node);

         if (Model.isResource(d)) {
            setView(renderer, robot);
         } else if (Model.isVariable(d)) {
            setView(renderer, variable);
         }
         

      } else {
         reg.setText(d.getName());
         setView(reg, robot);
      }

      return p;
   }

   public void registerRenderer(Class type, TreeRenderer editor) {
      editors.put(type.getName(), editor);
   }

   private void setView(Component c, ImageIcon ico) {
      p.removeAll();
      if(ico != null) {
         icon.setIcon(ico);
         p.add(icon, BorderLayout.WEST);
      }

      p.add(c, BorderLayout.CENTER);

   }

   
}
