package sequenceplanner.IO.EFA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data structure for transitions.<br/>
 * No support for {@link Location} objects today.
 * My intention was to model a transition without implementation requirements on start and finish states.
 * This should have enabled both modeling the states as locations in a EFA or as values in a variable domain.<br/>
 * To conclude: it should be no difference between locations and values inside SP!<br/>
 * @author patrik
 */
public class Transition {

    final public static String UNCONTROLLABLE = "uc";
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

    public Location getFinishLocation() {
        return mFinishLocation;
    }

    /**
     *
     * @param iLocation
     * @return true if ok else false
     */
    public boolean setFinishLocation(Location iLocation) {
        if (iLocation == null) {
            return false;
        }
        setFinishLocation(iLocation.mVariable, iLocation.mValue);
        return true;
    }

    /**
     *
     * @param iVariable
     * @param iValue
     * @return true if ok else false
     */
    public boolean setFinishLocation(IVariable iVariable, int iValue) {
        if (iVariable == null) {
            return false;
        }
        this.mFinishLocation = new Location(iVariable, iValue);
        return true;
    }

    public Location getStartLocation() {
        return mStartLocation;
    }

    /**
     *
     * @param iLocation
     * @return true if ok else false
     */
    public boolean setStartLocation(Location iLocation) {
        if (iLocation == null) {
            return false;
        }
        setStartLocation(iLocation.mVariable, iLocation.mValue);
        return true;
    }

    /**
     *
     * @param iVariable
     * @param iValue
     * @return true if ok else false
     */
    public boolean setStartLocation(IVariable iVariable, int iValue) {
        if (iVariable == null) {
            return false;
        }
        this.mStartLocation = new Location(iVariable, iValue);
        return true;
    }

    public Set<String> getmActionSet() {
        return mActionSet;
    }

    public Set<String> getmGuardConjunctionSet() {
        return mGuardConjunctionSet;
    }

    public String getLabel() {
        return mLabel;
    }

    public Transition copy() {
        final Transition cTrans = new Transition(mLabel);
        cTrans.setStartLocation(getStartLocation());
        cTrans.setFinishLocation(getFinishLocation());
        for (final String guard : getmGuardConjunctionSet()) {
            cTrans.andGuard(guard);
        }
        for (final String action : getmActionSet()) {
            cTrans.andAction(action);
        }
        for (final String key : mAttributeMap.keySet()) {
            cTrans.setAttribute(key, getAttribute(key));
        }
        return cTrans;
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

        final IVariable mVariable;
        final int mValue;

        public Location(IVariable iVariable, int iValue) {
            this.mVariable = iVariable;
            this.mValue = iValue;
        }

        @Override
        public String toString() {
            return mVariable + " == " + mValue;
        }
    }
}
