package sequenceplanner.multiProduct;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.VariableHelper;
import org.supremica.external.avocades.common.EFA;
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

    /**
     * Dialog to show transitions
     */
    private class TransitionsDialog implements ActionListener {

        JFrame mainFrame = null;
        JButton closeButton = null;

        public TransitionsDialog() {
            Dialog();
        }

        private void Dialog() {
            closeButton = new JButton("Close");
            closeButton.addActionListener(this);

            mainFrame = new JFrame("Transitions:");
            mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
            mainFrame.setLocationRelativeTo(null);
//            mainFrame.setAlwaysOnTop(true);

            for(SEFA automaton: automata) {
                mainFrame.getContentPane().add(new JLabel(automaton.getName()));
                for(JPanel jp : automaton.getTransitionsAsJPanel()) {
                    mainFrame.getContentPane().add(jp);
                }
            }

            mainFrame.getContentPane().add(closeButton);
            mainFrame.pack();
            mainFrame.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (closeButton == e.getSource()) {
                mainFrame.dispose();
            }
        }
    }
}
