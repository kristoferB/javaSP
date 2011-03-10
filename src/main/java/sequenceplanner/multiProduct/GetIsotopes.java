package sequenceplanner.multiProduct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.waters.model.des.TransitionProxy;

import org.supremica.automata.Arc;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import org.supremica.automata.LabeledEvent;
import org.supremica.automata.Project;
import org.supremica.automata.algorithms.AutomataSynthesizer;
import org.supremica.automata.algorithms.EquivalenceRelation;
import org.supremica.automata.algorithms.SynchronizationOptions;
import org.supremica.automata.algorithms.SynchronizationType;
import org.supremica.automata.algorithms.SynthesisAlgorithm;
import org.supremica.automata.algorithms.SynthesisType;
import org.supremica.automata.algorithms.SynthesizerOptions;
import org.supremica.automata.algorithms.minimization.AutomatonMinimizer;
import org.supremica.automata.algorithms.minimization.MinimizationOptions;
import org.supremica.gui.VisualProject;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrikm
 */
public class GetIsotopes {

    private String mProductType = "";
    private UnitTypeWrapper mUTRoot = new UnitTypeWrapper();
    private OpWrapper mOpRoot = new OpWrapper();
    private Model mModel = null;
    private TreeNode mParentOp = null;
    private SModule mModule = new SModule("temp");
    private SEFA mEFA = null;
    private Project mProject = null;
    private Automata mAutomata = null;
    private Automaton mAutomaton = null;

    public GetIsotopes(Model inModel, String inProductType) {
        mOpRoot.mChildren = new HashSet<OpWrapper>();
        mProductType = inProductType;
        mModel = inModel;

        if (!run()) {
            System.out.println("Stoped!");
        }

    }

    private boolean run() {
        //get process operations
        getOperations(mModel.getOperationRoot(), mModel.getResourceRoot().getId());

        //Test ids
        if (!testIDs()) {
            System.out.println("Problem with ids!");
            return false;
        }

        //init module and shared variables
        initModule();
        initSharedVaribles();

        //check that "inn" and "out" unit types exist
        if (!checkUnitTypeCombinations()) {
            System.out.println("Problem with unit types for operations: <code>inn</code> or <code>out</code> is not present");
            return false;
        }

        //loop process operations
        loopUserGivenOperations();

        //loop transport operations
        loopTransportOperations();

        //flatten out
        if (!flattenOut()) {
            System.out.println("Problem with flatten out!");
            return false;
        }

        //synthesis
        if (!synthesis()) {
            System.out.println("Problem with synthesis!");
            return false;
        }

        //Alphabet projection
        if (!projectAlphabet()) {
            System.out.println("Problem with projection!");
            return false;
        }

        //observation equivialence minimization
        if (!observationEquivalentMinimization()) {
            System.out.println("Problem with minimization!");
            return false;
        }

        createParentOperationInOperationTree();

        //loop process transitions and create new operations
        if (!loopProcessTransitions()) {
            System.out.println("Problem with transition parsing: process");
            return false;
        }
        //loop transport transitinos and create new operations
        if (!loopTransportTransitions()) {
            System.out.println("Problem with transition parsing: transport");
            return false;
        }

        saveOpertions();

        return true;
    }

    private boolean checkUnitTypeCombinations() {
        Set<UnitTypeWrapper> returnSet = new HashSet<UnitTypeWrapper>();

        mOpRoot.getUnitTypeSet("source", returnSet, new HashSet<String>());
        for (final UnitTypeWrapper ut : returnSet) {
            if (ut.mName.equals("out")) {
                return false;
            }
        }

        returnSet.clear();
        mOpRoot.getUnitTypeSet("dest", returnSet, new HashSet<String>());
        for (final UnitTypeWrapper ut : returnSet) {
            if (ut.mName.equals("inn")) {
                return false;
            }
        }

        return true;
    }

    private void viewAutomaton(Automaton iAutomaton) {
        VisualProject vp = new VisualProject();
        vp.addAutomaton(new Automaton(iAutomaton));
        try {
            vp.getAutomatonViewer(iAutomaton.getName());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Adds operations to <code>mParentOp</code> from transport transitions
     * @return <code>true</code> if ok else <code>false</code>
     */
    private boolean loopTransportTransitions() {
        if (mAutomaton != null) {
            for (final TransitionProxy t : mAutomaton.getTransitions()) {
                final String event = t.getEvent().getName();
                final String sourceState = t.getSource().getName();
                final String targetState = t.getTarget().getName();

                if (event.startsWith("t")) {
                    if (event.split("_").length == 3) {
                        final String sourceUnitType = event.split("_")[1];
                        final String destUnitType = event.split("_")[2];

                        String name = "";
                        Integer value = getUnitTypeIsotopeValue(sourceUnitType, sourceState);
                        if (value != null) {
                            name += sourceUnitType + value;
                            name += "_t_";
                            value = getUnitTypeIsotopeValue(destUnitType, targetState);
                            if (value != null) {
                                name += destUnitType + value;

                                //Fill operation helper and create operation
                                HashMap<String, String> opPropMap = new HashMap<String, String>(2);
                                opPropMap.put("name", name);
                                opPropMap.put("description", "");
                                createOperationInOperationTree(opPropMap);

                            } else {
                                System.out.println("error 1");
                                return false;
                            }
                        } else {
                            System.out.println("error 2");
                            return false;
                        }

                    } else {
                        System.out.println("Transport transition events not as expected!");
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private Integer getUnitTypeIsotopeValue(String iUnitType, String iState) {
        final UnitTypeWrapper unitType = mUTRoot.getUnitTypeChild(iUnitType);
        if (unitType != null) {
            return unitType.getUnitTypeChild(iState).mVariableValue;
        }
        return null;
    }

    /**
     *
     * Adds operations to <code>mParentOp</code> from process transitions</br>
     * Adds unit isotopes to unit types in <code>mUTRoot</code>
     * Takes no consideration to process operations with single unit type
     * 
     * @return <code>true</code> if ok else <code>false</code>
     */
    private boolean loopProcessTransitions() {
        HashMap<String, String> isoisoMap = new HashMap<String, String>();
        if (mAutomaton != null) {
            for (final TransitionProxy t : mAutomaton.getTransitions()) {
                final String event = t.getEvent().getName();
                final String source = t.getSource().getName();
                final String target = t.getTarget().getName();

                if (event.startsWith("e")) {
                    //process transition
                    OpWrapper op = mOpRoot.getOpFromEvent(event);

                    //How to handle process operations that start and finish in the same unit type
                    //Probelm if op.singleUnitType() == true but the automaton enhance the dest unit types.
                    /*
                    //Mapping of unit types
                    isoisoMap.put(source, source);
                    if (op.singleUnitType()) {
                    isoisoMap.put(target, source);
                    } else {
                    isoisoMap.put(target, target);
                    }
                    
                    
                    op.getUnitType("source").unitTypeFactory(op, isoisoMap.get(source));
                    op.getUnitType("dest").unitTypeFactory(op, isoisoMap.get(target));
                     */

                    //Add unit isotopes to unit types
                    op.getUnitType("source").unitTypeFactory(op, source);
                    op.getUnitType("dest").unitTypeFactory(op, target);

//                    UnitTypeWrapper ut = null;
//                    ut = op.getUnitType("source").unitTypeFactory(op, source);
//                    System.out.println(op.mName + " source: " + source + " ut: " + op.getUnitType("source").mName + " isotope: " + ut.mName);
//                    ut = op.getUnitType("dest").unitTypeFactory(op, target);
//                    System.out.println(op.mName + " target: " + target + " ut: " + op.getUnitType("dest").mName + " isotope: " + ut.mName);

//                    String sourceIsotope = op.getUnitType("source").mName + op.getUnitType("source").getUnitTypeChild(source).mVariableValue;
//                    String destIsotope = op.getUnitType("dest").mName + op.getUnitType("dest").getUnitTypeChild(target).mVariableValue;
//                    System.out.println(op.mName + " from: " + sourceIsotope + " to: " + destIsotope);

                    //Create a new operation
                    createOperationInOperationTree(op, source, target);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void createParentOperationInOperationTree() {
        mModel.setCounter(mModel.getCounter() + 1);
        OperationData data = new OperationData(mProductType + "_for_synthesis", mModel.getCounter());
//        data.setDescription(TypeVar.ED_PRODUCT_TYPE + TypeVar.DESC_VALUESEPARATION + mProductType);
        mParentOp = new TreeNode(data);
        mModel.getOperationRoot().insert(mParentOp);
    }

    private boolean createOperationInOperationTree(HashMap<String, String> iOperationHelper) {
        if (iOperationHelper.containsKey("name")) {
            if (iOperationHelper.get("name") != null) {
                mModel.setCounter(mModel.getCounter() + 1);
                OperationData opData = new OperationData(iOperationHelper.get("name"), mModel.getCounter());
                opData.setDescription(iOperationHelper.get("description"));
                mParentOp.insert(new TreeNode(opData));
                return true;
            }
        }
        return false;
    }

    private void createOperationInOperationTree(OpWrapper iOp, String iSourceState, String iTargetState) {
        //Product type
        String productType = TypeVar.ED_PRODUCT_TYPE + TypeVar.DESC_VALUESEPARATION + iOp.mProductType;

        //Source unit type isotope
        String sourceIsotope = iOp.getUnitType("source").mName + iOp.getUnitType("source").getUnitTypeChild(iSourceState).mVariableValue;
        String sourcePos = TypeVar.ED_SOURCE_POS + TypeVar.DESC_VALUESEPARATION + sourceIsotope;

        //Dest unit type isotope
        String destIsotope = iOp.getUnitType("dest").mName + iOp.getUnitType("dest").getUnitTypeChild(iTargetState).mVariableValue;
        String destPos = TypeVar.ED_DEST_POS + TypeVar.DESC_VALUESEPARATION + destIsotope;

        //Fill operation helper and create operation
        HashMap<String, String> opPropMap = new HashMap<String, String>(2);
        opPropMap.put("name", sourceIsotope + "_op_" + destIsotope);
        opPropMap.put("description", productType + " " + TypeVar.DESC_KEYSEPARATION + " " + sourcePos + " " + TypeVar.DESC_KEYSEPARATION + " " + destPos);
        createOperationInOperationTree(opPropMap);
    }

    private void saveOpertions() {
        TreeNode[] op = new TreeNode[mModel.getOperationRoot().getChildCount()];
        for (int i = 0; i < mModel.getOperationRoot().getChildCount(); ++i) {
            op[i] = mModel.getOperationRoot().getChildAt(i);
        }
        mModel.saveOperationData(op);
    }

    private boolean synthesis() {
        if (mProject != null) {
            mAutomata = (Automata) mProject;

            SynthesizerOptions syntho = new SynthesizerOptions();
            syntho.setSynthesisType(SynthesisType.NONBLOCKING);
            syntho.setSynthesisAlgorithm(SynthesisAlgorithm.MONOLITHIC);
            syntho.setPurge(true);

            SynchronizationOptions syncho = new SynchronizationOptions();
            syncho.setSynchronizationType(SynchronizationType.FULL);

            AutomataSynthesizer as = new AutomataSynthesizer(mAutomata, syncho, syntho);

            try {
                mAutomata = as.execute();
                mAutomaton = mAutomata.getFirstAutomaton();
                mAutomaton.setName("Supervisor");
                viewAutomaton(mAutomaton);
                return true;
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean projectAlphabet() {
        if (mAutomata != null) {
            if (mAutomata.nbrOfAutomata() == 1) {
                mAutomaton = mAutomata.getFirstAutomaton();

                Iterator<Arc> arcIt = mAutomaton.arcIterator();
                for (; arcIt.hasNext();) {
                    Arc arc = arcIt.next();
                    final String event = arc.getLabel();

                    if (event.startsWith("e")) { //only interested in process transitions
                        final OpWrapper op = mOpRoot.getOpFromEvent(event);
                        if (event.length() > op.getEventName().length()) { //only interested in event labels that are longer than normal
                            if (!mAutomaton.getAlphabet().contains(op.getEventName())) { //add new event if it is needed
                                mAutomaton.getAlphabet().addEvent(new LabeledEvent(op.getEventName()));
                            }
                            final LabeledEvent labeledEvent = mAutomaton.getAlphabet().getEvent(op.getEventName());
                            arc.setEvent(labeledEvent);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean observationEquivalentMinimization() {
        if (mAutomata != null) {
            if (mAutomata.nbrOfAutomata() == 1) {
                MinimizationOptions options = new MinimizationOptions();
                options.setIgnoreMarking(true);
                options.setMinimizationType(EquivalenceRelation.OBSERVATIONEQUIVALENCE);

                AutomatonMinimizer minimizer = new AutomatonMinimizer(mAutomata.getFirstAutomaton());
                try {
                    mAutomaton = minimizer.getMinimizedAutomaton(options);
                    mAutomaton.setName("Minimized supervisor");
                    viewAutomaton(mAutomaton);
                    return true;
                } catch (Exception e) {
                    System.out.println(e.toString());
                    return false;
                }
            }
        }
        return false;
    }

    private boolean flattenOut() {
        mProject = mModule.getDFA();
        if (mProject != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds all transport operation combinations possible based on unit types in process operations
     */
    private void loopTransportOperations() {

        Set<UnitTypeWrapper> startUTSet = new HashSet<UnitTypeWrapper>();
        Set<String> excludedUTSet = new HashSet<String>();
        excludedUTSet.add("out");
        mOpRoot.getUnitTypeSet("dest", startUTSet, excludedUTSet);

        Set<UnitTypeWrapper> finishUTSet = new HashSet<UnitTypeWrapper>();
        excludedUTSet.clear();
        excludedUTSet.add("inn");
        mOpRoot.getUnitTypeSet("source", finishUTSet, excludedUTSet);

        for (UnitTypeWrapper startUT : startUTSet) {
            for (UnitTypeWrapper finishUT : finishUTSet) {
                String name = "t_" + startUT.mName + "_" + finishUT.mName;
                SEGA ega = new SEGA(name);

                //Guards
                ega.andGuard("ut==" + startUT.mVariableValue);
                ega.andGuard("tok==1");

                //Action
                ega.addAction("ut=" + finishUT.mVariableValue);
                ega.addAction("tok=0");

                mEFA.addStandardSelfLoopTransition(ega);
            }
        }
    }

    private void initSharedVaribles() {
        //Unit types
        Integer range = mUTRoot.mChildren.size() - 1;
        Integer startUT = mUTRoot.getUnitTypeChild("inn").mVariableValue;
        Integer finishUT = mUTRoot.getUnitTypeChild("out").mVariableValue;
        mModule.addIntVariable("ut", 0, range, startUT, finishUT);

        //transport or process
        mModule.addIntVariable("tok", 0, 1, 0, null);
    }

    /**
     * Adds process operations to module
     */
    private void loopUserGivenOperations() {
        for (OpWrapper op : mOpRoot.mChildren) {
            SEGA ega = new SEGA("e" + op.mId);

            mModule.addIntVariable("c" + op.mId, 0, 1, 0, null);

            //Guards
            ega.andGuard("ut==" + op.getUnitType("source").mVariableValue);
            ega.andGuard("c" + op.mId + "==0");
            addGuardBasedOnSPpreCondition(op, ega);

            //Actions
            ega.addAction("ut=" + op.getUnitType("dest").mVariableValue);
            ega.addAction("c" + op.mId + "=1");
            ega.addAction("tok=1");

            mEFA.addStandardSelfLoopTransition(ega);
        }
    }

    private void addGuardBasedOnSPpreCondition(OpWrapper iOp, SEGA ioEga) {

        //Create condition
        String condition = iOp.mCondition;

        //add precondition to guard
        if (!condition.equals("")) {
            System.out.println(iOp.mName + " has precondition " + condition);
            String guardPreCon = condition; //Example of raw precondition 18_f A (143_iV19_f)

            //Change all ID to cID
            for (OpWrapper op : mOpRoot.mChildren) {
                guardPreCon = guardPreCon.replaceAll(op.mId.toString(), "c" + op.mId);
            }

            guardPreCon = guardFromSPtoEFASyntaxTranslation(guardPreCon);

            ioEga.andGuard(guardPreCon);
            System.out.println("and guard: " + guardPreCon);
        }
    }

    private void initModule() {
        mEFA = new SEFA(mProductType, mModule);
        mEFA.addState(TypeVar.LOCATION, true, true);
    }

    private void getOperations(TreeNode iTree, int iRootId) {
        for (int i = 0; i < iTree.getChildCount(); ++i) {
            InternalOpData iOpData = new InternalOpData((OperationData) iTree.getChildAt(i).getNodeData());

            if (iOpData.getProductType().equals(mProductType)) {
                OpWrapper opW = new OpWrapper();
                mOpRoot.mChildren.add(opW);
                opW.mName = iOpData.getName();
                opW.mId = iOpData.getId();
                opW.mProductType = mProductType;
                opW.mCondition = iOpData.getCondition();

                System.out.println(opW.mName);

                //Add source
                opW.setUnitType("source", mUTRoot.unitTypeFactory(opW, iOpData.getSourcePos()));

                //Add dest
                opW.setUnitType("dest", mUTRoot.unitTypeFactory(opW, iOpData.getDestPos()));

                getOperations(iTree.getChildAt(i), iRootId);
            }
        }
    }

    /**
     * Method in this class can't handle IDs that are suffix or prefix to each other, e.g. 18 and 118
     * @return true if IDs are ok else false
     */
    private boolean testIDs() {
        String test = "";
        for (OpWrapper opW : mOpRoot.mChildren) {
            if (test.contains(opW.mId.toString())) {
                return false;
            } else {
                test = test + opW.mId.toString() + TypeVar.SEPARATION;
            }
        }
        return true;
    }

    private String guardFromSPtoEFASyntaxTranslation(String ioGuard) {
        //Change all _i to ==0
        ioGuard = ioGuard.replaceAll(TypeVar.SEPARATION + TypeVar.SP_INITIAL, TypeVar.EFA_EQUAL + TypeVar.NO);
        //Change all _f to ==1
        ioGuard = ioGuard.replaceAll(TypeVar.SEPARATION + TypeVar.SP_FINISH, TypeVar.EFA_EQUAL + TypeVar.YES);
        //Change all A to &
        ioGuard = ioGuard.replaceAll(TypeVar.SP_AND, TypeVar.EFA_AND);
        //Change all V to |
        ioGuard = ioGuard.replaceAll(TypeVar.SP_OR, TypeVar.EFA_OR);

        return ioGuard;
    }
}
