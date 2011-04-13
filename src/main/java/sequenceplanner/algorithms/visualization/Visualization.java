package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;

/**
 *
 * @author patrik
 */
public class Visualization implements IVisualization {

    RelationsForOperationSet mRFOS;

    public Visualization(Model iModel) {
        mRFOS = new RelationsForOperationSet(iModel);
    }

    @Override
    public void addOset(ISopNode iSopNode) {
        mRFOS.setmSopNodeOset(iSopNode);
    }

    @Override
    public boolean addOsubset(ISopNode iSopNode) {
        mRFOS.setmSopNodeOsubset(iSopNode);
        return mRFOS.OsetIsSupersetForOsubset();
    }

    @Override
    public boolean addToOfinish(ISopNode iSopNode) {
        mRFOS.setmSopNodeOfinish(iSopNode);
        return mRFOS.OsetIsSupersetForOfinish();
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
    public boolean hierarchicalPartition(SopNodeWithRelations ioNode) {
        HierarchicalPartition hp = new HierarchicalPartition(ioNode);
        return true;
    }

    @Override
    public SopNodeWithRelations identifyRelations() {
        mRFOS.run();
        if (!mRFOS.saveFormalModel("C:/Users/patrik/Desktop/VisualizationAutomaton.wmod")) {
            return null;
        }
        return mRFOS.getSopRootWithRelations();
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
    public boolean sopNodeToGraphicalView(ISopNode iSopNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
