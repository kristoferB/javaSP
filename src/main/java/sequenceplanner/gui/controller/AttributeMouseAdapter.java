/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.gui.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import sequenceplanner.gui.view.AttributeClickMenu;
import sequenceplanner.gui.view.attributepanel.ConditionListPanel;

/**
 *
 * @author Peter
 */
public class AttributeMouseAdapter  extends MouseAdapter{

    private ConditionListPanel condList = new ConditionListPanel();
    private Component clickedComponent;

    public AttributeMouseAdapter( /*Map<String, Map<ConditionType, Condition>> condMap*/){
        //pull out condition
        System.out.println("XxXXxXxXXXxXXXxx");
    }

/*    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("TTT");
        popup(e);
    }*/

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("RRRR");
        popup(e);
    }

    /**
     * Creates a EditorClickMenu for clicked node
     *
     * @param e a MouseEvent
     */
    private void popup(MouseEvent e) {
        System.out.println("OOO");
        //Also need to check if pre or post panel
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
            System.out.println("Click: "+e.getX()+e.getY());

            JLabel panel = (JLabel)e.getSource();

            clickedComponent = panel;
            if(clickedComponent != null){
                System.out.println("hurrum: "+panel.toString());
                AttributeClickMenu menu = new AttributeClickMenu(clickedComponent,  new MenuListener());
                if(clickedComponent instanceof JLabel){
                        System.out.println("The click is on condition panel");
                        menu.showAttributePanelMenu(e);

                }
            }
        }
    }

    /**
     * Listens for actions in AttributeClickMenu
     */
    public class MenuListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if(command.equals("DELETE_VALUE")){
                System.out.println("DELETE_VALUE");
                //AttrController.delete(cond)& delete panel
                condList.deleteCondition(clickedComponent);
            }
            if(command.equals("EDIT_VALUE")){
                System.out.println("EDIT_VALUE");
                //AttrController.displayCondInField(Cond)
                //Issue: Delete cond before or after? If after -> need to flag
                //that its edited, and delete after.
                //If before -> need check so a cond is saved.
            }
        }

    }

}


