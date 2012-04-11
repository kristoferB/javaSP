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
import org.supremica.external.assemblyOptimizer.RelationIdentifier3StateOps;
import org.supremica.external.assemblyOptimizer.TheBuilder;
import sequenceplanner.visualization.algorithms.RelationContainer;

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
        ModelAssemblyOptimizerConverter.INSTANCE.convertProtoOperationsToModel(m,optimizer.computeOptimizer());
        
        return m;
    }   
    
    // Will also optimize as above
    public static void identifyRelations(Model m, RelationContainer rc){
        List<Operation> ops = ModelAssemblyOptimizerConverter.INSTANCE.convertModelToProtoOperations(m);
        List<Resource> res = ModelAssemblyOptimizerConverter.INSTANCE.convertModelToProtoResource(m);
        Optimizer optimizer = new Optimizer(ops,res);
        ModelAssemblyOptimizerConverter.INSTANCE.convertProtoOperationsToModel(m,optimizer.computeRelations());    
        ModelAssemblyOptimizerConverter.INSTANCE.convertToRelationContainer(optimizer.ri,rc,m,ops);
        
//        optimizer.computeRelations3s();
//        ModelAssemblyOptimizerConverter.INSTANCE.convertToRelationContainer(optimizer.ri3s,rc,m,ops);
    }
    
    org.supremica.external.assemblyOptimizer.TheBuilder builder;
    private Optimizer(List<Operation> ops, List<Resource> res ){ 
        optis = new AssemblyOptimizer(ops,res);
        builder = new TheBuilder(ops,res);
    }
    
    private RelationIdentifier ri;
    private RelationIdentifier returnRI(){return ri;}
    private List<Operation> computeRelations(){
        ri = new RelationIdentifier(builder,10000,2000);
        System.out.println("Start optimizing and relation building");
        ri.createRelationMap();
        return ri.getOptimizedOperationList();
    }
    
    private RelationIdentifier3StateOps ri3s;
    private RelationIdentifier3StateOps returnRI3s(){return ri3s;}
    private void computeRelations3s(){
        ri3s = new RelationIdentifier3StateOps(builder,10000,2000);
        System.out.println("Start optimizing and relation building");
        ri3s.createRelationMap();
    }
    
    private List<Operation> computeOptimizer(){
        try{        
            System.out.println("Starting planner");
            String[] args = {"-c","5000"};
            optis.parse(args);
            optis.computeMinimalCost();
        } catch (Exception e){
            System.out.println("optimizer failes:" + e.getLocalizedMessage());
        }
        return optis.getOptimizedOperationList();
    }
    
    
    

    

    
    

    
    
    
}
