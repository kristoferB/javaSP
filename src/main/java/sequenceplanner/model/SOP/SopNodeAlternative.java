package sequenceplanner.model.SOP;

import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeAlternative extends ASopNode {

    public SopNodeAlternative() {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", ""));
    }
}
