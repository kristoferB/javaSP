/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter;

/**
 *
 * @author EXJOBB SOCvision
 */
public class EFAVariables {

    // Options for constructor to the algorithms
    static int OPTION_ONE = 1;
    static int OPTION_TWO = 2;
    static int OPTION_THREE = 3;
    static int OPTION_FOUR = 4;
    static int OPTION_FIVE = 5;
    static String OPERATION_NAME_PREFIX = "Op";
    static String VARIABLE_NAME_PREFIX = "V_";
    static String ID_PREFIX = "_Id";
    // Cost string for EFA names
    static String COST_STRING = "_cost:";
    static String CYCLE_TIME_STRING = "makespan = ";
    static String COST_AUTOMATA_POSTFIX = "_TDFA";
    static String EFA_NAME_POSTFIX = "_EFA";
    // Postfixes for state names
    static String STATE_INITIAL_POSTFIX = "_i";
    static String STATE_EXECUTION_POSTFIX = "_e";
    static String STATE_FINAL_POSTFIX = "_f";
//	static String STATE_COST_POSTFIX = "_c";
    // Variables
    static String VARIABLE_NAME_POSTFIX = "";
    static String VARIABLE_DUMMY_POSTFIX = "_dummy";
    static String VARIABLE_INITIAL_STATE = "0";
    static String VARIABLE_EXECUTION_STATE = "1";
    static String VARIABLE_FINAL_STATE = "2";
    // Operators for EFA guards.
    static String EFA_AND = "&";
    static String EFA_OR = "|";
    static String EFA_BOOK = "+=1;";
    static String EFA_UNBOOK = "-=1;";
    static String EFA_EQUAL = "==";
    static String EFA_SET = "=";
    static String EFA_STRICTLY_LESS_THAN = "<";
    // Never change these two.
    static String EFA_PLUS_ONE = "+=1;";
    static String EFA_MINUS_ONE = "-=1;";
    // Operators in Sequence Planner
    public static String SP_AND = "A";
    public static String SP_OR = "V";
    static String SP_BOOK_RESOURCE = "+";
    static String SP_UNBOOK_RESOURCE = "-";
    static String EFA_START_EVENT_PREFIX = "Start_";
    static String EFA_STOP_EVENT_PREFIX = "Stop_";
}
