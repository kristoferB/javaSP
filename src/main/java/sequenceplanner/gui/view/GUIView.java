package sequenceplanner.gui.view;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;

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

import sequenceplanner.model.data.ViewData;

import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.utils.IconHandler;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.gui.controller.TabTitleObserver;
import sequenceplanner.view.resourceView.ResourceView;
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
    private ViewMap iViewMap = new ViewMap();
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
    private TreeView treeView;
    private View mResourceView = null;
    private boolean resourceViewOpen = false;

    

    public TreeView getTreeView() {
        return treeView;
    }
    private int opViewIndex;

    public int getOpViewIndex() {
        return opViewIndex;
    }
    private static JTextArea console;
    private JButton saveButton;
    /**
     * To map right id with right view. Both for operation views and object views.
     */
    private Map<View, Integer> mViewIdMap = new HashMap<View, Integer>();

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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //Take case of in GUIController
        setFocusable(true); //To enable key listener

    }

    public ViewMap getOpViewMap() {
        return opViewMap;
    }

    public ViewMap getObjectViewMap() {
        return objectViewMap;
    }

    /**
     * To change title in main titlebar<br/>
     * Title is always prefixed with <b>Sequence Planner - <b/><br/>
     * null as input gives the default title: <b>Sequence Planner - New Project<b/><br/>
     * @param iTitle
     */
    public void changeTitle(String iTitle) {
        if (iTitle == null) {
            iTitle = "New Project";
        }
        if (!iTitle.equals("")) {
            iTitle = " - " + iTitle;
        }
        setTitle("Sequence Planner" + iTitle);
    }

    /**
     * creates the frame
     */
    private void initJFrame() {
        changeTitle(null);
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
        rootWindow = DockingUtil.createRootWindow(iViewMap, true);
        rootWindow.getRootWindowProperties().getDockingWindowProperties().setMaximizeEnabled(true);

        treeRoot = DockingUtil.createRootWindow(treeViewMap, true);
        operationRoot = DockingUtil.createRootWindow(opViewMap, true);
        objectRoot = DockingUtil.createRootWindow(objectViewMap, true);
        editorRoot = DockingUtil.createRootWindow(editorViewMap, true);
        consoleRoot = DockingUtil.createRootWindow(consoleViewMap, true);
        operationRoot.getPropertyChangeListeners();
        treeView = new TreeView(guiModel.getModel());

        operationRootView = new View("SOP Views", null, operationRoot);
        consoleRootView = new View("Console Views", null, consoleRoot);
        treeRootView = new View("Tree Views", null, treeRoot);
        editorRootView = new View("Editor Views", null, editorRoot);
        objectRootView = new View("Object Views", null, objectRoot);


        //Sets all inner root windows
        operationRoot.setWindow(mainDocks = new TabWindow());
        operationRootView.getViewProperties().setAlwaysShowTitle(false);
        operationRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        operationRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);

        iViewMap.addView(1, operationRootView);

        //Create consoltreeRoote
        consoleViewMap.addView(1, new View("console", null, new JScrollPane(console = new JTextArea())));
        console.setEditable(false);
        consoleRoot.setWindow(new TabWindow(consoleViewMap.getView(1)));
        consoleRootView.getViewProperties().setAlwaysShowTitle(false);
        consoleRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        consoleRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);
        iViewMap.addView(2, consoleRootView);

        //Create treeview
        treeViewMap.addView(1, new View("Tree view", null, treeView));
        treeRoot.setWindow(new TabWindow(treeViewMap.getView(1)));
        treeRootView.getViewProperties().setAlwaysShowTitle(false);
        treeRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        treeRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);
        iViewMap.addView(3, treeRootView);

        //Test (adding save button to object attribute window) should be cleaned up!!!!
//        JPanel objectView = new JPanel();
//        saveButton = new JButton(new ImageIcon(SequencePlanner.class.getResource("resources/icons/save.png")));
//        objectView.add(saveButton);
//
//        objectMenu = new View("Object attribute view", null, objectView);
//        addToViewMap(objectMenu, null, objectViewMap);

//        objectRoot.setWindow(new SplitWindow(false, 0.2f, objectViewMap.getView(1), objectDocks = new TabWindow(objectViewMap.getView(2))));
        objectRoot.setWindow(objectDocks = new TabWindow());

        objectRootView.getViewProperties().setAlwaysShowTitle(false);
        objectRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        objectRootView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getUndockButtonProperties().setVisible(true);

        iViewMap.addView(4, treeRootView);

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

    public void addToMaindocks(final DockingWindow iWindowToAdd) {
        if (mainDocks.getChildWindowCount() == 0) {
            operationRoot.setWindow(mainDocks = new TabWindow(iWindowToAdd));
        } else {
            mainDocks.addTab(iWindowToAdd);
            mainDocks.restore();
        }
    }

    public void addToObjectdocks(final DockingWindow iWindowToAdd) {
        if (objectDocks.getChildWindowCount() == 0) {
            objectRoot.setWindow(objectDocks = new TabWindow(iWindowToAdd));
        } else {
            objectDocks.addTab(iWindowToAdd);
            objectDocks.restore();
        }
    }

    /**
     * Removes all tabs/{@link View}s from mainDocks and objectDocks.
     */
    public void removeAllViews() {
        for (int i = 1; opViewMap.getViewCount() != 0; i++) {
            if (opViewMap.getView(i) != null) {
                opViewMap.getView(i).close();
            }
            opViewMap.removeView(i);
        }

        for (int i = 1; objectViewMap.getViewCount() != 0; i++) {
            if (objectViewMap.getView(i) != null) {
                objectViewMap.getView(i).close();
            }
            objectViewMap.removeView(i);
        }
    }

    public boolean removeOperationView(final TreeNode iTreeNode) {
        //init
        if (!Model.isView(iTreeNode.getNodeData())) {
            return false;
        }
        final ViewData viewData = (ViewData) iTreeNode.getNodeData();
        final String viewDataId = Integer.toString(viewData.getId());

        //-----------------------------------------------------------------------
        OperationView viewToRemove = null;
        for (final OperationView opView : guiModel.getOperationViews()) {
            if (Integer.toString(opView.mViewData.getId()).equals(viewDataId)) {
                viewToRemove = opView;
            }
        }
        if (viewToRemove != null) {
            guiModel.getOperationViews().remove(viewToRemove);
        }
        //-----------------------------------------------------------------------

        for (int i = 1; opViewMap.getViewCount() >= i; i++) {
            View view = opViewMap.getView(i);
            //System.out.println(view);
        }

        for (View view : mViewIdMap.keySet()) {
            System.out.println("index: " + mViewIdMap.get(view));
        }

        //do check
        for (int i = 1; i <= getOpViewMap().getViewCount(); i++) {
            final View view = getOpViewMap().getView(i);
            final Integer id = mViewIdMap.get(view);
            if (id != null) {
                if (viewDataId.equals(id.toString())) {
                    System.out.println("Start Remove");
                    removeView(getOpViewMap(), i);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes {@link View} for operation given in parameter <p>iTreeNode</p>
     * @param iTreeNode
     * @return true if {@link View} was closed else false (A view for operation
     * might not have been opened, so false is not bad in this case).
     */
    public boolean removeOperationObjectView(final TreeNode iTreeNode) {
        //init
        if (!Model.isOperation(iTreeNode.getNodeData())) {
            return false;
        }
        final OperationData opData = (OperationData) iTreeNode.getNodeData();
        final String opId = Integer.toString(opData.getId());

        //do check
        for (int i = 1; i <= getObjectViewMap().getViewCount(); i++) {
            final View view = getObjectViewMap().getView(i);
            final Integer id = mViewIdMap.get(view);
            if (id != null) {
                if (opId.equals(id.toString())) {
                    removeView(getObjectViewMap(), i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes {@link View} if view exists.
     * @param iViewMap map to look in
     * @param iId id for view in map
     * @return true if ok  else false
     */
    public static boolean removeView(final ViewMap iViewMap, final Integer iId) {
        if (iId < 1 || iId > iViewMap.getViewCount()) {
            return false;
        }
        if (iViewMap.getView(iId) != null) {
            iViewMap.getView(iId).close();
        }
        iViewMap.removeView(iId);
        return true;
    }

    public boolean setOperationViewFocus(final TreeNode iTreeNode) {
        final int index = getViewIndexInMap(iTreeNode, opViewMap);
        if (index == -1) {
            return false;
        }

        opViewMap.getView(index).close();
        opViewMap.getView(index).restore();
        return true;
    }

    public int getViewIndexInMap(final TreeNode iTreeNode, final ViewMap iViewMap) {
        //init
        final String treeNodeId = Integer.toString(iTreeNode.getNodeData().getId());

        //do check
        for (int i = 1; i <= iViewMap.getViewCount(); i++) {
            final View view = iViewMap.getView(i);
            final Integer id = mViewIdMap.get(view);
            if (id != null) {
                if (treeNodeId.equals(id.toString())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Not yet implemented
     */
    public void updateViews() {
        throw new UnsupportedOperationException("Not yet implemented");
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
    private JMenu fileMenu, importMenu, edit, convert, mp, em, windows, visualization, help;
    private JMenuItem newOperationView, newResourceView, exit, preferences, addAll,
            open, save, saveAs, close, defaultWindows, saveEFAo, saveEFAr, saveCost, saveOptimal, identifyr,
            printProduct, efaForTrans, updateAfterTrans, efaForMP, bruteForceVisualization, addOperationsFromPSTextFile,
            normalEFA, reduceEFA, about, shortCommands;

    ;

    private JMenuBar createMenu() {
        JMenuBar mb = new JMenuBar();
        //File menu
        fileMenu = new JMenu("File");
        fileMenu.add(open = new JMenuItem("Open"));
        fileMenu.add(save = new JMenuItem("Save"));
        fileMenu.add(saveAs = new JMenuItem("Save As"));
        fileMenu.add(close = new JMenuItem("Close"));
        fileMenu.addSeparator();
        importMenu = new JMenu("Import");
        importMenu.add(addOperationsFromPSTextFile = new JMenuItem("From .txt (Process Simulate style)"));
        fileMenu.add(importMenu);
        fileMenu.addSeparator();
        fileMenu.add(exit = new JMenuItem("Exit"));
        this.add(fileMenu);

        //Edit menu
        edit = new JMenu("Edit");
        edit.add(preferences = new JMenuItem("Preferences"));
        edit.add(addAll = new JMenuItem("Add all cells to new view"));
        this.add(edit);

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

        this.add(visualization);

        windows = new JMenu("Windows");
        windows.add(newOperationView = new JMenuItem("New Operation View"));
        windows.add(newResourceView = new JMenuItem("New Resource View"));
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

    public void addSavePropViewL(ActionListener l) {
        saveButton.addActionListener(l);
    }

    public void addBruteForceVisualizationL(ActionListener l) {
        bruteForceVisualization.addActionListener(l);
    }

    public void addOperationsFromPSTextFileL(ActionListener l) {
        addOperationsFromPSTextFile.addActionListener(l);
    }

    public void addShortCommandsL(ActionListener l) {
        shortCommands.addActionListener(l);
    }

    public void addAboutL(ActionListener l) {
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
    public View addNewOpTab(String name, OperationView opView) {
//Should not be done here.. selectedOperationView is only updated when adding new tabs!
        opView.addmxIEventListener(this);

//----- 
        opViewIndex++;
        final View newView = new View(name, null, opView);
        addToViewMap(newView, opView.mViewData.getId(), opViewMap);

        addToMaindocks(newView);

        //Observer for update of view title
        opView.mViewData.addObserver(new TabTitleObserver(newView));

        if (opView.mViewData.isClosed()) {
            System.out.println("start closed: " + opView.getName());
            newView.close();
        }
        return newView;
    }

    /**
     * Adds a tab to the main tabwindow with a resourceview in it.
     */
    public void addResourceView() {
        if (mResourceView == null) {
            opViewIndex++;
            final ResourceView resourceView = guiModel.createNewReView();
            mResourceView = new View(resourceView.getName(), null, resourceView);
            addToViewMap(mResourceView, guiModel.getModel().getResourceRoot().getId(), opViewMap);
            addToMaindocks(mResourceView);
        } else {
            mResourceView.dock();
            mResourceView.close();
            mResourceView.restore();
        }
    }

    public static synchronized void printToConsole(String text) {
        if (console != null)
            console.append(text + "\n");
        System.out.println("printToConsole: " + text);
    }

    @Override
    public void invoke(Object arg0, mxEventObject arg1) {
        //System.out.println("GUIView: " + "arg0: " + arg0 + " arg1: " + arg1);
    }

    /**
     * To reset all {@link View}s in all {@link ViewMap}s.<br/>
     * 1) Undocked windows are docked.<br/>
     * 2) Closed windows are opened.<br/>
     */
    public void setWindowLayout() {
        //Dock and resore or views
        dockAndRestoreViewMap(iViewMap);
        dockAndRestoreViewMap(opViewMap);
        dockAndRestoreViewMap(editorViewMap);
        dockAndRestoreViewMap(treeViewMap);
        dockAndRestoreViewMap(consoleViewMap);
        dockAndRestoreViewMap(objectViewMap);

        //Set original rootwindow proportions
        setRootWindowProportions();

        printToConsole("Windows has been restored.");

    }

    private void dockAndRestoreViewMap(final ViewMap iViewMap) {
        for (int i = 1; i <= iViewMap.getViewCount(); i++) {
            if (iViewMap.getView(i) != null) {
                iViewMap.getView(i).dock();
                iViewMap.getView(i).restore();
            }
        }
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
                if (parent != null) {
                    parent.setSelectedTab(parent.getChildWindowIndex(opViewMap.getView(i)));
                } else {
                    opViewMap.getView(i).restore();
                }
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
        final OperationData opData = toInsert.getOperationData();
        final Integer operationId = opData.getId();

        // Check if view exists.
        for (int i = 1; objectViewMap.getViewCount() >= i; i++) {

            final Integer currentId = mViewIdMap.get(objectViewMap.getView(i));

            if (objectViewMap.getView(i) != null && objectViewMap.getView(i).getComponent() != null && currentId != null && operationId.toString().equals(currentId.toString())) {

                //Uncomment the line below if the focus should shift to the OjbectRootView
                //objectViewMap.getView(i).requestFocusInWindow();
                TabWindow parent = (TabWindow) objectViewMap.getView(i).getWindowParent();
                //Get the TabWindow containing the view
                if (parent != null) {
                    parent.setSelectedTab(parent.getChildWindowIndex(objectViewMap.getView(i)));
                } else {
                    objectViewMap.getView(i).restore();
                }
                //Set the tab containing the View selected

                return false;

            }
        }
        final View newView = new View(opData.getName(), null, new JScrollPane(toInsert));
        addToViewMap(newView, operationId, objectViewMap);

        addToObjectdocks(newView);

        //Observer for update of view title
        opData.addObserver(new TabTitleObserver(newView));

        return true;
    }

    /**
     * To add {@link View}s to <code>objectViewMap</code>.<br/>
     * An extra {@link Integer} parameter is included to compare different views
     * in the map based on unique ids.
     * @param iViewToAdd
     * @param iId {@link OperationData} id or null if the view is not for an operation.
     */
    private void addToViewMap(final View iViewToAdd, Integer iId, ViewMap iViewMap) {
        if (iViewToAdd == null) {
            return;
        }
        if (iId == null) {
            iId = -1;
        }
        mViewIdMap.put(iViewToAdd, iId);
        iViewMap.addView(iViewMap.getViewCount() + 1, iViewToAdd);
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

    public void setModel(GUIModel guiModel) {
        this.guiModel = guiModel;
    }

}
