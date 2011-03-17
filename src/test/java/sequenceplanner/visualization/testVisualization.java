package sequenceplanner.visualization;

import java.util.Set;
import javax.swing.JOptionPane;
import org.junit.Test;
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

    SP mSP = new SP();
    ModelParser mModelparser;
    OperationSequencer mOperationSequencer;

    public testVisualization() {
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
    
    @Test
    public void testForVisualization() {
        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/visualizationTest.sopx");

        ViewData vd = new ViewData("TestViewingOutput", mSP.getUpdatedIdCount());
        mSP.getGUIModel().createNewOpView(vd);
        OperationView opView = mSP.getGUIModel().getOperationViews(vd);
        
        VisualizationOfOperationSubset v;
        v = new VisualizationOfOperationSubset(new ModelParser(mSP.getModel()), opView);

        assertTrue(v.run());

        opView.save(false, true);
        mSP.saveToSOPXFile("C:/Users/patrik/Desktop/visualizationTestResult.sopx");
        
    }

}
