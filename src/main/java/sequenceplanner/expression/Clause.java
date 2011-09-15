package sequenceplanner.expression;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author patrik
 */
public class Clause implements IClause {

    private final List<ILiteral> mLiterals;

    public Clause() {
        mLiterals = new ArrayList<ILiteral>();
    }

    @Override
    public List<ILiteral> getLiteralList() {
        return mLiterals;
    }

    public void addLiteral(final ILiteral iLiteral) {
        if (iLiteral == null) {
            return;
        }
        mLiterals.add(iLiteral);
    }

    @Override
    public String toString() {
        String returnString = "| ";
        for (final ILiteral literal : mLiterals) {
            returnString += literal.toString() + " | ";
        }
        returnString = "\n";
        return returnString;
    }
}
