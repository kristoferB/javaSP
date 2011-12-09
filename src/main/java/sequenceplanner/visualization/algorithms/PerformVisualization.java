package sequenceplanner.visualization.algorithms;

import java.util.Set;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.algorithms.ISopNodeToolbox;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.view.operationView.OperationView;

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
public class PerformVisualization implements IPerformVisualization {

    private RelationContainer mRC;
    private String mWmodPath = "";
    private Set<ConditionData> mConditionsToInclude;
    private Set<ResourceVariableData> resources;


    /**
     *
     * @param iWmodPath where to store wmod file of operations.
     */
    public PerformVisualization(final String iWmodPath, final Set<ConditionData> iConditionsToInclude) {
        mRC = new RelationContainer();
        mWmodPath = iWmodPath;
        mConditionsToInclude = iConditionsToInclude;
    }

    @Override
    public boolean addOset(SopNode iSopNode) {
        return mRC.setOsetSopNode(iSopNode);
    }

    @Override
    public boolean addOsubset(SopNode iSopNode) {
        return mRC.setOsubsetSopNode(iSopNode);
    }

    @Override
    public boolean addToOfinish(SopNode iSopNode) {
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
        RelationsForOperationSet rfos = new RelationsForOperationSet(getmRC(),mWmodPath, mConditionsToInclude,this.resources);
        switch (rfos.run()) {
            case 0: //Errors have occured
                return null;
            case 1: //No supervisor found -> RelationsForOperationSet.ismSupervisorExists() returns false;
                return null;
            case 2: //Normal behaviour!
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
    public boolean sopNodeToGraphicalView(SopNode iSopNode, OperationView iView) {
        ISopNodeToolbox toolbox = new SopNodeToolboxSetOfOperations();
        toolbox.drawNode(iSopNode, iView.getGraph());
        return true;
    }

    public RelationContainer getmRC() {
        return mRC;
    }

    public void setmRC(RelationContainer mRC) {
        this.mRC = mRC;
    }
    
    public void addResources(Set<ResourceVariableData> resources){
        this.resources = resources;
    }
}
