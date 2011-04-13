package sequenceplanner.algorithms.visualization;

import java.util.Map;
import java.util.Set;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * To find relations between a set of operations given in a {@link ISopNode}.<br/>
 * The relations should be found with respect to some superset of operations also given as a {@link ISopNode}.<br/>
 * @author patrik
 */
public class RelationsForOperationSet {

    private ISopNode mSopNodeOset = null;
    private ISopNode mSopNodeOsubset = null;
    private ISopNode mSopNodeOfinish = null;
    private ISupremicaInteractionForVisualization formalMethods;
    private ROperationToolbox mROpToolbox = new ROperationToolbox();

    public RelationsForOperationSet(Model iModel) {
        mSopNodeOset = new SopNode();
        mSopNodeOsubset = new SopNode();
        mSopNodeOfinish = new SopNode();
        formalMethods = new SupremicaInteractionForVisualization();
    }

    public boolean run() {
        //Translate operations to EFA
        ModuleSubject ms = formalMethods.getModuleSubject(getmSopNodeOset(), getmSopNodeOfinish());
        if (ms == null) {
            System.out.println("Problem with translation from op to efa!");
            return false;
        }

        //flatten out (EFA->DFA, Module -> Automata)
        Automata automata = formalMethods.flattenOut(ms);
        if (automata == null) {
            System.out.println("Problem with flatten out!");
            return false;
        }

        //synthesis
        Automaton automaton = formalMethods.synthesize(automata);
        if (automaton == null) {
            System.out.println("Problem with synthesis!");
            return false;
        }

        //Get states where each event is enabled
        Map<String,Set<String>> eventStateSpaceMap = formalMethods.getStateSpaceForEventSetMap(automaton);

        //Set up of operations whose relations are interesting (Osubset)
        addToRelationOperationSet();

        //Relation identification
        if (!relationIdentification(automaton, eventStateSpaceMap)) {
            System.out.println("Problem with relation identification!");
            return false;
        }

        printRelations();
        
        return true;
    }

    public boolean saveFormalModel(final String iPath) {
        return formalMethods.saveSupervisorAsWmodFile(iPath);
    }

    public void printRelations() {
        for(final IROperation externalOp : mROpToolbox.getmRelationOperationSet()) {
            System.out.println("--------------------------------");
            for(final IROperation internalOp : mROpToolbox.getmRelationOperationSet()) {
                final ROperation externalOp2 = (ROperation) externalOp;
                final String externalOp2Name = externalOp2.getOperationData().getName();
                final ROperation internalOp2 = (ROperation) internalOp;
                final String internalOp2Name = internalOp2.getOperationData().getName();

                System.out.println(externalOp2Name + " has relation " + 
                        RelateTwoOperations.relationIntegerToString(externalOp.getRelationToIOperation(internalOp), " ", " ")
                        + " to " + internalOp2Name);
            }
        }
        System.out.println("--------------------------------");
    }

    public SopNodeWithRelations getSopRootWithRelations() {
        return new SopNodeWithRelations(mSopNodeOsubset, mROpToolbox.getmRelationOperationSet());
    }

    private boolean relationIdentification(final Automaton iAutomaton, final Map<String,Set<String>> iEventStateSpaceMap) {
        //Init of map for storage of states--------------------------------------
        //Remove Single EFA from automaton name (the name is Single) + extra substrings
        //From sup(oX||oY||Single) -> oX||oY
        mROpToolbox.setmStateNameExplanation(iAutomaton.getName().replaceAll("sup\\(", "").replaceAll("\\)", ""));
        mROpToolbox.setmStateNameExplanation(mROpToolbox.getmStateNameExplanation()
                .replaceAll("\\|\\|Single\\|\\|", "\\|\\|").replaceAll("\\|\\|Single", "").replaceAll("Single\\|\\|", ""));
        //-----------------------------------------------------------------------

        //Loop events of interest to find states---------------------------------
        for(final String event : mROpToolbox.getmEventStateSetMap().keySet()) {
            if(!iEventStateSpaceMap.containsKey(event)) {
                System.out.println("Mismatch between events in supervisor and subset!");
                return false;
            }
            final Set<String> stateSet = iEventStateSpaceMap.get(event);
            mROpToolbox.getmEventStateSetMap().get(event).addAll(stateSet);
        }//----------------------------------------------------------------------

        //Find in what locations for other operations the events of an operation can take place
        mROpToolbox.findEventOperationRelations();
        //-----------------------------------------------------------------------

        //Fill relations to Map in each operation AROperation for later look up--
        mROpToolbox.fillOperationRelations();
        //-----------------------------------------------------------------------

        return true;
    }

    public boolean addToRelationOperationSet() {
        for(final ISopNode node : getmSopNodeOsubset().getFirstNodesInSequencesAsSet()) {
            if(node.getNodeType() instanceof OperationData) {
                mROpToolbox.addToRelationOperationSet(node);
            }
        }
        return true;
    }

    public ISopNode getmSopNodeOfinish() {
        return mSopNodeOfinish;
    }

    public void setmSopNodeOfinish(ISopNode mSopNodeOfinish) {
        this.mSopNodeOfinish = mSopNodeOfinish;
    }

    public ISopNode getmSopNodeOset() {
        return mSopNodeOset;
    }

    public void setmSopNodeOset(ISopNode mSopNodeOset) {
        this.mSopNodeOset = mSopNodeOset;
    }

    public ISopNode getmSopNodeOsubset() {
        return mSopNodeOsubset;
    }

    public void setmSopNodeOsubset(ISopNode mSopNodeOsubset) {
        this.mSopNodeOsubset = mSopNodeOsubset;
    }

    public boolean OsetIsSupersetForOsubset() {
        return new SopNodeToolboxSetOfOperations().operationsAreSubset(getmSopNodeOsubset(), getmSopNodeOset());
    }

    public boolean OsetIsSupersetForOfinish() {
        return new SopNodeToolboxSetOfOperations().operationsAreSubset(getmSopNodeOfinish(), getmSopNodeOset());
    }
}
