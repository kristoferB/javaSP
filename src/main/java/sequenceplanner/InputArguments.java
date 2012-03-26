package sequenceplanner;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sequenceplanner.gui.controller.GUIController;

/**
 * To handle input arguments to SP
 * @author patrik
 */
public class InputArguments {

    private final GUIController mGUIController;
    private final String[] mInputArguments;

    public InputArguments(GUIController mGUIController, String[] mInputArguments) {
        this.mGUIController = mGUIController;
        this.mInputArguments = mInputArguments;
    }

    public void run() {
        for (int i = 0; i < mInputArguments.length; i += 2) {

            //Is there a second argument?
            if (i + 1 > mInputArguments.length) {
                return;
            }

            final String command = mInputArguments[i].toLowerCase();
            final String input = mInputArguments[i + 1];
            Matcher matcher;

            //To Read from file at start up--------------------------------------
            matcher = Pattern.compile("fromps").matcher(command);
            if (matcher.find()) {
                System.out.println("fromPS");
                final String path = input;
                final File file = new File(path);
                mGUIController.parseTextFile(file);
            }

            //Lift non-blocking problem + visualization DARPA--------------------
            matcher = Pattern.compile("weightdarpa").matcher(command);
            if (matcher.find()) {
                System.out.println("weightDARPA");
                final String path = input;
                final File file = new File(path);
                mGUIController.weightNonBlockingPlusVisualization(file);
            }
            
            // Intentional xml
            matcher = Pattern.compile("intentional").matcher(command);
            if (matcher.find()) {
                System.out.println("Intentional");
                final String path = input;
                mGUIController.intentionalXMLVisualize(path);
            }
        }
    }
}
