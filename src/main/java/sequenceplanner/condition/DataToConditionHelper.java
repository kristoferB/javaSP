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
     *          Condition preCondition = new Condition();
    Condition postCondition = new Condition();
    ConditionExpression preAction = new ConditionExpression();
    ConditionExpression preGuard = new ConditionExpression();
    ConditionExpression postAction = new ConditionExpression();
    ConditionExpression postGuard = new ConditionExpression();
    preCondition.setAction(preAction);
    preCondition.setGuard(preGuard);
    postCondition.setAction(postAction);
    postCondition.setGuard(postGuard);
    d.addPostConditions(postCondition);
    d.addPreCondition(preCondition);
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
            System.out.println(data.getName());
            for (LinkedList<SeqCond> list : data.getSequenceCondition()) {
                //Nytt conditionexpression -> or
                System.out.println("pre..OR..");
                for (SeqCond seqCond : list) {
                    System.out.println("pre..AND..");
                    //Nytt conditionexpression and
                    System.out.println(seqCond.id);
                    System.out.println(seqCond.value);
                    System.out.println(seqCond.state);
                }
            }
        } else {
            System.out.println("Post");
        }


        return condition;
    }
}
