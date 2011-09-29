package sequenceplanner.expression;

/**
 * Interface for a three-tuple, that is used as a literal.
 * @author patrik
 */
public interface ILiteral {

    Object getVariable();

    Object getValue();

    Literal.LiteralOperator getLiteralOperator();

    boolean isNegative();
}
