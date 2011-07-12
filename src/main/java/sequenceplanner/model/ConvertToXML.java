package sequenceplanner.model;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;


import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.model.data.ViewData;

import sequenceplanner.xml.CellData;

import sequenceplanner.xml.Liason;
import sequenceplanner.xml.ObjectFactory;
import sequenceplanner.xml.Operation;
import sequenceplanner.xml.OperationData;

import sequenceplanner.xml.Rectangle;
import sequenceplanner.xml.Resource;
import sequenceplanner.xml.SequencePlannerProjectFile;
import sequenceplanner.xml.Variable;
import sequenceplanner.xml.ViewType;

import com.mxgraph.model.mxGeometry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sequenceplanner.condition.Condition;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeAlternative;
import sequenceplanner.model.SOP.SopNodeArbitrary;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.SopNodeParallel;
import sequenceplanner.xml.GlobalProperty;

/**
 *
 * @author Erik Ohlson
 */
public class ConvertToXML {

    //TODO : Thread this
    private Model mModel;

    public ConvertToXML(Model model) {
        this.mModel = model;
    }

    public SequencePlannerProjectFile convert() {
        ObjectFactory f = new ObjectFactory();
//      f.createSequencePlannerProjectFileLiasons();
        SequencePlannerProjectFile project = f.createSequencePlannerProjectFile();

        //ID counter
        project.setIdCounter(mModel.getCounter());

        //Liason
//      project.setLiasons(getLiasonRoot());

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
        TreeNode node = mModel.getOperationRoot();

        SequencePlannerProjectFile.Operations result = new SequencePlannerProjectFile.Operations();

        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            sequenceplanner.model.data.OperationData in = (sequenceplanner.model.data.OperationData) child.getNodeData();
            result.getOperation().add(getOperation(child));
        }

        TreeMap<Integer, ViewData> views = mModel.getOperationsWithViews();
        Set<Integer> keys = views.keySet();

        for (Integer i : keys) {
            ViewData d = views.get(i);

            if (d != null && mModel.isOperationPresent(i)) {
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

        final String pattern = getPatternForConditionsToExclude();
        
        if (!data.getGlobalConditions().isEmpty()) {
            final OperationData.PreConditionSet pcsPre = new OperationData.PreConditionSet();
            pcsPre.getCondition().addAll(getConditions(data, pattern, ConditionType.PRE));
            dataX.setPreConditionSet(pcsPre);
        }
        
        if (!data.getGlobalConditions().isEmpty()) {
            final OperationData.PostConditionSet pcsPost = new OperationData.PostConditionSet();
            pcsPost.getCondition().addAll(getConditions(data, pattern, ConditionType.POST));
            dataX.setPostConditionSet(pcsPost);
        }

        return dataX;
    }

    private String getPatternForConditionsToExclude() {
        //Find names for SOPs that shold be excluded from storare. Because stored in SOP/view
        String patternView = "";
        final TreeNode[] viewArray = mModel.getChildren(mModel.getViewRoot());
        for (final TreeNode tn : viewArray) {
            final String name = tn.getNodeData().getName();
            if (!patternView.equals("")) {
                patternView += "|";
            }
            patternView += name;
        }
        System.out.println(patternView);
        return patternView;
    }

    private Set<String> getConditions(final sequenceplanner.model.data.OperationData iOpData, final String iConditionPattern, final ConditionType iConditionType) {
        final Set<String> returnSet = new HashSet<String>();
        for(final String viewName : iOpData.getGlobalConditions().keySet()) {
            final Matcher matcher = Pattern.compile(iConditionPattern).matcher(viewName);

            if(!matcher.find() || iConditionPattern.equals("")) {
                if(iOpData.getGlobalConditions().get(viewName).containsKey(iConditionType)) {
                    returnSet.add(iOpData.getGlobalConditions().get(viewName).get(iConditionType).toString());
                }
            }
        }
        return returnSet;
    }

    private SequencePlannerProjectFile.Views getViewRoot() {
        TreeNode node = mModel.getViewRoot();

        SequencePlannerProjectFile.Views result = new SequencePlannerProjectFile.Views();

        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            if (Model.isView(child.getNodeData())) {
                result.getView().add(getView((ViewData) child.getNodeData()));
            }
        }

        return result;
    }

    /**
     *
     * @param node, has to be an liason root
     * @return
     */
    private ViewType getView(ViewData iViewData) {
        ViewType viewX = new ViewType();

        viewX.setName(iViewData.getName());

        viewX.setIsClosed(iViewData.isClosed());
        viewX.setIsHidden(iViewData.isHidden());


        final Map<ISopNode, ViewData.CellData> nodeCellMap = iViewData.mNodeCellDataMap;
        for (final ISopNode node : nodeCellMap.keySet()) {
            //Init---------------------------------------------------------------
            final ViewData.CellData cellData = nodeCellMap.get(node);
            final ViewData.CellDataLayout cellDataLayout = iViewData.mNodeCellDataLayoutMap.get(node);

            //Return variable
            final CellData dataX = new CellData();

            //Ref id-------------------------------------------------------------
            dataX.setRefId(cellData.mRefId);

            //Sequence set-------------------------------------------------------
            final CellData.SequenceSet ss = new CellData.SequenceSet();
            for (final ISopNode childNode : node.getFirstNodesInSequencesAsSet()) {
                ss.getChildId().add(nodeCellMap.get(childNode).mRefId);
            }
            dataX.setSequenceSet(ss);

            //Successor----------------------------------------------------------
            final ISopNode successorNode = node.getSuccessorNode();
            if (successorNode != null) {
                final int successorRefId = iViewData.mNodeCellDataMap.get(successorNode).mRefId;
                dataX.setSuccessor(successorRefId);
            }

            //Node type----------------------------------------------------------
            if (node instanceof SopNode) {
                dataX.setType(0);
            } else if (node instanceof SopNodeOperation) {
                final int operationId = node.getOperation().getId();
                dataX.setType(operationId);
            } else if (node instanceof SopNodeAlternative) {
                dataX.setType(-2);
            } else if (node instanceof SopNodeArbitrary) {
                dataX.setType(-3);
            } else if (node instanceof SopNodeParallel) {
                dataX.setType(-4);
            }

            if (cellDataLayout != null) {
                //Geometry-------------------------------------------------------

                dataX.setGeo(getGeo(cellDataLayout.mGeo));

                //Expanded-------------------------------------------------------
                dataX.setExpanded(cellDataLayout.mExpanded);

            }

            //Add to view--------------------------------------------------------
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
        TreeNode node = mModel.getLiasonRoot();
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


        if (Model.isLiason(node.getNodeData()) || node == mModel.getLiasonRoot()) {
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
        TreeNode node = mModel.getResourceRoot();
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

        if (Model.isResource(node.getNodeData()) || node == mModel.getResourceRoot()) {
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
