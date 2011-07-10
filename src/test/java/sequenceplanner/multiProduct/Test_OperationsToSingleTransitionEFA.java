package sequenceplanner.multiProduct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNodeFromSPGraphModel;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.ViewData;
//import sequenceplanner.multiProduct.summer2011.ASupervisorFromOperationsBasedOnSingleTransition;
//import sequenceplanner.multiProduct.summer2011.SupervisorFromOperationsBasedOnSingleTransition;
import sequenceplanner.view.operationView.OperationView;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class Test_OperationsToSingleTransitionEFA {

    SP mSP = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void test1() {

//        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/fileForTesting.sopx");
//        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/TwoSimpleProducts.sopx");

//        new SupervisorFromOperationsBasedOnSingleTransition(mSP.getModel());

    }    
}
