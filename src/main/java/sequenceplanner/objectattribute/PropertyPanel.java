package sequenceplanner.objectattribute;

import javax.swing.JLabel;
import javax.swing.JPanel;
import sequenceplanner.model.data.OperationData;

/**
 * JPanel showing the properties of selected Operation.
 * @author QW4z1
 */
public class PropertyPanel extends JPanel{
    private OperationData data;
    private String precondString;
    private String idString;
    private JLabel operationIdLabel = new JLabel("Operation ID : %i");
    private JLabel preconditionLabel = new JLabel("Preconditions : /n");
    private JLabel preactionLabel = new JLabel("Preactions :/n");
    private JLabel postconditionLabel = new JLabel("Postcondition :/n");
    private JLabel postactionLabel = new JLabel("Postaction : /n");
    private JLabel propertyLabel = new JLabel("<html><u>Properties :</html></u>/n");
    
    
    
    public PropertyPanel(OperationData data){
        this.data = data;
        setIdString(data.getId());
        add(operationIdLabel);
        add(preconditionLabel);
        add(preactionLabel);
        add(postconditionLabel);
        add(postactionLabel);
        add(propertyLabel);
        operationIdLabel.setOpaque(true);
        propertyLabel.setOpaque(true);
        postconditionLabel.setOpaque(true);
        postactionLabel.setOpaque(true);
        propertyLabel.setOpaque(true);
        
    }
    
    private void setIdString(int id){
        idString = "Operation ID : " + id;
    }
    
    
}
