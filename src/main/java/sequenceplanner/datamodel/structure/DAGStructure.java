package sequenceplanner.datamodel.structure;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.graph.*;

/**
 * The DAGStructure is used to describe the hierarchical structure of operations, 
 * products or resources. Send in the type T that will be used as nodes / vertices
 * and a edge information type W. If not edge information is needed send in null.
 * 
 * @param <T> The type of the nodes in the graph
 * 
 * @author kbe
 */
public class DAGStructure<T> {

    private SimpleDirectedGraph<T,SpEdge> graph;
    private Set<T> rootNodes;
    
    public DAGStructure() {
        graph = new SimpleDirectedGraph<T,SpEdge>(SpEdge.class);
        rootNodes = new HashSet<T>();
    }
    
    public boolean addChild(T node, T child){
        if (graph.containsVertex(node)){
            if (rootNodes.contains(child)) return false;
            graph.addVertex(child); // will only add if not in graph
            graph.addEdge(node, child);
            return true;
        }
        return false;
    } 
    
    public boolean addRootNode(T node){
        graph.addVertex(node);
        return rootNodes.add(node);
    }
    
    public Set<T> getRootNodes(){
        return rootNodes;
    }
    
    
}
