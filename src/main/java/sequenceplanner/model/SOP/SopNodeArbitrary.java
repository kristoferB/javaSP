package sequenceplanner.model.SOP;

import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.visualization.algorithms.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeArbitrary extends ASopNode {

    public SopNodeArbitrary() {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER, "", ""));
    }
}
