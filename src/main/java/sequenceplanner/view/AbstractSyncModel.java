package sequenceplanner.view;

import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import sequenceplanner.model.IModel;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;

/**
 *
 * @author Erik Ohlson
 */
public abstract class AbstractSyncModel implements TreeModel, IModel.SyncModelListener {

   protected Model model;
   private TreeNode[] root;

   public AbstractSyncModel(Model model) {
      this(model, model.getRoot());
   }

   public AbstractSyncModel(Model model, TreeNode root) {
      this.model = model;
      root = getLocalInitalRoot(root);
      setRoot(root);
      this.model = model;
      model.addSyncModelListener(this);
   }

   protected TreeNode getLocalInitalRoot(TreeNode root) {
      return root;
   }

   protected void destroyView() {
      System.out.println("Destroy this view");
   }

   public Object getChild(Object parent, int index) {
      return model.getChild(parent, index);
   }

   public int getChildCount(Object parent) {
      return model.getChildCount(parent);
   }

   public int getIndexOfChild(Object parent, Object child) {
      return model.getIndexOfChild(parent, child);
   }

   public Object getRoot() {
      return root[root.length-1];
   }

   public void setRoot(TreeNode root) {
      this.root = model.getPath(root);
      
      changeStructure(new TreeNode[] { root });
   }

   public boolean isLeaf(Object node) {
      if(getChildCount(node) == 0) {
         return true;
      }

      return false;
   }
   private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

   public void addTreeModelListener(TreeModelListener l) {
      listeners.add(l);
   }

   public void removeTreeModelListener(TreeModelListener l) {
      listeners.remove(l);
   }

   // SYNCLISTNERS FROM MODEL
   @Override
   public void bigChange(TreeNode[] path) {
      changeStructure(path);
   }

   public void insert(TreeNode insertedNode, TreeNode[] path, int place) {

      path = convertPath(path, root[root.length-1]);

      for (int i = 0; i < path.length; i++) {
         TreeNode treeNode = path[i];
      }



      if (path.length > 0) {
         //If the change affect this model
         insertIntoModel(insertedNode, path, place);
      }

      if (path.length == 1 && path[0].getChildCount() == 1) {
         changeStructure(path);
      }
   }



   public void remove(TreeNode removedNode, TreeNode[] previusPath, int place) {
      //Is this root removed
      for (int i = 0; i < root.length; i++) {
         if (root[i] == removedNode) {
            destroyView();
         }
      }

      previusPath = convertPath(previusPath, root[root.length-1]);
      if (previusPath.length > 0) {
         //If the change affect this model
         removeFromModel(removedNode, previusPath, place);
      }
   }

   ///////////////////////
   public void insertIntoModel(TreeNode insertedNode, TreeNode[] path, int place) {

      int[] ch = {place};

      TreeModelEvent e = new TreeModelEvent(this, path, ch, new TreeNode[]{insertedNode});

      for (TreeModelListener treeModelListener : listeners) {
         treeModelListener.treeNodesInserted(e);
      }
   }

   public void removeFromModel(TreeNode removedNode, TreeNode[] previusPath, int place) {

      int[] ch = {place};

      TreeModelEvent e = new TreeModelEvent(this, previusPath, ch, new TreeNode[]{removedNode});

      for (TreeModelListener treeModelListener : listeners) {
         treeModelListener.treeNodesRemoved(e);
      }

   }

   public void dataChange(TreeNode changedNode, TreeNode[] path, int place) {
      int[] ch = { place };

      TreeModelEvent e = new TreeModelEvent(this, path, ch, new Object[] { changedNode });

      for (TreeModelListener treeModelListener : listeners) {
         treeModelListener.treeNodesChanged(e);
      }
   }

   


   public void changeStructure(TreeNode[] path) {
      
      TreeModelEvent e = new TreeModelEvent(this, path);

      for (TreeModelListener treeModelListener : listeners) {
         treeModelListener.treeStructureChanged(e);
      }

   }
   // -- END model listeners


   public TreeNode[] getPath(TreeNode node) {
      return convertPath(model.getPath(node), (TreeNode)getRoot() );

   }

   protected TreeNode[] convertPath(TreeNode[] inPath, TreeNode newRoot) {
      int inL = inPath.length, i =0;

      while (i < inL) {
         if (inPath[i] == newRoot) {
            break;
         }
         i++;
      }

      if (i == inL) {
         return new TreeNode[0];
      }

      int outL = inL - i;
      TreeNode[] outPath = new TreeNode[outL];

      while (i < inL) {
         outPath[i - (inL - outL)] = inPath[i];
         i++;
      }

      return outPath;
      
   }

   public void valueForPathChanged(TreePath path, Object newValue) {
      
   }
}
