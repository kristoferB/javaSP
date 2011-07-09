   // KB 100629
// We must rewrite all these data classes! We should introduce a class
// that is used both for pre, post and restconditions. There should be
// no difference! They should include both guards and actions.
// We should also use interfaces for all datastructures to be able to
// use injection!
package sequenceplanner.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import sequenceplanner.condition.Condition;

import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;

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
    private Map<String, Map<ConditionType, Condition>> mConditionMap;

    //OperationData newOp = new OperationData(OP,model.getNewId());
    public OperationData(String name, int id) {
        super(name, id);
        algebraicCounter = 1;
        preference = Collections.synchronizedMap(new HashMap<String, String>());

        mConditionMap = Collections.synchronizedMap(new HashMap<String, Map<ConditionType, Condition>>());
    }

    public void setConditions(Map<ConditionType, Condition> conditionMap, String operationViewName) {
        this.mConditionMap.put(operationViewName, conditionMap);
    }

    private void setValue(String key, String value) {
        if (key != null && value != null) {
            preference.put(key, value);
        } else {
            System.out.println("Error in UserFile: You shall not pass null values for key " + key);
        }
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

//    public void setDescription(String value) {
//        setValue(OP_DESCRIPTION, value);
//    }
//
//    public String getDescription() {
//        return getValue(OP_DESCRIPTION);
//    }
    @Override
    public String toString() {
        return getValue(OPERATION_NAME);
    }

    public Map<String, Map<ConditionType, Condition>> getGlobalConditions() {
        return mConditionMap;
    }

    public void removeCondition(String condKey) {
        mConditionMap.remove(condKey);
    }
}
