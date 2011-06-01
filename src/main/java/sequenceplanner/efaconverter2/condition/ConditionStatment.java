package sequenceplanner.efaconverter2.condition;

/**
 *
 * @author kbe
 */
public class ConditionStatment extends ConditionElement {

    public enum Operator {Equal("=="),
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
        
        Operator(String sign){
            opSign = sign;
        }
        
        @Override
        public String toString(){
            return opSign;
        }

    };
    public static String MULTIPLE_VALUES = "**";


    private String variable;
    private Operator op;
    private String value;
    private boolean isAction;



    public ConditionStatment(String variable, Operator op, String value) {
        super();
        this.variable = variable;
        this.op = op;
        this.value = value;
        this.isAction = isOperatorAction(op);
    }
    
    public ConditionStatment(String variable, 
                             Operator op, 
                             String value, 
                             ConditionOperator previousOperator, 
                             ConditionOperator nextOperator) 
    {
        super(previousOperator, nextOperator);
        this.variable = variable;
        this.op = op;
        this.value = value;
        this.isAction = isOperatorAction(op);
    }

    public boolean isAction(){
        return this.isAction;
    }

    public String getVariable(){
        return variable;
    }

    public Operator getOperator(){
        return this.op;
    }

    public String getValue(){
        return this.value;
    }

    public void setOp(Operator op) {
        this.op = op;
        this.isAction = isOperatorAction(op);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }





    private boolean isOperatorAction(Operator o){
        if (o.equals(Operator.Assign)
                || o.equals(Operator.Inc)
                || o.equals(Operator.Dec)
                || o.equals(Operator.PointAt)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isExpression() {
        return false;
    }

    @Override
    public boolean isStatment() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!( obj instanceof ConditionStatment)) {
            return false;
        }
        final ConditionStatment other = (ConditionStatment) obj;
        if ((this.variable == null) ? (other.variable != null) : !this.variable.equals(other.variable)) {
            return false;
        }
        if (this.op != other.op) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.variable != null ? this.variable.hashCode() : 0);
        hash = 17 * hash + this.op.hashCode();
        hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    


    public boolean evaluate(String variableValue){
        if (variableValue.equals(MULTIPLE_VALUES)) return true; // multipleValue location
        try{
            int intVar = Integer.parseInt(variableValue);
            int intValue = Integer.parseInt(value);
            return evaluate(intVar,this.op,intValue);

        } catch(NumberFormatException e){
            return evaluate(variableValue, this.op, this.value);
        }
    }

    public String getNewVariableValue(String currentVariableValue){
        if (!this.isAction) return currentVariableValue;
        boolean isInt = false;
        int intValue = 0;
        try{
             intValue = Integer.parseInt(currentVariableValue);
            isInt = true;
        } catch(NumberFormatException e){
            isInt = false;
        }
        switch(this.getOperator()){
            case Assign:
                return this.value;
            case Inc:
                if (isInt){
                    int result = intValue++;
                    return Integer.toString(result);
                }
                break;
            case Dec:
                if (isInt){
                    int result = intValue--;
                    return Integer.toString(result);
                }
                break;

            // do not support pointAt yet
        }
        return currentVariableValue;

    }

    public static boolean evaluate(int value1, Operator o, int value2){
        // Equal, NotEqual, Greater, Less, GreaterEq, LessEq, Assign, Inc, Dec, PointAt
        switch(o){
            case Equal:
                return value1 == value2;
            case NotEqual:
                return value1 != value2;
            case Greater:
                return value1 > value2;
            case Less:
                return value1 < value2;
            case GreaterEq:
                return value1 >= value2;
            case LessEq:
                return value1 <= value2;
        }
        return false;
    }

    public static boolean evaluate(String value1, Operator o, String value2){
        // Equal, NotEqual, Greater, Less, GreaterEq, LessEq, Assign, Inc, Dec, PointAt
        switch(o){
            case Equal:
                return value1.equals(value2);
            case NotEqual:
                return !value1.equals(value2);
        }
        return false;
    }

    @Override
    public String toString(){
        return this.variable + this.op + this.value;
              
    }

}
