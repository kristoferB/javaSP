package sequenceplanner.multiProduct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionOperator;
import sequenceplanner.condition.ConditionStatment;
import sequenceplanner.general.SP;
import sequenceplanner.model.data.OperationData;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class TestOfConditionPackage {

    SP sp = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void method1() {
        final OperationData op1 = sp.insertOperation();
        final OperationData op2 = sp.insertOperation();
        final OperationData op3 = sp.insertOperation();
        final OperationData op4 = sp.insertOperation();
        final OperationData op5 = sp.insertOperation();

        //How to get (op1006 and op1007) or (op1008 and (op1009 or op1010)) as guard

        //Left clause------------------------------------------------------------
        ConditionExpression left = new ConditionExpression();
        ConditionStatment cs1 = new ConditionStatment(Integer.toString(op1.getId()), ConditionStatment.Operator.Equal, "2");
        ConditionStatment cs2 = new ConditionStatment(Integer.toString(op2.getId()), ConditionStatment.Operator.Equal, "2");
        left.changeExpressionRoot(cs1);
        left.appendElement(ConditionOperator.Type.AND, cs2);
        //-----------------------------------------------------------------------

        //Right clause------------------------------------------------------------
        ConditionStatment cs4 = new ConditionStatment(Integer.toString(op4.getId()), ConditionStatment.Operator.Equal, "2");
        ConditionExpression subRight = new ConditionExpression(cs4);
        ConditionStatment cs5 = new ConditionStatment(Integer.toString(op5.getId()), ConditionStatment.Operator.Equal, "2");
        subRight.appendElement(ConditionOperator.Type.OR, cs5);

        ConditionExpression right = new ConditionExpression();
        ConditionStatment cs3 = new ConditionStatment(Integer.toString(op3.getId()), ConditionStatment.Operator.Equal, "2");

        right.changeExpressionRoot(cs3);
        right.appendElement(ConditionOperator.Type.AND, subRight);
        //-----------------------------------------------------------------------

        //Merge left and right---------------------------------------------------
        Condition c = new Condition();
        ConditionExpression ce = c.getGuard();

        ce.changeExpressionRoot(left);
        ce.appendElement(ConditionOperator.Type.OR, right);
        //-----------------------------------------------------------------------
        
        System.out.println(c.toString());
    }
}
