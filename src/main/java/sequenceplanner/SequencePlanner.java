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
//FIXME EOH: Refact, move icon functions to new class.
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
      public void run() {
        GUIModel model = new GUIModel();
        GUIView view = new GUIView(model);
        new GUIController(model, view);
      }
    });
        

    }

    public void initiateLogger() {
        BasicConfigurator.configure();
        Logger l = Logger.getRootLogger();
        l.getRootLogger().setLevel(Level.ALL);
    }
}
