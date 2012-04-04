package sequenceplanner.IO.EFA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.supremica.automata.Alphabet;
import org.supremica.automata.AlphabetHelpers;
import org.supremica.automata.Arc;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import org.supremica.automata.LabeledEvent;
import org.supremica.automata.State;
import sequenceplanner.algorithm.AAlgorithm;

/**
 * To find a subset of the marked language for a set of interacting plants.<br>
 * The algorithm tests a subset of the possible strings based on randomness.<br>
 * The plants communicate through full synchronus communication.<br>
 * Assumption: \Sigma_{Sp} \subset \Sigma_P<br>
 * @author patrik
 */
public class RandomSynthesizer extends AAlgorithm {

    final EmptyModule mModule;
    String mFilePath;
    Automata mAutomata;
    final Map<Automaton, State> mAutomatonStateMap;
    Integer mStateHashCode;
    final Map<Automaton, Map<State, StateEvents>> mAutomatonStatesEventSetMap;
    Alphabet mAllowedEventSet;
    LabeledEvent mEvent;
    final Set<List<LabeledEvent>> mMarkedStringsSet;
    List<LabeledEvent> mStringList;
    int mMaxNbrOfIterationsOnEachString;
    int mTotalNbrOfIterationsOnSystem;
    final Map<Integer, PossibleUcState> mPossibeUcStringsMap;

    public RandomSynthesizer(String iThreadName) {
        super(iThreadName);
        mModule = new EmptyModule("Test", "No comments");
        mAutomatonStateMap = new HashMap<Automaton, State>();
        mAutomatonStatesEventSetMap = new HashMap<Automaton, Map<State, StateEvents>>();
        mMarkedStringsSet = new HashSet<List<LabeledEvent>>();
        mPossibeUcStringsMap = new HashMap<Integer, PossibleUcState>();
    }

    @Override
    public void init(List<Object> iList) {
        mFilePath = (String) iList.get(0);
        mMaxNbrOfIterationsOnEachString = (Integer) iList.get(1);
        mTotalNbrOfIterationsOnSystem = (Integer) iList.get(2);
    }

    @Override
    public void run() {

        //Generate DFA from automata object and
        //build maps to be used during search.-----------------------------------
        fireNewMessageEvent("Starts with initialization ...");
        mAutomata = AModule.getDFA(AModule.readFromWMODFile(mFilePath));
        if (mAutomata == null) {
            fireNewMessageEvent("Problem with reading file, I will not go on!");
            return;
        }

        initAutomatonStatesEventSetMap();
        fireNewMessageEvent("... Done with initialization!");
        fireNewMessageEvent("Total time spent: " + getDurationForRunMethod());
        //-----------------------------------------------------------------------

        //No need to go on if initial state is uncontrollable
        if (isInitialStateUncontrollalbe()) {
            fireNewMessageEvent("Initial state is uncontrollable, no supervisor can be found!");
            fireNewMessageEvent("Total time spent: " + getDurationForRunMethod());
            return;
        }

        //Do the search/iterations on the system.--------------------------------
        fireNewMessageEvent("Starts with iterations ...");
        for (int i = 0; i < mTotalNbrOfIterationsOnSystem; ++i) {
            if (i % 5 == 0 && !getStatus("Number of iterations left: " + (mTotalNbrOfIterationsOnSystem - i))) {
                return;
            }
            buildString();
        }
        fireNewMessageEvent("... Finished with iterations!");
        fireNewMessageEvent("Number of candidate strings found: " + mMarkedStringsSet.size());

        fireNewMessageEvent("Total time spent: " + getDurationForRunMethod());
        //-----------------------------------------------------------------------

        //Remove strings that are uncontrollable---------------------------------
        fireNewMessageEvent("Starts with removing strings passing through uc states ...");

        int nbrOfIterationsLeft = mMarkedStringsSet.size();
        for (final List<LabeledEvent> list : mMarkedStringsSet) {
            if (nbrOfIterationsLeft % 5 == 0 && !getStatus("Number of iterations left: " + nbrOfIterationsLeft)) {
                return;
            }
            mStringList = list;
            examineString();
            --nbrOfIterationsLeft;
        }
        removeUncontrollableStrings();

        fireNewMessageEvent("... Done with removing strings passing through uc states!");
        fireNewMessageEvent("Number of strings in marked language found: " + mMarkedStringsSet.size());
        fireNewMessageEvent("Total time spent: " + getDurationForRunMethod());
        //-----------------------------------------------------------------------

        //Export results
        final List returnList = new ArrayList();
        returnList.add(mMarkedStringsSet);
        fireFinishedEvent(returnList);

    }

    private void initAutomatonStatesEventSetMap() {
        for (final Automaton a : mAutomata) {
            final Map<State, StateEvents> stateEventSetMap = new HashMap<State, StateEvents>();
            for (final State s : a.getStateSet()) {
                final Alphabet enabledEventSet = new Alphabet();
                for (final Arc arc : s.getOutgoingArcs()) {
                    enabledEventSet.add(arc.getEvent());
                }
                stateEventSetMap.put(s, new StateEvents(enabledEventSet, AlphabetHelpers.minus(a.getAlphabet(), enabledEventSet)));
            }
            mAutomatonStatesEventSetMap.put(a, stateEventSetMap);
        }
    }

    private void buildString() {

        initAutomatonStateMap();
        mStringList = new ArrayList<LabeledEvent>();

        for (int i = 0; i < mMaxNbrOfIterationsOnEachString; ++i) {
//            fireNewMessageEvent("mMaxNbrOfIterationsOnEachString: " + (mMaxNbrOfIterationsOnEachString - i));

            if (isCurrentStateMarked()) {
                saveAMarkedString();
            }

            calculateAllowedEventSet(false);
            pickOneEvent();

            if (mEvent == null) {
                //Deadlock!!
                return;
            }

            mStringList.add(mEvent);
            updateStates();
        }
    }

    private void initAutomatonStateMap() {
        final List<State> stateList = new ArrayList<State>();
        for (final Automaton a : mAutomata) {
            mAutomatonStateMap.put(a, a.getInitialState());
            stateList.add(a.getInitialState());
        }
        mStateHashCode = stateList.hashCode();
    }

    private boolean isCurrentStateMarked() {
        for (State s : mAutomatonStateMap.values()) {
            if (!s.isAccepting()) {
                return false;
            }
        }
        return true;
    }

    private void saveAMarkedString() {
        mMarkedStringsSet.add(new ArrayList<LabeledEvent>(mStringList));
    }

    private void calculateAllowedEventSet(final boolean iJustPlants) {
        Alphabet globallyEnabledEventSet = new Alphabet();
        Alphabet globallyForbiddenEventSet = new Alphabet();

        for (final Automaton a : mAutomatonStateMap.keySet()) {
            if (!iJustPlants || a.isPlant()) {
                final State state = mAutomatonStateMap.get(a);
                globallyEnabledEventSet = AlphabetHelpers.union(globallyEnabledEventSet, mAutomatonStatesEventSetMap.get(a).get(state).getmAllowedEventSet());
                globallyForbiddenEventSet = AlphabetHelpers.union(globallyForbiddenEventSet, mAutomatonStatesEventSetMap.get(a).get(state).getmForbiddenEventSet());
            }
        }

        mAllowedEventSet = AlphabetHelpers.minus(globallyEnabledEventSet, globallyForbiddenEventSet);
    }

    private void pickOneEvent() {
        final List<LabeledEvent> list = new ArrayList<LabeledEvent>(mAllowedEventSet.values());
        Collections.shuffle(list);
        if (!list.isEmpty()) {
            mEvent = list.get(0);
            return;
        }
        mEvent = null;
    }

    private void updateStates() {
        final List<State> stateList = new ArrayList<State>();
        for (final Automaton a : mAutomatonStateMap.keySet()) {
            final State sourceState = mAutomatonStateMap.get(a);
            final State targetState = sourceState.nextState(mEvent);
            if (targetState != null) {
                mAutomatonStateMap.put(a, targetState);
                stateList.add(targetState);
            } else {
                stateList.add(sourceState);
            }
        }
        mStateHashCode = stateList.hashCode();
    }

    private void examineString() {
        initAutomatonStateMap();

        for (final LabeledEvent event : mStringList) {
            mEvent = event;

            calculateAllowedEventSet(true);

            final Alphabet ucAlphabet = mAllowedEventSet.getUncontrollableAlphabet();

            if (ucAlphabet.size() != 0) {
                //Is this a new (global) state?
                if (!mPossibeUcStringsMap.containsKey(mStateHashCode)) {
                    mPossibeUcStringsMap.put(mStateHashCode, new PossibleUcState(ucAlphabet));
                }
                final PossibleUcState stateInfo = mPossibeUcStringsMap.get(mStateHashCode);

                //Has this (uc) event not been paired with a string
                if (!mEvent.isControllable() && !stateInfo.mEventStringPairMap.get(mEvent)) {
                    stateInfo.mEventStringPairMap.put(mEvent, true);
                }

                //Add this string to strings that pass by this possible uc state
                stateInfo.mStingsPassingThroughThisStateSet.add(mStringList);
            }
            updateStates();
        }
    }

    /**
     * Removes strings that pass through states that has at least one outgoing event that is uncontrollable.<br>
     * This uc event is not within any of the candidate string found.<br>
     */
    private void removeUncontrollableStrings() {
        for (final Integer state : mPossibeUcStringsMap.keySet()) {
            final PossibleUcState stateInfo = mPossibeUcStringsMap.get(state);
            if (stateInfo.stateIsUncontrollable()) {
                mMarkedStringsSet.removeAll(stateInfo.mStingsPassingThroughThisStateSet);
            }
        }
    }

    private boolean isInitialStateUncontrollalbe() {
        initAutomatonStateMap();
        calculateAllowedEventSet(true);
        if (mAllowedEventSet.getUncontrollableAlphabet().size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * To store allowed and forbidden events for a state.
     */
    private class StateEvents {

        final private Alphabet mAllowedEventSet;
        final private Alphabet mForbiddenEventSet;

        public StateEvents(Alphabet mAllowedEventSet, Alphabet mForbiddenEventSet) {
            this.mAllowedEventSet = mAllowedEventSet;
            this.mForbiddenEventSet = mForbiddenEventSet;
        }

        public Alphabet getmAllowedEventSet() {
            return mAllowedEventSet;
        }

        public Alphabet getmForbiddenEventSet() {
            return mForbiddenEventSet;
        }
    }

    private class PossibleUcState {

        final protected Map<LabeledEvent, Boolean> mEventStringPairMap;
        final Set<List<LabeledEvent>> mStingsPassingThroughThisStateSet;

        public PossibleUcState(final Alphabet iUcAlphabet) {
            mEventStringPairMap = new HashMap<LabeledEvent, Boolean>();
            mStingsPassingThroughThisStateSet = new HashSet<List<LabeledEvent>>();

            for (final LabeledEvent event : iUcAlphabet) {
                mEventStringPairMap.put(event, false);
            }
        }

        /**
         *
         * @return false if all outgoing uc events are contained in a string else true
         */
        public boolean stateIsUncontrollable() {
            for (final Boolean bool : mEventStringPairMap.values()) {
                if (!bool) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return mEventStringPairMap.toString();
        }
    }
}
