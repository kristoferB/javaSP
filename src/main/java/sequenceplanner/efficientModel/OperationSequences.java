/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
    private Model model;
    private List<TreeNode> operationList;

    public OperationSequences(Model model){
        this.model = model;
        operationList = new ArrayList<TreeNode>();
        init();
    }

    private void init() {
        // Get all operations in the model
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i){
            TreeNode op = model.getOperationRoot().getChildAt(i);
            operationList.add(op);
            if(op.getChildCount() != 0)
                queue.add(model.getOperationRoot().getChildAt(i));
        }

        while(!queue.isEmpty()){
            TreeNode node = queue.poll();
            if(node.getChildCount() != 0){
                for(int i = 0; i < node.getChildCount(); ++i){

                }

            }
        }
    }

    public void run(){
        System.err.println(operationList.size());
        //return operationList.size();
//        print("Here");
//        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
//            OperationData opData = (OperationData) model.getOperationRoot().getChildAt(i).getNodeData();
//            print("**********");
//            print(opData.getPrecondition());
//            LinkedList<LinkedList<Integer>> predecessors = opData.getPredecessors();
//
//            for(LinkedList<Integer> l1 : predecessors){
//                print("----OR----");
//                for(Integer l2 : l1)
//                    print(l2);
//            }
//        }
    }

    public int nbrOfOperation(){
        return operationList.size();
    }

    private void print(Object o){
        System.err.println(o);
    }

}
