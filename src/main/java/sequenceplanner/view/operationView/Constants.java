package sequenceplanner.view.operationView;

import java.awt.Color;
import java.awt.Insets;

/**
 *
 * @author Erik
 */
public class Constants {

    //Cell types
    public final static int OP = 0;
    public final static int SOP = 1;
    public final static int PARALLEL = 2;
    public final static int ALTERNATIVE = 3;
    public final static int ARBITRARY = 4;
    
    public static final Insets SOP_INSET = new Insets(20, 20, 20, 20);
    public static final Insets ALTERNATIVE_INSET = SOP_INSET;
    public static final Insets PARALLEL_INSET = new Insets(30, 10, 30, 10);
    public static final Insets ARBITRARY_INSET = SOP_INSET;
    //Colors
    public static final Color DEFAULT_OPERATION_COLOR = new Color(206, 229, 164);
    
    //Determine distance before and after inserted / autoarranged cell
    public static double BEFORE_CELL = 25;
    public static double AFTER_CELL = 25;
    public static double SEQUENCE_MAX_HEIGHT = 0;
    //What should the distance between two sequences be?
    public static double SEQUENCE_DISTANCE = 20;
    //Note only top will be used.
    public static Insets ROOTBORDER_DISTANCE = new Insets(30, 20, 30, 10);
//   public static String AND = "^";
//   public static String OR = "V";
//   public static String ENDING = "_";
//
//   public static String VIEW = "";
    //Only on my computer/Vista
    public static String AND = "\u2227";
    public static String OR = "\u2228";
    public static String ENDING = "_";
    public static String VIEW = "\u2318";
    //Only Vista.
    public static String FILEFORMAT = ".sopx";
}


/*
 * 
 * Whos TODO list?
 *  - Autoarrange
 *  - One view per hiearchy operation
 *  - Autocenter sequence
 *  - XML save
 */