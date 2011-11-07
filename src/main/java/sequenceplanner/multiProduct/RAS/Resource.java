package sequenceplanner.multiproduct.RAS;

import sequenceplanner.IO.EFA.VariableSameInitMarkedValue;

/**
 *
 * @author patrik
 */
public class Resource extends VariableSameInitMarkedValue {

    public Resource(String mVarLabel, String mVarUpperBound, String mInitMarkedValue) {
        super(mVarLabel, "0", mVarUpperBound, mInitMarkedValue);
    }

    @Override
    public String toString() {
        return mVarLabel + "(" + getVarUpperBound() + ")";
    }

    @Override
    public String getVarLabel() {
        return "Vr_" + mVarLabel;
    }
}
