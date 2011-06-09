package sequenceplanner.efaconverter2.condition;

import sequenceplanner.efaconverter2.EFAVariables;

/**
 *
 * @author kbe
 */
public class ConditionOperator {

    public enum Type {AND,OR};

    private ConditionElement nextElement;
    private ConditionElement prevElement;
    private Type operatotType;

    public ConditionOperator(ConditionElement previousElement, ConditionElement nextElemnt, Type operatorType) {
        this.nextElement = nextElemnt;
        this.prevElement = previousElement;
        this.operatotType = operatorType;
    }

    public ConditionOperator(Type operatorType){
        this.nextElement = null;
        this.prevElement = null;
        this.operatotType = operatorType;
    }

    public Type getOperatorType(){
        return this.operatotType;
    }

    public boolean isOperationType(Type operationType){
        return this.operatotType.equals(operationType);
    }

    public void setOperatorType(Type operatorType){
        this.operatotType = operatorType;
    }

    public ConditionElement getNextElement() {
        return nextElement;
    }

    public void setNextElement(ConditionElement nextElement) {
        this.nextElement = nextElement;
    }

    public boolean hasNextElement(){
        return this.nextElement != null;
    }

    public ConditionElement getPreviousElement() {
        return prevElement;
    }

    public void setPreviousElement(ConditionElement prevElemnt) {
        this.prevElement = prevElemnt;
    }

    public boolean hasPreviousElement(){
        return this.prevElement != null;
    }

    public void clear(){
        nextElement = null;
        prevElement = null;
    }

    @Override
    public String toString(){
        return this.operatotType.equals(Type.AND)?EFAVariables.EFA_AND:EFAVariables.EFA_OR;
    }

}
