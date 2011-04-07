package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sourceforge.waters.model.des.StateProxy;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.Arc;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import org.supremica.automata.algorithms.AutomataSynthesizer;
import org.supremica.automata.algorithms.SynchronizationOptions;
import org.supremica.automata.algorithms.SynchronizationType;
import org.supremica.automata.algorithms.SynthesisAlgorithm;
import org.supremica.automata.algorithms.SynthesisType;
import org.supremica.automata.algorithms.SynthesizerOptions;
import sequenceplanner.efaconverter.SEFA;
import sequenceplanner.efaconverter.SEGA;
import sequenceplanner.efaconverter.SModule;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * SP->EFA translation based on {@link SModule}, {@link SEFA}, and {@link SEGA}.<br/>
 * @author patrik
 */
public class SupremicaInteractionForVisualization implements ISupremicaInteractionForVisualization {

    private Set<Integer> mAllOperationSet = new HashSet<Integer>(); //All operations
    private SModule mModule = new SModule("temp");
    private SEFA mEfa = new SEFA(BIG_FLOWER_EFA_NAME, mModule);

    public SupremicaInteractionForVisualization() {
    }

    @Override
    public Automata flattenOut(ModuleSubject iModuleSubject) {
        return (Automata) mModule.getDFA();
    }

    @Override
    public ModuleSubject getModuleSubject(ISopNode iOperationSet, ISopNode iHasToFinishSet) {
        //Check
        if (iOperationSet == null || iHasToFinishSet == null) {
            System.out.println("One or more parameters are null");
            return null;
        } else if (!(new SopNodeToolboxSetOfOperations().operationsAreSubset(iHasToFinishSet, iOperationSet))) {
            System.out.println("Specifying markings on operations outside of operations in plant model.");
            return null;
        }

        //Create set for ids
        for (final ISopNode node : iOperationSet.getFirstNodesInSequencesAsSet()) {
            if (node.getNodeType() instanceof OperationData) {
                final OperationData opData = (OperationData) node.getNodeType();
                mAllOperationSet.add(opData.getId());
            } else {
                System.out.println("Node: " + node.typeToString() + " not an operation!");
                return null;
            }
        }

        //Check ids
        if (!mModule.testIDs(mAllOperationSet)) {
            System.out.println("Problem with ids!");
            return null;
        }

        SEGA ega;
        mEfa.addState(SEFA.SINGLE_LOCATION_NAME, true, true);

        for (final ISopNode node : iOperationSet.getFirstNodesInSequencesAsSet()) {
            if (!(node.getNodeType() instanceof OperationData)) {
                System.out.println("Node: " + node.typeToString() + " not an operation!");
                return null;
            }
            OperationData opData = (OperationData) node.getNodeType();
            final int id = opData.getId();
            final String varName = OPERATION_VARIABLE_PREFIX + id;

            //Add integer variable for operation---------------------------------
            Integer marking = null;
            if (new SopNodeToolboxSetOfOperations().getOperations(iHasToFinishSet).contains(node)) {
                marking = 2;
            }
            mModule.addIntVariable(varName, 0, 2, 0, marking);
            //-------------------------------------------------------------------

            //Add transition to start execute operation--------------------------
            ega = new SEGA(EVENT_PREFIX + id + EVENT_UP);
            ega.andGuard(varName + "==0");
            ega.addGuardBasedOnSPCondition(opData.getRawPrecondition(), OPERATION_VARIABLE_PREFIX, mAllOperationSet);
            ega.addAction(varName + "=1");
            mEfa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------

            //Add transition to finish execute operation-------------------------
            ega = new SEGA(EVENT_PREFIX + id + EVENT_DOWN);
            ega.andGuard(varName + "==1");
            ega.addGuardBasedOnSPCondition(opData.getRawPostcondition(), OPERATION_VARIABLE_PREFIX, mAllOperationSet);
            ega.addAction(varName + "=2");
            mEfa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------
        }

        return mModule.generateTransitions();
    }

    @Override
    public Map<String, Set<String>> getStateSpaceForEventSetMap(Automaton iAutomaton) {

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        //Loop states and events to fill map-------------------------------------
        for (StateProxy sp : iAutomaton.getStates()) {
            String stateName = sp.getName();
            for (Arc arc : iAutomaton.getStateWithName(stateName).getOutgoingArcs()) {
                //Remove the single EFA location "pm"
                stateName = stateName.replaceAll("."+SEFA.SINGLE_LOCATION_NAME+".", ".")
                        .replaceAll("."+SEFA.SINGLE_LOCATION_NAME, "").replaceAll(SEFA.SINGLE_LOCATION_NAME+".", "");
                String eventName = arc.getLabel();
                if (eventName.contains(EVENT_UP)) {
                    eventName = eventName.substring(0, eventName.indexOf(EVENT_UP) + 2); //To handle addition of suffix for events when disjunction
                } else {//EVENT_DOWN
                    eventName = eventName.substring(0, eventName.indexOf(EVENT_DOWN) + 4);
                }
                if (!map.keySet().contains(eventName)) {
                    map.put(eventName, new HashSet<String>());
                }
                map.get(eventName).add(stateName);
            }
        }//----------------------------------------------------------------------
        return map;
    }

    @Override
    public Automaton synthesize(Automata iAutomata) {
        if (iAutomata != null) {

            SynthesizerOptions syntho = new SynthesizerOptions();
            syntho.setSynthesisType(SynthesisType.NONBLOCKING);
            syntho.setSynthesisAlgorithm(SynthesisAlgorithm.MONOLITHIC);
            syntho.setPurge(true);

            SynchronizationOptions syncho = new SynchronizationOptions();
            syncho.setSynchronizationType(SynchronizationType.FULL);

            AutomataSynthesizer as = new AutomataSynthesizer(iAutomata, syncho, syntho);

            try {
                iAutomata = as.execute();
                return iAutomata.getFirstAutomaton();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return null;
    }
}
