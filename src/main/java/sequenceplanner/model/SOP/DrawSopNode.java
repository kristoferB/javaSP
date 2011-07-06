package sequenceplanner.model.SOP;

import com.mxgraph.model.mxGeometry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.Constants;
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

    /**
     * Dummy constructor just for test
     * @param mGraph
     */
    public DrawSopNode(final SPGraph mGraph) {
        this.mGraph = mGraph;
        drawExampleSequence();
    }

    public DrawSopNode(final ISopNode iRoot, final SPGraph mGraph) {
        this.mRoot = iRoot;
        this.mGraph = mGraph;
        addNodesToGraph(iRoot);
    }

    /**
     * Adds a {@link ISopNode}s in parameter iRoot to {@link SPGraph}.<br/>
     * @param iRoot container for nodes
     */
    private void addNodesToGraph(final ISopNode iRoot) {
        //Create Cells and a map between cells and nodes.
        final Map<ISopNode, Cell> nodeCellMap = new HashMap<ISopNode, Cell>();
        final Set<ISopNode> nodeSet = mSNToolbox.getNodes(iRoot, true);
        for (final ISopNode node : nodeSet) {
            nodeCellMap.put(node, getCellForNode(node));
        }

        recursiveCallToAllNodes(iRoot, nodeCellMap);

//        //Get Proper layout
//        mGraph.recursiveAutoArrange((Cell) mGraph.getDefaultParent());
    }

    /**
     * Doing the addition of nodes to the graph.<br/>
     * There are some different methods to add a Cell/Node dependent on where in the graph the cell should be added.<br/>
     * @param iRoot
     * @param iNodeCellMap What type of {@link Cell} each {@link ISopNode} is
     */
    private void recursiveCallToAllNodes(final ISopNode iRoot, final Map<ISopNode, Cell> iNodeCellMap) {
        for (ISopNode node : iRoot.getFirstNodesInSequencesAsSet()) {
            //First node---------------------------------------------------------
            //Especially first nodes are strange...
            if (iRoot == mRoot) {
                mGraph.addCell(iNodeCellMap.get(node));
            } else {
                mGraph.insertGroupNode(iNodeCellMap.get(iRoot), null, iNodeCellMap.get(node));
                if (iNodeCellMap.get(iRoot).getType() == Constants.SOP) {
                    Object[] oSet = mGraph.getEdges(iNodeCellMap.get(node));
                    for (Object o : oSet) {
                        Cell c = (Cell) o;
                        c.removeFromParent();
                    }
                }
            }//------------------------------------------------------------------

            //Children to first node---------------------------------------------
            recursiveCallToAllNodes(node, iNodeCellMap);
            //-------------------------------------------------------------------

            //Successor(s)-------------------------------------------------------
            while (node.getSuccessorNode() != null) {
                final ISopNode successorNode = node.getSuccessorNode();
                drawSequenceWithRespectToRelation(node, successorNode, iNodeCellMap);
                //Children to successor node
                recursiveCallToAllNodes(successorNode, iNodeCellMap);
                //Update for next round
                node = successorNode;
            }//------------------------------------------------------------------
        }
    }

    /**
     * RETHINK HOW THIS SHOULD BE!!!!!
     * Adds two cells in sequence with respect to their successor relation.
     * @param iCellPred predecessor node as {@link Cell}
     * @param iCellSucc
     * @param iSuccessorNode
     * @param iRelation
     * @param iNodeCellMap
     */
    private void drawSequenceWithRespectToRelation(final ISopNode iPredNode, final ISopNode iSuccNode, final Map<ISopNode, Cell> iNodeCellMap) {
        final Cell cellPred = iNodeCellMap.get(iPredNode);
        final Cell cellSucc = iNodeCellMap.get(iSuccNode);

        mGraph.insertNewCell(cellPred, cellSucc, false);


//        if (predSuccRelation == IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12) {
//            mGraph.insertNewCell(cellPred, cellSucc, false);
//            return;
//        } else if (predSuccRelation == IRelateTwoOperations.SOMETIMES_IN_SEQUENCE_12) {
//            //Create alternative as successor
//            final Cell alternativeCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ALTERNATIVE);
//            mGraph.insertNewCell(cellPred, alternativeCell, false);
//
//            //Empty right branch
//            final Cell emptyCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
//            final Data newOpData = new OperationData("", -1);
//            Model.giveId(newOpData);
//            emptyCell.setValue(newOpData);
//            mGraph.insertGroupNode(alternativeCell, null, emptyCell);
//
//            //iCellSucc
//            mGraph.insertGroupNode(alternativeCell, null, cellSucc);
//
//            //Update node cell map to get right mapping for later
//            iNodeCellMap.put(iSuccNode, alternativeCell);
//            return;
//        }
    }

    /**
     * Get right {@link Cell} for each {@link ISopNode} type.<br/>
     * @param iNode to translate
     * @return corresponding Cell
     */
    private Cell getCellForNode(final ISopNode iNode) {

        Cell cell;
        if (iNode instanceof SopNodeOperation) {
            final OperationData opData = iNode.getOperation();
            if (iNode.sequenceSetIsEmpty()) {
                //Operation
                cell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
            } else {
                //SOP Operation
                cell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_SOP);
                cell.setCollapsed(true);
            }
            final Data newOpData = new OperationData(opData.getName(), -1);
            Model.giveId(newOpData);
            cell.setValue(newOpData);
            cell.setGeometry(new mxGeometry(81.35, 81.35, opData.getId(), opData.getId()));
            return cell;
        } else if (iNode instanceof SopNode) {
            cell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_SOP);
            final Data newOpData = new OperationData("", -1);
            Model.giveId(newOpData);
            cell.setValue(newOpData);
            return cell;
        } else if (iNode instanceof SopNodeAlternative) {
            return CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ALTERNATIVE);
        } else if (iNode instanceof SopNodeArbitrary) {
            return CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ARBITRARY);
        } else if (iNode instanceof SopNodeParallel) {
            return CellFactory.getInstance().getOperation(SPGraphModel.TYPE_PARALLEL);
        }
        
        return null;
    }

    /**
     * Dummy method, just to see how to work with {@link SPGraph}.<br/>
     */
    private void drawExampleSequence() {

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
            Cell c = (Cell) o;
            c.removeFromParent();
        }

//        mGraph.addCell(cell8);
        mGraph.insertNewCell(cell4, cell8, false);

        mGraph.recursiveAutoArrange((Cell) mGraph.getDefaultParent());
    }
}
