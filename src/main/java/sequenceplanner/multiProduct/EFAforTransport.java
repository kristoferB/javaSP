package sequenceplanner.multiProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import sequenceplanner.efaconverter.SEFA;
import sequenceplanner.efaconverter.SEGA;
import sequenceplanner.efaconverter.SModule;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 * User selects one product from available products. <br/>
 * Transport planning should eventually return the minimal number
 * of position/resource instances needed and the transport
 * operations between these positions.<br/>
 * A single position may need to be represented in a nbr of instances
 * in order to capture e.g. alternaitve routes.<br/>
 * @author patrik
 */
public class EFAforTransport {

    static Logger log = Logger.getLogger(EFAforTransport.class);
    private Model model = null;
    private HashMap<String, InternalOpDatas> productTypes;

    public EFAforTransport(Model model) {
        this.model = model;
        productTypes = new HashMap<String, InternalOpDatas>();
        getOperations(model.getOperationRoot());
    }

    private void getOperations(TreeNode tree) {
        for (int i = 0; i < tree.getChildCount(); ++i) {
            InternalOpData iOpData = new InternalOpData((OperationData) tree.getChildAt(i).getNodeData());

            //handle global operation data
            if (!productTypes.containsKey(iOpData.getProductType())) {
                productTypes.put(iOpData.getProductType(), new InternalOpDatas());
            }
            productTypes.get(iOpData.getProductType()).add(iOpData);

            //handle local operation data
            if (tree.getId() != model.getOperationRoot().getId()) { //The root is not a operation parent
                iOpData.parentId = tree.getId();
            }
            getOperations(tree.getChildAt(i));
        }
    }

    /**
     * User selects one product from available products. <br/>
     * @return Selected product
     */
    public String transportPlanningDialog() {
        String answer = (String) JOptionPane.showInputDialog(null,
                "Pick a product type: ",
                "Transport Planning Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                productTypes.keySet().toArray(),
                null);
        return answer;
    }

    public void transportPlanning(String key) {
        new TransportPlanningProductType(key);
    }

    public void transportPlanning(String key, HashMap<String, String> nameGuardMap) {
        new TransportPlanningProductType(key, nameGuardMap);
    }

    private class TransportPlanningProductType {

        private Error e = new Error(TransportPlanningProductType.class.toString());
        private String productType;
        private InternalOpDatas operations;
        private Integer generation;
        private SModule smodule = new SModule("temp");
        private SEFA efa;
        private HashMap<Integer, String> opIdEFAsourcePosMap = new HashMap<Integer, String>();
        private HashMap<Integer, String> opIdEFAdestPosMap = new HashMap<Integer, String>();
        private HashMap<String, String> nameGuardMap = null;
        private HashMap<String, HashSet<String>> posRefinementMap = new HashMap<String, HashSet<String>>();
        private HashMap<String, HashSet<Integer>> posHistogramMap = new HashMap<String, HashSet<Integer>>();
        private String ps = null;

        public TransportPlanningProductType(String key) {
            log.info("Constructor transport planning generation 0");
            productType = key;
            generation = 0;
            run();
        }

        public TransportPlanningProductType(String key, HashMap<String, String> nameGuardMap) {
            log.info("Constructor transport planning generation 1");
            productType = key;
            this.nameGuardMap = nameGuardMap;
            generation = 1;
            run();
        }

        private void run() {
            ps = productType + TypeVar.SEPARATION;

            smodule.setComment("Module for transport planning\n*****\nSet start position manually\nSet finish position manually\n" +
                    "*****\nSynthesize supervisor through guard extraction\n");

            testIDs();

            operations = productTypes.get(productType);
            operations.setParentChildrenRelations();

            //Single Location automaton------------------------------------------
            efa = new SEFA(productType, smodule);
            efa.addState(TypeVar.LOCATION, true, true);
            //-------------------------------------------------------------------

            switch (generation) {//----------------------------------------------
                case 0:
                    //Only exists process operations in SP the first round
                    initG0();
                    addVariablesG0();
                    addTransportTransitionsG0();
                    addProcessTransitionsG0();
                    break;
                case 1:
                    initG1();
                    addVariablesG1();
                    addTransportTransitionsG1();
                    addProcessTransitionsG1();
                    break;
                default:
                    e.error("Generation is set out of bounds!");
                    break;
            }//------------------------------------------------------------------

            smodule.DialogAutomataTransitions();
            e.printErrorList();
        }

        /**
         * Method in this class can't handle IDs that are suffix or prefix to each other, e.g. 18 and 118
         * @return true if IDs are ok else false
         */
        private boolean testIDs() {
            String test = "";
            ArrayList<Integer> al = getOperationIDsforThisProductType();
            for (int i = 0; i < al.size(); ++i) {
                if (test.contains(al.get(i).toString())) {
                    e.error("I can't handle IDs that are prefix or suffix of each other, e.g. 18 and 118");
                    return false;
                } else {
                    test = test + al.get(i).toString() + TypeVar.SEPARATION;
                }
            }
            return true;
        }

        /**
         * To get the right position and id for hierarchical operations.<br/>
         * It should be the position for the top hierarchical op that counts.
         * @param iData Operation to test
         * @param posType Source position or destination position
         * @return Position and id selected
         */
        private HashMap<String, Integer> getPositionAndIdWithRespectToParents(InternalOpData iData, String posType) {
            HashMap<String, Integer> positionIdMap = new HashMap<String, Integer>(1);

            InternalOpData parent = iData.parent;

            if (parent != null) {
                if (parent.attributes.get(posType) != null) {
                    positionIdMap.put(parent.attributes.get(posType), parent.getId());
                } else {
                    positionIdMap.put(iData.attributes.get(posType), iData.getId());
                }
            } else {
                positionIdMap.put(iData.attributes.get(posType), iData.getId());
            }

            return positionIdMap;
        }

        /**
         * Fill positions into histogram<br/>
         * The histogram is later on used to create instances of given position, according to count given in histogram.<br/>
         * The whole histogram thing is not that nice...
         * @param posIdMap position to add
         */
        private void addToPosHistogramMap(HashMap<String, Integer> posIdMap) {
            for (String position : posIdMap.keySet()) {
                if (!posHistogramMap.containsKey(position)) {
                    posHistogramMap.put(position, new HashSet<Integer>());
                }
                posHistogramMap.get(position).add(posIdMap.get(position));
            }
        }

        /**
         * Create histogram over positions for process operations in product selected.
         */
        private void initG0() {
            for (InternalOpData iData : operations) {

                HashMap<String, Integer> posIdMap = null;

                //Get position and id
                posIdMap = getPositionAndIdWithRespectToParents(iData, "sourcePos");

                //Add position to histogram
                addToPosHistogramMap(posIdMap);

                if (!iData.hasSinglePos()) {
                    //Get position and id
                    posIdMap = getPositionAndIdWithRespectToParents(iData, "destPos");
                    //Add position to histogram
                    addToPosHistogramMap(posIdMap);
                }
            }
        }

        private void addToRefinementMap(String pos, String guard) {
            if (!posRefinementMap.containsKey(pos)) {
                posRefinementMap.put(pos, new HashSet<String>());
            }
            posRefinementMap.get(pos).add(guard);
        }

        private void initG1() {
            //fill posRefinementMap----------------------------------------------
            for (String name : nameGuardMap.keySet()) {
                String guard = nameGuardMap.get(name);
                String source = name.split(TypeVar.SEPARATION)[1];
                String dest = name.split(TypeVar.SEPARATION)[3];

                //Refine source position with guard (This is always _m, never _p)
                addToRefinementMap(source, guard);

                //Refine process (_p) of source position with guard (if this process pos exists)
                addToRefinementMap(source.replaceAll(TypeVar.POS_MOVE, TypeVar.POS_PROCESS), guard);

                //Add dest position but should not be refined
                addToRefinementMap(dest, "1");
            }//------------------------------------------------------------------

            //Remove basic guard from refined positions--------------------------
            for (String pos : posRefinementMap.keySet()) {
                if (posRefinementMap.get(pos).size() > 1) {
                    posRefinementMap.get(pos).remove("1");
                }
            }//------------------------------------------------------------------
        }

        /**
         * Method to make positions unique.<br/>
         * @param iData Operation
         * @param posType position to test
         * @param residualPosType should be dest pos if position to test is source pos and opposite.
         * @param variables Set that stored variables.
         * @return Name of position
         */
        private String addPosToVariable(InternalOpData iData, String posType, String residualPosType, Set<String> variables) {
            String posName = iData.getPos(posType);

            if (InternalOpData.posIsReal(iData.getPos(posType))) {
                //Not add ID if dest(source) pos is merge pos for a source(dest) pos
                if (posHistogramMap.get(posName).size() > 1 && !InternalOpData.posIsMergePos(iData.getPos(residualPosType))) {
                    posName = posName + ":" + iData.getId();
                }
            }

            if ((!iData.hasOperationCountNo() && iData.hasSinglePos()) || posName.equals(TypeVar.POS_OUT)) {
                if (posType.equals("sourcePos")) {
                    posName = posName + TypeVar.POS_PROCESS;
                } else {
                    posName = posName + TypeVar.POS_MOVE;
                }
            }
            variables.add(posName);

            return posName;
        }

        private void addVariablesG0() {
            Set<String> variables = new HashSet<String>(); //Uses Set in orderr to add each variable once

            //Check all preconditions for operation ids. Dest position for found operation ids are stored.
            //These dest positions needs to be refined first time the supremica module is built.
            HashMap<String, HashSet<Integer>> posIdMap = new HashMap<String, HashSet<Integer>>();
            for (InternalOpData externalData : operations.getChildOperations()) {
                String guard = externalData.getCondition();
                for (InternalOpData iData : operations.getChildOperations()) {
                    if (!posIdMap.containsKey(iData.getPos("destPos"))) {
                        posIdMap.put(iData.getPos("destPos"), new HashSet<Integer>());
                    }
                    if (guard.contains(iData.getId().toString())) {
                        posIdMap.get(iData.getPos("destPos")).add(iData.getId());
                        System.out.println("ex: " + externalData.getName() + " in: " + iData.getName() + " pos: " + iData.getPos("destPos"));
                    }
                }
            }

            for (InternalOpData iData : operations.getChildOperations()) {
                String destName = iData.getPos("destPos");

                String name = null;

                //Operation Count------------------------------------------------
                addOperationCountToVariable(iData);
                //---------------------------------------------------------------

                //Dest position--------------------------------------------------
                //Is there need for an initial refinement of the position?
                //Yes, if at least 2 later occuring process operations that have this operation (iData) in their gurad expressions.
                if (posIdMap.get(iData.getPos("destPos")).size() > 1) {
                    destName = destName + ":" + iData.getId();
                }
                //Is there need to refine the position into a m(ove) instance?
                //Yes, if the operation has a counter or the position is OUT (to distinguish the initial from the final out position).
                if ((!iData.hasOperationCountNo() && iData.hasSinglePos()) || iData.getPos("destPos").equals(TypeVar.POS_OUT)) {
                    destName = destName + TypeVar.POS_MOVE;
                }
                variables.add(destName);

                //Connect the position's name to operations. To be used during construction of transtion.
                opIdEFAdestPosMap.put(iData.getId(), destName);
                //---------------------------------------------------------------

                //Source position------------------------------------------------
                name = addPosToVariable(iData, "sourcePos", "destPos", variables);

                //Connect the position's name to operations. To be used during construction of transtion.
                opIdEFAsourcePosMap.put(iData.getId(), name);
                //---------------------------------------------------------------
            }

            //Add variables
            for (String name : variables) {
                smodule.addIntVariable(ps + name, 0, 1, 0, 0);
            }
        }

        private void addVariablesG1() {
            //Add Operation Count to Supremica module
            for (InternalOpData iData : operations.getChildOperations()) {
                addOperationCountToVariable(iData);
            }

            //Add positions to Supremica module
            for (String pos : posRefinementMap.keySet()) {
                for (String refinement : posRefinementMap.get(pos)) {
                    String varName = ps + pos;
                    if (!refinement.equals("1")) {
                        varName = varName + ":" + guardToSupremicaNameTranslation(refinement);
                    }
                    smodule.addIntVariable(varName, 0, 1, 0, 0);
                }
            }
        }

        /**
         * Help class to create transport transitions.<br/>
         * Creates one transition from each item in externalSet to each item in internalSet.<br/>
         * There are some additional requirements on the items in the sets...
         * @param externalSet from position
         * @param internalSet to position
         * @param eventName String between from and to position in transition name
         */
        private void loopTransport(Set<String> externalSet, Set<String> internalSet, String eventName) {
            for (String intDest : externalSet) {
                String dest = ps + intDest;
                for (String intSource : internalSet) {
                    String source = ps + intSource;
                    if (InternalOpData.posIsReal(intDest) && InternalOpData.posIsReal(intSource) && !intDest.equals(intSource)) {
                        SEGA ega = new SEGA();

                        ega.addBasicPositionBookAndUnbook(dest, source);

                        //Create the transition
                        ega.setEvent(transitionName(intDest, eventName, intSource));
                        efa.addStandardSelfLoopTransition(ega);
                    }
                }
            }
        }

        /**
         * Creates one transition for each dest/source position combination.
         */
        private void addTransportTransitionsG0() {
            //Only get one position once-----------------------------------------
            Set<String> destPosSet = new HashSet<String>();
            for (String pos : opIdEFAdestPosMap.values()) {
                destPosSet.add(pos);
            }
            Set<String> sourcePosSet = new HashSet<String>();
            for (String pos : opIdEFAsourcePosMap.values()) {
                sourcePosSet.add(pos);
            }//------------------------------------------------------------------

            //Create transitions to module
            loopTransport(destPosSet, sourcePosSet, "t");
        }

        /**
         * Transport transitions for second round is built upon the positions for transport
         * transitions in <i>nameGuardMap</i>. Additional positions due to
         * position refinement is possible.
         */
        private void addTransportTransitionsG1() {
            for (String name : nameGuardMap.keySet()) {
                String opType = name.split(TypeVar.SEPARATION)[2];
                if (opType.equals(TypeVar.TRANSPORT)) {
                    //String guard = nameGuardMap.get(name);
                    String sPos = name.split(TypeVar.SEPARATION)[1];
                    String dPos = name.split(TypeVar.SEPARATION)[3];

                    //Create transitions between all transport source and dest position, refined positions included
                    for (String sourceRefinement : posRefinementMap.get(sPos)) {
                        for (String destRefinement : posRefinementMap.get(dPos)) {

                            String sourcePosName = sPos;
                            String destPosName = dPos;
                            SEGA ega = new SEGA();

                            if (!sourceRefinement.equals("1")) {
                                sourcePosName = sourcePosName + ":" + guardToSupremicaNameTranslation(sourceRefinement);
                                ega.andGuard(sourceRefinement);
                            }
                            if (!destRefinement.equals("1")) {
                                destPosName = destPosName + ":" + guardToSupremicaNameTranslation(destRefinement);
                                ega.andGuard(destRefinement);
                            }

                            ega.addBasicPositionBookAndUnbook(ps + sourcePosName, ps + destPosName);

                            //Create the transition
                            ega.setEvent(transitionName(sourcePosName, "s" + opType, destPosName));
                            efa.addStandardSelfLoopTransition(ega);
                        }
                    }
                }
            }
        }

        /**
         * To create transitions for counters i.e. process operations.
         */
        private void addProcessTransitionsG0() {
            //go through operations, add guards and actions
            for (InternalOpData iData : operations.getChildOperations()) {

                if (!(iData.hasSinglePos() && iData.hasOperationCountNo())) {
                    SEGA ega = new SEGA();

                    String sPos = opIdEFAsourcePosMap.get(iData.getId());
                    String dPos = opIdEFAdestPosMap.get(iData.getId());

                    //guards and actions regardless of pre-conditions
                    ega.addBasicPositionBookAndUnbook(ps + sPos, ps + dPos);
                    addOpCountToGuardAndAction(iData, ega);

                    //guards based on pre-conditions
                    addGuardBasedOnSPpreCondition(iData, ega);

                    //Create the transition
                    String partOfName = iData.getId().toString();
                    if (iData.getMerge() != null) {
                        partOfName += TypeVar.ED_MERGE + iData.getMerge();
                    }
                    ega.setEvent(transitionName(sPos, partOfName, dPos));
                    efa.addStandardSelfLoopTransition(ega);
                }
            }
        }

        /**
         * Create process transitions with correct guards and actions.
         * @param iData Process operation in SP
         * @param sPos Source position according to process transition
         * @param sourceRefinement Refined source position
         * @param dPos Dest position according to process transition
         * @param destRefinement Refined dest position
         * @param eventNameMiddle Part of event
         */
        private void createProcessTransitions(InternalOpData iData, String sPos, String sourceRefinement, String dPos, String destRefinement, String eventNameMiddle) {
            String sourcePosName = sPos;
            String destPosName = dPos;
            SEGA ega = new SEGA();

            if (!sourceRefinement.equals("1")) {
                sourcePosName = sourcePosName + ":" + guardToSupremicaNameTranslation(sourceRefinement);
                ega.andGuard(removeOwnOpCountInGaurd(sourceRefinement, iData.getId()));
            }
            if (!destRefinement.equals("1")) {
                destPosName = destPosName + ":" + guardToSupremicaNameTranslation(destRefinement);
                ega.andGuard(removeOwnOpCountInGaurd(destRefinement, iData.getId()));
            }

            //basic guards and actions regardless of pre-conditions
            ega.addBasicPositionBookAndUnbook(ps + sourcePosName, ps + destPosName);
            addOpCountToGuardAndAction(iData, ega);

            //guards based on pre-conditions
            addGuardBasedOnSPpreCondition(iData, ega);

            //Create the transition
            ega.setEvent(transitionName(sourcePosName, "s" + eventNameMiddle, destPosName));
            efa.addStandardSelfLoopTransition(ega);
        }

        private void addProcessTransitionsG1() {
            //Get process operation
            HashMap<String, String> processOperationsWithGuardsMap =  new HashMap<String, String>();
            popsGuardMap(processOperationsWithGuardsMap);

            //Go through operations in SP for current product,add guards and actions
            for (InternalOpData iData : operations.getChildOperations()) {

                if (!iData.hasOperationCountNo()) {

                    String opType = null;
                    String sPos = null;
                    String dPos = null;

                    //Connect SP operation with process transition from previous round
                    for (String name : processOperationsWithGuardsMap.keySet()) {
                        opType = name.split(TypeVar.SEPARATION)[2].replaceAll("s", "");
                        if (opType.startsWith(iData.getId().toString())) {
                            sPos = name.split(TypeVar.SEPARATION)[1];
                            dPos = name.split(TypeVar.SEPARATION)[3];
                            break;
                        }
                    }

                    //Create process transitions for the new module in Supremica,
                    //with respect to refinement positions
                    for (String sourceRefinement : posRefinementMap.get(sPos)) {
                        if (posRefinementMap.get(sPos).size()>1 && posRefinementMap.get(dPos).size()>1) {
                            createProcessTransitions(iData, sPos, sourceRefinement, sPos.replaceAll(TypeVar.POS_PROCESS, TypeVar.POS_MOVE), sourceRefinement, opType);
                        } else {
                            for (String destRefinement : posRefinementMap.get(dPos)) {
                                createProcessTransitions(iData, sPos, sourceRefinement, dPos, destRefinement, opType);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Translate precondition in iData from SP syntax to Supremica syntax
         * @param iData Operation to take guard from
         * @param ega Transition where guard should be added
         */
        private void addGuardBasedOnSPpreCondition(InternalOpData iData, SEGA ega) {

            //Create condition
            String condition = iData.getCondition();

            //add precondition to guard
            if (!condition.equals("")) {
                log.info(iData.getName() + " has precondition " + condition);
                String guardPreCon = condition; //Example of raw precondition 18_f A (143_iV19_f)

                //Change all ID to ProductType_ID
                for (InternalOpData i : productTypes.get(productType)) {
                    guardPreCon = guardPreCon.replaceAll(i.getId().toString(), ps + i.getId());
                }

                guardPreCon = guardFromSPtoEFASyntaxTranslation(guardPreCon);

                ega.andGuard(guardPreCon);
                log.info("and guard: " + guardPreCon);
            }
        }

        private void addOpCountToGuardAndAction(InternalOpData iData, SEGA ega) {
            if (!iData.hasOperationCountNo()) {
                ega.andGuard(ps + iData.getId() + TypeVar.EFA_EQUAL + TypeVar.NO); //The operation should not have been performed before
                ega.addAction(ps + iData.getId() + TypeVar.EFA_PLUS_ONE); //Op is now performed once
            }
        }

        /**
         * Creates a variable {0,1} indicating that the operation has been performed.<br/>
         * A process operation has to allow that a count like this is added.<br/>
         * A buffer and process operations for fixture products are good examples of operations that should not have a counter variable.
         * @param iData Operation that counter should be added for.
         */
        private void addOperationCountToVariable(InternalOpData iData) {
            if (!iData.hasOperationCountNo()) {
                smodule.addIntVariable(ps + iData.getId(), 0, 1, 0, null);
            }
        }

        /**
         * Help method to write events/transition names:<br/>
         * @param from
         * @param type
         * @param to
         * @return "Product _ from _ type _ to"
         */
        private String transitionName(String from, String type, String to) {
            return ps + from + TypeVar.SEPARATION + type + TypeVar.SEPARATION + to;
        }

        private String guardFromSPtoEFASyntaxTranslation(String guard) {
            //Change all _i to ==0
            guard = guard.replaceAll(TypeVar.SEPARATION + TypeVar.SP_INITIAL, TypeVar.EFA_EQUAL + TypeVar.NO);
            //Change all _f to ==1
            guard = guard.replaceAll(TypeVar.SEPARATION + TypeVar.SP_FINISH, TypeVar.EFA_EQUAL + TypeVar.YES);
            //Change all A to &
            guard = guard.replaceAll(TypeVar.SP_AND, TypeVar.EFA_AND);
            //Change all V to |
            guard = guard.replaceAll(TypeVar.SP_OR, TypeVar.EFA_OR);

            return guard;
        }

        /**
         * Translation of non-usable event name charaters to useable
         * @param guard non-usable string to translate
         * @return usable translation
         */
        private String guardToSupremicaNameTranslation(String guard) {
//            guard = guard.replace("(", "ll").replace(")", "rr");
//            guard = guard.replace("&", "AA").replace("|", "VV");
//            guard = guard.replace("=", "ee").replace("!", "xx");
            guard = guard.replace("(", "l").replace(")", "r");
            guard = guard.replace("&", "A").replace("|", "V");
            guard = guard.replace("==", "e").replace("!=", "x");
            guard = guard.replace("_", "s");
            return guard;
        }

        /**
         *
         * @return The IDs for the operations that are of this product type. The list is sorted in reversed order.
         */
        private ArrayList<Integer> getOperationIDsforThisProductType() {
            ArrayList al = new ArrayList<Integer>(productTypes.get(productType).size());
            for (InternalOpData i : productTypes.get(productType)) {
                al.add(i.getId());
            }
            Collections.sort(al, Collections.reverseOrder());
            return al;
        }

        private String removeOwnOpCountInGaurd(String guard, Integer id) {
            String prefix = ps + id;
            guard = guard.replaceAll(prefix + TypeVar.EFA_EQUAL + 1, prefix + TypeVar.EFA_EQUAL + 0);
            guard = guard.replaceAll(prefix + TypeVar.EFA_UNEQUAL + 0, prefix + TypeVar.EFA_UNEQUAL + 1);
            return guard;
        }

        private void popsGuardMap(HashMap<String, String> map) {
            for (String name : nameGuardMap.keySet()) {
                String guard = nameGuardMap.get(name);
                //String source = name.split(TypeVar.SEPARATION)[1];
                String type = name.split(TypeVar.SEPARATION)[2];
                //String dest = name.split(TypeVar.SEPARATION)[3];

                if (!type.equals(TypeVar.TRANSPORT)) {
                    map.put(name, guard);
                }
            }
        }
    }
}
