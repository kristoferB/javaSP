package sequenceplanner.multiProduct;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.VariableHelper;
import org.supremica.external.avocades.common.Module;

/**
 *
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

    public void addComment(String comment) {
        setComment(getModuleSubject().getComment() + "\n" + comment);
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

    private ModuleSubject generateTransitions() {
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
                Calculation.saveWMODFile(getModuleSubject());
            }
        }
    }
}
