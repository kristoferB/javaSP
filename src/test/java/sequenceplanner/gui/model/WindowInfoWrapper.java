package sequenceplanner.gui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

/**
 * Possible wrapper class for windows in SP.
 * @author patrik
 */
public class WindowInfoWrapper {

    public static final String NBR_OF = "NBR_OF";
    public static final String NBR_OF_SAVED = "NBR_OF_SAVED";
    public static final String NBR_OF_UNSAVED = "NBR_OF_UNSAVED";
    public static final String NBR_OF_DOCKED = "NBR_OF_DOCKED";
    public static final String NBR_OF_UNDOCKED = "NBR_OF_UNDOCKED";
    public static final String NBR_OF_OPEN = "NBR_OF_OPEN";
    public static final String NBR_OF_UNOPEN = "NBR_OF_UNOPEN";

    String name = "";
    Set<WindowInfoWrapper> children = new HashSet<WindowInfoWrapper>();
    Boolean saved; //A window (think SOP window) can be created but it should not be possible to return to this window if it is not saved before closed
    Boolean docked; //docked or undocked to root window
    Boolean open; //The same as unhidden
    HashMap<String, Integer> childStatistics = new HashMap<String, Integer>();

    public WindowInfoWrapper() {
    }

    public String getName() {
        assertTrue("No name is given for window!",!name.equals(""));
        return name;
    }

    public Integer getStatistics(String key) {
        assertTrue(name + " lacks " + key,childStatistics.containsKey(key));
        return childStatistics.get(key);
    }
    //TODO:
    //How to add name of windows
    //How to keep the fileds "children" and "childStatistics" updated when the windows are modified.
}