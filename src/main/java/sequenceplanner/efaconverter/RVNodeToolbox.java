package sequenceplanner.efaconverter;

import sequenceplanner.algorithms.visualization.RelateTwoOperations;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * THIS CLASS SHOULD BE REMOVED!!!
 * Methods to enhance a tree built up from {@link RVNode}s.
 * @author patrik
 */
public class RVNodeToolbox {

    public static final String OPERATION = "operation";
    public static final String HIERARCHY = "sop";
    public final RVNode mRoot = new RVNode();
    public Set<RVNode> mAllNodes = new HashSet<RVNode>();
    HashMap<String, Set<String>> mEventStateSetMap = null;
    String mStateNameExplanation = "";

    public RVNodeToolbox() {
    }

    /**
     * Generic method to create a new node
     * @param iNodeType
     * @param iParent
     * @return
     */
    public RVNode createNode(String iNodeType, RVNode iParent) {
        //Create new node
        RVNode newNode = new RVNode();
        //Add to set for all nodes
        mAllNodes.add(newNode);
        //set parent
        newNode.mParent = iParent;
        //set node type;
        newNode.mNodeType = iNodeType;
        //set child relation
        iParent.mChildren.add(newNode);

        return newNode;
    }

    /**
     * Used when operations are added to tree first time.<br/>
     * All new nodes has mRoot as parent.
     * @param iOpNode the new {@link RVNode} built on this {@link OpNode}
     * @return the created node as {@link RVNode}
     */
    public RVNode createOperationNode(OpNode iOpNode) {
        //Create new node
        RVNode newNode = createNode(OPERATION, mRoot);
        //Set OpNode link
        newNode.mOpNode = iOpNode;

        return newNode;
    }

    public void fillOperationRelations() {
        RelateTwoOperations rto = new RelateTwoOperations();
        for(RVNode externalOp : mRoot.mChildren) {
            for(RVNode internalOp : mRoot.mChildren) {
                //externalOp.mOperationRelationMap.put(internalOp, externalOp.getRelationToNode(internalOp));
//                rto.setOperationPair(externalOp, internalOp);
                externalOp.mOperationRelationMap.put(internalOp, rto.getOperationRelation());
            }
        }
    }

    /**
     * Loops all children to mRoot.<br/>
     * Finds in what locations for other operations the events of an operation can take place.<br/>
     * This info is added to the field mEventOperationLocationSetMap for each RVNode in mRoot.mChildren.
     */
    public void findEventOperationRelations() {
        //Create a map between the serial order of an operation in the state name and it's id.
        HashMap<Integer, RVNode> serialnrOperationMap = new HashMap<Integer, RVNode>();
        final String[] operationNames = mStateNameExplanation.split("\\|\\|");
        for (int i = 0; i < operationNames.length; ++i) {
            final String operationId = operationNames[i].replaceAll("o", "");
            final RVNode rvNode = mRoot.getChildWithStringId(operationId);
            serialnrOperationMap.put(i, rvNode);
        }

        //Loop all events to find what operation locations that are present
        for (String key : mEventStateSetMap.keySet()) {
            Set<String> stateNameSet = mEventStateSetMap.get(key);

            //Init of map where result is stored, add to RVNode (operation)
            HashMap<RVNode, Set<String>> opLocationSetMap = getOperationLocationSetMapForEvent(key);
            for (RVNode operation : serialnrOperationMap.values()) {
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
     * Get a pointer to HashMap the describes all possible locations (value) <br/>
     * forall operations (keyset), where this event can happen.
     * @param iKey an event (includes operation id and event type "up" or "down")
     * @return pointer to set the describes all operations locations when this event can happen
     */
    private HashMap<RVNode, Set<String>> getOperationLocationSetMapForEvent(String iKey) {
        //Work with name
        iKey = iKey.replaceAll("e", "");
        String eventType = "down";
        if (iKey.contains("up")) {
            eventType = "up";
        }
        final String operationId = iKey.replaceAll(eventType, "");

        //Create storage for event
        final RVNode rvNode = mRoot.getChildWithStringId(operationId);
        rvNode.mEventOperationLocationSetMap.put(eventType, new HashMap<RVNode, Set<String>>());

        return rvNode.mEventOperationLocationSetMap.get(eventType);
    }
}
