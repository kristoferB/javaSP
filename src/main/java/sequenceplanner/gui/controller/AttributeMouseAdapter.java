package sequenceplanner.gui.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import sequenceplanner.gui.view.AttributeClickMenu;
import sequenceplanner.gui.view.attributepanel.ConditionListPanel;
import sequenceplanner.gui.view.attributepanel.OperationAttributeEditor;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.ViewData;

/**
 *
 * @author Peter
 */
public class AttributeMouseAdapter extends MouseAdapter {

    private Component clickedComponent;
    private String conditionKey;
    private String conditionValue;
    private AttributeClickMenu menu;
    private OperationAttributeEditor editor;
    private ConditionListPanel condList;
    private Model mModel;

    public AttributeMouseAdapter(OperationAttributeEditor editor, ConditionListPanel condList, final Model iModel) {
        this.editor = editor;
        this.condList = condList;
        this.mModel = iModel;
        //System.out.println("AttributeMouseAdapter costructor");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("AttributeMouseAdapter: mouseReleased");
        popup(e);
    }

    /**
     * Creates a AttributeClickMenu for clicked node
     *
     * @param e a MouseEvent
     */
    private void popup(MouseEvent e) {
        //Also need to check if pre or post panel
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
//            System.out.println("AttributeMouseAdapter: Click: " + e.getX() + e.getY());

            clickedComponent = (JLabel) e.getSource();

            if (clickedComponent != null) {
                if (clickedComponent instanceof JLabel) {


                    //To set <p>conditionKey</p>
                    getConditionValue(clickedComponent);

                    if (!conditionKey.equals("")) {
                        menu = new AttributeClickMenu(clickedComponent, new MenuListener(), true);
                    } else {
                        menu = new AttributeClickMenu(clickedComponent, new MenuListener(), false);
                    }
                    menu.showAttributePanelMenu(e);
                }
            }
        }
    }

    /**
     * Listens for actions in AttributeClickMenu
     */
    public class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("DELETE_VALUE")) {
                //System.out.println("DELETE_VALUE");
                //AttrController.delete(cond)& delete panel
                //condList.deleteCondition(conditionValue);
                //flytta delete hit..
                condList.deleteCondition(conditionKey);
            }
            if (command.equals("EDIT_VALUE")) {
                //System.out.println("EDIT_VALUE");
                //AttrController.displayCondInField(Cond)
                //System.out.println("Edit: " + conditionKey);

                condList.editCondition(conditionKey);

            }
        }
    }

    public void getConditionValue(Component conditionLabel) {
        final JLabel localConditionLabel = (JLabel) conditionLabel;

        //Loop conditions defined in SOP views in order to disable popup menu through <p>conditionKey</p> if conditionLabel is from SOP view
        final Set<ViewData> sopConditionSet = mModel.getAllSOPs().keySet();
        for (final ViewData sopName : sopConditionSet) {
            final Pattern conditionKeyPattern = Pattern.compile(sopName.getName());
            final Matcher matcher = conditionKeyPattern.matcher(localConditionLabel.getText());

            if (matcher.find()) {
                conditionKey = "";
                return;
            }
        }

        //conditionLabel is not from SOP view
        //<code>conditionKey</code> is now both key and value, e.g. signal2: id20==2
        conditionKey = localConditionLabel.getText();
    }
}
