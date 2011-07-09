package sequenceplanner.model.SOP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.model.data.ViewData.CellDataLayout;
import sequenceplanner.model.data.ViewData.CellData;
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
    private Map<ISopNode, Cell> mSopNodeCellMap = null;
    private Map<ISopNode, CellDataLayout> mNodeCellDataLayoutMap;
//    private Map<ISopNode, CellData> mNodeCellDataMap = null;
//    private Set<CellData> mCellDataSet = null;
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();
    private boolean doAutoLayout = true;

    /**
     * Dummy constructor just for test
     * @param mGraph
     */
    public DrawSopNode(final SPGraph mGraph) {
        this.mGraph = mGraph;
        drawExampleSequence();
    }

    public DrawSopNode(final ISopNode iRoot, final SPGraph iGraph) {
        this(iRoot, iGraph, null);
    }

    public DrawSopNode(final ISopNode iRoot, final SPGraph mGraph, final Set<CellData> iCellDataSet) {
        this.mRoot = iRoot;
        this.mGraph = mGraph;
//        this.mCellDataSet = iCellDataSet;
        addNodesToGraph();
    }

    public DrawSopNode(final ISopNode iRoot, final SPGraph iGraph, final Map<ISopNode, CellDataLayout> iCellData3Map, boolean b) {
        this.mRoot = iRoot;
        this.mGraph = iGraph;
        this.mNodeCellDataLayoutMap = iCellData3Map;
        addNodesToGraph();
    }

    /**
     * Adds a {@link ISopNode}s in parameter iRoot to {@link SPGraph}.<br/>
     * @param iRoot container for nodes
     */
    private void addNodesToGraph() {

//        init();

        //Create Cells and a map between cells and nodes.
        mSopNodeCellMap = new HashMap<ISopNode, Cell>();
        final Set<ISopNode> nodeSet = mSNToolbox.getNodes(mRoot, true);
        for (final ISopNode node : nodeSet) {
            mSopNodeCellMap.put(node, getCellForNode(node));
        }

        recursiveCallToAllNodes(mRoot);

        //Get Proper layout
        if (doAutoLayout) {
            mGraph.recursiveAutoArrange((Cell) mGraph.getDefaultParent());
        }
    }

    /**
     * Fill map, have had problems with pointers otherwise...
     */
//    private void init() {
//        if (mCellDataSet != null) {
//            if (mNodeCellDataMap == null) {
//                mNodeCellDataMap = new HashMap<ISopNode, CellData>();
//            } else {
//                mNodeCellDataMap.clear();
//            }
//            for (final ViewData.CellData cellData : mCellDataSet) {
//                mNodeCellDataMap.put(cellData.mSopNode, cellData);
//            }
//        }
//    }

    /**
     * Doing the addition of nodes to the graph.<br/>
     * There are some different methods to add a Cell/Node dependent on where in the graph the cell should be added.<br/>
     * @param iRoot
     * @param iNodeCellMap What type of {@link Cell} each {@link ISopNode} is
     */
    private void recursiveCallToAllNodes(final ISopNode iRoot) {
        //Problems to get a good layout if cells are not sorted from left to right.
        final List<ISopNode> sortedList = sortSequenceSetBasedOnGeometry(iRoot.getFirstNodesInSequencesAsSet());

        for (ISopNode node : sortedList) {
//            System.out.println("node to work with: " + node.typeToString());
            //First node---------------------------------------------------------
            //Especially first nodes are strange...
            if (iRoot == mRoot) {
                mGraph.addCell(mSopNodeCellMap.get(node));
            } else {

                if (doAutoLayout) {
                    mGraph.insertGroupNode(mSopNodeCellMap.get(iRoot), null, mSopNodeCellMap.get(node));
                } else {

//                    System.out.println(node.typeToString() + " bgeo: " + mSopNodeCellMap.get(node).getGeometry().getRectangle());
                    mGraph.insertGroupNode(mSopNodeCellMap.get(iRoot), mSopNodeCellMap.get(node));
//                    System.out.println(node.typeToString() + " ageo: " + mSopNodeCellMap.get(node).getGeometry().getRectangle());
                }

                if (mSopNodeCellMap.get(iRoot).getType() == Constants.SOP) {
                    Object[] oSet = mGraph.getEdges(mSopNodeCellMap.get(node));
                    for (Object o : oSet) {
                        Cell c = (Cell) o;
                        c.removeFromParent();
                    }
                }
            }//------------------------------------------------------------------

            //Children to first node---------------------------------------------
            recursiveCallToAllNodes(node);
            //-------------------------------------------------------------------

            //Successor(s)-------------------------------------------------------
            while (node.getSuccessorNode() != null) {
                final ISopNode successorNode = node.getSuccessorNode();
                drawSequenceWithRespectToRelation(node, successorNode, mSopNodeCellMap);
                //Children to successor node
                recursiveCallToAllNodes(successorNode);
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

        if (doAutoLayout) {
            mGraph.insertNewCell(cellPred, cellSucc, false);
        } else {
            mGraph.insertSuccessorCell(cellSucc, cellPred);
        }

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

        Cell returnCell = null;
        if (iNode instanceof SopNodeOperation) {
            final OperationData opData = iNode.getOperation();
            if (iNode.sequenceSetIsEmpty()) {
                //Operation
                returnCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
            } else {
                //SOP Operation
                returnCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_SOP);
            }
            returnCell.setValue(opData);

        } else if (iNode instanceof SopNode) {
            returnCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_SOP);
            final Data newOpData = new OperationData("", -1);
            Model.giveId(newOpData);
            returnCell.setValue(newOpData);

        } else if (iNode instanceof SopNodeAlternative) {
            returnCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ALTERNATIVE);
        } else if (iNode instanceof SopNodeArbitrary) {
            returnCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ARBITRARY);
        } else if (iNode instanceof SopNodeParallel) {
            returnCell = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_PARALLEL);
        }

        //Set celldata if such exists
        if (returnCell != null && mNodeCellDataLayoutMap != null) {
            if (mNodeCellDataLayoutMap.containsKey(iNode)) {

//                System.out.println("DrawSopNode: extra cell data for " + iNode.typeToString());

                final CellDataLayout cellData = mNodeCellDataLayoutMap.get(iNode);
                returnCell.setGeometry(cellData.mGeo);
                returnCell.setCollapsed(!cellData.mExpanded);

                doAutoLayout = false;
            }
        }

        return returnCell;
    }

    /**
     * Sorts parameter <p>iSet</p> with method getTopLeftSopNode(...).<br/>
     * @param iSet
     * @return the set sorted as a {@link List}
     */
    private List<ISopNode> sortSequenceSetBasedOnGeometry(final Set<ISopNode> iSet) {
        final List<ISopNode> sortedList = new ArrayList<ISopNode>();

        final Set<ISopNode> removeSet = new HashSet<ISopNode>(iSet);

        while (!removeSet.isEmpty()) {
            final ISopNode node = getTopLeftSopNode(removeSet);
            removeSet.remove(node);
            sortedList.add(node);
        }

        return sortedList;
    }

    /**
     * Returns the leftmost {@link Cell} in parameter <p>iSet</p>.<br/>
     * @param iSet
     * @return
     */
    private ISopNode getTopLeftSopNode(final Set<ISopNode> iSet) {
        //get start node
        ISopNode bestNode = iSet.iterator().next();

        //Find top left
        for (final ISopNode otherNode : iSet) {
            final Cell bestCell = mSopNodeCellMap.get(bestNode);
            final Cell otherCell = mSopNodeCellMap.get(otherNode);

            if (otherCell.getGeometry().getX() < bestCell.getGeometry().getX()) {
                bestNode = otherNode;
            }
        }

        return bestNode;
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
