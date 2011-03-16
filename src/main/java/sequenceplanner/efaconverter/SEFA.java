package sequenceplanner.efaconverter;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.supremica.external.avocades.common.EFA;

/**
 *
 * @author patrik
 */
public class SEFA {

    private EFA efa = null;
    private String name = null;
    private ArrayList<JPanel> jPanel = null;
    public ArrayList<HashMap<String, JTextArea>> transitions = null;
    final static String FROM = "from";
    final static String TO = "to";
    final static String EVENT = "event";
    final static String GUARD = "guard";
    final static String ACTION = "action";

    public SEFA(String name, SModule sm) {
        efa = new EFA(name, sm.getModule());
        this.name = name;
        sm.addAutomaton(this);
        jPanel = new ArrayList<JPanel>();
        transitions = new ArrayList<HashMap<String, JTextArea>>();
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

    public ArrayList<HashMap<String, JTextArea>> getTransitions() {
        return transitions;
    }

    public void addState(String name, boolean accepting, boolean initial) {
        efa.addState(name, accepting, initial);
    }

    public void addTransition(String from, String to, String event, String guard, String action) {
        HashMap<String, JTextArea> transString = new HashMap<String, JTextArea>(5);
        transitions.add(transString);

        //Save this transition
        transString.put(FROM, new JTextArea(from));
        transString.put(TO, new JTextArea(to));
        transString.put(EVENT, new JTextArea(event));
        transString.put(GUARD, new JTextArea(guard));
        transString.put(ACTION, new JTextArea(action));

    }
    public void addStandardSelfLoopTransition(SEGA ega) {
        addTransition("pm", "pm", ega.getEvent(), ega.getGuard(), ega.getAction());
    }
}

