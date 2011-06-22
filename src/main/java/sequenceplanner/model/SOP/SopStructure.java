/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.model.SOP;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import sequenceplanner.view.operationView.graphextension.Cell;

/**
 *
 * @author Peter
 */
public class SopStructure implements ISopStructure {

    private SopNode sopRootNode = new SopNode();
    private Iterator firstNodes;
    private ASopNode sopNode;
    private ISopNode sopIterator;
    private Iterator specialNode;
    private Iterator specialNode2;
    //private LinkedList<ASopNode> sopStructure = new LinkedList<ASopNode>();
    private LinkedList<ISopNode> withinSops;

    /**
     * Toolbox for {@link ISopNode}s
     */
    private ISopNodeToolbox mSNToolbox = new SopNodeToolboxSetOfOperations();

    public SopStructure() {
    }

    public ISopNode getRoot() {
        return sopRootNode;
    }

    @Override
    public void setSopRoot(ASopNode newNode) {
        //First node in a Sequence
        /*if (sopRootNode == null) {
            this.sopRootNode = newNode;
        }/* else {
            System.out.println("Adding: " + sopRootNode.getUniqueId());
            this.sopNode = newNode;
        }*/
        System.out.println("wheres your head at");
        sopRootNode.addNodeToSequenceSet(newNode);
//        System.out.println("Patrik.super.out.print"+sopRootNode.toString());
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode, boolean before) {
        //Rest of the nodes
        boolean finished = false;
        //sopIterator = sopStructure.getFirst();
        //System.out.println("____________________________________");
        //for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
        firstNodes = sopRootNode.getFirstNodesInSequencesAsSet().iterator();
        //If the added node is before the root it will be the new root
        while (firstNodes.hasNext()) {
            sopIterator = (ISopNode) firstNodes.next();
            //System.out.println("Iterating through sopStructure...");
            //System.out.println("Iterating Id: " + sopIterator.getUniqueId() + "Cell Id" + cell.getUniqueId());

            //Check if the added node is first in a sequence set
            if (sopIterator.getUniqueId() == cell.getUniqueId() && before == true) {
                //System.out.println("1");
                //If so, set the old SOPNode as a successor to the new SOPNode
                sopNode.setSuccessorNode(sopIterator);
                System.out.println("New Root added! It = " + sopIterator.toString());
                //..Remove the old one as a "first node"
                firstNodes.remove(); //IS THIS LINE NEEDED? DO WE USE firstNodes ANYMORE?
                mSNToolbox.removeNode(sopIterator, sopRootNode);
                //..And set the new one as a "first node"
                sopRootNode.addNodeToSequenceSet(sopNode);
                break;

                //Check if the node is added after the first node in the sequence
            } else if (sopIterator.getUniqueId() == cell.getUniqueId() && before == false) {
                //System.out.println("2");
                //Check for successor nodes
                if (sopNode.getSuccessorNode() != null) { //THIS IS ALWAYS TRUE! NO SUCCESSOR HAS BEEN ADDED!! -> ALWYAS GO TO ELSE
                    sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                }else{
                    //A->B and add C between A and B
                    //first C->B and then A->C gives A->C->B
                    sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                    sopIterator.setSuccessorNode(sopNode);
                }
                System.out.println("Adding " + sopNode.toString() + " after " + sopIterator.toString());
            } else {
                //System.out.println("3");
                //sopIterator = sopStructure.getFirst();
                //System.out.println("Successor node: " + sopIterator.getSuccessorNode().toString());

                //Since the click wasn't on the first node, we have to go through the whole Sequence chain
                do {
                    //System.out.println("3.1");

                    //Check if the next node is the clicked node.. and add before
                    if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == true) {
                        //System.out.println("3.2");
                        sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                        System.out.println("Adding " + sopNode.toString() + " before " + sopIterator.getSuccessorNode().toString());
                        sopIterator.setSuccessorNode(sopNode);
                        break;
                    //Check if the next node is the clicked node.. and add after
                    } else if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == false) {
                        //System.out.println("3.2");
                        sopIterator = sopIterator.getSuccessorNode();
                        sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                        System.out.println("Adding " + sopNode.toString() + " after " + sopIterator.toString());
                        sopIterator.setSuccessorNode(sopNode);
                        break;
                    }
                    //}
                    //System.out.println("Iterator successor: "+sopIterator.getSuccessorNode().toString());
                    //System.out.println("4");
                    //System.out.println("sopIterator point at: " + sopIterator.toString());
                    if (sopIterator.getSuccessorNode() != null) {
                        sopIterator = sopIterator.getSuccessorNode();
                    } else {
                        finished = true;
                    }
                } while (finished == false);

            }
            //printSops();
//            System.out.println("Patrik.super.out.print"+sopRootNode.toString());
        }
//        System.out.println("Patrik.super.out.print"+sopRootNode.toString());
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode) {
        //Put a cell within a cell
        firstNodes = sopRootNode.getFirstNodesInSequencesAsSet().iterator();
        boolean finished = false;
        while (firstNodes.hasNext()) {
            //System.out.println("blabla11 ");
            while (finished == false) {
                //System.out.println("blabla22 cell Id: " + cell.getUniqueId() + " sopIterator: " + sopIterator + " getOperation: ");
                //If not operation, we either add or look deeper
                if (sopIterator.getOperation() == null) {
                    //If the clicked cell is within the iterated cell
                    //System.out.println("0000000000000000");
                    if (sopIterator.getUniqueId() == cell.getUniqueId()) {
                        System.out.println("Adding:" + sopNode.toString());
                        sopIterator.addNodeToSequenceSet(sopNode);
                        break;
                    } else if (sopIterator.getFirstNodesInSequencesAsSet().isEmpty() == false) {
                        //The cell has a set
                        specialNode = sopIterator.getFirstNodesInSequencesAsSet().iterator();


                        //System.out.println("*********SpecialNode: " + sopIterator);
                        while (specialNode.hasNext()) {
                            //System.out.println("1.");
                            sopIterator = (ISopNode) specialNode.next();
                            //System.out.println("2.");
                            if (sopIterator.getUniqueId() == cell.getUniqueId()) {
                                //sopIterator.setSuccessorRelation(4);
                                System.out.println("Adding node to an Alternative node");
                                //setRelation
                                sopIterator.addNodeToSequenceSet(sopNode);
                                break;
                            } else {
                                do {
                                    if (sopIterator.getSuccessorNode() != null) {
                                        sopIterator = sopIterator.getSuccessorNode();
                                        //System.out.println("3.");
                                        if (sopIterator.getUniqueId() == cell.getUniqueId()) {
                                            //System.out.println("4.");
                                            sopIterator.addNodeToSequenceSet(sopNode);
                                        }
                                        //System.out.println("5.");
                                    }
                                } while (sopIterator.getSuccessorNode() != null);
                            }
                        }
                        //System.out.println("6.");
                        //sopIterator = (ISopNode) specialNode.next();
                        //System.out.println("woot wooooot");
                    }
                }
                if (sopIterator.getSuccessorNode() != null) {
                    sopIterator = sopIterator.getSuccessorNode();
                }//else if (sopIterator.getFirstNodesInSequencesAsSet() != null){
                // sopIterator =
                else {
                    finished = true;
                }

            }

            if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId()) {
                //sopIterator.setSuccessorRelation(4);
                //System.out.println("blabla33");
                //setRelation
                sopIterator = sopIterator.getSuccessorNode();
                sopIterator.addNodeToSequenceSet(sopNode);
                break;
            }
            //System.out.println(sopIterator.getSuccessor().);
            firstNodes.next();
        }
        //printSops();
//        System.out.println("Patrik.super.out.print"+sopRootNode.toString());

    }

    public void printSops() {
        int sequenceCounter = 0;
        System.out.println("bagagaga");
        firstNodes = sopRootNode.getFirstNodesInSequencesAsSet().iterator();
        //If the added node is before the root it will be the new root
        while (firstNodes.hasNext()) {
            //System.out.println("1");
            sequenceCounter++;
            sopIterator = (ISopNode) firstNodes.next();
            int place = 1;
            int deeperPlace = 0;
            //System.out.println("Place " + place + ": " + sopIterator);
            System.out.println("1Sequence: " + sequenceCounter + " Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.toString());
            place++;


            boolean finished = false;
            boolean tempForDemo = false;
            withinSops = new LinkedList<ISopNode>();

            while (finished == false) {
                /*System.out.println("sopIterator point at: " + sopIterator.toString());
                if (sopIterator.getSuccessorNode() != null) {
                System.out.println(" and Successor is: " + sopIterator.getSuccessorNode().toString());
                System.out.println("And.... " + sopIterator.getSuccessorNode().getFirstNodesInSequencesAsSet().isEmpty());
                }*/
                /*if (sopIterator.getFirstNodesInSequencesAsSet().isEmpty() == true) {
                System.out.println("whaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                }*/
                //Print normal operation
                if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getFirstNodesInSequencesAsSet().isEmpty() == true) {
                    System.out.println("2Sequence: " + sequenceCounter + " Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.getSuccessorNode().toString());
                    //System.out.println("_-_-_-_-_-");
                    sopIterator = sopIterator.getSuccessorNode();
                    place++;
                } else if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getFirstNodesInSequencesAsSet().isEmpty() == false) {
                    //This is within a node
                    System.out.println("3Sequence: " + sequenceCounter + " Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.getSuccessorNode().toString());
                    //Saving the node that we have to go deeper in
                    withinSops.add(sopIterator.getSuccessorNode());
                    //if(){
                    deeperPlace++;
                    //}
                    //Iterate through the nodes within the node
                    specialNode = sopIterator.getSuccessorNode().getFirstNodesInSequencesAsSet().iterator();

                    sopIterator = (ISopNode) specialNode.next();
                    //System.out.println("~~~~~~~~SpecialNode: " + sopIterator);
                    if (specialNode.hasNext() == true) {
                        System.out.println("4Sequence: " + sequenceCounter + " Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.toString());
                    }
                } else if (sopIterator.getSuccessorNode() == null) {
                    //Checking if we're within a node
                    //System.out.println("Check if we are deep");
                    //System.out.println(withinSops.isEmpty());
                    if (tempForDemo == false && sopIterator.getFirstNodesInSequencesAsSet().isEmpty() == false) {
                        System.out.println("5Sequence: " + sequenceCounter + " Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.toString());
                        //Saving the node that we have to go deeper in
                        withinSops.add(sopIterator);

                        deeperPlace++;

                        //Iterate through the nodes within the node
                        specialNode = sopIterator.getFirstNodesInSequencesAsSet().iterator();

                        sopIterator = (ISopNode) specialNode.next();
                        //System.out.println("22222SpecialNode: " + sopIterator);
                        //System.out.println("successor: "+sopIterator.getSuccessorNode().toString());
                        //if (specialNode.hasNext() == true) {
                        // System.out.println("6Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.toString());
                        //}
                    } else if (specialNode != null && specialNode.hasNext() == true) {
                        //Printing nodes within nodes
                        //System.out.println("specialNode hasNext(): ");
                        sopIterator = (ISopNode) specialNode.next();
                        System.out.println("7Sequence: " + sequenceCounter + " Place: " + place + "." + deeperPlace + " Cell: " + sopIterator);


                    } else if (specialNode != null && specialNode.hasNext() == false && withinSops.isEmpty() == false) {
                        //We're within a node
                        //System.out.println("We're deep");
                        /*if(withinSops.getLast() != null){
                        System.out.println(withinSops.getLast().toString());
                        }*/
                        System.out.println("8Sequence: " + sequenceCounter + " Place: " + place + "." + deeperPlace + " Cell: " + sopIterator);
                        sopIterator = withinSops.getLast();
                        withinSops.removeLast();
                        deeperPlace--;
                        tempForDemo = true;
                    } else {
                        finished = true;
                    }
                }
                /*if(specialNode != null){
                System.out.println("~~~~~~"+specialNode.hasNext());
                System.out.println("I'm out!2.0: "+ deeperPlace);
                } */            }
            //System.out.println("I'm out!");
        }
    }

    @Override
    public boolean addCellToSop(Cell iReferenceCell, Cell iNewCell, boolean iBefore) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addCellToSop(Cell iReferenceCell, Cell iNewCell) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addCellToSop(Cell iNewCell) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
