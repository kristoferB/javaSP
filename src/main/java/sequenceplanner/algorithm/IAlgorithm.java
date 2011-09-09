package sequenceplanner.algorithm;

import java.util.List;

/**
 *
 * @author patrik
 */
public interface IAlgorithm {

    void init(List<Object> iList);

    /**
     * To start algorithm.<br/>
     * This triggers start of a new {@link Thread}
     */
    void start();

    /**
     * To stop algorithm.
     */
    void stop();
}
