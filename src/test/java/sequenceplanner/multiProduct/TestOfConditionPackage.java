package sequenceplanner.multiProduct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionElement;
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
        OperationData op1 = sp.insertOperation();
        OperationData op2 = sp.insertOperation();

        Condition c = new Condition();
        ConditionExpression ce = c.getGuard();
        ConditionStatment cs1 = new ConditionStatment(Integer.toString(op1.getId()), ConditionStatment.Operator.NotEqual, "1");
        ConditionStatment cs2 = new ConditionStatment(Integer.toString(op2.getId()), ConditionStatment.Operator.NotEqual, "2");
        ConditionOperator co = new ConditionOperator(ConditionOperator.Type.OR);
        assertTrue(ce.appendElement(co, cs1));
        ce.appendElement(co, cs2);
        for (ConditionElement cee : ce.getConditionElements()) {
            System.out.println("start");
            System.out.println(cee.toString());
            System.out.println("stop");
        }

        System.out.println(c.getGuard().getExpressionRoot().toString());
        System.out.println(c.getGuard().getExpressionRoot().getNextElement().toString());
        System.out.println(c.getGuard().getExpressionRoot().getNextOperator().getOperatorType().toString());
        System.out.println(c.toString());
    }
}
