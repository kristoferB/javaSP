package sequenceplanner.gui.view;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.EventListenerList;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.spIcon.IconHandler;

/**
 *Main view class for the gui package. Updated by the GUIController and
 * shows the info in GUIModel.
 * @author qw4z1
 */
public class GUIView extends JFrame {
    //model of the gui

    private JMenuBar menuBar;
    private GUIModel guiModel;
    private ViewMap mainViewMap = new ViewMap();
    private RootWindow rootWindow;
    private ViewMap opViewMap = new ViewMap();
    private RootWindow opRootWindow = DockingUtil.createRootWindow(opViewMap, rootPaneCheckingEnabled);
    private EventListenerList listeners;
    private TabWindow tab1 = new TabWindow();

    public GUIView(GUIModel m) {
        guiModel = m;
        initJFrame();
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

        createRootWindow();

        this.setVisible(true);

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
        //TODO Peterkle: Stäng av så att man inte kan undocka första TabWindow.
        //dvs där vår treeview ska hamna.
        rootWindow = DockingUtil.createRootWindow(mainViewMap, true);
        rootWindow.getRootWindowProperties().getDockingWindowProperties().setCloseEnabled(false);
        opRootWindow.getRootWindowProperties().getDockingWindowProperties().setDragEnabled(false);
        opRootWindow.getRootWindowProperties().getDockingWindowProperties().setUndockEnabled(false);
        rootWindow.setWindow(
                new SplitWindow(true, 0.15f, tab1,
                new SplitWindow(true, 0.7f, opRootWindow,
                new SplitWindow(false, 0.5f, new TabWindow(), new TabWindow()))));
        this.getContentPane().add(rootWindow);
    }

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
     *  MP
     *      Print product types and op in model
     *      EFA for transport planning
     *      Update model after transport planning
     *      EFA for MP supervisor
     *
     * @author qw4z1
     */
    private JMenu fileMenu, edit, project, convert, mp;
    private JMenuItem newOperationView, newResourceView, exit, preferences, addAll,
            open, save, saveAs, close, saveEFAo, saveEFAr, saveCost, saveOptimal, identifyr,
            printProduct, efaForTrans, updateAfterTrans, efaForMP;

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

        //Add menues to menubar
        mb.add(fileMenu);
        mb.add(edit);
        mb.add(project);
        mb.add(convert);
        mb.add(mp);
        return mb;

    }//End createMenu

    //Menubar listeners
    public void addCreateOPL(ActionListener l) {
        newOperationView.addActionListener(l);
    }
    public void addCreateRVL(ActionListener l){
        newResourceView.addActionListener(l);
    }

    public void addExitL(ActionListener l) {
        exit.addActionListener(l);
    }

    public void addPrefL(ActionListener l) {
    }

    public void addAddCellsL(ActionListener l) {
    }

    public void addOpenL(ActionListener l) {
    }

    public void addSaveL(ActionListener l) {
    }

    public void addSaveAsL(ActionListener l) {
    }

    public void addCloseL(ActionListener l) {
    }

    public void addSaveEFAoL(ActionListener l) {
    }

    public void addSaveEFArL(ActionListener l) {
    }

    public void addSaveCostL(ActionListener l) {
    }

    public void addSaveOptAutomataL(ActionListener l) {
    }

    public void addIdentifyRL(ActionListener l) {
    }

    public void addPrintProdTypesL(ActionListener l) {
    }

    public void addEFAForTransL(ActionListener l){
    }

    public void addUpdateModelL(ActionListener l) {
    }

    public void addEFAForMPL(ActionListener l) {
    }
}
