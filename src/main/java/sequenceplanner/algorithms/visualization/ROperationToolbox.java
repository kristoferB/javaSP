package sequenceplanner.algorithms.visualization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.data.OperationData;

/**
 * Toolbox to use when working with {@link IROperation}s
 * @author patrik
 */
public class ROperationToolbox {

    private Set<IROperation> mRelationOperationSet = new HashSet<IROperation>(); //Operations where relations should be found
    private Map<String, Set<String>> mEventStateSetMap = new HashMap<String, Set<String>>();
    private String mStateNameExplanation = "";

    public ROperationToolbox() {
    }

    public void addToRelationOperationSet(final OperationData iOpData) {
        IROperation rOp = new ROperation(iOpData);
        getmRelationOperationSet().add(rOp);

        final int id = iOpData.getId();

        //Look for events to this operaiton in supervisor automaton.
        mEventStateSetMap.put(ISupremicaInteractionForVisualization.EVENT_PREFIX + id + ISupremicaInteractionForVisualization.EVENT_UP, new HashSet<String>());
        mEventStateSetMap.put(ISupremicaInteractionForVisualization.EVENT_PREFIX + id + ISupremicaInteractionForVisualization.EVENT_DOWN, new HashSet<String>());
    }

    public IROperation getROperationWithStringId(final String iId) {
        for (final IROperation iROp : getmRelationOperationSet()) {
            if (iId.equals(iROp.getIdAsString())) {
                return iROp;
            }
        }
        return null;
    }

    public Map<String, Set<String>> getmEventStateSetMap() {
        return mEventStateSetMap;
    }

    public String getmStateNameExplanation() {
        return mStateNameExplanation;
    }

    public void setmStateNameExplanation(String mStateNameExplanation) {
        this.mStateNameExplanation = mStateNameExplanation;
    }

    public Set<IROperation> getmRelationOperationSet() {
        return mRelationOperationSet;
    }

    public void fillOperationRelations() {
        IRelateTwoOperations rto = new RelateTwoOperations();
        for (final IROperation externalOp : getmRelationOperationSet()) {
            for (final IROperation internalOp : getmRelationOperationSet()) {
                rto.setOperationPair(externalOp, internalOp);
                AROperation rOp = (AROperation) externalOp;
                if (rOp.getmOperationRelationMap() == null) {
                    rOp.setmOperationRelationMap(new HashMap<IROperation, Integer>());
                }
                rOp.getmOperationRelationMap().put(internalOp, rto.getOperationRelation());
            }
        }
    }

    /**
     * Loops all children to mRoot.<br/>
     * Finds in what locations for other operations the events of an operation can take place.<br/>
     * This info is added to the field mEventOperationLocationSetMap for each RVNode in mRoot.mChildren.
     */
    public void findEventOperationRelations() {
        //Create a map between the order of an operation in the state name and it's id.
        Map<Integer, IROperation> serialnrOperationMap = new HashMap<Integer, IROperation>();
        final String[] operationNames = mStateNameExplanation.split("\\|\\|");
        for (int i = 0; i < operationNames.length; ++i) {
            final String operationId = operationNames[i].replaceAll(ISupremicaInteractionForVisualization.OPERATION_VARIABLE_PREFIX, "");
            final IROperation iROp = getROperationWithStringId(operationId);
            serialnrOperationMap.put(i, iROp);
        }

        //Loop all events to find what operation locations that are present
        for (String key : mEventStateSetMap.keySet()) {
            final Set<String> stateNameSet = mEventStateSetMap.get(key);

            //Init of map where result is stored, add to IROperation (operation)
            Map<IROperation, Set<String>> opLocationSetMap = getOperationLocationSetMapForEvent(key);
            for (final IROperation operation : serialnrOperationMap.values()) {
                opLocationSetMap.put(operation, new HashSet<String>());
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

    /**
     * Get a pointer to Map that describes all possible locations (value) <br/>
     * forall operations (keyset), where this event can happen.
     * @param iKey an event (includes operation id and event type "up" or "down")
     * @return pointer to set the describes all operations locations when this event can happen
     */
    private Map<IROperation, Set<String>> getOperationLocationSetMapForEvent(String iKey) {
        //Work with name
        iKey = iKey.replaceAll(ISupremicaInteractionForVisualization.EVENT_PREFIX, "");
        String eventType = ISupremicaInteractionForVisualization.EVENT_DOWN;
        if (iKey.contains(ISupremicaInteractionForVisualization.EVENT_UP)) {
            eventType = ISupremicaInteractionForVisualization.EVENT_UP;
        }
        final String operationId = iKey.replaceAll(eventType, "");

        //Create storage for event
        final AROperation rOp = (AROperation) getROperationWithStringId(operationId);
        if (rOp.getmEventOperationLocationSetMap() == null) {
            rOp.setmEventOperationLocationSetMap(new HashMap<String, Map<IROperation, Set<String>>>(2));
        }
        rOp.getmEventOperationLocationSetMap().put(eventType, new HashMap<IROperation, Set<String>>());

        return rOp.getmEventOperationLocationSetMap().get(eventType);
    }
}
