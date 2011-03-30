package sequenceplanner.multiProduct;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.apache.log4j.Logger;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author patrik
 */
public class EFAforSupervisor {

    private static Logger log = Logger.getLogger(EFAforTransport.class);
    private Error e = new Error(EFAforTransport.class.toString());
    private SModule module = new SModule("Supervisor module");
    private HashMap<String, HashSet<String>> productTypesMap = new HashMap<String, HashSet<String>>();
    private HashMap<String, HashMap<String, String>> productLimitMap = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, HashMap<String, String>> operationPropertiesMap = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, HashMap<String, Set<String>>> allPosPropertiesMap = new HashMap<String, HashMap<String, Set<String>>>();
    private HashSet<String> moversSet = new HashSet<String>();
    private HashMap<String, HashMap<String, Set<String>>> mergeMap = new HashMap<String, HashMap<String, Set<String>>>();
    final static String SOURCE_POS = "sourcePos";
    final static String DEST_POS = "destPos";
    final static String BETWEEN_POS = "betweenPos";
    final static String SOURCE_POS_BASE = "sourcePosBase";
    final static String DEST_POS_BASE = "destPosBase";
    final static String PRODUCT = "product";
    final static String OP_TYPE = "opType";
    final static String IN = "in";
    final static String BY = "by";
    final static String CAPACITY = "capacity";
    boolean singleEFA = false;
    boolean uniqueTransitionNames = true;

    public EFAforSupervisor(Model model) {

        //Build up internal model of operations in SP for this class
        getOperations(model.getOperationRoot());
        if (productTypesMap.isEmpty()) {
            e.error("No products exist!");
        }
        if (operationPropertiesMap.isEmpty()) {
            e.error("Some products lack operations!");
        }

        //Build up internal model of resources in SP for this class
        getPositions(model.getResourceRoot());
        if (allPosPropertiesMap.isEmpty()) {
            e.error("No positions are given!");
        }

        if (e.noErrors()) {
            //Let user select products
            new SelectProductsDialog();
        } else {
            e.printErrorList();
        }
    }

    private void getOperations(TreeNode tree) {
        //Loop all child operations to tree
        for (int i = 0; i < tree.getChildCount(); ++i) {
            String treeName = tree.getNodeData().getName().replaceAll("_for_synthesis", "");
            OperationData opData = (OperationData) tree.getChildAt(i).getNodeData();

            if (opData.getName().contains("_for_synthesis")) {
                //Add a new product
                String productName = opData.getName().replaceAll("_for_synthesis", "");
                if (productTypesMap.containsKey(productName)) {
                    e.error(opData.getName() + " occurs more than once! I will not go on...");
                } else {
                    productTypesMap.put(productName, new HashSet<String>());
                    getOperations(tree.getChildAt(i));
                }
            } else if (productTypesMap.containsKey(treeName)) {
                //Get operation data
                HashMap<String, String> op = new HashMap<String, String>(6);
                op.put(PRODUCT, treeName);
                setOperationData(op, opData.getName());

                //Add a operation to product and store operation data
                productTypesMap.get(treeName).add(opData.getName());
                operationPropertiesMap.put(opData.getName(), op);
            }

        }
    }

    private void setOperationData(HashMap<String, String> op, String opName) {
        if (opName.split(TypeVar.SEPARATION).length == 3) {
            op.put(SOURCE_POS, adaptPosition(opName.split(TypeVar.SEPARATION)[0]));
            op.put(SOURCE_POS_BASE, opName.split(TypeVar.SEPARATION)[0].split(":")[1]);
            op.put(OP_TYPE, opName.split(TypeVar.SEPARATION)[1]);
            op.put(DEST_POS, adaptPosition(opName.split(TypeVar.SEPARATION)[2]));
            op.put(DEST_POS_BASE, opName.split(TypeVar.SEPARATION)[2].split(":")[1]);
            op.put(BETWEEN_POS, op.get(SOURCE_POS) + "b" + op.get(DEST_POS));

        } else {
            e.error("Operation " + opName + " is not following the name convention! I will not go on.");
        }
    }

    /**
     * Only store positions that are real
     * @param position To test
     * @return <i>position</i> if position is real else <i>""</i>
     */
    private String adaptPosition(String position) {
        if (position.equals(TypeVar.POS_OUT)) {
            position = "";
        } else if (position.equals(TypeVar.POS_MERGE)) {
            position = "";
        }
        return position;
    }

    private void getPositions(TreeNode tree) {
        //Loop all child operations in tree
        for (int i = 0; i < tree.getChildCount(); ++i) {
            TreeNode node = tree.getChildAt(i);
            String parentName = node.getParent().getNodeData().getName();

            if (Model.isResource(node.getNodeData())) {
                if (parentName.equals(TypeVar.SP_RESOURCE_POSITIONS)) {
                    //Positions
                    ResourceData data = (ResourceData) node.getNodeData();
                    if (allPosPropertiesMap.put(data.getName(), new HashMap<String, Set<String>>(4)) != null) {
                        e.error("Position " + data.getName() + " is given again. Remove all except one!");
                    }
                    //init position properties
                    allPosPropertiesMap.get(data.getName()).put(IN, new HashSet<String>());
                    allPosPropertiesMap.get(data.getName()).put(BY, new HashSet<String>());
                    allPosPropertiesMap.get(data.getName()).put(CAPACITY, new HashSet<String>(1));
                    allPosPropertiesMap.get(data.getName()).get(CAPACITY).add("1");
                    allPosPropertiesMap.get(data.getName()).put("atInit", new HashSet<String>(1));
                    allPosPropertiesMap.get(data.getName()).get("atInit").add("0");
                }
                getPositions(node);
            } else if (Model.isVariable(node.getNodeData())) {
                ResourceVariableData data = (ResourceVariableData) node.getNodeData();

                if (data.getName().equals(TypeVar.SP_RESOURCE_CAPACITY)) {
                    //Capacity other then default value 1
                    allPosPropertiesMap.get(parentName).get(CAPACITY).clear();
                    allPosPropertiesMap.get(parentName).get(CAPACITY).add(Integer.toString(data.getMax()));
                } else if (data.getName().equals("Mover")) {
                    //Movers
                    //Capacity is set to capacity for mover
                    moversSet.add(parentName);
                    allPosPropertiesMap.get(parentName).get(CAPACITY).clear();
                    allPosPropertiesMap.get(parentName).get(CAPACITY).add(Integer.toString(data.getMax()));
                } else if (data.getName().equals(TypeVar.SP_RESOURCE_LIMIT)) {
                    //Limit for product
                    if (productLimitMap.containsKey(parentName)) {
                        e.error("Product " + parentName + " can't start in more than one position!");
                    } else {
                        productLimitMap.put(parentName, new HashMap<String, String>(3));
                        productLimitMap.get(parentName).put("position", tree.getParent().getNodeData().getName());
                        productLimitMap.get(parentName).put(TypeVar.SP_RESOURCE_LIMIT, Integer.toString(data.getMax()));
                        productLimitMap.get(parentName).put("variableName", parentName + TypeVar.SEPARATION + TypeVar.SP_RESOURCE_LIMIT);

                        //Initial values for position is modified if product starts in position
                        int currentStartValue = Integer.parseInt(allPosPropertiesMap.get(tree.getParent().getNodeData().getName()).get("atInit").iterator().next());
                        allPosPropertiesMap.get(tree.getParent().getNodeData().getName()).get("atInit").clear();
                        allPosPropertiesMap.get(tree.getParent().getNodeData().getName()).get("atInit").add(Integer.toString(currentStartValue + data.getMax()));
                        //Check that modification is within capacity given for position
                        if (Integer.parseInt(allPosPropertiesMap.get(tree.getParent().getNodeData().getName()).get("atInit").iterator().next()) >
                                Integer.parseInt(allPosPropertiesMap.get(tree.getParent().getNodeData().getName()).get(CAPACITY).iterator().next())) {
                            e.error("Bad relation between nbr of products that start in position " + productLimitMap.get(parentName).get("position") + " and it's capacity!");
                        }

                    }
                }
            }
        }
    }

    private class CollectInfomation {

        public CollectInfomation() {
            getMergeOperations();

            System.out.println("Merge-----------------------------");
            for (String merge : mergeMap.keySet()) {
                System.out.println(merge + " " + mergeMap.get(merge));
            }
            System.out.println("------------------------------------");

            getPositionsFromOperations();

            System.out.println("Operations--------------------------------------------");
            for (String operation : operationPropertiesMap.keySet()) {
                System.out.println(operation + " " + operation.toString());
            }
            System.out.println("------------------------------------------------------");

            System.out.println("Positions--------------------------");
            for (String pos : allPosPropertiesMap.keySet()) {
                System.out.println(pos + " " + allPosPropertiesMap.get(pos));
            }
            System.out.println("-------------------------------------");

            if (e.noErrors()) {
                new BuildModule();
            } else {
                e.printErrorList();
            }

        }

        /**
         * Collect the common merge operations<br/>
         * Merge operations are put in hashmap <i>mergeMap</i>
         */
        private void getMergeOperations() {
            for (String operation : operationPropertiesMap.keySet()) {
                HashMap<String, String> op = operationPropertiesMap.get(operation);
                if (op.get(OP_TYPE).contains(TypeVar.ED_MERGE)) {
                    String mergeName = op.get(OP_TYPE);
                    mergeName = mergeName.substring(mergeName.indexOf(TypeVar.ED_MERGE));
                    mergeName = mergeName.replaceAll(TypeVar.ED_MERGE, "");
                    op.put(OP_TYPE, mergeName);
                    if (!mergeMap.containsKey(mergeName)) {
                        mergeMap.put(mergeName, new HashMap<String, Set<String>>(5));
                        mergeMap.get(mergeName).put(SOURCE_POS, new HashSet<String>());
                        mergeMap.get(mergeName).put(DEST_POS, new HashSet<String>());
                        mergeMap.get(mergeName).put("sourceForLimit", new HashSet<String>()); //For limit guard/action
                        mergeMap.get(mergeName).put("destForLimit", new HashSet<String>()); //For limit guard/action
                        mergeMap.get(mergeName).put(BETWEEN_POS, new HashSet<String>());
                        mergeMap.get(mergeName).get(BETWEEN_POS).add(mergeName + "b");
                    }

                    if (!op.get(SOURCE_POS).contains(TypeVar.POS_OUT) && !op.get(SOURCE_POS).contains(TypeVar.POS_MERGE)) {
                        mergeMap.get(mergeName).get(SOURCE_POS).add(op.get(SOURCE_POS));
                        allPosPropertiesMap.get(op.get(SOURCE_POS_BASE)).get(BY).add(mergeMap.get(mergeName).get(BETWEEN_POS).iterator().next());
                    }
                    if (!op.get(DEST_POS).contains(TypeVar.POS_OUT) && !op.get(DEST_POS).contains(TypeVar.POS_MERGE)) {
                        mergeMap.get(mergeName).get(DEST_POS).add(op.get(DEST_POS));
                        allPosPropertiesMap.get(op.get(DEST_POS_BASE)).get(BY).add(mergeMap.get(mergeName).get(BETWEEN_POS).iterator().next());
                    }

                    mergeMap.get(mergeName).get("sourceForLimit").add(op.get(PRODUCT) + ":" + op.get(SOURCE_POS_BASE)); //For limit guard/action
                    mergeMap.get(mergeName).get("destForLimit").add(op.get(PRODUCT) + ":" + op.get(DEST_POS_BASE)); //For limit guard/action
                }
            }

            //Remove out and mrg positions from sets
            for (String mergeName : mergeMap.keySet()) {
                for (Set set : mergeMap.get(mergeName).values()) {
                    set.remove(TypeVar.POS_OUT);
                    set.remove(TypeVar.POS_MERGE);
                }
            }
        }

        /**
         * Go through each operation's positions to position set.<br/>
         * This gives a relation between products and positions.<br/>
         * These relations are later used to construct guards.
         */
        private void getPositionsFromOperations() {
            for (String operation : operationPropertiesMap.keySet()) {
                HashMap<String, String> op = operationPropertiesMap.get(operation);

                if (op.get(OP_TYPE).equals(TypeVar.TRANSPORT)) {
                    //source pos
                    addToPositionMap(op.get(SOURCE_POS_BASE), IN, op.get(SOURCE_POS));

                    //dest pos
                    addToPositionMap(op.get(DEST_POS_BASE), IN, op.get(DEST_POS));

                    //between pos
                    addToPositionMap(op.get(SOURCE_POS_BASE), BY, op.get(BETWEEN_POS));
                    addToPositionMap(op.get(DEST_POS_BASE), BY, op.get(BETWEEN_POS));

                    //Add to mover positions (robot) if operation is a transport operation
                    if (op.get(OP_TYPE).equals(TypeVar.TRANSPORT)) {
                        for (String moverPos : moversSet) {
                            addToPositionMap(moverPos, IN, op.get(BETWEEN_POS));
                        }
                    }
                }
            }
        }

        private void addToPositionMap(String key, String where, String positionInstance) {
            if (allPosPropertiesMap.containsKey(key)) {
                if (key.equals(TypeVar.POS_OUT) || key.equals(TypeVar.POS_MERGE)) {
                    allPosPropertiesMap.get(key).get(where).add("");
                } else if (!positionInstance.contains(TypeVar.POS_OUT) && !positionInstance.contains(TypeVar.POS_MERGE)) {
                    allPosPropertiesMap.get(key).get(where).add(positionInstance);
                }
            } else if (!key.equals(TypeVar.POS_MERGE)) {
                e.error("An operation contains position " + key + ", this position is not given in resource tree. I will not go on.");
            }
        }
    }

    private class BuildModule {

        HashMap<String, SEFA> productEFAMap = new HashMap<String, SEFA>();

        public BuildModule() {
            setEFA();

            addVariables();

            addTransitions();

            module.setComment("Module for supervisor synthesis\n*****\nSynthesize supervisor through guard extraction\n");

            module.DialogAutomataTransitions();
        }

        /**
         * Single or multiple efas are created.<br/>
         * The user decides through initial dialog.
         */
        private void setEFA() {
            if (singleEFA) {
                SEFA singleEFA = new SEFA("Single", module);
                singleEFA.addState(TypeVar.LOCATION, true, true);
                for (String product : productTypesMap.keySet()) {
                    productEFAMap.put(product, singleEFA);
                }
                productEFAMap.put("extraEFA", singleEFA);
            } else {
                for (String product : productTypesMap.keySet()) {
                    productEFAMap.put(product, new SEFA(product, module));
                    productEFAMap.get(product).addState(TypeVar.LOCATION, true, true);
                }
                productEFAMap.put("extraEFA", new SEFA("Merge" + "And" + TypeVar.SP_RESOURCE_LIMIT, module));
                productEFAMap.get("extraEFA").addState(TypeVar.LOCATION, true, true);
            }
        }

        private String lumpSetWith(Set<String> set, String item) {
            String guard = "";
            for (String pos : set) {
                if (!guard.equals("")) {
                    guard += item;
                }
                guard += pos;
            }
            return guard;
        }

        private String lumpSetWith(Set<String> set, String item, Set<String> illegalPos) {
            String guard = "";
            Set<String> tempSet = new HashSet<String>(set);

            tempSet.removeAll(illegalPos);

            for (String pos : tempSet) {
                if (!guard.equals("")) {
                    guard += item;
                }
                guard += pos;
            }
            return guard;
        }

        private void addVariables() {
            //allPosPropertiesMap------------------------------------------------
            for (String pos : allPosPropertiesMap.keySet()) {
                for (String var : allPosPropertiesMap.get(pos).get(IN)) {
                    module.addIntVariable(var, 0, Integer.parseInt(allPosPropertiesMap.get(pos).get(CAPACITY).iterator().next()),
                            Integer.parseInt(allPosPropertiesMap.get(pos).get("atInit").iterator().next()),
                            Integer.parseInt(allPosPropertiesMap.get(pos).get("atInit").iterator().next()));
                }
            }//------------------------------------------------------------------

            //productLimitMap----------------------------------------------------
            for (String product : productLimitMap.keySet()) {
                module.addIntVariable(productLimitMap.get(product).get("variableName"), 0,
                        Integer.parseInt(productLimitMap.get(product).get(TypeVar.SP_RESOURCE_LIMIT)), 0, 0);
            }//------------------------------------------------------------------

            //mergeMap-----------------------------------------------------------
            //Between positions used during merge operations
            Set<String> mergeVariables = new HashSet<String>();
            for (String merge : mergeMap.keySet()) {
                mergeVariables.add(mergeMap.get(merge).get(BETWEEN_POS).iterator().next());
            }
            for (String var : mergeVariables) {
                module.addIntVariable(var, 0, 1, 0, 0);
            }//------------------------------------------------------------------
        }

        private void addTransitions() {
            for (String operation : operationPropertiesMap.keySet()) {
                HashMap<String, String> op = operationPropertiesMap.get(operation);

                if (op.get(OP_TYPE).equals(TypeVar.TRANSPORT)) {
                    //Transport operations
                    addTransportTransitions(operation);
                } else if (productLimitMap.containsKey(op.get(PRODUCT)) && !mergeMap.containsKey(op.get(OP_TYPE))) {
                    //Extra transitions to set/reset product limit is needed
                    //(set/reset of limit is not captured neither with a transport transition nor with a merge transition)
                    if (productLimitMap.get(op.get(PRODUCT)).get("position").equals(op.get(SOURCE_POS_BASE))) {
                        //Need to add extra transition since set of limit is not covered with transport operations and merge operations
                        addLimitTransitions(operation, 0);
                    } else if (productLimitMap.get(op.get(PRODUCT)).get("position").equals(op.get(DEST_POS_BASE))) {
                        //Need to add extra transition since reset of limit is not covered with transport operations and merge operations
                        addLimitTransitions(operation, 1);
                    }
                }
            }
            //Merge operations
            for (String mergeOP : mergeMap.keySet()) {
                addMergeTransitions(mergeOP);
            }
        }

        /**
         * Create transitions to when not enough with transport and merge transitions to include limit requirement op products.
         * @param operation Key for hashMap <i>operationPropertiesMap</i>
         * @param positionType 0-set limit, 1-reset limit
         */
        private void addLimitTransitions(String operation, int positionType) {
            HashMap<String, String> op = operationPropertiesMap.get(operation);
            String eventName = "";
            SEFA efa = productEFAMap.get("extraEFA");
            SEGA ega = null;

            switch (positionType) {
                case 0:
                    //Transition source pos -> dest pos--------------------------
                    eventName = op.get(DEST_POS) + TypeVar.SEPARATION + "L";
                    if (uniqueTransitionNames) {
                        ega = new SEGA(eventName + TypeVar.SEPARATION + "01");
                    } else {
                        ega = new SEGA(eventName);
                    }

                    //Book and unbook local position
                    ega.addBasicPositionBookAndUnbook("", op.get(DEST_POS));

                    //Check all positions that are related to dest position
                    Set<String> guard = new HashSet<String>(2);
                    guard.add(lumpSetWith(allPosPropertiesMap.get(op.get(DEST_POS_BASE)).get(IN), "+"));
                    guard.add(lumpSetWith(allPosPropertiesMap.get(op.get(DEST_POS_BASE)).get(BY), "+"));

                    if (lumpSetWith(guard, "+").length() > "".length()) {
                        ega.andGuard("(" + lumpSetWith(guard, "+") + ")" + TypeVar.EFA_STRICTLY_LESS_THAN +
                                allPosPropertiesMap.get(op.get(DEST_POS_BASE)).get(CAPACITY).iterator().next());
                    }

                    //Add limit if needed
                    if (productLimitMap.containsKey(op.get(PRODUCT))) {
                        if (op.get(SOURCE_POS_BASE).equals(productLimitMap.get(op.get(PRODUCT)).get("position"))) {
                            ega.andGuard(productLimitMap.get(op.get(PRODUCT)).get("variableName") + TypeVar.EFA_STRICTLY_LESS_THAN +
                                    productLimitMap.get(op.get(PRODUCT)).get(TypeVar.SP_RESOURCE_LIMIT));
                            ega.addAction(productLimitMap.get(op.get(PRODUCT)).get("variableName") + TypeVar.EFA_PLUS_ONE);
                        }
                    }
                    break;
                case 1:
                    //Transition source pos -> dest pos--------------------------
                    eventName = op.get(SOURCE_POS) + TypeVar.SEPARATION + "L";
                    if (uniqueTransitionNames) {
                        ega = new SEGA(eventName + TypeVar.SEPARATION + "12");
                    } else {
                        ega = new SEGA(eventName);
                    }

                    //Book and unbook local position
                    ega.addBasicPositionBookAndUnbook(op.get(SOURCE_POS), "");

                    //Add limit if needed
                    if (productLimitMap.containsKey(op.get(PRODUCT))) {
                        if (op.get(DEST_POS_BASE).equals(productLimitMap.get(op.get(PRODUCT)).get("position"))) {
                            ega.addAction(productLimitMap.get(op.get(PRODUCT)).get("variableName") + TypeVar.EFA_MINUS_ONE);
                        }
                    }
                    break;
            }
            //Add ega to efa
            efa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------
        }

        private void addTransportTransitions(String operation) {
            HashMap<String, String> op = operationPropertiesMap.get(operation);
            String eventName = op.get(SOURCE_POS) + TypeVar.SEPARATION + TypeVar.TRANSPORT + TypeVar.SEPARATION + op.get(DEST_POS);
            SEFA efa = productEFAMap.get(op.get(PRODUCT));
            SEGA ega = null;

            //Transition source pos -> between pos-------------------------------
            if (uniqueTransitionNames) {
                ega = new SEGA(eventName + TypeVar.SEPARATION + "01");
            } else {
                ega = new SEGA(eventName);
            }

            //Book and unbook local position
            ega.addBasicPositionBookAndUnbook(op.get(SOURCE_POS), op.get(BETWEEN_POS));

            //Check all positions that are related to dest position
            Set<String> guard = new HashSet<String>(2);
            guard.add(lumpSetWith(allPosPropertiesMap.get(op.get(DEST_POS_BASE)).get(IN), "+"));
            guard.add(lumpSetWith(allPosPropertiesMap.get(op.get(DEST_POS_BASE)).get(BY), "+"));

            if (lumpSetWith(guard, "+").length() > "".length()) {
                ega.andGuard("(" + lumpSetWith(guard, "+") + ")" + TypeVar.EFA_STRICTLY_LESS_THAN +
                        allPosPropertiesMap.get(op.get(DEST_POS_BASE)).get(CAPACITY).iterator().next());
            }

            //Check position for mover (if mover exists)
            if (op.get(OP_TYPE).equals(TypeVar.TRANSPORT) && !moversSet.isEmpty()) {
                Set<String> moverGuard = new HashSet<String>(moversSet.size());
                for (String moverPos : moversSet) {
                    String mover = "(";
                    mover += lumpSetWith(allPosPropertiesMap.get(moverPos).get(IN), "+");
                    mover += ")" + TypeVar.EFA_STRICTLY_LESS_THAN + allPosPropertiesMap.get(moverPos).get(CAPACITY).iterator().next();
                    moverGuard.add(mover);
                }
                ega.andGuard("(" + lumpSetWith(moverGuard, TypeVar.EFA_OR) + ")");
            }

            //Add limit if needed
            if (productLimitMap.containsKey(op.get(PRODUCT))) {
                if (op.get(SOURCE_POS_BASE).equals(productLimitMap.get(op.get(PRODUCT)).get("position"))) {
                    ega.andGuard(productLimitMap.get(op.get(PRODUCT)).get("variableName") + TypeVar.EFA_STRICTLY_LESS_THAN +
                            productLimitMap.get(op.get(PRODUCT)).get(TypeVar.SP_RESOURCE_LIMIT));
                    ega.addAction(productLimitMap.get(op.get(PRODUCT)).get("variableName") + TypeVar.EFA_PLUS_ONE);
                }
            }

            //Add ega to efa
            efa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------

            //Transition between pos -> dest pos---------------------------------
            if (uniqueTransitionNames) {
                ega = new SEGA(eventName + TypeVar.SEPARATION + "12");
            } else {
                ega = new SEGA(eventName);
            }

            //Book and unbook local position
            ega.addBasicPositionBookAndUnbook(op.get(BETWEEN_POS), op.get(DEST_POS));

            //Add limit if needed
            if (productLimitMap.containsKey(op.get(PRODUCT))) {
                if (op.get(DEST_POS_BASE).equals(productLimitMap.get(op.get(PRODUCT)).get("position"))) {
                    ega.addAction(productLimitMap.get(op.get(PRODUCT)).get("variableName") + TypeVar.EFA_MINUS_ONE);
                }
            }

            //Add ega to efa
            efa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------
        }

        private void addMergeTransitions(String opType) {
            HashMap<String, Set<String>> op = mergeMap.get(opType);
            String eventName = opType;
            SEFA efa = productEFAMap.get("extraEFA");
            SEGA ega = null;

            //Transition source pos -> between pos-------------------------------
            if (uniqueTransitionNames) {
                ega = new SEGA(eventName + TypeVar.SEPARATION + "01");
            } else {
                ega = new SEGA(eventName);
            }

            Set<String> productAlreadyInThesePositions = new HashSet<String>();
            //Book and unbook local position
            for (String pos : op.get(SOURCE_POS)) {
                ega.addBasicPositionBookAndUnbook(pos, op.get(BETWEEN_POS).iterator().next());
                productAlreadyInThesePositions.add(pos.split(":")[1] + ":" + pos.split(":")[2]);
            }

            //Check all positions that are related to dest position
            for (String pos : op.get(DEST_POS)) {
                String posBase = pos.split(":")[1];
                String posInstance = posBase + ":" + pos.split(":")[2];
                //No need to book a destination position that is already taken by another product in the merge.
                if (!productAlreadyInThesePositions.contains(posInstance)) {
                    Set<String> guard = new HashSet<String>(2);
                    guard.add(lumpSetWith(allPosPropertiesMap.get(posBase).get(IN), "+", op.get(SOURCE_POS)));
                    guard.add(lumpSetWith(allPosPropertiesMap.get(posBase).get(BY), "+"));

                    if (lumpSetWith(guard, "+").length() > "".length()) {
                        ega.andGuard("(" + lumpSetWith(guard, "+") + ")" + TypeVar.EFA_STRICTLY_LESS_THAN +
                                allPosPropertiesMap.get(posBase).get(CAPACITY).iterator().next());
                    }
                }
            }

            //Add limit if needed
            for (String s : op.get("sourceForLimit")) {
                String product = s.split(":")[0];
                String pos = s.split(":")[1];
                if (productLimitMap.containsKey(product)) {
                    if (pos.equals(productLimitMap.get(product).get("position"))) {
                        ega.andGuard(productLimitMap.get(product).get("variableName") + TypeVar.EFA_STRICTLY_LESS_THAN +
                                productLimitMap.get(product).get(TypeVar.SP_RESOURCE_LIMIT));
                        ega.addAction(productLimitMap.get(product).get("variableName") + TypeVar.EFA_PLUS_ONE);
                    }

                }
            }

            //Add ega to efa
            efa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------

            //Transition between pos -> dest pos---------------------------------
            if (uniqueTransitionNames) {
                ega = new SEGA(eventName + TypeVar.SEPARATION + "12");
            } else {
                ega = new SEGA(eventName);
            }

            //Book and unbook local position
            for (String pos : op.get(DEST_POS)) {
                ega.addBasicPositionBookAndUnbook(op.get(BETWEEN_POS).iterator().next(), pos);
            }

            //Add limit if needed
            for (String s : op.get("destForLimit")) {
                String product = s.split(":")[0];
                String pos = s.split(":")[1];
                if (productLimitMap.containsKey(product)) {
                    if (pos.equals(productLimitMap.get(product).get("position"))) {
                        ega.addAction(productLimitMap.get(product).get("variableName") + TypeVar.EFA_MINUS_ONE);
                    }
                }
            }

            //Add ega to efa
            efa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------
        }
    }

    private class SelectProductsDialog extends JFrame implements ActionListener {

        JButton viewButton = new JButton("View!");
        JButton wmodButton = new JButton("Generate .wmod file");
        JButton sButton = new JButton("Select all");
        JButton dsButton = new JButton("Deselect all");
        JPanel buttonJp = new JPanel();
        JPanel jp = null;
        JRadioButton check1 = new JRadioButton("yes");
        JRadioButton check2 = new JRadioButton("yes");
        Set<JCheckBox> bg = new HashSet<JCheckBox>();

        public SelectProductsDialog() {
            setTitle("Product selection");
            Container c = getContentPane();
            c.setLayout(new GridLayout(5, 1));

            //Text1--------------------------------------------------------------
            c.add(new JLabel("Set output options:"));
            //-------------------------------------------------------------------

            //Boolean variables--------------------------------------------------
            jp = new JPanel();
            c.add(jp);
            jp.setLayout(new FlowLayout());

            jp.add(new JLabel("Single EFA:"));
            check1.setSelected(false);
            jp.add(check1);

            jp.add(new JLabel("Unique event names:"));
            check2.setSelected(true);
            jp.add(check2);
            //-------------------------------------------------------------------

            //Text2--------------------------------------------------------------
            c.add(new JLabel("Select products to include in supervisor:"));
            //-------------------------------------------------------------------

            //Product selection--------------------------------------------------
            jp = new JPanel();
            c.add(jp);
            jp.setLayout(new FlowLayout());

            for (String name : productTypesMap.keySet()) {
                JCheckBox rb = new JCheckBox(name);
                rb.addActionListener(this);
                rb.setSelected(true);
                jp.add(rb);
                bg.add(rb);
            }//------------------------------------------------------------------

            //Buttons------------------------------------------------------------
            c.add(buttonJp);

            //select all
            sButton.addActionListener(this);
            sButton.setEnabled(true);
            buttonJp.add(sButton);

            //deselect all
            dsButton.addActionListener(this);
            dsButton.setEnabled(true);
            buttonJp.add(dsButton);

//            viewButton.addActionListener(this);
//            viewButton.setEnabled(true);
//            buttonJp.add(viewButton);

            wmodButton.addActionListener(this);
            wmodButton.setEnabled(true);
            buttonJp.add(wmodButton);
            //-------------------------------------------------------------------

            setLocationRelativeTo(null);
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (viewButton == e.getSource() || wmodButton == e.getSource()) {
                Set<String> operations = new HashSet<String>();
                for (JCheckBox jcb : bg) {
                    if (jcb.isSelected()) {
                        operations.add(jcb.getText());
                    }
                }
                dispose();
                if (viewButton == e.getSource()) {
//                    new SimpleDraw(operations);
                } else {
                    singleEFA = check1.isSelected();
                    uniqueTransitionNames = check2.isSelected();
                    new CollectInfomation();
                }

            } else if (sButton == e.getSource()) {
                for (JCheckBox jcb : bg) {
                    jcb.setSelected(true);
                }
                viewButton.setEnabled(true);
                wmodButton.setEnabled(true);
            } else if (dsButton == e.getSource()) {
                for (JCheckBox jcb : bg) {
                    jcb.setSelected(false);
                }
                viewButton.setEnabled(false);
                wmodButton.setEnabled(false);
            } else {
                Boolean buttonOK = false;
                for (JCheckBox jcb : bg) {
                    if (jcb.isSelected()) {
                        buttonOK = true;
                        break;
                    }
                }
                if (buttonOK) {
                    viewButton.setEnabled(true);
                    wmodButton.setEnabled(true);
                } else {
                    viewButton.setEnabled(false);
                    wmodButton.setEnabled(false);
                }
            }
        }
    }
}
