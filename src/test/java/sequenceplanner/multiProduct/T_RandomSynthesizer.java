package sequenceplanner.multiProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.supremica.automata.LabeledEvent;
import sequenceplanner.IO.EFA.RandomSynthesizer;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class T_RandomSynthesizer implements IAlgorithmListener {

    private static final String mFilePath = "C:\\Users\\patrik\\Desktop\\";
//    private static final String mFileName = "test.wmod";
    private static final String mFileName = "manyOperations.wmod";
    private static final int mSecondsToRunSynthesisThread = 60;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void synthesize() {
        final RandomSynthesizer synthesizer = new RandomSynthesizer("Synthesizer_Thread");
        synthesizer.addAlgorithmListener(this);

        final List list = new ArrayList();
        list.add(mFilePath + mFileName);
        list.add(88);
        list.add(50);
        synthesizer.init(list);

        synthesizer.start();

        try {
            Thread.sleep(mSecondsToRunSynthesisThread * 1000);
        } catch (InterruptedException ie) {
        }
    }

    @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
        if (iFromAlgorithm instanceof RandomSynthesizer) {
//            final Set<List<LabeledEvent>> mMarkedStringsSet = (Set<List<LabeledEvent>>) iList.get(0);
//            System.out.println("The subset of the marked language that was found:");
//            for (final List<LabeledEvent> list : mMarkedStringsSet) {
//                System.out.println(list);
//            }
        }

    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        System.out.println(iMessage);
    }
}
