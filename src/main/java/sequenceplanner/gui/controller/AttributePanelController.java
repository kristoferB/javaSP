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
 * Listens to changes in the OperationData object connected to the AttributePanel
 * and updates the panel accordingly 
 * @author Qw4z1
 */
public class AttributePanelController implements ActionListener, Observer {

    private AttributePanel attributePanel;
    private OperationData model;
    private OperationAttributeEditor attributeEditor;
    
    /**
     * Creates an AttributePanelController with two views and one model
     * @param model
     * @param panel
     * @param editor 
     */
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
        OperationData od = (OperationData)arg;
        if(od.getName().equalsIgnoreCase(attributePanel.getName()))
            attributePanel.updateModel(od);
        
    }
    /**
     * Adds a set of conditions to the OperationData object acting as model
     * @param conditionString String conditions as a string in the SP form.
     */
    private void setCondition(String conditionString) {
        //ConditionType should be selected from the choises of the radiobuttons
        model.getConditions().put(ConditionType.PRE, StringConditionParser.getInstance().parseConditionString(conditionString));
    
    }
}
