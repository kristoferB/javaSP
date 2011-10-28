package sequenceplanner.visualization.algorithms;

import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.view.operationView.OperationView;

/**
 * Interface for how to visualize/project the relations between the operations in a subset with respect to some set.<br/>
 * Set: Oset, in most cases all operations def in SP. The operations that the relations should be based on.<br/>
 * Subset: Osubset s.t. Osubset \subseteq Oset, the subset of operations that should be visualized in a SOP.<br/>
 * HasToFinishSet: Ofinish s.t. Ofinish \subseteq Oset, a user can select what operations that has to finish (gives Mk = {Of} instead of Mk = {Oi,Oe,Of}).<br/>
 * The Ofinish set affects the non-blocking synthesis.<br/>
 * @author patrik
 */
public interface IPerformVisualization {

    /**
     * Add operations to Oset.<br/>
     * @param iSopNode operations to base relation on.
     * @return true if ok else false
     */
    public boolean addOset(final ISopNode iSopNode);

    /**
     * Add Osubset as operations in {@link ISopNode}.<br/>
     * @param iSopNode operations to find relations for.
     * @return true if operations in iSopNode \subseteq Oset else false
     */
    public boolean addOsubset(final ISopNode iSopNode);

    /**
     * Add operations to Ofinish.<br/>
     * @param iSopNode operations that has to finish in synthesis.
     * @return true if operations in iSopNode \subseteq Oset else false
     */
    public boolean addToOfinish(final ISopNode iSopNode);

    /**
     * Brute force:<br/>
     * Oset -> 3-location-EFA (marking as Ofinish) -> synthesize EFA -><br/>
     * -> loop state space and find locations for operations in Osubset for each event in alphabet-Osubset -><br/>
     * -> identify relations for each operation pair in Osubset -> create ARelationsForOperationSet.<br/>
     * @return The relations according to interface ARelationsForOperationSet
     */
    public RelationContainer identifyRelations();

    /**
     * Algorithm for hierarchical partition of operations in an {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @return true if ok else false
     */
    public boolean hierarchicalPartition(IRelationContainer ioRC);

    /**
     * Algorithm for alternative partition of operations in {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @return true if ok else false
     */
    public boolean alternativePartition(IRelationContainer ioRC);

    /**
     * Algorithm for arbitrary order partition of operations in {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @return true if ok else false
     */
    public boolean arbitraryOrderPartition(IRelationContainer ioRC);

    /**
     * Algorithm for parallel partition of operations in {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @return true if ok else false
     */
    public boolean parallelPartition(IRelationContainer ioRC);

    /**
     * Algorithm for sequenceing of operations in {@link ISopNode}.<br/>
     * @param iSopNode operations to sequence
     * @return true if ok else false
     */
    boolean sequenceing(IRelationContainer ioRC);

    /**
     * Show {@link ISopNode} as view to user.<br/>
     * @param iSopNode operations to visualize
     * @return true if possible to create view else false
     */
    public boolean sopNodeToGraphicalView(ISopNode iSopNode, OperationView iView);

    public void addResources(Set<ResourceVariableData> resources);
}
