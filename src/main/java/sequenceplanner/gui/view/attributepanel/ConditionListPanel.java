package sequenceplanner.gui.view.attributepanel;

import java.util.HashMap;
import javax.swing.BorderFactory;
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

    public ConditionListPanel(String title) {
        conditionList = new HashMap<String, Condition>();
        if(title != null || !title.equalsIgnoreCase(""))
            this.setBorder(BorderFactory.createTitledBorder(title));
    }

    @Override
    public void addCondition(String key, Condition condition) throws NullPointerException {
        if (condition == null) {
            throw new NullPointerException();
        } else {
            conditionList.put(key, condition);
        }
    }

    private void updateList() {
        if (!conditionList.isEmpty()) {
            for (String key : conditionList.keySet()) {
                JLabel conditionLabel = new JLabel(key + conditionList.get(key).toString());
                conditionLabel.setVisible(true);
                if (key.equals("manual")) {
                    JButton editButton = new JButton("Edit");
                    this.add(editButton);
                }
                
                this.add(conditionLabel);

            }
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
}
