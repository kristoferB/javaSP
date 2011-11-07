package sequenceplanner.multiProduct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.algorithms.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.SopNodeParallel;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class T_RemoveNode {

    SP sp = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void method1() {
        //build up data
        int id = 0;
        final ISopNode rootNode = new SopNode();
        final ISopNode parA = new SopNodeParallel();
        rootNode.addNodeToSequenceSet(parA);
        final ISopNode nodeA = new SopNodeOperation(new OperationData("nodeA", id++));
        parA.addNodeToSequenceSet(nodeA);
        final ISopNode parB = new SopNodeParallel();
        parA.setSuccessorNode(parB);
        final ISopNode nodeB = new SopNodeOperation(new OperationData("nodeB", id++));
        parB.addNodeToSequenceSet(nodeB);
        final ISopNode nodeC = new SopNodeOperation(new OperationData("nodeC", id++));
        parB.addNodeToSequenceSet(nodeC);
        final ISopNode nodeD = new SopNodeOperation(new OperationData("nodeD", id++));
        parB.setSuccessorNode(nodeD);
        
//        System.out.println("before");
//        System.out.println(rootNode.toString());

        //Test snToolbox methods
        final ISopNodeToolbox snToolbox = new SopNodeToolboxSetOfOperations();

        assertTrue(snToolbox.getParentIfNodeIsInSequenceSet(nodeB, rootNode).equals(parB));
        assertTrue(snToolbox.getParentIfNodeIsInSequenceSet(nodeD, rootNode) == null);


        assertTrue(snToolbox.getPredecessor(nodeB, rootNode) == null);


        assertTrue(snToolbox.removeNode(nodeD, rootNode));
//        assertFalse(snToolbox.removeNode(rootNode, rootNode));
//        assertFalse(snToolbox.removeNode(nodeF, rootNode));

//        System.out.println("after");
//        System.out.println(rootNode.toString());
    }
}

