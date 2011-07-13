package sequenceplanner.condition.parser;

/**
 * Pattern for parse of guard {@link String} from user text input to a {@link ConditionExpression}.
 * @author patrik
 */
public class ActionAsTextInputToConditionParser extends AStringToConditionParser {

    private static String variablePrefix = "id";
    private static String variable = "(" + variablePrefix + "\\d{1,})";
    private static String statementOperator = "([+-][=]|[=])";
    private static String value = "(\\d{1,})";
//    private static String clauseOperator = "(&&|&|and|or|\\|\\||\\|)";
    private static String clauseOperator = "(;)";

    @Override
    String getClauseOperator() {
        return clauseOperator;
    }

    @Override
    String getStatementOperator() {
        return statementOperator;
    }

    @Override
    String getValue() {
        return value;
    }

    @Override
    String getVariable() {
        return variable;
    }
}
