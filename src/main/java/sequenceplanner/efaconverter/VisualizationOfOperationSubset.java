package sequenceplanner.efaconverter;

import java.util.HashMap;
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
import org.supremica.gui.VisualProject;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class VisualizationOfOperationSubset {

    final SModule mModule = new SModule("Module name");
    final SEFA mEfa = new SEFA("Single", mModule);
    ModelParser mModelparser;
    Automata mAutomata;
    Automaton mAutomaton;

    public VisualizationOfOperationSubset(ModelParser iModelparser) {
        this.mModelparser = iModelparser;

    }

    public Automaton getAutomaton() {
        return mAutomaton;
    }

    public boolean run() {
        //Test ids
        if (!mModule.testIDs(mModelparser)) {
            System.out.println("Problem with ids!");
            return false;
        }

        //Translate operations to EFA
        if (!translateToEFA()) {
            System.out.println("Problem with translation from op to efa!");
            return false;
        }

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

        //Relation identification
        if (!relationIdentification()) {
            System.out.println("Problem with relation identification!");
            return false;
        }


        return true;
    }

    private boolean relationIdentification() {
        final int nbrOfOps = mModelparser.getOperations().size();
        HashMap<String, Set<String>> eventStateSetMap = new HashMap<String, Set<String>>(nbrOfOps * 2);
        initEventStateMap(eventStateSetMap);
        for (StateProxy sp : mAutomaton.getStates()) {
            String stateName = sp.getName();
            for (Arc arc : mAutomaton.getStateWithName(stateName).getOutgoingArcs()) {
//                stateName = stateName.replaceAll(".pm.", ".").replaceAll(".pm", "").replaceAll("pm.", "");
                String eventName = arc.getLabel();
                if (eventName.contains("up")) {
                    eventName = eventName.substring(0, eventName.indexOf("up") + 2);
                } else {//"down"
                    eventName = eventName.substring(0, eventName.indexOf("down") + 4);
                }
                eventStateSetMap.get(eventName).add(stateName);
            }
        }
        System.out.println(eventStateSetMap.toString());
        return true;
    }

    private void initEventStateMap(HashMap<String, Set<String>> ioMap) {
        for (OpNode opNode : mModelparser.getOperations()) {
            ioMap.put("e" + opNode.getStringId() + "up", new HashSet<String>());
            ioMap.put("e" + opNode.getStringId() + "down", new HashSet<String>());
        }
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

    private void viewAutomaton(Automaton iAutomaton) {
        VisualProject vp = new VisualProject();
        vp.addAutomaton(new Automaton(iAutomaton));
        try {
            vp.getAutomatonViewer(iAutomaton.getName());
        } catch (Exception e) {
            System.out.println(e.toString());
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

    public boolean translateToEFA() {
        SEGA ega;
        mEfa.addState("pm", true, true);

        for (OpNode opNode : mModelparser.getOperations()) {
            OperationData opData = (OperationData) opNode.getTreeNode().getNodeData();
            final int id = opData.getId();
            //Add integer variable for operation
            final String varName = "o" + id;
            mModule.addIntVariable(varName, 0, 2, 0, null);
            //Add transition to start execute operation
            ega = new SEGA("e" + id + "up");
            ega.andGuard(varName + "==0");
            ega.addGuardBasedOnSPCondition(opData.getRawPrecondition(), "o", mModelparser);
            ega.addAction(varName + "=1");
            mEfa.addStandardSelfLoopTransition(ega);
            //Add transition to finish execute operation
            ega = new SEGA("e" + id + "down");
            ega.andGuard(varName + "==1");
            ega.addGuardBasedOnSPCondition(opData.getRawPostcondition(), "o", mModelparser);
            ega.addAction(varName + "=2");
            mEfa.addStandardSelfLoopTransition(ega);
        }

        return true;
    }
}
