package sequenceplanner.visualization.algorithms;

import sequenceplanner.model.data.OperationData;

/**
 * Interface for class that finds relations between two {@link OperationData} operations.
 * @author patrik
 */
public interface IRelateTwoOperations {

    //Possible relations
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

    void setOperationPair(IRelationContainer iRC, OperationData iOpData1, OperationData iOpData2);

    Integer getOperationRelation();
}
