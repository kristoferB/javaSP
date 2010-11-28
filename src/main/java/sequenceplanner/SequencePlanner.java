package sequenceplanner;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.util.DockingUtil;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

   public static ImageIcon getNewIcon(String dir) {
      return getNewIcon(dir, true);
   }

   public static ImageIcon getNewIcon(String dir, boolean resize) {
      if (dir != null && !dir.equals("")) {
         try {
            ImageIcon ico = new ImageIcon(SequencePlanner.class.getResource(dir));
            if (resize) {
               return new ImageIcon(ico.getImage().getScaledInstance(16, -1, Image.SCALE_SMOOTH));
            } else {
               return ico;
            }
         } catch (Exception e) {
            logger.error("getNewIcon: Inputted icon path is not present");
         }

      } else {
         logger.error("getNewIcon called with an empty string");
      }

      return null;
   }

   /**
    * Instantiates a new sequence planner.
    *
    * @param screen the screen
    */
   public SequencePlanner(int screen) {
      this();
      try {
         UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      } catch (Exception e) {
      }

      createStartWindow(screen);
   }

   /**
    * Creates the start window.
    *
    * @param screen the screen
    */
   private void createStartWindow(int screen) {

      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] gs = ge.getScreenDevices();

      //Add to screen # set by the flag screen=#, if possible
      int i = 0;
      Rectangle bounds;
//	   screen = 0;
      if (gs.length > screen) {
         i = screen;
         bounds = gs[screen].getDefaultConfiguration().getBounds();
      } else {
         i = 0;
         bounds = gs[i].getDefaultConfiguration().getBounds();
      }


      //TODO improve with new Java 6 splash screen feature. Probably easiest with maven-jar-plugin
	   /*
       * The new feature make it possible to show a simple splash screen even before JVM started.
       * This can then be replaced with the present splash screen.
       */
      showSplash(bounds);

      SPContainer SPC = new SPContainer();
      JFrame sc = new JFrame("Sequence Planner");
      sc.setIconImage(getNewIcon("/sequenceplanner/resources/icons/icon.png").getImage());
      sc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      SPMenuBar menuBar = new SPMenuBar(SPC);
      sc.setJMenuBar(menuBar);
      sc.setLocation(bounds.x, bounds.y);
      sc.setSize(bounds.width, bounds.height);
      sc.setLayout(new BorderLayout());
     // sc.setContentPane(new SPContainer());


      RootWindow rootWindow = DockingUtil.createRootWindow(SPC.getViewMap(), true);

      rootWindow.setWindow(new SplitWindow(true,0.1f,SPC.getNonOpView(0),SPC.getOpView(0)));
      sc.getContentPane().add(rootWindow);


      //rootWindow.add(menuBar, NORTH)
      menuBar.setEnabled(true);
      sc.setVisible(true);
      sc.toFront();
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
      int screen = -1;

      if (args.length > 0) {
         for (int i = 0; i < args.length; i++) {
            int eq = args[i].indexOf("=");
            if (eq > 0 && args[i].substring(0, eq).equals("screen")) {
               screen = Integer.parseInt(args[i].substring(eq + 1));
            }
         }
      }

      if (screen < 0) {
         System.out.println("Only allowed input is screen=[What screen to start the application on]");
         System.out.println("-----------------\nUsing default values (Screen=0)s\n----------------");
         screen = 0;
      }
      final int screen1 = screen;
      SwingUtilities.invokeLater(new Runnable() {

         public void run() {
            new SequencePlanner(screen1);
         }
      });
   }

   public void initiateLogger() {
      BasicConfigurator.configure();
      Logger l = Logger.getRootLogger();
      l.getRootLogger().setLevel(Level.ALL);
   }
}
