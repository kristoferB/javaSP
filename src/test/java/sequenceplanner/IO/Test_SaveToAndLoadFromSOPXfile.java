package sequenceplanner.IO;

import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceData;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class Test_SaveToAndLoadFromSOPXfile {

    SP sp = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void method1() {

        sp.loadFromTemplateSOPXFile("resources/filesForTesting/fileForTesting.sopx");
        sp.insertOperation();

        loopDataRoots();
        sp.saveToSOPXFile("C:\\Users\\patrik\\Desktop\\output.sopx");

        sp.loadFromSOPXFile("C:\\Users\\patrik\\Desktop\\output.sopx");

        loopDataRoots();

        sp.saveToSOPXFile("C:\\Users\\patrik\\Desktop\\output2.sopx");

    }

    private void loopData(final List<TreeNode> operationList) {

        for (final TreeNode tn : operationList) {
            final Data data = tn.getNodeData();
            final ResourceData rd = (ResourceData) data;
            rd.
            System.out.println("name: " + data.getName());
        }

    }

    private void loopDataRoots() {
        System.out.println("Operations");
        loopData(sp.getModel().getAllOperations());
        System.out.println("Resources");
        loopData(sp.getModel().getAllResources());
//        System.out.println("Variables"); //This is not a resource variable
//        loopData(sp.getModel().getAllVariables());
    }
}
