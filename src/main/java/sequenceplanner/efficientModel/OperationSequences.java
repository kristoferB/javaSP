/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    private List<List<String>> paths;

    public OperationSequences(Model model){
        this.model = model;
        operationList = new ArrayList<TreeNode>();
        paths = new ArrayList<List<String>>();
        init();
    }

    private void init() {
        // Get all operations in the model
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i){
            DFS(model.getOperationRoot().getChildAt(i));
        }
    }

    public void run(){        
        OperationGraph graph = new OperationGraph();
        buildGraph(graph);
        graph.calculate(0);
        paths = graph.getPaths();
    }

    public int nbrOfOperation(){
        return operationList.size();
    }

    public int nbrOfPaths(){
        return paths.size();
    }

    private void print(Object o){
        System.err.println(o);
    }

    private void DFS(TreeNode node){
        if(node.getChildCount() > 0){
            for (int i = 0; i < node.getChildCount(); ++i){
                DFS(node.getChildAt(i));
            }
        }
        operationList.add(node);
    }

    private void buildGraph(OperationGraph graph) {
        for(TreeNode op : operationList){
            graph.addState(Integer.toString(op.getId()));
        }

        for(TreeNode op : operationList){
            OperationData opData = (OperationData)op.getNodeData();
            LinkedList<LinkedList<Integer>> predecessors = opData.getPredecessors();
            for(LinkedList<Integer> predecessor : predecessors){
                if(predecessor.size() == 1)
                    graph.addEdge(Integer.toString(predecessor.iterator().next()), Integer.toString(op.getId()),
                                    Integer.toString(predecessor.iterator().next())+"to"+Integer.toString(op.getId()), 1.0);
            }
        }
    }

}
