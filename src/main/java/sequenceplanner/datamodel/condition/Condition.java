package sequenceplanner.datamodel.condition;

import java.util.HashMap;
import java.util.Map;

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

    public boolean evaluateGuard(Map<String,String> variableValues){
        if (variableValues == null || variableValues.isEmpty()) return true;
        if (this.guard.isEmpty()) return true;
        return reqGuardEvaluater(guard,variableValues);
    }

    public Map<String, String> getUpdatedVariableValues(Map<String, String> varibleValues){
        if (varibleValues == null) return new HashMap<String, String>();

        Map<String,String> newVars = variableUpdater(this.action,varibleValues);
        

        return newVars;
    }
    
    public void appendCondition(Condition c) {
        if (c==null) return;
        if (!c.getGuard().isEmpty())
            this.guard.appendElement(ConditionOperator.Type.AND, c.getGuard());
        if (!c.getAction().isEmpty())
            this.action.appendElement(ConditionOperator.Type.SEMIKOLON, c.getAction());
    }
    

    private boolean reqGuardEvaluater(ConditionElement element, Map<String,String> variableValues){
        if (element == null) return true;
        boolean elementBoolean = true;
        if (element.isExpression()){
            ConditionExpression ce = (ConditionExpression) element;
            elementBoolean = reqGuardEvaluater(ce.getExpressionRoot(), variableValues);
        }else if (element.isStatment()){
            elementBoolean = validateStatment((ConditionStatement)element,variableValues);
        }
        if (element.hasNextElement()){
            if (element.getNextOperator().isOperationType(ConditionOperator.Type.AND)){
                elementBoolean = elementBoolean && reqGuardEvaluater(element.getNextElement(),variableValues);
            } else if (element.getNextOperator().isOperationType(ConditionOperator.Type.OR)){
                elementBoolean = elementBoolean || reqGuardEvaluater(element.getNextElement(),variableValues);
            }
        }
        return elementBoolean;
    }



    private boolean validateStatment(ConditionStatement statment, Map<String,String> variableValues){
        String var = statment.getVariable();
        if (variableValues.containsKey(var)){
            return statment.evaluate(variableValues.get(var));
        }
        return true;
    }

    private Map<String, String> variableUpdater(ConditionExpression ce, Map<String,String> oldVars){
        Map<String, String> newVars = new HashMap<String, String>(oldVars);
        for (ConditionElement e : ce) {
            if (e.isExpression()) {
                newVars.putAll(variableUpdater((ConditionExpression) e, oldVars));
            } else if (e.isStatment()){
                ConditionStatement cs =  (ConditionStatement) e;
                if (newVars.containsKey(cs.getVariable())){
                    newVars.put(cs.getVariable(), cs.getNewVariableValue(newVars.get(cs.getValue())));
                }
            }
        }
        return newVars;
    }

    @Override
    public String toString(){
        return this.guard.toString() + '/' + this.action.toString();
    }

    


}
