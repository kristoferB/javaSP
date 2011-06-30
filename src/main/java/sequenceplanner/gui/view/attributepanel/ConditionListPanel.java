package sequenceplanner.gui.view.attributepanel;

import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sequenceplanner.condition.Condition;

/**
 * Panel showing a list of Conditions. 
 * @author Qw4z1
 */
public class ConditionListPanel extends JPanel implements IConditionListPanel {

    private HashMap<String, Condition> conditionList;

    public ConditionListPanel() {
        conditionList = new HashMap<String, Condition>();
        init();
    }

    private void init() {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    }

    @Override
    public void addCondition(String key, Condition condition) throws NullPointerException {
        if (condition == null) {
            throw new NullPointerException();
        } else {
            conditionList.put(key, condition);
            updateList();
        }
    }

    private void updateList() {
        if (!conditionList.isEmpty()) {
            this.removeAll();
            for (String key : conditionList.keySet()) {
                JPanel internalPanel = new JPanel();
                internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.X_AXIS));
                JLabel conditionLabel = new JLabel(key +" "+ conditionList.get(key).toString());
                conditionLabel.setVisible(true);
                if (key.equals("manual")) {
                    JButton editButton = new JButton("Edit");
                    internalPanel.add(editButton);
                    JButton deleteButton = new JButton("Delete");
                    internalPanel.add(deleteButton);
                }

                internalPanel.add(conditionLabel);
                this.add(internalPanel);


            }
            this.repaint();
        }
    }

    @Override
    public void removeCondition(Condition condition) throws NullPointerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeCondition(int i) throws NullPointerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Condition condition) {
        return conditionList.containsValue(condition);
    }
}
