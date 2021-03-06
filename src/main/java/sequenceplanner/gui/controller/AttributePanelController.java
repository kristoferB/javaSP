package sequenceplanner.gui.controller;

import java.awt.event.FocusEvent;
import sequenceplanner.gui.view.attributepanel.OperationAttributeEditor;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import sequenceplanner.datamodel.condition.parser.AStringToConditionParser;
import sequenceplanner.datamodel.condition.parser.ActionAsTextInputToConditionParser;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionOperator.Type;
import sequenceplanner.datamodel.condition.parser.GuardAsTextInputToConditionParser;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;

/**
 * Listens to changes in the OperationData object connected to the AttributePanel
 * and updates the panel accordingly
 * @author Qw4z1
 */
public class AttributePanelController implements ActionListener, FocusListener, Observer {

    private AttributePanel attributePanel;
    private OperationData opData;
    private OperationAttributeEditor attributeEditor;
    private GUIController controller;

    /**
     * Creates an AttributePanelController with two views and one model
     * @param model
     * @param panel
     * @param editor
     */
    public AttributePanelController(OperationData model, AttributePanel panel, OperationAttributeEditor editor, GUIController controller) {
        this.opData = model;
        this.attributePanel = panel;
        this.attributeEditor = editor;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("save")) {
            //System.out.println("AttributePanelController: save condition");
            if (!attributeEditor.getConditionString().isEmpty()) {
                System.out.println("AttributePanelController: condition string to add: " + attributeEditor.getConditionString());
                setCondition(attributeEditor.getConditionString(), attributeEditor.getConditionTypeString());
            }

        } else if (e.getActionCommand().equalsIgnoreCase("edit")) {
            attributeEditor.opendToEdit(e.getSource());
        } else if (e.getActionCommand().equalsIgnoreCase("set description")) {
            System.out.println("AttributePanelController: set description ");
            setDescription();
        }

        //save changes to model
        controller.saveOperationToModel(opData);
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() instanceof JTextField) {
            setDescription();

            //save changes to model
            controller.saveOperationToModel(opData);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            final OperationData od = (OperationData) arg;
            final int opId = od.getId();

            try {
                final int attributePanelId = attributePanel.getOperationData().getId();

                if (opId == attributePanelId) {
                    this.opData = od;
                    attributePanel.updateModel(this.opData);
                    System.out.println("APC" + o.toString());
                }

            } catch (NullPointerException npe) {
                GUIView.printToConsole("Problem to update attribute panel: " + npe);
            }
        } catch (ClassCastException cce) {
        }
    }

    /**
     * Adds a set of conditions to the OperationData object acting as model
     * @param conditionString String conditions as a string in the SP form.
     */
    private void setCondition(String conditionString, String conditionTypeString) {
        //ConditionType should be selected from the choises of the radiobuttons

        Map<ConditionType, Condition> conditionMap = null;
        for (ConditionData c : this.opData.getConditions().keySet()){
            if (c.getName().equals(conditionTypeString)){
                conditionMap = this.opData.getConditions().get(c);
                break;
            }
        }
        if (conditionMap == null){
            conditionMap = new HashMap<ConditionType, Condition>();
        }

        Condition condition = null;
        if (attributeEditor.getPreButtonStatus()) {
            if (conditionMap.containsKey(ConditionType.PRE)){
                condition = conditionMap.get(ConditionType.PRE);
            } else {
                condition = new Condition();
                conditionMap.put(ConditionType.PRE, condition);
            }
         } else { //post
            if (conditionMap.containsKey(ConditionType.POST)){
                condition = conditionMap.get(ConditionType.POST);
            } else {
                condition = new Condition();
                conditionMap.put(ConditionType.POST, condition);
            }
         }

        if (attributeEditor.getGuardButtonStatus()) {//Guard
            final AStringToConditionParser parser = new GuardAsTextInputToConditionParser();
            final ConditionExpression ce = new ConditionExpression();
            if (parser.run(conditionString, ce)) {
                condition.getGuard().appendElement(Type.AND, ce);
            } else {
                JOptionPane.showMessageDialog(null, "This is not a correct guard!\n" + "This is: (id1234<e&id1002!=e&&(id1003==12342&id1004!=e))&&id1005==2&id1006!=e&&id1007==e||(id1008==2&id1009!=f)");
                return;
            }
        } else { //action
            final AStringToConditionParser parser = new ActionAsTextInputToConditionParser();
            final ConditionExpression ce = new ConditionExpression();
            if (parser.run(conditionString, ce)) {
                condition.getAction().appendElement(Type.AND, ce);
            } else {
                JOptionPane.showMessageDialog(null, "This is not a correct action!\n" + "This is: (id1234=100&id1002+=2&&(id1003=123|id1004=2))&&id1005-=2&id1006+=99&&id1007=7");
                return;
            }
        }

        ConditionData conditionData = attributeEditor.mConditionData;

        if (conditionData == null ){
            if (conditionTypeString.isEmpty() || conditionTypeString.equals("Enter condition type")) {
                conditionData = new ConditionData("Algebraic" + opData.getAlgebraicCounter());
                opData.increaseAlgebraicCounter();
            } else {
                conditionData = new ConditionData(conditionTypeString);
            }
        } else {
            if (!conditionData.getName().equals(conditionTypeString)){
                conditionData.setName(conditionTypeString);
            }
        }
        this.opData.getConditions().put(conditionData, conditionMap);



        //Reset condition name
        attributeEditor.mConditionData = null;

        this.attributeEditor.clearTextField();

        }


    private void setDescription() {
        opData.setDescription(attributePanel.getDescriptionPanel().getDescription());
    }

    public void setName(String text) {
        opData.setName(text);
        attributePanel.updateModel(opData);
    }

    public OperationData getModel() {
        return opData;
    }
}
