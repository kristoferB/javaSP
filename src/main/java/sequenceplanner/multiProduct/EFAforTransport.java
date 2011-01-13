package sequenceplanner.multiProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.OperationData;

/**
 * To create Supremica modules for transport planning
 * @author patrik
 */
public class EFAforTransport {

    static Logger log = Logger.getLogger(EFAforTransport.class);
    private Model model = null;
    private HashMap<String, ArrayList<InternalOpData>> productTypes;

    public EFAforTransport(Model model) {
        this.model = model;
        productTypes = new HashMap<String, ArrayList<InternalOpData>>();
        init();
    }

    private void init() {
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
            InternalOpData iOpData = new InternalOpData((OperationData) model.getOperationRoot().getChildAt(i).getNodeData());
            if (!productTypes.containsKey(iOpData.getProductType())) {
                productTypes.put(iOpData.getProductType(), new ArrayList<InternalOpData>());
            }
            productTypes.get(iOpData.getProductType()).add(iOpData);
        }
    }

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
        private Integer generation;
        private SModule smodule = new SModule("temp");
        private SEFA efa;
        private HashMap<Integer, String> opIdEFAsourcePosMap = new HashMap<Integer, String>();
        private HashMap<Integer, String> opIdEFAdestPosMap = new HashMap<Integer, String>();
        private HashMap<String, String> nameGuardMap = null;
        private HashMap<String, HashSet<String>> posRefinementMap = new HashMap<String, HashSet<String>>();
        private HashMap<String, ArrayList<Integer>> posHistogramMap = new HashMap<String, ArrayList<Integer>>();
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

            //Single Location automaton------------------------------------------
            efa = new SEFA(productType, smodule);
            efa.addState(TypeVar.LOCATION, true, true);
            //-------------------------------------------------------------------

            switch (generation) {//----------------------------------------------
                case 0:
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
                    e.error("generation is set out of bounds!");
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

        private void initG0() {
            for (InternalOpData iData : productTypes.get(productType)) {
                if (!posHistogramMap.containsKey(iData.getSourcePos())) {
                    posHistogramMap.put(iData.getSourcePos(), new ArrayList<Integer>());
                }
                posHistogramMap.get(iData.getSourcePos()).add(iData.getId());
                if (!iData.hasSinglePos()) {
                    if (!posHistogramMap.containsKey(iData.getDestPos())) {
                        posHistogramMap.put(iData.getDestPos(), new ArrayList<Integer>());
                    }
                    posHistogramMap.get(iData.getDestPos()).add(iData.getId());
                }
            }
        }

        private void initG1() {
            //fill posRefinementMap----------------------------------------------
            for (String name : nameGuardMap.keySet()) {
                String guard = nameGuardMap.get(name);
                String source = name.split(TypeVar.SEPARATION)[1];
                String dest = name.split(TypeVar.SEPARATION)[3];

                if (!posRefinementMap.containsKey(source)) {
                    posRefinementMap.put(source, new HashSet<String>());
                }
                posRefinementMap.get(source).add(guard); //Refine source position with guard

                if (!posRefinementMap.containsKey(dest)) {
                    posRefinementMap.put(dest, new HashSet<String>());
                }
                posRefinementMap.get(dest).add("1"); //Add dest position but should not be refined
            }//------------------------------------------------------------------

            //Remove basic guard from refined positions--------------------------
            for (String pos : posRefinementMap.keySet()) {
                if (posRefinementMap.get(pos).size()>1) {
                    posRefinementMap.get(pos).remove("1");
                }
            }//------------------------------------------------------------------
        }

        private void addVariablesG0() {
            Set<String> variables = new HashSet<String>(); //Uses Set just to add each variable once

            for (InternalOpData iData : productTypes.get(productType)) {
                String sourceName = iData.getSourcePos();
                String destName = iData.getDestPos();

                //Operation Count
                addOperationCountToVariable(iData);

                //Source pos
                if (iData.sourcePosIsReal()) {
                    if (posHistogramMap.get(iData.getSourcePos()).size() > 1 && !iData.destPosIsMergePos()) { //Not add ID if dest pos is merge pos
                        sourceName = sourceName + ":" + iData.getId();
                    }
                    if (!iData.hasOperationCountNo() && iData.hasSinglePos()) {
                        sourceName = sourceName + TypeVar.POS_PROCESS;
                    }
                    variables.add(sourceName);
                }
                opIdEFAsourcePosMap.put(iData.getId(), sourceName);

                //Dest pos
                if (iData.destPosIsReal()) {
                    if (posHistogramMap.get(iData.getDestPos()).size() > 1 && !iData.sourcePosIsMergePos()) { //Not add ID if source pos is merge pos
                        destName = destName + ":" + iData.getId();
                    }
                    if (!iData.hasOperationCountNo() && iData.hasSinglePos()) {
                        destName = destName + TypeVar.POS_MOVE;
                    }
                    variables.add(destName);
                }
                opIdEFAdestPosMap.put(iData.getId(), destName);
            }

            //Add variables
            for (String name : variables) {
                smodule.addIntVariable(ps + name, 0, 1, 0, 0);
            }

            //Add out_p pos if needed
            String sourcePosOut = "";
            if (opIdEFAsourcePosMap.containsValue(TypeVar.POS_OUT)) {
                sourcePosOut = TypeVar.POS_OUT + TypeVar.POS_PROCESS;
                smodule.addIntVariable(ps + sourcePosOut, 0, 1, 0, 0);
            }
            //Add out_m pos if needed
            String destPosOut = "";
            if (opIdEFAdestPosMap.containsValue(TypeVar.POS_OUT)) {
                destPosOut = TypeVar.POS_OUT + TypeVar.POS_MOVE;
                smodule.addIntVariable(ps + destPosOut, 0, 1, 0, 0);
            }
            //Update hashmaps if needed
            if (!sourcePosOut.isEmpty() || !destPosOut.isEmpty()) {
                for (InternalOpData iData : productTypes.get(productType)) {
                    if (iData.getSourcePos().equals(TypeVar.POS_OUT) && !sourcePosOut.isEmpty()) {
                        opIdEFAsourcePosMap.put(iData.getId(), sourcePosOut);
                    }
                    if (iData.getDestPos().equals(TypeVar.POS_OUT) && !destPosOut.isEmpty()) {
                        opIdEFAdestPosMap.put(iData.getId(), destPosOut);
                    }
                }
            }

            //Add mrg pos if needed
            if (posHistogramMap.containsKey(TypeVar.POS_MERGE)) {
                smodule.addIntVariable(ps + TypeVar.POS_MERGE, 0, 1, 0, 0);
            }

        }

        private void addVariablesG1() {
            //Operation Count
            for (InternalOpData iData : productTypes.get(productType)) {
                addOperationCountToVariable(iData);
            }

            //positions
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

        private void addTransportTransitionsG0() {
            //Only get one pos once----------------------------------------------
            Set<String> destPosSet = new HashSet<String>();
            for (String pos : opIdEFAdestPosMap.values()) {
                destPosSet.add(pos);
            }
            Set<String> sourcePosSet = new HashSet<String>();
            for (String pos : opIdEFAsourcePosMap.values()) {
                sourcePosSet.add(pos);
            }//------------------------------------------------------------------

            //opIdEFAdestPosMap.values()
            for (String intDest : destPosSet) {
                String dest = ps + intDest;
                for (String intSource : sourcePosSet) {
                    String source = ps + intSource;
                    if (InternalOpData.posIsReal(intDest) && InternalOpData.posIsReal(intSource) && !intDest.equals(intSource)) {
                        SEGA ega = new SEGA();

                        ega.addBasicPositionBookAndUnbook(dest, source);

                        //Create the transition
                        ega.setEvent(transitionName(intDest, TypeVar.TRANSPORT, intSource));
                        efa.addStandardSelfLoopTransition(ega);
                    }
                }
            }
        }

        private void addTransportTransitionsG1() {
            for (String name : topsGuardMap().keySet()) {
                //String guard = nameGuardMap.get(name);
                String sPos = name.split(TypeVar.SEPARATION)[1];
                String opType = name.split(TypeVar.SEPARATION)[2];
                String dPos = name.split(TypeVar.SEPARATION)[3];

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
                        ega.setEvent(transitionName(sourcePosName, opType, destPosName));
                        efa.addStandardSelfLoopTransition(ega);
                    }
                }
            }
        }

        private void addProcessTransitionsG0() {
            //go through operations, add guards and actions
            for (InternalOpData iData : productTypes.get(productType)) {

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
                    ega.setEvent(transitionName(sPos, iData.getId().toString(), dPos));
                    efa.addStandardSelfLoopTransition(ega);
                }
            }
        }

        private void addProcessTransitionsG1() {
            //go through operations, add guards and actions
            for (InternalOpData iData : productTypes.get(productType)) {

                if (!(iData.hasSinglePos() && iData.hasOperationCountNo())) {

                    String opType = null;
                    String sPos = null;
                    String dPos = null;

                    for (String name : popsGuardMap().keySet()) {
                        opType = name.split(TypeVar.SEPARATION)[2];
                        if (opType.equals(iData.getId().toString())) {
                            sPos = name.split(TypeVar.SEPARATION)[1];
                            dPos = name.split(TypeVar.SEPARATION)[3];
                            break;
                        }
                    }

                    for (String sourceRefinement : posRefinementMap.get(sPos)) {
                        for (String destRefinement : posRefinementMap.get(dPos)) {

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
                            ega.setEvent(transitionName(sourcePosName, opType, destPosName));
                            efa.addStandardSelfLoopTransition(ega);
                        }
                    }
                }
            }
        }

        private void addGuardBasedOnSPpreCondition(InternalOpData iData, SEGA ega) {
            if (!iData.getRawPrecondition().isEmpty()) {
                log.info(iData.getName() + " has precondition " + iData.getRawPrecondition());
                String guardPreCon = iData.getRawPrecondition(); //Example of raw precondition 18_f A (143_iV19_f)

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

        private void addOperationCountToVariable(InternalOpData iData) {
            if (!iData.hasOperationCountNo()) {
                smodule.addIntVariable(ps + iData.getId(), 0, 1, 0, null);
            }
        }

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

        private HashMap<String, String> topsGuardMap() {
            HashMap<String, String> topsMap = new HashMap<String, String>(nameGuardMap.size());
            for (String name : nameGuardMap.keySet()) {
                String guard = nameGuardMap.get(name);
                //String source = name.split(TypeVar.SEPARATION)[1];
                String type = name.split(TypeVar.SEPARATION)[2];
                //String dest = name.split(TypeVar.SEPARATION)[3];

                if (type.equals(TypeVar.TRANSPORT)) {
                    topsMap.put(name, guard);
                }
            }
            return topsMap;
        }

        private HashMap<String, String> popsGuardMap() {
            HashMap<String, String> map = new HashMap<String, String>(nameGuardMap.size());
            for (String name : nameGuardMap.keySet()) {
                String guard = nameGuardMap.get(name);
                //String source = name.split(TypeVar.SEPARATION)[1];
                String type = name.split(TypeVar.SEPARATION)[2];
                //String dest = name.split(TypeVar.SEPARATION)[3];

                if (!type.equals(TypeVar.TRANSPORT)) {
                    map.put(name, guard);
                }
            }
            return map;
        }
    }
}
