package sequenceplanner.visualization;

import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.algorithms.visualization.OperationRelations;
import static org.junit.Assert.*;
import sequenceplanner.efaconverter.ModelParser;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.efaconverter.OperationSequencer;
import sequenceplanner.efaconverter.VisualizationOfOperationSubset;
import sequenceplanner.efaconverter.convertSeqToEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.general.SP;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;

/**
 *
 * @author patrik
 */
public class testVisualization {

    static SP mSP ;
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
    public void testRelations() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/KristoferPPURivetingTASEExample_selfcontainedoperations.sopx");

        OperationRelations or = new OperationRelations(mSP.getModel());

        assertTrue(or.getOperationIds());

        or.addToRelationOperationSet(1006, true); //op1
        or.addToRelationOperationSet(1010, false); //op5
        or.addToRelationOperationSet(1012, true); //op7
        or.addToRelationOperationSet(1015, false); //op10

        assertTrue(or.identifyRelations());

        assertTrue(or.getRelationOperationSetAsSOPNode().getSequencesAsSet().size()==4);

        or.getSModule().saveToWMODFile("C:/Users/patrik/Desktop/result.wmod");

    }

}
