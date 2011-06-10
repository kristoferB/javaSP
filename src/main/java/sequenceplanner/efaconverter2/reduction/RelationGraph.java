/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2.reduction;

import java.util.LinkedList;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import sequenceplanner.model.TreeNode;

/**
 *
 * @author shoaei
 */
public class RelationGraph {
    
    private LinkedList<TreeNode> operations;
    
    public RelationGraph(LinkedList<TreeNode> operations){
        this.operations = operations;
    }
    
    public LinkedList<LinkedList<Integer>> getSequentialOperations(){
        LinkedList<LinkedList<Integer>> paths = new LinkedList<LinkedList<Integer>>();
        
        return paths;
    }
    
    private DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> getGraph(){
        DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> simple =
            new DirectedWeightedMultigraph<Integer, DefaultWeightedEdge>(
            DefaultWeightedEdge.class);
        
        // Source vertex
        simple.addVertex(0);
        // Sink vertex
        simple.addVertex(1);
        for(TreeNode op : operations){
            simple.addVertex(op.getId());
        }
        
        return simple;
        
    }
}
