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
import javax.swing.JTextField;
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
    private JTextField operationIdTextField;
    private ConditionListPanel postCondListPanel;
    private ConditionListPanel preCondListPanel;
    /**OperationData object, acting as operationData for the view**/
    private OperationData operationData;

    /** Creates new form AttributePanel */
    public AttributePanel(OperationData od) {

        initComponents();
        initVariables(od);

    }

    private void initComponents() {

        jSeparator1 = new JSeparator();
        operationIdTextField = new JTextField();
        operationIdTextField.setActionCommand("set name");
        operationAttributeEditor = new OperationAttributeEditor();
        preCondListPanel = new ConditionListPanel(operationAttributeEditor);
        postCondListPanel = new ConditionListPanel(operationAttributeEditor);
        jLabel1 = new JLabel("Preconditions");
        jLabel2 = new JLabel("Postconditions");
        jScrollPane1 = new JScrollPane(preCondListPanel);
        jScrollPane2 = new JScrollPane(postCondListPanel);

        jSeparator2 = new JSeparator();
        

        operationIdTextField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        operationIdTextField.setText("Operation ID: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addContainerGap(137, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE).addContainerGap()).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel2).addContainerGap(132, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE).addContainerGap()).addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE).addComponent(operationAttributeEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addGap(39, 39, 39).addComponent(operationIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(25, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(operationIdTextField).addGap(18, 18, 18).addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(operationAttributeEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(30, Short.MAX_VALUE)));
    }

    /**
     * Sets the ID label to display the id of the {@link OperationData} object.
     * @param id 
     */
    public void setID(int id) {
        this.operationIdTextField.setText("Operation ID: " + id);
    }

    /**
     * Sets the listobjects to display the conditions in the OperationData class.
     */
    public void setConditions() {
        if (operationData.getGlobalConditions() != null) {
            //Extract each set of condition sets
            for (Object viewKey : operationData.getGlobalConditions().keySet()) {
                System.out.println("uno");
                Map<ConditionType, Condition> conditionMap = operationData.getGlobalConditions().get(viewKey);
                //Split conditions into post and pre
                for (Object key : conditionMap.keySet()) {
                    System.out.println("due");

                    if (key == ConditionType.PRE && !preCondListPanel.contains(conditionMap.get(key))) {
                        System.out.println("pre");
                        preCondListPanel.addCondition(viewKey.toString(), conditionMap.get(key));

                        System.out.println("pre");
                    } else if (key == ConditionType.POST && !preCondListPanel.contains(conditionMap.get(key))) {
                        System.out.println("post");

                        preCondListPanel.addCondition(viewKey.toString(), conditionMap.get(key));

                    } 
                }
            }
        } else {
            System.out.println("Clear");
            preCondListPanel.clear();
        }
    }

    private void initVariables(OperationData od) {
        this.operationData = od;
        setConditions();
        this.setName(od.getName());
        setID(operationData.getId());
    }

    /**
     * Adds a ActionListener to the savebutton
     * @param l the ActionListener
     */
    public void addEditorSaveListener(ActionListener l) {
        operationAttributeEditor.addSaveButtonListener(l);
    }
    
    /**
     * Adds an ActionListener to the OperationIdTextField
     * @param l the ActionListener
     */
    public void addOperationIdTextFieldListener(ActionListener l){
        operationIdTextField.addActionListener(l);
    }
   
    /**
     * Method for getting the inner {@link OperationAttributeeEditor}
     * @return 
     */
    public OperationAttributeEditor getEditor() {
        return operationAttributeEditor;
    }

    /**
     * Sets operationData to a new OperationData and updates the conditions.
     * @param od 
     */
    public OperationData updateModel(OperationData od) {
        this.operationData = od;
        setConditions();
        return this.operationData;
    }

    public JTextField getOperationIdTextField() {
        return operationIdTextField;
    }
}
