package sequenceplanner.visualization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.algorithms.visualization.RelationContainer;
import sequenceplanner.algorithms.visualization.RelationIdentification;
import sequenceplanner.algorithms.visualization.RelationPartition;
import sequenceplanner.algorithms.visualization.PerformVisualization;
import static org.junit.Assert.*;
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
    static PerformVisualization mVisualization;

    public testVisualization() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        mSP = new SP();
    }

    @Before
    public void setUpMethod() throws Exception {
        //New PerformVisualization
        mVisualization = new PerformVisualization("C:/Users/patrik/Desktop/beforeSynthesis.wmod");

        //Add all operations to Oset
//        ISopNode allOpSet = getOperationsInModel(mSP.getModel().getOperationRoot());
//        mVisualization.addOset(allOpSet);
    }

    /**
     * Test of: Arbitrary order and alternative
     */
//    @Test
    public void test1() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/visualizationAlgorithmTestFile.sopx");

        //Add operations to Oset
        Set<Integer> setIds = new HashSet<Integer>();
        setIds.add(2006);
        setIds.add(2007);
        setIds.add(2008);
        setIds.add(2009);
        setIds.add(2010);
        ISopNode allOpSet = getOperations(setIds);
        mVisualization.addOset(allOpSet);

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(2006);
        subsetIds.add(2007);
        subsetIds.add(2008);
        subsetIds.add(2009);
        subsetIds.add(2010);
        ISopNode subOpSet = getOperations(subsetIds);
        assertTrue(mVisualization.addOsubset(subOpSet));

        //Operations that have to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
        finishSetIds.add(2006);
        finishSetIds.add(2007);
        finishSetIds.add(2010);
        ISopNode finishSet = getOperations(finishSetIds);
        assertTrue(mVisualization.addToOfinish(finishSet));

        workWithAddedData();

    }

    /**
     * The example in the Kristofer's TASE paper.<br/>
     * Sequence Planning with Multiple and Coordinated Sequences of Operations
     */
//    @Test
    public void KristoferPPURivetingTASEExample_selfcontainedoperations() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/KristoferPPURivetingTASEExample_selfcontainedoperations.sopx");

        //Add operations---------------------------------------------------------
        //All operations
        ISopNode allOpSet = getOperationsInModel(mSP.getModel().getOperationRoot());
        System.out.println("ALL OPERATIONS: \n" + allOpSet.toString());
        mVisualization.addOset(allOpSet);

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(1007); //op2
        subsetIds.add(1022); //op3a
        subsetIds.add(1015); //op10
        subsetIds.add(1017); //op12
        subsetIds.add(1020); //op15
        ISopNode subOpSet = getOperations(subsetIds);
        System.out.println("OPERATIONS TO VIEW: \n" + subOpSet.toString());
        assertTrue(mVisualization.addOsubset(subOpSet));

        //Operations that has to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
        finishSetIds.add(1010); //op5
        finishSetIds.add(1015); //op10
        finishSetIds.add(1020); //op15
        ISopNode finishSet = getOperations(finishSetIds);
        System.out.println("OPERATIONS THAT HAVE TO FINISH: \n" + finishSet.toString());
        assertTrue(mVisualization.addToOfinish(finishSet));
        //-----------------------------------------------------------------------

        workWithAddedData();
    }

    /**
     * Test of: Hierarchy
     */
//    @Test
    public void test2() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/visualizationAlgorithmTestFile.sopx");

        //Add operations to Oset
        Set<Integer> setIds = new HashSet<Integer>();
        setIds.add(1006);
        setIds.add(1007);
        setIds.add(1008);
        setIds.add(1009);
        setIds.add(1010);
        setIds.add(1017);
        setIds.add(1025);
        setIds.add(1033);
        setIds.add(1034);
        setIds.add(1053);
        ISopNode allOpSet = getOperations(setIds);
        mVisualization.addOset(allOpSet);

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(1006);
        subsetIds.add(1007);
        subsetIds.add(1010);
        subsetIds.add(1008);
        subsetIds.add(1009);
        subsetIds.add(1017);
        subsetIds.add(1025);
        subsetIds.add(1033);
        subsetIds.add(1034);
        subsetIds.add(1053);
        ISopNode subOpSet = getOperations(subsetIds);
        assertTrue(mVisualization.addOsubset(subOpSet));

        //Operations that have to finish, all operations have to finish :)
        //Operations that have to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
        finishSetIds.add(1006);
        finishSetIds.add(1007);
        finishSetIds.add(1008);
        finishSetIds.add(1009);
        finishSetIds.add(1010);
        finishSetIds.add(1017);
        finishSetIds.add(1025);
        finishSetIds.add(1033);
        finishSetIds.add(1034);
        finishSetIds.add(1053);
        ISopNode finishSet = getOperations(finishSetIds);
        assertTrue(mVisualization.addToOfinish(finishSet));
        //-----------------------------------------------------------------------

        workWithAddedData();
    }

    /**
     * Test of: grouping in {@link RelationPartition}
     */
//    @Test
    public void test3() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/visualizationAlgorithmTestFile.sopx");

        //Add operations to Oset
        Set<Integer> setIds = new HashSet<Integer>();
        setIds.add(1104);
        setIds.add(1105);
        setIds.add(1106);
        setIds.add(1107);
        ISopNode allOpSet = getOperations(setIds);
        mVisualization.addOset(allOpSet);

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(1104);
        subsetIds.add(1105);
        subsetIds.add(1106);
        subsetIds.add(1107);
        ISopNode subOpSet = getOperations(subsetIds);
        assertTrue(mVisualization.addOsubset(subOpSet));

        //Operations that have to finish, all operations have to finish :)
        //Operations that have to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
        finishSetIds.add(1104);
        finishSetIds.add(1105);
        finishSetIds.add(1106);
        finishSetIds.add(1107);
        ISopNode finishSet = getOperations(finishSetIds);
        assertTrue(mVisualization.addToOfinish(finishSet));
        //-----------------------------------------------------------------------

        workWithAddedData();
    }

    /**
     * Test of: node resolving after relation partition
     */
//    @Test
    public void test4() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/visualizationAlgorithmTestFile.sopx");

        //Add operations to Oset
        Set<Integer> setIds = new HashSet<Integer>();
        setIds.add(1108);
        setIds.add(1109);
        setIds.add(1110);
        setIds.add(1111);
        ISopNode allOpSet = getOperations(setIds);
        mVisualization.addOset(allOpSet);

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(1108);
        subsetIds.add(1109);
        subsetIds.add(1110);
        subsetIds.add(1111);
        ISopNode subOpSet = getOperations(subsetIds);
        assertTrue(mVisualization.addOsubset(subOpSet));

        //Operations that have to finish, all operations have to finish :)
        //Operations that have to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
        finishSetIds.add(1111);
        ISopNode finishSet = getOperations(finishSetIds);
        assertTrue(mVisualization.addToOfinish(finishSet));
        //-----------------------------------------------------------------------

        workWithAddedData();
    }

    /**
     * Test of: How many operations can the PerformVisualization methods handle?.<br/>
     * Either all operations in parallel or all operaitons in straigt sequence.<br/>
     * For straigt sequence: >200 operations without problem
     * For parallel: >9 operations
     * For parallel: 10 operations -> 4e5 transitions to go through in {@link RelationIdentification}...
     */
//    @Test
    public void test5() {
        int nbrOfOperations = 50;
        boolean withSequencePrecondition = true; //operations are in parallel if false

        final ISopNode sop = new SopNode();

        //First operation
        final OperationData firstOp = mSP.insertOperation();
        new SopNodeToolboxSetOfOperations().createNode(firstOp, sop);

        //Include nbrOfOperations nbr of operations.
        for (; nbrOfOperations > 0; --nbrOfOperations) {
            final OperationData opData = mSP.insertOperation();
            if (withSequencePrecondition) {
                //Add precondition that operation with id-1 has to be finished
                final OperationData.SeqCond sq = new OperationData.SeqCond(opData.getId() - 1, 2);
                final LinkedList<LinkedList<OperationData.SeqCond>> llAND = new LinkedList<LinkedList<OperationData.SeqCond>>();
                final LinkedList<OperationData.SeqCond> llOR = new LinkedList<OperationData.SeqCond>();
                llOR.add(sq);
                llAND.add(llOR);
                opData.setSequenceCondition(llAND);
            }
            new SopNodeToolboxSetOfOperations().createNode(opData, sop);
        }

        mVisualization.addOset(sop);
        assertTrue(mVisualization.addOsubset(sop)); //View all operations
        assertTrue(mVisualization.addToOfinish(sop)); //All operations have to finish

        workWithAddedData();
    }

    /**
     * Test of: complex operation conditions
     */
    @Test
    public void test6() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/visualizationAlgorithmTestFile.sopx");

        //Add operations to Oset
        Set<Integer> setIds = new HashSet<Integer>();
        setIds.add(2106);
        setIds.add(2107);
        setIds.add(2108);
        setIds.add(2109);
        setIds.add(2110);
        setIds.add(2111);
        setIds.add(2112);
        setIds.add(2113);
        setIds.add(2114);
        setIds.add(2115);
        setIds.add(2116);
        setIds.add(2117);
        setIds.add(2118);
        setIds.add(2119);
        ISopNode allOpSet = getOperations(setIds);
        mVisualization.addOset(allOpSet);

        //Operations to view
        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(2106);
        subsetIds.add(2107);
        subsetIds.add(2108);
        subsetIds.add(2109);
        subsetIds.add(2110);
        subsetIds.add(2111);
        subsetIds.add(2112);
        subsetIds.add(2113);
        subsetIds.add(2114);
        subsetIds.add(2115);
        subsetIds.add(2116);
        subsetIds.add(2117);
        subsetIds.add(2118);
        subsetIds.add(2119);
        ISopNode subOpSet = getOperations(subsetIds);
        assertTrue(mVisualization.addOsubset(subOpSet));

        //Operations that have to finish, all operations have to finish :)
        //Operations that have to finish
        Set<Integer> finishSetIds = new HashSet<Integer>();
        ISopNode finishSet = getOperations(finishSetIds);
        assertTrue(mVisualization.addToOfinish(finishSet));
        //-----------------------------------------------------------------------

        workWithAddedData();
    }

    private static void workWithAddedData() {
        RelationContainer rc = mVisualization.identifyRelations();
        assertTrue(rc != null);

        assertTrue(mVisualization.hierarchicalPartition(rc));
        assertTrue(mVisualization.alternativePartition(rc));
        assertTrue(mVisualization.arbitraryOrderPartition(rc));
        assertTrue(mVisualization.parallelPartition(rc));
        assertTrue(mVisualization.sequenceing(rc));

        System.out.println("\n--------------------------------");
        System.out.println("After partition");
        System.out.println(rc.getOsubsetSopNode().toString());
        System.out.println("--------------------------------");

        System.out.println("\n--------------------------------");
        System.out.println("Get conditions");
        new SopNodeToolboxSetOfOperations().relationsToSelfContainedOperations(rc.getOsubsetSopNode());
        System.out.println("--------------------------------");

    }

    /**
     * To get all operations that are children to the {@link TreeNode} parameter.<br/>
     * The operations are given as child nodes to a {@link ISopNode}.<br/>
     * @param iTree preferably the operation root
     * @return operations as in {@link ISopNode}
     */
    public static ISopNode getOperationsInModel(TreeNode iTree) {
        final ISopNode returnNode = new SopNode();
        for (int i = 0; i < iTree.getChildCount(); ++i) {
            OperationData opData = (OperationData) iTree.getChildAt(i).getNodeData();
            ISopNodeToolbox toolbox = new SopNodeToolboxSetOfOperations();
            toolbox.createNode(opData, returnNode);
        }
        return returnNode;
    }

    /**
     * Get operations in {@link ISopNode}.<br/>
     * @param iSet ids of operations
     * @return operations as in {@link ISopNode}
     */
    public static ISopNode getOperations(Set<Integer> iSet) {
        ISopNode returnSop = new SopNode();
        ISopNode sop = getOperationsInModel(mSP.getModel().getOperationRoot());
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
