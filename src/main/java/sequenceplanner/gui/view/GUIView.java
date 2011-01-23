package sequenceplanner.gui.view;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.spIcon.IconHandler;

/**
 *Main view class for the gui package.
 * @author qw4z1
 */
public class GUIView extends JFrame {
    //model of the gui

    private GUIModel guiModel;
    private ViewMap mainViewMap = new ViewMap();
    private RootWindow rootWindow;

    private ViewMap opViewMap = new ViewMap();
    private RootWindow opRootWindow = DockingUtil.createRootWindow(opViewMap, rootPaneCheckingEnabled);


    private TabWindow tab1 = new TabWindow();
    public GUIView() {
        initJFrame();
    }

    private void initJFrame() {
        setTitle("Sequence Planner");
        setIconImage(IconHandler.getNewIcon("/sequenceplanner/resources/icons/icon.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SPMenuBar menuBar = new SPMenuBar();
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
     *
     */
    private void createRootWindow() {
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

}
