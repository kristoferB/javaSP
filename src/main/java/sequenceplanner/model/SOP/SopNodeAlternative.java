package sequenceplanner.model.SOP;

import sequenceplanner.visualization.algorithms.IRelateTwoOperations;
import sequenceplanner.visualization.algorithms.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeAlternative extends ASopNode {

    public SopNodeAlternative() {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", ""));
    }
}
