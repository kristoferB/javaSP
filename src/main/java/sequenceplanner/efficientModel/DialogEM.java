/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import java.awt.BorderLayout;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.supremica.apps.Supremica;

/**
 *
 * @author shoaei
 */
public class DialogEM{

    public DialogEM(){
    }

    public void createAndShow(){
        JFrame frame = new JFrame("Efficient Model");
        frame.setResizable(false);
        Container content = frame.getContentPane();

        JPanel jpanel = new JPanel(new GridLayout(3, 1));

        String[] options = {"Option 1","Option 2","Option 3","Option 3","Option 3","Option 3","Option 3"};
        jpanel.add(createOptions("options", options));
        content.add(jpanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel createOptions(String title, String[] options) {
        JPanel opanel = new JPanel(new GridLayout(1, 1));
        opanel.setBorder(BorderFactory.createTitledBorder(title));
        JCheckBox checkbox;
        for(int i=0; i<options.length; i++){
            checkbox = new JCheckBox(options[i], false);
            opanel.add(checkbox);
        }
        return opanel;
    }

}
