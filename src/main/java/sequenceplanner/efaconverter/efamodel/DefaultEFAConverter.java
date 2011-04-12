/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.io.File;
import java.util.Iterator;
import javax.swing.JFileChooser;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import org.supremica.automata.ExtendedAutomata;
import org.supremica.automata.ExtendedAutomaton;
import org.supremica.external.avocades.common.Module;
import sequenceplanner.efaconverter.EFAVariables;

/**
 *
 * @author shoaei
 */
public class DefaultEFAConverter {

    private SpEFAutomata sp;
    private Module module;
    
    public DefaultEFAConverter(String iName, SpEFAutomata iSpEFAutomata){
        this.sp = iSpEFAutomata;
        this.module = new Module(iName, false);
        convert();
    }

    private void convert() {
        for(SpVariable spv : sp.getVariables()){
            DefaultEFAutomaton var = new DefaultEFAutomaton(createVariableName(spv.getName()), module);
            var.addVariable(spv.getMin(), spv.getMax(), spv.getInit());
        }
        System.err.println(sp.getVariables().size());
        
        for(SpEFA spefa : sp.getAutomatons()){
            DefaultEFAutomaton efa = new DefaultEFAutomaton(createEFAName(spefa.getName()), module);

            for(SpLocation spl : spefa.getLocations()){
                efa.addLocation(spl.getName(), spl.isAccepting(), spl.isInitialLocation());
                System.err.println(spl.isAccepting() + " ===== " + spl.isInitialLocation());
            }
            
            for(SpEvent spe : spefa.getAlphabet())
                efa.addEvent(spe.getName(), spe.isControllable());
            
            for(Iterator<SpTransition> itr = spefa.iterateTransitions(); itr.hasNext();){
                SpTransition tran = itr.next();
                efa.addTransition(tran.getFrom().getName(), 
                                  tran.getTo().getName(), 
                                  tran.getEvent().getName(), 
                                  tran.getGuard(), 
                                  tran.getAction());
            }
            module.addAutomaton(efa.getAutomaton());
        }
    }
    
    public Module getModule(){
        return module;
    }

    private String createEFAName(String iOperationName) {
        return EFAVariables.OPERATION_NAME_PREFIX + iOperationName;
    }

    private String createVariableName(String iVariableName) {
        return EFAVariables.VARIABLE_NAME_PREFIX + iVariableName;
    }
    
    public void saveToFile(){
        try {
            ModuleSubject moduleSubject = module.getModule();
            moduleSubject.setName("Sequence Planner to EFA output");

            String filepath = "";
            JFileChooser fc = new JFileChooser();
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
            System.err.println(t);
        }
    }
}    
//    private Model model;
//    private String name;
//    private DefaultEFAutomata automata;
//    private HashMap<Integer, TreeNode> operations;
//
//    public DefaultEFAConverter(String iProjectName, Model iModel){
//        this.name = iProjectName;
//        this.model = iModel;
//        automata = new DefaultEFAutomata(name);
//        operations = new HashMap<Integer, TreeNode>();
//        init();
//    }
//
//    public final void init(){
//        createOperationList();
//    }
//
//    public IEFAutomata getEFAutomata() {
//        buildEFA();
//        buildLocationVariables();
//        buildResouceVariables();
//        buildLiaisonVariables();
//        parseGuards();
//        parseActions();
//        checkConsistency();
//        return automata;
//    }
//
//    private void buildEFA() {
//        for(TreeNode op : operations.values()){
//            String opName = createOperationName(op);
//            DefaultEFAutomaton automaton = new DefaultEFAutomaton(opName + EFAVariables.EFA_NAME_POSTFIX, EFAType.ExtendedFiniteAutomaton ,automata);
//
//            String initL = opName + EFAVariables.STATE_INITIAL_POSTFIX;
//            String execL = opName + EFAVariables.STATE_EXECUTION_POSTFIX;
//            String finiL = opName + EFAVariables.STATE_FINAL_POSTFIX;
//
//            String startE = EFAVariables.EFA_START_EVENT_PREFIX + opName;
//            String stopE = EFAVariables.EFA_STOP_EVENT_PREFIX + opName;
//
//            automaton.addLocation(initL, true, true);
//            automaton.addLocation(execL, false, false);
//            automaton.addLocation(finiL, true, false);
//
//            automaton.addTransition(initL, execL, startE,"","");
//            automaton.addTransition(execL, finiL, stopE,"","");
//        }
//    }
//
//
//    private IEFAutomata getNodeEFAutomata(TreeNode iOperation) {
//        DefaultEFAutomata opAutomata = new DefaultEFAutomata(Integer.toString(iOperation.getId()));
//
//        String opName = createOperationName(iOperation);
//        DefaultEFAutomaton opAutomaton = new DefaultEFAutomaton(opName + EFAVariables.EFA_NAME_POSTFIX, Type.ExtendedFiniteAutomaton ,automata);
//
//        String initL = opName + EFAVariables.STATE_INITIAL_POSTFIX;
//        String execL = opName + EFAVariables.STATE_EXECUTION_POSTFIX;
//        String finiL = opName + EFAVariables.STATE_FINAL_POSTFIX;
//
//        String startE = EFAVariables.EFA_START_EVENT_PREFIX + opName;
//        String stopE = EFAVariables.EFA_STOP_EVENT_PREFIX + opName;
//
//        OperationData opData = (OperationData) iOperation.getNodeData();
//
//        opAutomaton.addLocation(initL, true, true);
//        opAutomaton.addLocation(execL, false, false);
//        opAutomaton.addLocation(finiL, true, false);
//
//        String startGuard = "";
//        startGuard += createConditionGuard(opData.getSequenceCondition());
//        startGuard += createResourceBookingGuard(opData.getResourceBooking());
//
//        String startAction = "";
//        startAction += createActions(opData.getActions());
//        startAction += createResourceBookingActions(opData.getResourceBooking());
//
//        return opAutomata;
//    }
//
//    public IEFAutomaton getProjectEFAutomaton(Model model) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    private String createOperationName(TreeNode iOperation) {
//        return EFAVariables.OPERATION_NAME_PREFIX + iOperation.getId();
//    }
//
//
//    private void createOperationList() {
//        Stack<TreeNode> stack = new Stack<TreeNode>();
//        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i){
//            stack.add(model.getOperationRoot().getChildAt(i));
//        }
//        while(!stack.isEmpty()){
//            TreeNode node = stack.pop();
//            if(node.getChildCount() > 0){
//                for (int i = 0; i < node.getChildCount(); ++i){
//                    stack.add(node.getChildAt(i));
//                }
//            }
//            operations.put(node.getId(), node);
//        }
//    }
//
//    private LinkedList<String> locations;
//    private LinkedList<String> events;
//    private LinkedList<String> variable;
//    private LinkedList<LinkedList<String>> transitions;
//
//    public DefaultEFAConverter(Model model, String name){
//        this.model = model;
//        this.name = name;
//        automata = new DefaultEFAutomata(name);
//        this.module = automata.getModule();
//
////        automaton = new DefaultEFAutomaton(name);
////        locations = new LinkedList<String>();
////        events = new LinkedList<String>();
////        variable = new LinkedList<String>();
////        transitions = new LinkedList<LinkedList<String>>();
////        createEFAutomaton();
//    }
////
//    public ExtendedAutomaton getEFAutomaton(String name){
//    }
//
////
////    public DefaultEFAutomaton getDefaultEFAutomaton(TreeNode operation, String name){
////        DefaultEFAutomaton automaton = createEFAutomaton(operation);
////        return automaton;
////    }
////
//    private void createEFAutomaton(TreeNode operation) {
//
//        String opName = createOperationName(Integer.toString(operation.getId()));
//        DefaultEFAutomaton automaton = new DefaultEFAutomaton(opName, module);
//
//        String initL = opName + EFAVariables.STATE_INITIAL_POSTFIX;
//        String execL = opName + EFAVariables.STATE_EXECUTION_POSTFIX;
//        String finiL = opName + EFAVariables.STATE_FINAL_POSTFIX;
//
//        String startE = EFAVariables.EFA_START_EVENT_PREFIX + opName;
//        String stopE = EFAVariables.EFA_STOP_EVENT_PREFIX + opName;
//
//        OperationData opData = (OperationData) operation.getNodeData();
//
//        automaton.addLocation(initL, true, true);
//        automaton.addLocation(execL, false, false);
//        automaton.addLocation(finiL, true, false);
//
//        String gStart = getConditionGuard(opData.getSequenceCondition());
//        gStart += getResourceBookingGuard(opData.getResourceBooking());
//
//        automaton.addTransition(initL, execL, startE, , );
//        automaton.addTransition(execL, finiL, stopE, , null);
//
//        return automaton;
//    }
//
//    private String createOperationName(String operation) {
//        String opName = EFAVariables.OPERATION_NAME_PREFIX + operation + EFAVariables.EFA_NAME_POSTFIX;
//        return opName;
//    }
//
//    private String[] createGuardAndAction(String rawPrecondition) {
//        String g = rawPrecondition;
//        String a = "";
//        final String SP_OPEERATION_PATTERN = "(\\d+)";
//        final String SP_INIT_PATTERN = "(" + EFAVariables.STATE_INITIAL_POSTFIX + ")";
//        final String SP_EXEC_PATTERN = "("+ EFAVariables.STATE_EXECUTION_POSTFIX +")";
//        final String SP_FINI_PATTERN = "("+ EFAVariables.STATE_FINAL_POSTFIX +")";
//        final String SP_AND_PATTERN = "\\sA{1}\\s";
//        final String SP_OR_PATTERN = "\\sV{1}\\s";
//        final String SP_BOOKIN_RESOURCE_PATTERN = "(\\w)(\\+)";
//        final String SP_UNBOOKIN_RESOURCE_PATTERN = "(\\w)(\\-)";
//
//        g.replaceAll(SP_AND_PATTERN, "&");
//        g.replaceAll(SP_OR_PATTERN, "|");
//        g.replaceAll(SP_OPEERATION_PATTERN, "Op$1_state==");
//        g.replaceAll(SP_INIT_PATTERN, "0");
//        g.replaceAll(SP_EXEC_PATTERN, "1");
//        g.replaceAll(SP_FINI_PATTERN, "2");
//
//        String[] ga = new String[2];
//        ga[1]=g;
//        ga[2]=a;
//        return ga;
//    }
//
//    private String createAction(String[] rawActions) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }    