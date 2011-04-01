
package sequenceplanner.efaconverter;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JFileChooser;

import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;

import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.Module;

import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionElement;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionStatment;
import sequenceplanner.efaconverter.efamodel.SpEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.efaconverter.efamodel.SpLocation;
import sequenceplanner.efaconverter.efamodel.SpTransition;
import sequenceplanner.efaconverter.efamodel.SpVariable;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * Comment kb 100701
 * This file creates EFAutomata based on a set of operation sequences created
 * by {@link OperationSequencer}.
 * First the sequences are converted to SpEFA in createSpEFA (it was a mess to use Supremica
 * ExtendedAutomata class so I did my own structure!). Local locations in
 * operations that where identified in OperationSequencer are collapsed.
 * A variable tracking the location of the sequence EFAs are created.
 * The guards and actions from the operations from SP1 are translated to text.
 * Finally in createWmodFile, the supremica EFA is created and saved to a file.
 * @author kbe
 */
public class convertSeqToEFA {

    private final Set<OpNode> sequences;
    private final ModelParser modelparser;

    private static int INIT = 0;
    private static int EXEC = 1;
    private static int FIN = 2;

    private OpNode currentNode;
    private int currentLocation = INIT;
    private String currentOutgoingEvent = new String();
    private int currentStateNumber = 0;
    private String currentSeqVar = "";

    private static String OPSEP_CHAR = "-";
    private static String OPLOCSEP_CHAR = "_";
    private static String STARTEVENT_CHAR = "eu";
    private static String STOPEVENT_CHAR = "ed";

    private HashMap<String,String> opLocationGuardMap;
    private HashMap<String,OpNode> seqLocationOpNodeMap;
    private HashMap<String,Integer> seqLocationOpNodeStateMap;


    public convertSeqToEFA(Set<OpNode> firstNodesInSequences, ModelParser modelparser) {
        this.sequences = firstNodesInSequences;
        this.modelparser = modelparser;
        opLocationGuardMap = new HashMap<String, String>();
        seqLocationOpNodeMap = new HashMap<String,OpNode>();
        seqLocationOpNodeStateMap = new HashMap<String,Integer>();       
    }

    public SpEFAutomata createSpEFA(){
        SpEFAutomata automata = new SpEFAutomata();
        for (OpNode n : sequences){            
            SpEFA efa = createSeqEFA(n,automata);
            if (efa != null){
                automata.addAutomaton(efa);
            }
        }

        for (VarNode n : modelparser.getVariables()){
            SpVariable v = createSPVariable(n);
            if (v!=null){
                automata.addVariable(v);
            }
        }

        // fix guards and actions
        for (SpEFA efa : automata.getAutomatons()){
            efa = createGuardsAndActions(efa);
        }

        return automata;
    }

    public void createWmodFile(SpEFAutomata seqAutomata){
        // Create ExtendedAutomata
        Module module = new Module("SpEFAAutomata", false);
         for (SpVariable v : seqAutomata.getVariables()){
            createVariableEFA(v,module);
            // If this efa is added to the model two efa will be saved.
        }
         // konvertera till automata
        for (SpEFA efa : seqAutomata.getAutomatons()){
            module.addAutomaton(createExtendedAutomata(efa,module));
        }
       
        saveToFile(module);
    }
      
    private SpEFA createSeqEFA(OpNode firstOp, SpEFAutomata automata){
        String efaName = createEFAName(firstOp);
        String seqName = "Seq_" + efaName;
        String varName = "Seq_" + firstOp.getName() + "_V";

        SpEFA efa = new SpEFA(seqName);

        // Init global variables. Maybe not that nice but some of them are needed
        // due to the reqursive method reqStateNameParser
        currentNode = firstOp;
        currentLocation = INIT;
        currentOutgoingEvent = new String();
        currentStateNumber = 0;
        currentSeqVar = varName;

        // Create initial state
        String currentStateName = getNextStateName();

        //Return null if oneStateAutomaton iR.e the complete seq is local (parallel)
        if (!currentNode.hasSuccessor() && currentLocation == FIN) return null;

        efa.setInitialLocation(currentStateName);
        while (currentNode.hasSuccessor() || (!currentNode.hasSuccessor() && currentLocation != FIN)){
            currentStateNumber++;
            String event = new String(currentOutgoingEvent);
            // Get guards and actions here!
            if (currentLocation != FIN) currentLocation++;
            String nextStateName = getNextStateName();
            if (currentStateName.equals(nextStateName)) break;

            efa.addLocation(nextStateName);
            efa.addTransition(currentStateName, nextStateName, event, "", currentSeqVar + EFAVariables.EFA_PLUS_ONE);

            currentStateName = nextStateName;
        }

        automata.addVariable(new SpVariable(varName,0,currentStateNumber,0));
        return efa;
    }



    private String getNextStateName(){
        String s = new String();
        s = currentNode.getName() + OPLOCSEP_CHAR + getLocationName(currentLocation);
        currentOutgoingEvent = getEventName(currentNode.getName(), currentLocation);
        s = s + reqStateNameParser();
        this.seqLocationOpNodeMap.put(s, currentNode);
        this.seqLocationOpNodeStateMap.put(s, currentLocation);
        return s;
    }

    private String reqStateNameParser(){

        // Creates a map between each location of the operation with current state of the seqEFA
        addLocationGuardMapping(currentNode.getName(), currentLocation);
        
        if (currentLocation == INIT){
            if (currentNode.hasSeqLocalPreCond()){
                currentLocation = EXEC;
                currentOutgoingEvent = getEventName(currentOutgoingEvent, currentNode.getName(), currentLocation);
                return getLocationName(EXEC) + reqStateNameParser();
            } else return "";
        }
        if (currentLocation == EXEC){
            if (currentNode.hasSeqLocalPostCond()){
                currentLocation = FIN;
                return getLocationName(FIN) + reqStateNameParser();
            } else return "";
        }
        if (currentLocation == FIN){
            if (currentNode.hasSuccessor()){
                currentNode = currentNode.getSuccessor();
                currentLocation = INIT;
                currentOutgoingEvent = getEventName(currentOutgoingEvent, currentNode.getName(), currentLocation);
                return OPSEP_CHAR + currentNode.getName() + OPLOCSEP_CHAR
                        + getLocationName(currentLocation)
                        + reqStateNameParser();
            }
        }
        return "";

    }

    private void addLocationGuardMapping(String opName, int location){
        String guard = currentSeqVar + EFAVariables.EFA_EQUAL + Integer.toString(currentStateNumber);
        opLocationGuardMap.put(createOpLocationName(opName,location), guard);
    }

    private String createOpLocationName(String opName, int location){
        return opName + getLocationName(location);
    }


    private String getLocationName(int location){
        if (location==INIT) return "i";
        if (location==EXEC) return "e";
        if (location==FIN) return "f";
        return "";
    }

    private String getEventName(String currentEventName, String opName, int location){
        String s =  !currentEventName.isEmpty()
                ? currentEventName + OPSEP_CHAR + getEventName(opName, location)
                : getEventName(opName, location);
        return s;
    }

    private String getEventName(String opName, int location){
        if (location==INIT) return STARTEVENT_CHAR + opName;
        if (location==EXEC) return STOPEVENT_CHAR + opName;
        if (location==FIN) return "";
        return "";
    }

    private String createEFAName(OpNode node){
        if (node == null) return "";
        if (node.hasSuccessor()){
            return node.getName() + OPSEP_CHAR + createEFAName(node.getSuccessor());
        } else {
            return node.getName();
        }
    }

    private EFA createVariableEFA(SpVariable variable, Module m){
        EFA efaVar = new EFA(variable.getName(),m);
        efaVar.addIntegerVariable(
                variable.getName(),
                variable.getMin(),
                variable.getMax(),
                variable.getInit(),
                variable.getInit());
        return efaVar;
    }

    private SpVariable createSPVariable(VarNode variable){
        if (variable.isHidden()) return null;
        return new SpVariable(
                createVarName(variable),
                modelparser.getVariableMin(variable),
                modelparser.getVariableMax(variable),
                modelparser.getVariableInit(variable));
    }


    private String createVarName(VarNode n){
        return  n.getName();
    }

    /**
     * Comment kb 100705
     * These methods must be changed since it is related to the SP1 data structure on
     * operation conditions. Have started designing a new condition structure in
     * package sequence.condition. Didn't have time to complete, therfore this ful hack!
     *
     */

    private SpEFA createGuardsAndActions(SpEFA efa){

        SpLocation initialLocation = efa.getInitialLocation();
        if (initialLocation != null){
            reqGuardAndActionGenerator(efa, initialLocation);
        }      
        return efa;
    }

    // This only works with our special Seq EFA created here with a straight seq
    private void reqGuardAndActionGenerator(SpEFA efa, SpLocation location){
        if (location == null) return;
        if (!location.hasOutTransition()) return;
        SpTransition transition = null;
        for (SpTransition t : location.getOutTransitions()){
            transition = t;
            break;
        }
        if (transition == null) return;
        SpLocation nextLocation = transition.getTo();

        OpNode op = this.seqLocationOpNodeMap.get(location.getName());
        Integer opState = this.seqLocationOpNodeStateMap.get(location.getName());

        transition.setGuard(getGuard(op,opState.intValue()));
        transition.setAction(transition.getAction() + getAction(op,opState.intValue()));
        transition.setCondition(getCondition(op,opState.intValue()));

        reqGuardAndActionGenerator(efa, transition.getTo());
    }

    private String getGuard(OpNode op, int location){
        if (location == INIT){
            return createPreGuard(op);
        } else if (location == EXEC){
            return createPostGuard(op);
        }
        return "";
    }

    private String getAction(OpNode op, int location){
        if (location == INIT){
            return createPreAction(op);
        } else if (location == EXEC){
            return createPostAction(op);
        }
        return "";
    }

    private Condition getCondition(OpNode op,int location){
        Condition c;
        if (location == INIT){
             c =  modelparser.createPreCondition(op);
        } else if (location == EXEC){
            c = modelparser.createPostCondition(op);
        } else c = new Condition();

        c.setGuard(translateOpGuards(c.getGuard()));

        return c;
    }


    private String createPreGuard(OpNode op){
        if (!modelparser.isOpNodeOk(op)) return "";
        OperationData od = (OperationData) op.getTreeNode().getNodeData();
        return createGuard(od.getSequenceCondition(),od.getResourceBooking());
    }

    private String createPostGuard(OpNode op){
        if (!modelparser.isOpNodeOk(op)) return "";
        OperationData od = (OperationData) op.getTreeNode().getNodeData();
        return createGuard(od.getPSequenceCondition(),od.getPResourceBooking());
    }

    private String createPreAction(OpNode op){
        if (!modelparser.isOpNodeOk(op)) return "";
        OperationData od = (OperationData) op.getTreeNode().getNodeData();
        return createAction(od.getActions(),od.getResourceBooking());
    }

    private String createPostAction(OpNode op){
        if (!modelparser.isOpNodeOk(op)) return "";
        OperationData od = (OperationData) op.getTreeNode().getNodeData();
        return createAction(new LinkedList<OperationData.Action>(),od.getResourceBooking());
    }

    private String createGuard(LinkedList<LinkedList<SeqCond>> seqCond, LinkedList<Integer[]> rAlloc){
        String guard = new String();
        for (LinkedList<SeqCond> orConds : seqCond){
            if (!guard.isEmpty()){
                guard += EFAVariables.EFA_AND;
            }
            if (orConds.size() > 1){
                guard += "(";
            }
            Iterator<SeqCond> i = orConds.iterator();
            while (i.hasNext()){
                SeqCond sc = i.next();
                boolean hidden = true;
                if (sc.isOperationCheck()){
                    OpNode o = modelparser.getOpNode(sc.id);
                    if (o != null){
                        hidden = false;
                        guard += this.opLocationGuardMap.get(createOpLocationName(o.getName(), sc.state));
                    }
                } else if (sc.isVariableCheck()){
                    VarNode v = modelparser.getVarNode(sc.id);
                    if (v != null){
                        if (!v.isHidden()){
                            hidden = false;
                            guard += createVarName(v) + Model.getVariabelCheck(sc.state) + Integer.toString(sc.value);
                        }
                    }
                }

                if (!hidden && i.hasNext()){
                    guard += EFAVariables.EFA_OR;
                }
            }

            if (orConds.size() > 1){
                guard += ")";
            }
        }

        Iterator<Integer[]> i = rAlloc.iterator();
        if (i.hasNext() && !guard.isEmpty()){
            guard += EFAVariables.EFA_AND;
        }
        while (i.hasNext()){
            Integer[] ints = i.next();
            VarNode n = modelparser.getVarNode(ints[0]);
            if (n != null && !n.isHidden()){
                if (n.getTreeNode() != null && Model.isVariable(n.getTreeNode().getNodeData())){
                    ResourceVariableData var = (ResourceVariableData) n.getTreeNode().getNodeData();
                    if (ints[1]==1){ // increase variable
                        guard += createVarName(n) + "<" + Integer.toString(var.getMax());
                    } else if (ints[1]==0){ // decrease variable
                        guard += createVarName(n) + ">" + Integer.toString(var.getMin());
                    }
                    if (i.hasNext()) guard += EFAVariables.EFA_AND;
                }
            }
        }


        return guard;
    }


    private String createAction(LinkedList<OperationData.Action> actions, LinkedList<Integer[]> rAlloc){
        String action = new String();
        Iterator<OperationData.Action> iA = actions.iterator();
        while (iA.hasNext()){
            OperationData.Action a = iA.next();
            VarNode n = modelparser.getVarNode(a.id);
            if (n != null && !n.isHidden()){
                action += createVarName(n) + Model.getActionSetType(a.state) + Integer.toString(a.value) + ";";
            }
        }


        Iterator<Integer[]> iR = rAlloc.iterator();
        while (iR.hasNext()){
            Integer[] ints = iR.next();
            VarNode n = modelparser.getVarNode(ints[0]);
            if (n != null && !n.isHidden()){
                if (n.getTreeNode() != null && Model.isVariable(n.getTreeNode().getNodeData())){
                    ResourceVariableData var = (ResourceVariableData) n.getTreeNode().getNodeData();
                    if (ints[1]==1){ // increase variable
                        action += createVarName(n) + EFAVariables.EFA_PLUS_ONE;
                    } else if (ints[1]==0){ // decrease variable
                        action += createVarName(n) + EFAVariables.EFA_MINUS_ONE;
                    }
                }
            }
        }


        return action;
    }


    private ConditionExpression translateOpGuards(ConditionExpression guard){
        Iterator<ConditionElement> i = guard.iterator();
        while (i.hasNext()){
            ConditionElement ce = i.next();
            if (ce.isExpression()){
                ce = translateOpGuards((ConditionExpression) ce);
            } else if(ce.isStatment()){
                //this.opLocationGuardMap.get(createOpLocationName(o.getName(), sc.state));
                ConditionStatment cs = (ConditionStatment) ce;
                if (isStatmentRelatedToOp(cs)){
                    // How this is handled now should probably change. opLocationGuardMap is 
                    // created to generate EFA to supremica. Therfore no error check here!
                    String newGuard = getGuardsRelatedToOpStatment(cs);
                    String[] guardAndValue = newGuard.split(EFAVariables.EFA_EQUAL);
                    cs.setVariable(guardAndValue[0]);
                    cs.setValue(guardAndValue[1]);
                }
                // maybe remove hidden variables here later!
            }
        }

        return guard;
    }

    private boolean isStatmentRelatedToOp(ConditionStatment cs){
        return modelparser.getOpNode(cs.getVariable()) != null;
    }

    private String getGuardsRelatedToOpStatment(ConditionStatment cs){
        try{
            int intValue = Integer.parseInt(cs.getValue());
            String guard = this.opLocationGuardMap.get(createOpLocationName(cs.getVariable(), intValue));
            if (guard != null) return guard;
            return "";
        } catch(NumberFormatException e){
            return "";
        }
    }



    private EFA createExtendedAutomata(SpEFA efa, Module m){
        EFA extendedAutomata = new EFA(efa.getName(),m);
        SpLocation current = efa.getInitialLocation();
        extendedAutomata.addInitialState(current.getName());
        while(current.hasOutTransition()){
            SpTransition trans = null;
            for (SpTransition t : current.getOutTransitions()){
                trans = t;
                break;
            }
            if (trans == null) break;
            SpLocation next = trans.getTo();
            if (next == null) break;
            extendedAutomata.addState(next.getName());
            extendedAutomata.addTransition(current.getName(),
                                           next.getName(),
                                           trans.getEventLabel(),
                                           trans.getGuard(),
                                           trans.getAction());
            current = next;
        }
        return extendedAutomata;
    }


    private void saveToFile(Module module){
        try {

            ModuleSubject moduleSubject = module.getModule();
            moduleSubject.setName("Sequence Planner to EFA output");

            String filepath = "";
            JFileChooser fc = new JFileChooser("C:\\Documents and Settings\\EXJOBB SOCvision\\Desktop");
            int fileResult = fc.showSaveDialog(null);
            if (fileResult == JFileChooser.APPROVE_OPTION) {
                filepath = fc.getSelectedFile().getAbsolutePath();

                File file = new File(filepath);

                file.createNewFile();

                ModuleSubjectFactory factory = new ModuleSubjectFactory();

                //Save module to file

                JAXBModuleMarshaller marshaller =
                        new JAXBModuleMarshaller(factory,
                        CompilerOperatorTable.getInstance());

                marshaller.marshal(moduleSubject, file);

            }


        } catch (Exception t) {
            t.printStackTrace();
        }
    }


}
