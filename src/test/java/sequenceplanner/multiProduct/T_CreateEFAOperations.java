package sequenceplanner.multiProduct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class T_CreateEFAOperations {

    SP mSP = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

//    @Test
    public void test1() {
//        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/KristoferTASEexample.sopx");
        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/KristoferPPURivetingTASEExample_selfcontainedoperations.sopx");

//        OperationToEFA obj = new OperationToEFA(mSP.getModel());

        //Test ids
//        assertTrue("Problem with ids!",obj.testIDs());
//        obj.testIDs();
//
//        obj.startToGetOperations();
//
//        obj.mSModule.saveToWMODFile("C:/Users/patrik/Desktop/visualizationTestResult.wmod");

    }

    @Test
    public void test3() {
        for (Integer i = 1; i < 17; ++i) {
            mSP.insertOperation("O" + i);
        }
//        mSP.saveToSOPXFile("C:/Users/patrik/Desktop/TestResult.sopx");
    }
    
}
