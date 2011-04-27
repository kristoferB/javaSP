package sequenceplanner.model.SOP;

import sequenceplanner.model.data.Data;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;
import sequenceplanner.view.operationView.graphextension.SPGraph;

/**
 * Translates all nodes that are children to given {@link ISopNode} to {@link Cell}s in a {@link SPGraph}.<br/>
 * @author patrik
 */
public class DrawSopNode {
    
    private SPGraph mGraph = null;

    public DrawSopNode(SPGraph mGraph) {
        this.mGraph = mGraph;
        drawSequence();
    }

        private void drawSequence() {

//   final public static String TYPE_OPERATION = "operation";
//   final public static String TYPE_SOP = "sop";
//   final public static String TYPE_PARALLEL = "parallel";
//   final public static String TYPE_ALTERNATIVE = "alternative";
//   final public static String TYPE_ARBITRARY = "arbitrary";

            Cell cell1;
            Cell cell2;
            Cell cell3;
            Cell cell4;
            Cell cell5;
            Cell cell6;
            Cell cell7;

            cell1 = CellFactory.getInstance().getOperation("operation");
            cell2 = CellFactory.getInstance().getOperation("parallel");
            cell3 = CellFactory.getInstance().getOperation("alternative");
            cell4 = CellFactory.getInstance().getOperation("operation");
            cell5 = CellFactory.getInstance().getOperation("operation");
            cell6 = CellFactory.getInstance().getOperation("operation");

            cell7 = CellFactory.getInstance().getOperation("operation");

            cell1.getGeometry().setX(10);
            cell1.getGeometry().setY(50);
            Data d = (Data) cell1.getValue();
            d.setName("hej");
            cell1.setValue(d);
            mGraph.addCell(cell1);

            mGraph.insertNewCell(cell1, cell2, false);

            mGraph.insertGroupNode(cell2, null, cell3);
            mGraph.insertGroupNode(cell3, null, cell4);
            mGraph.insertGroupNode(cell3, null, cell5);

            mGraph.insertGroupNode(cell2, null, cell6);

            mGraph.addCell(cell7);
    }
}
