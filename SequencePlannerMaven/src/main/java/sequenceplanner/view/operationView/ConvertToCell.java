package sequenceplanner.view.operationView;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import java.util.ArrayList;
import java.util.LinkedList;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.model.data.ViewData.CellData;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

/**
 *
 * @author Carl Thorstensson
 */
public class ConvertToCell {

   //TODO What need to urgently be fixed.
   // * An Op can have transformed into an SOP, check and change!
   // * A SOP can have transformed into an op


    public LinkedList<CellData> cellTable;
    public Model model;
    private Cell root;
    private OperationView view;
    private int realRootId = -1;

    public void ConvertToCell(ViewData data, Model theModel, OperationView view) {
        this.cellTable = data.getData();
        this.model = theModel;
        this.root = new Cell("");
        this.view = view;

        System.out.println("-------------------");

        this.realRootId = data.getRoot();

        for(int i=0; i<cellTable.size(); i++){
            CellData c = cellTable.get(i);
            System.out.println("CellID: " + c.id + " Previous Cell: " + c.previousCell
                    + " Type: " + c.type + " Relation: " + c.relation + " LastInRelation: " + c.lastInRelation);
        }

        System.out.println("------------------");
        for (int i = 0; i < cellTable.size(); i++) {
            CellData d = cellTable.get(i);

            if (d.previousCell == -1 && d.relation == realRootId) {
                newCell(i, null, null, null);
            }
        }
    }

    public Cell getRoot() {
        return root;
    }

    public void newCell(int tableIndex, Cell previousCell, Cell parentCell, CellData previousData) {

        CellData cellData = cellTable.get(tableIndex);
        int cellID = cellData.id;
        int previousCellID = cellData.previousCell;
        int cellType = cellData.type;
        int relationID = cellData.relation;
        boolean endOfRelation = cellData.lastInRelation;


        int previousCellType = -1;

        if (previousData != null) {
            previousCellType = previousData.type;
        }

        Cell thisCell = new Cell();





        if (cellType == Cell.PARALLEL) {

            // Draw a parallel cell with transition from previousCell to this cell.
            thisCell = drawParallel(tableIndex, previousCell, parentCell);

            if (endOfRelation) {

                int followingOp = getFollowingOperation(previousCellID);

                // followingOp is the op that eventually has this cell as precon.

                if (seqPreconditionOK(cellID, followingOp)) {
                    // Draw a transition from this cell to its parents end.
                    drawTransition(thisCell, parentCell);
                }
            }


        } else if (cellType == Cell.ALTERNATIVE) {
            // Draw an alternative cell with transition from previousCell to this cell.
            thisCell = drawAlternative(tableIndex, previousCell, parentCell);

            if (endOfRelation) {

                int followingOp = getFollowingOperation(previousCellID);

                // followingOp is the op that eventually has this cell as precon.

                if (seqPreconditionOK(cellID, followingOp)) {
                    // Draw a transition from this cell to its parents end.
                    drawTransition(thisCell, parentCell);
                }
            }



        } else if (cellType == Cell.OP || cellType == Cell.SOP) {

            if (isExisting(cellID)) {
                // An existing operation
                if (previousCellType == Cell.PARALLEL) {

                    if (relationID == previousCellID) {

                        boolean noPreviousOp = false;
                        int type = previousCellType;
                        int name = previousCellID;

                        while (type != Cell.OP) {
                            CellData o = cellTable.get(getIndexOfCell(name));

                            if(o.previousCell == -1){
                                noPreviousOp = true;
                                break;
                            }

                            CellData o2 = cellTable.get(getIndexOfCell(o.previousCell));
                            name = o2.id;
                            type = o2.type;                                                        
                        }

                        // name is the name of the precondition operation.
                        if (noPreviousOp) {
                            thisCell = drawOperation(tableIndex, previousCell, parentCell);

                        } else {
                            if (seqPreconditionOK(name, cellID)) {
                             
                                // Draw an operation cell in parentCell with transition from parentCell.
                                thisCell = drawOperation(tableIndex, previousCell, parentCell, name);

                            } else {

                                // Draw an operation cell in parentCell.
                                thisCell = drawOperation(tableIndex, null, parentCell);

                            }
                        }


                    } else {
                        // An operation after a parallel cell. The transition should always be drawn.
                        // Draw a parallel cell with transition from previousCell to this cell.
                        thisCell = drawOperation(tableIndex, previousCell, parentCell);

                    }

                } else if (previousCellType == Cell.OP || previousCellType == Cell.SOP) {

                    if (seqPreconditionOK(previousCellID, cellID)) {
                        // Draw an operation in its relation with transition from previousCell to this cell.
                        thisCell = drawOperation(tableIndex, previousCell, parentCell);

                    } else { // Precondition not ok
                        // Draw an operation in relation.
                        thisCell = drawOperation(tableIndex, null, parentCell);
                    }


                } else if (previousCellType == Cell.ALTERNATIVE) {

                    if (relationID == previousCellID) {

                        boolean noPreviousOp = false;
                        int type = previousCellType;
                        int name = previousCellID;

                        while (type != Cell.OP) {
                            CellData o = cellTable.get(getIndexOfCell(name));

                            if(o.previousCell == -1){
                                noPreviousOp = true;
                                break;
                            }

                            CellData o2 = cellTable.get(getIndexOfCell(o.previousCell));
                            name = o2.id;
                            type = o2.type;
                        }

// System.out.println("Op: " + cellID + "   seq   " + seqPreconditionOK(name, cellID) + "   res    " + altResourceOK(cellID, relationID));
                        // name is the name of the precondition operation.
                        if (noPreviousOp == true) {
                            thisCell = drawOperation(tableIndex, previousCell, parentCell);

                        } else if (altResourceOK(cellID, relationID) && seqPreconditionOK(name, cellID)) {
                            // The alternative resource and precondition exist.
                 
                            // Draw an operation cell in parentCell with transition from parentCell.
                            thisCell = drawOperation(tableIndex, previousCell, parentCell, name);
                        } else {
                            // Either the precondition or the resource booking is not valid, or both.
                            // Remove the resource booking and the precondition
                            // Draw an operation in parent with no connection to top of alternative

                      
                            thisCell = drawOperation(tableIndex, null, parentCell);
                            removeAltResourceBooking(cellID, relationID);
                            removeSeqPrecondition(cellID, name);
                        }


                    } else {
                        // An operation after a parallel cell. The transition should always be drawn.
                        // Draw a parallel cell with transition from previousCell to this cell.
                        thisCell = drawOperation(tableIndex, previousCell, parentCell);

                    }



                } else { // No previous cell
                    if (cellType == Cell.OP || cellType == Cell.SOP) {
                        thisCell = drawOperation(tableIndex, null, parentCell);
                    } else if (cellType == Cell.PARALLEL) {
                        thisCell = drawParallel(tableIndex, null, parentCell);
                    } else if (cellType == Cell.ALTERNATIVE) {
                        thisCell = drawAlternative(tableIndex, null, parentCell);
                    } else {
                        // Unknown type.
                    }
                }

                if (endOfRelation) {
                    int followingOp = getFollowingOperation(cellID);

                   
                    // followingOp is the op that should have this cell as sequence condition.

                    int relationIndex = getIndexOfCell(relationID);
                    if (cellTable.get(relationIndex).type == Cell.ALTERNATIVE) {
                        // This operation is last in an alternative cell with followingOp as first cell after the alternative.
            
                        if (followingOp == -1) {
                            // No following operation
                            drawTransition(thisCell, parentCell);

                        } else if (altPreconditionOK(cellID, followingOp)) {
                            // Find the first cell in this sequence
                            drawTransition(thisCell, parentCell);


                            //check if the first cell in the sequence is connected to parentCell.
//                     if(connected){
//                      drawTransition(thisCell, parentCell);
//                     }else if(removeAltPrecondition(followingOp, cellID)){
//                      drawTransition(thisCell, parentCell);
//                     } else{
//                      // Draw no line
//                     }
                        } else {
                            // No valid precondition. This sequence isn't part of the alternative end any more.
                            // Do nothing.
                        }
                    } else {
                        if (followingOp == -1) {
                            drawTransition(thisCell, parentCell);
                        } else if (seqPreconditionOK(cellID, followingOp)) {
                            // Draw a transition from this cell to its parents end.
                            drawTransition(thisCell, parentCell);
                        } else {
                            // There should be no transition from Op to the end of parent cell.
                        }
                    }
                }

            } else { // No existing cell in the model.
            }
        } else {
            // Unknown type of cell.
        }

        // Create new cells
        for (int i = 0; i < cellTable.size(); i++) {
            CellData o = cellTable.get(i);

            if (o.previousCell == cellID) {
                // This cell follows current cell
                if (o.relation == cellID) {
                    // The new cell lies in this cell as a child
                    newCell(i, thisCell, thisCell, cellData);
                } else {
                    // The new cell lies outside this cell and i therefore a child to the same cell as this cell is.
                    newCell(i, thisCell, parentCell, cellData);
                }
            } else if(o.previousCell == -1 && o.relation == cellID){

                // The cell lies in the relation but has no precondition
                newCell(i, null, thisCell, null);
            }
        }

    }

    public void drawTransition(Cell theCell, Cell theParent) {
        mxCell edge = CellFactory.getInstance().getEdge(false);
        theParent.insertEdge(edge, false);
        theCell.insertEdge(edge, true);
        theParent.insert(edge);
    }

    public Cell drawGroupCell(int tableIndex, Cell previousCell, Cell parent, String type) {
        CellData data = cellTable.get(tableIndex);

        Cell thisCell = drawCell(tableIndex, previousCell, parent, type, data);
        ((Data) thisCell.getValue()).setId(data.id);


        return thisCell;
    }

    public Cell drawOperation(int tableIndex, Cell previousCell, Cell parent) {
        return drawOperation(tableIndex, previousCell, parent, 0);
    }


    public Cell drawOperation(int tableIndex, Cell previousCell, Cell parent, int seqConId) {
        CellData data = cellTable.get(tableIndex);
        Cell thisCell;
        
        if(model.getOperation(data.id).getChildCount() > 0 || data.type == Cell.SOP){
            thisCell = drawCell(tableIndex, previousCell, parent, SPGraphModel.TYPE_SOP, data);


        } else {
            thisCell = drawCell(tableIndex, previousCell, parent, SPGraphModel.TYPE_OPERATION, data);

        }


        OperationData d = (OperationData) model.getOperation(data.id).getNodeData().clone();
        thisCell.setValue(d);

        if (previousCell != null && previousCell.isOperation()) {
            int id = ((Data) previousCell.getValue()).getId();
        }

        //New
        if (previousCell != null) {

            if (previousCell.isAlternative()) {
                if (previousCell == parent) {
                    // thisCell is first in an alternative cell.
                    removeAltResBookFromCell(thisCell, parent.getUniqueId());
                    removeSeqConFromCell(thisCell, seqConId);
                } else {
                    // thisCell is after an alternative
                    removeAltSeqConFromCell(thisCell);
                }


            } else if (previousCell.isParallel()) {
                if (previousCell == parent) {
                    // thisCell is first in a parallel cell.
                    removeSeqConFromCell(thisCell, seqConId);

                } else {
                    // thisCell is after a parallel cell.
                    removeParSeqConFromCell(thisCell);
                }
            } else if (previousCell.isOperation() || previousCell.isSOP()) {

                removeSeqConFromCell(thisCell, previousCell.getUniqueId());
            }
        }

        return thisCell;
    }

    private void removeAltResBookFromCell(Cell theCell, int parent) {
        ((OperationData) theCell.getValue()).removeResourceBooking(parent);
    }

    private void removeSeqConFromCell(Cell theCell, int seqconID) {
        ((OperationData) theCell.getValue()).removeAnd(seqconID, 2);
    }

    private void removeAltSeqConFromCell(Cell theOperationCell) {
        LinkedList<Cell> cells = view.getGraph().
                getPreviousOperations(view.getGraph().getNextCell(theOperationCell, true));

        LinkedList<SeqCond> listOfIDs = new LinkedList<SeqCond>();
        for (Cell cell : cells) {
            listOfIDs.add(new SeqCond(cell.getUniqueId(), 2) );
        }

        OperationData oD = ((OperationData)theOperationCell.getValue());

        LinkedList<LinkedList<SeqCond>> seqcon = oD.getSequenceCondition();

        for (LinkedList<SeqCond> linkedList : seqcon) {

     //        System.out.println("linkedList: " + linkedList);
            if (oD.isListEqual(linkedList, listOfIDs)) {
                seqcon.remove(linkedList);
                break;
            }
        }
    }

    private void removeParSeqConFromCell(Cell theOperationCell) {
        LinkedList<Cell> cells = view.getGraph().
                getPreviousOperations(view.getGraph().getNextCell(theOperationCell, true));;

        OperationData oD = ((OperationData)theOperationCell.getValue());

        for (Cell cell : cells) {
            oD.removeAnd(cell.getUniqueId(), 2);

          //    System.out.println("removeParSeqConFromCell cell:  " + cell);
        }        
    }

    /// FINISHED NEW
    public Cell drawCell(int tableIndex, Cell previousCell, Cell parent, String type, CellData data) {

        mxGeometry geo = (mxGeometry) data.geo.clone();

        if (parent == null) {
            parent = root;
        }

        Cell thisCell = CellFactory.getInstance().getOperation(type);
        thisCell.setGeometry(geo);

        thisCell.setCollapsed(!data.expanded);

        parent.insert(thisCell);


        if (previousCell != null) {
            boolean typus = !(previousCell.isGroup() && previousCell == parent);
            mxCell edge = CellFactory.getInstance().getEdge(typus);
            thisCell.insertEdge(edge, false);
            previousCell.insertEdge(edge, true);
            parent.insert(edge);
        }

        return thisCell;
    }

    public Cell drawParallel(
            int tableIndex, Cell previousCell, Cell parent) {
        return drawGroupCell(tableIndex, previousCell, parent, SPGraphModel.TYPE_PARALLEL);
    }

    public Cell drawAlternative(
            int tableIndex, Cell previousCell, Cell parent) {
        return drawGroupCell(tableIndex, previousCell, parent, SPGraphModel.TYPE_ALTERNATIVE);
    }

    public int getFollowingOperation(int previousCellID) {

        int previousName = previousCellID;

        boolean end = true;
        while (end) {
            CellData o = cellTable.get(getIndexOfCell(previousName));
            int parent = o.relation;

            CellData o2 = cellTable.get(getIndexOfCell(parent));
            end =  o2.lastInRelation;
            previousName = o2.id;
        }


        //TODO fix take the one before instead of the one after.
   
        // name is the ID of the cell which is the main parallel

        int followingOp = -1;
        for (int j = 0; j <
                cellTable.size(); j++) {
            CellData ob = cellTable.get(j);
            if (ob.previousCell == previousName && ob.relation != previousName) {
                followingOp = ob.id;
                break;

            }


        }
        // followingOp is the op that eventually has this cell as precon.

        return followingOp;
    }

    /**
     *
     * @param cellID
     * @return id or -1 if not found.
     */
    public int getIndexOfCell(int cellID) {
        int index = -1;

        for (int i = 0; i <
                cellTable.size(); i++) {
            if (cellTable.get(i).id == cellID) {
                index = i;
                break;

            }


        }
        return index;
    }


    /**
     * Methods for checking if the cell exists in the model and lies in the right SOP
     *
     * @param cellID the ID of the cell to check
     * @param parentID the ID for the parent to cellID
     * @return true if cellID exists and lies in the right SOP
     */
    public boolean isExisting(int cellID) {
        TreeNode node = model.getOperation(cellID);

        if (node == null) {
            return false;
        } else if (realRootId != -1 && node.getParent().getId() != realRootId) {
            return false;
        }
        
        return true;
        
    }

// Return true if the precondition that sourceOpID should be finished before targetOpID exists.
    public boolean seqPreconditionOK(int sourceOpID, int targetOpID) {
        TreeNode node = model.getOperation(targetOpID);

        if (node != null) {
            return ((OperationData) node.getNodeData()).isSequence(sourceOpID);
        }

        return false;
    }

    /**
     * Checks the resource booking of an alternative start sequence condition.¨
     *
     * @param cellID is the operation which is first in a sequence in an alternative cell.
     * @param relationID is the alternative parent to cellID
     *
     * @return true if the alternative resource booking is valid.
     */
    private boolean altResourceOK(int cellID, int relationID) {
        TreeNode node = model.getOperation(cellID);
        return ((OperationData) node.getNodeData()).isResourceBooked(relationID);
    }

    /**
     * Removes the alternative resource booking from the sequence condition.
     *
     * @param cellID is the operation which is first in a sequence in an alternative cell.
     * @param relationID is the alternative parent to cellID.
     */
    private void removeAltResourceBooking(int cellID, int relationID) {
        TreeNode node = model.getOperation(cellID);

        ((OperationData) node.getNodeData()).removeResourceBooking(relationID);

    }

    /**
     * Removes a normal AND-sequence condition.
     *
     * @param cellID is the operation which has a precondition that needs to be removed.
     * @param preconditionID is the operation that is in cellID's precondition.
     */
    private void removeSeqPrecondition(int cellID, int preconditionID) {
        TreeNode node = model.getOperation(cellID);
        LinkedList<LinkedList<OperationData.SeqCond>> seqcon = ((OperationData) node.getNodeData()).getSequenceCondition();

        for (int i = 0; i <
                seqcon.size(); i++) {
            if (seqcon.get(i).size() == 1 && seqcon.get(i).get(0).id == preconditionID) {
                seqcon.remove(i);
            }

        }
    }

    /**
     * Check if the operation following an alternative has the right sequence condition.
     *
     * @param sourceOpID is the source operation. This operation is the precondition
     * @param targetOpID is the target operation. This operations has the precondition
     *
     * @return true if the precondition that sourceOpID should be finished before targetOpID is executed exists.
     */
    public boolean altPreconditionOK(int sourceOpID, int targetOpID) {
        boolean precondition = false;

        ArrayList<CellData> listOfOps = new ArrayList<CellData>();
        TreeNode node = model.getOperation(targetOpID);
        LinkedList<LinkedList<OperationData.SeqCond>> seqcon = ((OperationData) node.getNodeData()).getSequenceCondition();

        int sourceParent = cellTable.get(getIndexOfCell(sourceOpID)).relation;

        for (int i = 0; i < cellTable.size(); i++) {
            CellData o = cellTable.get(i);
            if (o.relation == sourceParent && o.lastInRelation) {
                // CellData o is last cell in its sequence for the same alternative as sourceOpID is in.

                if (isExisting(o.id)) {
                    listOfOps.add(o);
                }

            }
        }

        for (int i = 0; i <
                seqcon.size(); i++) {
            boolean ok = true;

            for (int j = 0; j <
                    seqcon.get(i).size(); j++) {
                OperationData.SeqCond con = seqcon.get(i).get(j);

                boolean inTheList = false;

                for (int h = 0; h <
                        listOfOps.size(); h++) {
                    if (con.id == listOfOps.get(h).id && con.state == 2) {

                        inTheList = true;
                        if (con.id == sourceOpID) {
                            precondition = true;
                        }

                    }
                }
                if (!inTheList) {
                    ok = false;
                }

            }
            if (ok && precondition) {
                return true;
            }

        }
        return false;
    }
}
