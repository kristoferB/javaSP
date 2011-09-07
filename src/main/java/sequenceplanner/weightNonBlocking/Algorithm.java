package sequenceplanner.weightNonBlocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.waters.subject.module.EventDeclSubject;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.xsd.base.EventKind;
import org.supremica.automata.Arc;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import org.supremica.automata.BDD.EFA.BDDExtendedSynthesizer;
import org.supremica.automata.ExtendedAutomata;
import org.supremica.automata.ExtendedAutomaton;
import org.supremica.automata.State;
import org.supremica.automata.algorithms.AutomataSynthesizer;
import org.supremica.automata.algorithms.EFAMonlithicReachability;
import org.supremica.automata.algorithms.EditorSynthesizerOptions;
import org.supremica.automata.algorithms.Guard.BDDExtendedGuardGenerator;
import org.supremica.automata.algorithms.SynchronizationOptions;
import org.supremica.automata.algorithms.SynchronizationType;
import org.supremica.automata.algorithms.SynthesisAlgorithm;
import org.supremica.automata.algorithms.SynthesisType;
import org.supremica.automata.algorithms.SynthesizerOptions;
import sequenceplanner.IO.EFA.SEFA;
import sequenceplanner.IO.EFA.SEGA;
import sequenceplanner.IO.EFA.SModule;
import sequenceplanner.algorithm.AAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionStatement;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeAlternative;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;

/**
 * Calculates all sequences for how to execute a set of {@link Seam}s based on the
 * weights for the {@link Block}s in each seam. The weight to lift has to be less
 * than the payload for given resource.<br/>
 * DARPA<br/>
 * @author patrik
 */
public class Algorithm extends AAlgorithm {

    Set<Seam> mSeamSet;
    Set<Block> mBlockToLiftSet;
    Resource mResource;
    Map<Double, Integer> mWeightMap;
    SModule mModule;
    SEFA mEfa;
    ISopNode mRootSopNode;

    public Algorithm(IAlgorithmListener iListener) {
        super("weight nonBlocking");
        addAlgorithmListener(iListener);
        mSeamSet = new HashSet<Seam>();
        mBlockToLiftSet = new HashSet<Block>();
        mModule = new SModule("temp");
        mEfa = new SEFA("Weight", mModule);
    }

    /**
     *
     * @param iList 0=seams where lift is required, 1=resource to get payload
     */
    @Override
    public void init(List<Object> iList) {
        this.mSeamSet = (Set<Seam>) iList.get(0);
        this.mResource = (Resource) iList.get(1);
    }

    @Override
    public void run() {
        final List<Object> returnList = new ArrayList<Object>();
        //Get weights------------------------------------------------------------
        //The sum of each powerset element gives all possible weight sums.
        final List<Double> weightList = new ArrayList<Double>();
        final List<Double> toCheckList = new ArrayList<Double>();
        for (final Seam seam : mSeamSet) {
            weightList.add(seam.toAdd.mWeight);
            toCheckList.add(seam.toAdd.mWeight);
            weightList.add(seam.addTo.mWeight);
        }
        weightList.add(mResource.mPayload);

        final List<List<Double>> powerSet = powerSet(weightList);
        final Set<Double> weightSet = sumSubsets(powerSet, 0, mResource.mPayload);

        //Check------------------------------------------------------------------
        //The resource can lift(execute) all seams(operations)
        for (final Double d : toCheckList) {
            if (!weightSet.contains(d)) {
                fireNewMessageEvent("Resoruce cannot lift all blocks.");
                return;
            }
        }
        //Discretize weights-----------------------------------------------------
        discretizeDoubleToInteger(weightSet);
        printMapToModuleComments();

        final ModuleSubject ms = buildModuleSubject2();
        saveSupervisorAsWmodFile("C:\\Users\\patrik\\Desktop\\weight.wmod");
        reachabilityToGuards(ms);

        //Create automata--------------------------------------------------------
//        final ModuleSubject ms = buildModuleSubject();
//        saveSupervisorAsWmodFile("C:\\Users\\patrik\\Desktop\\weight.wmod"); //Send .wmod to Desktop
//        final Automata automata = flattenOut(ms);
//        final Automaton supervisor = synthesize(automata);

//        //Check------------------------------------------------------------------
//        //Supervisor exists?
//        if (supervisor == null || supervisor.nbrOfStates() == 0) {
//            fireNewMessageEvent("No supervisor found, no assembly sequence exists!");
//            return;
//        }
//
//        mRootSopNode = createOperationsFromAutomaton(supervisor);
//        returnList.add(mRootSopNode);
//
//        fireFinishedEvent(returnList);
    }

    void reachabilityToGuards(ModuleSubject ms) {
        //Easier to work with extended automata than module subject
        final ExtendedAutomata extendedAutomata = new ExtendedAutomata(ms);

        //Only one flower/efa/automaton
        final ExtendedAutomaton efa = extendedAutomata.getExtendedAutomataList().iterator().next();

        //Calculate reachability graph and save as extended automaton
        final EFAMonlithicReachability efaMR = new EFAMonlithicReachability(efa.getComponent(), extendedAutomata.getVars(), efa.getAlphabet());

        final ExtendedAutomaton efaMRautomaton = new ExtendedAutomaton(extendedAutomata, efaMR.createEFA());

        //Remove flower and variables
        ms.getComponentListModifiable().clear();
        extendedAutomata.getExtendedAutomataList().clear();
        extendedAutomata.getVars().clear();

        //Add reachability graph automaton
        extendedAutomata.addAutomaton(efaMRautomaton);

        saveSupervisorAsWmodFile("C:\\Users\\patrik\\Desktop\\weight.wmod"); //Send .wmod to Desktop

        //Synthesize
        final EditorSynthesizerOptions options = new EditorSynthesizerOptions();

        options.setSynthesisType(SynthesisType.NONBLOCKING);
        options.setSynthesisAlgorithm(SynthesisAlgorithm.PARTITIONBDD);
//        options.setSynthesisAlgorithm(SynthesisAlgorithm.MONOLITHICBDD);
        final BDDExtendedSynthesizer bddSynthesizer = new BDDExtendedSynthesizer(extendedAutomata, options);
        bddSynthesizer.synthesize(options);

        //Guard extraction
        final Vector<String> eventNames = new Vector<String>();

        for (final EventDeclSubject sigmaS : ms.getEventDeclListModifiable()) {
            if (sigmaS.getKind() == EventKind.CONTROLLABLE)// || sigmaS.getKind() == EventKind.UNCONTROLLABLE)
            {
                eventNames.add(sigmaS.getName());
            }
        }

        options.setExpressionType(1); //0=fromForbiddenStates, 1=fromAllowedStates, 2=mix
        bddSynthesizer.generateGuard(eventNames, options);
        final Map<String, BDDExtendedGuardGenerator> event2guard = bddSynthesizer.getEventGuardMap();

        //Print guards
        for (final String event : event2guard.keySet()) {
            final BDDExtendedGuardGenerator bddegg = event2guard.get(event);
            final String guard = bddegg.getGuard();
            System.out.println("event with guard " + event + " " + guard);

        }
    }

    ISopNode createOperationsFromAutomaton(Automaton iAutomation) {
        final ISopNode rootNode = new SopNode();
        if (createOperationFromOutgoingTransitions(iAutomation.getInitialState(), rootNode, rootNode)) {
            System.out.println(rootNode);
            return rootNode;
        }
        return new SopNode();
    }

    ISopNode createOperationFromTransition(final String iName) {
        //Create operation and add to model
        final String name = getSeamFromTransition(iName);
        if (name == null) {
            return null;
        }
        final OperationData opData = new OperationData(name, Model.newId());
        opData.setDescription(iName);
        return new SopNodeOperation(opData);
    }

    boolean createOperationFromOutgoingTransitions(final State iSourceState, final ISopNode iSopNode, final ISopNode iRootSopNode) {
        //Successor or alternative
        if (iSourceState.getOutgoingArcs().size() == 1) {
            //init
            final Arc arc = iSourceState.getOutgoingArcs().iterator().next();
            final String name = arc.getLabel();
            final ISopNode successorNode = createOperationFromTransition(name);
            if (successorNode == null) {
                return false;
            }

            //Successor
            if (iSopNode == iRootSopNode) {
                iSopNode.addNodeToSequenceSet(successorNode);
            } else {
                iSopNode.setSuccessorNode(successorNode);
            }
            if (!createOperationFromOutgoingTransitions(arc.getTarget(), successorNode, iRootSopNode)) {
                return false;
            }
        } else {
            //Create alternative node as successor
            if (!iSourceState.getOutgoingArcs().isEmpty()) {
                final ISopNode alternaitveNode = new SopNodeAlternative();
                if (iSopNode == iRootSopNode) {
                    iSopNode.addNodeToSequenceSet(alternaitveNode);
                } else {
                    iSopNode.setSuccessorNode(alternaitveNode);
                }
                for (final Arc arc : iSourceState.getOutgoingArcs()) {
                    //init
                    final String name = arc.getLabel();
                    final ISopNode childNode = createOperationFromTransition(name);
                    if (childNode == null) {
                        return false;
                    }

                    alternaitveNode.addNodeToSequenceSet(childNode);
                    if (!createOperationFromOutgoingTransitions(arc.getTarget(), childNode, iRootSopNode)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     *
     * @param iEventLabel
     * @return name of seam if a seam is found otherwise null
     */
    String getSeamFromTransition(final String iEventLabel) {
        for (final Seam seam : mSeamSet) {

            //Match event label for seam with event label for parameter
            final Matcher matcher = Pattern.compile(seam.eventLabel()).matcher(iEventLabel);
            if (matcher.find()) {
                return seam.name();
            }
        }
        return null;
    }

    /**
     * Adds parameter <code>iOpBefore</code> as precondition to parameter <code>iOpAfter</code>.<br/>
     * <code>iOpBefore</code> has to be finished before <code>iOpAfter</code> can start.
     * @param iOpBefore
     * @param iOpAfter
     */
    static void addSequentialPreCondition(final OperationData iOpBefore, final OperationData iOpAfter) {
        final ConditionStatement cs = new ConditionStatement("id" + Integer.toString(iOpBefore.getId()), ConditionStatement.Operator.Equal, "2");
        final ConditionExpression ce = new ConditionExpression(cs);
        final Condition condition = new Condition();
        condition.setGuard(ce);
        final Map<ConditionType, Condition> map = new HashMap<ConditionType, Condition>();
        map.put(ConditionType.PRE, condition);
        iOpAfter.setConditions(new ConditionData(iOpBefore.getName() + "_"), map);
    }

    ModuleSubject buildModuleSubject() {

        //Create center in flower automaton
        mEfa.addState(SEFA.SINGLE_LOCATION_NAME, true, true);

        for (final Seam seam : mSeamSet) {
            mBlockToLiftSet.add(seam.toAdd);
        }

        //Add integer variable for weights
        //Each toAdd can occur in many seams, so a set is required.
        final Map<String, Double> variableWeightMap = new HashMap<String, Double>();
        for (final Seam seam : mSeamSet) {
            variableWeightMap.put(seam.toAdd.variable(), seam.toAdd.mWeight);
        }
        for (final String variable : variableWeightMap.keySet()) {
            mModule.addIntVariable(variable, 0, mWeightMap.size() - 1, mWeightMap.get(variableWeightMap.get(variable)), null);
        }


        for (final Seam seam : mSeamSet) {

            //Add integer variable for lift of block-----------------------------
            mModule.addIntVariable(seam.executed(), 0, 1, 0, 1);

            //Go through weight combinations and add feasible combinations
            for (final Double weightToAdd : mWeightMap.keySet()) {
                if (mBlockToLiftSet.contains(seam.addTo)) {
                    for (final Double weightAddTo : mWeightMap.keySet()) {
                        generateTransition(seam, weightToAdd, weightAddTo);
                    }
                } else {
                    generateTransition(seam, weightToAdd, mResource.mPayload);
                }
            }
        }

        return mModule.generateTransitions();
    }

    ModuleSubject buildModuleSubject2() {

        //Create center in flower automaton
        mEfa.addState(SEFA.SINGLE_LOCATION_NAME, true, true);

        for (final Seam seam : mSeamSet) {
            mBlockToLiftSet.add(seam.toAdd);
        }

        //Add integer variable for weights
        //Each toAdd can occur in many seams, so a set is required.
        final Map<String, Double> variableWeightMap = new HashMap<String, Double>();
        for (final Seam seam : mSeamSet) {
            variableWeightMap.put(seam.toAdd.variable(), seam.toAdd.mWeight);
            variableWeightMap.put(seam.addTo.variable(), seam.addTo.mWeight);
        }
        for (final String variable : variableWeightMap.keySet()) {
            mModule.addIntVariable(variable, 0, 100000, variableWeightMap.get(variable).intValue(), null);
        }


        for (final Seam seam : mSeamSet) {

            //Add integer variable for lift of block-----------------------------
            mModule.addIntVariable(seam.executed(), 0, 1, 0, 1);

            //Add transition for seam
            //Event------------------------------------------------------
            final String event1 = seam.eventLabel();
//            System.out.println(event1);
            SEGA ega = new SEGA(event1);

            //Guard------------------------------------------------------
            final String guard1 = seam.toAdd.variable() + "<" + mResource.mPayload.intValue();
            ega.andGuard(guard1);
            final String guard3 = seam.executed() + "==" + "0";
            ega.andGuard(guard3);

            //Action-----------------------------------------------------
            final String action1 = seam.toAdd.variable() + "=" + seam.toAdd.variable() + "+" + seam.addTo.variable();
            ega.addAction(action1);
            final String action2 = seam.addTo.variable() + "=" + seam.addTo.variable() + "+" + seam.toAdd.variable();
            ega.addAction(action2);

            final String action3 = seam.executed() + "=" + "1";
            ega.addAction(action3);

            //Add transition---------------------------------------------
            mEfa.addStandardSelfLoopTransition(ega);
        }

        return mModule.generateTransitions();
    }

    /**
     * Check if <code>weightToAdd</code> is less than what a resource can lift.<br/>
     * If yes: Update weights for both toAdd and AddTo. This is done with action
     * in transition.<br/>
     * Also add gaurd and action that <code>seam</code> has been executed.<br/>
     * @param seam
     * @param weightToAdd
     * @param weightAddTo
     */
    void generateTransition(Seam seam, Double weightToAdd, Double weightAddTo) {
        //init check
        if (weightToAdd < seam.toAdd.mWeight) {
            return;
        }
        if (mBlockToLiftSet.contains(seam.addTo) && weightAddTo < seam.addTo.mWeight) {
            return;
        }

        if (weightToAdd < mResource.mPayload) {
            SEGA ega;

            //Event------------------------------------------------------
            final String event1 = seam.eventLabel() + weightToAdd + "_" + weightAddTo;
//            System.out.println(event1);
            ega = new SEGA(event1);

            //Guard------------------------------------------------------
            final String guard1 = seam.toAdd.variable() + "==" + mWeightMap.get(weightToAdd);
            ega.andGuard(guard1);
            if (mBlockToLiftSet.contains(seam.addTo)) {
                final String guard2 = seam.addTo.variable() + "==" + mWeightMap.get(weightAddTo);
                ega.andGuard(guard2);
            }
            final String guard3 = seam.executed() + "==" + "0";
            ega.andGuard(guard3);

            //Action-----------------------------------------------------
            final String action1 = seam.toAdd.variable() + "=" + getIntegerValueFromMap(weightToAdd + weightAddTo);
            ega.addAction(action1);
            if (mBlockToLiftSet.contains(seam.addTo)) {
                final String action2 = seam.addTo.variable() + "=" + getIntegerValueFromMap(weightToAdd + weightAddTo);
                ega.addAction(action2);
            }
            final String action3 = seam.executed() + "=" + "1";
            ega.addAction(action3);

            //Add transition---------------------------------------------
            mEfa.addStandardSelfLoopTransition(ega);
        }
    }

    /**
     * All values above payload for resource are set to payload for resources.<br/>
     * This is ok, because just equal to payload is not enough for lift.
     * @param iDouble value to test
     * @return check is done and Integer is returned
     */
    Integer getIntegerValueFromMap(Double iDouble) {
        if (mWeightMap.containsKey(iDouble)) {
            return mWeightMap.get(iDouble);
        }
        return mWeightMap.get(mResource.mPayload);
    }

    Automata flattenOut(ModuleSubject iModuleSubject) {
        return (Automata) mModule.getDFA(iModuleSubject);
    }

    Automaton synthesize(Automata iAutomata) {
        if (iAutomata != null) {

            final SynthesizerOptions syntho = new SynthesizerOptions();
            syntho.setSynthesisType(SynthesisType.NONBLOCKING);
            syntho.setSynthesisAlgorithm(SynthesisAlgorithm.MONOLITHIC);
            syntho.setPurge(true);

            final SynchronizationOptions syncho = new SynchronizationOptions();
            syncho.setSynchronizationType(SynchronizationType.FULL);

            final AutomataSynthesizer as = new AutomataSynthesizer(iAutomata, syncho, syntho);

            try {
                iAutomata = as.execute();
                return iAutomata.getFirstAutomaton();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return null;
    }

    boolean saveSupervisorAsWmodFile(String iFilePath) {
        return SModule.saveToWMODFile(iFilePath, mModule.getModuleSubject());
    }

    List<List<Double>> powerSet(List<Double> list) {
        List<List<Double>> ps = new ArrayList<List<Double>>();
        ps.add(new ArrayList<Double>());   // add the empty set

        // for every item in the original list
        for (Double item : list) {
            List<List<Double>> newPs = new ArrayList<List<Double>>();

            for (List<Double> subset : ps) {
                // copy all of the current powerSet's subsets
                newPs.add(subset);

                // plus the subsets appended with the current item
                List<Double> newSubset = new ArrayList<Double>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            // powerSet is now powerSet of list.subList(0, list.indexOf(item)+1)
            ps = newPs;
        }

        return ps;
    }

    Set<Double> sumSubsets(List<List<Double>> iSubsetList, final double iGreaterThan, final double iLessThenOrEqualTo) {
        final Set<Double> returnSet = new HashSet<Double>();

        for (final List<Double> list : iSubsetList) {
            Double sumOfValues = new Double(0);
            for (final Double value : list) {
                sumOfValues += value;
            }
            if (sumOfValues > iGreaterThan && sumOfValues <= iLessThenOrEqualTo) {
                returnSet.add(sumOfValues);
            }
        }

        return returnSet;
    }

    void discretizeDoubleToInteger(final Set<Double> iKeySet) {
        mWeightMap = new HashMap<Double, Integer>();

        int i = 0;
        for (final Double key : iKeySet) {
            mWeightMap.put(key, new Integer(i++));
        }
    }

    void printMapToModuleComments() {
        String s = "Map between true values and values in model\n";
        for (final Double d : mWeightMap.keySet()) {
            s += d + " : " + mWeightMap.get(d) + "\n";
        }
        mModule.setComment(s);
    }
}
