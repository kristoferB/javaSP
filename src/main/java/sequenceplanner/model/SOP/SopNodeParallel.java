package sequenceplanner.model.SOP;

import sequenceplanner.algorithms.visualization.IRelateTwoOperations;
import sequenceplanner.algorithms.visualization.RelateTwoOperations;

/**
 *
 * @author patrik
 */
public class SopNodeParallel extends ASopNode{
    private int id;
    public SopNodeParallel(int id) {
        super(RelateTwoOperations.relationIntegerToString(IRelateTwoOperations.PARALLEL, "", ""),id);
        this.id=id;
    }

    @Override
    public String toString(){
        return "Parallel-"+id;
    }
}
