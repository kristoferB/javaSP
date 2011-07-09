package sequenceplanner.gui.view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
*
* @author Peter
*/
public class HelpPanes extends JFrame{

    public HelpPanes(String window){

        JTextArea text;
        JLabel lb;
        if(window.equals("Short Commands")){
            ShortCommands sc = new ShortCommands();
            lb = new JLabel("Short Commands",JLabel.CENTER);
            text = new JTextArea(sc.getString(), 25,40);


        }
        else{ //if(window.equals("About")){
            About sc = new About();
            lb = new JLabel("About",JLabel.CENTER);
            text = new JTextArea(sc.getString(), 25,40);
        }
            text.setBackground(Color.white);
            text.setEditable(false);
            lb.setFont(new Font("SansSerif", Font.BOLD,18));
            JPanel pan = new JPanel();
            pan.setSize(100, 400);

            JPanel pan2 = new JPanel();
            pan2.setSize(500,400);

            pan2.add(text);
            pan.add(lb);

            setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
            add(pan);
            add(pan2);


            setSize(500,500);
            setVisible(true);
    }
}