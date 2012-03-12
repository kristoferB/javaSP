/*
 * We need to change the parsing of condition! We should have an option class
 * where how the various operators should be translated. We should also create
 * a better enum structure of the operators.
 */
package sequenceplanner.datamodel.condition.parser;

import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionOperator;
import sequenceplanner.datamodel.condition.ConditionStatement;

/**
 *
 * @author kbe
 */
public enum ConditionToJavaStringParser {
    INSTANCE;
    
    
    public String ConvertCondition(ConditionElement e){
        if (e == null) return "";
        
        if (e.isStatement()){
            ConditionStatement s = (ConditionStatement)e;
            return s.getVariable() + getStatmentOperator(s.getOperator()) + s.getValue();
        } else if (e.isExpression()){
            ConditionExpression exp = (ConditionExpression)e;
            if (exp.getExpressionRoot() == null) return "";
            String result = "";
            if (exp.getExpressionRoot().hasNextElement() && !exp.getExpressionRoot().getNextOperator().isOperationType(ConditionOperator.Type.SEMIKOLON))
                result = "(";                        

            for (ConditionElement child : exp) {
                if (!child.toString().isEmpty()){
                    if (child.hasPreviousElement()){
                        result += getLogicOperator(child.getPreviousOperator())+ ConvertCondition(child);
                        if (!child.hasNextElement() && !child.getPreviousOperator().isOperationType(ConditionOperator.Type.SEMIKOLON))
                            result += ")";
                    }else
                        result += ConvertCondition(child);                
                }            
            }
            return result;
        }
        return "";
        
    }
    
    private String getLogicOperator(ConditionOperator op){
        if (op.isOperationType(ConditionOperator.Type.AND)){
            return "&&";          
        } else if (op.isOperationType(ConditionOperator.Type.OR)){
            return "||";
        } else if (op.isOperationType(ConditionOperator.Type.SEMIKOLON)){
            return ";";
        }
        return "";    
    }
    
    private String getStatmentOperator(ConditionStatement.Operator op){
        if (op == ConditionStatement.Operator.Assign) return "=";
        if (op == ConditionStatement.Operator.Dec) return "-=";
        if (op == ConditionStatement.Operator.Equal) return "==";
        if (op == ConditionStatement.Operator.Greater) return ">";
        if (op == ConditionStatement.Operator.GreaterEq) return ">=";
        if (op == ConditionStatement.Operator.Inc) return "+=";
        if (op == ConditionStatement.Operator.Less) return "<";
        if (op == ConditionStatement.Operator.LessEq) return "<=";
        if (op == ConditionStatement.Operator.NotEqual) return "!=";
        return "ERROR";
    }
    
}
