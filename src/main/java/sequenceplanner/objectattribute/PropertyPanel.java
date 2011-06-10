package sequenceplanner.objectattribute;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JPanel showing the properties of selected Operation.
 * @author QW4z1
 */
public class PropertyPanel extends JPanel{
    private String precondString;
    private JLabel operationIdLabel = new JLabel("testid /n");
    private JLabel preconditionLabel = new JLabel("Preconditions : /n");
    private JLabel preactionLabel = new JLabel("Preactions :/n");
    private JLabel postconditionLabel = new JLabel("Postcondition :/n");
    private JLabel postactionLabel = new JLabel("Postaction : /n");
    private JLabel propertyLabel = new JLabel("<html><u>Properties :</html></u>/n");
    
    
    
    public PropertyPanel(){
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
    
}
