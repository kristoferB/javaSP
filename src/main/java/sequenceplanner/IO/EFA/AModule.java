package sequenceplanner.IO.EFA;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.expr.EvalException;
import net.sourceforge.waters.model.marshaller.DocumentManager;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.module.EventDeclSubject;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import net.sourceforge.waters.xsd.base.EventKind;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import org.supremica.automata.BDD.EFA.BDDExtendedSynthesizer;
import org.supremica.automata.ExtendedAutomata;
import org.supremica.automata.ExtendedAutomaton;
import org.supremica.automata.IO.ProjectBuildFromWaters;
import org.supremica.automata.Project;
import org.supremica.automata.VariableHelper;
import org.supremica.automata.algorithms.AutomataSynthesizer;
import org.supremica.automata.algorithms.EFAMonlithicReachability;
import org.supremica.automata.algorithms.EditorSynthesizerOptions;
import org.supremica.automata.algorithms.Guard.BDDExtendedGuardGenerator;
import org.supremica.automata.algorithms.SynchronizationOptions;
import org.supremica.automata.algorithms.SynchronizationType;
import org.supremica.automata.algorithms.SynthesisAlgorithm;
import org.supremica.automata.algorithms.SynthesisType;
import org.supremica.automata.algorithms.SynthesizerOptions;
import org.supremica.external.avocades.common.Module;

/**
 * Patrik Magnusson's interface to supremica jar-files.<br/>
 * The class contains some algorithms that can be usefull when solving SCT-problems.<br/>
 * @author patrik
 */
public abstract class AModule {

    final ModuleSubjectFactory mFactory;
    private Module mAvocadesModule = null;

    public AModule(final String iModuleName, final String iModuleComment) {
        this(iModuleName);
        setComment(iModuleComment);
    }

    private AModule(final String iModuleName) {
        mFactory = new ModuleSubjectFactory();
        mAvocadesModule = new Module(iModuleName, false);
    }

    public ModuleSubject getModuleSubject() {
        return mAvocadesModule.getModule();
    }

    public Module getAvocadesModule() {
        return mAvocadesModule;
    }

    public void setComment(String comment) {
        getModuleSubject().setComment(comment);
    }

    public void addComment(String iComment) {
        if (getModuleSubject().getComment() == null) {
            setComment(iComment);
        } else {
            setComment(getModuleSubject().getComment() + "\n" + iComment);
        }
    }

    /**
     * Add interger variable to module
     * @param varName Variable name
     * @param lowerBound Lower bound for variable
     * @param upperBound Upper bound for variable
     * @param initialValue Initial <i>lower &lt=</i> value <i>&lt= upper</i> for variable
     * @param markedValue A single <i>lower &lt=</i> value <i><= upper</i> may be marked
     */
    public void addIntVariable(String varName, int lowerBound, int upperBound, int initialValue, Integer markedValue) {
        getModuleSubject().getComponentListModifiable().add(VariableHelper.createIntegerVariable(varName, lowerBound, upperBound, initialValue, markedValue));
    }

    public void addIntVariable(final IVariable var) {
        Integer markedValue = null;
        if (!var.getVarMarkedValues().isEmpty()) {
            markedValue = Integer.valueOf(var.getVarMarkedValues().iterator().next());
        }
        addIntVariable(var.getVarLabel(),
                Integer.valueOf(var.getVarLowerBound()),
                Integer.valueOf(var.getVarUpperBound()),
                Integer.valueOf(var.getVarInitValue()),
                markedValue);

    }

    public void addAutomaton(SEFA sefa) {
        mAvocadesModule.addAutomaton(sefa.getEFA());
    }

    public boolean saveToWMODFile(final String iFilePath) {
        return saveToWMODFile(iFilePath, getModuleSubject());
    }

    /**
     * Save to wmod file given as parameter.<br/>
     * @param iFilePath path to file
     * @param iModuleSubject the {@link ModuleSubject} to save
     * @return true if save was ok else false
     */
    public static boolean saveToWMODFile(final String iFilePath, final ModuleSubject iModuleSubject) {
        try {
            final File file = new File(iFilePath + iModuleSubject.getName() + ".wmod");
            final ModuleSubjectFactory factory = new ModuleSubjectFactory();
            JAXBModuleMarshaller marshaller = new JAXBModuleMarshaller(factory, CompilerOperatorTable.getInstance());
            marshaller.marshal(iModuleSubject, file);
            return true;
        } catch (Exception t) {
            t.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param iExpressionType 0=fromForbiddenStates, 1=fromAllowedStates, 2=mix
     * @return
     */
    public static Map<String, String> getExtractedGuards(int iExpressionType, final ModuleSubject iModuleSubject) {
        final EditorSynthesizerOptions options = new EditorSynthesizerOptions();
        options.setSynthesisType(SynthesisType.NONBLOCKINGCONTROLLABLE);
        options.setSynthesisAlgorithm(SynthesisAlgorithm.PARTITIONBDD);
//        options.setSynthesisAlgorithm(SynthesisAlgorithm.MONOLITHICBDD);

        final BDDExtendedSynthesizer bddSynthesizer = new BDDExtendedSynthesizer(new ExtendedAutomata(iModuleSubject), options);
        bddSynthesizer.synthesize(options);

        //Guard extraction
        final Vector<String> eventNames = new Vector<String>();

        for (final EventDeclSubject sigmaS : iModuleSubject.getEventDeclListModifiable()) {
            if (sigmaS.getKind() == EventKind.CONTROLLABLE)// || sigmaS.getKind() == EventKind.UNCONTROLLABLE)
            {
                eventNames.add(sigmaS.getName());
            }
        }

        options.setExpressionType(iExpressionType); //0=fromForbiddenStates, 1=fromAllowedStates, 2=mix
        bddSynthesizer.generateGuard(eventNames, options);
        final Map<String, BDDExtendedGuardGenerator> event2guard = bddSynthesizer.getEventGuardMap();

        final Map<String, String> event2guardMap = new HashMap<String, String>();

        //Print guards
        for (final String event : event2guard.keySet()) {
            final BDDExtendedGuardGenerator bddegg = event2guard.get(event);
            final String guard = bddegg.getGuard();
            event2guardMap.put(event, guard);
            System.out.println("event: " + event + " guard: " + guard);
        }

        return event2guardMap;
    }

    /**
     *
     * @param iExpressionType 0=fromForbiddenStates, 1=fromAllowedStates, 2=mix
     * @return
     */
    public Map<String, String> getExtractedGuards(int iExpressionType) {
        return getExtractedGuards(iExpressionType, getModuleSubject());
    }

    /**
     * Can only handle one EFA in <code>iModuleSubject</code>
     * @param iModuleSubject
     * @return
     */
    public static ModuleSubject getMonolithicReachabilityModule(final ModuleSubject iModuleSubject) {
        //Easier to work with extended automata than module subject
        final ExtendedAutomata extendedAutomata = new ExtendedAutomata(iModuleSubject);

        //Only one flower/efa/automaton
        final ExtendedAutomaton efa = extendedAutomata.getExtendedAutomataList().iterator().next();

        //Calculate reachability graph and save as extended automaton
        final EFAMonlithicReachability efaMR = new EFAMonlithicReachability(efa.getComponent(), extendedAutomata.getVars(), efa.getAlphabet());

        final ExtendedAutomaton efaMRautomaton = new ExtendedAutomaton(extendedAutomata, efaMR.createEFA());

        //Remove flower and variables
        iModuleSubject.getComponentListModifiable().clear();
        extendedAutomata.getExtendedAutomataList().clear();
        extendedAutomata.getVars().clear();

        //Add reachability graph automaton
        extendedAutomata.addAutomaton(efaMRautomaton);

        return extendedAutomata.getModule();
    }

    public Map<String, String> getExtractedGuardsFromReachability() {
        return getExtractedGuards(2, getMonolithicReachabilityModule(getModuleSubject()));
    }

    public static Automata getDFA(final ModuleSubject iModuleSubject) {
        Project project = null;
        try {
            project = new ProjectBuildFromWaters(new DocumentManager()).build(iModuleSubject);

//            for (Automaton automaton : project) {
//                System.out.println("Automaton: " + automaton.getName());
//                for (TransitionProxy tp : automaton.getTransitions()) {
//                    System.out.println("Event: " + tp.getEvent().getName());
//                }
//            }

        } catch (EvalException e) {
            System.out.println(e.toString());
        }
        return (Automata) project;
    }

    public Automata getDFA() {
        return getDFA(getModuleSubject());
    }

    /**
     * Non-blocking and controllable monolithic synthesis for DFA<br/>
     * @param iAutomata
     * @return Purged supervisor if ok else null
     */
    public static Automaton getMonolithicSupervisor(Automata iAutomata) {
        if (iAutomata != null) {

            final SynthesizerOptions syntho = new SynthesizerOptions();
            syntho.setSynthesisType(SynthesisType.NONBLOCKINGCONTROLLABLE);
            syntho.setSynthesisAlgorithm(SynthesisAlgorithm.MONOLITHIC);
            syntho.setPurge(true);

            final SynchronizationOptions syncho = new SynchronizationOptions();
            syncho.setSynchronizationType(SynchronizationType.FULL);

            final AutomataSynthesizer as = new AutomataSynthesizer(iAutomata, syncho, syntho);

            try {
                iAutomata = as.execute();
                return iAutomata.getFirstAutomaton();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return null;
    }

    public static Automaton flattenOutAndGetMonolithicSupervisor(final ModuleSubject iModuleSubject) {
        final Automata automata = getDFA(iModuleSubject);
        if (automata == null) {
            return null;
        }
        return getMonolithicSupervisor(automata);
    }

    public Automaton fattenOutAndGetMonolithicSupervisor() {
        return flattenOutAndGetMonolithicSupervisor(getModuleSubject());
    }
}
