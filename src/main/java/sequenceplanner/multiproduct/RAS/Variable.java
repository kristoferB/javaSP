package sequenceplanner.multiproduct.RAS;

import sequenceplanner.IO.EFA.VariableSameInitMarkedValue;

/**
 *
 * @author patrik
 */
public class Variable extends VariableSameInitMarkedValue {

    public Variable(String mVarLabel, String mVarUpperBound, String mInitMarkedValue) {
        super(mVarLabel, "0", mVarUpperBound, mInitMarkedValue);
    }

    @Override
    public String toString() {
        return mVarLabel + "(" + getVarUpperBound() + ")";
    }

    @Override
    public String getVarLabel() {
        return mVarLabel;
    }
}
