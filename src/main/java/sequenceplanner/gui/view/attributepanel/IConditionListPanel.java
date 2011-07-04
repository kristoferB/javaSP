/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.gui.view.attributepanel;

import java.awt.Component;
import sequenceplanner.condition.Condition;

/**
 * Interface defining public methods for ConditionListPanel.
 * 
 * @author Qw4z1
 */
public interface IConditionListPanel{

    /**
     * Adds a condition to the list of conditions.
     * Does accept null conditions.
     * 
     * @param String a string describing the conditions location or parent.
     * @param {@link Conditon} the condition
     */
    public void addCondition(String key, Condition condition);

    /**
     * Removes the specified {@link Condition} from the panel.
     * @throws NullPointerException if condition is null or if the Condition
     * does not exist in the list.
     * @param Condition the Condition
     */
    public void removeCondition(Condition condition) throws NullPointerException;

    /**
     * Removes the condition in place i.
     * @param i index of the Condition
     * @throws NullPointerException if no Condtion exists in place i
     */
    public void removeCondition(int i) throws NullPointerException;

    /**
     * Checks if the panel contains a Condition
     * @param condition the Condition
     * @return true if it contains the Condition
     */
    public boolean contains(Condition condition);


    /**
     * Removes the condition that's in the Label.
     * @param The JLabel that displays the Condition
     * @throws NullPointerException if no Condtion exists in place i
     */
    public void deleteCondition(String conditionLabel)throws NullPointerException;

    public String getConditionValue(Component conditionLabel);
}
