package sequenceplanner.gui.view.attributepanel;

import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.model.data.ConditionData;

/**
 * Interface defining public methods for ConditionListPanel.
 *
 * @author Qw4z1
 */
public interface IConditionListPanel {

    /**
     * Adds a condition to the list of conditions.
     * Does accept null conditions.
     *
     * @param {@link ConditionData} describing the conditions location or parent.
     * @param {@link Conditon} the condition
     */
    public void addCondition(ConditionData key, Condition condition);

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
    public void deleteCondition(String conditionLabel) throws NullPointerException;

    public void editCondition(String conditionLabel) throws NullPointerException;
}
