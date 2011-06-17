/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.model.SOP;

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
    private LinkedList<ASopNode> sopStructure = new LinkedList<ASopNode>();
    private LinkedList<ASopNode> withinSops;

    public SopStructure() {
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

        sopIterator = sopStructure.getFirst();
        System.out.println("____________________________________");
        for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
            //If the added node is before the root it will be the new root

            System.out.println("Iterating through sopStructure...");
            
            System.out.println("Iterating Id: " + sopIterator.getUniqueId() + "Cell Id" + cell.getUniqueId() );
            if (sopIterator.getUniqueId() == cell.getUniqueId() && before == true) {
                sopNode.setSuccessorNode(sopIterator);
                System.out.println("New Root added!");
                it.set(sopNode);
            }else if(sopIterator.getUniqueId() == cell.getUniqueId() && before == false){
                if(sopNode.getSuccessorNode() != null){
                    sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                }
                System.out.println("Adding "+sopNode.toString()+" after "+ sopIterator.toString());
                sopIterator.setSuccessorNode(sopNode);

            } else{
                //sopIterator = sopStructure.getFirst();
                //Go through the whole Sequence chain
                System.out.println("Successor node: " + sopIterator.getSuccessorNode().toString());
                 do{
                    if (sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == true) {
                        sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                        System.out.println("Adding " + sopNode.toString() +" before "+sopIterator.getSuccessorNode().toString());
                        sopIterator.setSuccessorNode(sopNode);
                        break;
                    } else if (sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == false) {
                        sopIterator = sopIterator.getSuccessorNode();
                        sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                        System.out.println("Adding " + sopNode.toString() +" after "+sopIterator.toString());
                        sopIterator.setSuccessorNode(sopNode);
                        break;
                    }
                     //System.out.println("Iterator successor: "+sopIterator.getSuccessorNode().toString());
                     System.out.println("sopIterator point at: "+sopIterator.toString());
                     //System.out.println("sopNode point at: "+sopNode.toString());
                    sopIterator = sopIterator.getSuccessorNode();
                }while (sopIterator.getSuccessorNode() != null);
            }
            sopIterator = it.next();
        }
        for (ListIterator<ASopNode> it2 = sopStructure.listIterator(); it2.hasNext();) {
            sopIterator = it2.next();
            int place = 1;
            System.out.println("Place " + place + ": " + sopIterator);
            place++;
            while (sopIterator.getSuccessorNode() != null) {
                System.out.println("Place " + place + ": " + sopIterator.getSuccessorNode().toString());
                sopIterator = sopIterator.getSuccessorNode();
                place++;
            }
            
            System.out.println("I'm out!2.0");
        }
        System.out.println("I'm out!");

    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode) {
        //Lägg inuti en annan cell
        sopIterator = sopStructure.getFirst();

        for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
            System.out.println("blabla11 ");
            while (sopIterator.getSuccessorNode() != null) {
                System.out.println("blabla22 ");
                if (sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId()) {
                    //sopIterator.setSuccessorRelation(4);
                    System.out.println("blabla: " + sopIterator.getSuccessorRelation());
                    //setRelation

                }
                sopIterator = sopIterator.getSuccessorNode();
            }
            //System.out.println(sopIterator.getSuccessor().);
            it.next();
        }
    }
}
