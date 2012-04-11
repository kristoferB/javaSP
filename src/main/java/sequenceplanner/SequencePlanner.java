// start command example:
// run SP without UI and Intentional input XML format
// isnw infile outfile
// isnw temp/car_twoOp.xml temp/r.xml 
//
// run SP with UI and Intentional input
// intentional inputfile

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
    public static void main(final String[] args) {
// Docking windwos should be run in the Swing thread
        
        if (argsThatRunWithoutWindow(args)) return;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                GUIModel model = new GUIModel();
                GUIView view = new GUIView(model);
                GUIController gc = new GUIController(model, view);
                
                //Take action for input arguments
                new InputArguments(gc, args).run();

            }
        });
        

    }

    private void initiateLogger() {
        BasicConfigurator.configure();
        Logger l = Logger.getRootLogger();
        Logger.getRootLogger().setLevel(Level.ALL);
    }
    
    private static boolean argsThatRunWithoutWindow(final String[] args){
        
        //Is there a second argument?
        if (args.length < 2) return false;
        
        if (!args[0].toLowerCase().equals("intentionalnowindow")){
            if (!args[0].toLowerCase().equals("isnw")) return false;
        }
        if (args.length == 3 || args.length == 4 ){
            boolean includeSOP = false;
            if (args.length == 4 && args[3].toLowerCase().equals("-sop")) includeSOP = true;
            
            sequenceplanner.IO.XML.IntentionalXML.IntentionalWithoutWindowExecuter exec =
                new sequenceplanner.IO.XML.IntentionalXML.
                    IntentionalWithoutWindowExecuter(args[1],args[2], includeSOP);
            
        } else return false;
        
        return true;
    }
}
