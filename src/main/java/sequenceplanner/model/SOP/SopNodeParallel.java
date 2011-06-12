package sequenceplanner.model.SOP;

import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeParallel extends ASopNode{

    public SopNodeParallel() {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL, "", ""));
    }

}
