package sequenceplanner.IO.optimizer;

import java.util.ArrayList;
import java.util.List;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;

/**
 *
 * @author kbe
 */
public enum ExpressionToJavaConverter {
    INSTANCE;
    
    public String convertExpression(ConditionExpression expr){
        return expr.toString(); // hopefully this work :)!
    }
    
    public String appendExpression(String javaExpression, ConditionExpression expr){
        if (javaExpression.isEmpty()) return convertExpression(expr);
        return javaExpression + "&&" + convertExpression(expr);
    }
    
    public String appendStringExpression(String javaExpression, String expr){
        return javaExpression + " && " + expr;
    }
    
    public List<String> convertActionExpressions(ConditionExpression expr){
        List<String> actions = new ArrayList<String>();
        
        
        return actions;
    }
            
}
