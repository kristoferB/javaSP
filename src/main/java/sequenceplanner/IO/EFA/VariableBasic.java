package sequenceplanner.IO.EFA;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementaiton of {@link IVariable}.
 * @author patrik
 */
public class VariableBasic implements IVariable {

    protected final String mVarLabel;
    private final String mVarLowerBound;
    private final String mVarUpperBound;
    private final String mInitValue;
    private final String mMarkedValue;

    public VariableBasic(String mVarLabel, String mVarLowerBound, String mVarUpperBound, String mInitValue, String mMarkedValue) {
        this.mVarLabel = mVarLabel;
        this.mVarLowerBound = mVarLowerBound;
        this.mVarUpperBound = mVarUpperBound;
        this.mInitValue = mInitValue;
        this.mMarkedValue = mMarkedValue;
    }

    @Override
    public String getVarInitValue() {
        return mInitValue;
    }

    @Override
    public String getVarLabel() {
        return mVarLabel;
    }

    @Override
    public String getVarLowerBound() {
        return mVarLowerBound;
    }

    @Override
    public Set<String> getVarMarkedValues() {
        final Set<String> set = new HashSet<String>();
        if (mMarkedValue != null) {
            set.add(mMarkedValue);
        }
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
