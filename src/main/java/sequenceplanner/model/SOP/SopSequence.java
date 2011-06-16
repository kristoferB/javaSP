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
public class SopSequence{

    private LinkedList<Object> SOPStructure = new LinkedList<Object>();
    private LinkedList<Object> li;

    //A new Operation is created -> new SopSequence
    public SopSequence (ASopNode sopNode){
        //Create new SOPList
    }

    public LinkedList addSopToSequence(ASopNode sopOp, Cell cell, boolean before) {

        //If the cell is inserted before an other cell
        if (before == true) {
            for (ListIterator<Object> it = SOPStructure.listIterator(); it.hasNext();) {
                if (it.next() == cell) {
                    it.add(sopOp);
                    break;
                }
                //it.next();
            }
            //If the cell is inserted after an other cell
        } else if (before == false) {
            for (ListIterator<Object> it = SOPStructure.listIterator(); it.hasNext();) {
                if (it.next().equals(cell)) {
                    //it.next();
                    it.add(sopOp);
                    break;
                }
                //it.next();

            }
        }
        return SOPStructure;
        //If the cell is inserted within an other cell
        //TODO: Eventually check this first and use the other conditions in an
        //other class to be able to have one list for each sequence

    }
    //If theres neither before or after
    public LinkedList addSopToSequence(ASopNode sopOp, Cell cell) {

        for (ListIterator<Object> it = SOPStructure.listIterator(); it.hasNext();) {
            if (it.next().equals(cell)) {
                // Todo: Test if this works
                if (it.next().getClass().getName().equals(cell.getClass().getName())) {
                    it.next();
                    it.set(new LinkedList<Object>().add(it));
                } 
                //Might not be needed whith the parallel sops etc
                else {
                    li = new LinkedList<Object>();
                    li = (LinkedList) it.next();
                    li.add(cell);
                    it.set(li);
                }
            }

        }
        //Should return as a list
       return li;
    }
    public void addSopNode(){
        //To be implemented
    }
}
