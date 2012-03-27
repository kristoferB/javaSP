package sequenceplanner.model.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.model.Model;

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
    
    // Temp fixes for Intentional. To be fixed in a better way
    public boolean hasToFinish = false;
    public String seam = "";
    public String resource = "";
    public String startTime ="-1";
    public String stopTime ="-1";
    public int timecost = 1;
    public String guid= "";
    
    
    public OperationData(String name, int id) {
        super(name, id);
        algebraicCounter = 1;
        preference = Collections.synchronizedMap(new HashMap<String, String>());

        mConditionMap = Collections.synchronizedMap(new HashMap<ConditionData, Map<ConditionType, Condition>>());
    }

    public void setConditions(ConditionData iConditionData, Map<ConditionType, Condition> conditionMap) {
        if (this.mConditionMap.containsKey(iConditionData)){
            mergeConditionMaps(this.mConditionMap.get(iConditionData),conditionMap);
        } else 
            this.mConditionMap.put(iConditionData, conditionMap);
    }
    
    // We should refactor how conditions are stored! This is a temp fix
    // to merge conditions instead of overwrite. 
    private void mergeConditionMaps(Map<ConditionType, Condition> to, Map<ConditionType, Condition> from){
        to.putAll(from);
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

    @Override
    public String toString() {
        return getAttribute(Type.NAME);
    }

    public Map<ConditionData, Map<ConditionType, Condition>> getConditions() {
        return mConditionMap;
    }

    public void removeCondition(ConditionData condKey) {
        mConditionMap.remove(condKey);
    }
    
    public void addCondition(ConditionData condKey,ConditionType type, Condition c){
        if (c == null || condKey == null) return;
        Map<ConditionType, Condition> map = this.mConditionMap.get(condKey);
        if (map == null){
            map= new HashMap<ConditionType, Condition>();
            map.put(type, c);
            this.mConditionMap.put(condKey, map);
            return;
        }
        
        Condition cond = map.get(type);
        if (cond == null){
            map.put(type, c);
            return;
        }
        
        cond.appendCondition(c);       
        
    }
}
