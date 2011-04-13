package sequenceplanner.efaconverter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.des.TransitionProxy;
import net.sourceforge.waters.model.expr.EvalException;
import net.sourceforge.waters.model.marshaller.DocumentManager;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.base.AbstractSubject;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import net.sourceforge.waters.subject.module.VariableComponentSubject;
import org.supremica.automata.*;
import org.supremica.automata.IO.ProjectBuildFromWaters;
import org.supremica.automata.VariableHelper;
import org.supremica.external.avocades.common.Module;

/**
 * Has to do with EFA. Should be merged with the general EFA conversion classes...
 * @author patrik
 */
public class SModule {

    private Module module = null;
    private ArrayList<SEFA> automata = null;

    public SModule(String name) {
        module = new Module(name, false);
        automata = new ArrayList<SEFA>();
    }

    public void setName(String name) {
        module.getModule().setName(name);
    }

    public Module getModule() {
        return module;
    }

    public ModuleSubject getModuleSubject() {
        return module.getModule();
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

    public void addAutomaton(SEFA sefa) {
        module.addAutomaton(sefa.getEFA());
        automata.add(sefa);
    }

    public void DialogAutomataTransitions() {
        new TransitionsDialog();
    }

    public ModuleSubject generateTransitions() {
        for (SEFA sefa : automata) {
            for (HashMap<String, JTextArea> trans : sefa.transitions) {
                sefa.getEFA().addTransition(trans.get(SEFA.FROM).getText(),
                        trans.get(SEFA.TO).getText(), trans.get(SEFA.EVENT).getText(),
                        trans.get(SEFA.GUARD).getText(), trans.get(SEFA.ACTION).getText());
            }
        }
        return module.getModule();
    }

    /**
     * Method in this class can't handle IDs that are suffix or prefix to each other, e.g. 18 and 118
     * @return true if IDs are ok else false
     */
    public boolean testIDs(ModelParser iModelParser) {
        String test = "";

        for (OpNode opNode : iModelParser.getOperations()) {
            if (test.contains(opNode.getStringId())) {
                return false;
            } else {
                test = test + opNode.getStringId() + "_";
            }
        }
        for (OpNode opNode : iModelParser.getOperations()) {
            final String test2 = test.replaceFirst(opNode.getStringId(), "");
            if (test2.contains(opNode.getStringId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method in this class can't handle IDs that are suffix or prefix to each other, e.g. 18 and 118
     * @return true if IDs are ok else false
     */
    public boolean testIDs(final Set<Integer> iSet) {
        String test = "";

        for (final Integer id : iSet) {
            if (test.contains(Integer.toString(id))) {
                return false;
            } else {
                test = test + Integer.toString(id) + "_";
            }
        }
        for (final Integer id : iSet) {
            final String test2 = test.replaceFirst(Integer.toString(id), "");
            if (test2.contains(Integer.toString(id))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Translates {@link SModule} (this object) to {@link Project}.</br>
     * {@link Project} extends {@link Automata}.
     * @return a {@link Project} that can be used as a {@link Automata}.
     */
    public Project getDFA() {

        Project project = null;
//        if (variableInclusionCheck()) {
        try {
            project = new ProjectBuildFromWaters(new DocumentManager()).build(generateTransitions());

            for (Automaton automaton : project) {
//                System.out.println("Automaton: " + automaton.getName());
                for (TransitionProxy tp : automaton.getTransitions()) {
//                    System.out.println("Event: " + tp.getEvent().getName());
                }
            }

        } catch (EvalException e) {
            System.out.println(e.toString());
        }
//        } else {
//            System.out.println("No variables appears in guards or actions for automata. I will not go on!");
//        }
        return project;
    }

    private boolean variableInclusionCheck() {
        Set<String> variables = new HashSet<String>();

        //Collect variables in module
        for (AbstractSubject as : generateTransitions().getComponentListModifiable()) {
            if (as instanceof VariableComponentSubject) {
                variables.add(((VariableComponentSubject) as).getName());
            }
        }

        //Check all guards and actions.
        //It is enough that one variable in variables is included in one guard or one action
        for (SEFA sefa : automata) {
            for (HashMap<String, JTextArea> trans : sefa.transitions) {
                for (String var : variables) {
                    if (trans.get(SEFA.GUARD).getText().contains(var)) {
                        return true;
                    } else if (trans.get(SEFA.ACTION).getText().contains(var)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Dialog to show transitions
     */
    private class TransitionsDialog implements ActionListener {

        JFrame mainFrame = null;
        JButton execButton = null;

        public TransitionsDialog() {
            Dialog();
        }

        private void Dialog() {
            execButton = new JButton("Generate .wmod file");
            execButton.addActionListener(this);

            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            JScrollPane jsp = new JScrollPane(p);
            jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            jsp.setViewportBorder(new LineBorder(Color.RED));
            jsp.setPreferredSize(new Dimension(800, 800));

            mainFrame = new JFrame("Transitions:");
            mainFrame.setLocation(100, 100);
            mainFrame.getContentPane().add(jsp, BorderLayout.CENTER);
//            mainFrame.setAlwaysOnTop(true);

            for (SEFA automaton : automata) {
                p.add(new JLabel(automaton.getName()));
                for (HashMap<String, JTextArea> trans : automaton.getTransitions()) {
                    JPanel jp = new JPanel(new FlowLayout(5, 5, 5));
                    jp.add(trans.get(SEFA.EVENT));
                    jp.add(trans.get(SEFA.GUARD));
                    jp.add(trans.get(SEFA.ACTION));
                    p.add(jp);
                }
            }

            mainFrame.getContentPane().add(execButton, BorderLayout.SOUTH);
            mainFrame.pack();
            mainFrame.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (execButton == e.getSource()) {
                generateTransitions();
                mainFrame.dispose();
                saveToWMODFile();
            }
        }
    }

    /**
     * User is given dialog to select file name and path.<br/>
     */
    public void saveToWMODFile() {
        try {
            String filepath = "";
            JFileChooser fc = new JFileChooser("C:\\Documents and Settings\\EXJOBB SOCvision\\Desktop");
            int fileResult = fc.showSaveDialog(null);
            if (fileResult == JFileChooser.APPROVE_OPTION) {
                filepath = fc.getSelectedFile().getAbsolutePath();
                File file = new File(filepath);
                file.createNewFile();
                getModuleSubject().setName(file.getName().replaceAll(".wmod", ""));
                ModuleSubjectFactory factory = new ModuleSubjectFactory();
                // Save module to file
                JAXBModuleMarshaller marshaller = new JAXBModuleMarshaller(factory, CompilerOperatorTable.getInstance());
                marshaller.marshal(getModuleSubject(), file);
            }

        } catch (Exception t) {
            t.printStackTrace();
        }
    }

    /**
     * Save to wmod file given as parameter.<br/>
     * @param iFilePath path to file
     * @return true if save was ok else false
     */
    public boolean saveToWMODFile(final String iFilePath) {
        generateTransitions();
        try {
            File file = new File(iFilePath);
            getModuleSubject().setName(file.getName().replaceAll(".wmod", ""));
            ModuleSubjectFactory factory = new ModuleSubjectFactory();
            // Save module to file
            JAXBModuleMarshaller marshaller = new JAXBModuleMarshaller(factory, CompilerOperatorTable.getInstance());
            marshaller.marshal(getModuleSubject(), file);

            return true;
        } catch (Exception t) {
            t.printStackTrace();
        }
        return false;
    }
}
