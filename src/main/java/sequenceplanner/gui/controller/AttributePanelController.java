package sequenceplanner.gui.controller;

import sequenceplanner.gui.view.attributepanel.OperationAttributeEditor;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class AttributePanelController implements ActionListener, Observer {

    private AttributePanel attributePanel;
    private OperationData model;
    private OperationAttributeEditor attributeEditor;
    private boolean preRadioButton;
    private boolean guardRadioButton;
    /**
     * Creates an AttributePanelController with two views and one model
     * @param model
     * @param panel
     * @param editor 
     */
    public AttributePanelController(OperationData model, AttributePanel panel, OperationAttributeEditor editor) {
        this.model = model;
        this.attributePanel = panel;
        this.attributeEditor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("save")) {
            
            System.out.println("pre: "+attributeEditor.getPreButtonStatus());
            System.out.println("guard: "+attributeEditor.getGuardButtonStatus());
            preRadioButton = attributeEditor.getPreButtonStatus();
            guardRadioButton = attributeEditor.getGuardButtonStatus();
            setCondition(attributeEditor.getConditionString());
        } else if(e.getActionCommand().equalsIgnoreCase("edit")){
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

        if (guardRadioButton==true) {//Guard
            final AStringToConditionParser parser = new GuardAsTextInputToConditionParser();
            final ConditionExpression ce = new ConditionExpression();
            if (parser.run(conditionString, ce)) {
                condition.setGuard(ce);
            } else {
                JOptionPane.showMessageDialog(null, "This is not a correct guard!\n" +
                        "This is: (id1234<e&id1002!=e&&(id1003==12342&id1004!=e))&&id1005==2&id1006!=e&&id1007==e||(id1008==2&id1009!=f)");
            }

        } else { //action
            final AStringToConditionParser parser = new ActionAsTextInputToConditionParser();
            final ConditionExpression ce = new ConditionExpression();
            if (parser.run(conditionString, ce)) {
                condition.setAction(ce);
            } else {
                JOptionPane.showMessageDialog(null, "This is not a correct action!\n" +
                        "This is: (id1234=100&id1002+=2&&(id1003=123|id1004=2))&&id1005-=2&id1006+=99&&id1007=7");
            }
        }

        if(preRadioButton==true) {
            model.getConditions().put(ConditionType.PRE, condition);
        } else { //post
            model.getConditions().put(ConditionType.POST, condition);
        }
    }
}
