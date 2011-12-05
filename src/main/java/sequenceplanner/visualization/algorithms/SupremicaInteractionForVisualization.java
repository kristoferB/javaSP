package sequenceplanner.visualization.algorithms;

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
import sequenceplanner.IO.EFA.EmptyModule;
import sequenceplanner.IO.EFA.SEFA;
import sequenceplanner.IO.EFA.SEGA;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * SP->EFA translation based on {@link SModule}, {@link SEFA}, and {@link SEGA}.<br/>
 * @author patrik
 */
public class SupremicaInteractionForVisualization implements ISupremicaInteractionForVisualization {

    private final Set<ConditionData> mConditionsToInclude;
    private final Set<ResourceVariableData> resources;
    private Set<Integer> mAllOperationSet = new HashSet<Integer>(); //All operations
    private EmptyModule mmModule = new EmptyModule("visualizationBeforeSynthesisModule", null);
//    private SModule mModule = new SModule("temp");
//    private SEFA mEfa = new SEFA(Type.BIG_FLOWER_EFA_NAME.toString(), mModule);
    private SEFA mmEfa = new SEFA(Type.BIG_FLOWER_EFA_NAME.toString(), mmModule.getAvocadesModule());

    public SupremicaInteractionForVisualization(final Set<ConditionData> iConditionsToInclude, Set<ResourceVariableData> resources) {
        this.mConditionsToInclude = iConditionsToInclude;
        this.resources = resources;
    }

    @Override
    public Automata flattenOut(ModuleSubject iModuleSubject) {
        iModuleSubject = EmptyModule.getMonolithicReachabilityModule(iModuleSubject);
        return EmptyModule.getDFA(iModuleSubject);
    }

    @Override
    public ModuleSubject getModuleSubject(SopNode iOperationSet, SopNode iHasToFinishSet) {
        //Check
        if (iOperationSet == null || iHasToFinishSet == null) {
            System.out.println("One or more parameters are null");
            return null;
        } else if (!(new SopNodeToolboxSetOfOperations().operationsAreSubset(iHasToFinishSet, iOperationSet))) {
            System.out.println("Specifying markings on operations outside of operations in plant model.");
            return null;
        }

        GetComplParallelOps pOPs = new GetComplParallelOps();
        Set<String> parallelOps = pOPs.getParallelOps(iOperationSet);
        
        
//        //Create set for ids
//        for (final SopNode node : iOperationSet.getFirstNodesInSequencesAsSet()) {
//            if (node instanceof SopNodeOperation) {
//                mAllOperationSet.add(node.getOperation().getId());
//            } else {
//                System.out.println("Node: " + node.typeToString() + " not an operation!");
//                return null;
//            }
//        }
        
        // Add resource variables
        for (ResourceVariableData r : resources){
            String varName = Type.OPERATION_VARIABLE_PREFIX.toString() + r.getId();
            mmModule.addIntVariable(varName, r.getMin(), r.getMax(), r.getInitialValue(), null);
        }

        SEGA ega;
        //Create center in flower automaton
        mmEfa.addState(SEFA.SINGLE_LOCATION_NAME, true, true);

        for (final SopNode node : iOperationSet.getFirstNodesInSequencesAsSet()) {
            if (!(node instanceof SopNodeOperation)) {
                System.out.println("Node: " + node.typeToString() + " not an operation!");
                return null;
            }
            final OperationData opData = node.getOperation();
            final int id = opData.getId();
            final String varName = Type.OPERATION_VARIABLE_PREFIX.toString() + id;


            if (parallelOps.contains(varName)){
                mmModule.addIntVariable(varName, -1, -1, -1, null);
            } else {
            
                //Add integer variable for operation---------------------------------
                Integer marking = null;
                if (new SopNodeToolboxSetOfOperations().getOperations(iHasToFinishSet, false).contains(opData)) {
                    marking = 2;
                }
                mmModule.addIntVariable(varName, 0, 2, 0, marking);
                //-------------------------------------------------------------------



                //Add transition to start execute operation--------------------------
                ega = new SEGA(Type.EVENT_PREFIX.toString() + id + Type.EVENT_UP.toString());
                ega.andGuard(varName + "==0");
                ega.addCondition(opData, ConditionType.PRE, Type.LOOK_FOR_GUARD, mConditionsToInclude);
                ega.addCondition(opData, ConditionType.PRE, Type.LOOK_FOR_ACTION, mConditionsToInclude);
                ega.addAction(varName + "=1");
                mmEfa.addStandardSelfLoopTransition(ega);
                //-------------------------------------------------------------------

                //Add transition to finish execute operation-------------------------
                ega = new SEGA(Type.EVENT_PREFIX.toString() + id + Type.EVENT_DOWN.toString());
                ega.andGuard(varName + "==1");
                ega.addCondition(opData, ConditionType.POST, Type.LOOK_FOR_GUARD, mConditionsToInclude);
                ega.addCondition(opData, ConditionType.POST, Type.LOOK_FOR_ACTION, mConditionsToInclude);
                ega.addAction(varName + "=2");
                mmEfa.addStandardSelfLoopTransition(ega);
                //-------------------------------------------------------------------
            }
        }

        return mmModule.getModuleSubject();
    }
    
    

    @Override
    public Map<String, Set<String>> getStateSpaceForEventSetMap(final Automaton iAutomaton) {

        final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        int nr = 0;
        //Loop states and events to fill map-------------------------------------
        for (final StateProxy sp : iAutomaton.getStates()) {
            String stateName = sp.getName();
            if(nr%1000==0) {
                GUIView.printToConsole("On state " + nr + " in state space");
            }
            ++nr;
            for (final Arc arc : iAutomaton.getStateWithName(stateName).getOutgoingArcs()) {
                //Remove the single EFA location "pm"
                stateName = stateName.replaceAll("." + SEFA.SINGLE_LOCATION_NAME, "").replaceAll(SEFA.SINGLE_LOCATION_NAME + ".", "");
                String eventName = arc.getLabel();
                if (eventName.contains(Type.EVENT_UP.toString())) {
                    eventName = eventName.substring(0, eventName.indexOf(Type.EVENT_UP.toString()) + 2); //To handle addition of suffix for events when disjunction
                } else {//EVENT_DOWN
                    eventName = eventName.substring(0, eventName.indexOf(Type.EVENT_DOWN.toString()) + 4);
                }
                if (!map.keySet().contains(eventName)) {
                    map.put(eventName, new HashSet<String>());
                }
                map.get(eventName).add(stateName);
            }
        }//----------------------------------------------------------------------
        System.out.println("getStateSpaceForEventSetMap finished");
        return map;
    }

    @Override
    public Automaton synthesize(Automata iAutomata) {
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
                GUIView.printToConsole("Error in synthesis. I can not go on!");
                System.out.println(e.toString());
            }
        }
        return null;
    }

    @Override
    public boolean saveSupervisorAsWmodFile(String iFilePath) {
        return mmModule.saveToWMODFile(iFilePath);
    }
}
