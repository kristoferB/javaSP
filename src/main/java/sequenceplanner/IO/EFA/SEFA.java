package sequenceplanner.IO.EFA;

import java.util.Collection;
import net.sourceforge.waters.model.module.EdgeProxy;
import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.Module;

/**
 * Data structure for storage of EFAs.<br/>
 * @author patrik
 */
public class SEFA {

    private EFA mEfa = null;
    public final static String SINGLE_LOCATION_NAME = "pm";

    public SEFA(String name, Module iModule) {
        mEfa = new EFA(name, iModule);
        iModule.addAutomaton(mEfa);
    }

    public EFA getEFA() {
        return mEfa;
    }

    public String getName() {
        return mEfa.getName();
    }

    public Collection<EdgeProxy> getTransitions() {
        return mEfa.getTransitions();
    }

    public void addState(String name, boolean accepting, boolean initial) {
        mEfa.addState(name, accepting, initial);
    }

    public void addTransition(String from, String to, String event, String guard, String action) {
        mEfa.addTransition(from, to, event, guard, action);
    }

    /**
     * Requires that there exists a location in this object with label "pm".<br/>
     * @param ega
     */
    public void addStandardSelfLoopTransition(SEGA ega) {
        addTransition(SINGLE_LOCATION_NAME, SINGLE_LOCATION_NAME, ega.getEvent(), ega.getGuard(), ega.getAction());
    }

    public void addStandardSelfLoopTransition(final Transition iTrans) {
        final SEGA sega = new SEGA(iTrans.getmLabel());
        for (final String guard : iTrans.getmGuardConjunctionSet()) {
            sega.andGuard(guard);
        }
        for (final String action : iTrans.getmActionSet()) {
            sega.addAction(action);
        }

        final Boolean uncontrollable = (Boolean) iTrans.getAttribute(Transition.UNCONTROLLABLE);
        if (uncontrollable == null | uncontrollable == false) {
            mEfa.addEvent(iTrans.getmLabel(), "uncontrollable");
        }
        addStandardSelfLoopTransition(sega);
    }
}

