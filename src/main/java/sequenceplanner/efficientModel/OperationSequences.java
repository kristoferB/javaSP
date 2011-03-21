/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import java.util.LinkedList;
import java.util.Set;
import org.apache.log4j.Logger;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author shoaei
 */
public class OperationSequences {
    static Logger log = Logger.getLogger(OperationSequences.class);
    private Model model = null;


    public OperationSequences(Model model){
        this.model = model;
        init();
    }

    public void run(){
        print("Here");
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
            OperationData opData = (OperationData) model.getOperationRoot().getChildAt(i).getNodeData();
            print("**********");
            print(opData.getPrecondition());
            LinkedList<LinkedList<Integer>> predecessors = opData.getPredecessors();

            for(LinkedList<Integer> l1 : predecessors){
                print("----OR----");
                for(Integer l2 : l1)
                    print(l2);
            }
        }
    }

    private void init() {

    }
    private void print(Object o){
        System.err.println(o);
    }

}
