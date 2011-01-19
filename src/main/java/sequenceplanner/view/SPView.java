/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.view;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.util.DockingUtil;
import sequenceplanner.SPContainer;
import sequenceplanner.spIcon.IconHandler;
import sequenceplanner.gui.view.SPMenuBar;
import sequenceplanner.model.Model;

/**
 *
 * @author qw4z1
 */
public class SPView extends JFrame {

    private Model model;

    public SPView(Model model) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        //Add to screen # set by the flag screen=#, if possible
        int i = 0;
        Rectangle bounds;

        i = 0;
        bounds = gs[i].getDefaultConfiguration().getBounds();


        JFrame sc = new JFrame("Sequence Planner");
        sc.setIconImage(IconHandler.getNewIcon("/sequenceplanner/resources/icons/icon.png").getImage());
        sc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SPMenuBar menuBar = new SPMenuBar(model);
        sc.setJMenuBar(menuBar);
        sc.setLocation(bounds.x, bounds.y);
        sc.setSize(bounds.width, bounds.height);
        sc.setLayout(new BorderLayout());
        //  sc.setContentPane(new SPContainer());

        RootWindow rootWindow = DockingUtil.createRootWindow(SPC.getViewMap(), true);

        rootWindow.setWindow(new SplitWindow(true, 0.15f, SPC.getNonOpView(0), new TabWindow(SPC.getOpView(0))));


        sc.getContentPane().add(rootWindow);
        SPC.setRoot(rootWindow);

        menuBar.setEnabled(true);
        sc.setVisible(true);
        sc.toFront();
    }
}
