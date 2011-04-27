package sequenceplanner.model.SOP;

import com.mxgraph.model.mxCell;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
//import sequenceplanner.algorithms.visualization.RelateTwoOperations;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;
import sequenceplanner.view.operationView.graphextension.SPGraph;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

/**
 * Translates all nodes that are children to given {@link ISopNode} to {@link Cell}s in a {@link SPGraph}.<br/>
 * @author patrik
 */
public class DrawSopNode {

    private SPGraph mGraph = null;
    private ISopNode mRoot = null;
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();

    public DrawSopNode(final SPGraph mGraph) {
        this.mGraph = mGraph;
        drawExampleSequence();
    }

    public DrawSopNode(final ISopNode iRoot, final SPGraph mGraph) {
        this.mRoot = iRoot;
        this.mGraph = mGraph;
        addNodesToGraph(iRoot);
    }

    private void draw() {
    }

    private void addNodesToGraph(final ISopNode iRoot) {
        //Create Cells and a map between cells and nodes.
        final Map<ISopNode, Cell> nodeCellMap = new HashMap<ISopNode, Cell>();
        final Set<ISopNode> nodeSet = mSNToolbox.getNodes(iRoot, true);
        for (final ISopNode node : nodeSet) {
            nodeCellMap.put(node, getCellForNode(node));
        }

        for (ISopNode node : iRoot.getFirstNodesInSequencesAsSet()) {
            //Add node
            if (iRoot == mRoot) {
                mGraph.addCell(nodeCellMap.get(node));
            }
            //Children

            //Successor(s)
            while (node.getSuccessorNode() != null) {
                final Cell cellPred = nodeCellMap.get(node);
                final Cell cellSucc = nodeCellMap.get(node.getSuccessorNode());
                mGraph.insertNewCell(cellPred, cellSucc, false);
                node = node.getSuccessorNode();
            }
        }

        //Get Proper layout
        mGraph.recursiveAutoArrange((Cell) mGraph.getDefaultParent());
    }

    private Cell getCellForNode(final ISopNode iNode) {
        final Object nodeType = iNode.getNodeType();
        final boolean sequenceSetIsEmpty = iNode.getFirstNodesInSequencesAsSet().isEmpty();
        Cell cell;
        if (nodeType instanceof OperationData) {
            final OperationData opData = (OperationData) nodeType;
            if (sequenceSetIsEmpty) {
                //Operation
                cell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
            } else {
                //SOP Operation
                cell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_SOP);
            }
            cell.setValue(opData);
            return cell;
        } else if (nodeType instanceof String) {
            final String nodeTypeString = (String) nodeType;
            final String sop = "SOP";
            final String alternative = "+"; //RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", "");
            final String arbitraryOrder = "(+)"; //RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER, "", "");
            final String parallel = "||"; //RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL, "", "");

            if (nodeTypeString.equals(sop)) {
                return CellFactory.getInstance().getOperation(SPGraphModel.TYPE_SOP);
            } else if (nodeTypeString.equals(alternative)) {
                return CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ALTERNATIVE);
            } else if (nodeTypeString.equals(arbitraryOrder)) {
                return CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ARBITRARY);
            } else if (nodeTypeString.equals(parallel)) {
                return CellFactory.getInstance().getOperation(SPGraphModel.TYPE_PARALLEL);
            }
        }
        return null;
    }

    private void drawExampleSequence() {

//   final public static String TYPE_OPERATION = "operation";
//   final public static String TYPE_SOP = "sop";
//   final public static String TYPE_PARALLEL = "parallel";
//   final public static String TYPE_ALTERNATIVE = "alternative";
//   final public static String TYPE_ARBITRARY = "arbitrary";

        Cell cell1, cell2, cell3, cell4, cell5, cell6, cell7;
        Cell cell8;

        cell1 = CellFactory.getInstance().getOperation("operation");
        cell2 = CellFactory.getInstance().getOperation("parallel");
        cell3 = CellFactory.getInstance().getOperation("alternative");
        cell4 = CellFactory.getInstance().getOperation("operation");
        cell5 = CellFactory.getInstance().getOperation("operation");
        cell6 = CellFactory.getInstance().getOperation("sop");
        cell7 = CellFactory.getInstance().getOperation("operation");

        cell8 = CellFactory.getInstance().getOperation("operation");

        cell1.getGeometry().setX(10);
        cell1.getGeometry().setY(50);
        Data d = (Data) cell1.getValue();
        d.setName("hej");
        cell1.setValue(d);
        mGraph.addCell(cell1);

        mGraph.insertNewCell(cell1, cell2, false);

        mGraph.insertGroupNode(cell2, null, cell3);
        mGraph.insertGroupNode(cell3, null, cell4);
        mGraph.insertGroupNode(cell3, null, cell5);

        mGraph.insertGroupNode(cell2, null, cell6);

        mGraph.insertGroupNode(cell6, null, cell7);
        Object[] oSet = mGraph.getEdges(cell7);
        for (Object o : oSet) {
            mxCell c = (mxCell) o;
            c.removeFromParent();
        }

        mGraph.addCell(cell8);

        mGraph.recursiveAutoArrange((Cell) mGraph.getDefaultParent());
    }
}
