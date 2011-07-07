package sequenceplanner.utils;

/**
 * Small singelton class for trimmming strings to proper condition input format
 * for easy acccess throughout the code.
 * @author QW4z1
 */
public class StringTrimmer {

    private StringTrimmer() {
    }

    public static StringTrimmer getInstance() {
        return StringTrimmerHolder.INSTANCE;
    }

    private static class StringTrimmerHolder {

        private static final StringTrimmer INSTANCE = new StringTrimmer();
    }
    
    /**
     * Trims Condition.toString() string to proper AStringToConditionParser format.
     * @param conditionString String to trim
     * @return String in proper "id9==f" format
     */
    public String stringTrim(String conditionString) {
        String conditionString2 = "";
        String[] st = conditionString.split("and");

        st[0] = st[0].substring(1);
        for (String x : st) {
            conditionString2 = conditionString2 + "id" + x + "and";
        }
        //Remove the last )and
        conditionString = conditionString2.substring(0, conditionString2.length() - 4);

        String[] st2 = conditionString.split("or");
        conditionString2 = "";
        for (String x : st2) {
            conditionString2 = conditionString2 + "id" + x + "or";
        }
        //Remove the last or and the double "id"
        conditionString = conditionString2.substring(2, conditionString2.length() - 2);

        return conditionString;
    }
}
