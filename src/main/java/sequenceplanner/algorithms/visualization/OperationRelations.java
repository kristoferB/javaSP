package sequenceplanner.algorithms.visualization;

import java.util.HashSet;
import java.util.Set;
import net.sourceforge.waters.model.des.StateProxy;
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
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 * Class to get the relations between a set of operation with respect to some superset.<br/>
 * @author patrik
 */
public class OperationRelations implements IOperationRelations {

    private Set<Integer> mAllOperationSet = new HashSet<Integer>(); //All operations
    private Model mModel = null;
    private SModule mModule = new SModule("temp");
    private SEFA mEfa = new SEFA("some name", mModule);
    private Automata mAutomata = null;
    private Automaton mAutomaton = null;
    private ISopNode mSopRoot = new SopNode();
    private ROperationToolbox mROpToolbox = new ROperationToolbox();

    public OperationRelations(final Model iModel) {
        this.mModel = iModel;
    }

    @Override
    public boolean identifyRelations() {
        //Test ids
        if (!mModule.testIDs(mAllOperationSet)) {
            System.out.println("Problem with ids!");
            return false;
        }

        //Translate operations to EFA
        if (!translateToEFA()) {
            System.out.println("Problem with translation from op to efa!");
            return false;
        }

        //flatten out (EFA->DFA, Module -> Automata)
        if (!flattenOut()) {
            System.out.println("Problem with flatten out!");
            return false;
        }

        //synthesis
        if (!synthesis()) {
            System.out.println("Problem with synthesis!");
            return false;
        }

        //Relation identification
        if (!relationIdentification()) {
            System.out.println("Problem with relation identification!");
            return false;
        }

        //Create operation nodes as SopNode:s
        if (!createOperationNodes()) {
            System.out.println("Problem with operation node creation!");
            return false;
        }

        System.out.println("----------------------------------");
        for (final Integer i : mAllOperationSet) {
            System.out.println(i);
        }
        System.out.println("----------------------------------");
        for (final IROperation r : mROpToolbox.getmRelationOperationSet()) {
            ROperation rr = (ROperation) r;
            System.out.println(rr.getStringId());
        }
        System.out.println("----------------------------------");
        return true;
    }

    /**
     * Add an id to set for all operations
     * @param iId id to add
     * @return true if set did not contained this id else false
     */
    public boolean addToAllOperationSet(final int iId) {
        return mAllOperationSet.add(Integer.valueOf(iId));
    }

    public boolean addToRelationOperationSet(final int iId, final boolean iHasToFinish) {
        addToAllOperationSet(iId);
        return mROpToolbox.addToRelationOperationSet(iId, iHasToFinish);
    }

    private boolean relationIdentification() {
        //Init of map for storage of states--------------------------------------
        //Remove Single EFA from automaton name (the name is Single) + extra substrings
        //From sup(oX||oY||Single) -> oX||oY
        mROpToolbox.setmStateNameExplanation(mAutomaton.getName().replaceAll("sup\\(", "").replaceAll("\\)", ""));
        mROpToolbox.setmStateNameExplanation(mROpToolbox.getmStateNameExplanation().replaceAll("\\|\\|Single\\|\\|", "\\|\\|").replaceAll("\\|\\|Single", "").replaceAll("Single\\|\\|", ""));
        //-----------------------------------------------------------------------

        //Loop states and events to fill map-------------------------------------
        for (StateProxy sp : mAutomaton.getStates()) {
            String stateName = sp.getName();
            for (Arc arc : mAutomaton.getStateWithName(stateName).getOutgoingArcs()) {
                //Remove the single EFA location "pm"
                stateName = stateName.replaceAll(".pm.", ".").replaceAll(".pm", "").replaceAll("pm.", "");
                String eventName = arc.getLabel();
                if (eventName.contains("up")) {
                    eventName = eventName.substring(0, eventName.indexOf("up") + 2);
                } else {//"down"
                    eventName = eventName.substring(0, eventName.indexOf("down") + 4);
                }
                if (mROpToolbox.getmEventStateSetMap().keySet().contains(eventName)) {
                    mROpToolbox.getmEventStateSetMap().get(eventName).add(stateName);
                }
            }
        }//----------------------------------------------------------------------

        //Find in what locations for other operations the events of an operation can take place
        mROpToolbox.findEventOperationRelations();
        //-----------------------------------------------------------------------

        //Fill relations to Map in each operation ROperation for later look up---
        mROpToolbox.fillOperationRelations();
        //-----------------------------------------------------------------------

        return true;
    }

    private boolean createOperationNodes() {
        mSopRoot = new SopNode();
        for (final IROperation iOp : mROpToolbox.getmRelationOperationSet()) {
            mSopRoot.addNode(getOperationData(iOp));
        }
        return true;
    }

    public SModule getSModule() {
        return mModule;
    }

    public boolean getOperationIds() {
        getOperations(mModel.getOperationRoot(), mModel.getOperationRoot().getId());
        return true;
    }

    private void getOperations(TreeNode iTree, int iRootId) {
        for (int i = 0; i < iTree.getChildCount(); ++i) {
            OperationData opData = (OperationData) iTree.getChildAt(i).getNodeData();

            addToAllOperationSet(opData.getId());

            getOperations(iTree.getChildAt(i), iRootId);
        }
    }

    private IROperation getRelationOperationFromId(final int iId) {
        for (final IROperation iOp : mROpToolbox.getmRelationOperationSet()) {
            if (iOp.equals(iId)) {
                return iOp;
            }
        }
        return null;
    }

    private boolean synthesis() {
        if (mAutomata != null) {

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
                return true;
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean flattenOut() {
        mAutomata = (Automata) mModule.getDFA();
        if (mAutomata != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean translateToEFA() {
        SEGA ega;
        mEfa.addState("pm", true, true);

        for (final Integer i : mAllOperationSet) {
            OperationData opData = (OperationData) getOperationData(i);
            final int id = opData.getId();
            final String varName = "o" + id;
            IROperation iOp = getRelationOperationFromId(id);

            //Add integer variable for operation---------------------------------
            Integer marking = null;
            if (iOp != null) {
                if (iOp.hasToFinish()) {
                    marking = 2;
                }
            }
            mModule.addIntVariable(varName, 0, 2, 0, marking);
            //-------------------------------------------------------------------

            //Add transition to start execute operation--------------------------
            ega = new SEGA("e" + id + "up");
            if (iOp != null) {
                mROpToolbox.getmEventStateSetMap().put(ega.getEvent(), new HashSet<String>());
            }
            ega.andGuard(varName + "==0");
            ega.addGuardBasedOnSPCondition(opData.getRawPrecondition(), "o", mAllOperationSet);
            ega.addAction(varName + "=1");
            mEfa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------

            //Add transition to finish execute operation-------------------------
            ega = new SEGA("e" + id + "down");
            if (iOp != null) {
                mROpToolbox.getmEventStateSetMap().put(ega.getEvent(), new HashSet<String>());
            }
            ega.andGuard(varName + "==1");
            ega.addGuardBasedOnSPCondition(opData.getRawPostcondition(), "o", mAllOperationSet);
            ega.addAction(varName + "=2");
            mEfa.addStandardSelfLoopTransition(ega);
            //-------------------------------------------------------------------
        }

        return true;
    }

    @Override
    public OperationData getOperationData(final IROperation iOperation) {
        return getOperationData(iOperation.getId());
    }

    @Override
    public OperationData getOperationData(final Integer iId) {
        if (mModel.getNodeData(mModel.getNode(iId)) instanceof OperationData) {
            final OperationData opData = (OperationData) mModel.getNodeData(mModel.getNode(iId));
            return opData;
        }
        return null;
    }

    @Override
    public ISopNode getRelationOperationSetAsSOPNode() {
        return mSopRoot;
    }
}
