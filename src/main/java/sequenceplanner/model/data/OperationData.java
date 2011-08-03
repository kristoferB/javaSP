package sequenceplanner.model.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import sequenceplanner.condition.Condition;

import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;

/**
 *
 * @author erik
 */
public class OperationData extends Data {

    private int algebraicCounter;
    public static final String OPERATION_NAME = "name";
    public static final String OP_DESCRIPTION = "descr";
    private Map<String, String> preference;
    public static final int RESOURCE_BOOK = 1;
    public static final int RESOURCE_UNBOOK = 0;
    public static final int ACTION_ADD = 0;
    public static final int ACTION_DEC = 1;
    public static final int ACTION_EQ = 2;
    //Maps the pre and post conditions according to ConditionType, Condition
    private Map<ConditionData, Map<ConditionType, Condition>> mConditionMap;

    public OperationData(String name, int id) {
        super(name, id);
        algebraicCounter = 1;
        preference = Collections.synchronizedMap(new HashMap<String, String>());

        mConditionMap = Collections.synchronizedMap(new HashMap<ConditionData, Map<ConditionType, Condition>>());
    }

    public void setConditions(ConditionData iConditionData, Map<ConditionType, Condition> conditionMap) {
        this.mConditionMap.put(iConditionData, conditionMap);
    }

    public Map<String, String> getPreferences() {
        return preference;
    }

    public int getAlgebraicCounter() {
        return algebraicCounter;
    }

    public void increaseAlgebraicCounter() {
        this.algebraicCounter++;
    }

    public void decreaseAlgebraicCounter() {
        this.algebraicCounter--;
    }

    private String getValue(String key) {
        String s = preference.get(key);

        return s == null ? "" : s;
    }

    @Override
    public String toString() {
        return getValue(OPERATION_NAME);
    }

    public Map<ConditionData, Map<ConditionType, Condition>> getConditions() {
        return mConditionMap;
    }

    public void removeCondition(ConditionData condKey) {
        mConditionMap.remove(condKey);
    }
}
