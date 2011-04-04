package sequenceplanner.algorithms.visualization;

/**
 *
 * @author patrik
 */
public interface IRelateTwoOperations {

    Integer ALWAYS_IN_SEQUENCE_12 = 0;
    Integer ALWAYS_IN_SEQUENCE_21 = 1;
    Integer SOMETIMES_IN_SEQUENCE_12 = 2;
    Integer SOMETIMES_IN_SEQUENCE_21 = 3;
    Integer PARALLEL = 4;
    Integer ALTERNATIVE = 5;
    Integer ARBITRARY_ORDER = 6;
    Integer HIERARCHY_12 = 7;
    Integer HIERARCHY_21 = 8;
    Integer OTHER = 9;

    public void setOperationPair(final IROperation iOperation1, final IROperation iOperation2);

    public Integer getOperationRelation();
}
