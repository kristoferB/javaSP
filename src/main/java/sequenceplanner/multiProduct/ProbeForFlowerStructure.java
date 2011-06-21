package sequenceplanner.multiProduct;

import java.util.ArrayList;
import sequenceplanner.IO.WriteToPSOP;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.efaconverter.SEFA;
import sequenceplanner.efaconverter.SEGA;
import sequenceplanner.efaconverter.SModule;
import sequenceplanner.multiProduct.OperationResourceDataStructure.Operation;

/**
 *
 * @author patrik
 */
public class ProbeForFlowerStructure {

    private Set<Variable> mVariableSet = new HashSet<Variable>();
    private Set<SEGA> mSEGASet = new HashSet<SEGA>();
    private Map<SModule, Set<SEFA>> mSModuleSEFASetMap = new HashMap<SModule, Set<SEFA>>();
    private Map<OperationResourceDataStructure.Operation, List<SEGA>> mOperationTransitionMap = new HashMap<Operation, List<SEGA>>();

    public ProbeForFlowerStructure() {
    }

    /**
     * Creates a module based on the variables that set values in actions.<br/>
     * -------------------------------------------------<br/>
     * E.g.: action var1=1;var2=2;var3=0<br/>
     * Transition is added both to EFA for var1 and var2<br/>
     * -------------------------------------------------<br/>
     * The result is saved to wmod-file.<br/>
     * @param iWMODFilePath path to file
     * @return true if method ok else false
     */
    public boolean createModuleType1(final String iWMODFilePath) {
        final SModule smodule = new SModule("ProbeSModule");
        smodule.setComment("Creates a module based on the variables that set values in actions.\n" +
                "E.g.: action var1=1;var2=2;var3=0\n" + "Transition is added both to EFA for var1 and var2");
        mSModuleSEFASetMap.put(smodule, new HashSet<SEFA>());

        Set<SEGA> transitionSet = new HashSet<SEGA>(mSEGASet);

        for (final SEGA sega : mSEGASet) {
            final String[] conditionSplit = sega.getAction().split(";");
            for (final String action : conditionSplit) {
                final String[] actionSplit = action.split("=");
                final String variable = actionSplit[0];
                final String value = actionSplit[1];
                if (!value.equals("0")) {
                    SEFA sefa = getSEFA(variable, smodule);
                    addSelfLoop(sefa, sega, transitionSet);
                }
            }
        }

        if (!transitionSet.isEmpty()) {
            return false;
        }

        addVariableSetToModule(smodule);

        return smodule.saveToWMODFile(iWMODFilePath);
    }

    /**
     * Creates a module based on the first variable that set values in actions.<br/>
     * -------------------------------------------------<br/>
     * E.g.: action var1=1;var2=2;var3=0<br/>
     * Transition is added to EFA for var1 or var2<br/>
     * -------------------------------------------------<br/>
     * The result is saved to wmod-file.<br/>
     * @param iWMODFilePath path to file
     * @return true if method ok else false
     */
    public boolean createModuleType2(final String iWMODFilePath) {
        final SModule smodule = new SModule("ProbeSModule");
        smodule.setComment("Creates a module based on the variables that set values in actions.\n" +
                "E.g.: action var1=1;var2=2;var3=0\n" + "Transition is added to EFA for var1 or var2\n");
        mSModuleSEFASetMap.put(smodule, new HashSet<SEFA>());

        Set<SEGA> transitionSet = new HashSet<SEGA>(mSEGASet);

        for (final SEGA sega : mSEGASet) {
            final String[] conditionSplit = sega.getAction().replaceAll(" ", "").split(";");
            boolean transitionIsAdded = false;
            for (final String action : conditionSplit) {
                final String[] actionSplit = action.split("=");
                final String variable = actionSplit[0];
                final String value = actionSplit[1];
                if (!value.equals("0") && !transitionIsAdded) {
                    SEFA sefa = getSEFA(variable, smodule);
                    addSelfLoop(sefa, sega, transitionSet);
                    transitionIsAdded = true;
                }
            }
        }

        if (!transitionSet.isEmpty()) {
            return false;
        }

        addVariableSetToModule(smodule);

        return smodule.saveToWMODFile(iWMODFilePath);
    }

    /**
     * Creates a module where each operation gets it's own EFA.<br/>
     * The result is saved to wmod-file.<br/>
     * @param iWMODFilePath path to file
     * @return true if method ok else false
     */
    public boolean createModuleType3(final String iWMODFilePath) {
        final SModule smodule = new SModule("ProbeSModule");
        smodule.setComment("One EFA per operation");
        mSModuleSEFASetMap.put(smodule, new HashSet<SEFA>());

        Set<SEGA> transitionSet = new HashSet<SEGA>(mSEGASet);

        //Add to Wmod file
        for (final SEGA sega : mSEGASet) {
            final String[] conditionSplit = sega.getEvent().split("_");
            String opId = conditionSplit[1];

            if (conditionSplit.length > 2) {
                opId += "_" + conditionSplit[2];
            }

            SEFA sefa = getSEFA(opId, smodule);
            addSelfLoop(sefa, sega, transitionSet);
        }

        if (!transitionSet.isEmpty()) { //Check that all transitions has been added to a EFA
            return false;
        }

        addVariableSetToModule(smodule);

        return smodule.saveToWMODFile(iWMODFilePath);
    }

    /**
     * Creates a module where each variable gets it's own EFA.<br/>
     * The same event occurs in many EFAs.<br/>
     * @param iWMODFilePath path to file
     * @return true if method ok else false
     */
    public boolean createModuleType4(final String iWMODFilePath) {
        final SModule smodule = new SModule("ProbeSModule");
        smodule.setComment("One EFA per variable\n An event may occur in many EFAs");
        mSModuleSEFASetMap.put(smodule, new HashSet<SEFA>());

        Set<SEGA> transitionSet = new HashSet<SEGA>(mSEGASet);

        for (final SEGA sega : mSEGASet) {

            //Add guards to map based on variables-------------------------------
            final String[] guardSplit = sega.getGuard().replaceAll(" ", "").split("&");
            Map<String, Set<String>> guardMap = new HashMap<String, Set<String>>();
            for (final String guard : guardSplit) {
                final String[] localConditionSplit;
                if (guard.contains("!")) {
                    localConditionSplit = guard.split("!=");
                } else if (guard.contains("<")) {
                    localConditionSplit = guard.split("<");
                } else {
                    localConditionSplit = guard.split("==");
                }
                final String guardVariable = localConditionSplit[0];
                if (!guardMap.containsKey(guardVariable)) {
                    guardMap.put(guardVariable, new HashSet<String>());
                }
                guardMap.get(guardVariable).add(guard);
            }//------------------------------------------------------------------

            //Add actions to map based on variables------------------------------
            final String[] actionSplit = sega.getAction().replaceAll(" ", "").split(";");
            Map<String, String> actionMap = new HashMap<String, String>();
            for (final String action : actionSplit) {
                final String[] localConditionSplit;
                if (action.contains("\\+")) {
                    localConditionSplit = action.split("\\+=");
                } else if (action.contains("\\-")) {
                    localConditionSplit = action.split("\\-=");
                } else {
                    localConditionSplit = action.split("=");
                }
                final String actionVariable = localConditionSplit[0];
                actionMap.put(actionVariable, action);
            }//------------------------------------------------------------------

//            final Set<String> addedGuardVariableSet = new HashSet<String>();

            //Add transition to guard variable EFA for guard variables (action added for variable if such exists).
            for (final String variable : guardMap.keySet()) {
                //Get SEFA
                SEFA sefa = getSEFA(variable, smodule);
                SEGA newSega = new SEGA(sega.getEvent());

                //Add guards
                for (final String guard : guardMap.get(variable)) {
                    newSega.andGuard(guard);
                }

                //Add actions for variable if such exists
                if (actionMap.containsKey(variable)) {
                    newSega.addAction(actionMap.get(variable));
                }

                addSelfLoop(sefa, newSega, transitionSet);
            }//------------------------------------------------------------------

            //Add transition to action variable EFA where variable not in guard.
            actionMap.keySet().removeAll(guardMap.keySet()); //actions already added
            for (final String variable : actionMap.keySet()) {
                //Get SEFA
                SEFA sefa = getSEFA(variable, smodule);
                SEGA newSega = new SEGA(sega.getEvent());

                //Add actions for variable
                newSega.addAction(actionMap.get(variable));

                addSelfLoop(sefa, newSega, transitionSet);
            }//------------------------------------------------------------------
        }

//        if (!transitionSet.isEmpty()) { //Check that all transitions has been added to a EFA
//            System.out.println("Not all transitions have been added!");
//            return false;
//        }

        addVariableSetToModule(smodule);

        return smodule.saveToWMODFile(iWMODFilePath);
    }

    /**
     * Creates a module where each transition gets it's own EFA.<br/>
     * The result is saved to wmod-file.<br/>
     * @param iWMODFilePath path to file
     * @return true if method ok else false
     */
    public boolean createModuleType5(final String iWMODFilePath) {
        final SModule smodule = new SModule("ProbeSModule");
        smodule.setComment("One EFA per variable");
        mSModuleSEFASetMap.put(smodule, new HashSet<SEFA>());

        Set<SEGA> transitionSet = new HashSet<SEGA>(mSEGASet);

        for (final SEGA sega : mSEGASet) {
            SEFA sefa = getSEFA(sega.getEvent(), smodule);
            addSelfLoop(sefa, sega, transitionSet);
        }

        if (!transitionSet.isEmpty()) { //Check that all transitions has been added to a EFA
            return false;
        }

        addVariableSetToModule(smodule);

        return smodule.saveToWMODFile(iWMODFilePath);
    }

    /**
     * Translates {@link OperationResourceDataStructure.Operation}s and {@link SEGA} to PSOP sequence.<br/>
     * @param iTxtFilePath The PSOP sequence is given in text file stored according to this path
     * @return true if ok else false
     */
    public boolean createPSOPSequence(final String iTxtFilePath, final String iTitle,
            final int iPadForIF, final int iPadForPREACTION, final int iPadForPOSTACTION) {
        WriteToPSOP wtf = new WriteToPSOP("", iTxtFilePath);

        //Add to PSOP file
        wtf.addToSequence(mOperationTransitionMap, iTitle, iPadForIF, iPadForPREACTION, iPadForPOSTACTION);

        return wtf.writeToFile();
    }

    private void addSelfLoop(final SEFA sefa, final SEGA sega, final Set<SEGA> setToRemoveFrom) {
        sefa.addStandardSelfLoopTransition(sega);
        setToRemoveFrom.remove(sega);
    }

    private static String getEFAName(final String iName) {
        return "efa_" + iName;
    }

    private SEFA getSEFA(final String iSEFAName, final SModule iSModule) {
        final String sefaName = getEFAName(iSEFAName.replaceAll("\\+", "").replaceAll("\\-", ""));
        for (final SEFA sefa : mSModuleSEFASetMap.get(iSModule)) {
            if (sefa.getName().equals(sefaName)) {
                return sefa;
            }
        }
        SEFA sefa = new SEFA(sefaName, iSModule);
        sefa.addState("pm", true, true);
        mSModuleSEFASetMap.get(iSModule).add(sefa);
        return sefa;
    }

    public void addVariableSetToModule(final SModule iSModule) {
        for (final Variable var : mVariableSet) {
            iSModule.addIntVariable(var.getmName(), var.getmLowerBand(), var.getmUpperBound(), var.getInitialValue(), var.mMarking);
        }
    }

    public Set<SEGA> getmSEGASet() {
        return mSEGASet;
    }

    public void setmSEGASet(Set<SEGA> mSEGASet) {
        this.mSEGASet = mSEGASet;
    }

    public void addTomSEGASet(final SEGA iSEGA) {
        getmSEGASet().add(iSEGA);
    }

    public Map<Operation, List<SEGA>> getmOperationTransitionMap() {
        return mOperationTransitionMap;
    }

    public void addTomOperationTransitionMap(final Operation iKey, final SEGA iValue) {
        if (!getmOperationTransitionMap().containsKey(iKey)) {
            getmOperationTransitionMap().put(iKey, new ArrayList<SEGA>(2));
        }
        getmOperationTransitionMap().get(iKey).add(iValue);
    }

    public void storeOperationAndTransition(final OperationResourceDataStructure.Operation iOperation, final SEGA iSEGA) {
        addTomOperationTransitionMap(iOperation, iSEGA);
        addTomSEGASet(iSEGA);
    }

    public Set<Variable> getmVariableSet() {
        return mVariableSet;
    }

    public void addTomVariableSet(final String iName, final int iLowerBand, final int iUpperBand, final int initialValue, final Integer iMarking) {
        Variable var = new Variable();
        var.setmName(iName);
        var.setmLowerBand(iLowerBand);
        var.setmUpperBound(iUpperBand);
        var.setInitialValue(initialValue);
        var.setmMarking(iMarking);
        mVariableSet.add(var);
    }

    public void setmVariableSet(Set<Variable> mVariableSet) {
        this.mVariableSet = mVariableSet;
    }

    public Map<SModule, Set<SEFA>> getmSModuleSEFASetMap() {
        return mSModuleSEFASetMap;
    }

    public class Variable {

        private String mName = "";
        private int mLowerBand = 0;
        private int mUpperBound = 0;
        private int initialValue = 0;
        private Integer mMarking = null;

        public Variable() {
        }

        public int getInitialValue() {
            return initialValue;
        }

        public void setInitialValue(int initialValue) {
            this.initialValue = initialValue;
        }

        public int getmLowerBand() {
            return mLowerBand;
        }

        public void setmLowerBand(int mLowerBand) {
            this.mLowerBand = mLowerBand;
        }

        public Integer getmMarking() {
            return mMarking;
        }

        public void setmMarking(Integer mMarking) {
            this.mMarking = mMarking;
        }

        public String getmName() {
            return mName;
        }

        public void setmName(String mName) {
            this.mName = mName;
        }

        public int getmUpperBound() {
            return mUpperBound;
        }

        public void setmUpperBound(int mUpperBound) {
            this.mUpperBound = mUpperBound;
        }
    }
}
