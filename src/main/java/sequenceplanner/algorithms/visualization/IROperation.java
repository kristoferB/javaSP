package sequenceplanner.algorithms.visualization;

import java.util.Set;

/**
 *
 * @author patrik
 */
public interface IROperation {

    public int getId();

    public String getStringId();

    public boolean hasToFinish();

    public Integer getRelationToIOperation(final IROperation iOperation);

    public boolean subsetContainsRelationValue(final Set<IROperation> iSet, final Integer iRelation);

    public boolean equals(final int iId);
}
