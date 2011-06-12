/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2.reduction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.jgrapht.alg.EdmondsKarpMaximumFlow;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import sequenceplanner.efaconverter2.EFAVariables;
import sequenceplanner.efaconverter2.SpEFA.SpEFA;
import sequenceplanner.efaconverter2.SpEFA.SpEFAutomata;
import sequenceplanner.efaconverter2.SpEFA.SpVariable;
import sequenceplanner.efaconverter2.condition.ConditionElement;
import sequenceplanner.efaconverter2.condition.ConditionExpression;
import sequenceplanner.efaconverter2.condition.ConditionStatment;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author shoaei
 */
public class RelationGraph {
    
    private LinkedList<TreeNode> operations;
    private DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;
    private SpEFAutomata automata;
    private static int VERTEX_SOURCE = 0;
    private static int VERTEX_SINK = 1;
    private static double EDGE_WEIGHT = 1.0;
    private static String PROJECT_NAME = "6";    
    
    public RelationGraph(LinkedList<TreeNode> operations){
        this.operations = operations;
        this.graph = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        automata = null;
    }
    
    public RelationGraph(SpEFAutomata automata){
        this.automata = automata;
        this.graph = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        operations = null;
    }
    
    public LinkedList<LinkedList<Integer>> getSequentialPaths(){
        LinkedList<LinkedList<Integer>> allPaths = new LinkedList<LinkedList<Integer>>();
        if(operations != null)
            buildOperationGraph();
        else 
            buildAutomataGraph();
        
        while(!graph.vertexSet().isEmpty()){
            constructFlowGraph();
            Map<DefaultWeightedEdge, Double> result = calculateMaximumFlow();
            LinkedList<LinkedList<Integer>> paths = getPaths(result);
            for(LinkedList<Integer> path : paths)
                allPaths.add(path);
            cleanGraph(paths);
        }
        return allPaths;        
    }
    
    private void buildOperationGraph(){
        for(TreeNode op : operations){
            Integer id = op.getId();
            graph.addVertex(id);
            graph.addVertex(-id);
            DefaultWeightedEdge e = graph.addEdge(id, -id);
            graph.setEdgeWeight(e, EDGE_WEIGHT);
        }

        for(TreeNode op : operations){
            OperationData opData = (OperationData)op.getNodeData();
            LinkedList<LinkedList<Integer>> predecessors = opData.getPredecessors();
            for(LinkedList<Integer> predecessor : predecessors){
                if(predecessor.size() == 1){
                    DefaultWeightedEdge e = graph.addEdge(-predecessor.getFirst(), op.getId());
                    graph.setEdgeWeight(e, EDGE_WEIGHT);
                }
            }
        }
    }

    private void constructFlowGraph() {
        graph.addVertex(VERTEX_SOURCE);
        graph.addVertex(VERTEX_SINK);
        for(Integer v : graph.vertexSet()){
            if(!(v == VERTEX_SOURCE || v == VERTEX_SINK)){
                if(graph.inDegreeOf(v)==0){
                    DefaultWeightedEdge e = graph.addEdge(VERTEX_SOURCE,v);
                    graph.setEdgeWeight(e, EDGE_WEIGHT);
                }
                if(graph.outDegreeOf(v)==0){
                    DefaultWeightedEdge e = graph.addEdge(v,VERTEX_SINK);
                    graph.setEdgeWeight(e, EDGE_WEIGHT);
                }
            }
        }
    }

    private Map<DefaultWeightedEdge,Double> calculateMaximumFlow() {
        EdmondsKarpMaximumFlow<Integer, DefaultWeightedEdge> solver =
            new EdmondsKarpMaximumFlow<Integer, DefaultWeightedEdge>(graph);

        solver.calculateMaximumFlow(VERTEX_SOURCE,VERTEX_SINK);
        Map<DefaultWeightedEdge,Double> result = solver.getMaximumFlow();

        return result;
    }

    private LinkedList<LinkedList<Integer>> getPaths(Map<DefaultWeightedEdge, Double> result) {
        LinkedList<LinkedList<Integer>> paths = new LinkedList<LinkedList<Integer>>();
        for(DefaultWeightedEdge e : graph.edgesOf(VERTEX_SOURCE)){
            if(e.getWeight() == 1.0){
                LinkedList<Integer> path = new LinkedList<Integer>();
                int current = graph.getEdgeTarget(e);
                while(current != VERTEX_SINK){
                    if(!path.contains(abs(current)))
                        path.add(abs(current));
                    for(DefaultWeightedEdge ed : graph.edgesOf(current)){
                        if(ed.getWeight() == 1.0){
                            current = graph.getEdgeTarget(ed);
                        }
                    }
                }
                paths.add(path);
            }
        }
        return paths;
    }

    private int abs(int current) {
        if(current < 0)
            return -current;
        return current;
    }

    private void cleanGraph(LinkedList<LinkedList<Integer>> paths) {
        graph.removeVertex(VERTEX_SINK);
        graph.removeVertex(VERTEX_SOURCE);
        
        for(LinkedList<Integer> path : paths)
            for(Integer v : path){
                graph.removeVertex(v);
                graph.removeVertex(-v);
            }
        
        for(DefaultWeightedEdge e : graph.edgeSet())
            graph.setEdgeWeight(e, EDGE_WEIGHT);
    }

    private LinkedList<String> buildAutomataGraph() {
        LinkedList<String> map = new LinkedList<String>();
        map.add(VERTEX_SOURCE,"ReservedForSource");
        map.add(VERTEX_SINK,"ReservedForSink");
        for(SpEFA efa : automata.getAutomatons()){
            String efaName = efa.getName();
            if(!efaName.equals(PROJECT_NAME)){
                map.add(efaName);
                System.out.println(efaName);
                graph.addVertex(map.indexOf(efaName));
                graph.addVertex(-map.indexOf(efaName));
                DefaultWeightedEdge e = graph.addEdge(map.indexOf(efaName), -map.indexOf(efaName));
                graph.setEdgeWeight(e, EDGE_WEIGHT);
            }
        }
        
        for(SpEFA efa : automata.getAutomatons()){
            if (efa.getInitialLocation().getOutTransitions().isEmpty())
                return null;
            
            ConditionExpression c = efa.getInitialLocation().getOutTransitions().iterator().next().getConditionGuard();
            for(Iterator<ConditionElement> itr = c.iterator(); itr.hasNext();){
                ConditionElement e = itr.next();
                if(e.isStatment()){
                    ConditionStatment s = (ConditionStatment)e;
                    if(s.getOperator().equals(ConditionStatment.Operator.Equal)
                            && s.getValue().equals(EFAVariables.VARIABLE_FINAL_STATE) 
                            && !isVariable(s.getVariable())
                            && map.contains(s.getVariable())){
                        DefaultWeightedEdge ed = graph.addEdge(-map.indexOf(s.getVariable()), map.indexOf(efa.getName()));
                        graph.setEdgeWeight(ed, EDGE_WEIGHT);
                    }
                }
            }
        }
        
        return map;
    }

    private boolean isVariable(String variable) {
        for(SpVariable v : automata.getVariables())
            if(v.getName().equals(variable))
                return true;
        return false;
    }
}
