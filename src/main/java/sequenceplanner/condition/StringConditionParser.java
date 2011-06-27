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
    private static String action = "(=|[+-[=]])";
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
     * parses the String into a {@link Condition}
     * @param String Conditionstring
     * @return the {@link Condition} 
     */
    public Condition parseConditionString(String conditionString) {

        System.out.println("The real string: "+conditionString);
        Condition condition = new Condition();
        String op ="", var="", val="";
        String conditions = "";
        String conditions2 = "";
        String copyString = "";
        String tempCond = "";

        //Remove all whitespace characters
        conditionString = conditionString.replaceAll("\\s","");
        //To lower case
        conditionString = conditionString.toLowerCase();

        System.out.println("Replace whitespace: "+ conditionString);
        Pattern pattern1 = Pattern.compile(type + id + guard + value);
        Pattern pattern2 = Pattern.compile(type + id + guard + value);
        Pattern rootPattern = Pattern.compile(id + guard + value + type);
        Matcher matcher = pattern1.matcher(conditionString);
        Matcher matcher2 = pattern2.matcher(conditionString);
        Matcher matcher3 = rootPattern.matcher(conditionString);
        copyString = conditionString;
        ConditionExpression left = new ConditionExpression();
        ConditionExpression right = new ConditionExpression();



        //to remove the root from the string, since either the first or the last one differs from the pattern
        if(matcher3.find()){
            tempCond = conditionString.substring(matcher3.start(), matcher3.end()-matcher3.group(4).length());
            System.out.println("Root: " + tempCond);
            if(matcher3.group(4).equals("&&")||matcher3.group(4).equals("&")||matcher3.group(4).equals("and")){
                right.changeExpressionRoot(new ConditionStatement(matcher3.group(1), operators.get(matcher3.group(2)), matcher3.group(3)));
                
            }else if(matcher3.group(4).equals("||")||matcher3.group(4).equals("|")||matcher3.group(4).equals("or")){
                left.changeExpressionRoot(new ConditionStatement(matcher3.group(1), operators.get(matcher3.group(2)), matcher3.group(3)));
            }

            //Collecting conditions
            conditions = conditions + "  " + tempCond;
            copyString = copyString.replace(tempCond, " ");
        }

        //Make it prettier, seperate to method
        //Find the rest of the conditions
        while (matcher.find()) {

            tempCond = conditionString.substring(matcher.start(), matcher.end());
            System.out.println("tempCond: " + tempCond);
            ConditionStatement cs = new ConditionStatement(matcher.group(2), operators.get(matcher.group(3)), matcher.group(4));

            //Collecting conditions
            conditions = conditions + "  " + tempCond;
            copyString = copyString.replace(tempCond, " ");
            System.out.println("Statement: " + cs.toString());


            //Checking operators
            if(matcher.group(4).equals("&&")||matcher.group(4).equals("&")||matcher.group(4).equals("and")){
                right.appendElement(ConditionOperator.Type.AND, cs);

            }else if(matcher.group(4).equals("||")||matcher.group(4).equals("|")||matcher.group(4).equals("or")){
                left.appendElement(ConditionOperator.Type.OR, cs);
            }
        }


        //Matcher2 for actions
        while (matcher2.find()) {
            tempCond = conditionString.substring(matcher2.start(), matcher2.end());
            System.out.println("tempCond: " + tempCond);
            ConditionStatement cs = new ConditionStatement(matcher2.group(2), operators.get(matcher2.group(3)), matcher2.group(4));

            //Collecting conditions
            conditions2 = conditions2 + "  " + tempCond;
            copyString = copyString.replace(tempCond, " ");
            System.out.println("Statement: " + cs.toString());


            //Checking operators
            if(matcher2.group(4).equals("&&")||matcher2.group(4).equals("&")||matcher2.group(4).equals("and")){
                right.appendElement(ConditionOperator.Type.AND, cs);

            }else if(matcher2.group(4).equals("||")||matcher2.group(4).equals("|")||matcher2.group(4).equals("or")){
                left.appendElement(ConditionOperator.Type.OR, cs);
            }
        }



        //________________________________
        // Out prints

        //System.out.println("The original conditions: " + conditionString);
        System.out.println("The illegal characters: " + copyString);
        System.out.println("Final Guards: " + conditions);
        System.out.println("Final Actions: " + conditions2);

        //_________________________________

        //Check illegal statements
        if(copyString.matches("\\w+")){
            System.out.println("Illegal condition statment");
        }
        if(right.getExpressionRoot() != null){
            System.out.println("Root: "+ right.getExpressionRoot().toString());
        }else if(right.getExpressionRoot() != null){
            System.out.println("Root: "+ right.getExpressionRoot().toString());
        }else{
            System.out.println("Illegal condition statment");
        }


        /*while(right.getExpressionRoot().hasNextElement()){
            System.out.println("Elements: "+ right.getExpressionRoot().getNextElement().toString());
            //System.out.println("Iterator: "+right.iterator().next());
        }*/
        System.out.println("__________________");
        return condition;
    }
}

