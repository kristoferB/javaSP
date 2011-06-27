package sequenceplanner.condition;

import java.util.regex.*;
import java.util.HashMap;
import java.util.Map;
import sequenceplanner.condition.ConditionStatement.Operator;

/**
 * Simple singleton parser class.
 * Only has two public methods. getInstance and getParseConditionString.
 * @author Qw4z1
 */
public class StringConditionParser {

    public StringConditionParser() {
    }
    //private String test = "id1007!=0&&id1008<i&&id1009>=e";
    private static String guard = "([=><!][=]|[><])";
    private static String action = "([+-]=)"; //Fixa för =
    private static String id = "(id\\d{4})";
    private static String value = "([012ief])";
    private static String type = "(&&|&|and|or|\\|\\||\\|)";
    private Map<String, Operator> operators = new HashMap<String, Operator>();

    {
        operators.put("!=", ConditionStatement.Operator.NotEqual);
        operators.put("==", ConditionStatement.Operator.Equal);
        operators.put("<=", ConditionStatement.Operator.LessEq);
        operators.put(">=", ConditionStatement.Operator.GreaterEq);
        operators.put("=", ConditionStatement.Operator.Assign);
        operators.put("<", ConditionStatement.Operator.Less);
        operators.put(">", ConditionStatement.Operator.Greater);
        operators.put("+=", ConditionStatement.Operator.Inc);
        operators.put("-=", ConditionStatement.Operator.Dec);
        operators.put("->", ConditionStatement.Operator.PointAt);
    }

    public static StringConditionParser getInstance() {
        return StringConditionParserHolder.INSTANCE;
    }

    private static class StringConditionParserHolder {

        private static final StringConditionParser INSTANCE = new StringConditionParser();
    }

    /**
     * Takes a String as argument and 
     * parses the String into a {@link ConditionElement}
     * @param String Conditionstring
     * @return the {@link ConditionElement}
     */
    public ConditionExpression parseConditionString(String conditionString) {

        System.out.println("The real string: " + conditionString);
//        Condition condition = new Condition();
        String op = "", var = "", val = "";
        String conditions = "";
        String conditions2 = "";
        String copyString = "";
        String tempCond = "";

        //Remove all whitespace characters
        conditionString = conditionString.replaceAll("\\s", "");
        //To lower case
        conditionString = conditionString.toLowerCase();

        System.out.println("Replace whitespace: " + conditionString);
        final Pattern guardPattern = Pattern.compile(type + id + guard + value);
        final Pattern actionPattern = Pattern.compile(type + id + action + value);
        final Pattern rootPattern = Pattern.compile(id + guard + value + type);
//        Pattern rootPattern = Pattern.compile(id + guard + value);
        Matcher matcher = guardPattern.matcher(conditionString);
        Matcher matcher2 = actionPattern.matcher(conditionString);
        Matcher matcher3 = rootPattern.matcher(conditionString);
        copyString = conditionString;
        ConditionExpression ce = null;
//        ConditionExpression right = new ConditionExpression();



        //to remove the root from the string, since either the first or the last one differs from the pattern
        if (matcher3.find()) {
//            System.out.println("matcher3.start(): " + matcher3.start());
//            System.out.println("matcher3.end(): " + matcher3.end());
//            System.out.println("matcher3.group(3): " + matcher3.group(3));
            tempCond = conditionString.substring(matcher3.start(), matcher3.end() - matcher3.group(4).length());
            System.out.println("Root: " + tempCond);
            if (matcher3.group(4).equals("&&") || matcher3.group(4).equals("&") || matcher3.group(4).equals("and")) {
                ConditionStatement cs = createConditionStatement(matcher3.group(1), operators.get(matcher3.group(2)), matcher3.group(3));
                ce = new ConditionExpression(cs);

            } else if (matcher3.group(4).equals("||") || matcher3.group(4).equals("|") || matcher3.group(4).equals("or")) {
                ConditionStatement cs = createConditionStatement(matcher3.group(1), operators.get(matcher3.group(2)), matcher3.group(3));
                ce = new ConditionExpression(cs);
            }

            //Collecting conditions
            conditions = conditions + " " + tempCond;
            copyString = copyString.replace(tempCond, " ");
        }

        //Make it prettier, seperate to method
        //Find the rest of the conditions
        while (matcher.find()) {

            tempCond = conditionString.substring(matcher.start(), matcher.end());
            System.out.println("tempCond: " + tempCond);
            ConditionStatement cs = createConditionStatement(matcher.group(2), operators.get(matcher.group(3)), matcher.group(4));

            //Collecting conditions
            conditions = conditions + " " + tempCond;
            copyString = copyString.replace(tempCond, " ");
            System.out.println("Statement: " + cs.toString());


            System.out.println("matcher.group(4): " + matcher.group(4));

            //Checking operators
            if (matcher.group(1).equals("&&") || matcher.group(1).equals("&") || matcher.group(1).equals("and")) {
                ce.appendElement(ConditionOperator.Type.AND, cs);

            } else if (matcher.group(1).equals("||") || matcher.group(1).equals("|") || matcher.group(1).equals("or")) {
                ce.appendElement(ConditionOperator.Type.OR, cs);
            }
        }


        //Matcher2 for actions
//        while (matcher2.find()) {
//            tempCond = conditionString.substring(matcher2.start(), matcher2.end());
//            System.out.println("tempCond: " + tempCond);
//            ConditionStatement cs = new ConditionStatement(matcher2.group(2), operators.get(matcher2.group(3)), matcher2.group(4));
//
//            //Collecting conditions
//            conditions2 = conditions2 + "  " + tempCond;
//            copyString = copyString.replace(tempCond, " ");
//            System.out.println("Statement: " + cs.toString());
//
//
//            //Checking operators
//            if (matcher2.group(1).equals("&&") || matcher2.group(1).equals("&") || matcher2.group(1).equals("and")) {
//                ce.appendElement(ConditionOperator.Type.AND, cs);
//
//            } else if (matcher2.group(1).equals("||") || matcher2.group(1).equals("|") || matcher2.group(1).equals("or")) {
//                ce.appendElement(ConditionOperator.Type.OR, cs);
//            }
//        }



        //________________________________
        // Out prints

        //System.out.println("The original conditions: " + conditionString);
        System.out.println("The illegal characters: " + copyString);
        System.out.println("Final Guards: " + conditions);
        System.out.println("Final Actions: " + conditions2);

        //_________________________________

        //Check illegal statements
        if (copyString.matches("\\w+")) {
            System.out.println("Illegal condition statment");
        }
        if (ce.getExpressionRoot() != null) {
            System.out.println("Root: " + ce.getExpressionRoot().toString());
        } else if (ce.getExpressionRoot() != null) {
            System.out.println("Root: " + ce.getExpressionRoot().toString());
        } else {
            System.out.println("Illegal condition statment");
        }

//        while(ce.getExpressionRoot().hasNextElement()){
//            System.out.println("Elements: "+ ce.getExpressionRoot().getNextElement().toString());
//            //System.out.println("Iterator: "+right.iterator().next());
//        }
        System.out.println("__________________");
        return ce;
    }

    private ConditionStatement createConditionStatement(String iVariable, final Operator iOperator, String iValue) {
        iVariable = iVariable.replaceAll("id", "");

        iValue = iValue.replaceAll("i", "0");
        iValue = iValue.replaceAll("e", "1");
        iValue = iValue.replaceAll("f", "2");

        return new ConditionStatement(iVariable, iOperator, iValue);
    }
}

