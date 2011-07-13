package sequenceplanner.visualization.algorithms;

import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.view.operationView.OperationView;

/**
 * To manage the user interaction for {@link PerformVisualization}.<br/>
 * @author patrik
 */
public class StartVisualization implements Runnable {

    public Thread mWorkThread = new Thread(this, "VisualizationThread");
    private OperationView mOpView;
    private ISopNode mAllOperations;
    private ISopNode mOperationsToView;
    private ISopNode mHasToFinish;
    private Set<String> mConditionsToIncludeSet;
    private IPerformVisualization mVisualization = null;
    private SelectOperationsDialog mStatus;

    public StartVisualization(final OperationView iOpView, ISopNode mAllOperations, ISopNode mOperationsToView, ISopNode mHasToFinish, final Set<String> iConditionsToInclude, final SelectOperationsDialog iStatus) {
        this.mOpView = iOpView;
        this.mAllOperations = mAllOperations;
        this.mOperationsToView = mOperationsToView;
        this.mHasToFinish = mHasToFinish;
        this.mConditionsToIncludeSet = iConditionsToInclude;
        this.mStatus = iStatus;

    }

    public void start() {
        mWorkThread.start();
    }

    private boolean updateStatus(final String iText) {
        if (mStatus != null) {
            if (!mStatus.mGoOn) {
                return false;
            }
            mStatus.changeText(iText);
        }
        return true;
    }

    @Override
    public void run() {

        if (!updateStatus("Started...")) {
            return;
        }
        mVisualization = new PerformVisualization("C:/Users/patrik/Desktop/beforeSynthesis.wmod", mConditionsToIncludeSet);

        mVisualization.addOset(mAllOperations);

        if (!mVisualization.addOsubset(mOperationsToView)) {
            System.out.println("Operations to view are not a subset of all operations!");
            if (!updateStatus("Problem! See console")) {
            }
            return;
        }

        if (!mVisualization.addToOfinish(mHasToFinish)) {
            System.out.println("Operations to finish are not a subset of all operations!");
            if (!updateStatus("Problem! See console")) {
            }
            return;
        }
        if (!updateStatus("...SCT...")) {
            return;
        }
        final RelationContainer rc = mVisualization.identifyRelations();
        if (rc == null) {
            if (!updateStatus("Problem! See console")) {
            }
            return;
        }

        if (!updateStatus("...partition...")) {
            return;
        }
        mVisualization.hierarchicalPartition(rc);
        mVisualization.alternativePartition(rc);
        mVisualization.arbitraryOrderPartition(rc);
        mVisualization.parallelPartition(rc);
        mVisualization.sequenceing(rc);

        System.out.println("\n--------------------------------");
        System.out.println("After partition");
        System.out.println(rc.getOsubsetSopNode().toString());
        System.out.println("--------------------------------");

        if (!updateStatus("...drawing...")) {
            return;
        }
        mVisualization.sopNodeToGraphicalView(rc.getOsubsetSopNode(), mOpView);

        if (!updateStatus("...finished")) {
            return;
        }
//        System.out.println("\n--------------------------------");
//        System.out.println("Get conditions");
//        new SopNodeToolboxSetOfOperations().relationsToSelfContainedOperations(rc.getOsubsetSopNode());
//        System.out.println("--------------------------------");
    }
}
