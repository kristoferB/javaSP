package sequenceplanner.model.data;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
//
//import sequenceplanner.model.data.OperationData.SeqCond;
//
///**
// *
// * @author hamid2
// */
public class OperationDataTest {

    public OperationDataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    OperationData d1 = null;
    OperationData d2 = null;
//
//    @Before
//    public void setUp() {
//        //Create simple data for tests
//        d1 = new OperationData("d1", 1);
//        d2 = new OperationData("d1", 1);
//
//        //D1 sequence conditions
//        LinkedList<SeqCond> or1 = new LinkedList<SeqCond>();
//        or1.add(new SeqCond(20, 2));
//        or1.add(new SeqCond(30, 2));
//        or1.add(new SeqCond(40, 2));
//
//        d1.addOr(or1);
//        d1.addAnd(new SeqCond(10, 2));
//
//        //D1 resource booking
//        d1.addResourceBooking(100);
//        d1.addResourceBooking(101);
//        d1.addResourceBooking(102);
//
//        //D2
//        // ---- sequence conditions
//        d2.addAnd(new SeqCond(10, 2));
//
//        LinkedList<SeqCond> or2 = new LinkedList<SeqCond>();
//        or2.add(new SeqCond(30, 2));
//        or2.add(new SeqCond(20, 2));
//        or2.add(new SeqCond(40, 2));
//        d2.addOr(or1);
//
//        // ---- resource booking
//        d2.addResourceBooking(100);
//        d2.addResourceBooking(102);
//        d2.addResourceBooking(101);
//
//
//    }

    @After
    public void tearDown() {
    }

    @Test
    public static void dummy() {
        
    }
//
//
//
//    @Test
//    public void testIsPreconditionEqual() {
//        System.out.println("\nTesting OperationData.isPreconditionEqual with simple testobjects");
//        //Tests that it is equal with itself
//        assertTrue("Function is not reflexsive",
//                d1.isPreconditionEqual(d1.getSequenceCondition(), d1.getSequenceCondition()) );
//        assertTrue("Function is not reflexsive",
//                d2.isPreconditionEqual(d2.getSequenceCondition(), d2.getSequenceCondition()) );
//
//
//        //Tests that the two objects is said to be equal
//        assertTrue("Two simple equal objects is not returned as equal",
//                d1.isPreconditionEqual(d1.getSequenceCondition(), d2.getSequenceCondition()) );
//        assertTrue("Two simple equal objects is not returned as equal",
//                d2.isPreconditionEqual(d2.getSequenceCondition(), d1.getSequenceCondition()) );
//    }
//
//
//    @Test
//    public void testIsPreferencesEqual() {
//        System.out.println("\nTesting OperationData.isPreferencesEqual with simple testobjects");
//        //Tests that it is equal with itself
//        assertTrue("Function is not reflexsive",
//                d1.isPreferencesEqual(d1.getPreferences(), d1.getPreferences()) );
//        assertTrue("Function is not reflexsive",
//                d2.isPreferencesEqual(d2.getPreferences(), d2.getPreferences()) );
//
//
//        //Tests that the two objects is said to be equal
//        assertTrue("Two simple equal objects is not returned as equal",
//                d1.isPreferencesEqual(d1.getPreferences(), d2.getPreferences()) );
//        assertTrue("Two simple equal objects is not returned as equal",
//                d2.isPreferencesEqual(d2.getPreferences(), d1.getPreferences()) );
//    }
//
//
//    @Test
//    public void testIsResourceBookingEqual() {
//        System.out.println("\nTesting OperationData.isResourceBookingEqual with simple testobjects");
//        //Tests that it is equal with itself
//        assertTrue("Function is not reflexsive",
//                d1.isResourceBookingEqual(d1.getResourceBooking(), d1.getResourceBooking()));
//        assertTrue("Function is not reflexsive",
//                d2.isResourceBookingEqual(d2.getResourceBooking(), d2.getResourceBooking()));
//
//
//        //Tests that the two objects is said to be equal
//        assertTrue("Two simple equal objects is not returned as equal",
//                d1.isResourceBookingEqual(d1.getResourceBooking(), d2.getResourceBooking()));
//        assertTrue("Two simple equal objects is not returned as equal",
//                d2.isResourceBookingEqual(d2.getResourceBooking(), d1.getResourceBooking()));
//    }
//
//
//    /**
//     * Test of equals method, of class Data.
//     */
//    @Test
//    public void testEquals() {
//
//        System.out.println("\nTesting Data.equals with simple testobjects");
//        //Tests that it is equal with itself
//        assertTrue("Function is not reflexsive",
//                d1.equals(d1));
//        assertTrue("Function is not reflexsive",
//                d2.equals(d2));
//
//
//        //Tests that the two objects is said to be equal
//        assertTrue("Two simple equal objects is not returned as equal",
//                d1.equals(d2));
//        assertTrue("Two simple equal objects is not returned as equal",
//                d2.equals(d1));
//
//
//
//
//
//        System.out.println("Testing Data.equals for (Not equal) with simple testobjects");
//
//        d1.addAnd(568, 2);
//        assertFalse(d1.equals(d2));
//        d1.removeAnd(568, 2);
//
//
//    }
}