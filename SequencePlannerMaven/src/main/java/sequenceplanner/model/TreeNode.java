package sequenceplanner.model;

import org.apache.log4j.Logger;
import sequenceplanner.model.data.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author Erik Ohlson, erik.a.ohlson@gmail.com
 */
//public class TreeNode implements ITreeNode {
public class TreeNode {

   protected ArrayList<TreeNode> children = new ArrayList<TreeNode>();
   protected TreeNode parent;
   protected Data nodeData;

   protected Comparator sortComparator;

   static Logger logger = Logger.getLogger(TreeNode.class);

   public TreeNode(Data nodeData) {
      this.nodeData = nodeData;

      if (nodeData instanceof ResourceData) {
         sortComparator = new CompareResource();
      } else {
         sortComparator = new CompareName();
      }
   }

   public TreeNode getChildAt(int i) {
      return children.get(i);
   }

   public int getChildCount() {
      return children.size();
   }

   public int getIndex(TreeNode child) {
      return children.indexOf(child);

   }

   public void insert(TreeNode child) {
      children.add(child);
      child.setParent(this);
      sort(child);
   }
   
   public void remove(TreeNode child) {
      child.setParent(null);
      children.remove(child);
   }

   public TreeNode getParent() {
      return parent;
   }

   public void setParent(TreeNode parent) {
      this.parent = parent;
   }

   // Possible to implement differnt kinds of sorting depending on e.g. type.
   protected void sort(TreeNode insertedChild) {
      Collections.sort(children, sortComparator);
   }

   /**
    * Inline class for sorting according to sequence order.
    */
   public class CompareName implements Comparator {

      // Example plugin-comparator
      public int compare(Object o1, Object o2) {
         return 0;
      }
   }

    /**
    * Inline class for sorting according to sequence order.
    */
   public class CompareResource implements Comparator {

      public int compare(Object o1, Object o2) {
         if (o1 instanceof TreeNode && o2 instanceof TreeNode) {
            TreeNode t1 = (TreeNode)o1;
            TreeNode t2 = (TreeNode)o2;

            Data d1 = t1.getNodeData();
            Data d2 = t2.getNodeData();

            if (d1 instanceof ResourceVariableData
                    && d2 instanceof ResourceVariableData ) {
                    return d1.getName().compareTo(d2.getName());
            } else if ((d1 instanceof ResourceVariableData)
                    && !(d2 instanceof ResourceVariableData)  ) {
               return -1;
            } else if (!(d1 instanceof ResourceVariableData)
                    && (d2 instanceof ResourceVariableData)  ) {
               return 1;
            } else if (!(d1 instanceof ResourceVariableData)
                    && !(d2 instanceof ResourceVariableData)  ) {
               return d1.getName().compareTo(d2.getName());
            }
         }

         return 0;
      }
   }


   public Data getNodeData() {
      return nodeData;
   }

   public void setNodeData(Data nodeData) {
      this.nodeData = nodeData;
   }

   @Override
   public String toString() {
      return nodeData.getName();
   }

   public int getId() {
      return getNodeData().getId();
   }

   @Override
   public int hashCode() {
      return nodeData.getId();
   }

   public void removeAllChildren() {
      children.clear();
   }


   @Override
   public boolean equals(Object obj) {

//      TreeNode node = (obj instanceof TreeNode) ? (TreeNode) obj : null;
//
//      if (node != null && getNodeData().equals(node.getNodeData()) && getChildCount() == node.getChildCount()) {
//
//         for (int i = 0; i < getChildCount(); i++) {
//            int id = getChildAt(i).getId();
//            boolean tmpTest = false;
//
//            for (int j = 0; j < node.getChildCount(); j++) {
//               if ( node.getChildAt(j).getId() == id ) {
//                  tmpTest = getChildAt(i).equals(node.getChildAt(j) );
//
//               }
//            }
//
//            if (!tmpTest) {
//               return false;
//            }
//         }
//
//         return true;
//
//      } else {
//        return false;
//      }
      return this == obj;
   }
}