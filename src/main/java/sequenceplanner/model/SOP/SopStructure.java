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
    private LinkedList<LinkedList<ASopNode>> sopSeqs = new LinkedList<LinkedList<ASopNode>>();
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
        boolean notRoot = false;
        if (before == true) {
            sopIterator = sopStructure.getFirst();
            System.out.println("This is sopStructureRoot: " + sopStructure.getFirst());
            //If the added node is before the root it will be the new root
            for (ListIterator<ASopNode> it = sopStructure.listIterator(); it.hasNext();) {
                if (it.next().getUniqueId() == cell.getUniqueId()) {
                    sopNode.setSuccessorNode(sopStructure.getFirst());
                    sopStructure.removeFirst();
                    sopStructure.addFirst(sopNode);
                    System.out.println("__1__");
                }else{
                    sopIterator = sopStructure.getFirst();
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

    }
}
