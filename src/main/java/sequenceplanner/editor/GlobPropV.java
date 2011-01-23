/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Evelina
 */
public class GlobPropV extends JPanel{
    
    private GlobPropM model;
    private JTree tree;

    private JTextField propertyInput = new JTextField(20);
    private JTextField valueInput = new JTextField(20);
    private JButton    addProperty = new JButton("Add");

    GlobPropV(GlobPropM m){
        model = m;
/* test input
        setLayout(new FlowLayout());
        add(new JLabel("Property"));
        add(propertyInput);
        add(new JLabel("Value"));
        add(valueInput);
        add(addProperty);
 *
 */
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Global properties");

        tree = new JTree(top);

        add(tree);
    }

    public void createNodes(DefaultMutableTreeNode node){

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
