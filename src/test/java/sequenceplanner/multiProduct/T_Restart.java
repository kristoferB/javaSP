package sequenceplanner.multiProduct;

import org.junit.Test;
import sequenceplanner.restart.RestartModelRestartPaper;
import sequenceplanner.restart.RestartModelRestartInAlternative;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class T_Restart {

    @Test
    public void runRestartModelRestartPaper() {
        new RestartModelRestartPaper().run();
    }

//    @Test
    public void runRestartModelRestartInAlternative() {
        new RestartModelRestartInAlternative();
    }
}


