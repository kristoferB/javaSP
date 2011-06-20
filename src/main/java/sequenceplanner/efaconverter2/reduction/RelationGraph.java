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
import sequenceplanner.efaconverter2.condition.ConditionOperator;
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
    private LinkedList<String> map;
    private static int VERTEX_SOURCE = 0;
    private static int VERTEX_SINK = 1;
    private static double EDGE_WEIGHT = 1.0;
    private static String PROJECT_NAME = "6";    
    
    public RelationGraph(LinkedList<TreeNode> operations){
        this.operations = operations;
        this.graph = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.map = new LinkedList<String>();
        map.add(VERTEX_SOURCE,"ReservedForSource");
        map.add(VERTEX_SINK,"ReservedForSink");                
        automata = null;
    }
    
    public RelationGraph(SpEFAutomata automata){
        this.automata = automata;
        this.graph = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.map = new LinkedList<String>();
        map.add(VERTEX_SOURCE,"ReservedForSource");
        map.add(VERTEX_SINK,"ReservedForSink");                        
        operations = null;
    }
    
    public LinkedList<LinkedList<String>> getSequentialPaths(){
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
        
        return unmap(allPaths);        
    }

    private void buildOperationGraph(){
        for(TreeNode op : operations){
            map.add(Integer.toString(op.getId()));
            Integer v = map.indexOf(Integer.toString(op.getId()));
            graph.addVertex(v);
            graph.addVertex(-v);
            DefaultWeightedEdge e = graph.addEdge(v, -v);
            graph.setEdgeWeight(e, EDGE_WEIGHT);
        }

        for(TreeNode op : operations){
            OperationData opData = (OperationData)op.getNodeData();
            LinkedList<LinkedList<Integer>> predecessors = opData.getPredecessors();
            for(LinkedList<Integer> predecessor : predecessors){
                if(predecessor.size() == 1 
                        && map.contains(Integer.toString(predecessor.getFirst())) 
                        && map.contains(Integer.toString(op.getId()))){
                    DefaultWeightedEdge e = graph.addEdge(-map.indexOf(Integer.toString(predecessor.getFirst())), map.indexOf(Integer.toString(op.getId())));
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
        System.out.println("##########");
        for(DefaultWeightedEdge x : graph.edgeSet())
            System.out.println("Edge: <"+ map.get(abs(graph.getEdgeSource(x))) +","+map.get(abs(graph.getEdgeTarget(x))) +","+graph.getEdgeWeight(x)+"> --- Weight: "+result.get(x));
        
        LinkedList<LinkedList<Integer>> paths = new LinkedList<LinkedList<Integer>>();
        for(DefaultWeightedEdge e : graph.edgesOf(VERTEX_SOURCE)){
            if(result.get(e) == EDGE_WEIGHT){
                LinkedList<Integer> path = new LinkedList<Integer>();
                int current = graph.getEdgeTarget(e);
                while(current != VERTEX_SINK){
                    if(!path.contains(abs(current)))
                        path.add(abs(current));
                    for(DefaultWeightedEdge ed : graph.edgesOf(current)){
                        if(result.get(ed) == EDGE_WEIGHT){
                            current = graph.getEdgeTarget(ed);
                        }
                    }
                }
                paths.add(path);
            }
        }
        return paths;
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

    private void buildAutomataGraph() {
        for(SpEFA efa : automata.getAutomatons()){
            String efaName = efa.getName();
            
//            if(efaName.equals(PROJECT_NAME))
//                continue;
            
            map.add(efaName);
            System.out.println("Vertex Added: " + efa.getName());
            Integer v = map.indexOf(efaName);
            graph.addVertex(v);
            graph.addVertex(-v);
            DefaultWeightedEdge e = graph.addEdge(v, -v);
            graph.setEdgeWeight(e, EDGE_WEIGHT);
        }
        
        for(SpEFA efa : automata.getAutomatons()){
            if (efa.getInitialLocation().getOutTransitions().isEmpty())
                continue;
            
            ConditionExpression c = efa.getInitialLocation().getOutTransitions().iterator().next().getConditionGuard();
            for(ConditionElement e : c.getAllConditionStatments()){
                if((e.getNextOperator() != null && e.getNextOperator().isOperationType(ConditionOperator.Type.OR)) 
                        || (e.getPreviousOperator() != null && e.getPreviousOperator().isOperationType(ConditionOperator.Type.OR)))
                    continue;
                
                ConditionStatment s = (ConditionStatment)e;
                if(s.getOperator().equals(ConditionStatment.Operator.Equal)
                        && s.getValue().equals(EFAVariables.VARIABLE_FINAL_STATE) 
                        && !isVariable(s.getVariable())
                        && map.contains(s.getVariable())
                        && map.contains(efa.getName())
                        && graph.containsVertex(-map.indexOf(s.getVariable()))
                        && graph.containsVertex(map.indexOf(efa.getName()))){
                    DefaultWeightedEdge ed = graph.addEdge(-map.indexOf(s.getVariable()), map.indexOf(efa.getName()));
                    graph.setEdgeWeight(ed, EDGE_WEIGHT);
                }
            }
            
//            for(Iterator<ConditionElement> itr = c.iterator(); itr.hasNext();){
//                ConditionElement e = itr.next();
//                if(e.isStatment()){
//                    ConditionStatment s = (ConditionStatment)e;
//                    if((s.getOperator().equals(ConditionStatment.Operator.Equal) || s.getOperator().equals(ConditionStatment.Operator.GreaterEq))
//                            && s.getValue().equals(EFAVariables.VARIABLE_FINAL_STATE) 
//                            && !isVariable(s.getVariable())
//                            && map.contains(s.getVariable())){
//                        DefaultWeightedEdge ed = graph.addEdge(-map.indexOf(s.getVariable()), map.indexOf(efa.getName()));
//                        graph.setEdgeWeight(ed, EDGE_WEIGHT);
//                    }
//                }
//            }
        }
    }

    private boolean isVariable(String variable) {
        for(SpVariable v : automata.getVariables())
            if(v.getName().equals(variable))
                return true;
        return false;
    }

    private int abs(int current) {
        if(current < 0)
            return -current;
        return current;
    }

    private LinkedList<LinkedList<String>> unmap(LinkedList<LinkedList<Integer>> allPaths) {
        LinkedList<LinkedList<String>> result = new LinkedList<LinkedList<String>>();
        for(LinkedList<Integer> path : allPaths){
            LinkedList<String> p = new LinkedList<String>();
            for(Integer v : path){
                p.add(map.get(v));
            }
            result.add(p);
        }
        return result;
    }
  
}
