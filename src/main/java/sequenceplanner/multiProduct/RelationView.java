package sequenceplanner.multiProduct;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;
import sequenceplanner.view.operationView.graphextension.SPGraph;

/**
 * An attempt to get a better visualization of relations between operations. <br/>
 * The implementation only supports a very limited types of relations. <br/>
 * The supported types are tested. No visualization is performed if relations are to complex.
 * @author patrik
 */
public class RelationView {

    private Model model = null;
    private OperationView ov = null;
    private InternalOpDatas allOperations = new InternalOpDatas();
    private InternalOpDatas selectedOps = new InternalOpDatas();
    private HashMap<String, String> eventNameExtractedGuardMap = new HashMap<String, String>();
    final public static String UP = ":up";
    final public static String DOWN = ":down";

    public RelationView(Model model, OperationView ov) {
        this.model = model;
        this.ov = ov;
        init();

        if (testIDs()) {
            new SelectOperationsDialog();
        } else {
            JOptionPane.showMessageDialog(null, "I can't handle IDs that are prefix or suffix of each other, e.g. 18 and 118");
        }
    }

    private void init() {
        TreeNode treeNode = model.getOperationRoot();
        for (int i = 0; i < treeNode.getChildCount(); ++i) {
            allOperations.add(new InternalOpData((OperationData) treeNode.getChildAt(i).getNodeData()));
        }
    }

    /**
     * Method in this class can't handle IDs that are suffix or prefix to each other, e.g. 18 and 118
     * @return true if IDs are ok else false
     */
    private boolean testIDs() {
        String test = "";
        for (Integer id : allOperations.opIDs()) {
            if (test.contains(id.toString())) {
                return false;
            } else {
                test = test + id.toString() + TypeVar.SEPARATION;
            }
        }
        return true;
    }

    /**
     * Tests if postconditions are empty for selected operations <br/>
     * and maximum one guard. The guard should be of type <i>id_f</i>
     * @param ops operations to test
     * @return true if operations are ok other false
     */
    public static boolean allOperationsAreOK(InternalOpDatas ops) {
        for (InternalOpData opData : ops) {
            if (opData.getRawPrecondition().contains(TypeVar.SP_AND) || opData.getRawPrecondition().contains(TypeVar.SP_OR)) {
                return false;
            }
            if (opData.getRawPostcondition().length() > 1 || opData.getRawPostcondition().length() > 1) {
                return false;
            }
            if (opData.getRawPrecondition().length() > 1) {
                if (opData.getRawPrecondition().contains(TypeVar.SP_INITIAL) || opData.getRawPrecondition().contains(TypeVar.SP_EXECUTE)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void selectFile() {
        Boolean okToGoOn = true;
        JFileChooser fc = new JFileChooser("user.dir");
        //fc.setFileFilter(filter);

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedReader bis = new BufferedReader(new FileReader(fc.getSelectedFile()));
                while (bis.ready()) {
                    String row = bis.readLine();
                    //row := name # guard
                    if (row.split(TypeVar.DESC_KEYSEPARATION).length == 2) {
                        String name = row.split(TypeVar.DESC_KEYSEPARATION)[0];
                        String guard = row.split(TypeVar.DESC_KEYSEPARATION)[1].replaceAll(" ", "");
                        //name := OPid:up / OPid:down
                        //guard := 0 / 1 / something more complicated
                        eventNameExtractedGuardMap.put(name, guard.replaceAll("v", ""));
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong syntax in:\n" + fc.getSelectedFile().getName());
                        okToGoOn = false;
                        break;
                    }
                }
                bis.close();

                if (okToGoOn) {
                    //Generate sequence
                    new GenerateSequence();
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private String guardFromSPtoEFASyntaxTranslation(String guard) {
        //Change all _i to ==0
        guard = guard.replaceAll(TypeVar.SEPARATION + TypeVar.SP_INITIAL, TypeVar.EFA_EQUAL + "0");
        //Change all _e to ==1
        guard = guard.replaceAll(TypeVar.SEPARATION + TypeVar.SP_EXECUTE, TypeVar.EFA_EQUAL + "1");
        //Change all _f to ==2
        guard = guard.replaceAll(TypeVar.SEPARATION + TypeVar.SP_FINISH, TypeVar.EFA_EQUAL + "2");
        //Change all A to &
        guard = guard.replaceAll(TypeVar.SP_AND, TypeVar.EFA_AND);
        //Change all V to |
        guard = guard.replaceAll(TypeVar.SP_OR, TypeVar.EFA_OR);

        return guard;
    }

    private class SimpleDraw {

        SPGraph graph = ov.getGraph();

        public SimpleDraw(Set<String> namesOfSelectedOperations) {

            //Get the selected operations
            for (InternalOpData iData : allOperations) {
                if (namesOfSelectedOperations.contains(iData.getName())) {
                    selectedOps.add(iData);
                }
            }

            if (allOperationsAreOK(selectedOps)) {
                CreateInternalSOP();
            } else {
                JOptionPane.showMessageDialog(null, "Operation relations are to complex. \n I can't visualize operations!");
            }
        }

        private void CreateInternalSOP() {

            //root in wrapper class
            Wrapper master = new Wrapper();

            //create wrapper
            for (InternalOpData iData : selectedOps) {
                //generate new node
                Wrapper w = new Wrapper();
                w.iData = iData;
                w.head = master;

                //child to master
                master.children.add(w);
            }

            //update heads in wrapper class
            Set<Wrapper> startCells = new HashSet<Wrapper>(); //Operations without precontions

            for (Wrapper w : master.children) {

                Boolean isStartCell = true;

                if (!w.iData.getRawPrecondition().isEmpty()) {
                    String opInGuard = w.iData.getRawPrecondition().replaceAll(" ", "");
                    String suffix = TypeVar.SEPARATION + TypeVar.SP_FINISH;
                    opInGuard = opInGuard.substring(0, opInGuard.length() - suffix.length());
                    System.out.println(w.iData.getName() + " has id: " + opInGuard + " in guard");

                    for (Wrapper wGuard : master.children) {
                        if (wGuard.iData.getId().toString().equals(opInGuard)) {
                            w.head = wGuard;
                            isStartCell = false;
                            break;
                        }
                    }
                }

                if (isStartCell) {
                    //No precondition -> this w has to be a startCell!
                    startCells.add(w);
                    System.out.println(w.iData.getName() + " is start cell!");
                }
            }

            master.children.removeAll(startCells); //Already knows when these cells occur

            for (Wrapper w : startCells) {
                graph.addCell(w.setCell());
                fillInternalSOP(w, master.children);
            }
        }

        private void fillInternalSOP(Wrapper iWrap, Set<Wrapper> wrapps) {
            //Find wrapps that has this node in precondition
            Set<Wrapper> swrapps = new HashSet<Wrapper>();
            for (Wrapper w : wrapps) {
                if (w.head == iWrap) {
                    swrapps.add(w);
                }
            }

            if (swrapps.size() >= 1) {
                Cell parallelCell = null;
                if (swrapps.size() > 1) {
                    //Need to create a parallel cell
                    parallelCell = CellFactory.getInstance().getOperation("parallel");

                    //Add parallel cell after iWrap
                    graph.insertNewCell(iWrap.cell, parallelCell, false);
                }

                wrapps.removeAll(swrapps); //Know how to handle these wrapps -> remove them from set of wrapps

                //Add wrapps to cell
                for (Wrapper w : swrapps) {
                    if (swrapps.size() == 1) {
                        //Create a new cell after iWrap
                        graph.insertNewCell(iWrap.cell, w.setCell(), false);
                    } else {
                        //Create new cells in parallel cell
                        graph.insertGroupNode(parallelCell, null, w.setCell());
                    }
                    //Handle wrapps that are left
                    fillInternalSOP(w, wrapps);
                }
            }
        }

        private class Wrapper {

            Wrapper head = null;
            Cell cell = null;
            Set<Wrapper> children = new HashSet<Wrapper>();
            InternalOpData iData = null;

            public Wrapper() {
            }

            public Cell setCell() {
                cell = CellFactory.getInstance().getOperation("operation");
                //Data d = (Data) opCell.getValue();
                cell.setValue(iData.getOpData());
                return cell;
            }
        }
    }

    private class EFAModuleForView {

        final String varSuffix = ""; //_loc";
        final String varPrefix = "v";

        public EFAModuleForView(Set<String> namesOfSelectedOperations) {

            //Get the selected operations
            for (InternalOpData iData : allOperations) {
                if (namesOfSelectedOperations.contains(iData.getName())) {
                    selectedOps.add(iData);
                }
            }

            //Build the module
            SModule module = new SModule("temp");
            //Single Location automaton------------------------------------------
            SEFA efa = new SEFA("View", module);
            efa.addState(TypeVar.LOCATION, true, true);
            //-------------------------------------------------------------------
            for (InternalOpData iData : selectedOps) {
                SEGA ega;
                //create transition for initial->execute
                ega = new SEGA(iData.getName() + UP);
                basicTransGuardAction(ega, iData.getId().toString(), 0);
                addGuardBasedOnConditionString(ega, iData.getRawPrecondition());
                efa.addStandardSelfLoopTransition(ega);

                //create transition for execute->finish
                ega = new SEGA(iData.getName() + DOWN);
                basicTransGuardAction(ega, iData.getId().toString(), 1);
                addGuardBasedOnConditionString(ega, iData.getRawPostcondition());
                efa.addStandardSelfLoopTransition(ega);

                //create variables
                module.addIntVariable(varPrefix + iData.getId() + varSuffix, 0, 2, 0, 2);
            }

            //Generate the module
            module.DialogAutomataTransitions();

            //Receive the supervisor
            new SelectSupervisorDialog();
        }

        private void basicTransGuardAction(SEGA ega, String name, int from) {
            ega.andGuard(varPrefix + name + varSuffix + TypeVar.EFA_EQUAL + from);
            ega.addAction(varPrefix + name + varSuffix + TypeVar.EFA_SET + ++from);
        }

        private void addGuardBasedOnConditionString(SEGA ega, String condition) {
            if (!condition.isEmpty()) {
                //Example of raw precondition 18_f A (143_iV19_f)

                condition = guardFromSPtoEFASyntaxTranslation(condition);

                //Change all ID to opName+varSuffix_ID for selected operations
                for (InternalOpData iData : allOperations) {
                    if (selectedOps.contains(iData)) {
                        condition = condition.replaceAll(iData.getId().toString(), varPrefix + iData.getId() + varSuffix);
                    } else {
                        //Neutralize other relations to other operations
                        for (int i = 0; i <= 2; ++i) {
                            condition = condition.replaceAll(iData.getId().toString() + TypeVar.EFA_EQUAL + i, i + TypeVar.EFA_EQUAL + i);
                        }
                    }
                }

                ega.andGuard(condition);
            }
        }
    }

    private class GenerateSequence {

        SPGraph graph = ov.getGraph();

        public GenerateSequence() {
            System.out.println(GenerateSequence.class.toString());
            //drawSequence();
            setOperationGuards();
            updateSequence(null, null, null);
            //generateOperationSequence();
        }

        private void generateOperationSequence() {
            Set<OperationNode> nodes = new HashSet<OperationNode>(selectedOps.size());

            for (InternalOpData iData : selectedOps) {
                System.out.println(this.toString() + " " + iData.getName());
                //generate new node
                OperationNode opNode = new OperationNode();
                opNode.id = iData.getId();
                opNode.type = OperationNode.OPERATION;
                opNode.guardMap.put("pre", iData.preconditionForView);


            }

//                Cell cell = CellFactory.getInstance().getOperation("operation");
//                cell.setValue(iData.getOpData());
//                graph.addCell(cell);

        }

        private void drawSequence() {

//   final public static String TYPE_OPERATION = "operation";
//   final public static String TYPE_SOP = "sop";
//   final public static String TYPE_PARALLEL = "parallel";
//   final public static String TYPE_ALTERNATIVE = "alternative";
//   final public static String TYPE_ARBITRARY = "arbitrary";

            SPGraph graph = ov.getGraph();

            Cell cell1;
            Cell cell2;
            Cell cell3;
            Cell cell4;
            Cell cell5;
            Cell cell6;
            Cell cell7;

            cell1 = CellFactory.getInstance().getOperation("operation");
            cell2 = CellFactory.getInstance().getOperation("parallel");
            cell3 = CellFactory.getInstance().getOperation("alternative");
            cell4 = CellFactory.getInstance().getOperation("operation");
            cell5 = CellFactory.getInstance().getOperation("operation");
            cell6 = CellFactory.getInstance().getOperation("operation");

            cell7 = CellFactory.getInstance().getOperation("operation");

            cell1.getGeometry().setX(10);
            cell1.getGeometry().setY(50);
            Data d = (Data) cell1.getValue();
            d.setName("hej");
            cell1.setValue(d);
            graph.addCell(cell1);

            graph.insertNewCell(cell1, cell2, false);

            graph.insertGroupNode(cell2, null, cell3);
            graph.insertGroupNode(cell3, null, cell4);
            graph.insertGroupNode(cell3, null, cell5);

            graph.insertGroupNode(cell2, null, cell6);

            graph.addCell(cell7);

            System.out.println("cell2: " + cell2.getId());

        }

        private void fillSPGraph(OperationNode opNode, Set<OperationNode> nodes) {
            Set<OperationNode> sNodes = new HashSet<OperationNode>();
            for (OperationNode node : nodes) {
                if (node.head == opNode) {
                    sNodes.add(node);
                }
            }
            if (sNodes.size() > 1) {
                Cell parallelCell = CellFactory.getInstance().getOperation("parallel");
                graph.insertNewCell(opNode.cell, parallelCell, false);
                nodes.removeAll(sNodes);
                for (OperationNode node : sNodes) {
                    Cell opCell = CellFactory.getInstance().getOperation("operation");
                    Data d = (Data) opCell.getValue();
                    d.setName("pm" + node.id);
                    opCell.setValue(d);
                    node.cell = opCell;
                    graph.insertGroupNode(parallelCell, null, opCell);
                    fillSPGraph(node, nodes);
                }
            } else if (sNodes.size() == 1) {
                nodes.removeAll(sNodes);
                for (OperationNode node : sNodes) {
                    Cell opCell = CellFactory.getInstance().getOperation("operation");
                    Data d = (Data) opCell.getValue();
                    d.setName("pm" + node.id);
                    opCell.setValue(d);
                    node.cell = opCell;
                    graph.insertNewCell(opNode.cell, opCell, false);
                    fillSPGraph(node, nodes);
                }
            }
        }

        private void updateSequence(Integer relation, InternalOpData newData, InternalOpData oldData) {
            Integer counter = 10000;

            OperationNode master = new OperationNode();
            master.id = ++counter;
            master.type = OperationNode.PARALLEL;

            OperationNode masterStart = new OperationNode();
            masterStart.id = ++counter;
            masterStart.type = OperationNode.PARALLEL;
            master.children.add(masterStart);

            OperationNode masterEnd = new OperationNode();
            masterEnd.id = ++counter;
            masterEnd.type = OperationNode.PARALLEL;
            master.children.add(masterEnd);

            for (InternalOpData iData : selectedOps) {
                System.out.println(this.toString() + " " + iData.getName());
                //generate new node
                OperationNode opNode = new OperationNode();
                opNode.id = iData.getId();
                opNode.type = OperationNode.OPERATION;
                opNode.head = masterStart;
                opNode.tail = masterEnd;
                opNode.guardMap.put("pre", iData.preconditionForView);
                master.children.add(opNode);
            }

            for (OperationNode opNode : master.children) {
                System.out.println("Second time! " + opNode.id);

                if (opNode.hasGuard("pre")) {
                    //Has precondition
                    String otherOp = opNode.getFirstGuard();
                    System.out.println(otherOp);
                    otherOp = otherOp.substring(0, otherOp.length() - 3);
                    System.out.println(otherOp);

                    for (OperationNode node : master.children) {
                        if (node.id.toString().equals(otherOp)) {
                            opNode.head = node;
                            node.tail = opNode;
                            break;
                        }
                    }

                }
            }

            //print
            System.out.println("Print out");
            for (OperationNode opNode : master.children) {
                System.out.print(opNode.id);
                if (opNode.head != null) {
                    System.out.print(" head: " + opNode.head.id);
                }
                if (opNode.tail != null) {
                    System.out.print(" tail: " + opNode.tail.id);
                }
                System.out.println("");
            }

            //draw graph
            master.children.remove(masterEnd);
            master.children.remove(masterStart);
            Set<OperationNode> sNodes = new HashSet<OperationNode>();
            for (OperationNode node : master.children) {
                if (node.head == masterStart) {
                    sNodes.add(node);
                }
            }
            master.children.removeAll(sNodes);
            for (OperationNode node : sNodes) {
                Cell opCell = CellFactory.getInstance().getOperation("operation");
                Data d = (Data) opCell.getValue();
                d.setName("pm" + node.id);
                opCell.setValue(d);
                node.cell = opCell;
                graph.addCell(opCell);
                fillSPGraph(node, master.children);
            }
        }

        private Integer compareTwoOperations(InternalOpData newData, InternalOpData oldData) {

            String con = "";
            String nodeID = "";

            String newOldRelation = "";
            String oldNewRelation = "";

            con = newData.preconditionForView;
            nodeID = oldData.getId().toString();
            if (con.contains(nodeID)) {
                int startPos = con.indexOf(nodeID) + nodeID.length() + 2;
                newOldRelation = con.substring(startPos, startPos + 1);
            }

            con = oldData.preconditionForView;
            nodeID = newData.getId().toString();
            if (con.contains(nodeID)) {
                int startPos = con.indexOf(nodeID) + nodeID.length() + 2;
                oldNewRelation = con.substring(startPos, startPos + 1);
            }

            System.out.println(newData.getName() + " vs " + oldData.getName() + " | newOldRelation: " + newOldRelation + " | oldNewRelation: " + oldNewRelation);

            if (newOldRelation.equals("") && oldNewRelation.equals("")) {
                //Not direct related
                return 1;
            } else if (newOldRelation.equals("") && oldNewRelation.equals("2")) {
                //new->old
                return 2;
            } else if (newOldRelation.equals("2") && oldNewRelation.equals("")) {
                //old->new
                return 3;
            } else if (newOldRelation.equals("0") && oldNewRelation.equals("0")) {
                //Related with alternative
                return 4;
            } else {
                //Relation to complex
                return 0;
            }
        }

        private void setOperationGuards() {
            System.out.println(GenerateSequence.this.toString());
            for (InternalOpData iData : selectedOps) {
                //precondition
                if (eventNameExtractedGuardMap.containsKey(iData.getName() + UP)) {
                    String guard = "";
                    String superGuard = eventNameExtractedGuardMap.get(iData.getName() + UP);
                    if (!iData.getRawPrecondition().isEmpty()) {
                        guard = guardFromSPtoEFASyntaxTranslation(iData.getRawPrecondition());
                    }
                    if (superGuard.equals("0")) {
                        JOptionPane.showMessageDialog(null, "Big problems with relations! Result is not right");
                    } else if (!superGuard.equals("1")) {
                        if (guard.isEmpty()) {
                            guard = superGuard;
                        } else {
                            guard = "(" + guard + ")" + TypeVar.EFA_AND + superGuard;
                        }
                    }
                    iData.preconditionForView = guard.replaceAll(" ", "");
                }

//                //postcondition
//                if (eventNameExtractedGuardMap.containsKey(iData.getName() + DOWN)) {
//                    String guard = "";
//                    String superGuard = eventNameExtractedGuardMap.get(iData.getName() + DOWN);
//                    if (!iData.getRawPostcondition().isEmpty()) {
//                        guard = guardFromSPtoEFASyntaxTranslation(iData.getRawPostcondition());
//                    }
//                    if (superGuard.equals("0")) {
//                        JOptionPane.showMessageDialog(null, "Big problems with relations! Result is not right");
//                    } else if (!superGuard.equals("1")) {
//                        guard = "(" + guard + ")" + TypeVar.EFA_AND + superGuard;
//                    }
//                    iData.postconditionForView = guard.replaceAll(" ", "");
//                }

                System.out.println(iData.getName() + " has guards: " + iData.preconditionForView + " | " + iData.postconditionForView);
            }
        }
    }

    private class SelectSupervisorDialog extends JFrame implements ActionListener {

        JButton contButton = new JButton("Get supervisor!");

        public SelectSupervisorDialog() {
            setTitle("Select supervisor");
            Container c = getContentPane();
            //c.setLayout(new GridLayout(1, 1));
            c.add(contButton);
            contButton.addActionListener(this);
            contButton.setEnabled(true);

            setLocationRelativeTo(null);
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (contButton == e.getSource()) {
                dispose();
                selectFile();
            }
        }
    }

    private class SelectOperationsDialog extends JFrame implements ActionListener {

        JButton contButton = new JButton("View!");
        JButton sButton = new JButton("Select all");
        JButton dsButton = new JButton("Deselect all");
        JPanel buttonJp = new JPanel();
        JPanel jp = new JPanel();
        Set<JCheckBox> bg = new HashSet<JCheckBox>();

        public SelectOperationsDialog() {
            setTitle("Operation selection");
            Container c = getContentPane();
            c.setLayout(new GridLayout(3, 1));
            c.add(new JLabel("Select operations to include in view:"));
            c.add(jp);
            jp.setLayout(new FlowLayout());

            for (String name : allOperations.opNames()) {

                JCheckBox rb = new JCheckBox(name);
                rb.addActionListener(this);
                rb.setSelected(true);
                jp.add(rb);
                bg.add(rb);
            }

            c.add(buttonJp);
            sButton.addActionListener(this);
            sButton.setEnabled(true);
            buttonJp.add(sButton);

            dsButton.addActionListener(this);
            dsButton.setEnabled(true);
            buttonJp.add(dsButton);

            contButton.addActionListener(this);
            contButton.setEnabled(true);
            buttonJp.add(contButton);

            setLocationRelativeTo(null);
            pack();
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (contButton == e.getSource()) {
                Set<String> operations = new HashSet<String>();
                for (JCheckBox jcb : bg) {
                    if (jcb.isSelected()) {
                        operations.add(jcb.getText());
                    }
                }
                dispose();
                //new EFAModuleForView(operations);
                new SimpleDraw(operations);
            } else if (sButton == e.getSource()) {
                for (JCheckBox jcb : bg) {
                    jcb.setSelected(true);
                }
                contButton.setEnabled(true);
            } else if (dsButton == e.getSource()) {
                for (JCheckBox jcb : bg) {
                    jcb.setSelected(false);
                }
                contButton.setEnabled(false);
            } else {
                Boolean contButtonOK = false;
                for (JCheckBox jcb : bg) {
                    if (jcb.isSelected()) {
                        contButtonOK = true;
                        break;
                    }
                }
                if (contButtonOK) {
                    contButton.setEnabled(true);
                } else {
                    contButton.setEnabled(false);
                }
            }
        }
    }
}

