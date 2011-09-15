package sequenceplanner.multiproduct.InfoInSingleStateOperations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sourceforge.waters.xsd.module.Module;
import sequenceplanner.IO.EFA.Transition;
import sequenceplanner.algorithm.AAlgorithm;
import sequenceplanner.expression.IClause;

/**
 * This is were the magic happens!<br/>
 * From an internal data structure to a set of {@link Transition}.<br/>
 * This set can later on be feed to a {@link Module} implementaion.
 * @author patrik
 */
public class CreateTransitions extends AAlgorithm {

    private static Set<Operation> mOperationSet = null;
    private static Set<Transition> mTransitionSet = null;

    public CreateTransitions(String iThreadName) {
        super(iThreadName);
    }

    @Override
    public void init(List<Object> iList) {
        mOperationSet = (Set<Operation>) iList.get(0);

        mTransitionSet = new HashSet<Transition>();
    }

    @Override
    public void run() {

        for (final Operation op : mOperationSet) {

            if(!op.mPreOperationDNFClauseList.isEmpty()) {
            for (final IClause clause : op.mPreOperationDNFClauseList) {
                final String eventLabel = op.getVarLabel() + "_" + op.mPreOperationDNFClauseList.indexOf(clause);
                final Transition trans = createTransition(eventLabel);
            }
            } else {
                //No preoperations
            }
        }
    }

    private static Transition createTransition(final String iLabel) {
        final Transition trans = new Transition(iLabel);
        trans.setAttribute(Transition.CONTROLABLE, true);
        mTransitionSet.add(trans);
        return trans;
    }
}
