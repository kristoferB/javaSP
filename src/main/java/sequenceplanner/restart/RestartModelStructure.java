package sequenceplanner.restart;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import sequenceplanner.IO.EFA.ModuleBase;
import sequenceplanner.IO.EFA.MultiFlowerSingleTransitionModule;
import sequenceplanner.IO.EFA.Transition;
import sequenceplanner.IO.EFA.VariableBasic;

/**
 *
 * @author patrik
 */
public class RestartModelStructure {

    private final Map<String, ModuleBase> mMBMap;
    private final Map<PlacementSifting, Boolean> mSiftMap;
    private final Set<IRestartOperation> mOperationSet;
    private final Set<IRestartResource> mResourceSet;
    private final Set<String> mForbiddenStateExpressionSet;

    public static enum Flower {

        OPERATIONS("Operations"), RESET("Reset"), FORBIDDEN("Forbidden");
        private final String flower;

        Flower(String iName) {
            flower = iName;
        }

        @Override
        public String toString() {
            return flower;
        }
    };

    public static enum PlacementSifting {

        TO_ONLY_AFFECT_RESOURCES_IN_ERROR_OPERATION("Only affect resources in error operation (Kicki)"),
        TO_ONLY_ENABLE_PLACEMENT_IN_HOME_STATES_FOR_ALL_RESOURCES("Only placement in home states for all resources"),
        EXCLUDE_TRANSITION_IF_POWERSETELEMENT_CONTAINS_UNWANTED_OPERATIONS("Unwanted placement transitions have been excluded");
        private final String mComment;

        PlacementSifting(final String iComment) {
            mComment = iComment;
        }

        @Override
        public String toString() {
            return mComment;
        }
    };

    public RestartModelStructure() {
        mMBMap = new HashMap<String, ModuleBase>();
        initModuleBases();

        mSiftMap = new HashMap<PlacementSifting, Boolean>();
        initSiftMap();

        mOperationSet = new HashSet<IRestartOperation>();
        mResourceSet = new HashSet<IRestartResource>();

        mForbiddenStateExpressionSet = new HashSet<String>();
    }

    private void initModuleBases() {
        mMBMap.put(Flower.OPERATIONS.toString(), new ModuleBase());
        mMBMap.put(Flower.RESET.toString(), new ModuleBase());
        mMBMap.put(Flower.FORBIDDEN.toString(), new ModuleBase());
    }

    private void initSiftMap() {
        mSiftMap.put(PlacementSifting.TO_ONLY_AFFECT_RESOURCES_IN_ERROR_OPERATION, false);
        mSiftMap.put(PlacementSifting.TO_ONLY_ENABLE_PLACEMENT_IN_HOME_STATES_FOR_ALL_RESOURCES, false);
        mSiftMap.put(PlacementSifting.EXCLUDE_TRANSITION_IF_POWERSETELEMENT_CONTAINS_UNWANTED_OPERATIONS, false);
    }

    public void generateWmodFile(final String iFileName, final String iFilePath) {

        createOperationFlower();
        createResetFlower();
        createForbiddenFlower();

        final MultiFlowerSingleTransitionModule mfModule = new MultiFlowerSingleTransitionModule(iFileName, writeComment(iFilePath), mMBMap);
        mfModule.saveToWMODFile(iFilePath);
    }

    private String writeComment(final String iFilePath) {
        String comment = "";

        //Operations
        comment += "Operations: " + mOperationSet + ", (f)=has to finish, *=extra guards and/or actions added by user" + "\n";

        //Resources
        comment += "Resources: " + mResourceSet + "\n";

        comment += "\n";

        //Forbidden state combinations
        comment += "Forbidden state combinations: " + mForbiddenStateExpressionSet + "\n";

        comment += "\n";

        //Placement sifting
        for (final PlacementSifting ps : mSiftMap.keySet()) {
            if (mSiftMap.get(ps)) {
                comment += ps.toString() + "\n";
            }
        }

        comment += "\n";

        comment += "File path: " + iFilePath;

        return comment;
    }

    public Map<PlacementSifting, Boolean> getmSiftMap() {
        return mSiftMap;
    }

    public void addOperation(final IRestartOperation iOp) {
        mOperationSet.add(iOp);
    }

    public void addResource(final IRestartResource iResource) {
        mResourceSet.add(iResource);
    }

    /**
     * Requires that operation set has been filled
     * @param iResource
     * @param iOperations operations to add as a string "op1,op2,..."
     * @return false if problem else true
     */
    public boolean addBranch(final String iResource, final String iOperations) {
        final IRestartResource resource = getResourceObjectFromName(iResource);
        if (resource == null) {
            return false;
        }

        final Set<IRestartOperation> branch = new HashSet<IRestartOperation>();
        for (final String opName : iOperations.split(",")) {
            final IRestartOperation op = getOperationObjectFromName(opName);
            if (op == null) {
                return false;
            }
            branch.add(op);
        }
        resource.addBranch(branch);
        return true;
    }

    /**
     *
     * @param iOpName
     * @return null if op not found in operation set
     */
    private IRestartOperation getOperationObjectFromName(final String iOpName) {
        for (final IRestartOperation op : mOperationSet) {
            if (op.getName().equals(iOpName)) {
                return op;
            }
        }
        return null;
    }

    /**
     *
     * @param iName
     * @return null if op not found in operation set
     */
    private IRestartResource getResourceObjectFromName(final String iName) {
        for (final IRestartResource resource : mResourceSet) {
            if (resource.getName().equals(iName)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * To exclude placement transitions from being created.<br>
     * No check if the exclusionsets contradict each other!<br>
     * Let \sigma_{o,O'}^{place} and o'' \in O''<br>
     * if iProp == CONTAINS<br>
     * Then no placement transitions will be created where O'' \subseteq  O' || O''' \subseteq  O'...<br>
     * if iProp == NOT_CONTAINS<br>
     * Then no placement transitions will be created where O'' \not subseteq O' && O''' \not \subseteq O'...<br>
     * I.e. at least one of O''|O''' needs to be a subset of O'.<br>
     * if iProp == NOT_ONLY_CONTAINS<br>
     * Only the placement transitions where O'==O'' || O'==O''' || ... will be created.<br>
     * @param iOpName o
     * @param iProp
     * @param iOperationsToExcludeInDNF O''|O'''|...
     * @return true if add was ok else false
     */
    public boolean addExcludeSet(final String iOpName, final IRestartOperation.Property iProp, final String iOperationsToExcludeInDNF) {
        final IRestartOperation masterOp = getOperationObjectFromName(iOpName);
        if (masterOp == null) {
            System.err.println(iOpName + " not among operations!");
            return false;
        }

        for (final String clause : iOperationsToExcludeInDNF.split("\\|")) {
            final Set<IRestartOperation> excludeSet = new HashSet<IRestartOperation>();
            for (final String opToExcludeName : clause.split(",")) {
                final IRestartOperation slaveOp = getOperationObjectFromName(opToExcludeName);
                if (slaveOp == null || slaveOp.equals(masterOp)) {
                    System.err.println(slaveOp + " not among operations or that same as: " + iOpName);
                    return false;
                }
                excludeSet.add(slaveOp);
            }
            masterOp.getExcludeMap().get(iProp).add(excludeSet);
        }

        return true;
    }

    /**
     *
     * @param iEx expression to add
     * @return false if problem, else true
     */
    public boolean addForbiddenStatesCombination(final String iEx) {
        mForbiddenStateExpressionSet.add(iEx);
        return true;
    }

    private void createOperationFlower() {
        for (final IRestartOperation intefaceOp : mOperationSet) {
            final String op = intefaceOp.getName();

            //add transitions
            final Transition transStart = mMBMap.get(Flower.OPERATIONS.toString()).createControllableTransition("s" + op);
            transStart.andGuard(op + "==0");
            if (!intefaceOp.getExtraStartGuard().isEmpty()) {
                transStart.andGuard(intefaceOp.getExtraStartGuard());
            }
            transStart.andAction(op + "=1");
            if (!intefaceOp.getExtraStartAction().isEmpty()) {
                transStart.andAction(intefaceOp.getExtraStartAction());
            }
            final Transition transFinish = mMBMap.get(Flower.OPERATIONS.toString()).createControllableTransition("f" + op);
            transFinish.andGuard(op + "==1");
            if (!intefaceOp.getExtraFinishGuard().isEmpty()) {
                transFinish.andGuard(intefaceOp.getExtraFinishGuard());
            }
            transFinish.andAction(op + "=2");
            if (!intefaceOp.getExtraFinishAction().isEmpty()) {
                transFinish.andAction(intefaceOp.getExtraFinishAction());
            }

            //add variables
            String hasToFinish = null;
            if (intefaceOp.hasToFinish()) {
                hasToFinish = "2";
            }
            mMBMap.get(Flower.OPERATIONS.toString()).storeVariable(new VariableBasic(op, "0", "2", "0", hasToFinish));
        }
    }

    private static boolean excludeTransitionIfPowerSetElementContainsUnwantedOperations(final IRestartOperation iInterfaceOp, final Set<IRestartOperation> iPowerSetElement) {
        //Contains
        for (final Set<IRestartOperation> set : iInterfaceOp.getExcludeMap().get(IRestartOperation.Property.CONTAINS)) {
            if (iPowerSetElement.containsAll(set)) {
                return true;
            }
        }
        
        //Not contains
        boolean returnBoolean = true; //Assume that transition should be excluded
        for (final Set<IRestartOperation> set : iInterfaceOp.getExcludeMap().get(IRestartOperation.Property.NOT_CONTAINS)) {
            if (iPowerSetElement.containsAll(set)) {
                returnBoolean = false; //One clause is ok -> transition should not be excluded
            }
        }
        if (returnBoolean && !iInterfaceOp.getExcludeMap().get(IRestartOperation.Property.NOT_CONTAINS).isEmpty()) {
            return true;
        }

        //Not only contains
        returnBoolean = true; //Assume that transition should be excluded
        for (final Set<IRestartOperation> set : iInterfaceOp.getExcludeMap().get(IRestartOperation.Property.NOT_ONLY_CONTAINS)) {
            if (iPowerSetElement.containsAll(set) && set.containsAll(iPowerSetElement)) {
                returnBoolean = false; //One clause is ok -> transition should not be excluded
            }
        }
        if (returnBoolean && !iInterfaceOp.getExcludeMap().get(IRestartOperation.Property.NOT_ONLY_CONTAINS).isEmpty()) {
            return true;
        }

        return false;
    }

    private boolean toOnlyAffectResourcesInErrorOperation(final IRestartOperation iInterfaceOp, final Set<IRestartOperation> iPowerSetElement) {
        for (final IRestartOperation elem : iPowerSetElement) {
            final Set<IRestartResource> errorOpResourceSet = getResourceSetForOperation(iInterfaceOp);
            final Set<IRestartResource> elemOpResourceSet = getResourceSetForOperation(elem);
            if (!errorOpResourceSet.containsAll(elemOpResourceSet)) {
                return false;
            }
        }
        return true;
    }

    private Set<IRestartResource> getResourceSetForOperation(final IRestartOperation iInterfaceOp) {
        final Set<IRestartResource> returnSet = new HashSet<IRestartResource>();
        for (final IRestartResource resource : mResourceSet) {
            if (doesResourceBranchsContainOperation(resource.getBranchSet(), iInterfaceOp)) {
                returnSet.add(resource);
            }
        }
        return returnSet;
    }

    private static boolean doesResourceBranchsContainOperation(final Set<Set<IRestartOperation>> iResourceBranchSet, final IRestartOperation iInterfaceOp) {
        for (final Set<IRestartOperation> branch : iResourceBranchSet) {
            if (branch.contains(iInterfaceOp)) {
                return true;
            }
        }
        return false;
    }

    private void toOnlyEnablePlacementInHomeStatesForAllResources(final IRestartOperation iInterfaceOp, final Set<IRestartOperation> iPowerSetElement, final Transition ioTrans) {
        //Get all affected resources
        final Set<IRestartResource> resourcesToBeInHomeStatesSet = new HashSet<IRestartResource>(getResourceSetForOperation(iInterfaceOp));
        for (final IRestartOperation elem : iPowerSetElement) {
            resourcesToBeInHomeStatesSet.addAll(getResourceSetForOperation(elem));
        }

        //Get all branches for all affected resources
        final Set<Set<IRestartOperation>> branchesSet = new HashSet<Set<IRestartOperation>>();
        for (final IRestartResource resource : resourcesToBeInHomeStatesSet) {
            branchesSet.addAll(resource.getBranchSet());
        }

        //Loop branches
        for (final Set<IRestartOperation> opsInBranch : branchesSet) {
            final Set<IRestartOperation> opsInBranchCopy = new HashSet<IRestartOperation>(opsInBranch);
            opsInBranchCopy.removeAll(iPowerSetElement);
            opsInBranchCopy.remove(iInterfaceOp);

            //At least one operation from set (O' Union {o}) in branch
            if (opsInBranchCopy.size() < opsInBranch.size()) {
                for (final IRestartOperation opToRestrict : opsInBranchCopy) {
                    ioTrans.andGuard(opToRestrict.getName() + "==0");
                }
            }//No op from set (O' Union {o}) in branch
            //if the given mutex partition corresponds to resoruce allocation -> the operations in the other mutexes are in initial or finish state -> No need to include extra guards to capture this.
            else {
                String zeroString = "(";
                String twoString = "(";
                for (final IRestartOperation opToRestrict : opsInBranch) {
                    zeroString += opToRestrict.getName() + "==0 &";
                    twoString += opToRestrict.getName() + "==2 &";
                }
                ioTrans.andGuard("(" + zeroString + "1)" + "|" + twoString + "1)" + ")"); //"1)" Just because string ends with &
            }
        }
    }

    private boolean siftTransition(final IRestartOperation iInterfaceOp, final Set<IRestartOperation> iPowerSetElement, final Transition ioTrans) {
        //To only affect resources in error operation, The Kicki way!
        if (mSiftMap.get(PlacementSifting.TO_ONLY_AFFECT_RESOURCES_IN_ERROR_OPERATION)) {
            if (!toOnlyAffectResourcesInErrorOperation(iInterfaceOp, iPowerSetElement)) {
                return false;
            }
        }

        //To only enable placement in home states for all resources
        //For resources used in error operation
        //And for Resources used in other operations that are to be reseted
        if (mSiftMap.get(PlacementSifting.TO_ONLY_ENABLE_PLACEMENT_IN_HOME_STATES_FOR_ALL_RESOURCES)) {
            toOnlyEnablePlacementInHomeStatesForAllResources(iInterfaceOp, iPowerSetElement, ioTrans);
        }

        if (mSiftMap.get(PlacementSifting.EXCLUDE_TRANSITION_IF_POWERSETELEMENT_CONTAINS_UNWANTED_OPERATIONS)) {
            if (excludeTransitionIfPowerSetElementContainsUnwantedOperations(iInterfaceOp, iPowerSetElement)) {
                return false;
            }
        }

        return true;
    }

    private void createResetFlower() {
        for (final IRestartOperation interfaceOp : mOperationSet) {
            final String op = interfaceOp.getName();
            final Set<IRestartOperation> subSet = new HashSet<IRestartOperation>(mOperationSet);
            subSet.remove(interfaceOp);

            for (final Set<IRestartOperation> powerSetElement : powerSet(subSet)) {
                final Transition trans = new Transition("p" + op + "_" + collectionToString(powerSetElement));
                trans.setAttribute(Transition.UNCONTROLLABLE, false);

                //Should transition be included, or should extra guards be added?
                if (siftTransition(interfaceOp, powerSetElement, trans)) {

                    //guards-----------------------------------------------------
                    trans.andGuard(op + "==1");
                    for (final IRestartOperation elem : powerSetElement) {
                        trans.andGuard(elem.getName() + "!=0");
                    }

                    //actions----------------------------------------------------
                    trans.andAction(op + "=0");
                    for (final IRestartOperation elem : powerSetElement) {
                        trans.andAction(elem.getName() + "=0");
                    }

                    mMBMap.get(Flower.RESET.toString()).addTransition(trans);
                }
            }

        }

    }

    private void createForbiddenFlower() {
        mMBMap.get(Flower.FORBIDDEN.toString()).storeVariable(new VariableBasic("Xstate", "0", "1", "0", "0"));
        final Transition transForbidden = mMBMap.get(Flower.FORBIDDEN.toString()).createTransition("uc", true);

        //Add forbidden states combinations for resources allocation
        forbiddenStatesCombinationsForResourceAllocation();

        transForbidden.andGuard("Xstate" + "==0");
        if (mForbiddenStateExpressionSet.isEmpty()) {
            transForbidden.andGuard("(1)");
        } else {
            String exp = "(";
            for (final String expression : mForbiddenStateExpressionSet) {
                if (exp.length() > 1) {
                    exp += "|";
                }
                exp += "(";
                exp += expression;
                exp += ")";
            }
            exp += ")";
            transForbidden.andGuard(exp);
        }

        transForbidden.andAction("Xstate" + "=1");
    }

    private void forbiddenStatesCombinationsForResourceAllocation() {
        for (final IRestartResource resource : mResourceSet) {
            forbiddenStatesCombinationsForResourceAllocationHelpMethod(resource.getBranchSet());
        }
    }

    /**
     * Translates a set of branches to forbidden states combinations.
     * @param iBranchSet
     */
    private void forbiddenStatesCombinationsForResourceAllocationHelpMethod(final Set<Set<IRestartOperation>> iBranchSet) {
        //There need to be at least 2 branches for resource!
        if (iBranchSet.size() < 2) {
            return;
        }

        final Iterator<Set<IRestartOperation>> branchSetIt = iBranchSet.iterator();
        final Set<IRestartOperation> oneBranch = branchSetIt.next();

        final String oneBranchExpression = forbiddenStateExpression(oneBranch);

        while (branchSetIt.hasNext()) {
            addForbiddenStatesCombination(oneBranchExpression + "&" + forbiddenStateExpression(branchSetIt.next()));
        }

        final Set<Set<IRestartOperation>> remainingBranchSet = new HashSet<Set<IRestartOperation>>(iBranchSet);
        remainingBranchSet.remove(oneBranch);
        forbiddenStatesCombinationsForResourceAllocationHelpMethod(remainingBranchSet);
    }

    private static String forbiddenStateExpression(final Set<IRestartOperation> iOneBranch) {
        final Iterator<IRestartOperation> oneOpInOneBranchIt = iOneBranch.iterator();
        final IRestartOperation oneOp = oneOpInOneBranchIt.next();

        String disjuctionForBranch = "";
        if (iOneBranch.size() > 1) {
            disjuctionForBranch += "(";
        }
        disjuctionForBranch += oneOp.getName() + "==1";
        while (oneOpInOneBranchIt.hasNext()) {
            disjuctionForBranch += "|" + oneOp.getName() + "!=" + oneOpInOneBranchIt.next().getName();
        }
        if (iOneBranch.size() > 1) {
            disjuctionForBranch += ")";
        }
        return disjuctionForBranch;
    }

    private static String collectionToString(final Collection<IRestartOperation> iCollection) {
        String returnString = "";
        for (final IRestartOperation elem : iCollection) {
            returnString += elem.getName();
        }
        return returnString;
    }

    private static Set<Set<IRestartOperation>> powerSet(final Set<IRestartOperation> iSet) {
        Set<Set<IRestartOperation>> ps = new HashSet<Set<IRestartOperation>>();
        ps.add(new HashSet<IRestartOperation>());   // add the empty set

        // for every item in the original Set
        for (IRestartOperation item : iSet) {
            Set<Set<IRestartOperation>> newPs = new HashSet<Set<IRestartOperation>>();

            for (Set<IRestartOperation> subset : ps) {
                // copy all of the current powerSet's subsets
                newPs.add(subset);

                // plus the subsets appended with the current item
                Set<IRestartOperation> newSubset = new HashSet<IRestartOperation>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            // powerSet is now powerSet of list.subList(0, list.indexOf(item)+1)
            ps = newPs;
        }

        return ps;
    }
}
