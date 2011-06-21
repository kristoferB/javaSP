
package sequenceplanner.efaconverter2.condition;

import sequenceplanner.efaconverter2.EFA.EFAVariables;

/**
 *
 * @author kbe
 * @author Mohammad Reza Shoaei
 * @version 21062011
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
    public boolean equals(Object obj) {
        if(obj instanceof ConditionOperator)
             return this.isOperationType(((ConditionOperator)obj).getOperatorType());
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.nextElement != null ? this.nextElement.hashCode() : 0);
        hash = 59 * hash + (this.prevElement != null ? this.prevElement.hashCode() : 0);
        hash = 59 * hash + (this.operatotType != null ? this.operatotType.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        return this.operatotType.equals(Type.AND)?EFAVariables.EFA_AND:EFAVariables.EFA_OR;
    }

}
