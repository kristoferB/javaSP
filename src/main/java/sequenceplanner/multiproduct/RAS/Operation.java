package sequenceplanner.multiproduct.RAS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.IO.EFA.IVariable;
import sequenceplanner.expression.Clause;
import sequenceplanner.expression.ILiteral;

/**
 * Sub operations can be added in order to handle disassembly situations.<br/>
 * In that case:
 * mPreOperationDNFClauseList for parent
 * mResourceConjuction for each sub operation
 * I'm not sure that this is a good way to model disassembly!
 * @author patrik
 */
public class Operation implements IVariable {

    public static final String NO_RESOURCE_BOOKING = "no_res";
    public static final String EXTRA_ACTIONS = "extra_actions";
    public static final String EXTRA_GUARDS = "extra_guards";
    public static final String PRODUCT_TYPE = "pt";
    public static final String UPPER_LIMIT_GIVEN = "up";
    private String mLabel;
    /**
     * By defalut add a cluase without any literals. This simplifies algorithms.
     */
    public List<Clause> mPreOperationDNFClauseList;
    /**
     * The literals in this clause.<br/>
     * Variable: {@link Resource}<br/>
     * Value: instances needed
     */
    public Clause mResourceConjunction;
    /**
     * To handle disassembly situations
     */
    public Set<Operation> mSubOperationSet;
    private Map<String, Object> mAttributeMap;

    public Operation(String mLabel) {
        this.mLabel = mLabel;

        mPreOperationDNFClauseList = new ArrayList<Clause>();
        mSubOperationSet = new HashSet<Operation>();
        mAttributeMap = new HashMap<String, Object>();
    }

    public String getLabel() {
        return mLabel;
    }

    @Override
    public String getVarInitValue() {
        return "0";
    }

    @Override
    public String getVarLabel() {
        return "Vop_" + mLabel;
    }

    public String getUcVarLabel() {
        return getVarLabel() + "_uc";
    }

    @Override
    public String getVarLowerBound() {
        return "0";
    }

    @Override
    public Set<String> getVarMarkedValues() {
        final Set<String> returnSet = new HashSet<String>();
        returnSet.add("0");
        return returnSet;
    }

    /**
     * Upper bound is given as min(for all resources in mResourceConjuction.capacity)
     * @return
     */
    @Override
    public String getVarUpperBound() {
        if (mResourceConjunction == null) {
            return "1";
        } else if (mAttributeMap.containsKey(UPPER_LIMIT_GIVEN)) {
            return (String) mAttributeMap.get(UPPER_LIMIT_GIVEN);
        } else {
            Integer minValue = Integer.MAX_VALUE;
            for (final ILiteral literal : mResourceConjunction.getLiteralList()) {
                final Resource resource = (Resource) literal.getVariable();
                final Integer instancesNeeded = (Integer) literal.getValue();
                final Integer resourceUpperBound = Integer.valueOf(resource.getVarUpperBound());
                final Integer upperBound = resourceUpperBound/instancesNeeded;
                if (upperBound < minValue) {
                    minValue = upperBound;
                }
            }
            return Integer.toString(minValue);
        }
    }

    @Override
    public String toString() {
        return mLabel;
    }

    public String getEventLabel() {
        return "e_" + mLabel;
    }

    /**
     * To return the right operation(s) from this operation.<br/>
     * @return Child operations are returned if such exist else the operation itself
     */
    public Set<Operation> getOperationSet() {
        if (mSubOperationSet.isEmpty()) {
            final Set<Operation> set = new HashSet<Operation>();
            set.add(this);
            return set;
        } else {
            return mSubOperationSet;
        }
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
}
