package sequenceplanner.IO.optimizer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.parser.ConditionToJavaStringParser;

/**
 *
 * @author kbe
 */
public enum ExpressionToJavaConverter {
    INSTANCE;
    
    public String convertConditionElement(ConditionElement expr){
        return ConditionToJavaStringParser.INSTANCE.ConvertCondition(expr);
    }
    
    public String appendExpression(String javaExpression, ConditionExpression expr){
        if (javaExpression.isEmpty()) return convertConditionElement(expr);
        return javaExpression + "&&" + convertConditionElement(expr);
    }
    
    public String appendStringExpression(String javaExpression, String expr){
        if (javaExpression.isEmpty()) return expr;
        return javaExpression + " && " + expr;
    }
    
    public List<String> convertActionExpressions(ConditionExpression expr){
        List<String> actions = new ArrayList<String>();
        if (expr.isEmpty()) return actions;
        ArrayDeque<ConditionElement> stack = new ArrayDeque();
        stack.push(expr.getExpressionRoot());
        while (!stack.isEmpty()){
            ConditionElement poper = stack.pop();
            if (poper.isExpression() && !((ConditionExpression)poper).isEmpty() ) 
                    stack.push(((ConditionExpression)poper).getExpressionRoot());
            if (poper.isStatement())
                actions.add(convertConditionElement(poper));
            if (poper.hasNextElement())
                stack.push(poper.getNextElement());
            
        }
        
        
        return actions;
    }
 
            
}
