package sequenceplanner.gui.controller;

import sequenceplanner.gui.view.attributepanel.OperationAttributeEditor;
import sequenceplanner.gui.view.attributepanel.AttributePanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import sequenceplanner.condition.StringConditionParser;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author Qw4z1
 */
public class AttributePanelController implements ActionListener, Observer {

    private AttributePanel attributePanel;
    private OperationData model;
    private OperationAttributeEditor attributeEditor;
    

    public AttributePanelController(OperationData model,AttributePanel panel, OperationAttributeEditor editor) {
        this.model = model;
        this.attributePanel = panel;
        this.attributeEditor = editor;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("save")) {
            setCondition(attributeEditor.getConditionString());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.hasChanged() && arg == "Condition") {
            attributePanel.setConditions();
        }
    }

    private void setCondition(String conditionString) {
        //ConditionType should be selected from the choises of the radiobuttons
        model.setConditions(StringConditionParser.getInstance().getConditionMap(conditionString,ConditionType.PRE));
    
    }
}
