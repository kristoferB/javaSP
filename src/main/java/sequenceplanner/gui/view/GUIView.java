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
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.spIcon.IconHandler;

/**
 *Main view class for the gui package.
 * @author qw4z1
 */
public class GUIView extends JFrame {
    //model of the gui

    private GUIModel guiModel;
    private RootWindow mainRoot;

    public GUIView(GUIModel guiModel) {
        initJFrame();
    }

    private void initJFrame() {
        setIconImage(IconHandler.getNewIcon("/sequenceplanner/resources/icons/icon.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SPMenuBar menuBar = new SPMenuBar();
        setLocation(getEnvBounds().x, getEnvBounds().y);
        setSize(getEnvBounds().width, getEnvBounds().height);
        setLayout(new BorderLayout());
        mainRoot = DockingUtil.createRootWindow(guiModel.getViewMap(), true);
        mainRoot.setWindow(new SplitWindow(true, 0.15f, new TabWindow(),new TabWindow()));

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
}
