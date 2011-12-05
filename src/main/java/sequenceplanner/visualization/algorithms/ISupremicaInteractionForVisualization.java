package sequenceplanner.visualization.algorithms;

import java.util.Map;
import java.util.Set;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import sequenceplanner.model.SOP.SopNode;

/**
 * Interface for formal methods that are used for visualization.<br/>
 * @author patrik
 */
public interface ISupremicaInteractionForVisualization {

    public enum Type {

        EVENT_PREFIX("e"), EVENT_UP("up"), EVENT_DOWN("down"),
        OPERATION_VARIABLE_PREFIX("id"), BIG_FLOWER_EFA_NAME("Single"),
        LOOK_FOR_GUARD("guard"), LOOK_FOR_ACTION("action");
        private final String mType;

        Type(String iType) {
            mType = iType;
        }

        @Override
        public String toString() {
            return mType;
        }
    };

    /**
     * DOES NOT HANDLE VARIABLES IN CONDITIONS!!!<br/>
     * Creates a {@link ModuleSubject} from a {@link SopNode}.<br/>
     * Each element (operation) in the sequence set to iOperationSet is modeled as a EFA variable with values {0,1,2}.<br/>
     * If operation as {@link SopNode} in iHasToFinishSet -> only value 2 is marked, else all values are marked.<br/>
     * @param iOperationSet Operations to take consideration to
     * @param iHasToFinishSet Operations with only Of as marked location
     * @return null if problem else a {@link ModuleSubject}
     */
    ModuleSubject getModuleSubject(
            SopNode iOperationSet, SopNode iHasToFinishSet);

    /**
     * From EFA to DFA
     * @param iModuleSubject
     * @return null if problem else a {@link Automata}
     */
    Automata flattenOut(
            ModuleSubject iModuleSubject);

    /**
     * Monolithic non-blocking and controllable synthesis
     * @param iAutomata
     * @return null if problem else a {@link Automaton} as supervisor
     */
    Automaton synthesize(
            Automata iAutomata);

    /**
     * Get the states from where each event is enabled.
     * @param iAutomaton
     * @return keyset: event, valueset: states where key is enabled
     */
    Map<String, Set<String>> getStateSpaceForEventSetMap(Automaton iAutomaton);

    /**
     * To save a Module as wmod file
     * @param iFilePath wherer the wmod file should be saved
     * @return true if ok else false
     */
    boolean saveSupervisorAsWmodFile(String iFilePath);
}
