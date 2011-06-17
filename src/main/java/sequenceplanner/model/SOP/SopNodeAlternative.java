package sequenceplanner.model.SOP;

import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeAlternative extends ASopNode{

    public SopNodeAlternative(int id) {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", ""),id);
    }

}
