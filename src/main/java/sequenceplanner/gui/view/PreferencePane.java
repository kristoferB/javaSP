package sequenceplanner.gui.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Erik
 */
public class PreferencePane extends JFrame {

   JTabbedPane mainPain;

   public PreferencePane() {
//      JScrollPane p = new JScrollPane(this);
//      p.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      mainPain = new JTabbedPane();
      mainPain.add("Auto arrange", autoArrange());
      getContentPane().add(mainPain);
      this.setSize(300, 200);
      
   }

   private JPanel autoArrange() {
      JPanel panel = new JPanel();
      panel.add(new JLabel("Insert what to change"));
      
      return panel;
   }
}

//import javax.swing.*;
//
//public class Gujd {
//
//   public static void main(String[] args) {
//
//      JTextField name = new JTextField();
//      JTextField initials = new JTextField(6);
//      JTextField age = new JTextField(3);
//      JTextArea interests = new JTextArea(10,10);
//      interests.setLineWrap(true);
//      interests.setWrapStyleWord(true);
//
//      JScrollPane jsp = new JScrollPane(
//         interests,
//         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
//      );
//
//      String s0 = "Please enter your last name";
//      String s1 = "Please enter your initials";
//      String s2 = "Please enter your age\n(in years)";
//      String s3 = "Please enter your main interests\n(maximum 500 words)";
//
//      JOptionPane.showOptionDialog(
//         null,
//         new Object[] {s0,name,s1,initials,s2,age,s3,jsp},
//         "Personal Info",
//         JOptionPane.OK_OPTION,
//         JOptionPane.INFORMATION_MESSAGE,
//         null,
//         null,
//         null
//      );
//
//      System.out.println("name = " + name.getText());
//      System.out.println("initials = " + initials.getText());
//      System.out.println("age = " + age.getText());
//      System.out.println("interests = " + interests.getText());
//
//   }
//
//}
