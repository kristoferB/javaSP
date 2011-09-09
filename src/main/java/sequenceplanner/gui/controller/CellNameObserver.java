package sequenceplanner.gui.controller;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxRectangle;
import java.util.Observable;
import java.util.Observer;
import sequenceplanner.model.data.Data;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;

/**
 * Observer for the operation names in {@link Cell}<br/>
 * Update method changes title according to notifyObservers call in <code>setName</code> in {@link Data}
 * @author patrik
 */
public class CellNameObserver implements Observer {

    private Cell mCell;
    private SPGraph mSPGraph;

    public CellNameObserver(final Cell iCell, final SPGraph iSPGraph) {
        this.mCell = iCell;
        this.mSPGraph = iSPGraph;
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof String && mCell != null) {
            mxRectangle rect = SPGraph.getSizeForOperation(mCell);
            mxGeometry geo = new mxGeometry();
            geo.setHeight(rect.getHeight());
            geo.setWidth(rect.getWidth());
            mSPGraph.updateCellSize(mCell);
        }
//        System.out.println("o: " + o);
//        System.out.println("arg: " + arg);
    }
}
