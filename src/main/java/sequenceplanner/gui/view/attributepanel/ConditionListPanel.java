package sequenceplanner.gui.view.attributepanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sequenceplanner.condition.Condition;
import sequenceplanner.gui.controller.AttributeMouseAdapter;
import sequenceplanner.model.data.OperationData;

/**
 * Panel showing a list of Conditions. 
 * @author Qw4z1
 */
public class ConditionListPanel extends JPanel implements IConditionListPanel {

    private HashMap<String, Condition> conditionList;
    private JPanel internalPanel;
    JLabel conditionLabel;
    public ConditionListPanel() {
        conditionList = new HashMap<String, Condition>();
        init();
    }

    private void init() {
        this.setBackground(Color.white);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    }

    @Override
    public void addCondition(String key, Condition condition) {
        conditionList.remove(key);
        conditionList.put(key, condition);
        updateList();

    }

    private void updateList() {
        if (conditionList != null) {
            this.removeAll();
            for (String key : conditionList.keySet()) {
                if (conditionList.get(key) != null) {
                    System.out.println("kommer hit");
                    internalPanel = new JPanel();
                    internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.X_AXIS));
                    conditionLabel = new JLabel(key + " " + conditionList.get(key).toString());
                    conditionLabel.setVisible(true);
                    internalPanel.add(conditionLabel);
                    this.add(internalPanel);
                    internalPanel.setVisible(true);
                    addConditionListener(new AttributeMouseAdapter());
                } else {
                    this.removeAll();
                    
                }
                this.updateUI();
            }

        } else {
            this.removeAll();
            this.repaint();
        }
    }

    @Override
    public void deleteCondition(String conditionValue){
            System.out.println("Delete: "+ conditionValue);
            //opData.removeCondition();
        
    }
    @Override
    public String getConditionValue(Component conditionLabel){
        JLabel condition = (JLabel)conditionLabel;
        Pattern conditionValuePattern = Pattern.compile("Algebraic "+"(\\d)");
        Matcher m1 = conditionValuePattern.matcher(condition.getText());
        if(m1.find()){
            return m1.group();
        }else return "";
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
        this.removeAll();
        this.repaint();
}
        /*
     * Adds ActionListener to
     */

    public void addConditionListener(MouseAdapter l){
        conditionLabel.addMouseListener(l);

    }
}
