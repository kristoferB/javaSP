package sequenceplanner.datamodel.condition;


/**
 *
 * @author kbe
 */
public class Condition {

    private ConditionExpression guard;
    private ConditionExpression action;

    public Condition() {
        guard = new ConditionExpression();
        action = new ConditionExpression();
    }

    public Condition(ConditionExpression guard, ConditionExpression action) {
        this.guard = guard;
        this.action = action;
    }

    public ConditionExpression getAction() {
        return action;
    }

    public void setAction(ConditionExpression action) {
        this.action = action;
    }

    public boolean hasAction(){
        return !action.isEmpty();
    }

    public ConditionExpression getGuard() {
        return guard;
    }

    public void setGuard(ConditionExpression guard) {
        this.guard = guard;
    }

    public boolean hasGuard(){
        return !guard.isEmpty();
    }

    
    public void appendCondition(Condition c) {
        if (c==null) return;
        if (!c.getGuard().isEmpty())
            this.guard.appendElement(ConditionOperator.Type.AND, c.getGuard());
        if (!c.getAction().isEmpty())
            this.action.appendElement(ConditionOperator.Type.SEMIKOLON, c.getAction());
    }
    

    @Override
    public String toString(){
        return this.guard.toString() + '/' + this.action.toString();
    }

    


}
