/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.condition;

import java.util.HashMap;
import java.util.Map;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;

/**
 *
 * @author QW4z1
 */
public class StringConditionParser {
    
    private StringConditionParser() {
    }
    
    public static StringConditionParser getInstance() {
        return StringConditionParserHolder.INSTANCE;
    }
    
    private static class StringConditionParserHolder {

        private static final StringConditionParser INSTANCE = new StringConditionParser();
    }
    
    public Map<ConditionType,Condition> getConditionMap(String conditionString, ConditionType type){
        return new HashMap<ConditionType,Condition>();
    }
}
