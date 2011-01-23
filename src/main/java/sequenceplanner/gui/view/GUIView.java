package sequenceplanner.gui.view;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
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
    private SPMenuBar menuBar;
    private GUIModel guiModel;
    private ViewMap mainViewMap = new ViewMap();
    private RootWindow rootWindow;

    private ViewMap opViewMap = new ViewMap();
    private RootWindow opRootWindow = DockingUtil.createRootWindow(opViewMap, rootPaneCheckingEnabled);


    private TabWindow tab1 = new TabWindow();
    public GUIView(GUIModel m) {
        guiModel = m;
        initJFrame();
    }

    private void initJFrame() {
        setTitle("Sequence Planner");
        setIconImage(IconHandler.getNewIcon("/sequenceplanner/resources/icons/icon.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuBar = new SPMenuBar();
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
                new SplitWindow(true,0.7f,opRootWindow,
                new SplitWindow(false,0.5f,new TabWindow(),new TabWindow()))));
        this.getContentPane().add(rootWindow);
    }

    //Listener methods
    public void addCreateOpViewListener(ActionListener al) {
        
        //TODO add listener to everything that creates new operationviews.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void addCreateRViewListener(ActionListener al) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
