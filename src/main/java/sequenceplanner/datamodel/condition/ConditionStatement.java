package sequenceplanner.datamodel.condition;

/**
 *
 * Should be made immutable
 * 
 * @author kbe
 */
public class ConditionStatement extends ConditionElement {

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


    private String variable; //Operation Name
    private Operator op;
    private String value; // 0, 1, 2
    private boolean isAction;



    public ConditionStatement(String variable, Operator op, String value) {
        super();
        this.variable = variable;
        this.op = op;
        this.value = value;
        this.isAction = isOperatorAction(op);
    }
    
    public ConditionStatement(String variable,
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
    public boolean isStatement() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!( obj instanceof ConditionStatement)) {
            return false;
        }
        final ConditionStatement other = (ConditionStatement) obj;
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

    




    @Override
    public String toString(){
        return this.variable + this.op + this.value;
              
    }

}
