package sequenceplanner.model.SOP;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

/**
 * 
 * @author patrik
 */
public class SopStructurePatrikEdition implements ISopStructure {

    private Map<Cell, ISopNode> mCellSopNodeMap;
    private ISopNode mSopNodeRoot;
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();

    public SopStructurePatrikEdition() {
        mCellSopNodeMap = new HashMap<Cell, ISopNode>();
        mSopNodeRoot = new SopNode();
    }

    @Override
    public void setSopRoot(ASopNode sopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addCellToSop(Cell iReferenceCell, Cell iNewCell, boolean iBefore) {
        //Set new SopNode--------------------------------------------------------
        ISopNode newSopNode = setNewSopNode(iNewCell);
        if (newSopNode == null) {
            return false;
        }//----------------------------------------------------------------------

        //Set reference SopNode--------------------------------------------------
        ISopNode referenceSopNode = setReferenceSopNode(iReferenceCell);
        if (referenceSopNode == null) {
            return false;
        }//----------------------------------------------------------------------

        //Add node in a sequence-------------------------------------------------
        if (iBefore) { //Add before
            final Set<ISopNode> firstNodeSet = mSopNodeRoot.getFirstNodesInSequencesAsSet();
            if (firstNodeSet.contains(referenceSopNode)) {
                //remove reference node from root node
                mSNToolbox.removeNode(referenceSopNode, mSopNodeRoot);
                //add new node to root node
                mSopNodeRoot.addNodeToSequenceSet(newSopNode);
            } else {
                final ISopNode predecessor = mSNToolbox.getPredecessor(referenceSopNode, mSopNodeRoot);
                if (predecessor != null) {
                    predecessor.setSuccessorNode(newSopNode);
                } else {
                    //For example if an operation is added before first operation in a parallel node.
                }
            }
            newSopNode.setSuccessorNode(referenceSopNode);
        } else { //iBefore==false -> Add after
            final ISopNode oldSuccessorNode = referenceSopNode.getSuccessorNode();
            if (oldSuccessorNode != null) {
                newSopNode.setSuccessorNode(oldSuccessorNode);
            }
            referenceSopNode.setSuccessorNode(newSopNode);
        }//----------------------------------------------------------------------

        printMethod();
        return true;
    }

    @Override
    public boolean addCellToSop(Cell iReferenceCell, Cell iNewCell) {
        //Set new SopNode--------------------------------------------------------
        ISopNode newSopNode = setNewSopNode(iNewCell);
        if (newSopNode == null) {
            return false;
        }//----------------------------------------------------------------------

        //Set reference SopNode--------------------------------------------------
        ISopNode referenceSopNode = setReferenceSopNode(iReferenceCell);
        if (referenceSopNode == null) {
            return false;
        }//----------------------------------------------------------------------

        referenceSopNode.addNodeToSequenceSet(newSopNode);

        printMethod();
        return true;
    }

    @Override
    public boolean addCellToSop(Cell iNewCell) {
        //Set new SopNode--------------------------------------------------------
        ISopNode newSopNode = setNewSopNode(iNewCell);
        if (newSopNode == null) {
            return false;
        }//----------------------------------------------------------------------

        mSopNodeRoot.addNodeToSequenceSet(newSopNode);

        printMethod();
        return true;
    }

    @Override
    public boolean updateSopNode(SPGraph iSpGraph) {

        SPGraphModel graphModel = iSpGraph.getGraphModel();
        System.out.println("table: " + graphModel.getCells());
        System.out.println("root: " + graphModel.getRoot());
        System.out.println("graphroot: " + graphModel.getGraphRoot().toString());

        Set s = graphModel.getCells().keySet();

        for (Cell cell : mCellSopNodeMap.keySet()) {
            if (s.contains(cell.getId())) {
                System.out.println("graph: " + graphModel.getCell(cell.getId()).toString());
                System.out.println("map: " + mCellSopNodeMap.get(cell).getTypeAsString());

            } else {
                System.out.println("Not in keyset, Cell.id: " + cell.getId());
            }
        }

        Collection collection = graphModel.getCells().keySet();

        Map<String, Cell> idCellMap = new HashMap<String, Cell>();
        for (Cell cell : mCellSopNodeMap.keySet()) {
            idCellMap.put(cell.getId(), cell);
        }

        System.out.println(".-----");

        Set<Object> edgeSet = new HashSet<Object>();
        for (Object o : s) {
            String string = (String) o;
            Object[] oo = null;
            Object[] ooo = null;
            Object[] children = null;
            Object[] connections = null;
            o = graphModel.getCell(string);
            System.out.println("o: " + o + " value: " + graphModel.getValue(o));
            Cell cell = (Cell) graphModel.getCell(string);
            System.out.println("cell: " + cell.toString());
            if (idCellMap.containsKey(string)) {
                oo = SPGraphModel.getOutgoingEdges(graphModel, o);
                ooo = SPGraphModel.getIncomingEdges(graphModel, o);
                children = SPGraphModel.getChildren(graphModel, o);
                connections = SPGraphModel.getConnections(graphModel, o);
            }
//            } else {
//                oo = SPGraphModel.getChildren(graphModel, o);
//                oo = SPGraphModel.getConnections(graphModel, o);
//            }
            if(oo != null) {
            for (Object obj : oo) {
                System.out.println("obj: " + obj);
                if(edgeSet.contains(obj)) {
                    System.out.println("new edge");
                } else {
                    System.out.println("old edge");
                }
                edgeSet.add(obj);
            }
            }
            if(ooo != null) {
            for (Object obj : ooo) {
                System.out.println("obj: " + obj);
                if(edgeSet.contains(obj)) {
                    System.out.println("new edge");
                } else {
                    System.out.println("old edge");
                }
                edgeSet.add(obj);
            }
            }
            if(children != null) {
            for (Object obj : children) {
                System.out.println("child: " + obj);
            }
            }
            if(connections != null) {
            for (Object obj : connections) {
                System.out.println("con: " + obj);
                if(edgeSet.contains(obj)) {
                    System.out.println("new edge");
                } else {
                    System.out.println("old edge");
                }
                edgeSet.add(obj);
            }
            }
        }
        return true;
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode, boolean before) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void printMethod() {
        System.out.println("-----");
        System.out.println(mSopNodeRoot.toString());
        System.out.println("-----");
    }

    private ISopNode setNewSopNode(final Cell iNewCell) {
        ISopNode newSopNode = null;

        //Create node
        if (iNewCell != null) {
            if (iNewCell.isOperation()) {
                final OperationData opData = (OperationData) iNewCell.getValue();
                newSopNode = new SopNodeOperation(opData);
            } else if (iNewCell.isParallel()) {
                newSopNode = new SopNodeParallel();
            } else if (iNewCell.isAlternative()) {
                newSopNode = new SopNodeAlternative();
            } else if (iNewCell.isArbitrary()) {
                newSopNode = new SopNodeArbitrary();
            } else {
                System.out.println("iNewCell.isSOP(): " + iNewCell.isSOP());
            }
        }

        //Add to map
        if (newSopNode != null) {
            mCellSopNodeMap.put(iNewCell, newSopNode);
        }

        return newSopNode;
    }

    private ISopNode setReferenceSopNode(final Cell iReferenceCell) {
        ISopNode referenceSopNode = null;
        if (iReferenceCell != null) {
            if (mCellSopNodeMap.containsKey(iReferenceCell)) {
                referenceSopNode = mCellSopNodeMap.get(iReferenceCell);
            }
        }
        return referenceSopNode;
    }
}
