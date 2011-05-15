package sequenceplanner.multiProduct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.IO.ReadFromVolvoFile;
import sequenceplanner.general.SP;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class readFileCreateOperations {

    SP mSP = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void test1() {
        mSP = new SP();

        ReadFromVolvoFile r = new ReadFromVolvoFile("C:\\Users\\patrik\\Desktop\\Fixture.txt", null, mSP.getModel());
        assertTrue(r.run());
        r.printInfo();

    }
    
}
