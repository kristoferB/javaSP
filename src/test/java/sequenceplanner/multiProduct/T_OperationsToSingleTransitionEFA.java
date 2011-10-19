package sequenceplanner.multiProduct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.multiproduct.InfoInResources.SupervisorFromOperationsBasedOnSingleTransition;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class T_OperationsToSingleTransitionEFA {

    SP mSP = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

//    @Test
    public void test1() {
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/summer2011.sopx");
//        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/TwoSimpleProducts.sopx");


        SupervisorFromOperationsBasedOnSingleTransition s = new SupervisorFromOperationsBasedOnSingleTransition(mSP.getModel());
        s.start();

    }

//    @Test
    public void test2() {
    }

}
