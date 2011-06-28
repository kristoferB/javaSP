package sequenceplanner;

import java.awt.Rectangle;
import javax.swing.SwingUtilities;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;

/**
 *
 * @author Erik Ohlson
 */
// TODO Handle comparing variables with values in sequenceconditions
// TODO Handle actions
// TODO Remove preconditions when cell is removed.
public class SequencePlanner {

    static int id = 0;
    static Logger logger = Logger.getLogger(SequencePlanner.class);

    public SequencePlanner() {
        initiateLogger();

    }
    /**
     * Show splash.
     *
     * @param r the r
     */
    private void showSplash(final Rectangle r) {
        Thread splash = new Thread() {

            @Override
            public void run() {
                SplashScreen ss = new SplashScreen();
                ss.showSplash(r);

            }
        };
        splash.start();

    }

    /**
     * The main method.
     *
     * @param args the args
     */
    public static void main(String[] args) {
// Docking windwos should be run in the Swing thread

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIModel model = new GUIModel();
        GUIView view = new GUIView(model);
        GUIController gc = new GUIController(model, view);
      }
    });
        

    }

    private void initiateLogger() {
        BasicConfigurator.configure();
        Logger l = Logger.getRootLogger();
        Logger.getRootLogger().setLevel(Level.ALL);
    }
}
