package sequenceplanner.algorithms.visualization;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.ISopNodeToolbox;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;

/**
 * To manage the user interaction for {@link PerformVisualization}.<br/>
 * @author patrik
 */
public class UserInteractionForVisualization {

    private OperationView mOpView = null;
    private Model mModel = null;
    private ISopNode mSopNode = new SopNode();
    private IPerformVisualization mVisualization = new PerformVisualization("C:/Users/patrik/Desktop/beforeSynthesis.wmod");

    public UserInteractionForVisualization(OperationView mOpView, Model iModel) {
        this.mOpView = mOpView;
        this.mModel = iModel;
        new SelectOperationsDialog();
    }

    public boolean run(final ISopNode iAllOperaitons, final ISopNode iOperationsToView, final ISopNode iHasToFinish) {

        mVisualization.addOset(iAllOperaitons);

        if(!mVisualization.addOsubset(iOperationsToView)) {
            System.out.println("Operations to view are not a subset of all operations!");
            return false;
        }

        if(!mVisualization.addToOfinish(iHasToFinish)) {
            System.out.println("Operations to finish are not a subset of all operations!");
            return false;
        }

        RelationContainer rc = mVisualization.identifyRelations();
        if(rc != null) {
            return false;
        }

        mVisualization.hierarchicalPartition(rc);
        mVisualization.alternativePartition(rc);
        mVisualization.arbitraryOrderPartition(rc);
        mVisualization.parallelPartition(rc);
        mVisualization.sequenceing(rc);
        mVisualization.sopNodeToGraphicalView(mSopNode,mOpView);

        System.out.println("\n--------------------------------");
        System.out.println("After partition");
        System.out.println(rc.getOsubsetSopNode().toString());
        System.out.println("--------------------------------");

        return true;
    }

    public static Set<OperationData> getOperationsInModel(TreeNode iTree) {
        Set<OperationData> opDataSet = new HashSet<OperationData>();

        for (int i = 0; i < iTree.getChildCount(); ++i) {
            OperationData opData = (OperationData) iTree.getChildAt(i).getNodeData();
            opDataSet.add(opData);
        }
        return opDataSet;
    }

    private class SelectOperationsDialog extends JFrame implements ActionListener {

        JButton viewButton = new JButton("View!");
        JButton wmodButton = new JButton("Generate .wmod file");
        JButton sButton = new JButton("Select all");
        JButton dsButton = new JButton("Deselect all");
        JPanel buttonJp = new JPanel();
        JPanel jp = null;
        JRadioButton check1 = new JRadioButton("yes");
        JRadioButton check2 = new JRadioButton("yes");
        Map<JCheckBox, OperationData> checkBoxOpDataMap = new HashMap<JCheckBox, OperationData>();

        public SelectOperationsDialog() {
            setTitle("Product selection");
            Container c = getContentPane();
            c.setLayout(new GridLayout(5, 1));

            //Text1--------------------------------------------------------------
            c.add(new JLabel("Set output options:"));
            //-------------------------------------------------------------------

            //Boolean variables--------------------------------------------------
            jp = new JPanel();
            c.add(jp);
            jp.setLayout(new FlowLayout());

            jp.add(new JLabel("Single EFA:"));
            check1.setSelected(false);
            jp.add(check1);

            jp.add(new JLabel("Unique event names:"));
            check2.setSelected(true);
            jp.add(check2);
            //-------------------------------------------------------------------

            //Text2--------------------------------------------------------------
            c.add(new JLabel("Select products to include in supervisor:"));
            //-------------------------------------------------------------------

            //Operation selection--------------------------------------------------
            jp = new JPanel();
            c.add(jp);
            jp.setLayout(new FlowLayout());

            for (final OperationData opData : getOperationsInModel(mModel.getOperationRoot())) {
                final String name = opData.getName();
                JCheckBox rb = new JCheckBox(name);
                rb.addActionListener(this);
                rb.setSelected(true);
                jp.add(rb);
                checkBoxOpDataMap.put(rb, opData);
            }//------------------------------------------------------------------

            //Buttons------------------------------------------------------------
            c.add(buttonJp);

            //select all
            sButton.addActionListener(this);
            sButton.setEnabled(true);
            buttonJp.add(sButton);

            //deselect all
            dsButton.addActionListener(this);
            dsButton.setEnabled(true);
            buttonJp.add(dsButton);

            wmodButton.addActionListener(this);
            wmodButton.setEnabled(true);
            buttonJp.add(wmodButton);
            //-------------------------------------------------------------------

            setLocationRelativeTo(null);
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (wmodButton == e.getSource()) {
                for (JCheckBox jcb : checkBoxOpDataMap.keySet()) {
                    if (jcb.isSelected()) {
                        ISopNodeToolbox toolbox = new SopNodeToolboxSetOfOperations();
                        toolbox.createNode(checkBoxOpDataMap.get(jcb), mSopNode);
                    }
                }

                run(mSopNode, mSopNode, mSopNode);
                dispose();
            } else if (sButton == e.getSource()) {
                for (JCheckBox jcb : checkBoxOpDataMap.keySet()) {
                    jcb.setSelected(true);
                }
                viewButton.setEnabled(true);
                wmodButton.setEnabled(true);
            } else if (dsButton == e.getSource()) {
                for (JCheckBox jcb : checkBoxOpDataMap.keySet()) {
                    jcb.setSelected(false);
                }
                viewButton.setEnabled(false);
                wmodButton.setEnabled(false);
            } else {
                Boolean buttonOK = false;
                for (JCheckBox jcb : checkBoxOpDataMap.keySet()) {
                    if (jcb.isSelected()) {
                        buttonOK = true;
                        break;
                    }
                }
                if (buttonOK) {
                    viewButton.setEnabled(true);
                    wmodButton.setEnabled(true);
                } else {
                    viewButton.setEnabled(false);
                    wmodButton.setEnabled(false);
                }
            }
        }
    }
}
