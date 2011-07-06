package sequenceplanner.model;

import java.util.LinkedList;
import java.util.List;

import sequenceplanner.model.data.LiasonData;
import sequenceplanner.model.data.OperationData;

import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.xml.Actions;
import sequenceplanner.xml.Properties;
import sequenceplanner.xml.Bookings;
import sequenceplanner.xml.Bookings.ResourceBooking;
import sequenceplanner.xml.CellData;
import sequenceplanner.xml.Conditions;
import sequenceplanner.xml.Liason;
import sequenceplanner.xml.Operation;
import sequenceplanner.xml.Rectangle;
import sequenceplanner.xml.Resource;
import sequenceplanner.xml.SequencePlannerProjectFile;
import sequenceplanner.xml.Variable;
import sequenceplanner.xml.ViewType;


import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxRectangle;
import java.util.HashMap;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeAlternative;
import sequenceplanner.model.SOP.SopNodeArbitrary;
import sequenceplanner.model.SOP.SopNodeParallel;

/**
 *
 * @author Erik Ohlson
 */
public class ConvertFromXML {

    private Model model;

    public ConvertFromXML(Model model) {
        this.model = model;
    }

    public Model convert(SequencePlannerProjectFile project) {
        model.clearModel();

        model.setCounter(project.getIdCounter());

//      setLiasonRoot(project.getLiasons());

//      setResourceRoot(project.getResources());

      setViewRoot(project.getViews());

        //Recreate operations and operationViews
        getOperationData(project.getOperations());
//        setOperationRoot(project.getOperations());


        return this.model;
    }

    private void setOperationRoot(SequencePlannerProjectFile.Operations inputX) {
        for (Operation opX : inputX.getOperation()) {
            model.createModelOperationNode(opX.getName(), opX.getId());
//         model.getOperationRoot().insert(getOperations(opX));
        }

//      List<ViewData> d = new LinkedList<ViewData>();
//      for (ViewType viewType : inputX.getOperationViews()) {
//         d.add(getView(viewType));
//      }
//      model.saveOperationViews(d.toArray(new ViewData[0]));
    }

    private TreeNode getOperations(Operation opX) {
        TreeNode out = new TreeNode(getOperationData(opX));

        for (Operation childX : opX.getOperation()) {
            out.insert(getOperations(childX));
        }

        return out;
    }

    private void getOperationData(SequencePlannerProjectFile.Operations inputX) {
        for (Operation opX : inputX.getOperation()) {
            final TreeNode tn = model.createModelOperationNode(opX.getName(), opX.getId());
            final OperationData opData = (OperationData) tn.getNodeData();

            if (opX.getOperationData().getDescription() != null) {
                opData.setDescription(opX.getOperationData().getDescription());
            }
        }
    }

    private OperationData getOperationData(Operation dataX) {


        OperationData data = new OperationData(dataX.getName(), dataX.getId());

        data.setDescription(dataX.getOperationData().getDescription());
//      data.setCost(dataX.getOperationData().getCost());
//      data.setPreoperation(dataX.getOperationData().isIsPreoperation());
//      data.setPostoperation(dataX.getOperationData().isIsPostoperation());
//      data.setAccomplishes(dataX.getOperationData().getAccomplishes());
//      data.setRealizedBy(dataX.getOperationData().getRealizedBy());

        //Pre
        if (dataX.getOperationData().getPreSequenceCondtions() != null) {
//         data.setSequenceCondition(getCondition(dataX.getOperationData().getPreSequenceCondtions()));
        }
        if (dataX.getOperationData().getPreActions() != null) {
//         data.setActions(getAction(dataX.getOperationData().getPreActions()));
        }
        if (dataX.getOperationData().getPreResurceBooking() != null) {
//         data.setResourceBooking(getBooking(dataX.getOperationData().getPreResurceBooking()));
        }

        // Invariant
        if (dataX.getOperationData().getSequenceInvariants() != null) {
//         data.setSeqInvariant(getCondition(dataX.getOperationData().getSequenceInvariants()));
        }

        //Properties
        if (dataX.getOperationData().getProperties() != null) {
//         data.setProperties(getProperties(dataX.getOperationData().getProperties()));
        }

        //Post
        if (dataX.getOperationData().getPostSequenceCondtions() != null) {
//         data.setPSequenceCondition(getCondition(dataX.getOperationData().getPostSequenceCondtions()));
        }
        if (dataX.getOperationData().getPostResurceBooking() != null) {
//         data.setPResourceBooking(getBooking(dataX.getOperationData().getPostResurceBooking()));
        }

        return data;
    }

    private LinkedList<Integer[]> getBooking(Bookings bookX) {
        LinkedList<Integer[]> output = new LinkedList<Integer[]>();

        for (ResourceBooking in : bookX.getResourceBooking()) {
            output.add(new Integer[]{in.getResource(), in.getType()});
        }

        return output;
    }

    private HashMap<Integer, Boolean> getProperties(Properties propX) {
        HashMap<Integer, Boolean> output = new HashMap<Integer, Boolean>();

        for (Properties.Property p : propX.getProperty()) {
            output.put(p.getId(), p.isValue());
        }
        return output;
    }

    private void setViewRoot(SequencePlannerProjectFile.Views inputX) {
        for (ViewType viX : inputX.getView()) {
            model.getViewRoot().insert(new TreeNode(getView(viX)));
        }
    }

    private ViewData getView(ViewType viX) {
        ViewData data = new ViewData(viX.getName(), -1);
        data.setRoot(viX.getRoot());

        for (CellData cellData : viX.getCellData()) {
            data.getData().add(getCellData(cellData));
        }




        return data;
    }

    private ViewData.CellData getCellData(CellData cdX) {

        //Create SopNode
        final int type = cdX.getType();
        ISopNode node = null;
        
        if(type == 0) {
            node = new SopNode();
        } else if(type == -2) {
            node = new SopNodeAlternative();
        } else if(type == -3) {
            node = new SopNodeArbitrary();
        } else if(type == -4) {
            node = new SopNodeParallel();
        } 


        mxGeometry meo = getGeo(cdX.getGeo());

        ViewData.CellData cd = null; //new ViewData.CellData(cdX.getRefId(), cdX.getPreviousCell(), cdX.getType(), cdX.getRelation(), cdX.isLastInRelation(), meo, cdX.isExpanded());


        return cd;
    }

    private mxGeometry getGeo(CellData.Geo geoX) {
        Rectangle reX = geoX.getGeometry();
        mxGeometry geo = new mxGeometry(reX.getX(), reX.getY(), reX.getW(), reX.getH());

        reX = geoX.getAlternateGeometry();
        if (reX != null) {
            geo.setAlternateBounds(new mxRectangle(reX.getX(), reX.getY(), reX.getW(), reX.getH()));
        }

        return geo;
    }

    private void setLiasonRoot(SequencePlannerProjectFile.Liasons inputX) {

        for (Liason liX : inputX.getLiason()) {
            model.getLiasonRoot().insert(getLiason(liX));
        }
    }

    private TreeNode getLiason(Liason liX) {
        LiasonData data = new LiasonData(liX.getName(), liX.getId());
        TreeNode li = new TreeNode(data);

        for (Liason childX : liX.getLiason()) {
            li.insert(getLiason(childX));
        }

        return li;
    }

    private void setResourceRoot(SequencePlannerProjectFile.Resources inputX) {

        for (Resource resX : inputX.getResource()) {
            model.getResourceRoot().insert(getResource(resX));
        }
    }

    private TreeNode getResource(Resource resX) {
        ResourceData data = new ResourceData(resX.getName(), resX.getId());
        TreeNode res = new TreeNode(data);
        if (model.getVariableRoot() == null && resX.getName().equals(Model.VARIABLE_ROOT_NAME)) {
            model.setVariableRoot(res);
        } else if (resX.getName().equals(Model.VARIABLE_ROOT_NAME)) {
            resX.setName('_' + resX.getName());
        }

        for (Variable varX : resX.getVariable()) {
            ResourceVariableData var = new ResourceVariableData(varX.getName(), varX.getId());
            var.setData(0, varX.getMinValue(), varX.getMaxValue(), varX.getIntialValue()); //0 for type int /PM 101126
            res.insert(new TreeNode(var));

        }

        for (Resource childX : resX.getResource()) {
            res.insert(getResource(childX));

        }

        return res;
    }
}
