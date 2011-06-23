package sequenceplanner.model.SOP;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;

/**
 * 
 * @author patrik
 */
public class SopStructure2 implements ISopStructure {

    private Map<Cell, ISopNode> mCellSopNodeMap;
    private ISopNode mSopNodeRoot;
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();

    public SopStructure2() {
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
