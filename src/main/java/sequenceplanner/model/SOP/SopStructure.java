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
        if (before == true) {
            sopIterator = sopStructure.getFirst();
            
            for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
                //If the added node is before the root it will be the new root
                if (it.next().getUniqueId() == cell.getUniqueId()) {
                    sopNode.setSuccessorNode(sopStructure.getFirst());
                    sopStructure.removeFirst();
                    sopStructure.addFirst(sopNode);
                }else{
                    sopIterator = sopStructure.getFirst();
                    //Go through the whole Sequence chain
                    while (sopIterator.getSuccessorNode() != null) {
                        if (sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == true) {
                            sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                            sopIterator.setSuccessorNode(sopNode);
                        }else if (sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId() && before == false) {
                            sopIterator = sopIterator.getSuccessorNode();
                            sopNode.setSuccessorNode(sopIterator.getSuccessorNode());
                            sopIterator.setSuccessorNode(sopNode);
                        }
                        sopIterator = sopIterator.getSuccessorNode();
                    }
                }
                for (ListIterator<ASopNode> it2 = sopStructure.listIterator(); it2.hasNext();) {
                    System.out.print("List: " + it2.next().toString());
                }
            }
        }
    }

    @Override
    public void setSopSequence(Cell cell, ASopNode sopNode) {
        //Lägg inuti en annan cell
        for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
            while (sopIterator.getSuccessorNode() != null) {
                if (sopIterator.getSuccessorNode().getUniqueId() == cell.getUniqueId()){
                    //sopIterator.getSuccessorNode().setSuccessorRelation(iRelation);
                    //setRelation somehow
                }
                sopIterator = sopIterator.getSuccessorNode();
            }
            it.next();
        }
    }
}
