package sequenceplanner.visualization;

import java.util.HashSet;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.algorithms.visualization.Visualization;
import static org.junit.Assert.*;
import sequenceplanner.efaconverter.ModelParser;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.efaconverter.OperationSequencer;
import sequenceplanner.efaconverter.VisualizationOfOperationSubset;
import sequenceplanner.efaconverter.convertSeqToEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.general.SP;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;

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

    /**
     * Not working with opView.save ...
     */
//    @Test
    public void testForVisualization() {
        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationTestHierarchy2.sopx");

        ViewData vd = new ViewData("TestViewingOutput", mSP.getUpdatedIdCount());
        mSP.getGUIModel().createNewOpView(vd);
        OperationView opView = mSP.getGUIModel().getOperationViews(vd);

        VisualizationOfOperationSubset v;
        v = new VisualizationOfOperationSubset(new ModelParser(mSP.getModel()), opView);

        assertTrue(v.run());

        opView.save(false, true);
        mSP.saveToSOPXFile("C:/Users/patrik/Desktop/visualizationTestResult.sopx");

    }

//    @Test
    public void testForRelations() {
        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationTestHierarchy2.sopx");

        ViewData vd = new ViewData("TestViewingOutput", mSP.getUpdatedIdCount());
        mSP.getGUIModel().createNewOpView(vd);
        OperationView opView = mSP.getGUIModel().getOperationViews(vd);

        VisualizationOfOperationSubset v;
        v = new VisualizationOfOperationSubset(new ModelParser(mSP.getModel()), opView);

        assertTrue(v.run());
    }

    @Test
    public void testVisualizationUseingSopNode() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/KristoferPPURivetingTASEExample_selfcontainedoperations.sopx");

        SopNode allOpSet = getOperationsInModel(mSP.getModel().getOperationRoot());
        System.out.println(allOpSet.toString());

        Set<Integer> subsetIds = new HashSet<Integer>();
        subsetIds.add(1006); //op1
        subsetIds.add(1010); //op5
        subsetIds.add(1012); //op7
        subsetIds.add(1015); //op10
        SopNode subOpSet = getOperations(subsetIds);
        System.out.println(subOpSet.toString());

        Visualization v = new Visualization(mSP.getModel());

        v.addOset(allOpSet);
        assertTrue(v.addOsubset(subOpSet));

        v.identifyRelations();


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
