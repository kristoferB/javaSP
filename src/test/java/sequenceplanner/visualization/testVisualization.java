package sequenceplanner.visualization;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.algorithms.visualization.RelationContainer;
import sequenceplanner.algorithms.visualization.RelationPartition;
import sequenceplanner.algorithms.visualization.Visualization;
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
    static Visualization mVisualization;

    public testVisualization() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        mSP = new SP();
    }

    @Before
    public void setUpMethod() throws Exception {
        //New Visualization
        mVisualization = new Visualization();

        //Add all operations to Oset
//        ISopNode allOpSet = getOperationsInModel(mSP.getModel().getOperationRoot());
//        mVisualization.addOset(allOpSet);
    }

    /**
     * Test of: Arbitrary order and alternative
     */
    @Test
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
        subsetIds.add(2006);
        subsetIds.add(2007);
        subsetIds.add(2010);
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

    private static void workWithAddedData() {
        RelationContainer rc = mVisualization.identifyRelations();
        assertTrue(rc != null);

        assertTrue(mVisualization.hierarchicalPartition(rc));
        assertTrue(mVisualization.alternativePartition(rc));
        assertTrue(mVisualization.arbitraryOrderPartition(rc));
        assertTrue(mVisualization.parallelPartition(rc));

        System.out.println("\n--------------------------------");
        System.out.println("After partition");
        System.out.println(rc.getOsubsetSopNode().inDepthToString());
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
