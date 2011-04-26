package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.supremica.automata.Automaton;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * Translates information in an {@link Automaton} to a {@link IRelationContainer}.<br/>
 * @author patrik
 */
public class RelationIdentification {

    private Map<String, Set<String>> mEventStateSetMap = new HashMap<String, Set<String>>();
    private String mStateNameExplanation = "";
    private Automaton mAutomaton = null;
    private IRelationContainer mRC = null;

    public RelationIdentification(final Automaton iAutomaton, final RelationContainer iRelationContainer, final Map<String, Set<String>> iEventStateSetMap) {
        this.mAutomaton = iAutomaton;
        this.mEventStateSetMap = iEventStateSetMap;
        this.mRC = iRelationContainer;
    }

    public boolean run() {
        initStateName();
        initEventOperationLocationSetMapForRelationContainer();
        if (!findEventOperationRelations()) {
            return false;
        }
        initOperationRelationMapForRelationContainer();
        fillOperationRelations();
        return true;
    }

    /**
     * Loops operations in subset and adds two events per operation to a map in {@link RelationContainer} object.
     */
    private void initEventOperationLocationSetMapForRelationContainer() {
        Set<OperationData> setToLoop = new SopNodeToolboxSetOfOperations().getOperations(mRC.getOsubsetSopNode(), false);
        Map<OperationData, Map<String, Map<OperationData, Set<String>>>> map = new HashMap<OperationData, Map<String, Map<OperationData, Set<String>>>>();
        for (final OperationData opData : setToLoop) {

            final Map<String, Map<OperationData, Set<String>>> eventMap = new HashMap<String, Map<OperationData, Set<String>>>();
            eventMap.put(ISupremicaInteractionForVisualization.EVENT_UP, new HashMap<OperationData, Set<String>>());
            eventMap.put(ISupremicaInteractionForVisualization.EVENT_DOWN, new HashMap<OperationData, Set<String>>());

            map.put(opData, eventMap);
        }
        mRC.setEventOperationLocationSetMap(map);
    }

    private void initStateName() {
        //Remove Single EFA from automaton name (the name is Single) + extra substrings
        //From sup(oX||oY||Single) -> oX||oY
        mStateNameExplanation = mAutomaton.getName().replaceAll("sup\\(", "").replaceAll("\\)", "");
        mStateNameExplanation = mStateNameExplanation.replaceAll("\\|\\|" + ISupremicaInteractionForVisualization.BIG_FLOWER_EFA_NAME + "\\|\\|", "\\|\\|").
                replaceAll("\\|\\|" + ISupremicaInteractionForVisualization.BIG_FLOWER_EFA_NAME, "").
                replaceAll(ISupremicaInteractionForVisualization.BIG_FLOWER_EFA_NAME + "\\|\\|", "");
    }

    private void fillOperationRelations() {
        IRelateTwoOperations rto = new RelateTwoOperations();
        Set<OperationData> setToLoop = new SopNodeToolboxSetOfOperations().getOperations(mRC.getOsubsetSopNode(), false);
        for (final OperationData externalOp : setToLoop) {
            for (final OperationData internalOp : setToLoop) {
                rto.setOperationPair(mRC, externalOp, internalOp);
                mRC.getOperationRelationMap(externalOp).put(internalOp, rto.getOperationRelation());
            }
        }
    }

    private void initOperationRelationMapForRelationContainer() {
        Set<OperationData> setToLoop = new SopNodeToolboxSetOfOperations().getOperations(mRC.getOsubsetSopNode(), false);
        Map<OperationData, Map<OperationData, Integer>> returnMap = new HashMap<OperationData, Map<OperationData, Integer>>();
        for (final OperationData opData : setToLoop) {
            returnMap.put(opData, new HashMap<OperationData, Integer>());
        }
        mRC.setOperationRelationMap(returnMap);
    }

    private OperationData getOperationWithStringId(final String iId) {
        Set<OperationData> setToLoop = new SopNodeToolboxSetOfOperations().getOperations(mRC.getOsubsetSopNode(), false);
        for (final OperationData opData : setToLoop) {
            if (iId.equals(Integer.toString(opData.getId()))) {
                return opData;
            }
        }
        return null;
    }

    /**
     * Loops all children to mRoot.<br/>
     * Finds in what locations for other operations the events of an operation can take place.<br/>
     * This info is added to the field mEventOperationLocationSetMap for each RVNode in mRoot.mChildren.
     */
    public boolean findEventOperationRelations() {
        //Create a map between the order of an operation in the state name and it's id.
        Map<Integer, OperationData> serialnrOperationMap = new HashMap<Integer, OperationData>();
        final String[] operationNames = mStateNameExplanation.split("\\|\\|");
        for (int i = 0; i < operationNames.length; ++i) {
            final String operationName = operationNames[i];
            final String operationId = operationName.replaceAll(ISupremicaInteractionForVisualization.OPERATION_VARIABLE_PREFIX, "");
            final OperationData iOpData = getOperationWithStringId(operationId);
            serialnrOperationMap.put(i, iOpData);
        }

        //Loop events of interest to find what operation locations that are present
        Set<OperationData> opDataSet = new SopNodeToolboxSetOfOperations().getOperations(mRC.getOsubsetSopNode(), false);
        for (final OperationData opDataExternal : opDataSet) {
            Set<String> eventSet = mRC.getEventOperationLocationSetMap(opDataExternal).keySet();
            for (final String eventType : eventSet) {
                final Integer id = opDataExternal.getId();
                String eventToLookFor = ISupremicaInteractionForVisualization.EVENT_PREFIX + id + eventType;
                if (!mEventStateSetMap.containsKey(eventToLookFor)) {
                    System.out.println("Mismatch between events in supervisor and subset!");
                    return false;
                }
                final Set<String> stateNameSet = mEventStateSetMap.get(eventToLookFor);

                //Init of map where result is stored
                Map<OperationData, Set<String>> opLocationSetMap =
                        mRC.getEventOperationLocationSetMap(opDataExternal).get(eventType);
                for (final OperationData opData : serialnrOperationMap.values()) {
                    opLocationSetMap.put(opData, new HashSet<String>());
                }

                //Loop all states for event and store locations for each operation
                for (String stateName : stateNameSet) {
                    final String[] opLocations = stateName.split("\\.");
                    for (int i = 0; i < opLocations.length; ++i) {
                        final String opLocation = opLocations[i];
                        opLocationSetMap.get(serialnrOperationMap.get(i)).add(opLocation);
                    }
                }
            }
        }
        return true;
    }
}
