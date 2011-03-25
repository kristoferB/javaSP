package sequenceplanner.gui.view;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;

import sequenceplanner.SequencePlanner;
import sequenceplanner.editor.EditorView;
import sequenceplanner.objectattribute.PropertyView;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.utils.IconHandler;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.treeView.TreeView;

/**
 *Main view class for the gui package. Updated by the GUIController and
 * shows the info in GUIModel.
 * @author qw4z1
 */
public class GUIView extends JFrame implements mxEventSource.mxIEventListener {
    //model of the gui

    private JMenuBar menuBar;
    private GUIModel guiModel;

    //ViewMaps holding all views for the rootwindows
    private ViewMap rootViewMap = new ViewMap();
    private ViewMap opViewMap = new ViewMap();
    private ViewMap treeViewMap = new ViewMap();
    private ViewMap consoleViewMap = new ViewMap();
    private ViewMap editorViewMap = new ViewMap();
    private ViewMap objectViewMap = new ViewMap();

    private TabWindow mainDocks;// = new TabWindow(new DockingWindow[]{});
   
    //RootWindows
    private RootWindow rootWindow;
    private RootWindow opRootWindow;// = DockingUtil.createRootWindow(opViewMap, rootPaneCheckingEnabled);
    private RootWindow treeRoot;
    private RootWindow editorRoot;
    private RootWindow objectRoot;
    private RootWindow consoleRoot;

    private EventListenerList listeners;
    private View objectMenu;
    private EditorView editorView;
    private TreeView treeView;

    public TreeView getTreeView() {
        return treeView;
    }
    private PropertyView propertyView;
    private OperationView selectedOperationView;
    private int opViewIndex;
    private JTextArea console;
    private JButton saveButton;
    public GUIView(GUIModel m) {
        guiModel = m;
        initJFrame();
        createRootWindow();
        setStartingWindowsProperties();
    }

    /**
     * creates the frame
     */
    private void initJFrame() {
        setTitle("Sequence Planner");
        setIconImage(IconHandler.getNewIcon("/sequenceplanner/resources/icons/icon.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = createMenu();
        setJMenuBar(menuBar);

        setLocation(getEnvBounds().x, getEnvBounds().y);
        setSize(getEnvBounds().width, getEnvBounds().height);
        setLayout(new BorderLayout());

        this.setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);

    }

    /**
     * Get a rectangle object representing sceen bounds.
     * @return  screen bounds
     */
    private Rectangle getEnvBounds() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        //Add to screen # set by the flag screen=#, if possible
        int i = 0;
        return gs[i].getDefaultConfiguration().getBounds();
    }

    /**
     *Creates the main RootWindow and sets innerRootWindows, TabWindows and SplitWindows.
     *
     */
    private void createRootWindow() {
        //Work in progress...

        rootWindow = DockingUtil.createRootWindow(rootViewMap, false);
        
        treeRoot = DockingUtil.createRootWindow(treeViewMap, true);
        opRootWindow = DockingUtil.createRootWindow(opViewMap, true);
        objectRoot = DockingUtil.createRootWindow(objectViewMap, true);
        editorRoot = DockingUtil.createRootWindow(editorViewMap, true);
        consoleRoot = DockingUtil.createRootWindow(consoleViewMap, true);

        editorView = new EditorView(guiModel.getGlobalProperties());
        treeView = new TreeView(guiModel.getModel());

        propertyView = new PropertyView(guiModel.getGlobalProperties());

        //Create first opview
        opViewIndex++;
        opViewMap.addView(opViewIndex, new View(guiModel.getOperationViews().getFirst().toString(), null, guiModel.getOperationViews().getFirst()));
        opRootWindow.setWindow(mainDocks = new TabWindow(opViewMap.getView(opViewIndex)));

        guiModel.getOperationViews().getFirst().addmxIEventListener(this);
        selectedOperationView = guiModel.getOperationViews().getFirst();
        propertyView.setOpView(guiModel.getOperationViews().getFirst());

        //Create consoltreeRoote
        consoleViewMap.addView(1, new View("console", null, new JScrollPane(console = new JTextArea())));
        consoleRoot.setWindow(new TabWindow(consoleViewMap.getView(1)));

        //Create treeview
        treeViewMap.addView(1, new View("Tree view", null, treeView));
        treeRoot.setWindow(new TabWindow(treeViewMap.getView(1)));

        editorViewMap.addView(1, new View("Editor view", null, editorView));
        editorRoot.setWindow(new TabWindow(editorViewMap.getView(1)));

        //Set window starting layout. Should perhaps be moved to a default layout object.
        rootWindow.setWindow(
                new SplitWindow(false, 0.9f, //Console takes up 10% of the frame.
                new SplitWindow(true, 0.15f, treeRoot,
                new SplitWindow(true, 0.7f, opRootWindow,
                new SplitWindow(false, 0.5f, objectRoot, editorRoot))),
                consoleRoot));
        this.getContentPane().add(rootWindow);

//Test (adding save button to object attribute window) should be cleaned up!!!!

        JPanel objectView = new JPanel();
        saveButton = new JButton(new ImageIcon(SequencePlanner.class.getResource("resources/icons/save.png")));

        objectView.add(saveButton);
        objectMenu = new View("Object attribute view", null, objectView);
        objectViewMap.addView(1, objectMenu);
        objectViewMap.addView(2, new View("Property view", null, propertyView));
        objectRoot.setWindow(new SplitWindow(false, 0.2f, objectViewMap.getView(1), new TabWindow(objectViewMap.getView(2))));
//--------------------

        printToConsole("Welcome to SP 2.0");
    }

    private void setStartingWindowsProperties() {

        rootWindow.getRootWindowProperties().getSplitWindowProperties().setContinuousLayoutEnabled(false);
        rootWindow.getRootWindowProperties().setRecursiveTabsEnabled(false);
        rootWindow.getRootWindowProperties().getDockingWindowProperties().setDragEnabled(false);

        
        //   mainDocks.getWindowProperties().setDragEnabled(false);
    }

    /**
     * Empties the opViewMap and removes all tabs from mainDocks.
     */
    public void closeAllViews() {
        for(int i = 1; opViewMap.getViewCount() != 0; i++)
            opViewMap.removeView(i);
        mainDocks = new TabWindow();
    }

    public void updateViews() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void updatePropertyView() {
        propertyView.updateTree();
    }
    /**
     * Main menu bar for sequenceplanner.
     * Only graphical if used alone.
     *
     * Menu layout:
     *  File
     *      Create new OperationView
     *      Create new ResourceView
     *      Exit
     *  Edit
     *      Preferences
     *      AddAllCellsToNewView
     *  Project
     *      Open
     *      Save
     *      Save As
     *      Close
     *  Convert
     *      Save EFA as file (optimized)
     *      Save EFA as file (reset)
     *      Save cost automata as file
     *      Save optimal automaton as file
     *      Identify relations
     *  MP
     *      Print product types and op in model
     *      EFA for transport planning
     *      Update model after transport planning
     *      EFA for MP supervisor
     *  EM
     *      Identify operation sequences
     *      Reduced-order EFA
     *
     */
    private JMenu fileMenu, edit, project, convert, mp, em;
    private JMenuItem newOperationView, newResourceView, exit, preferences, addAll,
            open, save, saveAs, close, saveEFAo, saveEFAr, saveCost, saveOptimal, identifyr,
            printProduct, efaForTrans, updateAfterTrans, efaForMP,
            opSequences, reduceEFA;

    private JMenuBar createMenu() {
        JMenuBar mb = new JMenuBar();
        //File menu
        fileMenu = new JMenu("File");
        fileMenu.add(newOperationView = new JMenuItem("New Operation View"));
        fileMenu.add(newResourceView = new JMenuItem("New Resource View"));
        fileMenu.add(exit = new JMenuItem("Exit"));
        this.add(fileMenu);

        //Edit menu
        edit = new JMenu("Edit");
        edit.add(preferences = new JMenuItem("Preferences"));
        edit.add(addAll = new JMenuItem("Add all cells to new view"));
        this.add(edit);

        //Project menu
        project = new JMenu("Project");
        project.add(open = new JMenuItem("Open"));
        project.add(save = new JMenuItem("Save"));
        project.add(saveAs = new JMenuItem("Save As"));
        project.add(close = new JMenuItem("Close"));
        this.add(project);

        //Convert menu
        convert = new JMenu("Convert");
        convert.add(saveEFAo = new JMenuItem("Save EFA as file (optimized)"));
        convert.add(saveEFAr = new JMenuItem("Save EFA as file (reset)"));
        convert.add(saveCost = new JMenuItem("Save cost automata as file"));
        convert.add(saveOptimal = new JMenuItem("Save optimal automaton as file"));
        convert.add(identifyr = new JMenuItem("Identify relations"));
        this.add(convert);

        //Multiproduct menu
        mp = new JMenu("MultiProduct");
        mp.add(printProduct = new JMenuItem("Print product types and op in model"));
        mp.add(efaForTrans = new JMenuItem("EFA for transport planning"));
        mp.add(updateAfterTrans = new JMenuItem("Update model after transport planning"));
        mp.add(efaForMP = new JMenuItem("EFA for MP supervisor"));
        this.add(mp);

        //Efficient Model menu
        em = new JMenu("Efficient Model");
        em.add(opSequences = new JMenuItem("Identify operation sequences"));
        em.add(reduceEFA = new JMenuItem("Reduced-order EFA"));
        this.add(em);

        //Add menues to menubar
        mb.add(fileMenu);
        mb.add(edit);
        mb.add(project);
        mb.add(convert);
        mb.add(mp);
        mb.add(em);
        return mb;

    }//End createMenu

    //Menubar listeners
    public void addCreateOPL(ActionListener l) {
        newOperationView.addActionListener(l);
    }

    public void addCreateRVL(ActionListener l) {
        newResourceView.addActionListener(l);
    }

    public void addExitL(ActionListener l) {
        exit.addActionListener(l);
    }

    public void addPrefL(ActionListener l) {
        preferences.addActionListener(l);
    }

    public void addAddCellsL(ActionListener l) {
        addAll.addActionListener(l);
    }

    public void addOpenL(ActionListener l) {
        open.addActionListener(l);
    }

    public void addSaveL(ActionListener l) {
        save.addActionListener(l);
    }

    public void addSaveAsL(ActionListener l) {
        saveAs.addActionListener(l);
    }

    public void addCloseL(ActionListener l) {
        close.addActionListener(l);
    }

    public void addSaveEFAoL(ActionListener l) {
        saveEFAo.addActionListener(l);
    }

    public void addSaveEFArL(ActionListener l) {
        saveEFAr.addActionListener(l);
    }

    public void addSaveCostL(ActionListener l) {
        saveCost.addActionListener(l);
    }

    public void addSaveOptAutomataL(ActionListener l) {
        saveOptimal.addActionListener(l);
    }

    public void addIdentifyRL(ActionListener l) {
        identifyr.addActionListener(l);
    }

    public void addPrintProdTypesL(ActionListener l) {
        printProduct.addActionListener(l);
    }

    public void addEFAForTransL(ActionListener l) {
        efaForTrans.addActionListener(l);
    }

    public void addUpdateModelL(ActionListener l) {
        updateAfterTrans.addActionListener(l);
    }

    public void addEFAForMPL(ActionListener l) {
        efaForMP.addActionListener(l);
    }

<<<<<<< HEAD
    public void addSeqForOp(ActionListener l) {
        opSequences.addActionListener(l);
    }

    public void addReducedEFA(ActionListener l) {
        reduceEFA.addActionListener(l);
    }

    public void addEditorListener(){
        editorView.addMouseListener(new EditorMouseAdapter(editorView.getTree(), guiModel.getGlobalProperties()));
=======
    public void addEditorListener(MouseAdapter l) {
        editorView.addMouseListener(l);
    }

    //Call the view to add a listener to the model? oO
    public void addTreeModelListener(TreeModelListener l) {
        guiModel.addTreeModelListener(l);
    }

    public void addSavePropViewL(ActionListener l) {
        saveButton.addActionListener(l);
>>>>>>> cbe8babd91337bebd11ec601b3d9afe3c1ea7f2c
    }
//End listeners

    public void showPrefPane() {
        PreferencePane p = createPrefPane();
        p.setVisible(true);
    }

    //Will perhaps be bigger whne prefpane is properly implemented
    private PreferencePane createPrefPane() {
        return new PreferencePane();
    }

    /**
     * Adds a new tab to the opViewWindow and opViewMap.
     * @param name      name of the tab
     * @param opView    operationview to be shown in the TabWindow
     */
    public void addNewOpTab(String name, OperationView opView) {

//Should not be done here..
        opView.addmxIEventListener(this);
        selectedOperationView = opView;
        propertyView.setOpView(opView);
//-----

        opViewIndex++;
        View newView = new View(name, null, opView);
        opViewMap.addView(opViewIndex, newView);

        if (mainDocks.getChildWindowCount() == 0) {
            opRootWindow.setWindow(mainDocks = new TabWindow(opViewMap.getView(opViewIndex)));
        } else {
            mainDocks.addTab(newView);
        }
    }

    public void addResourceView() {
        mainDocks.addTab(new View(guiModel.getResourceView().getName(), null, guiModel.getResourceView()));
    }

    @Override
    public void invoke(Object source, mxEventObject evt) {
        propertyView.setOperation();

    }

    public void printToConsole(String text) {
        console.append(text + "\n");
    }

    public EditorView getEditorView() {
        return editorView;
    }

    public PropertyView getPropertyView() {
        return propertyView;
    }
}
