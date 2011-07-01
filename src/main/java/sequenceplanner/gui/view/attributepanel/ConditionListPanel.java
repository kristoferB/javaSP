package sequenceplanner.gui.view.attributepanel;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    public void addCondition(String key, Condition condition) {
        conditionList.remove(key);
        conditionList.put(key, condition);
        updateList();

    }

    private void updateList() {
        System.out.println("updateList CLP");
        if (conditionList != null) {
            this.removeAll();
            for (String key : conditionList.keySet()) {
                if (conditionList.get(key) != null) {
                    System.out.println("panel");
                    JPanel internalPanel = new JPanel();
                    internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.X_AXIS));
                    JLabel conditionLabel = new JLabel(key + " " + conditionList.get(key).toString());
                    conditionLabel.setVisible(true);
                    if (key.equals("manual")) {
                        JButton editButton = new JButton("Edit");
                        internalPanel.add(editButton);
                        JButton deleteButton = new JButton("Delete");
                        internalPanel.add(deleteButton);
                    }

                    internalPanel.add(conditionLabel);
                    this.add(internalPanel);
                    internalPanel.setVisible(true);
                } else {
                    this.removeAll();
                    
                }
                this.repaint();

            }

        } else {
            System.out.println("removeall");
            this.removeAll();
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

    void clear() {
        System.out.println("clear2");
        this.removeAll();
        this.repaint();
    }
}
