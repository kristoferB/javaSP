/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import sequenceplanner.model.Model;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import sequenceplanner.general.SP;

/**
 *
 * @author shoaei
 */
public class testOperationSequences {
    
    static SP mSP;

    public testOperationSequences() {
    }


    @BeforeClass
    public static void setUpClass() throws Exception {
        mSP = new SP();
    }

    @Test
    public void testNumberOfOperations(){
        mSP.loadFromSOPXFile("src/main/resources/sequenceplanner/resources/filesForTesting/testOperationSequences.sopx");
        Model model = mSP.getModel();
        OperationSequences ops = new OperationSequences(model);
        assertEquals(20, ops.nbrOfOperation());
    }

    @Test
    public void testNumberOfPaths(){
        mSP.loadFromSOPXFile("src/main/resources/sequenceplanner/resources/filesForTesting/testOperationSequences.sopx");
        Model model = mSP.getModel();
        OperationSequences ops = new OperationSequences(model);
        ops.run();
        assertEquals(14, ops.nbrOfPaths());
    }

}
