package sequenceplanner.multiProduct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.condition.parser.AStringToConditionParser;
import sequenceplanner.condition.parser.ActionAsTextInputToConditionParser;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionElement;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionOperator;
import sequenceplanner.condition.ConditionStatement;
import sequenceplanner.condition.parser.GuardAsTextInputToConditionParser;
import sequenceplanner.condition.parser.SupremicaGuardToConditionParser;
import sequenceplanner.general.SP;
import sequenceplanner.model.data.OperationData;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class T_conditionPackage {

    SP sp = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

//    @Test
    public void method1() {
        final OperationData op1 = sp.insertOperation();
        final OperationData op2 = sp.insertOperation();
        final OperationData op3 = sp.insertOperation();
        final OperationData op4 = sp.insertOperation();
        final OperationData op5 = sp.insertOperation();

        //How to get (op1006 and op1007) or (op1008 and (op1009 or op1010)) as guard

        //Left clause------------------------------------------------------------
        ConditionExpression left = new ConditionExpression();
        ConditionStatement cs1 = new ConditionStatement(Integer.toString(op1.getId()), ConditionStatement.Operator.Equal, "2");
        ConditionStatement cs2 = new ConditionStatement(Integer.toString(op2.getId()), ConditionStatement.Operator.Equal, "2");
        left.changeExpressionRoot(cs1);
        left.appendElement(ConditionOperator.Type.AND, cs2);
        //-----------------------------------------------------------------------

        //Right clause------------------------------------------------------------
        ConditionStatement cs4 = new ConditionStatement(Integer.toString(op4.getId()), ConditionStatement.Operator.Equal, "2");
        ConditionExpression subRight = new ConditionExpression(cs4);
        ConditionStatement cs5 = new ConditionStatement(Integer.toString(op5.getId()), ConditionStatement.Operator.Equal, "2");
        subRight.appendElement(ConditionOperator.Type.OR, cs5);

        ConditionExpression right = new ConditionExpression();
        ConditionStatement cs3 = new ConditionStatement(Integer.toString(op3.getId()), ConditionStatement.Operator.Equal, "2");

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

        //Testing----------------------------------------------------------------
        ConditionExpression ceT = c.getGuard();

        ConditionStatement cs6 = new ConditionStatement(Integer.toString(op3.getId()), ConditionStatement.Operator.Equal, "2");
        ConditionExpression cee = new ConditionExpression(cs6);
        System.out.println(cee.clone());
        ConditionElement ceT2 = ceT.getExpressionRoot().getNextElement();
        System.out.println(((ConditionExpression) ceT2).getExpressionRoot());
        //-----------------------------------------------------------------------
    }

    @Test
    public void method2() {
        String supremicaGuard = "(T2_P1 != 0 | ((R1_P1 != 0 & R1_P1 != 4) | ((M2 != 0 | (R1 != 1 | ((T2 != 0 | (cP2 != 0 | R1_F2 == 3)) & (T2 != 1 | (T2_P2 != 0 | cP2 != 0))))) & (M2 != 1 | (R1 != 1 | ((((cP1 != 0 | R1_F2 == 3) & (cP1 != 2 | ((M1_P1 == 1 | R1_F2 == 3) & (M1_P1 != 1 | ((T2 != 0 | R1_F2 == 3) & T2 != 1))))) & (cP1 != 1 | ((M1_P1 == 1 | ((T2 != 0 | R1_F2 == 3) & T2 != 1)) & (M1_P1 != 1 | R1_F2 == 3)))) & (cP1 != 3 | ((T2 != 0 | R1_F2 == 3) & T2 != 1))))))))";
        String guard = "(id1234<e&id1002!=e&&(id1003==12342&id1004!=e))&&id1005==2|id1006!=e&&id1007==e||(id1008==2&id1009!=f)";
        String action = "(id1234=100;id1002+=2;(id1003=123;id1004=2));id1005-=2;id1006+=99;id1007=7";

        AStringToConditionParser parser;
        ConditionExpression ce;

        parser = new SupremicaGuardToConditionParser();
        ce = new ConditionExpression();
        assertTrue(parser.run(supremicaGuard, ce));
        ce = new ConditionExpression();
        assertFalse(parser.run(guard, ce));
        ce = new ConditionExpression();
        assertFalse(parser.run(action, ce));

        parser = new GuardAsTextInputToConditionParser();
        ce = new ConditionExpression();
        assertFalse(parser.run(supremicaGuard, ce));
        ce = new ConditionExpression();
        assertTrue(parser.run(guard, ce));
        ce = new ConditionExpression();
        assertFalse(parser.run(action, ce));

        parser = new ActionAsTextInputToConditionParser();
        ce = new ConditionExpression();
        assertFalse(parser.run(supremicaGuard, ce));
        ce = new ConditionExpression();
        assertFalse(parser.run(guard, ce));
        ce = new ConditionExpression();
        assertTrue(parser.run(action, ce));

    }

}
