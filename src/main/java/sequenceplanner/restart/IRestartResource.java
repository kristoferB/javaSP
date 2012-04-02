package sequenceplanner.restart;

import java.util.Set;

/**
 *
 * @author patrik
 */
public interface IRestartResource {

    enum Property {

        NAME, BRANCH;
    }

    String getName();

    Set<Set<IRestartOperation>> getBranchSet();

    void addBranch(final Set<IRestartOperation> iBranch);
}
