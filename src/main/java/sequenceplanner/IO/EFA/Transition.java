package sequenceplanner.IO.EFA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data structure for transitions.
 * @author patrik
 */
public class Transition {

    final public static String CONTROLABLE = "CONTROLABLE";

    final private String mLabel;
    final private Set<String> mGuardConjunctionSet;
    final private Set<String> mActionSet;
    private Location mStartLocation = null;
    private Location mFinishLocation = null;
    private Map<String, Object> mAttributeMap;

    public Transition(String mLabel) {
        this.mLabel = mLabel;
        mGuardConjunctionSet = new HashSet<String>();
        mActionSet = new HashSet<String>();
        mAttributeMap = new HashMap<String, Object>();
    }

    public Location getmFinishLocation() {
        return mFinishLocation;
    }

    public void setmFinishLocation(Location mFinishLocation) {
        this.mFinishLocation = mFinishLocation;
    }

    public Location getmStartLocation() {
        return mStartLocation;
    }

    public void setmStartLocation(Location mStartLocation) {
        this.mStartLocation = mStartLocation;
    }

    public Set<String> getmActionSet() {
        return mActionSet;
    }

    public Set<String> getmGuardConjunctionSet() {
        return mGuardConjunctionSet;
    }

    public String getmLabel() {
        return mLabel;
    }

    /**
     * 
     * @param iKey
     * @return <code>value</code> or <code>null</code> if no value for <code>iKey</code>
     */
    public Object getAttribute(final String iKey) {
        if (!mAttributeMap.containsKey(iKey)) {
            return null;
        }
        return mAttributeMap.get(iKey);
    }

    /**
     *
     * @param iKey
     * @param iValue
     * @return <code>true</code> if value was set else <code>false</code>
     */
    public boolean setAttribute(final String iKey, final Object iValue) {
        if (iKey == null || iKey.equals("")) {
            return false;
        }
        mAttributeMap.put(iKey, iValue);
        return true;
    }

    public void andAction(final String iToAnd) {
        andToSet(iToAnd, mActionSet);
    }

    public void andGuard(final String iToAnd) {
        andToSet(iToAnd, mGuardConjunctionSet);
    }

    private static void andToSet(final String iToAdd, final Set<String> iSet) {
        if (iToAdd == null || iToAdd.length() < 1) {
            return;
        }
        iSet.add(iToAdd);
    }

    public class Location {

        final private IVariable mVariable;
        final private Integer mValue;

        public Location(IVariable mVariable, Integer mValue) {
            this.mVariable = mVariable;
            this.mValue = mValue;
        }

        public String getLocation() {
            return mVariable.getVarLabel() + "==" + mValue;
        }
    }
}
