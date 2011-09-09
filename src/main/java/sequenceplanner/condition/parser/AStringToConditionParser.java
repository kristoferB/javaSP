package sequenceplanner.condition.parser;

import sequenceplanner.condition.*;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sequenceplanner.condition.ConditionStatement.Operator;

/**
 * To parse a {@link String} <code>s</code> to a {@link ConditionExpression}.<br/>
 * It is expected that <code>s</code> consists of <code>statements</code> and <code>clauses</code>.<br/>
 * A <code>statement</code> looks like: [variable][statementOperator][value], e.g. Var2==34.<br/>
 * A <code>clause</code> can look like: ([statement1][clauseOperator][statement2])[clauseOperator][statement3].<br/>
 * This abstract class needs to be extended with acceptable: variables, statementOperators, values, and clauseOperators.<br/>
 * Good ref for regular expressions {@link http://www.vogella.de/articles/JavaRegularExpressions/article.html#examples_or}<br/>
 * @author Qw4z1, Patrik
 */
public abstract class AStringToConditionParser {

    public AStringToConditionParser() {
    }
    private static Map<String, Operator> statementOperatorMap = new HashMap<String, Operator>();

    {
        statementOperatorMap.put(ConditionStatement.Operator.NotEqual.toString(), ConditionStatement.Operator.NotEqual);
        statementOperatorMap.put(ConditionStatement.Operator.Equal.toString(), ConditionStatement.Operator.Equal);
        statementOperatorMap.put(ConditionStatement.Operator.LessEq.toString(), ConditionStatement.Operator.LessEq);
        statementOperatorMap.put(ConditionStatement.Operator.GreaterEq.toString(), ConditionStatement.Operator.GreaterEq);
        statementOperatorMap.put(ConditionStatement.Operator.Assign.toString(), ConditionStatement.Operator.Assign);
        statementOperatorMap.put(ConditionStatement.Operator.Less.toString(), ConditionStatement.Operator.Less);
        statementOperatorMap.put(ConditionStatement.Operator.Greater.toString(), ConditionStatement.Operator.Greater);
        statementOperatorMap.put(ConditionStatement.Operator.Inc.toString(), ConditionStatement.Operator.Inc);
        statementOperatorMap.put(ConditionStatement.Operator.Dec.toString(), ConditionStatement.Operator.Dec);
        statementOperatorMap.put(ConditionStatement.Operator.PointAt.toString(), ConditionStatement.Operator.PointAt);
    }

    abstract String getClauseOperator();

    abstract String getVariable();

    abstract String getStatementOperator();

    abstract String getValue();

    /**
     * To parse a {@link String} parameter <code>iConditionString</code> into a
     * {@link ConditionExpression} parameter <code>iConditionExpression</code>.<br/>
     * @param iConditionString
     * @param iConditionExpression
     * @return <code>true</code> if parse was ok else <code>false</code>
     */
    public boolean run(String iConditionString, final ConditionExpression iConditionExpression) {
        //Remove all whitespace characters
        iConditionString = iConditionString.replaceAll("\\s", "");

        return parseConditionString(iConditionString, iConditionExpression);
    }

    /**
     * To parse a {@link String} parameter <code>iConditionString</code> into a
     * {@link ConditionExpression}.<br/>
     * @param iConditionString
     * @return {@link ConditionExpression} object or null if parse didn't work
     */
    public ConditionExpression run(String iConditionString) {
        final ConditionExpression ce = new ConditionExpression();
        if (run(iConditionString, ce)) {
            return ce;
        }
        return null;
    }

    /**
     * To parse a {@link String} parameter <code>iConditionString</code> into a
     * {@link ConditionExpression} parameter <code>iConditionExpression</code>.<br/>
     * This method is called recusively.<br/>
     * @param iConditionString
     * @param iConditionExpression
     * @return <code>true</code> if parse was ok else <code>false</code>
     */
    private boolean parseConditionString(String iConditionString, final ConditionExpression iConditionExpression) {
        final String needToStart = "^";
        final String leftp = "(\\()";
        final String any = "(.*)";

        Matcher matcher;

        //iConditionExpression == "ClauseOperator Variable StatementOperator Value...
        matcher = Pattern.compile(needToStart + getClauseOperator() + getVariable() + getStatementOperator() + getValue() + any).matcher(iConditionString);
        if (matcher.find()) {
            //Check that this is not the first clause in condition expression
            //E.g. &&variable...
            if (iConditionExpression.isEmpty()) {
                return false;
            }

            //Create operator
            final ConditionOperator.Type operatorType = getClauseOperator(matcher.group(1));
            //Create statement
            final ConditionStatement cs = createConditionStatement(matcher.group(2), matcher.group(3), matcher.group(4));
            //Append
            iConditionExpression.appendElement(operatorType, cs);

            //Work with rest
            if (!parseConditionString(matcher.group(5), iConditionExpression)) {
                return false;
            }
            return true;
        }//----------------------------------------------------------------------

        //iConditionExpression == "Variable StatementOperator Value...-----------
        matcher = Pattern.compile(needToStart + getVariable() + getStatementOperator() + getValue() + any).matcher(iConditionString);
        if (matcher.find()) {
            //Create statement
            final ConditionStatement cs = createConditionStatement(matcher.group(1), matcher.group(2), matcher.group(3));
            //Change root
            iConditionExpression.changeExpressionRoot(cs);

            //Work with rest
            if (!parseConditionString(matcher.group(4), iConditionExpression)) {
                return false;
            }
            return true;
        }//----------------------------------------------------------------------

        //iConditionExpression == "ClauseOperator (...---------------------------
        matcher = Pattern.compile(needToStart + getClauseOperator() + leftp + any).matcher(iConditionString);
        if (matcher.find()) {
            //Check that this is not the first clause in condition expression
            //E.g. &&(variable...
            if (iConditionExpression.isEmpty()) {
                return false;
            }

            //Create operator
            final String operator = matcher.group(1);
            final ConditionOperator.Type operatorType = getClauseOperator(operator);

            //Remove operator from condition string
            iConditionString = iConditionString.substring(operator.length());

            //Get clauses
            final List<String> clauseList = checkParenthesesReturnStringList(iConditionString);
            if (clauseList.size() != 3) {
                return false;
            }
            if (clauseList.get(1).length() <= 1) {
                return false;
            }

            //Left clause
            final ConditionExpression childConditionExpression = new ConditionExpression();
            if (!parseConditionString(clauseList.get(1), childConditionExpression)) {
                return false;
            }
            //Append
            iConditionExpression.appendElement(operatorType, childConditionExpression);

            //Right clause
            if (!parseConditionString(clauseList.get(2), iConditionExpression)) {
                return false;
            }
            return true;
        }//----------------------------------------------------------------------

        //iConditionExpression == "(...------------------------------------------
        matcher = Pattern.compile(needToStart + leftp + any).matcher(iConditionString);
        if (matcher.find()) {
            //Get clauses
            final List<String> clauseList = checkParenthesesReturnStringList(iConditionString);
            if (clauseList.size() != 3) {
                return false;
            }
            if (clauseList.get(1).length() <= 1) {
                return false;
            }

            //Left clause
            final ConditionExpression childConditionExpression = new ConditionExpression();
            if (!parseConditionString(clauseList.get(1), childConditionExpression)) {
                return false;
            }
            //Change root
            iConditionExpression.changeExpressionRoot(childConditionExpression);

            //Right clause
            if (!parseConditionString(clauseList.get(2), iConditionExpression)) {
                return false;
            }
            return true;
        }//----------------------------------------------------------------------

        if (iConditionString.equals("")) {
            return true;
        }

        return false;
    }

    /**
     * Splits parameter <code>s</code> in three parts based on the parentheses in <code>s</code>.<br/>
     * Ex: a((b))c gives the three parts: a,(b),c.<br/>
     * @param s
     * @return {@link List<String>} with three elements or empty list if mismatch with parentheses
     */
    private static List<String> checkParenthesesReturnStringList(String s) {
        int nesting = 0;
        int startIndex = 0;
        final List<String> clauseList = new ArrayList<String>();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '(':
                    nesting++;
                    if (nesting == 1) {
                        startIndex = i;
                    }
                    break;
                case ')':
                    nesting--;
                    if (nesting == 0) {
                        clauseList.add(s.substring(0, startIndex));
                        clauseList.add(s.substring(startIndex + 1, i));
                        clauseList.add(s.substring(i + 1));
                        return clauseList;
                    }
                    if (nesting < 0) {
                        return clauseList;
                    }
                    break;
            }
        }
        return clauseList;
    }

    /**
     * Get {@link ConditionOperator} based on a string.
     * @param iOperator
     * @return {@link ConditionOperator} or null if no operator was found
     */
    private static ConditionOperator.Type getClauseOperator(final String iOperator) {
        if (iOperator.equals("&&") || iOperator.equals("&") || iOperator.equals("and")) {
            return ConditionOperator.Type.AND;
        } else if (iOperator.equals("||") || iOperator.equals("|") || iOperator.equals("or")) {
            return ConditionOperator.Type.OR;
        } else if (iOperator.equals(";")) {
            return ConditionOperator.Type.SEMIKOLON;
        }
        return null;
    }

    /**
     * Creates a {@link ConditionStatement} that is returned.
     * @param iVariable
     * @param iOperator
     * @param iValue
     * @return {@link ConditionStatement} or null if probelm
     */
    private static ConditionStatement createConditionStatement(String iVariable, final String iOperator, String iValue) {

        //Variable
//        iVariable = iVariable.replaceAll("id", "");

        //Operator
        if (!statementOperatorMap.containsKey(iOperator)) {
            return null;
        }
        final Operator operator = statementOperatorMap.get(iOperator);

        //Value
        iValue = iValue.replaceAll("i", "0");
        iValue = iValue.replaceAll("e", "1");
        iValue = iValue.replaceAll("f", "2");

        return new ConditionStatement(iVariable, operator, iValue);
    }
}

