package sequenceplanner.model.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author erik
 */
public class DataTest {

    public DataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    /**
     * Test of equals method, of class Data.
     */
    @Test
    public void testEquals() {

        //Create simple data for tests
        Data d1 = new Data("d1", 1);
        Data d2 = new Data("d1", 1);


        //Start with just setting name and id
        System.out.println("\nTesting Data.equals with simple testobjects");
        //Tests that it is equal with itself
        assertTrue("Function is not reflexsive",
                d1.equals(d1));
        assertTrue("Function is not reflexsive",
                d2.equals(d2));

        //Tests that the two objects is said to be equal
        assertTrue("Two simple equal objects is not returned as equal",
                d1.equals(d2) );
        assertTrue("Two simple equal objects is not returned as equal",
                d2.equals(d1) );


        System.out.println("Testing Data.equals for (Not equal) with simple testobjects");
        
        d1.setName("d2");

        assertFalse(d1.equals(d2) );
        assertFalse(d2.equals(d1) );


        d1.setName("d1");
        d1.setId(12);
        assertFalse(d1.equals(d2) );
        assertFalse(d2.equals(d1) );


        //Setting more aviable values.
        //System.out.println("Testing Data.equals with complex testobjects");

    }

}