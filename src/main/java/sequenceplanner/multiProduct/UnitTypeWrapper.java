package sequenceplanner.multiProduct;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class for unit types and unit isotopes.
 * @author patrikm
 */
public class UnitTypeWrapper {

    String mName = "";
    Integer mVariableValue = null;
    Set<OpWrapper> mOp = new HashSet<OpWrapper>();
    Set<UnitTypeWrapper> mChildren = new HashSet<UnitTypeWrapper>();

    public UnitTypeWrapper() {
    }

    /**
     * Factory to create unit type children.<br/>
     * A new child is only created if non of the current children has name <code>iName</code>.<br/>
     * @param iOp Operation that uses the unit type
     * @param iName Name of unit type
     * @return A new unit type object or an existing unit type object
     */
    public UnitTypeWrapper unitTypeFactory(OpWrapper iOp, String iName) {
        //Checks if iName exists
        for (UnitTypeWrapper ut : mChildren) {
            if (ut.mName.equals(iName)) {
                return ut;
            }
        }

        //Create new ut
        UnitTypeWrapper ut = new UnitTypeWrapper();
        ut.mName = iName;
        ut.mVariableValue = mChildren.size();
        ut.mOp.add(iOp);
        mChildren.add(ut);
        return ut;
    }

    /**
     *
     * @param iUnitType name of unit type
     * @return the {@link UnitTypeWrapper} object or null if not found
     */
    public UnitTypeWrapper getUnitTypeChild(String iUnitType) {
        for (final UnitTypeWrapper ut : mChildren) {
            if (ut.mName.equals(iUnitType)) {
                return ut;
            }
        }
        return null;
    }

    /**
     * Compare names of two {@link UnitTypeWrapper} objects
     * @param iUT
     * @return <code>true</code> if names are equal else <code>false</code>
     */
    public boolean equals(UnitTypeWrapper iUT) {
        if (mName.equals(iUT.mName)) {
            return true;
        } else {
            return false;
        }
    }
}
