package sequenceplanner.IO.EFA;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementaiton of {@link IVariable} where initial value and marked value is the same.<br/>
 * @author patrik
 */
public class VariableSameInitMarkedValue implements IVariable {

    protected final String mVarLabel;
    private final String mVarLowerBound;
    private final String mVarUpperBound;
    private final String mInitMarkedValue;

    public VariableSameInitMarkedValue(String mVarLabel, String mVarLowerBound, String mVarUpperBound, String mInitMarkedValue) {
        this.mVarLabel = mVarLabel;
        this.mVarLowerBound = mVarLowerBound;
        this.mVarUpperBound = mVarUpperBound;
        this.mInitMarkedValue = mInitMarkedValue;
    }

    @Override
    public String getVarInitValue() {
        return mInitMarkedValue;
    }

    @Override
    public String getVarLabel() {
        return "V_" + mVarLabel;
    }

    @Override
    public String getVarLowerBound() {
        return mVarLowerBound;
    }

    @Override
    public Set<String> getVarMarkedValues() {
        final Set<String> set = new HashSet<String>();
        set.add(mInitMarkedValue);
        return set;
    }

    @Override
    public String getVarUpperBound() {
        return mVarUpperBound;
    }

    @Override
    public String toString() {
        return getVarLabel();
    }
}
