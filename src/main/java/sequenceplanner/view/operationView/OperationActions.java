package sequenceplanner.view.operationView;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;
import sequenceplanner.view.operationView.graphextension.SPGraph;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

/**
 *
 * @author Erik Ohlson
 */
public class OperationActions {

    static Logger logger = Logger.getLogger(OperationActions.class);

    protected static OperationView getView(ActionEvent e) {
        return (OperationView) e.getSource();
    }

    protected static SPGraph getGraph(ActionEvent e) {
        return getView(e).getGraph();
    }

    /**
     *
     */
    public static class InsertOperation implements ActionListener {

        private Point p;

        public InsertOperation(int x, int y) {
            p = new Point(x, y);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object parent = (Cell) getView(e).getGraphComponent().getCellAt(p.x, p.y);

            getGraph(e).insertOperation(parent, p, Model.newId());
        }

        public void setParent(TreeNode parent) {
        }
    }

    public static class AlternateSOP implements ActionListener {

        private Cell cell;

        public AlternateSOP() {
        }

        public AlternateSOP(Cell cell) {
            this.cell = cell;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SPGraph graph = getGraph(e);
            Object[] cells = new Object[]{cell};

            graph.getModel().beginUpdate();
            try {

                if (cell.isSOP()) {
                    getGraph(e).cellTypeChanged(cell, Constants.OP);
                    graph.foldCells(true, false, cells);
                    graph.updateCellSize(cell);

                } else {
                    getGraph(e).cellTypeChanged(cell, Constants.SOP);
                    graph.foldCells(false, false, cells);
                }

            } finally {
                graph.getModel().endUpdate();
            }
        }
    }

    /**
     * Add new operations in Graph
     *
     */
    public static class AddOperation implements ActionListener {

        private mxPoint clickPoint;
        private String type;
        private boolean before;
        private boolean into;
                
        public AddOperation(Point clickPoint, String type, boolean before, boolean into) {
            this.clickPoint = new mxPoint(clickPoint.getX(), clickPoint.getY());
            this.type = type;
            this.before = before;
            this.into = into;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SPGraph graph = getGraph(e);

            Cell insertedCell = null;

            Cell cell = (Cell)getView(e).getGraphComponent().getCellAt(clickPoint.getPoint().x, clickPoint.getPoint().y);


            //TODO :UGLY fix to always insert after edge
            if (cell == null || ((Cell) cell).isEdge()) {
                cell = (Cell)graph.getSelectionCell();
            }

            graph.getModel().beginUpdate();
            try {
                insertedCell = CellFactory.getInstance().getOperation(type);

                if (insertedCell != null && type.equals(SPGraphModel.TYPE_OPERATION)) {

                    graph.updateCellSize(insertedCell);
                }
                if (cell instanceof Cell) {

                    if (cell.isEdge()) {
                        logger.debug("insert operation on edge is not implemented");
                    } else {

                        mxCellState state = getGraph(e).getView().getState(cell);

                        mxPoint p = new mxPoint(clickPoint.getX() - state.getX(),
                                clickPoint.getY() - state.getY());

                        if (cell.isOperation()) {
                            graph.insertNewCell(cell, insertedCell, this.before);

                        } else if (cell.isSOP()) {
                            if (!this.into) {
                                graph.insertNewCell(cell, insertedCell, this.before);
                            } else {
                                //Relative movement
                                //TODO place cells.
                                graph.addCell(insertedCell, cell);
                            }

                        } else if (cell.isAlternative() || cell.isArbitrary() || cell.isParallel()) {

                            if (!this.into) {
                                graph.insertNewCell(cell, insertedCell, this.before);

                            } else {
                                graph.insertGroupNode(cell, p, insertedCell);
                            }
                        }
                    }
                } else {
                    insertedCell.getGeometry().setX(clickPoint.getX());
                    insertedCell.getGeometry().setY(clickPoint.getY());
                    graph.addCell(insertedCell);
                }
                getView(e).updateSopNode();
            } finally {
                graph.getModel().endUpdate();
            }
        }
    }

    public static class Undo implements ActionListener {

        public Undo() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getView(e).getGraphComponent().getUndoManager().undo();
            getView(e).updateSopNode();
        }
    }

    public static class Redo implements ActionListener {

        public Redo() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getView(e).getGraphComponent().getUndoManager().redo();
            getView(e).updateSopNode();
        }
    }

    public static class AutoarrangeGroup implements ActionListener {

        Cell cell;

        public AutoarrangeGroup(Cell cell) {
            this.cell = cell;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cell == null) {
                getGraph(e).recursiveAutoArrange((Cell) getGraph(e).getDefaultParent());
            } else if (cell instanceof Cell && ((cell).isGroup()) || (cell).isSOP()) {
                getGraph(e).recursiveAutoArrange(cell);
            }
        }
    }

    // Added by KB
    public static class AutoSeqGroup implements ActionListener {

        Cell cell;

        public AutoSeqGroup(Cell cell) {
            this.cell = cell;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cell == null) {
                getGraph(e).autoSequence((Cell) getGraph(e).getDefaultParent());
            } else if (cell instanceof Cell && ((cell).isGroup()) || (cell).isSOP()) {
                getGraph(e).autoSequence(cell);
            } else if (cell instanceof Cell && ((cell).isOperation())) {
                Cell[] sops = getGraph(e).getGraphModel().getChildSOP(getGraph(e).getDefaultParent());
                getGraph(e).addGraphicalPrecond(cell, sops);
            }
        }
    }

    public static class Select implements ActionListener {

        String type;

        public Select(String type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (type.equalsIgnoreCase("all")) {
                getGraph(e).selectAll();
            } else if (type.equalsIgnoreCase("sequence")) {
                getGraph(e).selectSequences();
            } else if (type.equalsIgnoreCase("none")) {
                getGraph(e).clearSelection();
            } else if (type.equalsIgnoreCase("group")) {
                getGraph(e).selectGroup();
            }
        }
    }

    public static class Delete implements ActionListener {

        Cell toRemove;

        public Delete() {
        }

        public Delete(Cell cell) {
            toRemove = cell;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (toRemove == null) {
                getGraph(e).deleteMarkedCells();
            } else if (toRemove.isEdge()) {
                getGraph(e).removeCells(new Object[]{toRemove});
            }

            getView(e).updateSopNode();
        }
    }
}