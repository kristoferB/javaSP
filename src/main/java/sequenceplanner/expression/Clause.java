package sequenceplanner.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * More or less a class that cotains a list for {@link ILiteral}s.</br>
 * @author patrik
 */
public class Clause {

    private boolean isNegative = false;
    private final List<ILiteral> mLiterals;

    public Clause() {
        mLiterals = new ArrayList<ILiteral>();
    }

    public List<ILiteral> getLiteralList() {
        return mLiterals;
    }

    public void addLiteral(final ILiteral iLiteral) {
        if (iLiteral == null) {
            return;
        }
        mLiterals.add(iLiteral);
    }

    public boolean isNegative() {
        return isNegative;
    }

    public void setIsNegative(boolean isNegative) {
        this.isNegative = isNegative;
    }

    @Override
    public String toString() {
        String returnString = "";
        if (mLiterals.isEmpty()) {
            return returnString;
        }
        returnString += "| ";
        for (final ILiteral literal : mLiterals) {
            returnString += literal.toString() + " | ";
        }
        return returnString;
    }
}
