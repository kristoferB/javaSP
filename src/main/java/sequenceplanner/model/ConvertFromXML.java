package sequenceplanner.model;

import java.util.LinkedList;
import java.util.List;

import sequenceplanner.model.data.LiasonData;
import sequenceplanner.model.data.OperationData;

import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.xml.Properties;
import sequenceplanner.xml.Bookings;
import sequenceplanner.xml.Bookings.ResourceBooking;
import sequenceplanner.xml.CellData;
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
import java.util.Map;
import sequenceplanner.datamodel.condition.parser.AStringToConditionParser;
import sequenceplanner.datamodel.condition.parser.ActionAsTextInputToConditionParser;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.parser.GuardAsTextInputToConditionParser;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeAlternative;
import sequenceplanner.model.SOP.SopNodeArbitrary;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.SopNodeParallel;
import sequenceplanner.model.data.ConditionData;

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

        model.setCounter(project.getIdCounter());

//      setLiasonRoot(project.getLiasons());

        setResourceRoot(project.getResources());

        getOperationData(project.getOperations());

        setViewRoot(project.getViews()); //Has to be after gerOperationData

        //Recreate operations and operationViews

//        setOperationRoot(project.getOperations());


        return this.model;
    }

    private void getOperationData(SequencePlannerProjectFile.Operations inputX) {
        for (Operation opX : inputX.getOperation()) {
            final OperationData opData = new OperationData(opX.getName(), opX.getId());
            model.createModelOperationNode(opData);

            if (opX.getOperationData().getDescription() != null) {
                opData.setDescription(opX.getOperationData().getDescription());
            }

            if (opX.getOperationData().getPreConditionSet() != null) {
                if (opX.getOperationData().getPreConditionSet().getCondition() != null) {
                    getConditions(opData, opX.getOperationData().getPreConditionSet().getCondition(), ConditionType.PRE);
                }
            }
            if (opX.getOperationData().getPostConditionSet() != null) {
                if (opX.getOperationData().getPostConditionSet().getCondition() != null) {
                    getConditions(opData, opX.getOperationData().getPostConditionSet().getCondition(), ConditionType.POST);
                }
            }
        }
    }

    private OperationData getConditions(OperationData data, List<sequenceplanner.xml.Condition> iConditionList, final ConditionType iConditionType) {
        
        for (final sequenceplanner.xml.Condition condition : iConditionList) {
            final String key = condition.getCondKey();
            final String value = condition.getCondValue();

            final Map<ConditionType, Condition> map = new HashMap<ConditionType, Condition>();
            map.put(iConditionType, conditionFromString(value));
            data.setConditions(new ConditionData(key), map);
        }

        return data;
    }

    private Condition conditionFromString(String savedCondition) {
//        System.out.println("preparse " + savedCondition);
//        String formatstring = StringTrimmer.getInstance().stringTrim(savedCondition);
//        System.out.println("postparse " + formatstring);
        final String[] conditionSplit = savedCondition.split("/");
        final Condition condition = new Condition();

        final AStringToConditionParser guardParser = new GuardAsTextInputToConditionParser();
        final ConditionExpression gurad = new ConditionExpression();

        
        final AStringToConditionParser actionParser = new ActionAsTextInputToConditionParser();
        final ConditionExpression action = new ConditionExpression();

        if (guardParser.run(conditionSplit[0], gurad)) {
            condition.setGuard(gurad);
        }
        if (conditionSplit.length >1 && actionParser.run(conditionSplit[1], action)) {
            condition.setAction(action);
        }
        return condition;
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
        final ViewData viewData = new ViewData(viX.getName(), Model.newId());

        viewData.setClosed(viX.isIsClosed());
        viewData.setHidden(viX.isIsHidden());

        for (CellData cellData : viX.getCellData()) {
            final ISopNode sopNode = getSopNode(cellData);
            //Data related to relations
            viewData.mNodeCellDataMap.put(sopNode, getCellDataRelations(cellData));

            //Data related to layout
            viewData.mNodeCellDataLayoutMap.put(sopNode, getCellDataLayout(cellData));
        }

        return viewData;
    }

    private ISopNode getSopNode(final CellData iCdX) {
        final int type = iCdX.getType();
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
                System.out.println("Could not import: " + iCdX.toString());
            }
        }

        return newSopNode;
    }

    private ViewData.CellData getCellDataRelations(CellData cdX) {

        //Get Sequence set-------------------------------------------------------
        final List<Integer> sequenceSet = cdX.getSequenceSet().getChildId();
        //-----------------------------------------------------------------------

        //Get Successor----------------------------------------------------------
        final Integer successor = cdX.getSuccessor();
        //-----------------------------------------------------------------------

        //Add as CellData object in ViewData-------------------------------------
        final ViewData.CellData newCellData = new ViewData.CellData(sequenceSet, successor, cdX.getRefId());

        return newCellData;
    }

    private ViewData.CellDataLayout getCellDataLayout(CellData cdX) {

        //Get geometry info------------------------------------------------------
        final mxGeometry meo = getGeo(cdX.getGeo());
        //-----------------------------------------------------------------------

        //Add as CellData object in ViewData-------------------------------------
        final ViewData.CellDataLayout newCellData = new ViewData.CellDataLayout(meo, cdX.isExpanded());

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
