package sequenceplanner.view.operationView;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.graphextension.Cell;

/**
 * Observes the operations in the model and updates all existing operation views when the model is changed
 *
 * @author Evelina
 */
public class OperationViewController implements Observer {

    //All exsting operation views
    private LinkedList<OperationView> views;

    public OperationViewController() {
        views = new LinkedList();
    }

    /**
     * Add an operation view to observe
     *
     */
    public void addOperationView(OperationView v) {
        views.add(v);
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof OperationData) {
            OperationData od = (OperationData) arg;

            for (OperationView operationView : views) {
                //if operation view contains od with this id, update od
                Hashtable cells = operationView.getGraphModel().getCells();

                for(int i = 2; i < cells.size(); i++){
                    Cell c = (Cell) cells.get(Integer.toString(i));
                    
                    if (c.getValue() instanceof OperationData) {
                        OperationData data = (OperationData) operationView.getGraphModel().getValue(c);

                        if (data.getId() == od.getId()) {
                            //replace old operation data with the updated version
                            operationView.getGraph().setValue(c, od);
                        }
                    }
                }
            }
        }
    }
}
