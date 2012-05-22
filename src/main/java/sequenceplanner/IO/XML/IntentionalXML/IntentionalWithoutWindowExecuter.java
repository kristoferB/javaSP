/* 
   Copyright (c) 2012, Kristofer Bengtsson, Sekvensa AB, Chalmers University of Technology
   Developed with the sponsorship of the Defense Advanced Research Projects Agency (DARPA).
   Permission is hereby granted, free of charge, to any person obtaining a copy of this data, including any
   software or models in source or binary form, specifications, algorithms, and documentation (collectively
   "the Data"), to deal in the Data without restriction, including without limitation the rights to use, copy,
   modify, merge, publish, distribute, sublicense, and/or sell copies of the Data, and to permit persons to
   whom the Data is furnished to do so, subject to the following conditions:
   The above copyright notice and this permission notice shall be included in all copies or substantial
   portions of the Data.
   THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
   INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
   PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS,
   SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
   OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR
   OTHER DEALINGS IN THE DATA.
*/


package sequenceplanner.IO.XML.IntentionalXML;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sequenceplanner.IO.optimizer.Optimizer;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeEmpty;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.visualization.algorithms.VisualizationAlgorithm;

/**
 *
 * @author kbe
 */
public class IntentionalWithoutWindowExecuter implements IAlgorithmListener{
    
    Model model;
    String fileToSave;

    public IntentionalWithoutWindowExecuter(String fileToLoad, String fileToSave, boolean createSOP) {
        Set<Object> models =  new HashSet<Object>(); models.add(Model.getInstance());
        sequenceplanner.IO.XML.IntentionalXML.ParseIntentionalXML parser =
                new sequenceplanner.IO.XML.IntentionalXML.ParseIntentionalXML(fileToLoad,models);
                
        this.model = parser.getModel();
        this.fileToSave = fileToSave;
        
        // Algorithms:
        // removed since the allocation generation is not working correct 
        //createVisualizationForAllOperations();
        
        //runOptimizer();
        
        //if (createSOP){
            //CreateBooking.INSTANCE.createBookingForSeams(parser.getModel());
            //CreateBooking.INSTANCE.createBookingForResources(parser.getModel(),true);       
            createVisualizationForAllOperations();
        //}
            
        saveFile();
        
    }
    
    private void runOptimizer(){
        Optimizer.optimizeOperations(model);     
    }
    
    private void createVisualizationForAllOperations(){
        //Call Visualization Algorithm---------------------------------------
       
        VisualizationAlgorithm mVisualizationAlgorithm = new VisualizationAlgorithm("Intentional", this);

        final SopNode allOperationsNode = new SopNodeEmpty();
        final SopNode hasToFinishNode = new SopNodeEmpty();
        final SopNode operationsToViewNode = new SopNodeEmpty();

        Set<SopNodeOperation> allOpsAsNodes = convertOperationsToNodes(model);
        allOperationsNode.getFirstNodesInSequencesAsSet().addAll(allOpsAsNodes);
        operationsToViewNode.getFirstNodesInSequencesAsSet().addAll(allOpsAsNodes);
        Set<ConditionData> conditionNameToIncludeSet = new HashSet<ConditionData>();

        for (SopNodeOperation op : allOpsAsNodes){
            if (op.getOperation().hasToFinish){
                hasToFinishNode.addNodeToSequenceSet(op);
            }
            conditionNameToIncludeSet.addAll(op.getOperation().getConditions().keySet());
        }

        Set<ResourceVariableData> resources = new HashSet<ResourceVariableData>();
        for (TreeNode n : model.getAllVariables()){
            if (n.getNodeData() instanceof ResourceVariableData){
                resources.add((ResourceVariableData)n.getNodeData());
            }
        }


        //init
        final List<Object> list = new ArrayList<Object>();
        list.add(allOperationsNode);
        list.add(operationsToViewNode);
        list.add(hasToFinishNode);
        list.add(conditionNameToIncludeSet);
        list.add(resources);
        mVisualizationAlgorithm.init(list);

        //start
        mVisualizationAlgorithm.start();
    }
    
    private void createVisualizationForEachViewInModel(Model m){
        
    }
    
    private Set<SopNodeOperation> convertOperationsToNodes(Model m){
        Set<SopNodeOperation> nodes = new HashSet<SopNodeOperation>();
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                nodes.add( new SopNodeOperation((OperationData)n.getNodeData()) );
            }
        }
        
        return nodes;
    }
    
    
        @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {

        if (iList.get(0) instanceof SopNode) {
            final SopNode sopNode = (SopNode) iList.get(0);
            model.sops.add(sopNode);          
        }
        
        saveFile();
        
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        //
    }
    
    private void saveFile(){
        // save
        sequenceplanner.IO.XML.IntentionalXML.SaveIntentionalXML save=
                new sequenceplanner.IO.XML.IntentionalXML.SaveIntentionalXML(fileToSave,model);
    }


    
}
