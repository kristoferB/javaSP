package sequenceplanner.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import sequenceplanner.editor.EditorMouseAdapter;

import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.treeView.TreeViewController;

import sequenceplanner.efficientModel.EfficientEFA;
import sequenceplanner.efficientModel.OperationSequences;
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

    public GUIController(GUIModel m, GUIView v) {
        guiModel = m;
        guiView = v;

        treeViewController = new TreeViewController(this, guiView.getTreeView());
      //  guiModel.createNewOpView();
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
        guiView.addSaveEFAoL(new SaveEFAoListener());
        guiView.addSaveEFArL(new SaveEFArListener());
        guiView.addSaveCostL(new SaveCostListener());
        guiView.addSaveOptAutomataL(new SaveOptimalListener());
        guiView.addIdentifyRL(new IdentifyListener());
        guiView.addPrintProdTypesL(new PrintProductListener());
        guiView.addEFAForTransL(new EFAForTListener());
        guiView.addUpdateModelL(new UpdateModelListener());
        guiView.addEFAForMPL(new EFAForMPListener());
<<<<<<< HEAD
        guiView.addSeqForOp(new OperationSeqListener());
        guiView.addReducedEFA(new EfficientEFAListener());
        guiView.addEditorListener();
=======
        guiView.addEditorListener(new EditorMouseAdapter(guiView.getEditorView().getTree(), guiModel.getGlobalProperties()));
        guiView.addTreeModelListener(new EditorTreeModelListener());
        guiView.addSavePropViewL(new SavePropViewListener());
>>>>>>> cbe8babd91337bebd11ec601b3d9afe3c1ea7f2c
    }
    //Listener classes

    //private methods
    private void addNewOpTab(){
         guiView.addNewOpTab(guiModel.getOperationViews().getLast().toString(), guiModel.getOperationViews().getLast());
    }

    public void printToConsole(String text){
        guiView.printToConsole(text);
    }
    public void addNewOpTab(ViewData data){
        for(OperationView op : guiModel.getOperationViews()){
            System.out.println(op.getName());
        }
        guiView.addNewOpTab(guiModel.getOperationViews(data).toString(), guiModel.getOperationViews(data));
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
<<<<<<< HEAD
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class OperationSeqListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            OperationSequences ops = new OperationSequences(guiModel.getModel());
            ops.run();
        }
    }

    class EfficientEFAListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Opens a filechooser and lets the user select a previously created project
     * to open.
     * @return
     */
    private boolean openModel() {
        JFileChooser fc = new JFileChooser("user.dir");

        fc.setFileFilter(filter);
        int answer = fc.showOpenDialog(null);

        if (answer == JFileChooser.APPROVE_OPTION) {
            openModel(fc.getSelectedFile());
            guiModel.getModel().reloadNamesCache();
            try {
                ViewData toOpen = (ViewData) guiModel.getModel().getViewRoot().getChildAt(0).getNodeData();
                guiModel.removeAllOpViews();
                guiModel.createNewOpView(toOpen);
                guiView.addNewOpTab();

            } catch (ClassCastException e) {
                System.out.println("Could not cast first child of viewroot to viewData");
            }
            return true;
=======
            guiView.printToConsole("Not supported yet.");
>>>>>>> cbe8babd91337bebd11ec601b3d9afe3c1ea7f2c
        }
    }

    class EditorTreeModelListener implements TreeModelListener{

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

    class SavePropViewListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                guiView.getPropertyView().saveSettings();
            }

    }

    /**
     * Tells the model to open a new project and adds a new tab in the view
     */
    private void openModel() {
        if(guiModel.openModel()){
            guiView.closeAllViews();
            addNewOpTab();

        }
        printToConsole("new model opened!");
    }


    private void saveModel(boolean saveAs){
        guiModel.saveModel(saveAs);
    }

}
