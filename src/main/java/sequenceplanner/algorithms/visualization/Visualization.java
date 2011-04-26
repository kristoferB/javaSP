package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.ISopNode;

/**
 * The methods should be called in the following order:<br/>
 * addOset<br/>
 * addOsubset<br/>
 * addOfinish<br/>
 * identifyRelations<br/>
 * hierarchicalPartition<br/>
 * alternativePartition<br/>
 * arbitraryOrderPartition<br/>
 * parallelPartition<br/>
 * sequenceing<br/>
 * sopNodeToGraphicalView<br/>
 * @author patrik
 */
public class Visualization implements IVisualization {

    RelationContainer mRC;

    public Visualization() {
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
    public boolean alternativePartition(IRelationContainer ioRC) {
        RelationPartition rp = new RelationPartition(ioRC, IRelateTwoOperations.ALTERNATIVE);
        return true;
    }

    @Override
    public boolean arbitraryOrderPartition(IRelationContainer ioRC) {
        RelationPartition rp = new RelationPartition(ioRC, IRelateTwoOperations.ARBITRARY_ORDER);
        return true;
    }

    @Override
    public boolean hierarchicalPartition(IRelationContainer ioRC) {
        HierarchicalPartition hp = new HierarchicalPartition(ioRC);
        return true;
    }

    @Override
    public RelationContainer identifyRelations() {
        RelationsForOperationSet rfos = new RelationsForOperationSet(getmRC());
        switch (rfos.run()) {
            case 0:
                return null;
            case 1:
                rfos.getmRC().setRootNode(null);
                System.out.println("No supervisor found!");
                return rfos.getmRC(); //No supervisor found
            case 2:
                break;
        }
        return rfos.getmRC();
    }

    @Override
    public boolean parallelPartition(IRelationContainer ioRC) {
        RelationPartition rp = new RelationPartition(ioRC, IRelateTwoOperations.PARALLEL);
        return true;
    }

    @Override
    public boolean sequenceing(IRelationContainer ioRC) {
        Sequencing s = new Sequencing(ioRC);
        return true;
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
