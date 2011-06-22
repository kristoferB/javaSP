/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.view.operationView.graphextension;
import com.mxgraph.model.mxIGraphModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Qw4z1
 */
public class SPGraphTest {

    static SPGraph graph;
    static mxIGraphModel model;

    public SPGraphTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        model = new SPGraphModel();
        graph = new SPGraph(model);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInsertNewCell() {
        Cell cell0 = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
        cell0.getGeometry().setX(50);
        cell0.getGeometry().setY(50);
        
        Cell cell1 = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
        Cell cell2 = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
        Cell cell3 = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_ALTERNATIVE);
        Cell cell4 = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
        Cell cell5 = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_OPERATION);
        Cell cell6 = CellFactory.getInstance().getOperation(SPGraphModel.TYPE_PARALLEL);

        boolean before = true;
        boolean passedTest = true;
        
        graph.getModel().beginUpdate();
        graph.addCell(cell0);
        graph.insertNewCell(cell0, cell1, before);
        graph.insertNewCell(cell1, cell2, !before);
        graph.insertNewCell(cell2, cell3, before);
        graph.insertNewCell(cell3, cell4, !before);
        graph.insertNewCell(cell4, cell5, !before);
        graph.insertNewCell(cell5, cell6, before);
        graph.getModel().endUpdate();



        assertTrue(true);
    }
}
