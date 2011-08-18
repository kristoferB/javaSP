package sequenceplanner.model.data;

import sequenceplanner.model.Model;

/**
 * To keep track on the different conditions.<br/>
 * One conditionData object per sop view.<br/>
 * A user can also create his/her own conditionData objects.<br/>
 * @author patrik
 */
public class ConditionData extends Data {

    public ConditionData(String name) {
        super(name, Model.newId());
    }
}
