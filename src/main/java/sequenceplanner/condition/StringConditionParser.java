package sequenceplanner.condition;


/**
 * Simple singleton parser class.
 * Only has two public methods. getInstance and getParseConditionString.
 * @author Qw4z1
 */
public class StringConditionParser {

    public StringConditionParser() {

    }
    private String op, var, val;
    private String test = "1007>=1";
    private static String[] operatorPattern = {"==", "!=", ">=", "<=", "+=", "-=", "->", ">", "<", "="};

    public static StringConditionParser getInstance() {
        return StringConditionParserHolder.INSTANCE;
    }

    private static class StringConditionParserHolder {

        private static final StringConditionParser INSTANCE = new StringConditionParser();
    }

    /**
     * Takes a String as argument and 
     * parses the String into a {@link Condition}
     * @param String Conditionstring
     * @return the {@link Condition} 
     */
    public Condition parseConditionString(String conditionString) {
        Condition condition = new Condition();

        System.out.println("Condition variable: " + (var = test.substring(0, 4)));
        if(test.length() == 7){
            System.out.println("Condition operator: " + (op = test.substring(4, 6)));
            System.out.println("Condition value: " + (val = test.substring(6, 7)));
        }else if(test.length() == 6){
            System.out.println("Condition operator: " + (op = test.substring(4, 5)));
            System.out.println("Condition value: " + (val = test.substring(5, 6)));
        }else {
            System.out.println("Wrong condition");
            return null;
        }

        //Check if operator is valid (==,!=,>,<,>=,<=,=,+=,-=,->)
        for (String opList : operatorPattern) {
            if (op.equals(opList)) {
                System.out.println(op + " is valid");
            }
        }

        if(Integer.parseInt(val) == 0 || Integer.parseInt(val) == 1 ||Integer.parseInt(val) == 2){
            System.out.println(val + " is valid");
        }else {
            System.out.println("Wrong condition");
            return null;
        }
        //ConditionStatement condState = new ConditionStatement(var,op,val);

        return condition;
    }
}
