package sequenceplanner.multiProduct;

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.supremica.external.avocades.common.EFA;

/**
 *
 * @author patrik
 */
public class SEFA {

    private EFA efa = null;
    private String name = null;
    private ArrayList<JPanel> jPanel = null;

    public SEFA(String name, SModule sm) {
        efa = new EFA(name, sm.getModule());
        this.name = name;
        sm.addAutomaton(this);
        jPanel = new ArrayList<JPanel>();
    }
    public EFA getEFA() {
        return efa;
    }
    public String getName() {
        return name;
    }

    public ArrayList<JPanel> getTransitionsAsJPanel() {
        return jPanel;
    }

    public void addState(String name, boolean accepting, boolean initial) {
        efa.addState(name, accepting, initial);
    }

    public void addTransition(String from, String to, String event, String guard, String action) {
        efa.addTransition(from, to, event, guard, action);
        addToJPanel(event, guard, action);
    }
    private void addToJPanel(String event, String guard, String action) {
        JPanel jp = new JPanel(new GridLayout(1,3,5,5));
        jp.add(new JLabel(event));
        jp.add(new JLabel(guard));
        jp.add(new JLabel(action));
        jPanel.add(jp);
    }
}
