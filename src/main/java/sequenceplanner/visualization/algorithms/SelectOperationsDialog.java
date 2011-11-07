package sequenceplanner.visualization.algorithms;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import javax.swing.JScrollPane;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.view.operationView.OperationView;

/**
 *
 * @author patrik
 */
public class SelectOperationsDialog extends JFrame implements ActionListener, IAlgorithmListener {

    JScrollPane mScrollPane;
    JButton generateButton = new JButton("Generate projection");
    JButton[] mSButtonArray = new JButton[3];
    JButton[] mDSButtonArray = new JButton[3];
    JPanel jpWest = null;
    JPanel jpEast = null;
    JPanel jpCond = null;
    JPanel jpStatus;
    JLabel jlStatus;
    JButton mStopButton;
    List<TreeNode> mOperationList = null;
    JCheckBox[][] mOpSelectionTable = null;
    Set<ConditionData> mConditionNameSet = null;
    Map<ConditionData, JCheckBox> mConditionSelectionMap = null;
    Model mModel;
    OperationView mOpView;
    private VisualizationAlgorithm mVisualizationAlgorithm;

    public SelectOperationsDialog(Model iModel, OperationView iOpView) {
        this.mModel = iModel;
        this.mOpView = iOpView;

        //Work with listeners------------------------------------------------
        generateButton.addActionListener(this);
        generateButton.setEnabled(false);

        //Collect operations and init variables
        mOperationList = mModel.getAllOperations();
        mOpSelectionTable = new JCheckBox[mOperationList.size()][3];
        jpWest = new JPanel(new GridLayout(1 + 2 + mOperationList.size(), 1));
        jpEast = new JPanel(new GridLayout(1 + 2 + mOperationList.size(), 3));


        //Collect conditions and init vairables
        mConditionNameSet = mModel.getAllConditions();
        mConditionSelectionMap = new HashMap<ConditionData, JCheckBox>();
        jpCond = new JPanel();
        jpCond.setLayout(new GridLayout(mConditionNameSet.size(), 2));

        //Conditions---------------------------------------------------------
        for (final ConditionData condition : mConditionNameSet) {
            jpCond.add(new JLabel(condition.getName()));
            JCheckBox cb = null;

            cb = new JCheckBox();
            cb.addActionListener(this);
            cb.setSelected(true);
            mConditionSelectionMap.put(condition, cb);
            jpCond.add(cb);
        }

        //Text---------------------------------------------------------------
        jpWest.add(new JLabel(""));
        JLabel jl = null;
        jl = new JLabel("To include");
        jl.setToolTipText("Select operations to include in calculations");
        jpEast.add(jl);
        jl = new JLabel("To finish");
        jl.setToolTipText("Select operations that has to finish");
        jpEast.add(jl);
        jl = new JLabel("To view");
        jl.setToolTipText("Select operations to view");
        jpEast.add(jl);
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

            jpWest.add(new JLabel(tnData.getNodeData().getName()));
            JCheckBox cb = null;

            cb = new JCheckBox();
            cb.addActionListener(this);
            cb.setSelected(true);
            mOpSelectionTable[operationIndex][0] = cb;
            jpEast.add(cb);

            cb = new JCheckBox();
            cb.addActionListener(this);
            cb.setSelected(false);
            mOpSelectionTable[operationIndex][1] = cb;
            jpEast.add(cb);

            cb = new JCheckBox();
            cb.addActionListener(this);
            cb.setSelected(false);
            mOpSelectionTable[operationIndex][2] = cb;
            jpEast.add(cb);
        }//------------------------------------------------------------------

        //Status JPanel----------------------------------------------------------
        jpStatus = new JPanel();
        jlStatus = new JLabel("Status: not started");
        mStopButton = new JButton("Stop execution");
        mStopButton.setEnabled(false);
        mStopButton.addActionListener(this);
        jpStatus.add(jlStatus);
        jpStatus.add(mStopButton);

        //Layout-------------------------------------------------------------
        setTitle("Operation selection");
        Container c = getContentPane();

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel jpButton = new JPanel(new GridLayout(1, 1));
        jpButton.add(generateButton);
        mainPanel.add(jpButton);
        mainPanel.add(jpStatus);
        mainPanel.add(jpCond);
        final JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(jpWest, BorderLayout.WEST);
        jp.add(jpEast, BorderLayout.EAST);
        mainPanel.add(jp);

        mScrollPane = new JScrollPane(mainPanel);
        c.add(mScrollPane);
        //-------------------------------------------------------------------

        setMinimumSize(new Dimension(300, 400));
        pack();
//        setLocationRelativeTo(null);
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
            final Set<ConditionData> conditionNameToIncludeSet = new HashSet<ConditionData>();
            for (final ConditionData condition : mConditionSelectionMap.keySet()) {
                if (mConditionSelectionMap.get(condition).isSelected()) {
                    conditionNameToIncludeSet.add(condition);
                }
            }
            
            Set<ResourceVariableData> resources = new HashSet<ResourceVariableData>();
            for (TreeNode n : mModel.getAllVariables()){
                if (n.getNodeData() instanceof ResourceVariableData){
                    resources.add((ResourceVariableData)n.getNodeData());
                }
            }
            
            //Call Visualization Algorithm---------------------------------------
            mVisualizationAlgorithm = new VisualizationAlgorithm("FromVisualizationDialog", this);

            //init
            final List<Object> list = new ArrayList<Object>();
            list.add(allOperationsNode);
            list.add(operationsToViewNode);
            list.add(hasToFinishNode);
            list.add(conditionNameToIncludeSet);
            list.add(resources);
            mVisualizationAlgorithm.init(list);

            //start
            mVisualizationAlgorithm.start();
            //-------------------------------------------------------------------
            mStopButton.setEnabled(true);

        } else if (mStopButton == e.getSource()) {

            mVisualizationAlgorithm.stop();
            newMessageFromAlgorithm("Stoped!", null);
            mStopButton.setEnabled(false);

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
        jpWest.add(new JLabel(iButtonText));
        for (int iLocal = 0; iLocal < 3; ++iLocal) {
            JButton button = null;
            button = new JButton("Do");
            button.setEnabled(true);
            button.addActionListener(this);
            button.setPreferredSize(new Dimension(2, 2));
            jpEast.add(button);
            iButtonArray[iLocal] = button;
        }
    }

    @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
        mStopButton.setEnabled(false);

        if (iList.get(0) instanceof ISopNode) {
            final ISopNode sopNode = (ISopNode) iList.get(0);

            newMessageFromAlgorithm("...drawing...", null);

            //Translate datastructure to graph in operation view.
            mOpView.drawGraph(sopNode);

            newMessageFromAlgorithm("...finished", null);
        }
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        jlStatus.setText("Status: " + iMessage);
        repaint();
    }
    
    // Should not be done here
    
}

