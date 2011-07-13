package sequenceplanner;

import java.awt.Rectangle;
import java.io.File;
import javax.swing.SwingUtilities;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import sequenceplanner.IO.txt.ReadFromProcessSimulateTextFile;
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
    public static void main(final String[] args) {
// Docking windwos should be run in the Swing thread

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIModel model = new GUIModel();
        GUIView view = new GUIView(model);
        GUIController gc = new GUIController(model, view);

        for(String arg : args ) {
            System.out.println("arg " + arg);
        }

        //To Read from file at start up
        if(args.length >= 2) {
            if(args[0].equals("fromPS")) {
                final String path = args[1];
                final File file = new File(path);
                gc.parseTextFile(file);
//                final ReadFromProcessSimulateTextFile rftf = new ReadFromProcessSimulateTextFile(path, null, gc.getModel());
//                final boolean result = rftf.run();
//                GUIView.printToConsole("Result from text file parse: " + result);
            }
        }

      }
    });
        

    }

    private void initiateLogger() {
        BasicConfigurator.configure();
        Logger l = Logger.getRootLogger();
        Logger.getRootLogger().setLevel(Level.ALL);
    }
}
