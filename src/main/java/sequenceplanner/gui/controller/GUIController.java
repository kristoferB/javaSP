package sequenceplanner.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import sequenceplanner.algorithms.visualization.UserInteractionForVisualization;
import sequenceplanner.editor.EditorMouseAdapter;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.OperationViewController;
import sequenceplanner.view.treeView.TreeViewController;

/**
 *Main controller in the GUI package. Listens for changes calls from the view,
 * changes the model accordingly and finally tells the view to show the updated
 * model.
 * @author qw4z1
 */
public class GUIController {

    //Instances of the model and view.
    private GUIModel guiModel;
    private GUIView guiView;
    //TreeviewListener
    private TreeViewController treeViewController;
    private OperationViewController opViewController;

    public GUIController(GUIModel m, GUIView v) {
        guiModel = m;
        guiView = v;

        treeViewController = new TreeViewController(this, guiView.getTreeView());

        //Set observer on model
        opViewController = new OperationViewController();
        guiModel.getModel().addObserver(opViewController);
        //Add first operation view to opViewController
        opViewController.addOperationView(guiModel.getOperationViews().getLast());

        //  addNewOpTab();
        addListeners();


    }

    private void addListeners() {
        guiView.addCreateOPL(new CreateOpListener());
        guiView.addCreateRVL(new CreateRVListener());
        guiView.addExitL(new ExitListener());
        guiView.addPrefL(new PrefListener());
        guiView.addAddCellsL(new AddAllListener());
        guiView.addOpenL(new OpenListener());
        guiView.addSaveL(new SaveListener());
        guiView.addSaveAsL(new SaveAsListener());
        guiView.addCloseL(new CloseListener());
        guiView.addDefWindL(new DefaultListener());
        guiView.addSaveEFAoL(new SaveEFAoListener());
        guiView.addSaveEFArL(new SaveEFArListener());
        guiView.addSaveCostL(new SaveCostListener());
        guiView.addSaveOptAutomataL(new SaveOptimalListener());
        guiView.addIdentifyRL(new IdentifyListener());
        guiView.addPrintProdTypesL(new PrintProductListener());
        guiView.addEFAForTransL(new EFAForTListener());
        guiView.addUpdateModelL(new UpdateModelListener());
        guiView.addEFAForMPL(new EFAForMPListener());
        guiView.addEditorListener(new EditorMouseAdapter(guiView.getEditorView().getTree(), guiModel.getGlobalProperties()));
        guiView.addTreeModelListener(new EditorTreeModelListener());
        guiView.addSavePropViewL(new SavePropViewListener());
        guiView.addBruteForceVisualizationL(new BruteForceVisualizationListener());
    }
    //Listener classes

    //private methods
    private void addNewOpTab() {
        guiView.addNewOpTab(guiModel.getOperationViews().getLast().toString(), guiModel.getOperationViews().getLast());
    }

    public void printToConsole(String text) {
        guiView.printToConsole(text);
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
            guiModel.createNewOpView(data);
            guiView.addNewOpTab(guiModel.getOperationViews(data).toString(), guiModel.getOperationViews(data));
        } else {
            guiView.setFocused(data);
            printToConsole("Already open!");
        }
    }

    //File menu listenrs
    private class CreateOpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.createNewOpView();
            addNewOpTab();
        }
    }

    class CreateRVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.createNewReView();
            guiView.addResourceView();
        }
    }

    class ExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.exit();
        }
    }
    //Edit menu listeners

    class PrefListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.showPrefPane();

        }
    }

    class AddAllListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.addAllOperations();
            addNewOpTab();
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
            guiView.printToConsole("Not supported yet.");
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
            guiView.printToConsole("Not supported yet.");
        }
    }

    class SaveEFArListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    class SaveCostListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    class SaveOptimalListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    class IdentifyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    //MP menu listeners
    class PrintProductListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    class EFAForTListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    class UpdateModelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    class EFAForMPListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.printToConsole("Not supported yet.");
        }
    }

    class EditorTreeModelListener implements TreeModelListener {

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            guiView.updatePropertyView();
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            guiView.updatePropertyView();
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            guiView.updatePropertyView();
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            guiView.updatePropertyView();
        }
    }

    class SavePropViewListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.getPropertyView().saveSettings();
        }
    }

    class BruteForceVisualizationListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.createNewOpView();
            final OperationView opView = guiModel.getOperationViews().getLast();
            opView.setName("Projection" + guiModel.getModel().getCounter());
            new UserInteractionForVisualization(opView,guiModel.getModel());
            addNewOpTab();
            
        }
    }

    /**
     * Tells the model to open a new project (and adds a new tab in the view?)
     */
    private void openModel() {
        if (guiModel.openModel()) {
            guiView.closeAllViews();
            guiView.updateEditorView();
            guiView.updatePropertyView();
            for(OperationView o:guiModel.getOperationViews())
                 guiView.addNewOpTab(o.toString(), o);

        }
        printToConsole("New model opened!");
    }

    private void saveModel(boolean saveAs) {
        guiModel.saveModel(saveAs);
    }

    private void defaultWindows() {
        guiView.setWindowLayout();

    }

    /**
     * Checks if a view with the selected data already is opened.
     * @param data ViewData to check
     * @return true if the SOP is already opened else false
     */
    public boolean isOpened(ViewData data) {
        for (OperationView op : guiModel.getOperationViews()) {
            if (op.getName().equals(data.getName())) {
                return true;
            }
        }
        return false;
    }
}
