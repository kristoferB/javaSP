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
    private String name;
    private String idString;
    private JLabel operationIdLabel = new JLabel("Operation ID : ");
    private JLabel preconditionLabel = new JLabel("Preconditions : /n");
    private JLabel preactionLabel = new JLabel("Preactions :/n");
    private JLabel postconditionLabel = new JLabel("Postcondition :/n");
    private JLabel postactionLabel = new JLabel("Postaction : /n");
    private JLabel propertyLabel = new JLabel("<html><u>Properties :</html></u>/n");
    
    
    
    public PropertyPanel(OperationData data){
        this.data = data;
        name = data.getName();
        setIdString(data.getName());
        operationIdLabel.setText(idString);
        add(operationIdLabel);
        operationIdLabel.setOpaque(true);
        propertyLabel.setOpaque(true);
        postconditionLabel.setOpaque(true);
        postactionLabel.setOpaque(true);
        propertyLabel.setOpaque(true);
        
    }
    
    private void setIdString(String id){
        idString = "Operation ID : " + id;
    }

    public String getIdString(){
        return idString;
    }
    public String getName() {
        return name;
    }
    
    
}
