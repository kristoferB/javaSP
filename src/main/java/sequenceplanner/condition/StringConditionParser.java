package sequenceplanner.condition;

import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;

/**
 * Simple singleton parser class.
 * Only has two public methods. getInstance and getParseConditionString.
 * @author Qw4z1
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
    
    /**
     * Takes a String as argument and 
     * parses the String into a {@link Condition}
     * @param String Conditionstring
     * @return the {@link Condition} 
     */
    public Condition parseConditionString(String conditionString){
        Condition condition = new Condition();
        return condition;
    }
}
