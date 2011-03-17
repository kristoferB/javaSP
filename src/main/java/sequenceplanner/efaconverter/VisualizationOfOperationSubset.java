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
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;

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
    RVNodeToolbox mRVNodeToolbox;
    OperationView mOpView;
    SPGraph mGraph;

    public VisualizationOfOperationSubset(ModelParser iModelparser, OperationView iOpView) {
        this.mModelparser = iModelparser;
        this.mOpView = iOpView;
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

        

        //Create operation nodes
        if (!createOperationNodes()) {
            System.out.println("Problem with operation node creation!");
            return false;
        }

        //Relation identification
        if (!relationIdentification()) {
            System.out.println("Problem with relation identification!");
            return false;
        }

        for (RVNode rvNode : mRVNodeToolbox.mRoot.mChildren) {
            System.out.println(rvNode.mOpNode.getName());
//            for(String key : rvNode.mEventOperationLocationSetMap.keySet()) {
//                System.out.println(key);
//                System.out.println(rvNode.mEventOperationLocationSetMap.get(key).toString());
//            }
            rvNode.getRelationToNode(mRVNodeToolbox.mRoot.getChildWithStringId("5"));
        }


//        //Draw operation nodes
//        if (!drawing()) {
//            System.out.println("Problem with drawing!");
//            return false;
//        }

        return true;
    }

    /**
     * Simple method that creates a new operation for each operation node.<br/>
     * The new operations are added as {@link Cell}s to the {@link SPGraph} in an {@link OperationView}.
     * @return true if drawing was ok else false
     */
    private boolean drawing() {
        if (mOpView != null) {
            mGraph = mOpView.getGraph();

            //Loop children (the operation nodes)
            for (RVNode rvNode : mRVNodeToolbox.mRoot.mChildren) {
                OperationData opData = new OperationData(rvNode.getOpData().getName(), 1000 + rvNode.getOpData().getId());
                mGraph.addCell(rvNode.setCell(opData));
            }
            mGraph.autoArrange((Cell) mGraph.getDefaultParent());
            return true;
        }
        return false;
    }
//
//        private class SimpleDraw {
//
//        SPGraph graph = ov.getGraph();
//
//        public SimpleDraw(Set<String> namesOfSelectedOperations) {
//
//            //Get the selected operations
//            for (InternalOpData iData : allOperations) {
//                if (namesOfSelectedOperations.contains(iData.getName())) {
//                    selectedOps.add(iData);
//                }
//            }
//
//            if (allOperationsAreOK(selectedOps)) {
//                CreateInternalSOP();
//            } else {
//                JOptionPane.showMessageDialog(null, "Operation relations are to complex. \n I can't visualize operations!");
//            }
//        }
//
//        private void CreateInternalSOP() {
//
//            //root in wrapper class
//            Wrapper master = new Wrapper();
//
//            //create wrapper
//            for (InternalOpData iData : selectedOps) {
//                //generate new node
//                Wrapper w = new Wrapper();
//                w.iData = iData;
//                w.head = master;
//
//                //child to master
//                master.children.add(w);
//            }
//
//            //update heads in wrapper class
//            Set<Wrapper> startCells = new HashSet<Wrapper>(); //Operations without preconditions
//
//            for (Wrapper w : master.children) {
//
//                Boolean isStartCell = true;
//
//                if (!w.iData.getRawPrecondition().isEmpty()) {
//                    String opInGuard = w.iData.getRawPrecondition().replaceAll(" ", "");
//                    String suffix = TypeVar.SEPARATION + TypeVar.SP_FINISH;
//                    opInGuard = opInGuard.substring(0, opInGuard.length() - suffix.length());
//                    System.out.println(w.iData.getName() + " has id: " + opInGuard + " in guard");
//
//                    for (Wrapper wGuard : master.children) {
//                        if (wGuard.iData.getId().toString().equals(opInGuard)) {
//                            w.head = wGuard;
//                            isStartCell = false;
//                            break;
//                        }
//                    }
//                }
//
//                if (isStartCell) {
//                    //No precondition -> this w has to be a startCell!
//                    startCells.add(w);
//                    System.out.println(w.iData.getName() + " is start cell!");
//                }
//            }
//
//            master.children.removeAll(startCells); //Already knows when these cells occur
//
//            for (Wrapper w : startCells) {
//                graph.addCell(w.setCell());
//                fillInternalSOP(w, master.children);
//            }
//        }
//
//        private void fillInternalSOP(Wrapper iWrap, Set<Wrapper> wrapps) {
//            //Find wrapps that has this node in precondition
//            Set<Wrapper> swrapps = new HashSet<Wrapper>();
//            for (Wrapper w : wrapps) {
//                if (w.head == iWrap) {
//                    swrapps.add(w);
//                }
//            }
//
//            if (swrapps.size() >= 1) {
//                Cell parallelCell = null;
//                if (swrapps.size() > 1) {
//                    //Need to create a parallel cell
//                    parallelCell = CellFactory.getInstance().getOperation("parallel");
//
//                    //Add parallel cell after iWrap
//                    graph.insertNewCell(iWrap.cell, parallelCell, false);
//                }
//
//                wrapps.removeAll(swrapps); //Know how to handle these wrapps -> remove them from set of wrapps
//
//                //Add wrapps to cell
//                for (Wrapper w : swrapps) {
//                    if (swrapps.size() == 1) {
//                        //Create a new cell after iWrap
//                        graph.insertNewCell(iWrap.cell, w.setCell(), false);
//                    } else {
//                        //Create new cells in parallel cell
//                        graph.insertGroupNode(parallelCell, null, w.setCell());
//                    }
//                    //Handle wrapps that are left
//                    fillInternalSOP(w, wrapps);
//                }
//            }
//        }
//
//        private class Wrapper {
//
//            Wrapper head = null;
//            Cell cell = null;
//            Set<Wrapper> children = new HashSet<Wrapper>();
//            InternalOpData iData = null;
//
//            public Wrapper() {
//            }
//
//            public Cell setCell() {
//                cell = CellFactory.getInstance().getOperation("operation");
//                //Data d = (Data) opCell.getValue();
//                cell.setValue(iData.getOpData());
//                return cell;
//            }
//        }
//    }
//

    private boolean createOperationNodes() {
        mRVNodeToolbox = new RVNodeToolbox();
        for (OpNode opNode : mModelparser.getOperations()) {
            mRVNodeToolbox.addOperation(opNode);
        }
        return true;
    }

    private boolean relationIdentification() {

        //Init of hashmap for storage of states----------------------------------
        final int nbrOfOps = mModelparser.getOperations().size();
        mRVNodeToolbox.mEventStateSetMap = new HashMap<String, Set<String>>(nbrOfOps * 2);
        initEventStateMap(mRVNodeToolbox.mEventStateSetMap);
        //Remove Single EFA from automaton name (the name is Single) + extra substrings
        //From sup(oX||oY||Single) -> oX||oY
        mRVNodeToolbox.mStateNameExplanation = mAutomaton.getName().replaceAll("sup\\(", "").replaceAll("\\)", "");
        mRVNodeToolbox.mStateNameExplanation = mRVNodeToolbox.mStateNameExplanation.replaceAll("\\|\\|Single\\|\\|", "\\|\\|").replaceAll("\\|\\|Single", "").replaceAll("Single\\|\\|", "");
        //-----------------------------------------------------------------------

        //Loop states and events to fill hashmap---------------------------------
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
                mRVNodeToolbox.mEventStateSetMap.get(eventName).add(stateName);
            }
        }//----------------------------------------------------------------------

        //Find in what locations for other operations the events of an operation can take place
        mRVNodeToolbox.findEventOperationRelations();
        //-----------------------------------------------------------------------

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
//                viewAutomaton(mAutomaton);
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
