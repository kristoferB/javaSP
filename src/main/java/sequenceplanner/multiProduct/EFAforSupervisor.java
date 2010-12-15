package sequenceplanner.multiProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.apache.log4j.Logger;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author patrik
 */
public class EFAforSupervisor {

    static Logger log = Logger.getLogger(EFAforSupervisor.class);
    /**
     * To access operation and resource tree
     */
    private Model model;
    /**
     * To store automata and variables
     */
    private SModule smodule;
    /**
     * The product types that are to be included in the synthesis
     */
    private ArrayList<String> products;
    /**
     * Operations that belongs to selected product types.<br/>
     * <b>key</b> name of product type<br/>
     * <b>value</b> operations that belongs to that product type
     */
    private HashMap<String, ArrayList<OperationData>> operationsMap;
    /**
     * Limit on nbr of product type instances in system.<br/>
     * <b>key</b> product type<br/>
     * <b>value</b> nbr of instances
     */
    private HashMap<String, Integer> productLimitMap;
    /**
     * To store start pos for product type instances. All instancs are assumed to start in the same position.<br/>
     * <b>key</b> product type<br/>
     * <b>value</b> start position
     */
    private HashMap<String, String> productInitPosMap;
    /**
     * To store nbr of product type instances that start in start position.<br/>
     * <b>key</b> product type<br/>
     * <b>value</b> nbr on instances
     */
    private HashMap<String, Integer> productInitNbrMap;
    /**
     * To store capacity for position.<br/>
     * <b>key</b> position<br/>
     * <b>value</b> nbr on instances
     */
    private HashMap<String, Integer> positionCapacityMap;
    /**
     * To store actions that not are related to simple book/unbook positions. E.g. processing level<br/>
     * Use @see sequenceplanner.multiproduct.EFAforSupervisor.addToActionsForOpMap to add new entries.<br/>
     * <b>key</b> operation<br/>
     * <b>value</b> action as strings
     */
    private HashMap<OperationData, ArrayList<String>> actionsForOpMap;
    /**
     * To store guards that not are related to simple book/unbook positions. E.g. processing level<br/>
     * <b>key</b> operation<br/>
     * <b>value</b> guard as strings
     */
    private HashMap<OperationData, ArrayList<String>> guardsForOpMap;
    /**
     * To handle booking/unbooking of positions for merge operations.<br/>
     * <b>key</b> merge transition <br/>
     * <b>value</b> source positions when that are booked when operation start
     */
    private HashMap<String, ArrayList<String>> mergeSourcePositionsMap;
    /**
     * Create guard that sum all variables that affect the same position<br/>
     * E.g. (v_1+v_2+..) less than 2<br/>
     * <b>key</b> position <br/>
     * <b>value</b> guard
     */
    private HashMap<String, String> positionGuardMap;
    /**
     * Positions used by product
     * <b>key</b> product <br/>
     * <b>value</b> positions
     */
    private HashMap<String, ArrayList<String>> posForProductMap;
    /**
     * Positions that should be splited in two variables (with sufix _m and _p) in  .wmod file<br/>
     * <b>key</b> product <br/>
     * <b>value</b> positions
     */
    private HashMap<String, ArrayList<String>> detailedPosForProductMap;
    /**
     * Process operation that should be transitions in .wmod file<br/>
     * <b>key</b> product <br/>
     * <b>value</b> process operations
     */
    private HashMap<String, ArrayList<OperationData>> detailedPopsForProductMap;

    private Error e = null;

    public EFAforSupervisor(ArrayList<String> products, Model model) {
        this.products = products;
        this.model = model;
        init();
        collectInfo();
        new ArrangeInfo();
        new BuildModule();
        e.printErrorList();
        smodule.DialogAutomataTransitions();
    }

    private void declaration() {
        smodule = new SModule("temp");
        operationsMap = new HashMap<String, ArrayList<OperationData>>();
        productLimitMap = new HashMap<String, Integer>();
        productInitPosMap = new HashMap<String, String>();
        productInitNbrMap = new HashMap<String, Integer>();
        positionCapacityMap = new HashMap<String, Integer>();
        actionsForOpMap = new HashMap<OperationData, ArrayList<String>>();
        guardsForOpMap = new HashMap<OperationData, ArrayList<String>>();
        mergeSourcePositionsMap = new HashMap<String, ArrayList<String>>();
        positionGuardMap = new HashMap<String, String>();
        posForProductMap = new HashMap<String, ArrayList<String>>();
        detailedPosForProductMap = new HashMap<String, ArrayList<String>>();
        detailedPopsForProductMap = new HashMap<String, ArrayList<OperationData>>();

        e = new Error();
    }

    private void init() {
        declaration();
        exploreOperationTree(model.getOperationRoot());
        exploreResourceTree(model.getResourceRoot());
    }

    private void collectInfo() {

        for (int i = 0; i < products.size(); ++i) {
            CollectInfo ci = new CollectInfo(products.get(i));
            posForProductMap.put(products.get(i), ci.getPosForProductMap());
            detailedPopsForProductMap.put(products.get(i), ci.getDetailedPopsForProductMap());
            detailedPosForProductMap.put(products.get(i), ci.getDetailedPosForProductMap());
        }
    }

    /**
     * <b>COLLECT INFORMATION</b><br/>
     * for each product----------------------------------------<br/>
     * |arraylist positions for product<br/>
     * |arraylist positions that require _m and _p<br/>
     * |Create arraylist pop that requires transition and fill arraylist positions that require _m and _p<br/>
     * |+iterate top and their guards/get top with gurads!=1 to find plCount needed<br/>
     * |+iterate pop to get merge/get pop with merge operation ->fill arraylist positions for product<br/>
     * |+if product type has limit. add start and finish op for product to arraylist if op are pop ->fill arraylist positions for product<br/>
     * +----------------------------------------------------------<br/>
     */
    private class CollectInfo {

        Logger log = Logger.getLogger(CollectInfo.class);
        private String product;
        private ArrayList<String> posForProductMap;
        private ArrayList<String> detailedPosForProductMap;
        private ArrayList<OperationData> detailedPopsForProductMap;

        public CollectInfo(String product) {
            posForProductMap = new ArrayList<String>();
            detailedPosForProductMap = new ArrayList<String>();
            detailedPopsForProductMap = new ArrayList<OperationData>();
            this.product = product;
            this.calc();
        }

        private void calc() {
            Iterator<OperationData> itOpData = operationsMap.get(product).iterator();

            //Collect data from each operation for this product type-------------
            while (itOpData.hasNext()) {
                OperationData opData = itOpData.next();
                String opDesc = opData.getDescription();

                if (ExtendedData.getOPType(opDesc) != null) {
                    if (ExtendedData.getOPType(opDesc).equals(TypeVar.ED_OP_TYPE_PROCESS)) {
                        pop(opData, opDesc);
                    } else if (ExtendedData.getOPType(opDesc).equals(TypeVar.ED_OP_TYPE_TRANSPORT)) {
                        top(opData, opDesc);
                    }
                } else {//opType is not set
                    e.error(opData.getName(), TypeVar.ED_OP_TYPE);
                }
            }
            //-------------------------------------------------------------------

            //Is there an upper limit for number of product instances for this product type?
            limit();
        }

        public ArrayList<OperationData> getDetailedPopsForProductMap() {
            return detailedPopsForProductMap;
        }

        public ArrayList<String> getDetailedPosForProductMap() {
            return detailedPosForProductMap;
        }

        public ArrayList<String> getPosForProductMap() {
            return posForProductMap;
        }

        private void pop(OperationData opData, String opDesc) {

            //add source pos-----------------------------------------------------
            if (ExtendedData.getSourcePos(opDesc) != null) {
                if (positionIsOk(ExtendedData.getSourcePos(opDesc))) {
                    addToArrayList(posForProductMap, ExtendedData.getSourcePos(opDesc));
                }
            } else {
                e.error(opData.getName(), TypeVar.ED_SOURCE_POS);
            }//------------------------------------------------------------------

            //add dest pos-------------------------------------------------------
            if (ExtendedData.getDestPos(opDesc) != null) {
                if (positionIsOk(ExtendedData.getDestPos(opDesc))) {
                    addToArrayList(posForProductMap, ExtendedData.getDestPos(opDesc));
                }
            } else {
               e.error(opData.getName(), TypeVar.ED_DEST_POS);
            }//------------------------------------------------------------------

            //check merge pop----------------------------------------------------
            if (ExtendedData.getMerge(opDesc) != null) {
                addDetails(opData);
            }//------------------------------------------------------------------
        }

        private void top(OperationData opData, String opDesc) {
            //Guardgeneration should be based on allowed stateset -> all guards are plCount==X
            if (ExtendedData.getGuard(opDesc) != null) {

                //Account for processing level count-----------------------------
                String guard = ExtendedData.getGuard(opDesc);
                if (!guard.equals("1")) {
                    if (guard.contains(TypeVar.EFA_EQUAL)) {
                        //log.info(opData.getName() + " has guard " + guard);
                        while (guard.contains(TypeVar.EFA_EQUAL)) {

                            //create variable
                            String oldGuard = guard;
                            int plus = TypeVar.EFA_EQUAL.length() + 1; //Assume that 0<X<9 for all plCount==X
                            int index = guard.indexOf(TypeVar.EFA_EQUAL);
                            int minus = TypeVar.PROCESSING_LEVEL.length() + TypeVar.SEPARATION.length() + 3; //Assume that pos = "pYZ"
                            guard = guard.substring(0, index + plus).replaceAll(TypeVar.EFA_EQUAL, TypeVar.SEPARATION);
                            String varName = guard.substring(index - minus, index + TypeVar.SEPARATION.length() + 1); //Assume 0<X<9
                            smodule.addIntVariable(product + varName, 0, TypeVar.PROCESSING_LEVEL_COUNT_LIMIT, 0, 0);
                            guard = guard + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO;
                            if (index + plus <= oldGuard.length()) {
                                guard = guard + oldGuard.substring(index + plus);
                            }
                            //log.info(guard);

                            //create actions
                            String pos = varName.substring(TypeVar.SEPARATION.length()).substring(0, 3); //Assume that pos = "pYZ"
                            String count = varName.substring(varName.length() - 1); //Assume that 0<X<9 for all plCount==X
                            new AddCountAction(pos, count);
                        }
                    } else {
                        e.error(opData.getName() + " has strange " + TypeVar.ED_GUARD);
                    }
                    addToGuardsForOpMap(opData, guard);
                    //The guard in the operation should also be changed
                }//--------------------------------------------------------------
            } else {
                e.error(opData.getName(), TypeVar.ED_GUARD);
            }
        }

        private void limit() {
            ArrayList<OperationData> ops;
            Iterator<OperationData> itOp;

            //need for limit on product?
            if (productLimitMap.containsKey(product)) {

                //create variable
                smodule.addIntVariable(product + TypeVar.LIMIT, 0, productLimitMap.get(product), 0, 0);

                //add limit restriction to first op------------------------------
                ops = getOps(TypeVar.ED_ORDER, TypeVar.ED_ORDER_FIRST);
                if (!ops.isEmpty()) {
                    itOp = ops.iterator();
                    while (itOp.hasNext()) {
                        OperationData opData = itOp.next();
                        addDetails(opData);
                        String action = product + TypeVar.LIMIT;
                        addToGuardsForOpMap(opData, action + TypeVar.EFA_STRICTLY_LESS_THAN + productLimitMap.get(product));
                        addToActionsForOpMap(opData, action + TypeVar.EFA_PLUS_ONE);
                    }
                } else {
                    e.error(product + " has limit demand but lacks " + TypeVar.ED_ORDER_FIRST + " operation");
                }
                //---------------------------------------------------------------

                //add limit restriction to last op-------------------------------
                ops = getOps(TypeVar.ED_ORDER, TypeVar.ED_ORDER_LAST);
                if (!ops.isEmpty()) {
                    itOp = ops.iterator();
                    while (itOp.hasNext()) {
                        OperationData opData = itOp.next();
                        addDetails(opData);
                        String action = product + TypeVar.LIMIT;
                        addToActionsForOpMap(opData, action + TypeVar.EFA_MINUS_ONE);
                    }
                } else {
                    e.error(product + " has limit demand but lacks " + TypeVar.ED_ORDER_LAST + " operation");
                }
                //---------------------------------------------------------------
            }
        }

        /**
         * Checks if operations and it's source and dest pos should be transitions and variables in .wmod file
         * @param opData operation to check
         */
        private void addDetails(OperationData opData) {
            String opDesc = opData.getDescription();
            if (ExtendedData.getOPType(opDesc) != null) {
                if (ExtendedData.getOPType(opDesc).equals(TypeVar.ED_OP_TYPE_PROCESS)) {
                    addToArrayList(detailedPopsForProductMap, opData);
                    if (ExtendedData.getSourcePos(opDesc).equals(ExtendedData.getDestPos(opDesc))) {
                        addToArrayList(detailedPosForProductMap, ExtendedData.getSourcePos(opDesc));
                    }
                }
            } else {
                e.error(opData.getName(), TypeVar.ED_OP_TYPE);
            }
        }

        private class AddCountAction {

            private String pos;
            private String count;
            private ArrayList<OperationData> ops;
            Iterator<OperationData> it;

            public AddCountAction(String pos, String count) {
                ops = new ArrayList<OperationData>();
                this.pos = pos;
                this.count = count;
                //Find operations that affect processing level for pos
                findOP();
                //Sequel sorting of ops of size two
                sequelSorting();
                //add actions very ugly implmentation. Better to use op relations when that is ready
                addActions();
            }

            private void findOP() {
                it = operationsMap.get(product).iterator();
                while (it.hasNext()) {
                    OperationData opData = it.next();
                    String opDesc = opData.getDescription();
                    if (ExtendedData.getOPType(opDesc) != null && ExtendedData.getSourcePos(opDesc) != null) {
                        if (ExtendedData.getOPType(opDesc).equals(TypeVar.ED_OP_TYPE_PROCESS) && ExtendedData.getSourcePos(opDesc).equals(pos)) {
                            ops.add(opData);
                        }
                    } else {
                        e.error(opData.getName(), TypeVar.ED_OP_TYPE + " or " + TypeVar.ED_SOURCE_POS);
                    }
                }
            }

            private void sequelSorting() {
                if (ops.size() == 2) {
                    if (ops.get(1).getRawPrecondition().contains(ops.get(0).getName())) {
                        //->ops.get(0) is before ops.get(1)
                    } else {
                        //->ops.get(1) is before ops.get(0)
                        OperationData temp = ops.remove(1);
                        ops.add(0, temp);
                    }
                } else if (ops.size() == 1) {
                } else {
                    e.error("Can't create actions for plCounters. Relations to complex for implementation");
                }
            }

            private void addActions() {
                String action = product + TypeVar.SEPARATION + pos + TypeVar.PROCESSING_LEVEL + TypeVar.SEPARATION + count;
                if (count.equals("0")) {

                    //+=1--------------------------------------------------------
                    ArrayList firstops = getOps(TypeVar.ED_ORDER, TypeVar.ED_ORDER_FIRST);
                    if (!firstops.isEmpty()) {
                        it = firstops.iterator();
                        while (it.hasNext()) {
                            OperationData opData = it.next();
                            addToActionsForOpMap(opData, action + TypeVar.EFA_PLUS_ONE);
                            addDetails(opData);
                        }
                    } else {
                        e.error(product, TypeVar.ED_ORDER + " with " + TypeVar.ED_ORDER_FIRST);
                    }//----------------------------------------------------------

                    //-=1--------------------------------------------------------
                    addToActionsForOpMap(ops.get(0), action + TypeVar.EFA_MINUS_ONE);
                    addDetails(ops.get(0));
                    //-----------------------------------------------------------

                } else if (count.equals("1")) {

                    //+=1--------------------------------------------------------
                    addToActionsForOpMap(ops.get(0), action + TypeVar.EFA_PLUS_ONE);
                    addDetails(ops.get(0));
                    //-----------------------------------------------------------

                    //-=1--------------------------------------------------------
                    if (ops.size() == 1) {
                        ArrayList lastops = getOps(TypeVar.ED_ORDER, TypeVar.ED_ORDER_LAST);
                        if (!lastops.isEmpty()) {
                            it = lastops.iterator();
                            while (it.hasNext()) {
                                OperationData opData = it.next();
                                addToActionsForOpMap(opData, action + TypeVar.EFA_MINUS_ONE);
                                addDetails(opData);
                            }
                        } else {
                            e.error(product, TypeVar.ED_ORDER + " with " + TypeVar.ED_ORDER_LAST);
                        }
                    } else if (ops.size() == 2) {
                        addToActionsForOpMap(ops.get(0), action + TypeVar.EFA_MINUS_ONE);
                        addDetails(ops.get(0));
                    } else {
                        e.error("Can't create actions for plCounters. Relations to complex for implementation");
                    }//----------------------------------------------------------

                } else {
                    e.error("Can't create actions for plCounters. Relations to complex for implementation");
                }
            }
        }

        /**
         * Get arraylist based on attribute and value for attirbute
         * @param attribute
         * @param value
         * @return ArrayList OperationData
         */
        private ArrayList<OperationData> getOps(String attribute, String value) {
            ArrayList<OperationData> result = new ArrayList<OperationData>();
            Iterator<OperationData> it = operationsMap.get(product).iterator();
            while (it.hasNext()) {
                OperationData opData = it.next();
                String opDesc = opData.getDescription();
                if (ExtendedData.get(opDesc, attribute) != null) {
                    if (ExtendedData.get(opDesc, attribute).equals(value)) {
                        result.add(opData);
                    }
                }
            }
            return result;
        }
    }

    /**
     * <b>ARRANGE INFORMATION</b><br/>
     * Creates haspmap that takes all product positions for a position into account and position capacity
     * this should later on be used for topar
     */
    private class ArrangeInfo {

        Logger log = Logger.getLogger(ArrangeInfo.class);
        /**
         * Collects products (value) that uses a position (key)
         */
        private HashMap<String, ArrayList<String>> productForPosMap;

        public ArrangeInfo() {
            productForPosMap = new HashMap<String, ArrayList<String>>();
            createProductForPosMap();
            createPositionGuardMap();
        }

        /**
         * create productForPosMap
         */
        private void createProductForPosMap() {
            Iterator<String> itProduct = posForProductMap.keySet().iterator();
            while (itProduct.hasNext()) {
                String product = itProduct.next();
                Iterator<String> itPos = posForProductMap.get(product).iterator();
                while (itPos.hasNext()) {
                    String pos = itPos.next();
                    if (!productForPosMap.containsKey(pos)) {
                        productForPosMap.put(pos, new ArrayList<String>());
                    }
                    productForPosMap.get(pos).add(product);
                }
            }
        }

        /**
         * Creates positionGuardMap</br>
         * I.e. sum of all variables that affect the same position
         */
        private void createPositionGuardMap() {
            Iterator<String> itPos = productForPosMap.keySet().iterator();
            while (itPos.hasNext()) {
                String guard = "";
                String pos = itPos.next();
                Iterator<String> itProduct = productForPosMap.get(pos).iterator();
                while (itProduct.hasNext()) {
                    String product = itProduct.next();

                    if (guard.length() > 0) {
                        guard = guard + "+";
                    }
                    if (detailedPosForProductMap.get(product).contains(pos)) {
                        guard = guard + product + TypeVar.SEPARATION + pos + TypeVar.POS_MOVE + "+";
                        guard = guard + product + TypeVar.SEPARATION + pos + TypeVar.POS_PROCESS;
                    } else {
                        guard = guard + product + TypeVar.SEPARATION + pos;
                    }
                }
                if (positionCapacityMap.containsKey(pos)) {
                    guard = "(" + guard + ")" + TypeVar.EFA_STRICTLY_LESS_THAN + positionCapacityMap.get(pos);
                } else {
                    e.error(pos, "capacity");
                }
                positionGuardMap.put(pos, guard);
            }
        }
    }

    /**
     * <b>BUILD MODULE</b><br/>
     * The collected and arranged data are put together in EFA variables and automata.<br/>
     * The generated module my be opened in supremica for synthesis.
     */
    private class BuildModule {

        SEGA ega;
        OperationData cOpData;

        public BuildModule() {
            addVariables();
            addAutomata();
        }

        private void addVariables() {
            Iterator<String> itProduct = posForProductMap.keySet().iterator();
            while (itProduct.hasNext()) {
                String product = itProduct.next();
                Iterator<String> itPos = posForProductMap.get(product).iterator();
                while (itPos.hasNext()) {
                    String pos = itPos.next();
                    String start = product + TypeVar.SEPARATION + pos;
                    if (positionCapacityMap.containsKey(pos)) {
                        if (detailedPosForProductMap.containsKey(product)) {
                            if (detailedPosForProductMap.get(product).contains(pos)) {
                                smodule.addIntVariable(start + TypeVar.POS_PROCESS, 0, positionCapacityMap.get(pos), 0, 0);
                                smodule.addIntVariable(start + TypeVar.POS_MOVE, 0, positionCapacityMap.get(pos), 0, 0);
                                if (productInitPosMap.containsKey(product)) {
                                    if (productInitPosMap.get(product).equals(pos)) {
                                        e.error("The initial pos is partitioned! Add initial and marked value for variable manually!");
                                    }
                                }
                            } else {
                                if (productInitPosMap.containsKey(product)) {
                                    if (productInitPosMap.get(product).equals(pos)) {
                                        smodule.addIntVariable(start, 0, positionCapacityMap.get(pos),
                                                productInitNbrMap.get(product), productInitNbrMap.get(product));
                                    } else {
                                        smodule.addIntVariable(start, 0, positionCapacityMap.get(pos), 0, 0);
                                    }
                                } else {
                                    smodule.addIntVariable(start, 0, positionCapacityMap.get(pos), 0, 0);
                                }
                            }
                        } else {
                            smodule.addIntVariable(start, 0, positionCapacityMap.get(pos), 0, 0);
                        }
                    } else {
                        e.error(pos, "capacity");
                    }
                }
            }
        }

        private void addAutomata() {
            for (int i = 0; i < products.size(); ++i) {
                addAutomaton(products.get(i));
            }
        }

        private void addAutomaton(String product) {
            //create automaton---------------------------------------------------
            SEFA efa = new SEFA(product, smodule);
            efa.addState(TypeVar.LOCATION, true, true);
            //-------------------------------------------------------------------

            //add transitions----------------------------------------------------
            Iterator<OperationData> itod = operationsMap.get(product).iterator();
            while (itod.hasNext()) {
                cOpData = itod.next();
                if (detailedPopsForProductMap.containsKey(product)) {
                    if (detailedPopsForProductMap.get(product).contains(cOpData) ||
                            ExtendedData.getOPType(cOpData.getDescription()).equals(TypeVar.ED_OP_TYPE_TRANSPORT)) {
                        ega = new SEGA();
                        addEvent();
                        addGuard();
                        addAction();
                        efa.addTransition(TypeVar.LOCATION, TypeVar.LOCATION, ega.getEvent(), ega.getGuard(), ega.getAction());
                    }
                }
            }
            //-------------------------------------------------------------------
        }

        private void addEvent() {
            String desc = cOpData.getDescription();
            if (ExtendedData.getMerge(desc) != null) {
                ega.addEvent(ExtendedData.getMerge(desc));
            } else {
                String event = cOpData.getName();
                if (ExtendedData.getOPType(desc).equals(TypeVar.ED_OP_TYPE_PROCESS)) {
                    event = ExtendedData.getProductType(desc) + TypeVar.SEPARATION + event;
                }
                ega.addEvent(event);
            }
        }

        private void addGuard() {
            String desc = cOpData.getDescription();

            //top----------------------------------------------------------------------
            if (ExtendedData.getOPType(desc).equals(TypeVar.ED_OP_TYPE_TRANSPORT)) {
                String guard = "";

                //source pos-----------------------------------------------------
                if (positionIsOk(ExtendedData.getSourcePos(desc))) {
                    guard = ExtendedData.getProductType(desc) + TypeVar.SEPARATION + ExtendedData.getSourcePos(desc);
                    if (detailedPosForProductMap.containsKey(ExtendedData.getProductType(desc))) {
                        if (detailedPosForProductMap.get(ExtendedData.getProductType(desc)).contains(ExtendedData.getSourcePos(desc))) {
                            guard = guard + TypeVar.POS_MOVE;
                        }
                    }
                    ega.andGuard(guard + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
                }//--------------------------------------------------------------

                //dest pos-------------------------------------------------------
                if (positionGuardMap.containsKey(ExtendedData.getDestPos(desc))) {
                    ega.andGuard(positionGuardMap.get(ExtendedData.getDestPos(desc)));
                } else {
                    if (positionIsOk(ExtendedData.getDestPos(desc))) {
                        guard = ExtendedData.getProductType(desc) + TypeVar.SEPARATION + ExtendedData.getDestPos(desc);
                        if (detailedPosForProductMap.containsKey(ExtendedData.getProductType(desc))) {
                            if (detailedPosForProductMap.get(ExtendedData.getProductType(desc)).contains(ExtendedData.getDestPos(desc))) {
                                guard = guard + TypeVar.POS_PROCESS;
                            }
                        }
                        ega.andGuard(guard + TypeVar.EFA_STRICTLY_LESS_THAN + positionCapacityMap.get(ExtendedData.getDestPos(desc)));
                    }
                }//--------------------------------------------------------------

            } //----------------------------------------------------------------------
            //pop---------------------------------------------------------------------
            else {
                String guard = "";

                //source pos-----------------------------------------------------
                if (positionIsOk(ExtendedData.getSourcePos(desc))) {
                    guard = ExtendedData.getProductType(desc) + TypeVar.SEPARATION + ExtendedData.getSourcePos(desc);
                    if (detailedPosForProductMap.containsKey(ExtendedData.getProductType(desc))) {
                        if (detailedPosForProductMap.get(ExtendedData.getProductType(desc)).contains(ExtendedData.getSourcePos(desc))) {
                            guard = guard + TypeVar.POS_PROCESS;
                        }
                    }
                    ega.andGuard(guard + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
                }//--------------------------------------------------------------

                //dest pos-------------------------------------------------------
                if (!ExtendedData.getSourcePos(desc).equals(ExtendedData.getDestPos(desc)) &&
                        positionIsOk(ExtendedData.getDestPos(desc))) {
                    if (!skipGuardDoToMerge(desc)) {
                        guard = ExtendedData.getProductType(desc) + TypeVar.SEPARATION + ExtendedData.getDestPos(desc);
                        ega.andGuard(guard + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
                    }
                }//--------------------------------------------------------------
            }
            //------------------------------------------------------------------------

            //add special guards-------------------------------------------------
            if (guardsForOpMap.containsKey(cOpData)) {
                Iterator<String> its = guardsForOpMap.get(cOpData).iterator();
                while (its.hasNext()) {
                    ega.andGuard(its.next());
                }
            }//------------------------------------------------------------------
        }

        /**
         * Checks if dest pos already is taken during merge transitions
         * -> there should be no guard related to dest pos.
         * @param desc description for operation
         * @return
         */
        private boolean skipGuardDoToMerge(String desc) {
            if (ExtendedData.getMerge(desc) != null) {
                if (mergeSourcePositionsMap.get(ExtendedData.getMerge(desc)).contains(ExtendedData.getDestPos(desc))) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        private void addAction() {
            String desc = cOpData.getDescription();
            String action = "";

            //Book---------------------------------------------------------------
            if (positionIsOk(ExtendedData.getDestPos(desc))) {
                action = ExtendedData.getProductType(desc) + TypeVar.SEPARATION + ExtendedData.getDestPos(desc);
                if (detailedPosForProductMap.containsKey(ExtendedData.getProductType(desc))) {
                    if (detailedPosForProductMap.get(ExtendedData.getProductType(desc)).contains(ExtendedData.getDestPos(desc))) {
                        if (ExtendedData.getOPType(desc).equals(TypeVar.ED_OP_TYPE_PROCESS)) {
                            action = action + TypeVar.POS_MOVE;
                        } else { //transport
                            action = action + TypeVar.POS_PROCESS;
                        }
                    }
                }
                ega.addAction(action + TypeVar.EFA_PLUS_ONE);
            }//------------------------------------------------------------------

            //Unbook-------------------------------------------------------------
            if (positionIsOk(ExtendedData.getSourcePos(desc))) {
                action = ExtendedData.getProductType(desc) + TypeVar.SEPARATION + ExtendedData.getSourcePos(desc);
                if (detailedPosForProductMap.containsKey(ExtendedData.getProductType(desc))) {
                    if (detailedPosForProductMap.get(ExtendedData.getProductType(desc)).contains(ExtendedData.getSourcePos(desc))) {
                        if (ExtendedData.getOPType(desc).equals(TypeVar.ED_OP_TYPE_PROCESS)) {
                            action = action + TypeVar.POS_PROCESS;
                        } else { //transport
                            action = action + TypeVar.POS_MOVE;
                        }
                    }
                }
                ega.addAction(action + TypeVar.EFA_MINUS_ONE);
            }//------------------------------------------------------------------

            //add special actions------------------------------------------------
            if (actionsForOpMap.containsKey(cOpData)) {
                Iterator<String> its = actionsForOpMap.get(cOpData).iterator();
                while (its.hasNext()) {
                    ega.addAction(its.next());
                }
            }//------------------------------------------------------------------
        }
    }

    public ModuleSubject getSubjectModule() {
        return smodule.getModuleSubject();
    }

    /**
     * Store operations that belongs to selected product types in hashmap <b>operationsMap</b>
     * @param treeNode operation tree root
     */
    private void exploreOperationTree(TreeNode treeNode) {
        for (int i = 0; i < treeNode.getChildCount(); ++i) {
            OperationData opData = (OperationData) treeNode.getChildAt(i).getNodeData();
            if (ExtendedData.getProductType(opData.getDescription()) != null) {
                String key = ExtendedData.getProductType(opData.getDescription());
                if (!operationsMap.containsKey(key)) {
                    operationsMap.put(key, new ArrayList<OperationData>());
                }
                operationsMap.get(key).add(opData);
                //log.info(opData.getName() + " is added to operationsMap");

                exploreMergeSourcePositions(opData);
            } else {
                e.error(opData.getName(), TypeVar.ED_PRODUCT_TYPE);
            }
        }
    }

    /**
     * Store source pos for merge operations.<br/>
     * Used to skip checking dest pos if pos already taken before the merge transition occurs.
     * @param opData
     */
    private void exploreMergeSourcePositions(OperationData opData) {
        String desc = opData.getDescription();
        if (ExtendedData.getMerge(desc) != null) {
            if (!mergeSourcePositionsMap.containsKey(ExtendedData.getMerge(desc))) {
                mergeSourcePositionsMap.put(ExtendedData.getMerge(desc), new ArrayList<String>());
            }
            if (positionIsOk(ExtendedData.getSourcePos(desc)) &&
                    !mergeSourcePositionsMap.get(ExtendedData.getMerge(desc)).contains(ExtendedData.getSourcePos(desc))) {
                mergeSourcePositionsMap.get(ExtendedData.getMerge(desc)).add(ExtendedData.getSourcePos(desc));
            }
        }
    }

    /**
     * <b>Resource tree</b><br/>
     * Positions<br/>
     * +Pos1<br/>
     * -Pos2<br/>
     *  -Capacity<br/>
     *  -ProductType1<br/>
     *   -Limit<br/>
     * -out<br/>
     *  -ProductType2<br/>
     *  -ProductType3<br/>
     *   -Limit<br/>
     * @param Resource tree root
     */
    private void exploreResourceTree(TreeNode treeNode) {
        for (int i = 0; i < treeNode.getChildCount(); ++i) {
            TreeNode node = treeNode.getChildAt(i);
            if (Model.isResource(node.getNodeData())) {
                exploreResourceTree(treeNode.getChildAt(i));
            } else if (Model.isVariable(node.getNodeData())) {
                ResourceVariableData data = (ResourceVariableData) node.getNodeData();
                //positions------------------------------------------------------
                if (data.getName().equals(TypeVar.SP_RESOURCE_CAPACITY)) {
                    String pos = node.getParent().getNodeData().getName();
                    if (!positionCapacityMap.containsKey(pos)) {
                        positionCapacityMap.put(pos, data.getMax());
                    } else {
                        e.error("Position " + pos + " has redundent capacity informaion");
                    }
                } //-----------------------------------------
                //products---------------------------------
                else if (data.getName().equals(TypeVar.SP_RESOURCE_LIMIT)) {
                    String product = node.getParent().getNodeData().getName();
                    if (!productLimitMap.containsKey(product)) {
                        productLimitMap.put(product, data.getMax());
                    } else {
                        e.error("Product " + product + " has redundent limitation informaion");
                    }
                    if (!productInitPosMap.containsKey(product)) {
                        productInitPosMap.put(product, node.getParent().getParent().getNodeData().getName());
                        if (!productInitNbrMap.containsKey(product)) {
                            productInitNbrMap.put(product, data.getMax());
                        } else {
                            e.error("Product " + product + " has redundent start number informaion");
                        }
                    } else {
                        e.error("Product " + product + " has redundent start position informaion");
                    }
                }
                //----------------------------------------
            } else {
                e.error("Problem to iterate resource tree");
            }
        }
    }

    private boolean addToArrayList(ArrayList al, Object ob) {
        if (ob != null) {
            if (!al.contains(ob)) {
                al.add(ob);
            }
            return true;
        } else {
            return false;
        }
    }

    private void addToActionsForOpMap(OperationData key, String value) {
        if (!actionsForOpMap.containsKey(key)) {
            actionsForOpMap.put(key, new ArrayList<String>());
        }
        if (!actionsForOpMap.get(key).contains(value)) {
            actionsForOpMap.get(key).add(value);
        }
    }

    private void addToGuardsForOpMap(OperationData key, String value) {
        if (!guardsForOpMap.containsKey(key)) {
            guardsForOpMap.put(key, new ArrayList<String>());
        }
        if (!guardsForOpMap.get(key).contains(value)) {
            guardsForOpMap.get(key).add(value);
        }
    }

    public void printStringArrayList(ArrayList<String> al) {
        Iterator<String> it = al.iterator();
        System.out.println("\n" + al.toString() + "-----");
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public void printOperationDataArrayList(ArrayList<OperationData> al) {
        Iterator<OperationData> it = al.iterator();
        System.out.println("\n" + al.toString() + "-----");
        while (it.hasNext()) {
            System.out.println(it.next().getName());
        }
    }

    /**
     * Some position names have special meaning and sometimes excluded.
     * @param position to test
     * @return true - position is ok, false - position should be excluded
     */
    private boolean positionIsOk(String pos) {
        if (pos.equals(TypeVar.POS_OUT)) {
            return false;
        } else if (pos.equals(TypeVar.POS_MERGE)) {
            return false;
        } else {
            return true;
        }
    }
}
