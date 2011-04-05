package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.ISopNode;

/**
 *
 * @author patrik
 */
public class Visualization implements IVisualization{

    RelationsForOperationSet mRFOS = new RelationsForOperationSet();

    public Visualization() {
    }

    @Override
    public void addOset(ISopNode iSopNode) {
        mRFOS.setmSopNodeOset(iSopNode);
    }

    @Override
    public boolean addOsubset(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addToOfinish(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISopNode alternativePartition(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISopNode arbitraryOrderPartition(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISopNode hierarchicalPartition(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IRelationsForOperationSet identifyRelations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISopNode parallelPartition(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISopNode sequenceing(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean sopNodeToGraphicalView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
