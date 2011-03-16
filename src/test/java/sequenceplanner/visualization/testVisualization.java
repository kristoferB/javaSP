package sequenceplanner.visualization;

import java.util.Set;
import org.junit.Test;
import org.supremica.automata.Automata;
import static org.junit.Assert.*;
import sequenceplanner.efaconverter.ModelParser;
import sequenceplanner.efaconverter.OpNode;
import sequenceplanner.efaconverter.OperationSequencer;
import sequenceplanner.efaconverter.SEFA;
import sequenceplanner.efaconverter.SEGA;
import sequenceplanner.efaconverter.SModule;
import sequenceplanner.efaconverter.convertSeqToEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.general.SP;
import sequenceplanner.model.data.OperationData;

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
    public void test4() {
        SModule module = new SModule("Test4");
        SEFA efa = new SEFA("Single", module);
        efa.addState("pm", true, true);
        SEGA ega;

        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/precon.sopx");
        mModelparser = new ModelParser(mSP.getModel());

        assertTrue("Id's are not ok",module.testIDs(mModelparser));

        for (OpNode opNode : mModelparser.getOperations()) {
            OperationData opData = (OperationData) opNode.getTreeNode().getNodeData();
            final int id = opData.getId();
            //Add integer variable for operation
            final String varName = "o"+id;
            module.addIntVariable(varName, 0, 2, 0, null);
            //Add transition to start execute operation
            ega = new SEGA("e"+id+"up");
            ega.andGuard(varName+"==0");
            ega.addGuardBasedOnSPCondition(opData.getRawPrecondition(), "o", mModelparser);
            ega.addAction(varName+"=1");
            efa.addStandardSelfLoopTransition(ega);
            //Add transition to finish execute operation
            ega = new SEGA("e"+id+"down");
            ega.andGuard(varName+"==1");
            ega.addGuardBasedOnSPCondition(opData.getRawPostcondition(), "o", mModelparser);
            ega.addAction(varName+"=2");
            efa.addStandardSelfLoopTransition(ega);
        }

        module.generateTransitions();
        module.saveToWMODFile();
        Automata automata = module.getDFA();
        assertFalse(automata == null);

    }

}
