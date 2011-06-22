/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.model.SOP;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;

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
    private ISopNode specialNodeSaved;
    private boolean before, after;
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
        sopRootNode.addNodeToSequenceSet(newNode);
        firstNodes = sopRootNode.getFirstNodesInSequencesAsSet().iterator();
        //System.out.println("Sop Structure: "+sopRootNode.toString()+"\n_____________________________________");
        System.out.println("First2: "+firstNodes.next());
        printSops();
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode, boolean before) {
        this.before = before;
        if(before == false){
            after = true;
        }else after = false;
        setSopSequence(cell, sopNode);
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode) {
        //Put a cell within a cell
        firstNodes = sopRootNode.getFirstNodesInSequencesAsSet().iterator();

        boolean finished = false;
        do {
            sopIterator = (ISopNode) firstNodes.next();
            while (finished == false) {
                //If not operation, we either add or look deeper
                if (sopIterator.getOperation() == null) {
                    //If the clicked cell is within the iterated cell
                    if (sopIterator.getUniqueId() == cell.getUniqueId()&& before == false && after == false) {
                        System.out.println("Adding:" + sopNode.toString());
                        sopIterator.addNodeToSequenceSet(sopNode);
                        break;
                    } else if (sopIterator.getFirstNodesInSequencesAsSet().isEmpty() == false) {
                        //The cell has a set
                        specialNodeSaved = sopIterator;
                        specialNode = sopIterator.getFirstNodesInSequencesAsSet().iterator();

                        while (specialNode.hasNext()) {
                            sopIterator = (ISopNode) specialNode.next();

                            if (sopIterator.getUniqueId() == cell.getUniqueId()) {
                                if(before == true){
                                    sopNode.setSuccessorNode(sopIterator);
                                    specialNodeSaved.getFirstNodesInSequencesAsSet().remove(sopIterator);
                                    specialNodeSaved.addNodeToSequenceSet(sopNode);
                                }else if(after == true){
                                    if (sopNode.getSuccessorNode() != null) {
                                        sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                                    } else {
                                        sopIterator.setSuccessorNode(sopNode);
                                    }
                                    System.out.println("Adding " + sopNode.toString() + " after " + sopIterator.toString());
                                    break;
                                }else{
                                    System.out.println("Adding node to an Alternative node");
                                    sopIterator.addNodeToSequenceSet(sopNode);
                                }
                                break;
                            } else {
                                while (sopIterator.getSuccessorNode() != null) {
                                        sopIterator = sopIterator.getSuccessorNode();
                                        if (sopIterator.getUniqueId() == cell.getUniqueId() && before == true) {
                                            //If so, set the old one as a successor
                                            sopNode.setSuccessorNode(sopIterator);
                                            System.out.println("New Root added! It = " + sopIterator.toString());
                                            //..Remove the old one as a "first node"
                                            sopRootNode.getFirstNodesInSequencesAsSet().remove(sopIterator);
                                            //..And set the new one as a "first node"
                                            sopRootNode.addNodeToSequenceSet(sopNode);
                                            break;
                                        } else if (sopIterator.getUniqueId() == cell.getUniqueId() && after == true) {
                                            //Check for successor nodes
                                            if (sopNode.getSuccessorNode() != null) {
                                                sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                                            } else {
                                                sopIterator.setSuccessorNode(sopNode);
                                            }
                                            System.out.println("Adding " + sopNode.toString() + " after " + sopIterator.toString());
                                            break;
                                        }
                                } 
                            }
                        }
                    }
                }

                else if (sopIterator.getUniqueId() == cell.getUniqueId() && before == true && sopRootNode.getFirstNodesInSequencesAsSet().contains(sopIterator)) {
                    //If so, set the old one as a successor
                    sopNode.setSuccessorNode(sopIterator);
                    System.out.println("New Root added! It = " + sopNode.toString());
                    //..Remove the old one as a "first node"
                    sopRootNode.getFirstNodesInSequencesAsSet().remove(sopIterator);
                    //..And set the new one as a "first node"
                    sopRootNode.addNodeToSequenceSet(sopNode);
                    break;

                }else if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == true) {
                        sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                        System.out.println("Adding " + sopNode.toString() + " before " + sopIterator.getSuccessorNode().toString());
                        sopIterator.setSuccessorNode(sopNode);
                        break;
                }else if (sopIterator.getUniqueId() == cell.getUniqueId() && after == true) {
                    //Check for successor nodes
                    if (sopNode.getSuccessorNode() != null) {
                        sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                    } else {
                        sopIterator.setSuccessorNode(sopNode);

                    }
                    System.out.println("Adding " + sopNode.toString() + " after " + sopIterator.toString());
                    //reset
                    after=false;
                    break;

                }

                if (sopIterator.getSuccessorNode() != null) {
                    sopIterator = sopIterator.getSuccessorNode();
                }
                else {
                    finished = true;
                }

            }

            if (sopIterator.getSuccessorNode() != null && sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId()&&sopIterator.getFirstNodesInSequencesAsSet().isEmpty()==false) {
                sopIterator = sopIterator.getSuccessorNode();
                sopIterator.addNodeToSequenceSet(sopNode);
                break;
            }
            
        }while (firstNodes.hasNext());
        printSops();
        System.out.println("Sop Structure: "+sopRootNode.toString()+"\n_____________________________________");
    }

    public void deleteNode(ASopNode sopNode) {
    }

    public void printSops() {
        int sequenceCounter = 0;
        
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

    @Override
    public boolean updateSopNode(SPGraph iSpGraph) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
