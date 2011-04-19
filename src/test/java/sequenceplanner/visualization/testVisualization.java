package sequenceplanner.visualization;

import java.util.HashSet;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.algorithms.visualization.RelationContainer;
import sequenceplanner.algorithms.visualization.Visualization;
import static org.junit.Assert.*;
import sequenceplanner.efaconverter.ModelParser;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.efaconverter.OperationSequencer;
import sequenceplanner.efaconverter.convertSeqToEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.general.SP;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class testVisualization {

    static SP mSP;
//    SP mSP = new SP();
    ModelParser mModelparser;
    OperationSequencer mOperationSequencer;

    public testVisualization() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        mSP = new SP();
    }

//    @Test
    public void test1() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/KristoferTASEexample.sopx");

        this.mModelparser = new ModelParser(mSP.getModel());
        this.mOperationSequencer = new OperationSequencer(mModelparser);
        Set<OpNode> tops = mOperationSequencer.sequenceOperations();

        convertSeqToEFA seqToEFA = new convertSeqToEFA(tops, mModelparser);
        SpEFAutomata automata = seqToEFA.createSpEFA();
        seqToEFA.createWmodFile(automata);
    }

//    @Test
    public void testVisualizationUsingSopNode() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/KristoferPPURivetingTASEExample_selfcontainedoperations.sopx");
//        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationTestHierarchy3.sopx");
//        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationTest.sopx");

        Visualization v = new Visualization(mSP.getModel());

        //Add operations---------------------------------------------------------
        //All operations
        SopNode allOpSet = getOperationsInModel(mSP.getModel().getOperationRoot());
        System.out.println("ALL OPERATIONS: \n" + allOpSet.toString());
        v.addOset(allOpSet);

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(1007); //op2
//        subsetIds.add(1022); //op3a
        subsetIds.add(1015); //op10
        subsetIds.add(1017); //op12
        subsetIds.add(1020); //op15
        SopNode subOpSet = getOperations(subsetIds);
        System.out.println("OPERATIONS TO VIEW: \n" + subOpSet.toString());
        assertTrue(v.addOsubset(subOpSet));

        //Operations that has to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
        finishSetIds.add(1021); //op16
        finishSetIds.add(1020); //op15
        finishSetIds.add(1010); //op5
        SopNode finishSet = getOperations(finishSetIds);
        //all operations have to finish :)
//        SopNode finishSet = getOperationsInModel(mSP.getModel().getOperationRoot());
        System.out.println("OPERATIONS THAT HAVE TO FINISH: \n" + finishSet.toString());
        assertTrue(v.addToOfinish(finishSet));
        //-----------------------------------------------------------------------
        
        //Work with data---------------------------------------------------------
//        SopNodeWithRelations snwr = v.identifyRelations();
//        assertTrue(snwr != null);
//
//        assertTrue(v.hierarchicalPartition(snwr));
//        assertTrue(v.alternativePartition(snwr));
//        assertTrue(v.arbitraryOrderPartition(snwr));
//
//        assertTrue(v.parallelPartition(snwr));
//        System.out.println("\n--------------------------------");
//        System.out.println("After partition");
//        System.out.println(snwr.getmRootSop().inDepthToString());
//        System.out.println("--------------------------------");
    }

    @Test
    public void testVisualizationAlgorithms() {
//        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/KristoferPPURivetingTASEExample_selfcontainedoperations.sopx");
//        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationAlgorithmTestFile.sopx");
        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationTestHierarchy3.sopx");

        Visualization v = new Visualization(mSP.getModel());

        //Add operations---------------------------------------------------------
        //All operations
        SopNode allOpSet = getOperationsInModel(mSP.getModel().getOperationRoot());
        System.out.println("ALL OPERATIONS: \n" + allOpSet.toString());
//        try{
            v.addOset(allOpSet);
//    }
//        catch(Exception e) {
//            System.out.println(e.toString());
//        }

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
//        //Algorithm test----
//        subsetIds.add(1006);
//        subsetIds.add(1007);
//        subsetIds.add(1008);
//        subsetIds.add(1009);
//        subsetIds.add(1010);
//        //------------------

        subsetIds.add(1006);
        subsetIds.add(1007);
        subsetIds.add(1010);
        subsetIds.add(1017);
        subsetIds.add(1025);
        subsetIds.add(1033);
        subsetIds.add(1034);
        subsetIds.add(1053);
        
        SopNode subOpSet = getOperations(subsetIds);
        System.out.println("OPERATIONS TO VIEW: \n" + subOpSet.toString());
        assertTrue(v.addOsubset(subOpSet));

        //Operations that has to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
//        //Algorithm test----
//        subsetIds.add(1006);
//        subsetIds.add(1007);
//        subsetIds.add(1010);
//        SopNode finishSet = getOperations(finishSetIds);
        //all operations have to finish :)
        SopNode finishSet = getOperationsInModel(mSP.getModel().getOperationRoot());
        System.out.println("OPERATIONS THAT HAVE TO FINISH: \n" + finishSet.toString());
        assertTrue(v.addToOfinish(finishSet));
        //-----------------------------------------------------------------------

        //Work with data---------------------------------------------------------
        RelationContainer rc = v.identifyRelations();
        assertTrue(rc != null);

        assertTrue(v.hierarchicalPartition(rc));
//        assertTrue(v.alternativePartition(snwr));
//        assertTrue(v.arbitraryOrderPartition(snwr));
//        assertTrue(v.parallelPartition(snwr));

        System.out.println("\n--------------------------------");
        System.out.println("After partition");
        System.out.println(rc.getOsubsetSopNode().inDepthToString());
        System.out.println("--------------------------------");
    }

    public SopNode getOperationsInModel(TreeNode iTree) {
        SopNode node = new SopNode();
        for (int i = 0; i < iTree.getChildCount(); ++i) {
            OperationData opData = (OperationData) iTree.getChildAt(i).getNodeData();
            ISopNodeToolbox toolbox = new SopNodeToolboxSetOfOperations();
            toolbox.createNode(opData, node);
        }
        return node;
    }

    public SopNode getOperations(Set<Integer> iSet) {
        SopNode returnSop = new SopNode();
        SopNode sop = getOperationsInModel(mSP.getModel().getOperationRoot());
        for (ISopNode node : sop.getFirstNodesInSequencesAsSet()) {
            if (node.getNodeType() instanceof OperationData) {
                OperationData opData = (OperationData) node.getNodeType();
                if (iSet.contains(opData.getId())) {
                    returnSop.addNodeToSequenceSet(node);
                }
            }
        }
        return returnSop;
    }
}
