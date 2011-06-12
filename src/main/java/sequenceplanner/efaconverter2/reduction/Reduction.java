/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2.reduction;

import java.util.LinkedList;
import sequenceplanner.efaconverter2.DefaultModelParser;
import sequenceplanner.efaconverter2.SpEFA.*;
import sequenceplanner.model.Model;

/**
 *
 * @author shoaei
 */
public class Reduction {
    
    private Model model;
        
    public Reduction(Model model){
        this.model = model;
    }
    
    public SpEFAutomata getReducedModel(){
        return reduceModel();
    }
    
    private SpEFAutomata reduceModel(){
        DefaultModelParser parser = new DefaultModelParser(model);
        SpEFAutomata automata = parser.getSpEFAutomata();
        RelationGraph graph = new RelationGraph(model.getAllOperations());
        LinkedList<LinkedList<Integer>> paths = graph.getSequentialPaths();
        
        return automata;
    }
}
