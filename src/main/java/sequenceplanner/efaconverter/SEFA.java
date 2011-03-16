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

    private String guardFromSPtoEFASyntaxTranslation(String guard) {
        //Change all _i to ==0
        guard = guard.replaceAll("_i", "==0");
        //Change all _e to ==1
        guard = guard.replaceAll("_e", "==1");
        //Change all _f to ==2
        guard = guard.replaceAll("_f", "==2");
        //Change all A to &
        guard = guard.replaceAll("A", "&");
        //Change all V to |
        guard = guard.replaceAll("V", "|");

        return guard;
    }

    public void addGuardBasedOnSPCondition(String iCondition, SEGA iEga, String iOpVariablePrefix) {

        //Create condition
        String condition = iData.getCondition();

        //add precondition to guard
        if (!iCondition.equals("")) {
            String guardPreCon = condition; //Example of raw precondition 18_f A (143_iV19_f)

            //Change all ID to ProductType_ID
            for (InternalOpData i : productTypes.get(productType)) {
                guardPreCon = guardPreCon.replaceAll(i.getId().toString(), ps + i.getId());
            }

            guardPreCon = guardFromSPtoEFASyntaxTranslation(guardPreCon);

            iEga.andGuard(guardPreCon);
        }
    }
}

