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

    private ASopNode sopRootNode;
    private ASopNode sopNode;
    private ISopNode sopIterator;
    private Iterator specialNode;
    private Iterator specialNode2;
    private LinkedList<ASopNode> sopStructure = new LinkedList<ASopNode>();
    private LinkedList<ISopNode> withinSops;

    public SopStructure() {
    }

    public ISopNode getRoot(){
        return sopRootNode;
    }
    @Override
    public void setSopRoot(ASopNode sopRootNode) {
        //First node in a Sequence
        this.sopRootNode = sopRootNode;
        System.out.println("Adding: " + sopRootNode.getUniqueId());
        sopStructure.add(sopRootNode);
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode, boolean before) {
        //Rest of the nodes
         boolean finished = false;
        sopIterator = sopStructure.getFirst();
        //System.out.println("____________________________________");
        for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
            //If the added node is before the root it will be the new root
            sopIterator = it.next();
            //System.out.println("wagege "+ sopIterator);
            //System.out.println("Iterating through sopStructure...");

            //System.out.println("Iterating Id: " + sopIterator.getUniqueId() + "Cell Id" + cell.getUniqueId());
            if (sopIterator.getUniqueId() == cell.getUniqueId() && before == true) {
                //System.out.println("1");
                sopNode.setSuccessorNode(sopIterator);
                System.out.println("New Root added! It = " + sopIterator.toString());
                //it.next();
                it.set(sopNode);

                break;
                //sopStructure.addLast(sopNode);
            } else if (sopIterator.getUniqueId() == cell.getUniqueId() && before == false) {
                //System.out.println("2");
                if (sopNode.getSuccessorNode() != null) {
                    sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                }
                System.out.println("Adding " + sopNode.toString() + " after " + sopIterator.toString());
                sopIterator.setSuccessorNode(sopNode);

            } else {
                //System.out.println("3");
                //sopIterator = sopStructure.getFirst();
                //Go through the whole Sequence chain
                //System.out.println("Successor node: " + sopIterator.getSuccessorNode().toString());
                do {
                    
                    //System.out.println("3.1");
                    //if (it.hasNext()) {

                        //System.out.println(" sot...");
                        if (sopIterator.getSuccessorNode()!= null &&sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == true) {
                            //System.out.println("3.2");
                            sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                            System.out.println("Adding " + sopNode.toString() + " before " + sopIterator.getSuccessorNode().toString());
                            sopIterator.setSuccessorNode(sopNode);
                            break;
                        } else if (sopIterator.getSuccessorNode()!= null &&sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == false) {
                            //System.out.println("3.2");
                            sopIterator = sopIterator.getSuccessorNode();
                            sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                            System.out.println("Adding " + sopNode.toString() + " after " + sopIterator.toString());
                            sopIterator.setSuccessorNode(sopNode);
                            break;
                        }
                        //sopIterator = it.next();
                    //}
                    //System.out.println("Iterator successor: "+sopIterator.getSuccessorNode().toString());
                    //System.out.println("4");
                    //System.out.println("sopIterator point at: " + sopIterator.toString());
                    if(sopIterator.getSuccessorNode() != null){
                        sopIterator = sopIterator.getSuccessorNode();
                    }else{
                        finished = true;
                    }
                } while (finished == false);
            }
        }
        printSops();

    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode) {
        //Put a cell within a cell
        sopIterator = sopStructure.getFirst();
        boolean finished = false;
        for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
            //System.out.println("blabla11 ");
            while (finished == false) {
                //System.out.println("blabla22 cell Id: " + cell.getUniqueId() + " sopIterator: " + sopIterator + " getOperation: ");
                //If not operation, we either add or look deeper
                if (sopIterator.getOperation() == null) {
                    //If the clicked cell is within the iterated cell
                    //System.out.println("0000000000000000");
                    if(sopIterator.getUniqueId() == cell.getUniqueId()){
                        System.out.println("Adding:" + sopNode.toString());
                        sopIterator.addNodeToSequenceSet(sopNode);
                        break;
                    }else if(sopIterator.getFirstNodesInSequencesAsSet().isEmpty() == false){
                    //The cell has a set
                    specialNode = sopIterator.getFirstNodesInSequencesAsSet().iterator();

                    
                    //System.out.println("*********SpecialNode: " + sopIterator);
                    while (specialNode.hasNext()) {
                        //System.out.println("1.");
                         sopIterator = (ISopNode) specialNode.next();
                            //System.out.println("2.");
                            if (sopIterator.getUniqueId() == cell.getUniqueId()) {
                                //sopIterator.setSuccessorRelation(4);
                                //System.out.println("^^^^^^^^^^^^^^^^");
                                //setRelation
                                sopIterator.addNodeToSequenceSet(sopNode);
                                break;
                            } else {
                                do {
                                    if(sopIterator.getSuccessorNode() != null){
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
            it.next();
        }
        printSops();
    }

    public void printSops() {
        int sequenceCounter = 0;
        for (ListIterator<ASopNode> it2 = sopStructure.listIterator(); it2.hasNext();) {
            //System.out.println("1");
            sequenceCounter++;
            sopIterator = it2.next();
            int place = 1;
            int deeperPlace = 0;
            //System.out.println("Place " + place + ": " + sopIterator);
            System.out.println("1Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.toString());
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
                    System.out.println("2Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.getSuccessorNode().toString());
                    //System.out.println("_-_-_-_-_-");
                    sopIterator = sopIterator.getSuccessorNode();
                    place++;
                } else if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getFirstNodesInSequencesAsSet().isEmpty() == false) {
                    //This is within a node
                    System.out.println("3Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.getSuccessorNode().toString());
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
                        System.out.println("4Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.toString());
                    }
                } else if (sopIterator.getSuccessorNode() == null) {
                    //Checking if we're within a node
                    //System.out.println("Check if we are deep");
                    //System.out.println(withinSops.isEmpty());
                    if (tempForDemo == false &&sopIterator.getFirstNodesInSequencesAsSet().isEmpty() == false) {
                        System.out.println("5Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator.toString());
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
                        System.out.println("7Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator);

                        
                    } else if (specialNode != null && specialNode.hasNext() == false && withinSops.isEmpty() == false) {
                        //We're within a node
                        //System.out.println("We're deep");
                        /*if(withinSops.getLast() != null){
                        System.out.println(withinSops.getLast().toString());
                        }*/
                        System.out.println("8Sequence: "+sequenceCounter +" Place: " + place + "." + deeperPlace + " Cell: " + sopIterator);
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
}
