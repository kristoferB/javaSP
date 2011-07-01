/*
 * AttributePanel.java
 *
 * Created on 2011-jun-21, 10:14:10
 */
package sequenceplanner.gui.view.attributepanel;

import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import sequenceplanner.condition.Condition;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.OperationData;

/**
 * Class showing attributes of an {@link OperationData} object
 * @author Qw4z1
 */
public class AttributePanel extends JPanel {

    private JLabel jLabel1;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private OperationAttributeEditor operationAttributeEditor;
    private JLabel operationIdLabel;
    private ConditionListPanel postCondListPanel;
    private ConditionListPanel preCondListPanel;
    /**OperationData object, acting as model for the view**/
    private OperationData model;

    /** Creates new form AttributePanel */
    public AttributePanel(OperationData od) {

        initComponents();
        initVariables(od);

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new JSeparator();
        operationIdLabel = new JLabel();

        preCondListPanel = new ConditionListPanel();
        postCondListPanel = new ConditionListPanel();
        jLabel1 = new JLabel("Preconditions");
        jLabel2 = new JLabel("Postconditions");
        jScrollPane1 = new JScrollPane(preCondListPanel);
        jScrollPane2 = new JScrollPane(postCondListPanel);

        jSeparator2 = new JSeparator();
        operationAttributeEditor = new OperationAttributeEditor();

        operationIdLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        operationIdLabel.setText("Operation ID: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addContainerGap(137, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE).addContainerGap()).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel2).addContainerGap(132, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE).addContainerGap()).addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE).addComponent(operationAttributeEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addGap(39, 39, 39).addComponent(operationIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(25, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(operationIdLabel).addGap(18, 18, 18).addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(operationAttributeEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(30, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Sets the ID label to display the id of the {@link OperationData} object.
     * @param id 
     */
    public void setID(int id) {
        this.operationIdLabel.setText("Operation ID: " + id);
    }

    /**
     * Sets the listobjects to display the conditions in the OperationData class.
     */
    public void setConditions() {
        if (model.getGlobalConditions() != null) {
            //Extract each set of condition sets
            for (Object viewKey : model.getGlobalConditions().keySet()) {
                Map<ConditionType, Condition> conditionMap = model.getGlobalConditions().get(viewKey);
                //Split conditions into post and pre
                for (Object key : conditionMap.keySet()) {
                    if (key == ConditionType.PRE && !preCondListPanel.contains(conditionMap.get(key))) {
                        preCondListPanel.addCondition(viewKey.toString(), conditionMap.get(key));
                    } else if (key == ConditionType.POST && !preCondListPanel.contains(conditionMap.get(key))) {
                        preCondListPanel.addCondition(viewKey.toString(), conditionMap.get(key));

                    } 
                }
            }
        } else {
            preCondListPanel.clear();
        }
    }

    private void initVariables(OperationData od) {
        this.model = od;
        setConditions();
        this.setName(od.getName());
        setID(model.getId());
    }

    /**
     * Adds a ActionListener to the savebutton
     * @param l the ActionListener
     */
    public void addEditorSaveListener(ActionListener l) {
        operationAttributeEditor.addSaveButtonListener(l);
    }
   
    /**
     * Method for getting the inner {@link OperationAttributeeEditor}
     * @return 
     */
    public OperationAttributeEditor getEditor() {
        return operationAttributeEditor;
    }

    /**
     * Sets model to a new OperationData and updates the conditions.
     * @param od 
     */
    public void updateModel(OperationData od) {
        this.model = od;
        setConditions();
    }
}
