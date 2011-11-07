/*
* OperationAttributeEditor.java
*
* Created on 2011-jun-21, 11:27:20
*/
package sequenceplanner.gui.view.attributepanel;

import java.awt.event.ActionListener;
import sequenceplanner.model.data.ConditionData;

/**
* Small editor class. Used to edit or add conditions to an OperationData object.
* @author Qw4z1
*/
public class OperationAttributeEditor extends javax.swing.JPanel {

    /**
     * Fix to save condition name during edit by user.<br/>
     * If mConditionData.getName is !equal to "" then is this condition saved with that name.<br/>
     * If mConditionData.getName is eqial to "" is this condition saved with default name "Algebraic"
     */
    public ConditionData mConditionData;

    /** Creates new form OperationAttributeEditor */
    public OperationAttributeEditor() {
        initComponents();
    }

    /** This method is called from within the constructor to
* initialize the form.
* WARNING: Do NOT modify this code. The content of this method is
* always regenerated by the Form Editor.
*/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        conditionButtonGroup = new javax.swing.ButtonGroup();
        actionGuardButtonGroup = new javax.swing.ButtonGroup();
        conditionTypeTextField = new javax.swing.JTextField();
        expresionTextField = new javax.swing.JTextField();
        preRadioButton = new javax.swing.JRadioButton();
        postRadioButton = new javax.swing.JRadioButton();
        guardRadioButton = new javax.swing.JRadioButton();
        actionRadioButton = new javax.swing.JRadioButton();
        saveButton = new javax.swing.JButton();

        conditionButtonGroup.add(preRadioButton);
        conditionButtonGroup.add(postRadioButton);

        actionGuardButtonGroup.add(guardRadioButton);
        actionGuardButtonGroup.add(actionRadioButton);
        
        conditionTypeTextField.setText("Enter condition type");
        conditionTypeTextField.setToolTipText("Enter condition type");
        conditionTypeTextField.setActionCommand("save");

        expresionTextField.setText("Enter SP condtition");
        expresionTextField.setToolTipText("Enter SP condition");
        expresionTextField.setActionCommand("save");

        preRadioButton.setSelected(true);
        preRadioButton.setText("Pre");

        postRadioButton.setText("Post");

        guardRadioButton.setSelected(true);
        guardRadioButton.setText("Guard");

        actionRadioButton.setText("Action");

        saveButton.setText("Save");
        saveButton.setToolTipText("Save condition");
        saveButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        saveButton.setActionCommand("save");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(conditionTypeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))                   
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(expresionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preRadioButton)
                            .addComponent(guardRadioButton))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(actionRadioButton)
                            .addComponent(postRadioButton))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(conditionTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expresionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(preRadioButton)
                    .addComponent(postRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guardRadioButton)
                    .addComponent(actionRadioButton))
                .addContainerGap(53, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private javax.swing.ButtonGroup actionGuardButtonGroup;
    private javax.swing.JRadioButton actionRadioButton;
    private javax.swing.ButtonGroup conditionButtonGroup;
    private javax.swing.JTextField conditionTypeTextField;
    private javax.swing.JTextField expresionTextField;
    private javax.swing.JRadioButton guardRadioButton;
    private javax.swing.JRadioButton postRadioButton;
    private javax.swing.JRadioButton preRadioButton;
    private javax.swing.JButton saveButton;

    public void addSaveButtonListener(ActionListener l){
        saveButton.addActionListener(l);
        expresionTextField.addActionListener(l);
        conditionTypeTextField.addActionListener(l);
    }

    /**
* Clears the textfield and returns a String with the text
* @return String text
*/
    public String getConditionString(){
        return expresionTextField.getText();
    }
    
    public String getConditionTypeString(){
        return conditionTypeTextField.getText();
    }

    public void opendToEdit(Object source) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    

    /**
* Sets the text in the textfield to the String specified in the argument
* @param conditionString the input String
*/
    public void setConditionString(String conditionString){
        expresionTextField.setText(conditionString);
    }
    
    public void setConditionTypeString(String conditionTypeString){
        conditionTypeTextField.setText(conditionTypeString);
    }

    public boolean getPreButtonStatus (){
        return preRadioButton.isSelected();
    }

    public boolean getGuardButtonStatus (){
        return guardRadioButton.isSelected();
    }
    public void setGuardButtonStatus(boolean on){
        guardRadioButton.setSelected(on);
    }
    public void setActionButtonStatus(boolean on){
        actionRadioButton.setSelected(on);
    }

    public void setPreButtonStatus(boolean on){
        preRadioButton.setSelected(on);
    }
    public void setPostButtonStatus(boolean on){
        postRadioButton.setSelected(on);
    }
    public void clearTextField() {
        expresionTextField.setText("");
    }
    
     public void clearConditionTypeField() {
        conditionTypeTextField.setText("");
    }
}