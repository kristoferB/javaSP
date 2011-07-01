package sequenceplanner.gui.view;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Iterator;
import java.util.LinkedList;

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
import net.infonode.docking.DockingWindow;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.drop.DropFilter;
import net.infonode.docking.drop.DropInfo;
import net.infonode.docking.drop.InteriorDropInfo;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

import sequenceplanner.SequencePlanner;
import sequenceplanner.editor.EditorView;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.objectattribute.PropertyView;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import sequenceplanner.utils.IconHandler;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.treeView.TreeView;

/**
 * Main view class for the gui package. Updated by the GUIController and
 * shows the info in GUIModel.
 * @author Qw4z1
 */
public class GUIView extends JFrame implements mxEventSource.mxIEventListener {
    //model of the gui

    private JMenuBar menuBar;
    private GUIModel guiModel;
    //ViewMaps holding all views for the RootWindows
    private ViewMap rootViewMap = new ViewMap();
    private ViewMap opViewMap = new ViewMap();
    private ViewMap treeViewMap = new ViewMap();
    private ViewMap consoleViewMap = new ViewMap();
    private ViewMap editorViewMap = new ViewMap();
    private ViewMap objectViewMap = new ViewMap();
    //TabWindows
    private TabWindow objectDocks;
    private TabWindow mainDocks;
    //RootWindows
    private RootWindow rootWindow;
    private RootWindow operationRoot;
    private RootWindow treeRoot;
    private RootWindow editorRoot;
    private RootWindow objectRoot;
    private RootWindow consoleRoot;
    //RootViews
    private View operationRootView;
    private View consoleRootView;
    private View treeRootView;
    private View editorRootView;
    private View objectRootView;
    private EventListenerList listeners;
    private View objectMenu;
    private EditorView editorView;
    private TreeView treeView;
    private boolean resourceViewOpen = false;

    public TreeView getTreeView() {
        return treeView;
    }
    private PropertyView propertyView;
    private OperationView selectedOperationView;
    private int opViewIndex;

    public int getOpViewIndex() {
        return opViewIndex;
    }
    private JTextArea console;
    private JButton saveButton;

    /**
     * Constructor for the GUIView class
     * Creates main frame, initializes all infonode views
     * and sets starting properties.
     * @param m reference to the GUIModel instance
     */
    public GUIView(GUIModel m) {
        guiModel = m;
        initJFrame();
        createRootWindow();
        setStartingWindowsProperties();
        setRootDropDisabled();
    }

    public ViewMap getOpViewMap() {
        return opViewMap;
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

        //Create main RootWindow and set it's dragrectangleborderwidth
        //to 0, i.e. invisible.
        rootWindow = DockingUtil.createRootWindow(rootViewMap, true);
        rootWindow.getRootWindowProperties().getDockingWindowProperties().setMaximizeEnabled(true);

        treeRoot = DockingUtil.createRootWindow(treeViewMap, true);
        operationRoot = DockingUtil.createRootWindow(opViewMap, true);
        objectRoot = DockingUtil.createRootWindow(objectViewMap, true);
        editorRoot = DockingUtil.createRootWindow(editorViewMap, true);
        consoleRoot = DockingUtil.createRootWindow(consoleViewMap, true);
        operationRoot.getPropertyChangeListeners();
        editorView = new EditorView(guiModel.getGlobalProperties());
        treeView = new TreeView(guiModel.getModel());

        operationRootView = new View("Operation Views", null, operationRoot);
        consoleRootView = new View("Console Views", null, consoleRoot);
        treeRootView = new View("Tree Views", null, treeRoot);
        editorRootView = new View("Editor Views", null, editorRoot);
        objectRootView = new View("Object Views", null, objectRoot);

        propertyView = new PropertyView(guiModel.getGlobalProperties());

        //Sets all inner root windows
        operationRoot.setWindow(mainDocks = new TabWindow(opViewMap.getView(opViewIndex)));
        operationRootView.getViewProperties().setAlwaysShowTitle(false);
        operationRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        operationRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);


        rootViewMap.addView(1, operationRootView);

        //Create consoltreeRoote
        consoleViewMap.addView(1, new View("console", null, new JScrollPane(console = new JTextArea())));
        consoleRoot.setWindow(new TabWindow(consoleViewMap.getView(1)));
        consoleRootView.getViewProperties().setAlwaysShowTitle(false);
        consoleRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        consoleRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);
        rootViewMap.addView(2, consoleRootView);

        //Create treeview
        treeViewMap.addView(1, new View("Tree view", null, treeView));
        treeRoot.setWindow(new TabWindow(treeViewMap.getView(1)));
        treeRootView.getViewProperties().setAlwaysShowTitle(false);
        treeRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        treeRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);
        rootViewMap.addView(3, treeRootView);

        editorViewMap.addView(1, new View("Editor view", null, editorView));
        editorRoot.setWindow(new TabWindow(editorViewMap.getView(1)));
        editorRootView.getViewProperties().setAlwaysShowTitle(false);
        editorRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        editorRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);
        rootViewMap.addView(4, treeRootView);

        //Test (adding save button to object attribute window) should be cleaned up!!!!
        JPanel objectView = new JPanel();
        saveButton = new JButton(new ImageIcon(SequencePlanner.class.getResource("resources/icons/save.png")));
        objectView.add(saveButton);

        objectMenu = new View("Object attribute view", null, objectView);
        objectViewMap.addView(1, objectMenu);
        objectViewMap.addView(2, new View("Property view", null, propertyView));

//        objectRoot.setWindow(new SplitWindow(false, 0.2f, objectViewMap.getView(1), objectDocks = new TabWindow(objectViewMap.getView(2))));
        objectRoot.setWindow(objectDocks = new TabWindow(objectViewMap.getView(2)));

        objectRootView.getViewProperties().setAlwaysShowTitle(false);
        objectRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        objectRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);

        rootViewMap.addView(5, treeRootView);

        //--------------------

        //Set window starting layout. Should perhaps be moved to a default layout object.
        setRootWindowProportions();
        this.getContentPane().add(rootWindow);

        printToConsole("Welcome to SP 2.0");
    }

    private void setRootDropDisabled() {
        DropFilter df = new DropFilter() {

            @Override
            public boolean acceptDrop(DropInfo dropInfo) {
                InteriorDropInfo inter = (InteriorDropInfo) dropInfo;

                if (inter.getDropWindow() instanceof DockingWindow || inter.getWindow() instanceof DockingWindow || inter.getWindow() == rootWindow || inter.getDropWindow() instanceof RootWindow || inter.getDropWindow() instanceof View) {
                    return false;
                }
                return true;
            }
        };
        rootWindow.getRootWindowProperties().getDockingWindowProperties().getDropFilterProperties().setInteriorDropFilter(df);
        operationRoot.getRootWindowProperties().getDockingWindowProperties().getDropFilterProperties().setInteriorDropFilter(df);
        treeRoot.getRootWindowProperties().getDockingWindowProperties().getDropFilterProperties().setInteriorDropFilter(df);
        editorRoot.getRootWindowProperties().getDockingWindowProperties().getDropFilterProperties().setInteriorDropFilter(df);
        objectRoot.getRootWindowProperties().getDockingWindowProperties().getDropFilterProperties().setInteriorDropFilter(df);
        consoleRoot.getRootWindowProperties().getDockingWindowProperties().getDropFilterProperties().setInteriorDropFilter(df);
    }

    /**
     * All starting properties should be specified here.
     */
    private void setStartingWindowsProperties() {

        rootWindow.getRootWindowProperties().getSplitWindowProperties().setContinuousLayoutEnabled(true);
        rootWindow.getRootWindowProperties().setRecursiveTabsEnabled(false);
        rootWindow.getWindowProperties().setDragEnabled(true);
        rootWindow.getRootWindowProperties().getSplitWindowProperties().setDividerLocationDragEnabled(true);

        rootWindow.getRootWindowProperties().getSplitWindowProperties().setDividerSize(4);
        rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setDirection(Direction.RIGHT);
        rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setOrientation(Direction.UP);
        rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setVisible(true);
    }

    /**
     * Empties the opViewMap and removes all tabs from mainDocks.
     */
    public void closeAllViews() {
        for (int i = 1; opViewMap.getViewCount() != 0; i++) {
            if (opViewMap.getView(i) != null) {
                opViewMap.getView(i).close();
            }
            opViewMap.removeView(i);
        }

        //operationRoot.remove(mainDocks);
        mainDocks = new TabWindow();

    }

    /**
     * Not yet implemented
     */
    public void updateViews() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *Updates the state of the property tree view
     */
    public void updatePropertyView() {
        propertyView.updateTree();
    }

    public void updateEditorView() {
        editorView.setEditorTreeModel(guiModel.getModel().getGlobalProperties());
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
     *  Visualization
     *      Brute Force
     *      Add Self-contained operations from file
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

    private JMenu fileMenu, edit, project, convert, mp, em, windows, visualization, help;
    private JMenuItem newOperationView, newResourceView, exit, preferences, addAll,
            open, save, saveAs, close, defaultWindows, saveEFAo, saveEFAr, saveCost, saveOptimal, identifyr,
            printProduct, efaForTrans, updateAfterTrans, efaForMP, bruteForceVisualization, addOperationsFromFile,
            normalEFA, reduceEFA, about, shortCommands;;

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
        em.add(normalEFA = new JMenuItem("Normal EFA"));
        em.add(reduceEFA = new JMenuItem("Reduced EFA"));
        this.add(em);
        //Visualization
        visualization = new JMenu("Visualization");
        visualization.add(bruteForceVisualization = new JMenuItem("Brute Force"));
        visualization.add(addOperationsFromFile = new JMenuItem("Add Selfcontained operations from file"));
        this.add(visualization);

        windows = new JMenu("Windows");
        windows.add(defaultWindows = new JMenuItem("Default Windows"));
        this.add(windows);

        //Help
        help = new JMenu("Help");
        help.add(shortCommands = new JMenuItem("Short commands"));
        help.add(about = new JMenuItem("About"));
        this.add(help);

        //Add menues to menubar
        mb.add(fileMenu);
        mb.add(edit);
        mb.add(project);
        mb.add(convert);
        mb.add(mp);
        mb.add(em);
        mb.add(windows);
        mb.add(visualization);
        mb.add(help);
        return mb;


    }//End createMenu
    //<editor-fold defaultstate="collapsed" desc="AddListener Methods">
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

    public void addDefWindL(ActionListener l) {
        defaultWindows.addActionListener(l);
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

    public void addNormalEFA(ActionListener l) {
        normalEFA.addActionListener(l);
    }

    public void addReducedEFA(ActionListener l) {
        reduceEFA.addActionListener(l);
    }

    public void addEditorListener(MouseAdapter l) {
        editorView.addMouseListener(l);
    }

    //Call the view to add a listener to the model? oO
    public void addTreeModelListener(TreeModelListener l) {
        guiModel.addTreeModelListener(l);
    }

    public void addSavePropViewL(ActionListener l) {
        saveButton.addActionListener(l);
    }

    public void addBruteForceVisualizationL(ActionListener l) {
        bruteForceVisualization.addActionListener(l);
    }

    public void addAddOperationsFromFileL(ActionListener l) {
        addOperationsFromFile.addActionListener(l);
    }
    public void addShortCommandsL(ActionListener l){
        shortCommands.addActionListener(l);
    }
    public void addAboutL(ActionListener l){
        about.addActionListener(l);
    }
//End listeners 
    //</editor-fold>

    /**
     * Opens a new window with a preference pane in it.
     */
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
//Should not be done here.. selectedOperationView is only updated when adding new tabs!
        opView.addmxIEventListener(this);
        selectedOperationView = opView;
        propertyView.setOpView(opView);
//----- 
        opViewIndex++;
        View newView = new View(name, null, opView);
        opViewMap.addView(opViewIndex, newView);

        if (mainDocks.getChildWindowCount() == 0) {
            operationRoot.setWindow(mainDocks = new TabWindow(opViewMap.getView(opViewIndex)));
        } else {
            mainDocks.addTab(newView);
            mainDocks.restore();
        }

    }

    /**
     * Adds a tab to the main tabwindow with a resourceview in it.
     */
    public void addResourceView() {
        opViewIndex++;
        View newView = new View(guiModel.getResourceView().getName(), null, guiModel.getResourceView());
        opViewMap.addView(opViewIndex, newView);

        mainDocks.addTab(newView);
        resourceViewOpen = true;
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

    public void setWindowLayout() {
        System.out.println("Focus:" + operationRoot.getLastFocusedChildWindow() + " @ " + " :end");
        //System.out.println("is closed? " + operationRoot.getChildWindow(1));
//--- Taking views from the model and recreating them (Not done yet, need to close the empty Tabs)
//+mainDocks.getWindowProperties().getTabProperties().getFocusedProperties().toString()
//------- Docking the undocked windows ---------

        DockingWindow tempViewMap = new TabWindow();
        try {
            tempViewMap = operationRoot.getLastFocusedChildWindow();


        } catch (NullPointerException e) {
            System.out.println("error: " + e);
        }
        for (int i = 1; i <= rootViewMap.getViewCount(); i++) {

            rootViewMap.getView(i).dock();
            rootViewMap.getView(i).restore();
        }
        for (int i = 1; i <= opViewMap.getViewCount(); i++) {

            if (opViewMap.getView(i) != null) {
                opViewMap.getView(i).dock();
                opViewMap.getView(i).restore();
            }
        }
        for (int i = 1; i <= editorViewMap.getViewCount(); i++) {
            editorViewMap.getView(i).dock();
            editorViewMap.getView(i).restore();
        }
        for (int i = 1; i <= treeViewMap.getViewCount(); i++) {
            treeViewMap.getView(i).dock();
            treeViewMap.getView(i).restore();
        }
        for (int i = 1; i <= consoleViewMap.getViewCount(); i++) {
            consoleViewMap.getView(i).dock();
            consoleViewMap.getView(i).restore();
        }
        for (int i = 1; i <= objectViewMap.getViewCount(); i++) {
            objectViewMap.getView(i).dock();
            objectViewMap.getView(i).restore();
        }

        closeAllViews();
        LinkedList<OperationView> modelViews = guiModel.getOperationViews();
        Iterator modelViews2 = modelViews.listIterator(0);
        opViewIndex = 0;
        while (modelViews2.hasNext()) {
            opViewIndex++;
            OperationView op12 = (OperationView) modelViews2.next();
            opViewMap.addView(opViewIndex, new View(op12.toString(), null,
                    op12));
            System.out.println(op12);

        }
        if (resourceViewOpen == true) {
            addResourceView();
        }

        //Recreate operation views window and view map
        operationRoot = DockingUtil.createRootWindow(opViewMap, true);
        operationRootView = new View("Operation Views", null, operationRoot);
        operationRoot.setWindow(mainDocks = new TabWindow(opViewMap.getView(1)));
        /*int count = 1;
        while (count <= opViewIndex) {
        
        mainDocks.addTab(opViewMap.getView(count));
        count = count + 1;
        System.out.println(count);
        System.out.println(opViewIndex);
        }*/
        try {
            for (int i = 0; i < tempViewMap.getChildWindowCount(); i++) {
                mainDocks.addTab(tempViewMap.getChildWindow(i));
            }

        } catch (NullPointerException e) {
            System.out.println("error 2: " + e);
        }
        operationRootView.getViewProperties().setAlwaysShowTitle(false);
        operationRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        operationRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);
        rootViewMap.addView(1, operationRootView);

        //Set original rootwindow proportions
        setRootWindowProportions();
        // mainDocks = new TabWindow(tempViewMap);
        mainDocks.restore();
    }

    /**
     * Set focus to the operation view containing the view data sent as parameter.
     * @param data ViewData
     */
    public void setFocusedOperationView(ViewData data) {
        for (int i = 1; opViewMap.getViewCount() >= i; i++) {
            if (data.getName().equals(opViewMap.getView(i).getTitle())) {
                TabWindow parent = (TabWindow) opViewMap.getView(i).getWindowParent();
                //Set the tab containing the View selected
                parent.setSelectedTab(parent.getChildWindowIndex(opViewMap.getView(i)));
            }
        }

    }

    /**
     * To get the viewmap used for SOPs.<br/>
     * The method is used for JUnit testing.
     * @return {@link ViewMap} for the SOP root window.
     */
    public ViewMap getSOPViewMap() {
        return opViewMap;
    }

    /**
     * Adds a new View with a PropertPanel to the objectViewMap.
     * Duplicate views are not allowed.
     * @param toInsert PropertyPanel to insert
     * @return false if a PropertyPanelView for the same operation already exists else true
     */
    public boolean addAttributePanelView(AttributePanel toInsert) {
        // Check if view exists.
        for (int i = 1; objectViewMap.getViewCount() >= i; i++) {

            if (objectViewMap.getView(i).getComponent() != null
                    && toInsert.getName().equals(objectViewMap.getView(i).getTitle())) {


                //Uncomment the line below if the focus should shift to the OjbectRootView
                //objectViewMap.getView(i).requestFocusInWindow();

                //Get the TabWindow containing the view
                TabWindow parent = (TabWindow) objectViewMap.getView(i).getWindowParent();

                //Set the tab containing the View selected
                parent.setSelectedTab(parent.getChildWindowIndex(objectViewMap.getView(i)));


                return false;


            }
        }
        View newView = new View(toInsert.getName(), null, new JScrollPane(toInsert));
        objectViewMap.addView(objectViewMap.getViewCount() + 1, newView);
        objectDocks.addTab(newView);
        objectDocks.restore();


        return true;
    }

    /**
     * To set the layout/proportions between splitwindow in root/main window
     */
    private void setRootWindowProportions() {
        rootWindow.setWindow(
                new SplitWindow(false, 0.85f, //Console takes up 15% of the frame.
                new SplitWindow(true, 0.15f, treeRootView,
                new SplitWindow(true, 0.75f, operationRootView,
                new SplitWindow(false, 0.7f, objectRootView, editorRootView))),
                consoleRootView));
    }

}
