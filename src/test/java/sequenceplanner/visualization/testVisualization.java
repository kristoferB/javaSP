package sequenceplanner.visualization;

import java.util.ListIterator;
import java.util.Set;
import javax.swing.JOptionPane;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sequenceplanner.efaconverter.ModelParser;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.efaconverter.OperationSequencer;
import sequenceplanner.efaconverter.RVNode;
import sequenceplanner.efaconverter.RVNodeToolbox;
import sequenceplanner.efaconverter.RelateTwoOperations;
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

    @Test
    public void testForRelations() {
        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationTestHierarchy2.sopx");

        ViewData vd = new ViewData("TestViewingOutput", mSP.getUpdatedIdCount());
        mSP.getGUIModel().createNewOpView(vd);
        OperationView opView = mSP.getGUIModel().getOperationViews(vd);

        VisualizationOfOperationSubset v;
        v = new VisualizationOfOperationSubset(new ModelParser(mSP.getModel()), opView);

        assertTrue(v.run());
    }

}
