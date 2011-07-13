package sequenceplanner.condition.parser;

/**
 * Pattern for parse of guard {@link String} from Supremica to a {@link ConditionExpression}.
 * @author patrik
 */
public class SupremicaGuardToConditionParser extends AStringToConditionParser {

    private static String variable = "([^\\(\\)\\d]\\w*)"; 
    private static String statementOperator = "([=><!][=]|[<>])";
    private static String value = "([\\d])";
    private static String clauseOperator = "(&|\\|)";

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
