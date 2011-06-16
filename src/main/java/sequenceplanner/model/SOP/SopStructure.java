package sequenceplanner.model.SOP;

import java.util.LinkedList;
import java.util.ListIterator;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.view.operationView.graphextension.Cell;

/**
 *
 * @author Qw4z1
 * *Till viktor*
 * Vi måste ha en Linked List för varje sekvens där ny root "Before ->Operation"
 * läggs till som ny "addFirst". Läggs en ny operation till "after" så läggs den
 * i sist i listan. Läggs en parallell eller alternativ till så måste de länkas
 * ihop i en annan lista via listan.
 *
 */
public class SopStructure implements ISopStructure {

    private ASopNode node;
    private LinkedList<LinkedList<ASopNode>> sopSeqs = new LinkedList<LinkedList<ASopNode>>();
    private LinkedList<ASopNode> sopStructure;
    private LinkedList<ASopNode> li;

    public SopStructure() {
    }
    /*public SopStructure(Cell cell, ASopNode sopNode, boolean before) {
    //If the cell exists in the sequence, the new cell should be added
    //*This is not really true, since the cell can exists within two
    //sequences in the same OpView. So have to rethink this structure*
    for (SopSequence sopSeq :sopSeqs)  {
    if (sopSeqs.contains(sopNode)) {
    sopSeqs.add(sopNode);
    } else {
    //*******Fixa till Lista!*******
    SopSequence sopSeq = new SopSequence(sopNode, cell, before);

    }
    }
    }

    @Override
    public void addNode(ISopNode node) {
    }

    @Override
    public void addNodeToRoot(ISopNode node) {
    throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addNodeToSequence(ISopNode node) {
    throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addSopSequence(SopSequence seq) {
    sopSeqs.add(seq);
    }
    //Should be a list that is returned

    public LinkedList<SopSequence> getAllSopSequences() {
    return sopSeqs;
    }*/
    //A new Operation is created -> new SopSequence

    public LinkedList getSopSequence() {
        return sopStructure;
    }

    @Override
    public void setSopSequence(ASopNode sopNode) {
        //Create new SOPList
        sopStructure = new LinkedList<ASopNode>();
        sopStructure.add(sopNode);
        sopSeqs.add(sopStructure);
        System.out.println("Sequence initiated");
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode, boolean before) {

        //sopStructure.add(sopNode);
        for (LinkedList sopSeq : sopSeqs) {
            System.out.println("First: " + sopSeq.getFirst().toString());
            System.out.println("Second:" + sopNode.toString());
            //Need to figure out how to compare
            //if (sopSeq.contains(sopNode)) {
            //If the cell is inserted before an other cell
            if (before == true) {
                for (ListIterator<ASopNode> it = sopSeq.listIterator(); it.hasNext();) {

                    //Need to figure out how to compare cell with SopNode
                    if (it.next().getClass() == SopNodeOperation.class) {
                        if (it.next().getOperation() == cell.getValue()) {
                            System.out.println("Adding Sop to list");
                            it.add(sopNode);
                            break;
                        }
                        System.out.println("Going deeper");
                        
                    }
                }
                //If the cell is inserted after an other cell
            } else if (before == false) {
                for (ListIterator<ASopNode> it = sopSeq.listIterator(); it.hasNext();) {
                    if (it.next().getOperation() == cell.getValue()) {
                        it.next();
                        it.add(sopNode);
                        break;
                    }


                }
            }



            //} else {
            //   System.out.println("Something went wrong!");
            //}
        }
    }
}
