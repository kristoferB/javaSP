package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;

/**
 *
 * @author patrik
 */
public class Visualization implements IVisualization {

    RelationContainer mRC;

    public Visualization(Model iModel) {
        mRC = new RelationContainer();
    }

    @Override
    public boolean addOset(ISopNode iSopNode) {
        return mRC.setOsetSopNode(iSopNode);
    }

    @Override
    public boolean addOsubset(ISopNode iSopNode) {
        return mRC.setOsubsetSopNode(iSopNode);
    }

    @Override
    public boolean addToOfinish(ISopNode iSopNode) {
        return mRC.setOfinishsetSopNode(iSopNode);
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
        RelationsForOperationSet rfos = new RelationsForOperationSet(getmRC());
        switch (rfos.run()) {
            case 0:
                return null;
            case 1:
                return new SopNodeWithRelations(new SopNode(), null); //No supervisor found
            case 2:
                break;
        }
        if (!rfos.saveFormalModel("C:/Users/patrik/Desktop/VisualizationAutomaton.wmod")) {
            return null;
        }
        return rfos.getSopRootWithRelations();
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

    public RelationContainer getmRC() {
        return mRC;
    }

    public void setmRC(RelationContainer mRC) {
        this.mRC = mRC;
    }


}
