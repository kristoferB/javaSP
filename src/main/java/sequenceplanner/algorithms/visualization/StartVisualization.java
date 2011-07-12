package sequenceplanner.algorithms.visualization;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;

/**
 * To manage the user interaction for {@link PerformVisualization}.<br/>
 * @author patrik
 */
public class StartVisualization {

    private OperationView mOpView = null;
    private Model mModel = null;
    private IPerformVisualization mVisualization = null;

    /**
     * A dialog is started where user selects operation to visualize.
     * @param mOpView
     */
    public StartVisualization(OperationView mOpView) {
        init(mOpView);
        new SelectOperationsDialog();
    }

    /**
     * Start visualization based on parameters
     * @param iOpView
     * @param iAllOperaitons
     * @param iOperationsToView
     */
    public StartVisualization(final OperationView iOpView, final ISopNode iAllOperaitons, final ISopNode iOperationsToView) {
        init(iOpView);
        run(iAllOperaitons, iOperationsToView, new SopNode());
    }

    private void init(final OperationView iOpView) {
        this.mOpView = iOpView;
        this.mModel = iOpView.getModel();
    }

    public boolean run(final ISopNode iAllOperaitons, final ISopNode iOperationsToView, final ISopNode iHasToFinish) {
        return run(iAllOperaitons, iOperationsToView, iHasToFinish, null);
    }

    public boolean run(final ISopNode iAllOperaitons, final ISopNode iOperationsToView, final ISopNode iHasToFinish, final Set<String> iConditionsToInclude) {

        mVisualization = new PerformVisualization("C:/Users/patrik/Desktop/beforeSynthesis.wmod", iConditionsToInclude);

        mVisualization.addOset(iAllOperaitons);

        if (!mVisualization.addOsubset(iOperationsToView)) {
            System.out.println("Operations to view are not a subset of all operations!");
            return false;
        }

        if (!mVisualization.addToOfinish(iHasToFinish)) {
            System.out.println("Operations to finish are not a subset of all operations!");
            return false;
        }

        final RelationContainer rc = mVisualization.identifyRelations();
        if (rc == null) {
            return false;
        }

        mVisualization.hierarchicalPartition(rc);
        mVisualization.alternativePartition(rc);
        mVisualization.arbitraryOrderPartition(rc);
        mVisualization.parallelPartition(rc);
        mVisualization.sequenceing(rc);

        System.out.println("\n--------------------------------");
        System.out.println("After partition");
        System.out.println(rc.getOsubsetSopNode().toString());
        System.out.println("--------------------------------");

        mVisualization.sopNodeToGraphicalView(rc.getOsubsetSopNode(), mOpView);

//        System.out.println("\n--------------------------------");
//        System.out.println("Get conditions");
//        new SopNodeToolboxSetOfOperations().relationsToSelfContainedOperations(rc.getOsubsetSopNode());
//        System.out.println("--------------------------------");

        return true;
    }

    public static List<OperationData> getOperationsInModel(TreeNode iTree) {
        List<OperationData> opDataList = new LinkedList<OperationData>();

        for (int i = 0; i < iTree.getChildCount(); ++i) {
            OperationData opData = (OperationData) iTree.getChildAt(i).getNodeData();
            opDataList.add(opData);
        }
        return opDataList;
    }

    private class SelectOperationsDialog extends JFrame implements ActionListener {

        JButton generateButton = new JButton("Generate projection");
        JButton[] mSButtonArray = new JButton[3];
        JButton[] mDSButtonArray = new JButton[3];
        JPanel jp = null;
        JPanel jpCond = null;
        List<TreeNode> mOperationList = null;
        JCheckBox[][] mOpSelectionTable = null;
        Set<String> mConditionNameSet = null;
        Map<String,JCheckBox> mConditionSelectionMap = null;

        public SelectOperationsDialog() {
            //Work with listeners------------------------------------------------
            generateButton.addActionListener(this);
            generateButton.setEnabled(false);

            //Collect operations and init variables
            mOperationList = mModel.getAllOperations();
            mOpSelectionTable = new JCheckBox[mOperationList.size()][3];
            jp = new JPanel();
            jp.setLayout(new GridLayout(1 + 2 + mOperationList.size(), 4));

            //Collect conditions and init vairables
            mConditionNameSet = getConditionNames();
            mConditionSelectionMap = new HashMap<String, JCheckBox>();
            jpCond = new JPanel();
            jpCond.setLayout(new GridLayout(mConditionNameSet.size(), 2));

            //Conditions---------------------------------------------------------
            
            
            for(final String condition : mConditionNameSet) {
                jpCond.add(new JLabel(condition));
                JCheckBox cb = null;

                cb = new JCheckBox();
                cb.addActionListener(this);
                cb.setSelected(true);
                mConditionSelectionMap.put(condition, cb);
                jpCond.add(cb);
            }

            //Text---------------------------------------------------------------
            jp.add(new JLabel(""));
            JLabel jl = null;
            jl = new JLabel("To include");
            jl.setToolTipText("Select operations to include in calculations");
            jp.add(jl);
            jl = new JLabel("To finish");
            jl.setToolTipText("Select operations that has to finish");
            jp.add(jl);
            jl = new JLabel("To view");
            jl.setToolTipText("Select operations to view");
            jp.add(jl);
            //-------------------------------------------------------------------

            //Select and DeSelect------------------------------------------------
            addButtons("Select all", mSButtonArray);
            addButtons("Deselect all", mDSButtonArray);
            //-------------------------------------------------------------------

            //Add operations to JPanel-------------------------------------------
            final Iterator<TreeNode> listIterator = mOperationList.iterator();
            while (listIterator.hasNext()) {
                final TreeNode tnData = listIterator.next();
                final int operationIndex = mOperationList.indexOf(tnData);

                jp.add(new JLabel(tnData.getNodeData().getName()));
                JCheckBox cb = null;

                cb = new JCheckBox();
                cb.addActionListener(this);
                cb.setSelected(true);
                mOpSelectionTable[operationIndex][0] = cb;
                jp.add(cb);

                cb = new JCheckBox();
                cb.addActionListener(this);
                cb.setSelected(false);
                mOpSelectionTable[operationIndex][1] = cb;
                jp.add(cb);

                cb = new JCheckBox();
                cb.addActionListener(this);
                cb.setSelected(false);
                mOpSelectionTable[operationIndex][2] = cb;
                jp.add(cb);
            }//------------------------------------------------------------------

            //Layout-------------------------------------------------------------
            setTitle("Operation selection");
            Container c = getContentPane();

            c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
            JPanel jpButton = new JPanel(new GridLayout(1, 1));
            jpButton.add(generateButton);
            c.add(jpButton);
            c.add(jpCond);
            c.add(jp);
            //-------------------------------------------------------------------

            setLocationRelativeTo(null);
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (generateButton == e.getSource()) {
                final ISopNode allOperationsNode = new SopNode();
                final ISopNode hasToFinishNode = new SopNode();
                final ISopNode operationsToViewNode = new SopNode();

                //Operations
                final Iterator<TreeNode> listIterator = mOperationList.iterator();
                while (listIterator.hasNext()) {
                    final TreeNode tnData = listIterator.next();
                    final int operationIndex = mOperationList.indexOf(tnData);
                    final OperationData opData = (OperationData) tnData.getNodeData();

                    final ISopNode opNode = new SopNodeOperation(opData);
                    if (mOpSelectionTable[operationIndex][0].isSelected()) {
                        allOperationsNode.addNodeToSequenceSet(opNode);
                        if (mOpSelectionTable[operationIndex][1].isSelected()) {
                            hasToFinishNode.addNodeToSequenceSet(opNode);
                        }
                        if (mOpSelectionTable[operationIndex][2].isSelected()) {
                            operationsToViewNode.addNodeToSequenceSet(opNode);
                        }
                    }
                }

                //Conditions
                final Set<String> conditionNameToIncludeSet = new HashSet<String>();
                for(final String condition : mConditionSelectionMap.keySet()) {
                    if(mConditionSelectionMap.get(condition).isSelected()) {
                        conditionNameToIncludeSet.add(condition);
                    }
                }
                run(allOperationsNode, operationsToViewNode, hasToFinishNode, conditionNameToIncludeSet);

            } else if (mSButtonArray[0] == e.getSource()) {
                changeCheckBoxColumn(0, true);
            } else if (mSButtonArray[1] == e.getSource()) {
                changeCheckBoxColumn(1, true);
            } else if (mSButtonArray[2] == e.getSource()) {
                changeCheckBoxColumn(2, true);
            } else if (mDSButtonArray[0] == e.getSource()) {
                changeCheckBoxColumn(0, false);
            } else if (mDSButtonArray[1] == e.getSource()) {
                changeCheckBoxColumn(1, false);
            } else if (mDSButtonArray[2] == e.getSource()) {
                changeCheckBoxColumn(2, false);
            }
            //Only be possible to press generate button if there is something to work with
            final Iterator<TreeNode> listIterator = mOperationList.iterator();
            while (listIterator.hasNext()) {
                final TreeNode tnData = listIterator.next();
                final int operationIndex = mOperationList.indexOf(tnData);
                if (mOpSelectionTable[operationIndex][0].isSelected() &&
                        mOpSelectionTable[operationIndex][2].isSelected()) {
                    generateButton.setEnabled(true);
                    return;
                }
            }
            generateButton.setEnabled(false);
        }

        private void changeCheckBoxColumn(final int iColumnIndex, final boolean iBoolean) {
            final Iterator<TreeNode> listIterator = mOperationList.iterator();
            while (listIterator.hasNext()) {
                final TreeNode tnData = listIterator.next();
                final int operationIndex = mOperationList.indexOf(tnData);
                mOpSelectionTable[operationIndex][iColumnIndex].setSelected(iBoolean);
            }
        }

        private void addButtons(final String iButtonText, final JButton[] iButtonArray) {
            jp.add(new JLabel(iButtonText));
            for (int iLocal = 0; iLocal < 3; ++iLocal) {
                JButton button = null;
                button = new JButton("Do");
                button.setEnabled(true);
                button.addActionListener(this);
                button.setPreferredSize(new Dimension(2, 2));
                jp.add(button);
                iButtonArray[iLocal] = button;
            }
        }

        private Set<String> getConditionNames() {
            final Set<String> returnSet = new HashSet<String>();
            final List<TreeNode> operationList = mModel.getAllOperations();

            for(final TreeNode tn : operationList) {
                if(Model.isOperation(tn.getNodeData())) {
                    final OperationData opData = (OperationData) tn.getNodeData();
                    final Set<String> conditionNameSet = opData.getGlobalConditions().keySet();
                    for(final String cond : conditionNameSet) {
                        returnSet.add(cond);
                    }
                }
            }

            return returnSet;
        }
    }
}
