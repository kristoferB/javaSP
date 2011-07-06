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
import sequenceplanner.model.SOP.SopNodeOperation;
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

        getOperationData(project.getOperations());

        setViewRoot(project.getViews()); //Has to be after gerOperationData

        //Recreate operations and operationViews

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
        final ViewData viewData = new ViewData(viX.getName(), -1);
        viewData.setRoot(viX.getRoot());

        for (CellData cellData : viX.getCellData()) {
            viewData.mCellDataSet.add(getCellData(cellData));
        }

        return viewData;
    }

    private ViewData.CellData2 getCellData(CellData cdX) {

        //Create SopNode---------------------------------------------------------
        final int type = cdX.getType();
        ISopNode newSopNode = null;

        if (type == 0) {
            newSopNode = new SopNode();
        } else if (type == -2) {
            newSopNode = new SopNodeAlternative();
        } else if (type == -3) {
            newSopNode = new SopNodeArbitrary();
        } else if (type == -4) {
            newSopNode = new SopNodeParallel();
        } else {
            final TreeNode tn = model.getOperation(type);
            if (tn != null) {
                if (Model.isOperation(tn.getNodeData())) {
                    final OperationData opData = (OperationData) tn.getNodeData();
                    newSopNode = new SopNodeOperation(opData);
                }
            } else {
                System.out.println("Could not import: " + cdX.toString());
            }
        }//----------------------------------------------------------------------

        //Get Sequence set-------------------------------------------------------
        final List<Integer> sequenceSet = cdX.getSequenceSet().getChildId();
        //-----------------------------------------------------------------------

        //Get Successor----------------------------------------------------------
        final Integer successor = cdX.getSuccessor();
        //-----------------------------------------------------------------------


        //Get geometry info------------------------------------------------------
        final mxGeometry meo = getGeo(cdX.getGeo());
        //-----------------------------------------------------------------------

        //Add as CellData object in ViewData-------------------------------------
        final ViewData.CellData2 newCellData = new ViewData.CellData2(newSopNode, sequenceSet, successor, cdX.getRefId(), meo, cdX.isExpanded());
        return newCellData;
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
