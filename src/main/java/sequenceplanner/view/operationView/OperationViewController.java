package sequenceplanner.view.operationView;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.graphextension.Cell;

/**
 * Observes the model and updates all existing operation views when the model is changed
 *
 * @author Evelina
 */
public class OperationViewController implements Observer {

    //SPGraphModels of all exsting operation views
    private LinkedList<OperationView> views;

    public OperationViewController() {
        views = new LinkedList();
    }

    /**
     * Add an SPGraphModel for a operation view
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
                    System.out.println("a1");
                    if (c.getValue() instanceof OperationData) {
                        System.out.println("a2");

                        OperationData data = (OperationData) c.getValue();
                        System.out.println("data: "+ data.toString());
                        System.out.println("c.Get: "+c.getValue());
                        System.out.println("c:"+ c);
                        if (data.getId() == od.getId()) {
                            System.out.println("a3");
                            operationView.getGraph().setValue(c, data);
                        }
                    }
                }
            }
        }
    }
}
