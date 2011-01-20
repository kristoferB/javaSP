package sequenceplanner.multiProduct;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Store errors
 * @author patrik
 */
public class Error {

    private ArrayList<String> errorMsgs;
    private String title;

    public Error() {
        this("Title is not set");
    }

    public Error(String title) {
        this.title = title;
        errorMsgs = new ArrayList<String>();
    }

    public void error(String error) {
        System.err.println(error);
        errorMsgs.add(error);
    }

    public void error(String x, String y) {
        error(x + " lacks " + y);
    }

    public void printErrorList() {
        if (!errorMsgs.isEmpty()) {
            String error = "Look through the following errors: \n";
            for (String s : errorMsgs) {
                error = error + "\n" + s;
            }
            new ErrorDialog();
            //JOptionPane.showMessageDialog(null, error, "ErrorList", 1);
        }
    }

    public void resetErrorList() {
        errorMsgs.clear();
    }

    public boolean noErrors() {
        if (errorMsgs.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Dialog to show errors
     */
    public class ErrorDialog extends JFrame implements ActionListener {

        JButton closeButton = null;

        public ErrorDialog() {
            Dialog();
        }

        private void Dialog() {
            Container c = getContentPane();
            setTitle(title + " | To look up:");
            closeButton = new JButton("Close");
            closeButton.addActionListener(this);

            c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
            setLocationRelativeTo(null);
            setAlwaysOnTop(true);

            c.add(closeButton);
  
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            c.add(panel);
            for (String s : errorMsgs) {
                panel.add(new JLabel(s));
            }
            
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (closeButton == e.getSource()) {
                dispose();
            }
        }
    }
}
