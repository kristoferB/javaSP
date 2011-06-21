
package sequenceplanner.efaconverter2.condition;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kbe
 * @author Mohammad Reza Shoaei
 * @version 21062011
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
        return action != null;
    }

    public ConditionExpression getGuard() {
        return guard;
    }

    public void setGuard(ConditionExpression guard) {
        this.guard = guard;
    }

    public boolean hasGuard(){
        return guard != null;
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

    private boolean reqGuardEvaluater(ConditionElement element, Map<String,String> variableValues){
        if (element == null) return true;
        boolean elementBoolean = true;
        if (element.isExpression()){
            ConditionExpression ce = (ConditionExpression) element;
            elementBoolean = reqGuardEvaluater(ce.getExpressionRoot(), variableValues);
        }else if (element.isStatment()){
            elementBoolean = validateStatment((ConditionStatment)element,variableValues);
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



    private boolean validateStatment(ConditionStatment statment, Map<String,String> variableValues){
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
                ConditionStatment cs =  (ConditionStatment) e;
                if (newVars.containsKey(cs.getVariable())){
                    newVars.put(cs.getVariable(), cs.getNewVariableValue(newVars.get(cs.getValue())));
                }
            }
        }
        return newVars;
    }

    public boolean isEmpty(){
        return action.isEmpty() && guard.isEmpty();
    }
    
    @Override
    public String toString(){
        return this.guard.toString() + '/' + this.action.toString();
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Condition){
            Condition c = (Condition)obj;
            if(this.guard.equals(c.getGuard()) && this.action.equals(c.getAction()))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.guard != null ? this.guard.hashCode() : 0);
        hash = 11 * hash + (this.action != null ? this.action.hashCode() : 0);
        return hash;
    }

}
