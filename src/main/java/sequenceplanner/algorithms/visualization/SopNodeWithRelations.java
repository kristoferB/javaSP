package sequenceplanner.algorithms.visualization;

import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;

/**
 * To store an {@link ISopNode} as root <br/>
 * and the relations among its operations in a set of {@link IROperation}.<br/>
 * @author patrik
 */
public class SopNodeWithRelations {

    private ISopNode mRootSop = null;
    private Set<IROperation> mRelationSet = null;

    public SopNodeWithRelations(final ISopNode iRootSop, final Set<IROperation> iSet) {
        setmRootSop(iRootSop);
        setmRelationSet(iSet);
    }

    public Set<IROperation> getmRelationSet() {
        return mRelationSet;
    }

    public void setmRelationSet(Set<IROperation> mRelationSet) {
        this.mRelationSet = mRelationSet;
    }

    public ISopNode getmRootSop() {
        return mRootSop;
    }

    public void setmRootSop(ISopNode mRootSop) {
        this.mRootSop = mRootSop;
    }
}
