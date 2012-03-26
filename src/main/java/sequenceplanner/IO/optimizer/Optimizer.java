package sequenceplanner.IO.optimizer;

import java.util.ArrayList;
import java.util.List;
import org.supremica.external.assemblyOptimizer.AssemblyOptimizer;
import sequenceplanner.model.Model;
import org.supremica.external.assemblyOptimizer.AssemblyStructureProtos.Operation;
import org.supremica.external.assemblyOptimizer.AssemblyStructureProtos.Resource;
import org.supremica.external.assemblyOptimizer.AssemblyStructureProtos.Variable;
import org.supremica.external.assemblyOptimizer.GenericOperation;
import org.supremica.external.assemblyOptimizer.RelationIdentifier;
import org.supremica.external.assemblyOptimizer.TheBuilder;

/**
 *
 * @author kbe
 */
public class Optimizer {
    
    private final AssemblyOptimizer optis;
    
    // Later this can be a factory inlucing more set up of different optimizers and settings.
    public static Model optimizeOperations(Model m){
        
        List<Operation> ops = ModelAssemblyOptimizerConverter.INSTANCE.convertModelToProtoOperations(m);
        List<Resource> res = ModelAssemblyOptimizerConverter.INSTANCE.convertModelToProtoResource(m);
        Optimizer optimizer = new Optimizer(ops,res);
        ModelAssemblyOptimizerConverter.INSTANCE.convertProtoOperationsToModel(m,optimizer.compute());
        
        return m;
    }   
    
    org.supremica.external.assemblyOptimizer.TheBuilder builder;
    private Optimizer(List<Operation> ops, List<Resource> res ){ 
        optis = new AssemblyOptimizer(ops,res);
        builder = new TheBuilder(ops,res);
    }
    
    private List<Operation> compute(){
        System.out.println("Start compiling");
        RelationIdentifier ri = new RelationIdentifier(builder);
        System.out.println("Start optimizing and relation building");
        ri.createRelationMap();
        return ri.getOptimizedOperationList();
//        try{        
//            System.out.println("Starting planner");
//            String[] args = {"-c","500"};
//            optis.parse(args);
//            optis.computeMinimalCost();
//        } catch (Exception e){
//            System.out.println("optimizer failes:" + e.getLocalizedMessage());
//        }
//        return optis.getOptimizedOperationList();
    }
    

    

    
    

    
    
    
}
