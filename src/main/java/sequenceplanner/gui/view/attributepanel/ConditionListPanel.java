package sequenceplanner.gui.view.attributepanel;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.gui.controller.AttributeMouseAdapter;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.utils.StringTrimmer;

/**
 * Panel showing a list of Conditions.
 * @author Qw4z1
 */
public class ConditionListPanel extends JPanel implements IConditionListPanel {

    private HashMap<ConditionData, Condition> conditionList;
    private JPanel internalPanel;
    JLabel conditionLabel;
    OperationAttributeEditor editor;
    OperationData opData;
    ConditionType type;
    private Model mModel;

    public ConditionListPanel(OperationAttributeEditor editor, OperationData opData, ConditionType type, final Model iModel) {
        this.editor = editor;
        this.opData = opData;
        this.type = type;
        this.mModel = iModel;
        conditionList = new HashMap<ConditionData, Condition>();
        init();
    }

    private void init() {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    }

    @Override
    public void addCondition(ConditionData key, Condition condition) {
        conditionList.put(key, condition);
        updateList();

    }

    private void updateList() {
        //System.out.println("updateList CLP");
        if (conditionList != null) {
            this.removeAll();
            for (ConditionData key : conditionList.keySet()) {
                if (conditionList.get(key) != null) {
                    internalPanel = new JPanel();
                    internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.X_AXIS));
                    conditionLabel = new JLabel(key.getName() + ": " + conditionList.get(key).toString());
                    conditionLabel.setVisible(true);
                    internalPanel.add(conditionLabel);
                    this.add(internalPanel);
                    internalPanel.setVisible(true);
                    addConditionListener(new AttributeMouseAdapter(editor, this, mModel));
                } else {
                    this.removeAll();

                }
                this.updateUI();
            }

        } else {
//            System.out.println("removeall");
            this.removeAll();
            conditionList.clear();
            this.repaint();
            this.updateUI();
        }
        this.updateUI();
    }

    /**
     * Trim condition to only include condition key in <code>global condition map</code> for this operation.
     * @param iConditionKey
     * @return
     */
    private ConditionData getConditionKey(final String iConditionKey) {
        final Set<ConditionData> conditionSet = opData.getConditions().keySet();
        for (final ConditionData condition : conditionSet) {
            final Pattern conditionKeyPattern = Pattern.compile(condition.getName());
            final Matcher matcher = conditionKeyPattern.matcher(iConditionKey);

            if (matcher.find()) {
                return condition;
            }
        }
        return null;
    }

    @Override
    public void deleteCondition(String iConditionKey) {

        final ConditionData conditionKey = getConditionKey(iConditionKey);

        opData.getConditions().remove(conditionKey);
        conditionList.remove(conditionKey);

        this.updateList();

    }

    @Override
    public void editCondition(String iConditionKey) throws NullPointerException {
        final ConditionData conditionKey = getConditionKey(iConditionKey);
        
        String conditionString = "";

        //To extract the original input string
        if (type == ConditionType.PRE) {
            editor.setPreButtonStatus(true);
            if (opData.getConditions().get(conditionKey).get(ConditionType.PRE).hasGuard()) {
//                conditionString = StringTrimmer.getInstance().stringTrim(conditionString + opData.getConditions().get(conditionKey).get(ConditionType.PRE).getGuard().toString());
                conditionString += opData.getConditions().get(conditionKey).get(ConditionType.PRE).getGuard().toString();
                editor.setGuardButtonStatus(true);
            } else if (opData.getConditions().get(conditionKey).get(ConditionType.PRE).hasAction()) {
//                conditionString = StringTrimmer.getInstance().stringTrim(conditionString + opData.getConditions().get(conditionKey).get(ConditionType.PRE).getAction().toString());
                conditionString += opData.getConditions().get(conditionKey).get(ConditionType.PRE).getAction().toString();
                editor.setActionButtonStatus(true);
            }
        } else if (type == ConditionType.POST) {
            editor.setPostButtonStatus(true);
            if (opData.getConditions().get(conditionKey).get(ConditionType.POST).hasGuard()) {
//                conditionString = StringTrimmer.getInstance().stringTrim(conditionString + opData.getConditions().get(conditionKey).get(ConditionType.POST).getGuard().toString());
                conditionString += opData.getConditions().get(conditionKey).get(ConditionType.POST).getGuard().toString();
                editor.setGuardButtonStatus(true);
            } else if (opData.getConditions().get(conditionKey).get(ConditionType.POST).hasAction()) {
//                conditionString = StringTrimmer.getInstance().stringTrim(conditionString + opData.getConditions().get(conditionKey).get(ConditionType.POST).getAction().toString());
                conditionString += opData.getConditions().get(conditionKey).get(ConditionType.POST).getAction().toString();
                editor.setActionButtonStatus(true);
            }
        }

        //Remebmer name for condition
        editor.mConditionData = conditionKey;
        //Place the String in the input text window
        editor.setConditionString(conditionString);
        
        editor.setConditionTypeString(conditionKey.getName());

        deleteCondition(conditionKey.getName());
    }

    @Override
    public boolean contains(Condition condition) {
        return conditionList.containsValue(condition);
    }

    void clear() {
        conditionList.clear();
        this.removeAll();
        this.repaint();
    }

    /*
     * Adds ActionListener to the conditions that are listed
     */
    public void addConditionListener(AttributeMouseAdapter l) {
        conditionLabel.addMouseListener(l);
    }
}
