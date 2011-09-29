package sequenceplanner.multiProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.IO.EFA.ModuleBase;
import sequenceplanner.IO.EFA.SingleFlowerSingleTransitionModule;
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

    private String mfilePath;

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
        mfilePath = "C:\\Users\\patrik\\Desktop\\FLEXAplus5productsNobuffers.xls";
        initList.add(mfilePath);

        coar.init(initList);
        coar.start();


        try {
            Thread.sleep(40000);
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
        final String comment = "From excel file: " + mfilePath;
        final SingleFlowerSingleTransitionModule sfstModule = new SingleFlowerSingleTransitionModule("TestOut", comment, iMB);
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
            System.out.println(moduleBase.toString());

            //Start next step
            method3(moduleBase);
        }

    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        System.out.println(iMessage);
    }
}
