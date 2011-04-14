package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;

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
    public boolean alternativePartition(SopNodeWithRelations ioNode) {
        RelationPartition rp = new RelationPartition(ioNode, IRelateTwoOperations.ALTERNATIVE);
        return true;
    }

    @Override
    public boolean arbitraryOrderPartition(SopNodeWithRelations ioNode) {
        RelationPartition rp = new RelationPartition(ioNode, IRelateTwoOperations.ARBITRARY_ORDER);
        return true;
    }

    @Override
    public boolean hierarchicalPartition(SopNodeWithRelations ioNode) {
        HierarchicalPartition hp = new HierarchicalPartition(ioNode);
        return true;
    }

    @Override
    public SopNodeWithRelations identifyRelations() {
        switch (mRFOS.run()) {
            case 0:
                return null;
            case 1:
                return new SopNodeWithRelations(new SopNode(), null); //No supervisor found
            case 2:
                break;
        }
        if (!mRFOS.saveFormalModel("C:/Users/patrik/Desktop/VisualizationAutomaton.wmod")) {
            return null;
        }
        return mRFOS.getSopRootWithRelations();
    }

    @Override
    public boolean parallelPartition(SopNodeWithRelations ioNode) {
        RelationPartition rp = new RelationPartition(ioNode, IRelateTwoOperations.PARALLEL);
        return true;
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
