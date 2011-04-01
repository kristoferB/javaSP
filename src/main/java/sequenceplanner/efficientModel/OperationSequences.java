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
import sequenceplanner.model.data.OperationData.Action;

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
        for(TreeNode node : operationList)
            printGuards(node);

//        DialogEM dialogem = new DialogEM();
//        dialogem.createAndShow();
        //OperationGraph graph = new OperationGraph();
        //buildGraph(graph);
        // mode 0: Save no graph
        // mode 1: Save system graph (iteration zero)
        // mode 2: Saved each iteration graph
        //int mode = 0;
        //graph.calculate(mode);
        //paths = graph.getPaths();
        //createOperationView("Name of view", paths);
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

    private void createOperationView(String string, List<List<String>> paths) {

    }

    private void printGuards(TreeNode node){
        OperationData opData = (OperationData)node.getNodeData();

        print("===== "+node.getId()+" =====");
        String rawPrecondition = opData.getRawPrecondition();
        print(rawPrecondition);
//        String action = "Action: ";
//
//        LinkedList<Action> actions = opData.getActions();
//        for(Action a : actions)
//            action  += a.id+Model.getActionSetType(a.state)+a.value+"; ";
//
//        opData.getSequenceCondition();
    }
}
