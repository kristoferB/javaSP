package sequenceplanner.model.SOP;

import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeArbitrary extends ASopNode {

    private int id;

    public SopNodeArbitrary(int id) {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER, "", ""), id);
        this.id = id;
    }

    public SopNodeArbitrary() {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.ARBITRARY_ORDER, "", ""));
    }

    @Override
    public String toString() {
        return "Parallel-" + id;
    }
}
