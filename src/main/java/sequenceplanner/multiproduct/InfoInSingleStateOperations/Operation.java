package sequenceplanner.multiproduct.InfoInSingleStateOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import sequenceplanner.IO.EFA.IVariable;
import sequenceplanner.expression.Clause;

/**
 *
 * @author patrik
 */
public class Operation implements IVariable {

    public String mLabel;
    public List<Clause> mPreOperationDNFClauseList;

    public Operation(String mLabel) {
        this.mLabel = mLabel;

        mPreOperationDNFClauseList = new ArrayList<Clause>();
    }

    @Override
    public String getVarInitValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getVarLabel() {
        return "V_" + mLabel;
    }

    @Override
    public String getVarLowerBound() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getVarMarkedValues() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getVarUpperBound() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
