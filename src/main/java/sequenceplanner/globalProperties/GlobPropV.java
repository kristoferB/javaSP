/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.globalProperties;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Evelina
 */
public class GlobPropV extends JFrame{
    
    private GlobPropM model;

    private JTextField propertyInput = new JTextField(20);
    private JTextField valueInput = new JTextField(20);
    private JButton    addProperty = new JButton("Add");

    GlobPropV(GlobPropM m){
        model = m;

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Property"));
        content.add(propertyInput);
        content.add(new JLabel("Value"));
        content.add(valueInput);
        content.add(addProperty);

        //... finalize layout
        this.setContentPane(content);
        this.pack();

        this.setTitle("Global Properties - test");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public String getPropertyInput() {
        return propertyInput.getText();
    }

    public String getValueInput() {
        return valueInput.getText();
    }

    void addAddPropertyListener(ActionListener add) {
        addProperty.addActionListener(add);
    }


    //Skapa dummy interface f�r att l�gga till nya properties (Editor)
    //Skapa dummy interface f�r att s�tta property f�r en operation (Attribute)

}
