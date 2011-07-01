package sequenceplanner.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import sequenceplanner.IO.ReadFromVolvoFile;
import sequenceplanner.algorithms.visualization.UserInteractionForVisualization;
import sequenceplanner.editor.EditorMouseAdapter;
import sequenceplanner.efaconverter2.export.DefaultExport;
import sequenceplanner.efaconverter2.EFA.DefaultEFAConverter;
import sequenceplanner.efaconverter2.SpEFA.DefaultModelParser;
import sequenceplanner.efaconverter2.reduction.Reduction;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.gui.view.HelpPanes;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import sequenceplanner.model.Model;
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
    private GUIModel guiModel;
    private GUIView guiView;
    //TreeviewListener
    private TreeViewController treeViewController;
    private OperationViewController opViewController;

    public GUIController(GUIModel m, GUIView v) {
        guiModel = m;
        guiView = v;

        treeViewController = new TreeViewController(this, guiView.getTreeView());

        //Turned off by Patrik 2011 - 06 - 31
        //Set observer on model
//        opViewController = new OperationViewController();
//        guiModel.getModel().addObserver(opViewController);

        addListeners();

        //Add a new empty operation view
        final OperationView opView = guiModel.createNewOpView();
        addNewOpTab(opView);

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
        guiView.addNormalEFA(new NormalEFAListener());
        guiView.addReducedEFA(new ReducedEFAListener());
//        guiView.addEditorListener();
        guiView.addEditorListener(new EditorMouseAdapter(guiView.getEditorView().getTree(), guiModel.getGlobalProperties()));
        guiView.addTreeModelListener(new EditorTreeModelListener());
        guiView.addSavePropViewL(new SavePropViewListener());
        guiView.addBruteForceVisualizationL(new BruteForceVisualizationListener());
        guiView.addAddOperationsFromFileL(new AddOperationsFromFileListener());
        guiView.addShortCommandsL(new AddShortCommandsListener());
        guiView.addAboutL(new AddAboutListener());
    }
    //Listener classes

    public void printToConsole(String text) {
        guiView.printToConsole(text);
    }

    //private methods
    /**
     * To add a {@link OperationView} to a operation tab in the operationRootView
     * @param iOperationView the view to add.
     */
    private void addNewOpTab(final OperationView iOperationView) {
        guiView.addNewOpTab(iOperationView.toString(), iOperationView);
//        opViewController.addOperationView(iOperationView);
        guiView.getOpViewMap().getView(guiView.getOpViewIndex()).addListener(new OperationWindowListener(this.guiView));

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
            final OperationView opView = guiModel.createNewOpView(data);
            addNewOpTab(opView);

        } else {
            guiView.setFocusedOperationView(data);
            printToConsole("Already open!");
        }
    }

    public GUIView getView() {
        return this.guiView;
    }

    //File menu listenrs
    private class CreateOpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final OperationView opView = guiModel.createNewOpView();
            addNewOpTab(opView);
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
            final OperationView opView = guiModel.addAllOperations();
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

    class NormalEFAListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultModelParser parser = new DefaultModelParser(guiModel.getModel());
            if (parser.getSpEFAutomata().getAutomatons().isEmpty()) {
                return;
            }
            DefaultEFAConverter converter = new DefaultEFAConverter(parser.getSpEFAutomata());
            DefaultExport export = new DefaultExport(converter.getModule(), guiModel.getPath());
            export.save();
            guiModel.setPath(export.getPath());
        }
    }

    class ReducedEFAListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultModelParser parser = new DefaultModelParser(guiModel.getModel());
            if (parser.getSpEFAutomata().getAutomatons().isEmpty()) {
                return;
            }
            Reduction reduce = new Reduction(parser.getSpEFAutomata());
            DefaultEFAConverter converter = new DefaultEFAConverter(reduce.getReducedModel());
            DefaultExport export = new DefaultExport(converter.getModule(), guiModel.getPath());
            export.save();
            guiModel.setPath(export.getPath());
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

    class OperationIdTextFieldListener implements KeyListener{
        int id;
        public OperationIdTextFieldListener(int id){
            super();
        }
        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyCode()== KeyEvent.VK_ENTER){
                JTextField field = (JTextField) e.getSource();
                guiModel.getModel().getOperation(id).getNodeData().setName(field.getText());
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
        
        
    }
    class BruteForceVisualizationListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final OperationView opView = guiModel.createNewOpView();
            opView.setName("Projection" + Model.newId());
            new UserInteractionForVisualization(opView, guiModel.getModel());
            addNewOpTab(opView);
        }
    }

    class AddOperationsFromFileListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));

            if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                final String path = dialog.getSelectedFile().getAbsolutePath();
                final ReadFromVolvoFile r = new ReadFromVolvoFile(path, null, guiModel.getModel());
                r.run();
            }
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
                    if (guiModel.getModel().getOperation(clickedCell.getUniqueId()) != null) {
                        clickedCell.setValue(addPropertyPanelView((OperationData) guiModel.getModel().getOperation(clickedCell.getUniqueId()).getNodeData()));
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
        if (guiModel.openModel()) {
            guiView.closeAllViews();
            guiView.updateEditorView();
            guiView.updatePropertyView();
            for (OperationView o : guiModel.getOperationViews()) {
                guiView.addNewOpTab(o.toString(), o);
                if (o.isClosed()) {
                    guiView.getOpViewMap().getView(guiView.getOpViewIndex()).close();
                }

            }

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

    public OperationData addPropertyPanelView(OperationData data) {
        AttributePanel panel = new AttributePanel(data);
        if (guiView.addAttributePanelView(panel)) {
            AttributePanelController ctrl = new AttributePanelController(data, panel, panel.getEditor());
            panel.addEditorSaveListener(ctrl);
            panel.addOperationIdTextFieldListener(new OperationIdTextFieldListener(data.getId()));
            guiModel.getModel().addObserver(ctrl);
            printToConsole("Operation " + data.getName() + " opened.");
        } else {
            printToConsole("Operation already opened.");

        }
        return data;
    }
}
