package sequenceplanner.condition;

import java.util.LinkedList;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;

/**
 * Class for extracting a Condition object from an existing OperationData
 * @author QW4z1
 */
public class DataToConditionHelper {

    private static final DataToConditionHelper instance = new DataToConditionHelper();

    ;
    
    /**
     * 
     * @return 
     */
    public static DataToConditionHelper getInstance() {
        return instance;
    }

    private DataToConditionHelper() {
    }

    /*          This class should: 
     * 
     */
    public static Condition extractPre(OperationData data) {
        return extractCondition(data, true);
    }

    public static Condition extractPost(OperationData data) {
        return extractCondition(data, false);
    }

    private static Condition extractCondition(OperationData data, boolean isPreCond) {
        Condition condition = new Condition();
        if (isPreCond) {
            for (LinkedList<SeqCond> list : data.getSequenceCondition()) {
                //Nytt conditionexpression -> or
                for (SeqCond seqCond : list) {
                    //Nytt conditionexpression and
                }
            }
        } else {
        }


        return condition;
    }
}
