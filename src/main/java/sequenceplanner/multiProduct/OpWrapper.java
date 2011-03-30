package sequenceplanner.multiProduct;

import java.util.HashMap;
import java.util.Set;

/**
 * Wrapper class for operations.</br>
 * Only fields for necessary information in order to find isotopes
 * @author patrikm
 */
public class OpWrapper {
    private HashMap<String,UnitTypeWrapper> mUnitTypeMap = null;
    String mName = "";
    Integer mId = null;
    Set<OpWrapper> mChildren = null;
    String mCondition = "";
    String mProductType = "";

    public OpWrapper() {
        mUnitTypeMap = new HashMap<String, UnitTypeWrapper>(2);
    }

    /**
     * To source and destination {@link UnitTypeWrapper}
     * @param iKey "source" or "dest"
     * @param iValue the {@link UnitTypeWrapper}
     */
    public void setUnitType(String iKey, UnitTypeWrapper iValue) {
        mUnitTypeMap.put(iKey,iValue);
    }

    /**
     *
     * @param iKey "source" or "dest"
     * @return the {@link UnitTypeWrapper}
     */
    public UnitTypeWrapper getUnitType(String iKey) {
        return mUnitTypeMap.get(iKey);
    }

    /**
     * To get unit types used by children to this operation
     * @param iKey "source" or "dest"
     * @param oUTSet Found unit types are added to this set
     * @param iExcludeSet Names of unit types that should be excluded
     */
    public void getUnitTypeSet(String iKey, Set<UnitTypeWrapper> oUTSet, Set<String> iExcludeSet) {
        for(final OpWrapper op : mChildren) {
            UnitTypeWrapper ut = op.getUnitType(iKey);
            if(!iExcludeSet.contains(ut.mName)) {
                oUTSet.add(ut);
            }
        }
    }
    /**
     *
     * @param iEvent
     * @return the {@link OpWrapper} or null if no operation was found
     */
    public OpWrapper getOpFromEvent(String iEvent) {
        for(final OpWrapper op : mChildren) {
            if(iEvent.startsWith(op.getEventName())) {
                return op;
            }
        }
        return null;
    }

    public boolean singleUnitType() {
        return getUnitType("source").equals(getUnitType("dest"));
    }

    public String getEventName() {
        return "e" + mId;
    }
 }
