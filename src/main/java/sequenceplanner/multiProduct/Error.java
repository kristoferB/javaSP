package sequenceplanner.multiProduct;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Store errors
 * @author patrik
 */
public class Error {
    private ArrayList<String> errorMsgs;

    public Error() {
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
            for (String s:errorMsgs) {
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
        if(errorMsgs.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * Dialog to show errors
     */
    public class ErrorDialog implements ActionListener {

        JFrame mainFrame = null;
        JButton closeButton = null;

        public ErrorDialog() {
            Dialog();
        }

        private void Dialog() {
            closeButton = new JButton("Close");
            closeButton.addActionListener(this);

            mainFrame = new JFrame("To look up:");
            mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setAlwaysOnTop(true);
            for(String s: errorMsgs) {
                mainFrame.getContentPane().add(new JLabel(s));
            }
            mainFrame.getContentPane().add(closeButton);
            mainFrame.pack();
            mainFrame.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (closeButton == e.getSource()) {
                mainFrame.dispose();
            }
        }
    }
}
