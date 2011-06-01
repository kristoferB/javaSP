/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2;

/**
 *
 * @author EXJOBB SOCvision
 */
public class EFAVariables {

    // Options for constructor to the algorithms
    public static int OPTION_ONE = 1;
    public static int OPTION_TWO = 2;
    public static int OPTION_THREE = 3;
    public static int OPTION_FOUR = 4;
    public static int OPTION_FIVE = 5;
    public static String OPERATION_NAME_PREFIX = "Op";
    public static String VARIABLE_NAME_PREFIX = "V_";
    public static String ID_PREFIX = "_Id";
    // Cost string for EFA names
    public static String COST_STRING = "_cost:";
    public static String CYCLE_TIME_STRING = "makespan = ";
    public static String COST_AUTOMATA_POSTFIX = "_TDFA";
    public static String EFA_NAME_POSTFIX = "_EFA";
    // Postfixes for state names
    public static String STATE_INITIAL_POSTFIX = "_i";
    public static String STATE_EXECUTION_POSTFIX = "_e";
    public static String STATE_FINAL_POSTFIX = "_f";
//	public static String STATE_COST_POSTFIX = "_c";
    // Variables
    public static String VARIABLE_NAME_POSTFIX = "";
    public static String VARIABLE_DUMMY_POSTFIX = "_dummy";
    public static String VARIABLE_INITIAL_STATE = "0";
    public static String VARIABLE_EXECUTION_STATE = "1";
    public static String VARIABLE_FINAL_STATE = "2";
    // Operators for EFA guards.
    public static String EFA_AND = "&";
    public static String EFA_OR = "|";
    public static String EFA_BOOK = "+=1;";
    public static String EFA_UNBOOK = "-=1;";
    public static String EFA_EQUAL = "==";
    public static String EFA_SET = "=";
    public static String EFA_STRICTLY_LESS_THAN = "<";
    // Never change these two.
    public static String EFA_PLUS_ONE = "+=1;";
    public static String EFA_MINUS_ONE = "-=1;";
    // Operators in Sequence Planner
    public static String SP_AND = "A";
    public static String SP_OR = "V";
    public static String SP_BOOK_RESOURCE = "+";
    public static String SP_UNBOOK_RESOURCE = "-";
    public static String EFA_START_EVENT_PREFIX = "Start_";
    public static String EFA_STOP_EVENT_PREFIX = "Stop_";
}
