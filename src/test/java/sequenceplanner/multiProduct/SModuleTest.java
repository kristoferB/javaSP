package sequenceplanner.multiProduct;

import javax.swing.JOptionPane;

import org.apache.log4j.BasicConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.supremica.automata.Arc;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import org.supremica.automata.AutomatonType;
import org.supremica.automata.LabeledEvent;
import org.supremica.automata.State;
import org.supremica.automata.algorithms.AutomataSynthesizer;
import org.supremica.automata.algorithms.EquivalenceRelation;

import org.supremica.automata.algorithms.SynchronizationOptions;
import org.supremica.automata.algorithms.SynchronizationType;
import org.supremica.automata.algorithms.SynthesisAlgorithm;
import org.supremica.automata.algorithms.SynthesisType;
import org.supremica.automata.algorithms.SynthesizerOptions;

import org.supremica.automata.algorithms.minimization.AutomatonMinimizer;
import org.supremica.automata.algorithms.minimization.MinimizationOptions;

import static org.junit.Assert.*;

import org.supremica.gui.VisualProject;


import org.supremica.automata.Project;

/**
 *
 * @author patrik
 */
public class SModuleTest {

    private SModule smodule = new SModule("Test module");
    private Automata automata;
    Automaton automaton;

    public SModuleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

//    @Test
    public void EFAtoDFA() {
        buildTestEFA2();
        Project project = smodule.getDFA();
//        if (project != null) {
//            viewAutomaton(project.getAutomaton("var2"));
//        }
        assertTrue(project != null);
    }

    @Test
    public void synthesis() {

        BasicConfigurator.configure();

        buildTestEFA2();
        Project project = smodule.getDFA();

        assertTrue(project != null);

        automata = (Automata) project;
        buildTestDFA();

        SynthesizerOptions syntho = new SynthesizerOptions();
        syntho.setSynthesisType(SynthesisType.NONBLOCKING);
        syntho.setSynthesisAlgorithm(SynthesisAlgorithm.MONOLITHIC);
        syntho.setPurge(true);

        SynchronizationOptions syncho = new SynchronizationOptions();
        syncho.setSynchronizationType(SynchronizationType.FULL);

        AutomataSynthesizer as = new AutomataSynthesizer(automata, syncho, syntho);

        Automata autos;

        try {
            autos = as.execute();

            MinimizationOptions options = new MinimizationOptions();
            options.setIgnoreMarking(true);
            options.setMinimizationType(EquivalenceRelation.OBSERVATIONEQUIVALENCE);

            AutomatonMinimizer minimizer = new AutomatonMinimizer(autos.getFirstAutomaton());
            final Automaton newAutomaton = minimizer.getMinimizedAutomaton(options);

            viewAutomaton(newAutomaton);
            
        } catch (Exception e) {
            System.out.println(e.toString());
            fail();

        }

    }

    public void viewAutomaton(Automaton inAutomaton) {
        VisualProject vp = new VisualProject();
        vp.addAutomaton(inAutomaton);
        try {
            vp.getAutomatonViewer(inAutomaton.getName());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        JOptionPane.showMessageDialog(null, "To end");
    }

    public void buildTestEFA() {
        SEFA e = new SEFA("EFA11", smodule);

        SEGA sega = new SEGA("event");
        sega.andGuard("var2==0");
        sega.addAction("var2=1");
        e.addStandardSelfLoopTransition(sega);

        smodule.addIntVariable("var2", 0, 4, 0, 3);
        smodule.addIntVariable("var3", 0, 4, 0, 3);

    }

    public void buildTestEFA2() {
        SEFA e = new SEFA("EFATest", smodule);
        SEGA sega;

        smodule.addIntVariable("p", 0, 3, 0, 3);

        smodule.addIntVariable("a", 0, 1, 0, null);
        sega = new SEGA("doA");
        sega.andGuard("a==0&b==0&p==0");
        sega.addAction("a=1;p=1");
        e.addStandardSelfLoopTransition(sega);

        smodule.addIntVariable("b", 0, 1, 0, null);
        sega = new SEGA("doB");
        sega.andGuard("a==0&b==0&p==0");
        sega.addAction("b=1;p=2");
        e.addStandardSelfLoopTransition(sega);

        sega = new SEGA("doF");
        sega.andGuard("(a==1&p==1)|(b==1&p==2)");
        sega.addAction("p=3");
        e.addStandardSelfLoopTransition(sega);

    }

    public void buildTestDFA() {
        //-----------------------------------------------------------------------
        automaton = new Automaton("Automaton1");
        automaton.setType(AutomatonType.PLANT);
        automata.addAutomaton(automaton);

        State s1 = new State("s1");
        s1.setInitial(true);
        automaton.addState(s1);

        State s2 = new State("s2");
        automaton.addState(s2);

        State s3 = new State("s3");
        automaton.addState(s3);

        State s4 = new State("s4");
        s4.setAccepting(true);
        automaton.addState(s4);

        State s5 = new State("s5");
        s5.setAccepting(true);
        automaton.addState(s5);

        LabeledEvent le = new LabeledEvent("a");
        le.setControllable(true);
        automaton.getAlphabet().addEvent(le);
        Arc arc = new Arc(s1, s2, le);
        automaton.addArc(arc);

        le =
                new LabeledEvent("b");
        le.setControllable(true);
        automaton.getAlphabet().addEvent(le);
        arc =
                new Arc(s1, s3, le);
        automaton.addArc(arc);

        le =
                new LabeledEvent("f");
        le.setControllable(true);
        automaton.getAlphabet().addEvent(le);
        arc =
                new Arc(s2, s4, le);
        automaton.addArc(arc);

        arc =
                new Arc(s3, s5, le);
        automaton.addArc(arc);

        //-----------------------------------------------------------------------

        //-----------------------------------------------------------------------
        automaton =
                new Automaton("Automaton2");
        automaton.setType(AutomatonType.PLANT);
        automata.addAutomaton(automaton);

        s1 =
                new State("p1");
        s1.setInitial(true);
        automaton.addState(s1);

        s2 =
                new State("p2");
        automaton.addState(s2);

        s3 =
                new State("p3");
        s3.setAccepting(true);
        automaton.addState(s3);

        le =
                new LabeledEvent("a");
        le.setControllable(true);
        automaton.getAlphabet().addEvent(le);
        arc =
                new Arc(s1, s2, le);
        automaton.addArc(arc);

        le =
                new LabeledEvent("f");
        le.setControllable(true);
        automaton.getAlphabet().addEvent(le);
        arc =
                new Arc(s2, s3, le);
        automaton.addArc(arc);
        //-----------------------------------------------------------------------

    }
}
