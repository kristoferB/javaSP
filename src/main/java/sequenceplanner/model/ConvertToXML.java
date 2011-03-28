package sequenceplanner.model;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import sequenceplanner.model.data.OperationData.Action;
import sequenceplanner.model.data.OperationData.SeqCond;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.xml.Actions;
import sequenceplanner.xml.Bookings;
import sequenceplanner.xml.CellData;
import sequenceplanner.xml.Conditions;
import sequenceplanner.xml.Liason;
import sequenceplanner.xml.ObjectFactory;
import sequenceplanner.xml.Operation;
import sequenceplanner.xml.OperationData;
import sequenceplanner.xml.Properties;
import sequenceplanner.xml.Rectangle;
import sequenceplanner.xml.Resource;
import sequenceplanner.xml.SequencePlannerProjectFile;
import sequenceplanner.xml.Variable;
import sequenceplanner.xml.ViewType;

import com.mxgraph.model.mxGeometry;
import java.util.HashMap;

/**
 *
 * @author Erik Ohlson
 */
public class ConvertToXML {

   //TODO : Thread this
   private Model model;

   public ConvertToXML(Model model) {
      this.model = model;
   }

   public SequencePlannerProjectFile convert() {
      ObjectFactory f = new ObjectFactory();
      f.createSequencePlannerProjectFileLiasons();
      SequencePlannerProjectFile project = f.createSequencePlannerProjectFile();

      //ID counter
      project.setIdCounter(model.getCounter());

      //Liason
      project.setLiasons(getLiasonRoot());

      //Resource
      project.setResources(getResourceRoot());

      //Views
      project.setViews(getViewRoot());

      //Operations
      project.setOperations(getOperationRoot());

      //Global properties
//      project.setGlobalProperties(getGlobalProperties());


      return project;
   }

   private SequencePlannerProjectFile.Operations getOperationRoot() {
      TreeNode node = model.getOperationRoot();

      SequencePlannerProjectFile.Operations result = new SequencePlannerProjectFile.Operations();

      for (int i = 0; i < node.getChildCount(); i++) {
         TreeNode child = node.getChildAt(i);
         sequenceplanner.model.data.OperationData in = (sequenceplanner.model.data.OperationData) child.getNodeData();
         result.getOperation().add(getOperation(child));
      }

      TreeMap<Integer, ViewData> views = model.getOperationsWithViews();
      Set<Integer> keys = views.keySet();

      for (Integer i : keys) {
         ViewData d = views.get(i);

         if (d != null && model.isOperationPresent(i)) {
            result.getOperationViews().add(getView(d));
         }
      }


      return result;
   }

   private Operation getOperation(TreeNode node) {
      Operation op = new Operation();

      if (Model.isOperation(node.getNodeData())) {
         op.setId(node.getId());
         op.setName(node.getNodeData().getName());

         sequenceplanner.model.data.OperationData in = (sequenceplanner.model.data.OperationData) node.getNodeData();
         op.setOperationData(getOperationData(in));

         for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            op.getOperation().add(getOperation(child));

         }
         return op;
      }
      return null;
   }

   private OperationData getOperationData(sequenceplanner.model.data.OperationData data) {

      OperationData dataX = new OperationData();

      if (!data.getDescription().isEmpty()) {
         dataX.setDescription(data.getDescription());
      }
      
      dataX.setCost(data.getCost());
      dataX.setIsPostoperation(data.isPostoperation());
      dataX.setIsPreoperation(data.isPreoperation());
      dataX.setAccomplishes(data.getAccomplishes());
      dataX.setRealizedBy(data.getRealizedBy());

      //Preconditions
      if (!data.getSequenceCondition().isEmpty()) {
         dataX.setPreSequenceCondtions(getConditions(data.getSequenceCondition()));
      }
      if (!data.getResourceBooking().isEmpty()) {
         dataX.setPreResurceBooking(getBookings(data.getResourceBooking()));
      }

      if (!data.getActions().isEmpty()) {
         dataX.setPreActions(getActions(data.getActions()));
      }

      //Invariant
      if (!data.getSeqInvariant().isEmpty()) {
         dataX.setSequenceInvariants(getConditions(data.getSeqInvariant()));
      }

      //Properties
      if (!data.getProperties().isEmpty()){
        dataX.setProperties(getProperties(data.getProperties()));
      }

      //PostConditions
      if (!data.getPSequenceCondition().isEmpty()) {
         dataX.setPostSequenceCondtions(getConditions(data.getPSequenceCondition()));
      }
      if (!data.getPResourceBooking().isEmpty()) {
         dataX.setPostResurceBooking(getBookings(data.getPResourceBooking()));
      }

      return dataX;
   }
 /*
   private SequencePlannerProjectFile.GlobalProperties getGlobalProperties() {
       SequencePlannerProjectFile.GlobalProperties dataX = new SequencePlannerProjectFile.GlobalProperties();
       model.getglo

       return
   }
*/
   private Conditions getConditions(LinkedList<LinkedList<SeqCond>> data) {
      Conditions dataX = new Conditions();

      for (LinkedList<SeqCond> one : data) {
         Conditions.Or inOr = new Conditions.Or();


         for (SeqCond seqCond : one) {
            Conditions.Or.SequenceCondition sc = new Conditions.Or.SequenceCondition();
            sc.setOperation(seqCond.id);
            sc.setStatus(seqCond.state);
            inOr.getSequenceCondition().add(sc);
         }

         dataX.getOr().add(inOr);
      }
      return dataX;
   }

   private Properties getProperties(HashMap<Integer, Boolean> data){

      Properties dataX = new Properties();

      for(Integer id : data.keySet()){
        Properties.Property p = new Properties.Property();
        p.setId(id);
        p.setValue(data.get(id));
        dataX.getProperty().add(p);
      }

      return dataX;
   }

   private Bookings getBookings(LinkedList<Integer[]> data) {
      Bookings dataX = new Bookings();

      for (Integer[] in : data) {
         Bookings.ResourceBooking b = new Bookings.ResourceBooking();
         b.setResource(in[0]);
         b.setType(in[1]);
         dataX.getResourceBooking().add(b);
      }

      return dataX;
   }

   private Actions getActions(LinkedList<Action> data) {
      Actions dataX = new Actions();

      for (Action action : data) {
         Actions.Action a = new Actions.Action();
         a.setVariable(action.id);
         a.setValue(action.value);
         a.setType(action.state);
         dataX.getAction().add(a);
      }
      return dataX;
   }

   private SequencePlannerProjectFile.Views getViewRoot() {
      TreeNode node = model.getViewRoot();

      SequencePlannerProjectFile.Views result = new SequencePlannerProjectFile.Views();

      for (int i = 0; i < node.getChildCount(); i++) {
         TreeNode child = node.getChildAt(i);
         if (model.isView(child.getNodeData())) {
            result.getView().add(getView((ViewData)child.getNodeData()));
         }
      }

      return result;
   }

   /**
    *
    * @param node, has to be an liason root
    * @return
    */
   private ViewType getView(ViewData view) {
      ViewType viewX = new ViewType();

      
         
         viewX.setName(view.getName());
         viewX.setRoot(view.getRoot());

         LinkedList<ViewData.CellData> list = view.getData();

         for (ViewData.CellData data : list) {
            CellData dataX = new CellData();
            dataX.setRefId(data.id);
            dataX.setPreviousCell(data.previousCell);
            dataX.setRelation(data.relation);
            dataX.setType(data.type);
            dataX.setLastInRelation(data.lastInRelation);
            dataX.setGeo(getGeo(data.geo));
            dataX.setExpanded(data.expanded);
            viewX.getCellData().add(dataX);
         }

      
      return viewX;
   }

   private CellData.Geo getGeo(mxGeometry geo) {
      CellData.Geo geoX = new CellData.Geo();

      Rectangle prim = new Rectangle();
      Rectangle alt = new Rectangle();

      prim.setX(geo.getX());
      prim.setY(geo.getY());
      prim.setW(geo.getWidth());
      prim.setH(geo.getHeight());

      if (geo.getAlternateBounds() != null) {
         alt.setX(geo.getAlternateBounds().getX());
         alt.setY(geo.getAlternateBounds().getY());
         alt.setW(geo.getAlternateBounds().getWidth());
         alt.setH(geo.getAlternateBounds().getHeight());

         geoX.setAlternateGeometry(alt);
      }


      geoX.setGeometry(prim);


      return geoX;
   }

   private SequencePlannerProjectFile.Liasons getLiasonRoot() {
      TreeNode node = model.getLiasonRoot();
      Liason li = getLiason(node);

      SequencePlannerProjectFile.Liasons result = new SequencePlannerProjectFile.Liasons();
      result.getLiason().addAll(li.getLiason());

      return result;
   }

   /**
    *
    * @param node, has to be an liason root
    * @return
    */
   private Liason getLiason(TreeNode node) {
      Liason li = new Liason();


      if (Model.isLiason(node.getNodeData()) || node == model.getLiasonRoot()) {
         li.setId(node.getId());
         li.setName(node.getNodeData().getName());

         for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            li.getLiason().add(getLiason(child));

         }
         return li;
      }
      return null;
   }

   private SequencePlannerProjectFile.Resources getResourceRoot() {
      TreeNode node = model.getResourceRoot();
      Resource res = getResource(node);

      SequencePlannerProjectFile.Resources result = new SequencePlannerProjectFile.Resources();
      result.getResource().addAll(res.getResource());

      return result;
   }

   /**
    *
    * @param node, has to be an liason root
    * @return
    */
   private Resource getResource(TreeNode node) {
      Resource res = new Resource();

      if (Model.isResource(node.getNodeData()) || node == model.getResourceRoot()) {
         res.setId(node.getId());
         res.setName(node.getNodeData().getName());

         for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);

            if (Model.isVariable(child.getNodeData())) {
               ResourceVariableData var = (ResourceVariableData) child.getNodeData();

               Variable varX = new Variable();

               varX.setId(child.getId());
               varX.setName(child.getNodeData().getName());
               varX.setIntialValue(var.getInitialValue());
               varX.setMaxValue(var.getMax());
               varX.setMinValue(var.getMin());
               res.getVariable().add(varX);
            } else if (Model.isResource(child.getNodeData())) {

               res.getResource().add(getResource(child));
            }

         }
         return res;
      }
      return null;
   }
}