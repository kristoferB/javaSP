package sequenceplanner.gui.controller;

import java.awt.event.KeyEvent;
import sequenceplanner.gui.view.attributepanel.OperationAttributeEditor;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;
import sequenceplanner.condition.AStringToConditionParser;
import sequenceplanner.condition.ActionAsTextInputToConditionParser;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.GuardAsTextInputToConditionParser;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.OperationData;

/**
 * Listens to changes in the OperationData object connected to the AttributePanel
 * and updates the panel accordingly
 * @author Qw4z1
 */
public class AttributePanelController implements ActionListener, Observer, KeyListener {

    private AttributePanel attributePanel;
    private OperationData opData;
    private OperationAttributeEditor attributeEditor;

    /**
     * Creates an AttributePanelController with two views and one model
     * @param model
     * @param panel
     * @param editor
     */
    public AttributePanelController(OperationData model, AttributePanel panel, OperationAttributeEditor editor) {
        this.opData = model;
        this.attributePanel = panel;
        this.attributeEditor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equalsIgnoreCase("save")) {
            if (!attributeEditor.getConditionString().isEmpty()) {
                setCondition(attributeEditor.getConditionString());
            }
        } else if (e.getActionCommand().equalsIgnoreCase("edit")) {
            attributeEditor.opendToEdit(e.getSource());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        OperationData od = (OperationData) arg;
        if (od.getName().equalsIgnoreCase(attributePanel.getName())) {
            attributePanel.updateModel(od);
            System.out.println("APC" + o.toString());
        }
    }

    /**
     * Adds a set of conditions to the OperationData object acting as model
     * @param conditionString String conditions as a string in the SP form.
     */
    private void setCondition(String conditionString) {
        //ConditionType should be selected from the choises of the radiobuttons
        final Condition condition = new Condition();

        if (attributeEditor.getGuardButtonStatus()) {//Guard
            final AStringToConditionParser parser = new GuardAsTextInputToConditionParser();
            final ConditionExpression ce = new ConditionExpression();
            if (parser.run(conditionString, ce)) {
                condition.setGuard(ce);
            } else {
                JOptionPane.showMessageDialog(null, "This is not a correct guard!\n"
                        + "This is: (id1234<e&id1002!=e&&(id1003==12342&id1004!=e))&&id1005==2&id1006!=e&&id1007==e||(id1008==2&id1009!=f)");
            }

        } else { //action
            final AStringToConditionParser parser = new ActionAsTextInputToConditionParser();
            final ConditionExpression ce = new ConditionExpression();
            if (parser.run(conditionString, ce)) {
                condition.setAction(ce);
            } else {
                JOptionPane.showMessageDialog(null, "This is not a correct action!\n"
                        + "This is: (id1234=100;id1002+=2;(id1003=123;id1004=2));id1005-=2;id1006+=99;id1007=7");
            }
        }
        Map<ConditionType, Condition> map = new HashMap<ConditionType, Condition>();
        if (attributeEditor.getPreButtonStatus()) {
            map.put(ConditionType.PRE, condition);
        } else { //post
            map.put(ConditionType.POST, condition);
        }

        opData.setConditions(map, "Algebraic " + opData.getAlgebraicCounter());
        opData.increaseAlgebraicCounter();

        this.attributePanel.setConditions();
        this.attributeEditor.clearTextField();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("get keycode" + e.getKeyCode());
        System.out.println("keyevent keycode" + KeyEvent.VK_ENTER);
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            setCondition(attributeEditor.getConditionString());
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
