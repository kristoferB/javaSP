package sequenceplanner.restart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author patrik
 */
public class RestartResource implements IRestartResource {

    private final Map<Property, Object> mMap;

    public RestartResource(final String iName) {
        mMap = new HashMap<Property, Object>();

        mMap.put(Property.NAME, iName);
        mMap.put(Property.BRANCH, new HashSet<Set<IRestartOperation>>());
    }

    @Override
    public String getName() {
        return (String) mMap.get(Property.NAME);
    }

    @Override
    public Set<Set<IRestartOperation>> getBranchSet() {
        return (Set<Set<IRestartOperation>>) mMap.get(Property.BRANCH);
    }

    @Override
    public void addBranch(final Set<IRestartOperation> iBranch) {
        ((Set<Set<IRestartOperation>>) mMap.get(Property.BRANCH)).add(iBranch);
    }

    @Override
    public String toString() {
        return getName() + getBranchSet();
    }
}


