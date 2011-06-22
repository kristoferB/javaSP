package sequenceplanner.model.SOP;

import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeAlternative extends ASopNode {

    private int id;

    public SopNodeAlternative(int id) {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", ""), id);
        this.id = id;
    }

    public SopNodeAlternative() {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ALTERNATIVE, "", ""));
    }

    @Override
    public String toString() {
        return "Parallel-" + id;
    }
}
