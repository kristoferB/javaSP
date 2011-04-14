package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.ISopNode;

/**
 * Interface for how to visualize/project the relations between the operations in a subset with respect to some set.<br/>
 * Set: Oset, in most cases all operations def in SP. The operations that the relations should be based on.<br/>
 * Subset: Osubset s.t. Osubset \subseteq Oset, the subset of operations that should be visualized in a SOP.<br/>
 * HasToFinishSet: Ofinish s.t. Ofinish \subseteq Oset, a user can select what operations that has to finish (gives Mk = {Of} instead of Mk = {Oi,Oe,Of}).<br/>
 * The Ofinish set affects the non-blocking synthesis.<br/>
 * @author patrik
 */
public interface IVisualization {

    /**
     * Add operations to Oset.<br/>
     * @param iSopNode operations to base relation on.
     */
    public void addOset(final ISopNode iSopNode);

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
    public SopNodeWithRelations identifyRelations();

    /**
     * Algorithm for hierarchical partition of operations in an {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @return true if ok else false
     */
    public boolean hierarchicalPartition(SopNodeWithRelations ioNode);

    /**
     * Algorithm for alternative partition of operations in {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @returna true if ok else false
     */
    public boolean alternativePartition(SopNodeWithRelations ioNode);

    /**
     * Algorithm for arbitrary order partition of operations in {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @return true if ok else false
     */
    public boolean arbitraryOrderPartition(SopNodeWithRelations ioNode);

    /**
     * Algorithm for parallel partition of operations in {@link ISopNode}.<br/>
     * @param ioNode contains operations to partition and their relations
     * @returna true if ok else false
     */
    public boolean parallelPartition(SopNodeWithRelations ioNode);

    /**
     * Algorithm for sequenceing of operations in {@link ISopNode}.<br/>
     * @param iSopNode operations to sequence
     * @returna {@link ISopNode} where operations are sequenced
     */
    public ISopNode sequenceing(final ISopNode iSopNode);

    /**
     * Show {@link ISopNode} as view to user.<br/>
     * @param iSopNode operations to visualize
     * @return true if possible to create view else false
     */
    public boolean sopNodeToGraphicalView(ISopNode iSopNode);
}
