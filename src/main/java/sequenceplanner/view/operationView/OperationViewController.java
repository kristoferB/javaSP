package sequenceplanner.view.operationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeFromSPGraphModel;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.graphextension.Cell;

/**
 * Observes the operations in the model and updates all existing operation views when the model is changed
 *
 * @author Evelina
 */
public class OperationViewController implements Observer {

    //All exsting operation views
    private LinkedList<OperationView> views;

//    private static Map<ViewData,OperationView> mViewViewMap;

    private final GUIController mGUIController;

    public OperationViewController() {
        this(null);
    }

    public OperationViewController(GUIController iGUIController) {
        mGUIController = iGUIController;
        views = new LinkedList();
    }

    /**
     * Add an operation view to observe
     *
     */
    public void addOperationView(OperationView v) {
        views.add(v);
    }

    public OperationView createOperationView() {
        final int id = Model.newId();
        return createOperationView(new ViewData("Sop View " + id, id));
    }

    public OperationView createOperationView(final ViewData iViewData) {
        final OperationView opView = mGUIController.getGUIModel().createNewOpView(iViewData);
        mGUIController.addNewOpTab(opView);
        return opView;
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("OVC");
        if (arg instanceof OperationData) {
            OperationData od = (OperationData) arg;
            System.out.println("operation update: " + od.getName());
//            for (OperationView operationView : views) {
//                //if operation view contains od with this id, update od
//                Hashtable cells = operationView.getGraphModel().getCells();
//
//                for (int i = 2; i < cells.size(); i++) {
//                    Cell c = (Cell) cells.get(Integer.toString(i));
//
//                    if (c != null && c.getValue() != null && c.getValue() instanceof OperationData) {
//                        OperationData data = (OperationData) operationView.getGraphModel().getValue(c);
//                        if (data.getId() == od.getId()) {
//                            //replace old operation data with the updated version
//                            operationView.getGraph().setValue(c, od);
//                        }
//                    }
//                }
//            }
        }
    }

    public static class SaveOperationView implements ActionListener {

        private OperationView mOpView;

        public SaveOperationView(final OperationView iOpView){
            this.mOpView = iOpView;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
            final ViewData viewData = mOpView.mViewData;
            if(viewData != null) {

                mOpView.model.removeConditions(viewData.getName());
                
                mOpView.model.saveView(viewData);

                mOpView.model.updateSopNodeStructureWithObjectsInModel(viewData.mSopNodeForGraphPlus.getRootSopNode(true));

                mOpView.model.setConditions(viewData.mSopNodeForGraphPlus.getRootSopNode(false), viewData.getName());

                System.out.println(viewData.mSopNodeForGraphPlus.getRootSopNode(false).toString());

                System.out.println("save was done: " + viewData.getName() + " e: " + e);
            }
        }
    }



}
