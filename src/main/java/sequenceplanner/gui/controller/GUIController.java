package sequenceplanner.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import sequenceplanner.algorithms.visualization.UserInteractionForVisualization;

import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.gui.view.HelpPanes;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeFromViewData;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.TreeNode;
import sequenceplanner.view.operationView.ClickMenu;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.OperationViewController;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.treeView.TreeViewController;

/**
 *Main controller in the GUI package. Listens for changes calls from the view,
 * changes the model accordingly and finally tells the view to show the updated
 * model.
 * @author qw4z1
 */
public class GUIController {

    //Instances of the model and view.
    private GUIModel mGuiModel;
    private GUIView mGuiView;
    //TreeviewListener
    private TreeViewController mTreeViewController;
    public OperationViewController mOpViewController;

    public GUIController(GUIModel m, GUIView v) {
        mGuiModel = m;
        mGuiView = v;

        mTreeViewController = new TreeViewController(this, mGuiView.getTreeView());

        //Set observer on model
        mOpViewController = new OperationViewController(this);
        mGuiModel.getModel().addObserver(mOpViewController);

        addListeners();

    }

    private void addListeners() {
        mGuiView.addCreateOPL(new CreateOpListener());
        mGuiView.addCreateRVL(new CreateRVListener());
        mGuiView.addExitL(new ExitListener());
        mGuiView.addPrefL(new PrefListener());
        mGuiView.addAddCellsL(new AddAllListener());
        mGuiView.addOpenL(new OpenListener());
        mGuiView.addSaveL(new SaveListener());
        mGuiView.addSaveAsL(new SaveAsListener());
        mGuiView.addCloseL(new CloseListener());
        mGuiView.addDefWindL(new DefaultListener());
        mGuiView.addSaveEFAoL(new SaveEFAoListener());
        mGuiView.addSaveEFArL(new SaveEFArListener());
        mGuiView.addSaveCostL(new SaveCostListener());
        mGuiView.addSaveOptAutomataL(new SaveOptimalListener());
        mGuiView.addIdentifyRL(new IdentifyListener());
        mGuiView.addPrintProdTypesL(new PrintProductListener());
        mGuiView.addEFAForTransL(new EFAForTListener());
        mGuiView.addUpdateModelL(new UpdateModelListener());
        mGuiView.addEFAForMPL(new EFAForMPListener());
        

        mGuiView.addBruteForceVisualizationL(new BruteForceVisualizationListener());
        mGuiView.addShortCommandsL(new AddShortCommandsListener());
        mGuiView.addAboutL(new AddAboutListener());
    }
    //Listener classes

    public void printToConsole(String text) {
        mGuiView.printToConsole(text);
    }

    //private methods
    /**
     * To add a {@link OperationView} to a operation tab in the operationRootView
     * @param iOperationView the view to add.
     */
    public void addNewOpTab(final OperationView iOperationView) {
        mGuiView.addNewOpTab(iOperationView.toString(), iOperationView);

        //Listener function unclear
        mOpViewController.addOperationView(iOperationView);

        //Listener related to Infonode
        mGuiView.getOpViewMap().getView(mGuiView.getOpViewIndex()).addListener(new OperationWindowListener(this.mGuiView));

        //Listener for mouse click related operations
        iOperationView.addGraphComponentListener(new OperationViewGraphicsListener(iOperationView));
    }

    /**
     * Creates a new Operations View in the GUIModel using the
     * ViewData. If an operation with the same name exists,
     * this method shifts focus to that Operation View instead.
     *
     * @param data ViewData on which the operationview is based
     */
    public void addNewOpTab(ViewData data) {
        if (!isOpened(data)) {
            final OperationView opView = mGuiModel.createNewOpView(data);
            addNewOpTab(opView);

        } else {
            mGuiView.setFocusedOperationView(data);
            printToConsole("Already open!");
        }
    }

    public GUIView getView() {
        return this.mGuiView;
    }

    public Model getModel() {
        return mGuiModel.getModel();
    }

    public GUIModel getGUIModel() {
        return mGuiModel;
    }

    //File menu listenrs
    private class CreateOpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final OperationView opView = mGuiModel.createNewOpView();
            addNewOpTab(opView);
        }
    }

    class CreateRVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiModel.createNewReView();
            mGuiView.addResourceView();
        }
    }

    class ExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiModel.exit();
        }
    }
    //Edit menu listeners

    class PrefListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.showPrefPane();

        }
    }

    class AddAllListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final OperationView opView = mGuiModel.addAllOperations();
            addNewOpTab(opView);
        }
    }
    //Project menu listeners

    class OpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            openModel();
        }
    }

    class SaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            saveModel(false);
        }
    }

    class SaveAsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            saveModel(false);
        }
    }

    class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiModel = new GUIModel();
            mGuiView.resetView(mGuiModel);
            mGuiView.closeAllViews();
        }
    }

    class DefaultListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            defaultWindows();
        }
    }

    //Convert menu listeners
    class SaveEFAoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    class SaveEFArListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    class SaveCostListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    class SaveOptimalListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    class IdentifyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    //MP menu listeners
    class PrintProductListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    class EFAForTListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    class UpdateModelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }

    class EFAForMPListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.printToConsole("Not supported yet.");
        }
    }


    class OperationIdTextFieldListener implements ActionListener {

        int id;
        AttributePanelController ctrl;

        public OperationIdTextFieldListener(int id , AttributePanelController ctrl) {
            super();
            this.id = id;
            this.ctrl = ctrl;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("set name")) {
                JTextField field = (JTextField) e.getSource();
                
                System.out.println("setname");
                if (mGuiModel.getModel().getOperation(id) != null) {
                    mGuiModel.getModel().getOperation(id).getNodeData().setName(field.getText());
                    mOpViewController.update(null, ctrl.getModel());
                    System.out.println("setname");
                    
                }   
                mOpViewController.update(null, ctrl.getModel());
            }
        }
    }

    class BruteForceVisualizationListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final OperationView opView = mGuiModel.createNewOpView();
            opView.setName("Projection" + Model.newId());
            new UserInteractionForVisualization(opView, mGuiModel.getModel());
            addNewOpTab(opView);
        }
    }

    class AddShortCommandsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            HelpPanes hp = new HelpPanes("Short Commands");

        }
    }

    class AddAboutListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Soon implemented");
        }
    }

    /**
     * Class for listening on clicks in an OperationView.
     */
    private class OperationViewGraphicsListener extends MouseAdapter {

        private OperationView oV;

        public OperationViewGraphicsListener(OperationView oV) {
            super();
            this.oV = oV;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            createPopup(e);

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            createPopup(e);
            OperationView v = oV;
            //If double click
            if (e.getClickCount() == 2) {
                //If operation is clicked
                Cell clickedCell = (Cell) v.getGraphComponent().getCellAt(e.getX(), e.getY());
                if (clickedCell != null && v.getGraph().isOperation(clickedCell) || v.getGraph().isSOP(clickedCell)) {
                    if (mGuiModel.getModel().getOperation(clickedCell.getUniqueId()) != null) {
                        clickedCell.setValue(addPropertyPanelView((OperationData) mGuiModel.getModel().getOperation(clickedCell.getUniqueId()).getNodeData()));
                    } else {
                        clickedCell.setValue(addPropertyPanelView((OperationData) clickedCell.getValue()));
                    }


                }
            }
        }

        public void createPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {

                ClickMenu c = new ClickMenu();
                c.show(oV, e);

            }
        }
    }

    /**
     * Tells the model to open a new project and adds all open views as tabs.
     */
    private void openModel() {
        if (mGuiModel.openModel()) {
            System.out.println("open was ok");

            try {

                for (int i = 0; i < getModel().getViewRoot().getChildCount(); i++) {
                    if (getModel().getViewRoot().getChildAt(i).getNodeData() != null) {
                        final ViewData viewData = (ViewData) getModel().getViewRoot().getChildAt(i).getNodeData();
                        System.out.println("viewData to open: " + viewData.getName());

//                        final SopNodeFromViewData trans = new SopNodeFromViewData(viewData);
//                        final OperationView opView = mGuiModel.createNewOpView(viewData);
//
//                        new SopNodeToolboxSetOfOperations().drawNode(viewData.mSopNodeRoot, opView, viewData.mCellDataSet);
//
//                        //Set conditions
//                        mGuiModel.getModel().setConditions(viewData, viewData.getName());
//
//                        //Include tab for view
//                        addNewOpTab(opView);


//                        final OperationView opView = guiModel.createNewOpView(viewData);
//                        createNewOpView(viewData);
//                        if (viewData.isClosed()) {
//                            operationViews.getLast().setClosed(true);
//                        }
                    }
                }

            } catch (ClassCastException e) {
                System.out.println("Could not cast first child of viewroot to viewData");
            }

//            guiView.closeAllViews(); //BIG PROBLEM WITH METHOD, BLOCKS SP
//            for (OperationView o : guiModel.getOperationViews()) {
//                guiView.addNewOpTab(o.toString(), o);
//                if (o.isClosed()) {
//                    guiView.getOpViewMap().getView(guiView.getOpViewIndex()).close();
//                }
//            }

        }
        printToConsole("New model opened!");
    }

    private void saveModel(boolean saveAs) {
        mGuiModel.saveModel(saveAs);
    }

    private void defaultWindows() {
        mGuiView.setWindowLayout();

    }

    /**
     * Checks if a view with the selected data already is opened.
     * @param data ViewData to check
     * @return true if the SOP is already opened else false
     */
    public boolean isOpened(ViewData data) {
        for (OperationView op : mGuiModel.getOperationViews()) {
            if (op.getName().equals(data.getName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Saves an OperationData object to the main project model.
     * @param data 
     */
    public void saveOperationToModel(OperationData data){
        TreeNode dataNode = new TreeNode(data);
        mGuiModel.getModel().saveOperationData(new TreeNode[]{dataNode});
    }
    public OperationData addPropertyPanelView(OperationData data) {
        AttributePanel panel = new AttributePanel(data);
        if (mGuiView.addAttributePanelView(panel)) {
            AttributePanelController ctrl = new AttributePanelController(data, panel, panel.getEditor(),this);
            panel.addEditorSaveListener(ctrl);
            panel.addOperationIdTextFieldListener(new OperationIdTextFieldListener(data.getId(), ctrl));
            mGuiModel.getModel().addObserver(ctrl);
            printToConsole("Operation " + data.getName() + " opened.");
        } else {
            printToConsole("Operation already opened.");

        }
        return data;
    }
}
