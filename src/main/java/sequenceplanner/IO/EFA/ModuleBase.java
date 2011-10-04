package sequenceplanner.IO.EFA;

import java.util.HashSet;
import java.util.Set;

/**
 * To store variables and transitions for later translation to some implementation of {@link AModule}.
 * @author patrik
 */
public class ModuleBase {

    final private Set<IVariable> mVariableSet;
    final private Set<Transition> mTransitionSet;

    public ModuleBase() {
        mVariableSet = new HashSet<IVariable>();
        mTransitionSet = new HashSet<Transition>();
    }

    public Set<Transition> getTransitionSet() {
        return mTransitionSet;
    }

    public Set<IVariable> getVariableSet() {
        return mVariableSet;
    }

    public Transition createTransition(final String iLabel, final boolean iControllable) {
        final Transition trans = new Transition(iLabel);
        trans.setAttribute(Transition.UNCONTROLLABLE, iControllable);
        mTransitionSet.add(trans);
        return trans;
    }

    /**
     *
     * @param iVariable
     * @return false if <code>iVariable</code> == <code>null</code> else true
     */
    public boolean storeVariable(final IVariable iVariable) {
        if (iVariable == null) {
            return false;
        }
        mVariableSet.add(iVariable);
        return true;
    }

    @Override
    public String toString() {
        String returnString = "";

        returnString += "----------\n";
        returnString += "Variables:\n";

        for (final IVariable variable : getVariableSet()) {
            returnString += variable.getVarLabel() + " " + variable.getVarLowerBound() + ".." + variable.getVarUpperBound() + " " + variable.getVarInitValue() + " " + variable.getVarMarkedValues();
            returnString += "\n";
        }

        returnString += "------------\n";
        returnString += "Transitions:\n";

        for (final Transition trans : getTransitionSet()) {
            returnString += trans.getLabel() + " " + "g:" + trans.getmGuardConjunctionSet() + " " + "a:" + trans.getmActionSet();
            returnString += "\n";
        }

        returnString += "------------\n";

        return returnString;
    }
}
