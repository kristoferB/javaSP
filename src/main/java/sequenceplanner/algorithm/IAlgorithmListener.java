package sequenceplanner.algorithm;

import java.util.List;

/**
 *
 * @author patrik
 */
public interface IAlgorithmListener {

    void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm);

    void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm);
}
