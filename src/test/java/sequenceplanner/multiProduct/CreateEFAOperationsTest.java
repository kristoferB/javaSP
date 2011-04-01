package sequenceplanner.multiProduct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.efaconverter.ModelParser;
import sequenceplanner.efaconverter.RVNode;
import sequenceplanner.efaconverter.RelateTwoOperations;
import sequenceplanner.efaconverter.VisualizationOfOperationSubset;
import sequenceplanner.general.SP;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class CreateEFAOperationsTest {

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

        OperationToEFA obj = new OperationToEFA(mSP.getModel());

        //Test ids
//        assertTrue("Problem with ids!",obj.testIDs());
        obj.testIDs();

        obj.startToGetOperations();

        obj.mSModule.saveToWMODFile("C:/Users/patrik/Desktop/visualizationTestResult.wmod");

    }

//    @Test
    public void test3() {
        for (Integer i = 1; i < 17; ++i) {
            mSP.insertOperation("O" + i);
        }
        mSP.saveToSOPXFile("C:/Users/patrik/Desktop/TestResult.sopx");
    }

    @Test
    public void test2() {
        mSP.loadFromSOPXFile("C:/Users/patrik/Desktop/KristoferPPURivetingTASEExample_selfcontainedoperations.sopx");

        ViewData vd = new ViewData("TestViewingOutput", mSP.getUpdatedIdCount());
        mSP.getGUIModel().createNewOpView(vd);
        OperationView opView = mSP.getGUIModel().getOperationViews(vd);

        VisualizationOfOperationSubset v;
        v = new VisualizationOfOperationSubset(new ModelParser(mSP.getModel()), opView);

        v.run();

        String returnString = "Relations between operations\n";
        returnString += "-----------------\n";
        returnString += "Subset:\n";

        Set<String> operationSubset = new HashSet<String>();
        operationSubset.add("O2");
        operationSubset.add("O10");
        operationSubset.add("O12");
        operationSubset.add("O15");
        returnString += "O2, O10, O12, O15,\n";
        returnString += "-----------------\n";
        returnString += "Set:\n";

        Map<String, RVNode> mNodeMap = new HashMap<String, RVNode>();

        //Collect RVNodes
        for (final RVNode node : v.mRVNodeToolbox.mAllNodes) {
            System.out.println(node.getName());
            returnString += node.getName() + ", ";
            if (operationSubset.contains(node.getName())) {
                mNodeMap.put(node.getName(), node);
            }
        }
        returnString += "\n-----------------\n";

        for (final String name : mNodeMap.keySet()) {
            returnString += name + ":\n";
            for (final RVNode node : mNodeMap.values()) {
                if (!node.getName().equals(name)) {
                    int i = mNodeMap.get(name).getRelationToNode(node);
                    System.out.println(i);
                    returnString += RelateTwoOperations.relationIntegerToString(i, name + " ", " " + node.getName() + ", ");
                }
            }
            returnString += "\n-----------------\n";
        }

        JOptionPane.showMessageDialog(null, returnString);
    }
}
