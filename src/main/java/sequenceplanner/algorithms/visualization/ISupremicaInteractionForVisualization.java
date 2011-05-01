package sequenceplanner.algorithms.visualization;

import java.util.Map;
import java.util.Set;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import sequenceplanner.model.SOP.ISopNode;

/**
 * Interface for formal methods that are used for visualization.<br/>
 * @author patrik
 */
public interface ISupremicaInteractionForVisualization {

    String EVENT_PREFIX  = "e";
    String EVENT_UP = "up";
    String EVENT_DOWN = "down";
    String OPERATION_VARIABLE_PREFIX = "o";
    String BIG_FLOWER_EFA_NAME = "Single";

    /**
     * DOES NOT HANDLE VARIABLES IN CONDITIONS!!!<br/>
     * Creates a {@link ModuleSubject} from a {@link ISopNode}.<br/>
     * Each element (operation) in the sequence set to iOperationSet is modeled as a EFA variable with values {0,1,2}.<br/>
     * If operation as {@link ISopNode} in iHasToFinishSet -> only value 2 is marked, else all values are marked.<br/>
     * @param iOperationSet Operations to take consideration to
     * @param iHasToFinishSet Operations with only Of as marked location
     * @return null if problem else a {@link ModuleSubject}
     */
    ModuleSubject getModuleSubject(ISopNode iOperationSet, ISopNode iHasToFinishSet);

    /**
     * From EFA to DFA
     * @param iModuleSubject
     * @return null if problem else a {@link Automata}
     */
    Automata flattenOut(ModuleSubject iModuleSubject);

    /**
     * Monolithic non-blocking and controllable synthesis
     * @param iAutomata
     * @return null if problem else a {@link Automaton} as supervisor
     */
    Automaton synthesize(Automata iAutomata);

    /**
     * Get the states from where each event is enabled.
     * @param iAutomaton
     * @return keyset: event, valueset: states where key is enabled
     */
    Map<String,Set<String>> getStateSpaceForEventSetMap(Automaton iAutomaton);

    /**
     * To save a Module as wmod file
     * @param iFilePath wherer the wmod file should be saved
     * @return true if ok else false
     */
    boolean saveSupervisorAsWmodFile(String iFilePath);

}
