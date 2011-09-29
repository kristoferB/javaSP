package sequenceplanner.expression;

/**
 * Implementation of a three-tuple literal.<br/>
 * Variable, operator, and value.<br/>
 * Contains a number of operators that can be used to relate variables to values.<br/>
 * Eg var1 == 2<br/>
 * @author patrik
 */
public class Literal implements ILiteral {

    private final Object mVariable;
    private final Object mValue;
    private final LiteralOperator mLiteralOperator;
    private boolean mIsNegative = false;

    public Literal(Object mVariable, LiteralOperator mLiteralOperator, Object mValue) {
        this.mVariable = mVariable;
        this.mLiteralOperator = mLiteralOperator;
        this.mValue = mValue;
    }

    public void setIsNegative(final boolean iIsNegative) {
        mIsNegative = iIsNegative;
    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public Object getVariable() {
        return mVariable;
    }

    @Override
    public LiteralOperator getLiteralOperator() {
        return mLiteralOperator;
    }

    @Override
    public boolean isNegative() {
        return mIsNegative;
    }

    @Override
    public String toString() {
        String returnString = "";
        if (mIsNegative) {
            returnString += "neg ";
        }

        returnString += getVariable().toString() + " " + getLiteralOperator().toString() + " " + getValue().toString();
        return returnString;
    }

    public enum LiteralOperator {

        Equal("=="),
        NotEqual("!="),
        Greater(">"),
        Less("<"),
        GreaterEq(">="),
        LessEq("<="),
        Assign("="),
        Inc("+="),
        Dec("-="),
        PointAt("->");
        private final String opSign;

        LiteralOperator(String sign) {
            opSign = sign;
        }

        @Override
        public String toString() {
            return opSign;
        }
    };
}
