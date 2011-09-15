package sequenceplanner.expression;

import sequenceplanner.expression.ILiteral;
import sequenceplanner.condition.ConditionOperator;
import sequenceplanner.condition.ConditionStatement;

/**
 *
 * @author patrik
 */
public class Literal extends ConditionStatement implements ILiteral{

    public Literal(String variable, Operator op, String value, ConditionOperator previousOperator, ConditionOperator nextOperator) {
        super(variable, op, value, previousOperator, nextOperator);
    }

    public Literal(String variable, Operator op, String value) {
        super(variable, op, value);
    }

    
}
