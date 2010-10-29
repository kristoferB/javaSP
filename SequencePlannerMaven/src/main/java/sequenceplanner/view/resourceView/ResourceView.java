package sequenceplanner.view.resourceView;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import sequenceplanner.SPContainer;
import sequenceplanner.SequencePlanner;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.view.AbstractView;
import sequenceplanner.view.resourceView.Editors.ResourceEditor;
import sequenceplanner.view.resourceView.Editors.VariableEditor;
import sequenceplanner.view.resourceView.Renderers.ResourcePanel;
import sequenceplanner.view.resourceView.Renderers.VariablePanel;
import sequenceplanner.view.resourceView.Renderers.VariablePanel;
import sequenceplanner.view.treeView.ClickMenu;

/**
 *
 * @author Erik Ohlson
 */
public class ResourceView extends AbstractView {

   protected final JTree tree;
   protected ResourceModel rModel;

   public ResourceView(SPContainer spc, String name) {
      super(spc, name);

      rModel = new ResourceModel(model);

      tree = new JTree(rModel);

      tree.setRootVisible(false);
      tree.setToggleClickCount(-1);
      tree.setRowHeight(-1);
      tree.setShowsRootHandles(true);
      tree.setEditable(true);

      //Create and register editors
      ResourceTreeEditor rte = new ResourceTreeEditor();
      rte.registerEditor(ResourceData.class, new ResourceEditor(this));
      rte.registerEditor(ResourceVariableData.class, new VariableEditor(this));

      //Create and register renderers
      ResourceRenderer rr = new ResourceRenderer();
      rr.registerRenderer(ResourceData.class, new ResourcePanel(this));
      rr.registerRenderer(ResourceVariableData.class, new VariablePanel(this));

      tree.setCellEditor(rte);
      tree.setBackground(getBackground());
      tree.setCellRenderer(rr);

      ((BasicTreeUI) tree.getUI()).setExpandedIcon(SequencePlanner.getNewIcon("resources/icons/min.png"));
      ((BasicTreeUI) tree.getUI()).setCollapsedIcon(SequencePlanner.getNewIcon("resources/icons/max.png"));

      tree.addMouseListener(new MouseAdapter() {

         @Override
         public void mousePressed(MouseEvent e) {
            popup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            popup(e);
         }

         private void popup(MouseEvent e) {
            if (e.isPopupTrigger()) {
               TreePath path = tree.getPathForLocation(e.getX(), e.getY());
               if (path != null) {
                  tree.setSelectionPath(path);
                  ClickMenu c = new ClickMenu((TreeNode) path.getLastPathComponent(), ResourceView.this.model);
                  c.show(ResourceView.this, e);
               }
            }
         }
      });



      this.setLayout(new BorderLayout());
      this.add(new JScrollPane(tree), BorderLayout.CENTER);
   }

   public ResourceView(SPContainer spc, TreeNode root, String name) {
      this(spc, name);
      setRoot(root);
   }

   public void setRoot(TreeNode root) {
      rModel.setRoot(root);
   }

   @Override
   public Action createAction(String name, ActionListener usedAction, String icon) {
      return super.createAction(name, usedAction, icon, this);
   }

   public boolean moveRootUp() {
      TreeNode oldP = ((TreeNode) rModel.getRoot());
      TreeNode p = oldP.getParent();

      if (p != null) {
         rModel.setRoot(p);
         TreePath tp = new TreePath(rModel.getPath(oldP));
         tree.expandPath(tp);
         return true;
      }

      return false;
   }

   @Override
   public boolean closeView() {
      return true;
   }


}
