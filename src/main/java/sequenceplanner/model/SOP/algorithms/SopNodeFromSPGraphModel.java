package sequenceplanner.model.SOP.algorithms;

import sequenceplanner.model.SOP.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

/**
 * To translate a {@link SPGraphModel} object to a structure of {@link SopNode} objects.<br/>
 * The {@link SPGraphModel} is given as parameter to constructor.<br/>
 * The result (root {@link SopNode}) is available with the method getSopNodeRoot().<br/>
 * @author patrik
 */
public class SopNodeFromSPGraphModel {

    private Set<Cell> mAllSopNodesAsCells = null;
    private Set<Cell> mChildSopNodesAsCells = null;
    private SPGraphModel mSPGraphModel = null;
    private Map<SopNode, Cell> mSopNodeCellMap = null;
    private SopNode mSopNodeRoot = null;

    /**
     *
     * @param iSPGraphModel the graph to work with
     */
    public SopNodeFromSPGraphModel(final SPGraphModel iSPGraphModel, final SopNode iSopNodeRoot) {
        mSPGraphModel = iSPGraphModel;
        mSopNodeRoot = iSopNodeRoot;
        mSopNodeCellMap = new HashMap<SopNode, Cell>();
        filterHashTable();
        getChildCells();
        hierarchicalPartitioning();
        sequenceChildren(mSopNodeRoot);
    }

    /**
     *
     * @return The root {@link SopNode}
     */
    public SopNode getSopNodeRoot() {
        return mSopNodeRoot;
    }

    /**
     * Each {@link SopNode} key has a corresponding {@link Cell} value.<br/>
     * Could be used to get eg geometry info of cell object.<br/>
     * @return
     */
    public Map<SopNode, Cell> getNodeCellMap() {
        return mSopNodeCellMap;
    }

    /**
     * A {@link SPGraphModel} contains many different {@link Cell} types.<br/>
     * This method filters out the cells given mehtod getCorrectCells();
     * @return always true
     */
    private boolean filterHashTable() {
        final Collection allCells = mSPGraphModel.getCells().values();
        mAllSopNodesAsCells = getCorrectCells(allCells);
        return true;
    }

    /**
     * Collects "correct" cells that are children to other correct cells.</br>
     * The collected cells should not be children to mSopNodeRoot.
     */
    private void getChildCells() {
        mChildSopNodesAsCells = new HashSet<Cell>();
        for (final Cell cell : mAllSopNodesAsCells) {
            if (cell.getChildCount() > 0) {
                final Set<Object> children = objectToSet(SPGraphModel.getChildren(mSPGraphModel, cell));
                final Set<Cell> childrenFiltered = getCorrectCells(children);
                mChildSopNodesAsCells.addAll(childrenFiltered);
            }
        }
    }

    /**
     * To start hierarchical partitioning between cells.
     * @return always true
     */
    private boolean hierarchicalPartitioning() {
        while (!mAllSopNodesAsCells.isEmpty()) {
            final Cell anyCell = mAllSopNodesAsCells.iterator().next();
            workWithCell(anyCell, mSopNodeRoot);
        }
        return true;
    }

    /**
     * For parameter iCell:<br/>
     * Create a new {@link SopNode} based on Cell type (operation, operation sop, alternative, arbitrary, parallel).<br/>
     * Recursively call this method with child cells to iCell.<br/>
     * Add the new node as child to parameter iParentNode
     * @param iCell current cell
     * @param iParentNode parent to iCell.
     * @return always true
     */
    private boolean workWithCell(final Cell iCell, final SopNode iParentNode) {
        mAllSopNodesAsCells.remove(iCell);
        final SopNode node = setNewSopNode(iCell);

        if (iCell.getChildCount() > 0) {
            final Set<Object> children = objectToSet(SPGraphModel.getChildren(mSPGraphModel, iCell));
            final Set<Cell> childrenFiltered = getCorrectCells(children);
            for (final Cell child : childrenFiltered) {
                workWithCell(child, node);
            }
        }

        //Add child to parent must be after recursive calls otherwise problems parent/child relation
        if(iParentNode.equals(mSopNodeRoot) && mChildSopNodesAsCells.contains(iCell) ){ //Extra for this case
            return true;
        }
        iParentNode.addNodeToSequenceSet(node);

        return true;
    }

    /**
     * Filter out cells that are operations, operation sops, alternative, arbitrary order, and parallel.<br/>
     * @param iCollection the collection to filter
     * @return A set with cells taht passed the filer
     */
    private Set<Cell> getCorrectCells(final Collection iCollection) {
        final Set<Cell> returnSet = new HashSet<Cell>();
        for (final Object object : iCollection) {
            final Cell cell = (Cell) object;
            if (cell.isOperation() || cell.isSOP() || cell.isAlternative() || cell.isArbitrary() || cell.isParallel()) {
                returnSet.add(cell);
            }
        }
        return returnSet;
    }

    /**
     * Create sequence for the child nodes to parameter iParentNode.<br/>
     * This is accomplished through comparison of incoming and outgoing edges to and from the cells.<br/>
     * Sequenced child modes are removed as "first in seqence set" for iParentNode.<br/>
     * Each child is called recursively.<br/>
     * @param iParentNode
     * @return always true
     */
    private boolean sequenceChildren(final SopNode iParentNode) {

        //Create map for incoming edges------------------------------------------
        final Map<Set<Object>, SopNode> incomingEdgesNodeMap = new HashMap<Set<Object>, SopNode>();
        for (final SopNode node : iParentNode.getFirstNodesInSequencesAsSet()) {
            final Cell cell = mSopNodeCellMap.get(node);
            final Set<Object> incomingEdges = objectToSet(SPGraphModel.getIncomingEdges(mSPGraphModel, cell));
            final Set<Object> childEdges = objectToSet(SPGraphModel.getChildren(mSPGraphModel, cell));
            incomingEdges.removeAll(childEdges);
            incomingEdgesNodeMap.put(incomingEdges, node);
        }//----------------------------------------------------------------------

        //loop child nodes and match edges---------------------------------------
        final Set<SopNode> nodesToRemoveForParent = new HashSet<SopNode>();
        for (final SopNode node : iParentNode.getFirstNodesInSequencesAsSet()) {
            matchEdges(node, incomingEdgesNodeMap, nodesToRemoveForParent);
            sequenceChildren(node);
        }//----------------------------------------------------------------------

        //remove nodes that not are first in sequence among children to parent---
        for (final SopNode node : nodesToRemoveForParent) {
            iParentNode.removeFromSequenceSet(node);
        }//----------------------------------------------------------------------

        return true;
    }

    /**
     * Method for comparison of edges.
     * @param iNode
     * @param iIncomingEdgesNodeMap
     * @param ioNodesToRemoveForParent
     */
    private void matchEdges(final SopNode iNode, final Map<Set<Object>, SopNode> iIncomingEdgesNodeMap, final Set<SopNode> ioNodesToRemoveForParent) {
        final Cell cell = mSopNodeCellMap.get(iNode);
        final Set<Object> outgoingEdges = objectToSet(SPGraphModel.getOutgoingEdges(mSPGraphModel, cell));
        final Set<Object> childEdges = objectToSet(SPGraphModel.getChildren(mSPGraphModel, cell));
        outgoingEdges.removeAll(childEdges);

        for (final Object objOut : outgoingEdges) {
            for (final Set<Object> objInSet : iIncomingEdgesNodeMap.keySet()) {
                for (final Object objIn : objInSet) {
                    if (objOut.equals(objIn)) {
                        final SopNode successorNode = iIncomingEdgesNodeMap.get(objInSet);
                        iNode.setSuccessorNode(successorNode);
                        iNode.setSuccessorRelation(IRelateTwoOperations.ALWAYS_IN_SEQUENCE_12);
                        ioNodesToRemoveForParent.add(successorNode);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Translate an array to a {@link Set}
     * @param iObjectSet array
     * @return the set
     */
    private Set<Object> objectToSet(final Object[] iObjectSet) {
        final Set<Object> returnSet = new HashSet<Object>();
        for (final Object obj : iObjectSet) {
            returnSet.add(obj);
        }
        return returnSet;
    }

    /**
     * Creates a {@link SopNode} from a {@link Cell}.<br/>
     * Initial check that a node hasn't already been given for parameter iNewCell.<br/>
     * @param iNewCell the cell
     * @return the new node
     */
    private SopNode setNewSopNode(final Cell iNewCell) {
        //Return node if this cell already has been worked with.
        if (mSopNodeCellMap.containsValue(iNewCell)) {
            for (final SopNode node : mSopNodeCellMap.keySet()) {
                if (mSopNodeCellMap.get(node).equals(iNewCell)) {
                    return node;
                }
            }
        }
        SopNode newSopNode = null;

        //Create node
        if (iNewCell != null) {
            if (iNewCell.isOperation() || iNewCell.isSOP()) {
                final OperationData opData = (OperationData) iNewCell.getValue();
                newSopNode = new SopNodeOperation(opData);
            } else if (iNewCell.isParallel()) {
                newSopNode = new SopNodeParallel();
            } else if (iNewCell.isAlternative()) {
                newSopNode = new SopNodeAlternative();
            } else if (iNewCell.isArbitrary()) {
                newSopNode = new SopNodeArbitrary();
            } else {
                System.out.println("Could not create new cell");
            }
        }

        //Add to map
        if (newSopNode != null) {
            mSopNodeCellMap.put(newSopNode, iNewCell);
        }

        return newSopNode;
    }
}
