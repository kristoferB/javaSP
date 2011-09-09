package sequenceplanner.multiProduct;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class Test_Thread {

    SP mSP = new SP();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void test1() {

//        new Algorithm("PM");


    }

    abstract class Algorithm extends JFrame implements ActionListener, Runnable {

        private final Thread mWorkThread;
        private final JButton mInterruptButton;
        private final MsgArea mMessageArea;
        private boolean dispose = false;

        public Algorithm(final String iTitle) {

            //FRAME layout-------------------------------------------------------
            setTitle(iTitle);

            //Interrupt button
            mInterruptButton = new JButton("Break!");
            mInterruptButton.addActionListener(this);

            //Message area
            mMessageArea = new MsgArea();

            //Layout
            Container c = getContentPane();

            c.setLayout(new BorderLayout());
            c.add(mMessageArea, BorderLayout.NORTH);
            c.add(mInterruptButton, BorderLayout.SOUTH);

            setLocationRelativeTo(null);
            pack();
            setVisible(true);

            //THREAD-------------------------------------------------------------
            mWorkThread = new Thread(this);
            mWorkThread.start();

            try {
                Thread.sleep(2000);
                mMessageArea.addText("PM");
                Thread.sleep(2000);
                mMessageArea.addText("SS");
                Thread.sleep(12000);
            } catch (InterruptedException ie) {
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mInterruptButton == e.getSource()) {
                if (!dispose) {
                    mWorkThread.interrupt();
                    mMessageArea.endText("BREAK");
                    mInterruptButton.setText("Exit");
                    dispose = true;
                } else {
                    dispose();
                }
            }

        }

        abstract void localRun();

        @Override
        public void run() {
            
                localRun();
                dispose();

        }
    }

    private class MsgArea extends TextArea implements Runnable {

        protected Thread mMsgThread;
        private final String mDots = "....";

        public MsgArea() {

            setRows(10);
            setColumns(8);
            setEditable(false);

            mMsgThread = new Thread(this);
            mMsgThread.start();
        }

        public void addText(final String iText) {
            endText(iText);
            mMsgThread = new Thread(this);
            mMsgThread.start();
            append("\n");
        }

        public void endText(final String iText) {
            mMsgThread.interrupt();
            append("\n" + iText);
        }

        @Override
        public void run() {

            append(mDots + "\\");

            boolean run = true;
            while (run) {
                try {
                    Thread.sleep(200);
                    if (!run) {
                        break;
                    }
                    updateDummy("-");
                    Thread.sleep(200);
                    if (!run) {
                        break;
                    }
                    updateDummy("/");
                    Thread.sleep(200);
                    if (!run) {
                        break;
                    }
                    updateDummy("\\");
                } catch (InterruptedException ie) {
                    run = false;
                }
            }
        }

        private void updateDummy(final String iChar) {
            setText(getText().substring(0, getText().length() - mDots.length() - 1) + mDots + iChar);
        }
    }
}
