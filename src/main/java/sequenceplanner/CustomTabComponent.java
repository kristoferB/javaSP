//package sequenceplanner;

/**
 * Depricated...
 */




//
//import java.awt.Component;
//import java.awt.FlowLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//
//import javax.swing.BorderFactory;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//
//
//public class CustomTabComponent extends JPanel {
//
//   final JLabel lab;
//
//
//   public CustomTabComponent(final SPContainer cont, final Component view) {
//      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//
//      lab = new JLabel(view.getName());
//
//      view.addPropertyChangeListener("name", new PropertyChangeListener() {
//
//         public void propertyChange(PropertyChangeEvent evt) {
//            lab.setText((String) evt.getNewValue());
//         }
//      });
//
//      add(lab);
//      lab.setOpaque(false);
//      this.setOpaque(false);
//
//      lab.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
//
//      JButton button = new JButton();
//
//      button.setOpaque(false);
//      ImageIcon iico = new ImageIcon(SequencePlanner.class.getResource("resources/icons/close.png") );
//      button.setIcon(iico);
//      button.setMargin(new Insets(0,0,0,0) );
//      button.setRolloverEnabled(false);
//      add(button);
//
//      button.addActionListener(new ActionListener() {
//
//         @Override
//         public void actionPerformed(ActionEvent e) {
//            //cont.close(view);
//         }
//      });
//
//      setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//
//   }
//}
