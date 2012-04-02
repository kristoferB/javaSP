package sequenceplanner.multiproduct.RAS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.IO.EFA.AModule;
import sequenceplanner.IO.EFA.ModuleBase;
import sequenceplanner.IO.EFA.Transition;
import sequenceplanner.algorithm.AAlgorithm;
import sequenceplanner.expression.Clause;
import sequenceplanner.expression.ILiteral;

/**
 * This is where the magic happens!<br/>
 * Basic outline: implementation of a translation from operations to EFA models to model Resource Allocation Systems<br/>
 * From an internal data structure({@link Operation}, {@link Resource}, and {@link Variable}) to a {@link ModuleBase} object.<br/>
 * The {@link ModuleBase} object can later on be feed to a {@link AModule} implementaion.<br/>
 * Each operaiton is modeled as a counter.<br/>
 * Each resource is modeled as a counter.<br/>
 * Allocation of next resource (the same as advance in operation) is modeled as a {@link Transition} object.<br/>
 * The transition gets a guard to check that the counter for previous operations is != 0
 * and a guard to check that \exists resources for this operation.<br/>
 * The resources to be used in this operation are booked with an action and
 * the resources used in the previous operation are unbooked with an action.<br/>
 * The previous operaiton counter is decreased with one and the counter for this operation is increased with one.<br/>
 * @author patrik
 */
public class CreateTransitionsAndVariables extends AAlgorithm {

    private Set<Operation> mOperationSet = null;
    private Set<Variable> mVariableSet = null;
    private String mProductTypeValue = null;
    private ModuleBase mModuleBase = null;
    /**
     * Storage for two things:<br/>
     * 1) What resource variables that are used.<br/>
     * 2) Guards and actions that are attached to transitions and has to do with resource booking/unbooking.
     */
    private ModuleBase mModuleBaseResourceInfo = null;

    public CreateTransitionsAndVariables(String iThreadName) {
        super(iThreadName);
    }

    @Override
    public void init(List<Object> iList) {
        mOperationSet = (Set<Operation>) iList.get(0);
        mVariableSet = (Set<Variable>) iList.get(1);
        if (iList.size() > 2) {
            mProductTypeValue = (String) iList.get(2);
        }
        mModuleBase = new ModuleBase();
        mModuleBaseResourceInfo = new ModuleBase();
    }

    @Override
    public void run() {

        if(!getStatus("Store Variables...")) {
            return;
        }
        createVariables();

        if(!getStatus("Create Transitions...")) {
            return;
        }
        createTransitions();

        final List<Object> returnList = new ArrayList<Object>();
        returnList.add(mModuleBase);
        returnList.add(mModuleBaseResourceInfo);
        returnList.add(mProductTypeValue);
        fireFinishedEvent(returnList);
    }

    private void createVariables() {

        //Operations...
        for (final Operation op : mOperationSet) {

            for (final Operation op1 : op.getOperationSet()) {
                if (op1.getAttribute(Operation.NO_RESOURCE_BOOKING) == null) {
                    mModuleBase.storeVariable(op1);
                }
                if (op1.getAttribute(Transition.UNCONTROLLABLE) != null) {
                    mModuleBase.storeVariable(new Operation(op1.getLabel() + "_" + Transition.UNCONTROLLABLE));
                }
                //... and resources
                subMethodCreateVariables(op1);
            }

        }

        //"Extra" variables
        for (final Variable var : mVariableSet) {
            mModuleBase.storeVariable(var);
        }
    }

    private void subMethodCreateVariables(final Operation iOp) {
        for (final ILiteral literal : iOp.mResourceConjunction.getLiteralList()) {
            final Resource resource = (Resource) literal.getVariable();
            mModuleBase.storeVariable(resource);
            mModuleBaseResourceInfo.storeVariable(resource);
        }
    }

    /**
     * One event/transition per clause in DNF of preoperations.<br/>
     * A "NO_RESOURCE_BOOKING" is treated in a special way.<br/>
     */
    private void createTransitions() {
        for (final Operation op : mOperationSet) {

            for (final Clause clause : op.mPreOperationDNFClauseList) {
                final String eventLabel = op.getEventLabel() + "_" + op.mPreOperationDNFClauseList.indexOf(clause);

                final Transition trans = mModuleBase.createControllableTransition(eventLabel);

                //Go to execution/finish state
                for (final Operation op1 : op.getOperationSet()) {
                    if (op1.getAttribute(Operation.NO_RESOURCE_BOOKING) == null) {
                        final String action = op1.getVarLabel() + "=" + op1.getVarLabel() + "+1";
                        trans.andAction(action);
                    }
                }

                //Check that predecessor operations are active
                //This can either be the controllable transition or the extra uncontrollable transition
                Transition firstTrans;
                if (op.getAttribute(Transition.UNCONTROLLABLE) == null) {
                    firstTrans = trans;
                } else { //Add an extra uncontrollable transition before the real transitions
                    firstTrans = mModuleBase.createTransition(Transition.UNCONTROLLABLE + "_" + eventLabel, true);
                    final String action1 = op.getUcVarLabel() + "=" + op.getUcVarLabel() + "+1";
                    firstTrans.andAction(action1);
                    //The "original" transition can occur after the uncontrollable "first"-transition
                    final String guard2 = op.getUcVarLabel() + ">" + "0";
                    trans.andGuard(guard2);
                    final String action2 = op.getUcVarLabel() + "=" + op.getUcVarLabel() + "-1";
                    trans.andAction(action2);
                }

                //Add guards and actions to check that predecessor operations are active
                //firstTrans can either be the normal controllable trans or the uncontrollable trans
                for (final ILiteral literal : clause.getLiteralList()) {
                    final Operation preOp = (Operation) literal.getVariable();
                    final String guard = preOp.getVarLabel() + ">" + "0";
                    firstTrans.andGuard(guard);
                    final String action = preOp.getVarLabel() + "=" + preOp.getVarLabel() + "-1";
                    firstTrans.andAction(action);
                }

                //Create booking-unbooking map
                final Set<Operation> beforeOpSet = new HashSet<Operation>();
                for (final ILiteral literal : clause.getLiteralList()) {
                    final Operation preOp = (Operation) literal.getVariable();
                    beforeOpSet.add(preOp);
                }
                final Map<Resource, List<Integer>> bookUnbookMap = createBookUnbookMap(beforeOpSet, op.getOperationSet());

                //To store resource guards and actions, used in preprocessing step
                final Transition resourceTrans = mModuleBaseResourceInfo.createControllableTransition(eventLabel);
                if (op.getAttribute(Transition.UNCONTROLLABLE) != null) {
                    mModuleBaseResourceInfo.createTransition(Transition.UNCONTROLLABLE + "_" + eventLabel, true);
                }

                //Book and unbook resources
                for (final Resource resource : bookUnbookMap.keySet()) {
                    final List<Integer> valueList = bookUnbookMap.get(resource);
                    if (valueList.get(0) > 0) {
                        final String guard = resource.getVarLabel() + ">=" + valueList.get(0);
                        trans.andGuard(guard);
                        resourceTrans.andGuard(guard);
                    }
                    final Integer actionValue = valueList.get(1) * (-1);
                    String action = resource.getVarLabel() + "=" + resource.getVarLabel();
                    if (actionValue != 0) {
                        if (actionValue > 0) {
                            action += "+";
                        }
                        action += actionValue;
                        trans.andAction(action);
                        resourceTrans.andAction(action);
                    }
                }

                //Extra guards and actions
                extraGuardsActions(op, Operation.EXTRA_GUARDS, trans);
                extraGuardsActions(op, Operation.EXTRA_ACTIONS, trans);

                //Add avoidance if wrong product type
                //Avoidance == transition is always false
                final Object value = op.getAttribute(Operation.PRODUCT_TYPE);
                if (value != null && mProductTypeValue != null) {
                    if (!((String) value).equals(mProductTypeValue)) {
                        trans.andGuard("0");
                        firstTrans.andGuard("0");
                    }
                }
            }
        }
    }

    private static Map<Resource, List<Integer>> createBookUnbookMap(final Set<Operation> iBeforeOpSet, final Set<Operation> iAfterOpSet) {
        final Map<Resource, List<Integer>> map = new HashMap<Resource, List<Integer>>();

        //Unbook
        helpClassCreateBookUnbookMap(iBeforeOpSet, map, false);

        //Book
        helpClassCreateBookUnbookMap(iAfterOpSet, map, true);

        return map;
    }

    /**
     * Takes differnet action if operation is a "NO_RESOURCE_BOOKING" or not.<br/>
     * Should only check that resources can be booked, but not book them.<br/>
     * @param iOpSet
     * @param ioMap
     * @param iAdd
     */
    private static void helpClassCreateBookUnbookMap(final Set<Operation> iOpSet, final Map<Resource, List<Integer>> ioMap, final boolean iAdd) {
        for (final Operation op : iOpSet) {
            for (final ILiteral literal : op.mResourceConjunction.getLiteralList()) {
                final Resource resource = (Resource) literal.getVariable();
                final Integer value = (Integer) literal.getValue();

                Integer guardValue = 0;
                Integer actionValue = 0;
                if (ioMap.containsKey(resource)) {
                    guardValue = ioMap.get(resource).get(0);
                    actionValue = ioMap.get(resource).get(1);
                } else {
                    ioMap.put(resource, new ArrayList<Integer>(2));
                }
                final List<Integer> list = ioMap.get(resource);
                if (iAdd) {
                    guardValue = guardValue + value;
                    if (op.getAttribute(Operation.NO_RESOURCE_BOOKING) == null) {
                        actionValue = actionValue + value;
                    }
                } else {
                    guardValue = guardValue - value;
                    actionValue = actionValue - value;
                }
                list.clear();
                list.add(guardValue);
                list.add(actionValue);
                ioMap.put(resource, list);
            }
        }
    }

    private void extraGuardsActions(final Operation iOp, final String iKey, final Transition ioTrans) {
        for (final Operation op1 : iOp.getOperationSet()) {
            if (op1.getAttribute(iKey) != null) {
                final String[] array = ((String) op1.getAttribute(iKey)).split(";");
                for (final String obj : array) {
                    if (iKey.equals(Operation.EXTRA_GUARDS)) {
                        ioTrans.andGuard(obj);
                    } else if (iKey.equals(Operation.EXTRA_ACTIONS)) {
                        ioTrans.andAction(obj);
                    }
                }
            }
        }
    }

    class IntPair {

        Integer mInt1;
        Integer mInt2;

        public IntPair(Integer mInt1, Integer mInt2) {
            this.mInt1 = mInt1;
            this.mInt2 = mInt2;
        }
    }
}
