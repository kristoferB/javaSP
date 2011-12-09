package sequenceplanner.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.infonode.docking.View;
import sequenceplanner.IO.txt.ReadFromProcessSimulateTextFile;
import sequenceplanner.visualization.algorithms.SelectOperationsDialog;

import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.gui.view.HelpPanes;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import sequenceplanner.model.ConvertFromXML;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.algorithms.SopNodeFromViewData;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.TreeNode;
import sequenceplanner.view.operationView.ClickMenuOperationView;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.OperationViewController;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.treeView.TreeViewController;
import sequenceplanner.weightNonBlocking.WeightNonBlocking;
import sequenceplanner.xml.SequencePlannerProjectFile;

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

        //Listener for top right exit button-------------------------------------
        mGuiView.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent winEvt) {
                exitProject();
            }
        });//--------------------------------------------------------------------

        //The general keylisterner for the program. Save, open, ...
        mGuiView.addKeyListener(keyListener);

    }

    private void addListeners() {
        //File
        mGuiView.addOpenL(new OpenListener());
        mGuiView.addSaveL(new SaveListener());
        mGuiView.addSaveAsL(new SaveAsListener());
        mGuiView.addCloseL(new CloseListener());
        mGuiView.addOperationsFromPSTextFileL(new AddOperationsFromFileListener("PSTextFile"));
        mGuiView.addExitL(new ExitListener());

        //Edit
        mGuiView.addPrefL(new PrefListener());
        mGuiView.addAddCellsL(new AddAllListener());

        //Windows
        mGuiView.addCreateOPL(new CreateOpListener());
        mGuiView.addCreateRVL(new CreateRVListener());
        mGuiView.addDefWindL(new DefaultListener());

        //Mix of different...
        mGuiView.addSaveEFAoL(new SaveEFAoListener());
        mGuiView.addSaveEFArL(new SaveEFArListener());
        mGuiView.addSaveCostL(new SaveCostListener());
        mGuiView.addSaveOptAutomataL(new SaveOptimalListener());
        mGuiView.addIdentifyRL(new IdentifyListener());
        mGuiView.addPrintProdTypesL(new PrintProductListener());
        mGuiView.addEFAForTransL(new EFAForTListener());
        mGuiView.addUpdateModelL(new UpdateModelListener());
        mGuiView.addEFAForMPL(new EFAForMPListener());

        //Visualization
        mGuiView.addBruteForceVisualizationL(new BruteForceVisualizationListener());

        //About
        mGuiView.addShortCommandsL(new AddShortCommandsListener());
        mGuiView.addAboutL(new AddAboutListener());
    }
    /**
     * Key listener for program.<br/>
     * Is not working when other keylisteners have been registered...
     * Save, open, ...
     */
    final KeyListener keyListener = new KeyAdapter() {

        @Override
        public synchronized void keyPressed(KeyEvent e) {
            final boolean doSave = e.getKeyCode() == 83 && e.isControlDown(); //83=='s'
            final boolean doOpen = e.getKeyCode() == 79 && e.isControlDown(); //79=='o'

            if (doSave) {
                saveModel(false);
            }

            if (doOpen) {
                if (askForSaveOfModel("Save project before open?")) {
                    openModel();
                }
            }
        }
    };

    public GUIView getView() {
        return this.mGuiView;
    }

    public Model getModel() {
        return mGuiModel.getModel();
    }

    public GUIModel getGUIModel() {
        return mGuiModel;
    }

    //Listener classes
    //File menu listenrs
    /**
     * To add a {@link OperationView} to a operation tab in the operationRootView
     * @param iOperationView the view to add.
     */
    public void addNewOpTab(final OperationView iOperationView) {
        //Add view to Infonode window
        final View newView = mGuiView.addNewOpTab(iOperationView.toString(), iOperationView);

        //Listener function unclear
        mOpViewController.addOperationView(iOperationView);

        //Listener related to Infonode
        newView.addListener(new OperationWindowListener(this.mGuiView));

        //Listener for mouse click related operations
        iOperationView.addGraphComponentListener(new OperationViewGraphicsListener(iOperationView));
    }

    private class CreateOpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mOpViewController.createOperationView();
        }
    }

    class CreateRVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.addResourceView();

        }
    }

    class ExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            exitProject();
        }
    }

    private void exitProject() {
        if (askForSaveOfModel("Save project before exit?")) {
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
            final OperationView opView = mOpViewController.createOperationView();
            final SopNode rootNode = opView.mViewData.mSopNodeForGraphPlus.getRootSopNode(false);
            final List<TreeNode> allOperationList = getModel().getAllOperations();
            for (final TreeNode tn : allOperationList) {
                if (Model.isOperation(tn.getNodeData())) {
                    final OperationData opData = (OperationData) tn.getNodeData();
                    final SopNode newNode = new SopNodeOperation(opData);
                    rootNode.addNodeToSequenceSet(newNode);
                }
            }
            opView.redrawGraph();
        }
    }
    //Project menu listeners

    class OpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //if (askForSaveOfModel("Save project before open?")) {
                openModel();
            //}
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
            saveModel(true);
        }
    }

    class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (askForSaveOfModel("Save project before close?")) {
                reloadAnEmptyProject(true);
            }
        }
    }

    /**
     * Go back to an empty project.<br/>
     * @param iPrintToConsole true == a text is written in console, false == no text
     */
    public void reloadAnEmptyProject(boolean iPrintToConsole) {
        mGuiView.removeAllViews();
        getModel().clearModel();
        getModel().rootUpdated();
        mGuiView.changeTitle(null);
        if (iPrintToConsole) {
            GUIView.printToConsole("A New Project created");
        }
    }

    class DefaultListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mGuiView.setWindowLayout();
        }
    }

    //Convert menu listeners
    class SaveEFAoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class SaveEFArListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class SaveCostListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class SaveOptimalListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class IdentifyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    //MP menu listeners
    class PrintProductListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class EFAForTListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class UpdateModelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class EFAForMPListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            GUIView.printToConsole("Not supported yet.");
        }
    }

    class OperationIdTextFieldListener implements ActionListener {

        int id;
        AttributePanelController ctrl;

        public OperationIdTextFieldListener(int id, AttributePanelController ctrl) {
            super();
            this.id = id;
            this.ctrl = ctrl;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("set name")) {
                JTextField field = (JTextField) e.getSource();

                if (mGuiModel.getModel().getOperation(id) != null) {
                    mGuiModel.getModel().getOperation(id).getNodeData().setName(field.getText());
                    mOpViewController.update(null, mGuiModel.getModel().getOperation(id).getNodeData());
                }
                //mOpViewController.update(null, mGuiModel.getModel()); //not used...
            }
        }
    }

    class BruteForceVisualizationListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //Create a new projection view
//            final int id = Model.newId();
//            final ViewData viewData  = new ViewData("Projection " + id, id);
//            final OperationView opView = mOpViewController.createOperationView(viewData);

            //Create a new view like any view
            final OperationView opView = mOpViewController.createOperationView();

            //Start visualization
            new SelectOperationsDialog(getModel(), opView);
        }
    }

    class AddOperationsFromFileListener implements ActionListener {

        private final String mTypeOfImport;

        public AddOperationsFromFileListener(String mTypeOfImport) {
            this.mTypeOfImport = mTypeOfImport;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (askForSaveOfModel("Save project before import?")) {
                final JFileChooser dialog = new JFileChooser(getGUIModel().path);

                if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    final File file = dialog.getSelectedFile();

                    if (mTypeOfImport.equals("PSTextFile")) {
                        parseTextFile(file);
                    }
                }
            }
        }
    }

    /**
     * Parses a text file. Process Simulate style.<br/>
     * @param iFile
     */
    public void parseTextFile(final File iFile) {
        if (iFile != null) {
            final String path = iFile.getAbsolutePath();
            final String name = iFile.getName();

            final ReadFromProcessSimulateTextFile rftf = new ReadFromProcessSimulateTextFile(path, null, getModel());
            final boolean result = rftf.run();
            if (result) {
                GUIView.printToConsole("Parse of " + name + " was ok!");
                return;
            }
        }
        GUIView.printToConsole("Problem to parse from file!");
    }

    public void weightNonBlockingPlusVisualization(final File iFile) {
        if (iFile != null) {
            final String path = iFile.getAbsolutePath();
            final String name = iFile.getName();

            final WeightNonBlocking w = new WeightNonBlocking(iFile, mOpViewController);
            w.run();
            return;

        }
        GUIView.printToConsole("Problem to run weight non-blocking algorithm!");
    }
    
    
    // Det känns inte så bra att ha dessa metoder här! KB
    public void intentionalXMLVisualize(String path){
        Set<Object> models =  new HashSet<Object>(); models.add(this.getGUIModel().getModel());
        sequenceplanner.IO.XML.IntentionalXML.ParseIntentionalXML parser =
                new sequenceplanner.IO.XML.IntentionalXML.ParseIntentionalXML(path,models);
                
        
        
        // Fixa bättre hantering av modellen i parsern. Skall kunna skicka in den.
        this.mGuiModel.setModel(parser.getModel());
              
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
            HelpPanes hp = new HelpPanes("About");
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
            if (e.getClickCount() == 1) {
                //If operation is clicked
                Cell clickedCell = (Cell) v.getGraphComponent().getCellAt(e.getX(), e.getY());
                if (clickedCell != null && v.getGraph().isOperation(clickedCell) || v.getGraph().isSOP(clickedCell)) {
                    if (!((OperationData) clickedCell.getValue()).getName().isEmpty()){
                        if (mGuiModel.getModel().getOperation(clickedCell.getUniqueId()) == null) {
                            mGuiModel.getModel().saveOperationData(new TreeNode[]{new TreeNode((OperationData) clickedCell.getValue())});                        
                        }
                        clickedCell.setValue(addPropertyPanelView((OperationData) mGuiModel.getModel().getOperation(clickedCell.getUniqueId()).getNodeData()));
                    }
                }
            }
        }

        public void createPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {

                ClickMenuOperationView c = new ClickMenuOperationView();
                c.show(oV, e);

            }
        }
    }

    /**
     * Tells the model to open a new project and adds all open views as tabs.
     */
    private void openModel() {
        final SequencePlannerProjectFile newProject = getGUIModel().openModel();

        if (newProject != null) {
            openModel(newProject);
            return;
        }
        GUIView.printToConsole("Project could not be opened!");

    }

    public void openModel(final SequencePlannerProjectFile iNewProject) {
        //Remove old project. This is done when only if the newProejct != null
        reloadAnEmptyProject(false);

        //Load newProject to Model
        final ConvertFromXML con = new ConvertFromXML(getModel());
        getGUIModel().setModel(con.convert(iNewProject));

        getModel().rootUpdated();
        getModel().reloadNamesCache();

        //To redraw the operation sequences in each operation view
        try {
            for (int i = 0; i < getModel().getViewRoot().getChildCount(); i++) {
                if (getModel().getViewRoot().getChildAt(i).getNodeData() != null) {
                    final ViewData viewData = (ViewData) getModel().getViewRoot().getChildAt(i).getNodeData();
                    System.out.println("viewData to open: " + viewData.getName());
                    final OperationView opView = mOpViewController.createOperationView(viewData);
                    System.out.println(viewData.mSopNodeForGraphPlus.getRootSopNode(false));
                    final SopNodeFromViewData trans = new SopNodeFromViewData(viewData, viewData.mSopNodeForGraphPlus.getRootSopNode(false));
                    opView.redrawGraph();
                    //Set conditions
                    mGuiModel.getModel().setConditions(viewData.mSopNodeForGraphPlus.getRootSopNode(false), viewData.mConditionData);
                }
            }

            //Inform user of update
            final String name = mGuiModel.getProjectName();
            getView().changeTitle(name);
            GUIView.printToConsole("Project " + name + " opened!");
            return;

        } catch (ClassCastException e) {
            System.out.println("Could not cast first child of viewroot to viewData");
        }

    }

    /**
     *
     * @param saveAs true = do saveAs, false = "normal" save
     */
    private boolean saveModel(boolean saveAs) {
        if (mGuiModel.saveModel(saveAs)) {
            final String name = mGuiModel.getProjectName();
            getView().changeTitle(name);
            GUIView.printToConsole("Project " + name + " saved!");
            return true;
        }
        return false;
    }

    /**
     * Saves project if user answers yes.<br/>
     * @param iQuestion text in question
     * @return true if user pressed yes or no, false if user aborted or exit
     */
    private boolean askForSaveOfModel(final String iQuestion) {
        final int choice = JOptionPane.showConfirmDialog(null, iQuestion);
        switch (choice) {
            case 0: //Yes
                if (saveModel(false)) {
                    return true;
                }
                return false;
            case 1: //No
                return true;
            default:
        }
        return false; //Abort or exit
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
    public void saveOperationToModel(OperationData data) {
        TreeNode dataNode = new TreeNode(data);
        mGuiModel.getModel().saveOperationData(new TreeNode[]{dataNode});
    }

    public OperationData addPropertyPanelView(OperationData data) {
        final AttributePanel panel = new AttributePanel(data, getModel());
        if (mGuiView.addAttributePanelView(panel)) {
            AttributePanelController ctrl = new AttributePanelController(data, panel, panel.getEditor(), this);
            panel.addEditorSaveListener(ctrl);
            panel.addOperationIdTextFieldListener(new OperationIdTextFieldListener(data.getId(), ctrl));
            panel.addDescriptionListeners(ctrl, ctrl);

            mGuiModel.getModel().addObserver(ctrl);

            GUIView.printToConsole("Operation " + data.getName() + " opened.");
        }
        return data;
    }
}
