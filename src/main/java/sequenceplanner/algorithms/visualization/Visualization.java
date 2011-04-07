package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;

/**
 *
 * @author patrik
 */
public class Visualization implements IVisualization{

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
        return mRFOS.OsetSupersetForOsubset();
    }

    @Override
    public boolean addToOfinish(ISopNode iSopNode) {
        mRFOS.setmSopNodeOfinish(iSopNode);
        return mRFOS.OsetSupersetForOfinish();
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
    public RelationsForOperationSet identifyRelations() {
        mRFOS.run();
        mRFOS.getWrapSet(); //set of IROperations. Each IRoperation contains both ISopNode and relations to other operations in set.
        return mRFOS;
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
