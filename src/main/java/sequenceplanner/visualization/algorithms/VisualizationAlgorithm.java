package sequenceplanner.visualization.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import sequenceplanner.algorithm.AAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * To manage the user interaction for {@link PerformVisualization}.<br/>
 * @author patrik
 */
public class VisualizationAlgorithm extends AAlgorithm {

    private SopNode mAllOperations;
    private SopNode mOperationsToView;
    private SopNode mHasToFinish;
    private Set<ConditionData> mConditionsToIncludeSet;
    private IPerformVisualization mVisualization = null;
    private Set<ResourceVariableData> resources;

    public VisualizationAlgorithm(String iThreadName, IAlgorithmListener iAL) {
        super(iThreadName);
        addAlgorithmListener(iAL);
    }

    @Override
    public void init(List<Object> iList) {
        this.mAllOperations = (SopNode) iList.get(0);
        this.mOperationsToView = (SopNode) iList.get(1);
        this.mHasToFinish = (SopNode) iList.get(2);
        this.mConditionsToIncludeSet = (Set<ConditionData>) iList.get(3);
        if (iList.size()>4) resources = (Set<ResourceVariableData>) iList.get(4);
    }

    @Override
    public void run() {

        if (!getStatus("Started...")) {
            return;
        }
        mVisualization = new PerformVisualization("C:/", mConditionsToIncludeSet);

        mVisualization.addResources(this.resources);
        mVisualization.addOset(mAllOperations);

        if (!mVisualization.addOsubset(mOperationsToView)) {
            System.out.println("Operations to view are not a subset of all operations!");
            if (!getStatus("Problem! See console")) {
            }
            return;
        }

        if (!mVisualization.addToOfinish(mHasToFinish)) {
            System.out.println("Operations to finish are not a subset of all operations!");
            if (!getStatus("Problem! See console")) {
            }
            return;
        }
        if (!getStatus("...SCT...")) {
            return;
        }
        final RelationContainer rc = mVisualization.identifyRelations();
        if (rc == null) {
            if (!getStatus("Problem! See console")) {
            }
            return;
        }

        if (!getStatus("...partition...")) {
            return;
        }
        mVisualization.hierarchicalPartition(rc);
        mVisualization.alternativePartition(rc);
        mVisualization.arbitraryOrderPartition(rc);
        mVisualization.parallelPartition(rc);
        mVisualization.sequenceing(rc);

//        System.out.println("\n--------------------------------");
//        System.out.println("After partition");
//        System.out.println(rc.getOsubsetSopNode());
//        System.out.println("--------------------------------");

        final List<Object> list = new ArrayList<Object>();
        list.add(rc.getOsubsetSopNode());
        fireFinishedEvent(list);
    }
}
