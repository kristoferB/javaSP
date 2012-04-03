package sequenceplanner.IO.EFA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
 * @author patrik
 */
public class RandomSynthesizer extends AAlgorithm {

    final EmptyModule mModule;
    String mFilePath;
    Automata mAutomata;
    final Map<Automaton, State> mAutomatonStateMap;
    Alphabet mAllowedEventSet;
    LabeledEvent mEvent;
    Set<List<LabeledEvent>> mMarkedStringsSet;
    List<LabeledEvent> mStringList;
    int mMaxNbrOfIterationsOnEachString;
    int mTotalNbrOfIterationsOnSystem;

    public RandomSynthesizer(String iThreadName) {
        super(iThreadName);
        mModule = new EmptyModule("Test", "No comments");
        mAutomatonStateMap = new HashMap<Automaton, State>();
        mMarkedStringsSet = new HashSet<List<LabeledEvent>>();
    }

    @Override
    public void init(List<Object> iList) {
        mFilePath = (String) iList.get(0);
        mMaxNbrOfIterationsOnEachString = (Integer) iList.get(1);
        mTotalNbrOfIterationsOnSystem = (Integer) iList.get(2);
    }

    @Override
    public void run() {
        //Init Automata object
        mAutomata = AModule.getDFA(AModule.readFromWMODFile(mFilePath));
        if (mAutomata == null) {
            fireNewMessageEvent("Problem with reading file, I will not go on!");
            return;
        }

        //Plantify specifications

        //Init automaton state map

        //Do the iterations on the system.
        for (int i = 0; i < mTotalNbrOfIterationsOnSystem; ++i) {
//            System.out.println("mTotalNbrOfIterationsOnSystem: " + (mTotalNbrOfIterationsOnSystem - i));
            buildString();
        }

        final List returnList = new ArrayList();
        returnList.add(mMarkedStringsSet);
        returnList.add(getDurationForRunMethod());
        fireFinishedEvent(returnList);

    }

    private void initAutomata() {
        for (final Automaton a : mAutomata) {
            mAutomatonStateMap.put(a, a.getInitialState());
        }
    }

    private void buildString() {

        initAutomata();
        mStringList = new ArrayList<LabeledEvent>();

        for (int i = 0; i < mMaxNbrOfIterationsOnEachString; ++i) {
//            System.out.println("mMaxNbrOfIterationsOnEachString: " + (mMaxNbrOfIterationsOnEachString - i));

            if (isCurrentStateMarked()) {
                mMarkedStringsSet.add(mStringList);
                fireNewMessageEvent(mMarkedStringsSet.toString());
            }

            calculateAllowedEventSet();
            pickOneEvent();

            if (mEvent == null) {
                //Deadlock!!
                return;
            }

            mStringList.add(mEvent);

            updateStates();
        }
    }

    private void calculateAllowedEventSet() {
        Alphabet globallyEnabledEventSet = new Alphabet();
        Alphabet globallyForbiddenEventSet = new Alphabet();

        for (final Automaton a : mAutomatonStateMap.keySet()) {
            final State state = mAutomatonStateMap.get(a);

            final Alphabet enabledEventSet = new Alphabet();
            for (final Arc arc : state.getOutgoingArcs()) {
                enabledEventSet.add(arc.getEvent());
            }
            globallyEnabledEventSet = AlphabetHelpers.union(globallyEnabledEventSet, enabledEventSet);
            globallyForbiddenEventSet = AlphabetHelpers.union(globallyForbiddenEventSet, AlphabetHelpers.minus(a.getAlphabet(), enabledEventSet));
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
        for (final Automaton a : mAutomatonStateMap.keySet()) {
            final State sourceState = mAutomatonStateMap.get(a);

            final State targetState = sourceState.nextState(mEvent);

            if (targetState != null) {
                mAutomatonStateMap.put(a, targetState);
            }
        }
    }

    private boolean isCurrentStateMarked() {
        for (State s : mAutomatonStateMap.values()) {
            if (!s.isAccepting()) {
                return false;
            }
        }
        return true;
    }
}
