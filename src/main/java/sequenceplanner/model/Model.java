package sequenceplanner.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Stack;
import java.util.TreeMap;
import javax.swing.event.TreeModelListener;

import org.apache.log4j.Logger;
import sequenceplanner.editor.EditorTreeModel;

import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.FolderData;
import sequenceplanner.model.data.LiasonData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.Constansts;

/**
 *
 * @author Erik Ohlson
 *
 * This class will be specialized to keep operations, liasons and resources. 
 * If you in the future want to add to this the model probably will need
 * heavy reprogramming.
 */



public class Model extends Observable implements IModel{

   public static final String VARIABLE_ROOT_NAME = "Variables";

   static Logger logger = Logger.getLogger(Model.class);
   private static int idConter = 5;
   private static int propertyIdCounter = 0;
   //Holds a cache for easy access to all operations names and paths
   private NameCacheMap nameCache;
   //Holds operationspecific view
   private TreeMap<Integer, ViewData> operationViews;
   //Listeners
   private LinkedList<AsyncModelListener> aSyncListeners;
   //Listeners
   private LinkedList<SyncModelListener> syncListeners;
   // Holds the root for the tree
   private TreeNode treeRoot;
   // Holds the root to the operation folder, to enhance redability of code.
   private TreeNode operationRoot;
   // Holds the root to the Liason folder, to enhance redability of code.
   private TreeNode liasonRoot;
   // Holds the root to the Resource folder, to enhance redability of code.
   private TreeNode resourceRoot;
   //Reference to the resource that holds all variables used in graphical objects.
   private TreeNode variableRoot;
   // Holds the root to the View folder, to enhance redability of code.
   protected TreeNode viewRoot;
   //Holds info about global properties
   private EditorTreeModel globalProperties;

   public Model() {
      treeRoot = new TreeNode(new Data("root", newId()));

      //Initalize tree
      // -------------------
      operationRoot = new TreeNode(new FolderData("Operations", newId()));
      treeRoot.insert(operationRoot);

      //liasonRoot = new TreeNode(new FolderData("Liasons", newId()));
      //treeRoot.insert(liasonRoot);

      resourceRoot = new TreeNode(new FolderData("Resources", newId()));


      treeRoot.insert(resourceRoot);

      viewRoot = new TreeNode(new FolderData("View", newId()));
      treeRoot.insert(viewRoot);
      // ---------------------

      //Initialize global properties
      globalProperties = new EditorTreeModel();

      aSyncListeners = new LinkedList<AsyncModelListener>();
      syncListeners = new LinkedList<SyncModelListener>();

      nameCache = new NameCacheMap();
      operationViews = new TreeMap<Integer, ViewData>();
      
   }

   @Override
   public void addAsyncModelListener(AsyncModelListener l) {
      aSyncListeners.add(l);
   }

   @Override
   public void removeAsyncModelListener(AsyncModelListener l) {
      aSyncListeners.remove(l);
   }

   @Override
   public void addSyncModelListener(SyncModelListener l) {
      syncListeners.add(l);
   }

   @Override
   public void removeSyncModelListener(SyncModelListener l) {
      syncListeners.remove(l);
   }

   public void addTreeModelListener(TreeModelListener l){
      globalProperties.addTreeModelListener(l);
   }

   public TreeNode getRoot() {
      return treeRoot;
   }

   public TreeNode getLiasonRoot() {
      return liasonRoot;
   }

   public TreeNode getOperationRoot() {
      return operationRoot;
   }

   public TreeNode getResourceRoot() {
      return resourceRoot;
   }

   public TreeNode getVariableRoot() {
      return variableRoot;
   }

   public void setVariableRoot(TreeNode vRoot) {
      variableRoot = vRoot;
   }

   public TreeNode getViewRoot() {
      return viewRoot;
   }

   public static int newId() {
      return idConter++;
   }

   public static int newPropertyId(){
      return propertyIdCounter++;
   }

   // Handle views
   public boolean isViewPresent(String name) {
      return isNamePresent(getViewRoot(), name, true);
   }

   public boolean saveOperationViews(ViewData[] data) {
      for (int i = 0; i < data.length; i++) {
         ViewData viewData = data[i];
         operationViews.put(viewData.getRoot(), viewData);
      }
      return true;
   }

   public TreeMap<Integer, ViewData> getOperationsWithViews() {
      return operationViews;
   }

   public ViewData getOperationView(int id) {
      ViewData out = operationViews.get(id);

      if (out != null) {
         return out;
      }

      if (out == null && isOperationPresent(id)) {

         String name = getOperation(id).getNodeData().getName();
         out = new ViewData(name, -1);
         out.setRoot(id);
      } else {
         return null;
      }

      return out;
   }

   public EditorTreeModel getGlobalProperties(){
        return globalProperties;
   }

   /**
    * If name of data is already present -> overwrite.
    * @param data 
    * @return
    */
   public boolean saveView(ViewData data) {
      //TODO verify that an inputted view is valid
      String name = data.getName();

      for (int i = 0; i < getViewRoot().getChildCount(); i++) {
         if (getViewRoot().getChildAt(i).getNodeData().getName().equals(name)) {
            setValue(getViewRoot().getChildAt(i), data);
            return true;
         }
      }
      TreeNode viewNode = new TreeNode(data);
      insertChild(getViewRoot(), viewNode);
      return true;
   }

   /**
    *  Just saveOperation but accepts an arrayinput and do an extra check of inputed data;
    * @param data
    */
   public void saveOperationData(TreeNode[] data) {

      for (int i = 0; i < data.length; i++) {
         TreeNode node = data[i];

         if (node.getNodeData() instanceof OperationData) {
             OperationData od = (OperationData) node.getNodeData();
             saveNode(data[i], operationRoot);
             setChanged();
             notifyObservers(od);
         } else {
             logger.debug("An none operationdata was inserted to saveData(TreeNode[] data)");
         }

      }

      reloadNamesCache();
   }

   /**
    *
    * @param id
    * @param root search for TreeNode with id from root
    * @return
    */
   private TreeNode saveNode(TreeNode newNode, TreeNode root) {
      ArrayList<Integer> oldTree = getIds(root, -1);

      TreeNode node = getNode(newNode.getId(), root);

      if (node == null) { //Node not present in model
         // Create new empty root
         node = new TreeNode(new OperationData("root", newNode.getId()));
         insertChild(root, node);

      }
//      else if (node.equals(newNode)) { // Node is present in model and is updated
//         return node;
//      }

      // The node is present, what should we change in the model?
      ArrayList<Integer> newTree = getIds(newNode, -1);

      // Get the intersection of oldTree and newTree to get if any is removed.
      ArrayList<Integer> toMove = getInteserction(getIds(root, node.getId()), newTree);

      // Only top cells need to be handled

      if (toMove.size() > 0) {
         toMove = getTopNodes(toMove);
         removeNodes(toMove, treeRoot);
      }

// newTree is the model version






// oModelTree - newTree = is any cells in the old tree removed.
// FIXME:
//Insert new node
      TreeNode parent = node.getParent();
      parent.remove(node);
      parent.insert(newNode);



      ArrayList<Integer> reallyNewTree = getIds(root, -1);
      for (Integer i : reallyNewTree) {
         oldTree.remove(i);
      }

      //Oldtree now contains all opeations that really has been moved.



      fireSyncBigChangeEvent(getPath(parent));

      reloadNamesCache();
      if(newNode.getNodeData() instanceof OperationData){
          OperationData od = (OperationData) newNode.getNodeData();
      }
      return newNode;

   }

   public boolean isOperationPresent(int id) {
      return isNodePresent(id, operationRoot);
   }

   /**
    *
    * @param id
    * @return return node if found else it return null.
    */
   public TreeNode getResource(int id) {
      return getNode(id, getResourceRoot());
   }

   public TreeNode getOperation(int id) {
      return getNode(id, getOperationRoot());
   }

   public TreeNode getNode(int id) {
      return getNode(id, getRoot());
   }

   protected void updateCachedName(int id) {
      TreeNode node = getNode(id);

      String name = node.getNodeData().getName();

      String path = "";

      node =
            node.getParent();
      while (node != resourceRoot && node != liasonRoot && node != operationRoot) {

         if (path.isEmpty()) {
            path = node.getNodeData().getName();
         } else {
            path = node.getNodeData().getName() + "." + path;
         }

         node = node.getParent();
      }

      nameCache.put(id, path, name);


   }

   public void reloadNamesCache() {
      nameCache.clearMap();
      reloadNameChache(resourceRoot, "");
      reloadNameChache(operationRoot, "");
   }

   protected void reloadNameChache(TreeNode node, String path) {
      for (int i = 0; i <
            node.getChildCount(); i++) {
         TreeNode child = node.getChildAt(i);
         String name = child.getNodeData().getName();
         nameCache.put(child.getId(), path, name);

         String newPath = name;
         if (!path.isEmpty()) {
            newPath = path + (path.isEmpty() ? "" : ".") + name;
         }

         reloadNameChache(child, newPath);
      }

   }

   public NameCacheMap getNameCache() {
      return nameCache;
   }

   public boolean isLiason(int id) {
      return isNodePresent(id, liasonRoot);
   }

   public void saveLiason(TreeNode liason) {
   }

   public boolean isResourcePresent(int id) {
      return isNodePresent(id, resourceRoot);
   }

   public boolean createGroupVariable(int id) {
      if (getVariableRoot() == null) {
         variableRoot = new TreeNode(new ResourceData(VARIABLE_ROOT_NAME, idConter));
         insertChild(getResourceRoot(), variableRoot);
      }

      if (!isNodePresent(id, variableRoot)) {
         ResourceVariableData var = new ResourceVariableData("Alt" + Integer.toString(id), id);
         var.setType(ResourceVariableData.BINARY);
         var.setInitialValue(0);
         var.setMax(1);
         var.setMin(0);

         TreeNode variable = new TreeNode(var);

         insertChild(variableRoot, variable);
      }

      return true;
   }

   /**
    *
    * @param id
    * @param root search for TreeNode with id from root
    * @return
    */
   protected boolean isNodePresent(int id, TreeNode root) {
      if (getNode(id, root) != null) {
         return true;
      } else {
         return false;
      }

   }

   /**
    * @param id
    * @param root
    * @return null if id is not present
    *
    */
   protected TreeNode getNode(int id, TreeNode root) {

      Stack<TreeNode> children = new Stack<TreeNode>();
      children.push(root);

      TreeNode temp;

      while (!children.isEmpty()) {
         temp = children.pop();

         if (temp.getNodeData().getId() == id) {
            return temp;
         }

         for (int i = 0; i < temp.getChildCount(); i++) {
            children.push(temp.getChildAt(i));
         }

      }

      return null;
   }

   public ArrayList<Integer> getIds(TreeNode topNode, int exclude) {
      ArrayList<Integer> ex = new ArrayList<Integer>();
      ex.add(exclude);

      return getIds(topNode, ex);
   }

   private ArrayList<Integer> getIds(TreeNode topNode, ArrayList<Integer> exclude) {

      ArrayList<Integer> tId = new ArrayList<Integer>();
      Stack<TreeNode> nodes = new Stack<TreeNode>();

      nodes.push(topNode);

      while (!nodes.isEmpty()) {
         TreeNode node = nodes.pop();

         if (!exclude.contains(node.getId())) {
            tId.add(node.getId());

            for (int i = 0; i <
                  node.getChildCount(); i++) {
               nodes.push(node.getChildAt(i));
            }

         }
      }

      return tId;
   }

   private ArrayList<Integer> getInteserction(ArrayList<Integer> one, ArrayList<Integer> two) {
      ArrayList<Integer> result = new ArrayList<Integer>();

      for (Integer id : one) {

         for (Integer idIn : two) {

            if (id.equals(idIn)) {
               result.add(id);
            }

         }
      }

      return result;
   }

   protected void updatePreconditions(TreeNode node) {

      for (int i = 0; i < node.getChildCount(); i++) {
         OperationData d = ((OperationData) node.getChildAt(i).getNodeData());

         d.setPrecondition(Model.updateCondition(nameCache, d.getSequenceCondition(), d.getResourceBooking()));
         d.setPostcondition(Model.updateCondition(nameCache, d.getPSequenceCondition(), d.getPResourceBooking()));

         updatePreconditions(node.getChildAt(i));
      }
   }

   public static String updateCondition(NameCacheMap cache,
         LinkedList<LinkedList<SeqCond>> sequenceCondition,
         LinkedList<Integer[]> resources){
      return updateCondition(cache, sequenceCondition, resources, true);
}

   public static String updateCondition(NameCacheMap cache,
         LinkedList<LinkedList<SeqCond>> sequenceCondition,
         LinkedList<Integer[]> resources, boolean showPath) {
      String s = "";


      for (LinkedList<SeqCond> linkedList : sequenceCondition) {
         if (linkedList.size() == 1) {
            s = s.isEmpty() ? s : s + " " + Constansts.AND + " ";

            String[] out = cache.get(linkedList.getFirst().id);

            SeqCond seqCond = linkedList.getFirst();

            s += showPath ? out[0] + "." : "";
            s += out[1];
            if (seqCond.isOperationCheck()) {
                  s += getOperationEnding(seqCond.state);

               } else if (seqCond.isVariableCheck()) {
                  s += getVariabelCheck(seqCond.state) + Integer.toString(seqCond.value);
               }

            

         } else if (linkedList.size() > 1) {
            s += s.isEmpty() ? s : " " + Constansts.AND + " ";

            boolean is = s.isEmpty();

            s = is ? s : s + " (";
            for (Iterator<SeqCond> it = linkedList.iterator(); it.hasNext();) {
               SeqCond seqCond = it.next();

               
               String[] out = cache.get(seqCond.id);


               s += showPath && !out[0].isEmpty() ? out[0] + "."  : "";
               s += out[1];

              
               if (seqCond.isOperationCheck()) {
                  s += getOperationEnding(seqCond.state);

               } else if (seqCond.isVariableCheck()) {
                  s += getVariabelCheck(seqCond.state) + Integer.toString(seqCond.value);
               }
               

               if (it.hasNext()) {
                  s = s + " " + Constansts.OR + " ";
               }
            }
            s = is ? s : s + ") ";

         }

      }


      s = s.isEmpty() || resources.isEmpty() ? s : s + " " + Constansts.AND + " ";

      for (Iterator<Integer[]> it = resources.iterator(); it.hasNext();) {
         Integer[] integers = it.next();
         String[] out = cache.get(integers[0]);
         s =
               s + out[0] + "." + out[1] + getResourceEnding(integers[1]);

         if (it.hasNext()) {
            s = s + " " + Constansts.AND + " ";
         }

      }

      return s;
   }

   public static String getVariabelCheck(int equalType) {
      if (equalType == 0) {
         return "==";
      } else if (equalType == 1) {
         return "<=";
      } else if (equalType == 2) {
         return ">=";
      } else if (equalType == 3) {
         return "<";
      } else if (equalType == 4) {
         return ">";
      }

      return "";
   }

    public static String getActionSetType(int setType) {
      if (setType == OperationData.ACTION_ADD) {
         return "+=";
      } else if (setType == OperationData.ACTION_DEC) {
         return "-=";
      } else if (setType == OperationData.ACTION_EQ) {
         return "=";
      }
      return "";
   }

   public static String getOperationEnding(int state) {
      String s = Constansts.ENDING;

      if (state == 0) {
         s = s + "i";
      } else if (state == 1) {
         s = s + "e";
      } else if (state == 2) {
         s = s + "f";
      }

      return s;
   }

   public static String getResourceEnding(
         int state) {
      if (state == 0) {
         return "-";
      } else if (state == 1) {
         return "+";
      }

      return "";
   }

   /**
    *
    * @param node TreeNode to where you want to get path
    * @return ordered array of parents with the root first and inputed node the last
    */
   public TreeNode[] getPath(TreeNode node) {
      LinkedList<TreeNode> path = new LinkedList<TreeNode>();

      while (node.getParent() != null) {
         path.addFirst(node);
         node =
               node.getParent();
      }

      path.addFirst(node);

      TreeNode[] n = new TreeNode[path.size()];

      int i = 0;
      for (TreeNode treeNode : path) {
         n[i++] = treeNode;
      }

      return n;
   }

   public TreeNode[] getOperationRealizedBy(final int resource) {
      
      PreferenceFilter f = new PreferenceFilter() {

         @Override
         public boolean approved(TreeNode node) {
            if (node.getNodeData() instanceof OperationData) {
               OperationData d = (OperationData) node.getNodeData();

               if (d.getRealizedBy() == resource ) {
                  return true;
               }
            }
            return false;
         }
      };

      return getOperations(f, true);
   }

   private ArrayList<Integer> getListIds(ArrayList<TreeNode> nodes) {
      ArrayList<Integer> result = new ArrayList<Integer>();

      for (TreeNode node : nodes) {
         result.add(node.getId());
      }

      return result;

   }

   private ArrayList<TreeNode> getNodesFromId(ArrayList<Integer> nodesId) {
      ArrayList<TreeNode> result = new ArrayList<TreeNode>();

      for (Integer id : nodesId) {
         TreeNode node = getNode(id);
         if (node != null) {
            result.add(node);
         }
      }

      return result;
   }
   private TreeNode[] getOperations(PreferenceFilter filter, boolean onlyTop) {

      ArrayList<TreeNode> result = new ArrayList<TreeNode>();
      

      Stack<TreeNode> tempS = new Stack<TreeNode>();
      tempS.add(getOperationRoot());

      while (!tempS.isEmpty()) {
         TreeNode node = tempS.pop();

         for(int i = 0; i < node.getChildCount(); i++) {
            tempS.add(node.getChildAt(i));
         }

         if (filter.approved(node)) {
            result.add(node);
         }
      }

      if (onlyTop) {
         ArrayList<Integer> topNodes = getTopNodes(getListIds(result));
         result = getNodesFromId(topNodes);
      }


      return result.toArray(new TreeNode[0]);
   }

   protected interface PreferenceFilter {
      public boolean approved(TreeNode node);
   }


   private boolean isParentIncluded(Integer id, ArrayList<Integer> list){
       TreeNode[] tN = getPath(getNode(id, treeRoot));
       for (int i = tN.length - 2; i >= 0; i--) {
           if (list.contains(tN[i].getId()))
               return true;
       }
       return false;
   }

   private ArrayList<Integer> getTopNodes(ArrayList<Integer> toSort) {

      ArrayList<Integer> result = new ArrayList<Integer>();
      for (Integer id : toSort) {
          if (!isParentIncluded(id,toSort)){
              result.add(id);
          }
      }
      return result;
   }



   protected void removeNodes(ArrayList<Integer> id, TreeNode root) {
      for (Integer integer : id) {
         TreeNode tNode = getNode(integer, root);
         removeChild(tNode.getParent(), tNode);

      }

   }

   public static Object getParent(
         Object child) {

      if (child instanceof TreeNode) {
         return ((TreeNode) child).getParent();
      }

      return null;
   }

   public static Object getChild(
         Object parent, int index) {
      if (parent instanceof TreeNode) {
         return ((TreeNode) parent).getChildAt(index);
      }

      return null;
   }

   public Data getNodeData(
         Object node) {
      if (node instanceof TreeNode) {
         return ((TreeNode) node).getNodeData();
      }

      return null;
   }

   public int getChildCount(Object parent) {
      if (parent instanceof TreeNode) {
         return ((TreeNode) parent).getChildCount();
      }

      return -1;
   }

   public void insertChild(TreeNode parent, TreeNode child) {

      if (allowedParent(parent, child)) {
         parent.insert(child);
         if (!isView(child.getNodeData())) {
            updateCachedName(child.getId());
         }

         fireSyncInsertEvent(child, getPath(parent), parent.getIndex(child));
      } else {
         logger.error("Not allowed to insert " + child + " into " + parent);
      }

   }

   /**
    * General rule for insert of new children.
    * @param parent
    * @param child
    *
    * @return if parent is allowed to include children.
    */
   public boolean allowedParent(TreeNode parent, TreeNode child) {

      Data pData = parent.getNodeData();
      Data cData = child.getNodeData();

      boolean result = false;


      if (isResource(cData)) {
         result = isResource(pData) || parent == getResourceRoot();

      } else if (isVariable(cData)) {
         result = isResource(pData);

      } else if (isLiason(cData)) {
         result = isLiason(pData) || parent == getLiasonRoot();

      } else if (isOperation(cData)) {
         result = isOperation(pData) || parent == getOperationRoot();

      } else if (isView(cData)) {
         result = parent == getViewRoot();
      }

      return result;
   }

   public void removeChild(TreeNode parent, TreeNode child) {

      TreeNode[] path = getPath(parent);
      int place = parent.getIndex(child);
      parent.remove(child);
      fireSyncRemoveEvent(child, path, place);

   }

   public int getIndexOfChild(Object parent, Object child) {

      if (parent instanceof TreeNode && child instanceof TreeNode) {
         return ((TreeNode) parent).getIndex((TreeNode) child);
      }

      return -1;
   }

   public void setValue(TreeNode node, Data d) {
      node.setNodeData(d);
      fireSyncNodeChangeEvent(node, getPath(node.getParent()), node.getParent().getIndex(node));

   }

   /**
    * @param cell 
    * @param name 
    * @param isParent 
    * @return
    */
   public boolean isNamePresent(TreeNode cell, String name, boolean isParent) {
      TreeNode parent = !isParent ? cell.getParent() : cell;

      if (parent != null) {
         for (int i = 0; i <
               parent.getChildCount(); i++) {
            TreeNode node = parent.getChildAt(i);

            if (node != cell && node.getNodeData().getName().equals(name)) {
               return true;
            }

         }
      } else {
         logger.error("Tried to check name on cell without parent with par = false");
      }

      return false;
   }

   public void setName(TreeNode cell, String name) {

      if (isNamePresent(cell, name, false)) {
         name = name + "" + cell.getId();

      }

      Data data = cell.getNodeData();
      data.setName(name);

      setValue(cell, data);
   }

   public void fireSyncRemoveEvent(TreeNode removed, TreeNode[] path, int place) {
      for (SyncModelListener l : syncListeners) {
         l.remove(removed, path, place);
      }

   }

   public void fireSyncInsertEvent(TreeNode inserted, TreeNode[] path, int place) {
      for (SyncModelListener l : syncListeners) {
         l.insert(inserted, path, place);
      }

   }

   public void fireSyncNodeChangeEvent(TreeNode changed, TreeNode[] path, int place) {
      for (SyncModelListener l : syncListeners) {
         l.dataChange(changed, path, place);
      }

   }

   public void fireSyncBigChangeEvent(TreeNode[] path) {
      for (SyncModelListener l : syncListeners) {
         l.bigChange(path);
      }

   }

   public void rootUpdated() {
      fireSyncBigChangeEvent(getPath(getRoot()));
   }

   public TreeNode getChild(
         int type) {
      Data d = null;
      int id = newId();


      if (type == Data.RESOURCE) {
         d = new ResourceData("R_" + Integer.toString(id), id);

      } else if (type == Data.RESOURCE_VARIABLE) {
         d = new ResourceVariableData("V_" + Integer.toString(id), id);

      } else if (type == Data.OPERATION) {
         d = new OperationData(Integer.toString(id), id);

      } else if (type == Data.LIASON) {
         d = new LiasonData(Integer.toString(id), id);

      }

      return new TreeNode(d);


   }

   public TreeNode[] getChildren(TreeNode parent) {
      TreeNode[] children = new TreeNode[parent.getChildCount()];
      for (int i = 0; i <
            parent.getChildCount(); i++) {
         children[i] = parent.getChildAt(i);
      }

      return children;
   }

   public static void giveId(Data data) {
      if (data.getId() == -1) {
         data.setId(newId());
      }

   }

   public static boolean isOperation(Object data) {
      return (data instanceof OperationData);
   }

   public static boolean isLiason(Object data) {
      return (data instanceof LiasonData);
   }

   public static boolean isResource(Object data) {
      return (data instanceof ResourceData);
   }

   public static boolean isFolder(Object data) {
      return (data instanceof FolderData);
   }

   public static boolean isVariable(Object data) {
      return (data instanceof ResourceVariableData);
   }

   public static boolean isView(Object data) {
      return (data instanceof ViewData);
   }

   public boolean isResourceRoot(TreeNode node) {
      return node == getResourceRoot();
   }

   public boolean isLiasonRoot(TreeNode node) {
      return node == getLiasonRoot();
   }

   public boolean giveDataId(Data d) {
      if (d.getId() < 0) {
         d.setId(newId());
         return true;
      }

      return false;
   }

   /**
    *
    * @param view view that is going to be saved. Observe that you have to update the root of the view.
    * @return
    */
   public boolean insertView(ViewData view) {
      Integer index = view.getRoot();

      if (index > -1) {
         operationViews.put(index, view);
         return true;
      }

      return false;
   }

   public ViewData getView(Integer id) {
      ViewData d = operationViews.get(id);

      if (d != null) {
         return d;
      } else {
         d = new ViewData(Integer.toString(id), -1);
         d.setRoot(id);
         return d;
      }
   }

   public int getCounter() {
      return idConter;
   }

   public void setCounter(int counter) {
      idConter = counter;
   }

   public void clearModel() {
      operationViews.clear();

      for (int i = 0; i < getRoot().getChildCount(); i++) {
         getRoot().getChildAt(i).removeAllChildren();
      }
   }
   
   public LinkedList<TreeNode> getAllOperations(){       
        LinkedList<TreeNode> operations = new LinkedList<TreeNode>();
                
        if(operationRoot == null || operationRoot.getChildCount() == 0)
            return operations;

        Stack<TreeNode> children = new Stack<TreeNode>();
        for (int i = 0; i < operationRoot.getChildCount(); i++)
            if(Model.isOperation(operationRoot.getChildAt(i).getNodeData()))
                children.push(operationRoot.getChildAt(i));

        while (!children.isEmpty()) {
            TreeNode temp = children.pop();
            operations.add(temp);
            for (int i = 0; i < temp.getChildCount(); i++)
                if(Model.isOperation(temp.getChildAt(i).getNodeData()))
                    children.push(temp.getChildAt(i));
        }
        return operations;
   }
   
   public LinkedList<TreeNode> getAllVariables(){
        LinkedList<TreeNode> variables = new LinkedList<TreeNode>();
        
        if(variableRoot == null || variableRoot.getChildCount() == 0)
            return variables;
        
        Stack<TreeNode> children = new Stack<TreeNode>();
        for (int i = 0; i < variableRoot.getChildCount(); i++) 
            if(Model.isVariable(variableRoot.getChildAt(i).getNodeData()))
                children.push(variableRoot.getChildAt(i));

        while (!children.isEmpty()) {
            TreeNode temp = children.pop();
            variables.add(temp);
            for (int i = 0; i < temp.getChildCount(); i++)
                if(Model.isVariable(temp.getChildAt(i).getNodeData()))
                    children.push(temp.getChildAt(i));
        }
        return variables;
   }

   public LinkedList<TreeNode> getAllResources(){
        LinkedList<TreeNode> resources = new LinkedList<TreeNode>();
        
        if(resourceRoot == null || resourceRoot.getChildCount() == 0)
            return resources;
        
        Stack<TreeNode> children = new Stack<TreeNode>();
        for (int i = 0; i < resourceRoot.getChildCount(); i++) 
            if(Model.isResource(resourceRoot.getChildAt(i).getNodeData()))
                children.push(resourceRoot.getChildAt(i));

        while (!children.isEmpty()) {
            TreeNode temp = children.pop();
            resources.add(temp);
            for (int i = 0; i < temp.getChildCount(); i++)
                if(Model.isResource(temp.getChildAt(i).getNodeData()))
                    children.push(temp.getChildAt(i));
        }
        return resources;
   }
   
}
