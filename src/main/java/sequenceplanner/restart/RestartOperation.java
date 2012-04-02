package sequenceplanner.restart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author patrik
 */
public class RestartOperation implements IRestartOperation {

    private final Map<Property, Object> mMap;

    public RestartOperation(final String iName, final boolean iHasToFinish) {
        this(iName, iHasToFinish, "", "", "", "");
    }

    public RestartOperation(final String iName, final boolean iHasToFinish, final String iExtraStartGuard, final String iExtraStartAction, final String iExtraFinishGuard, final String iExtraFinishAction) {
        mMap = new HashMap<Property, Object>();

        mMap.put(Property.NAME, iName);
        mMap.put(Property.HAS_TO_FINISH, iHasToFinish);
        initExcludeMap();

        mMap.put(Property.EXTRA_START_GUARD, iExtraStartGuard);
        mMap.put(Property.EXTRA_START_ACTION, iExtraStartAction);
        mMap.put(Property.EXTRA_FINISH_GUARD, iExtraFinishGuard);
        mMap.put(Property.EXTRA_FINISH_ACTION, iExtraFinishAction);
    }

    @Override
    public Map<Property, Set<Set<IRestartOperation>>> getExcludeMap() {
        return (Map<Property, Set<Set<IRestartOperation>>>) mMap.get(Property.EXCLUDE_MAP);
    }

    private void initExcludeMap() {
        final Map<Property, Set<Set<IRestartOperation>>> map = new HashMap<Property, Set<Set<IRestartOperation>>>();
        final Set<Property> properties = new HashSet<Property>();
        properties.add(Property.CONTAINS);
        properties.add(Property.NOT_CONTAINS);
        properties.add(Property.NOT_ONLY_CONTAINS);
        for (final Property prop : properties) {
            map.put(prop, new HashSet<Set<IRestartOperation>>());
        }
        mMap.put(Property.EXCLUDE_MAP, map);
    }

    @Override
    public String getName() {
        return (String) mMap.get(Property.NAME);
    }

    @Override
    public boolean hasToFinish() {
        return (Boolean) mMap.get(Property.HAS_TO_FINISH);
    }

    @Override
    public String toString() {
        String extra = "";
        if (hasToFinish()) {
            extra += "(f)";
        }
        if (!getExtraStartGuard().isEmpty() || !getExtraStartAction().isEmpty() || !getExtraFinishGuard().isEmpty() || !getExtraFinishAction().isEmpty()) {
            extra += "*";
        }
        return getName() + extra;
    }

    @Override
    public String getExtraFinishAction() {
        return (String) mMap.get(Property.EXTRA_FINISH_ACTION);
    }

    @Override
    public String getExtraFinishGuard() {
        return (String) mMap.get(Property.EXTRA_FINISH_GUARD);
    }

    @Override
    public String getExtraStartAction() {
        return (String) mMap.get(Property.EXTRA_START_ACTION);
    }

    @Override
    public String getExtraStartGuard() {
        return (String) mMap.get(Property.EXTRA_START_GUARD);
    }
}
