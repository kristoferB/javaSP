package sequenceplanner.weightNonBlockingTest;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;

import java.util.Set;
import javax.swing.JOptionPane;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.general.SP;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.weightNonBlocking.Algorithm;
import sequenceplanner.weightNonBlocking.Block;
import sequenceplanner.weightNonBlocking.Resource;
import sequenceplanner.weightNonBlocking.Seam;
import static org.junit.Assert.*;

/**
 * DARPA
 * @author patrik
 */
public class Test_weightNonBlocking implements IAlgorithmListener {

    static SP mSP = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
        mSP.initViewAndController();

//        final OperationView opView = mSP.getGUIController().mOpViewController.createOperationView();
//
//        mCam = new CreateAutomataModels(opView, mSP.getModel());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void test3() {
        //Create blocks
        Block block1 = new Block(800, "block1");
        Block block2 = new Block(300, "block2");
        Block block3 = new Block(100, "block3");
//        Block block4 = new Block(923, "block4");
        Block vehicle = new Block(2500, "vehicle");

        //Create Seams
        Set<Seam> seamSet = new HashSet<Seam>();
        seamSet.add(new Seam(block2, block1));
        seamSet.add(new Seam(block3, block1));
        seamSet.add(new Seam(block1, vehicle));
//        seamSet.add(new Seam(block4, block3));

        //Resource payload
        Resource crane = new Resource(1000);

        Algorithm algorithm = new Algorithm(this);
        final List list = new ArrayList();
        list.add(seamSet);
        list.add(crane);
        algorithm.init(list);
        algorithm.start();

        JOptionPane.showConfirmDialog(null, "Close");

    }

    @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
        final ISopNode rootNode = (ISopNode) iList.get(0);
        final OperationView ow = mSP.getGUIController().mOpViewController.createOperationView();
        ow.drawGraph(rootNode);
        mSP.saveToSOPXFile("C:\\Users\\patrik\\Desktop\\weight.sopx");
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        System.out.println(iMessage);
    }
}

