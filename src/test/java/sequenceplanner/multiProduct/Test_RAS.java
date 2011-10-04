package sequenceplanner.multiProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.IO.EFA.ModuleBase;
import sequenceplanner.IO.EFA.SingleFlowerSingleTransitionModule;
import sequenceplanner.IO.EFA.SingleFlowerSingleTransitionModule_Test;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.multiproduct.RAS.CreateOperationsAndResources;
import sequenceplanner.multiproduct.RAS.CreateTransitionsAndVariables;
import sequenceplanner.multiproduct.RAS.Operation;
import sequenceplanner.multiproduct.RAS.Variable;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class Test_RAS implements IAlgorithmListener {

    private static String mFilePath = "C:\\Users\\patrik\\Desktop\\";
    private static String mFileName = "ProductTypesTest.xls";
    private static int mSecondsToRunSynthesisThread = 5;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void method1() {

        final CreateOperationsAndResources coar = new CreateOperationsAndResources("COAR_Thread");
        coar.addAlgorithmListener(this);
        final List<Object> initList = new ArrayList<Object>();

        initList.add(mFilePath + mFileName);

        coar.init(initList);
        coar.start();

        try {
            Thread.sleep(mSecondsToRunSynthesisThread * 1000);
        } catch (InterruptedException ie) {
        }

    }

    public void method2(final Set<Operation> iOpSet, final Set<Variable> iVariableSet) {
        final CreateTransitionsAndVariables ctav = new CreateTransitionsAndVariables("CTAV_Thread");
        ctav.addAlgorithmListener(this);
        final List<Object> initList = new ArrayList<Object>();
        initList.add(iOpSet);
        initList.add(iVariableSet);
        ctav.init(initList);
        ctav.start();
    }

    public void method3(final ModuleBase iMB) {
        final SingleFlowerSingleTransitionModule sfstModule = new SingleFlowerSingleTransitionModule("TestOut", addFileInfoToComment(), iMB, mFilePath);
    }

    public void method4(final ModuleBase iMB, final ModuleBase iMBresources) {
        final SingleFlowerSingleTransitionModule_Test testModule = new SingleFlowerSingleTransitionModule_Test("TestNormalWay", addFileInfoToComment(), iMB, iMBresources, mFilePath);
    }

    private String addFileInfoToComment() {
        String comment = "File path: " + mFilePath + "\n";
        comment += "Excel file name: " + mFileName;
        return comment;
    }

    @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
        if (iFromAlgorithm instanceof CreateOperationsAndResources) {

            final Set<Operation> opSet = (Set<Operation>) iList.get(0);
            final Set<Variable> variableSet = (Set<Variable>) iList.get(1);

            //Start next step
            method2(opSet, variableSet);
        }

        if (iFromAlgorithm instanceof CreateTransitionsAndVariables) {
            final ModuleBase moduleBase = (ModuleBase) iList.get(0);
            final ModuleBase moduleBaseResources = (ModuleBase) iList.get(1);
            System.out.println(moduleBase.toString());

            //Start next step
//            method3(moduleBase); //Normal execution. Works good for not to big examples
            method4(moduleBase, moduleBaseResources); //Prototype execution.
        }

    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        System.out.println(iMessage);
    }
}
