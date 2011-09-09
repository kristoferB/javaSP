package sequenceplanner.model.SOP;

import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.visualization.algorithms.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeParallel extends ASopNode {

    public SopNodeParallel() {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL, "", ""));
    }
}
