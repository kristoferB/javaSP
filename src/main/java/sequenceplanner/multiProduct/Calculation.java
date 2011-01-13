package sequenceplanner.multiProduct;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import org.apache.log4j.Logger;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class Calculation {

    static Logger log = Logger.getLogger(Calculation.class);
    private Model model = null;
    private HashMap<String, ArrayList<OperationData>> productTypes;

    public Calculation(Model model) {
        this.model = model;
        productTypes = new HashMap<String, ArrayList<OperationData>>();
        init();
    }

    private void init() {
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
            OperationData opData = (OperationData) model.getOperationRoot().getChildAt(i).getNodeData();
            String opProductType = ExtendedData.getProductType(opData.getDescription());
            if (!productTypes.containsKey(opProductType)) {
                productTypes.put(opProductType, new ArrayList<OperationData>());
            }
            productTypes.get(opProductType).add(opData);
        }
    }

    public String transportPlanningDialog() {
        String answer = (String) JOptionPane.showInputDialog(null,
                "Pick a product type: ",
                "Transport Planning Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                productTypes.keySet().toArray(),
                null);
        return answer;
    }

    public void transportPlanning() {
        Iterator<String> itKey = productTypes.keySet().iterator();
        while (itKey.hasNext()) {
            transportPlanningProductType(itKey.next());
        }
    }

    public void transportPlanningProductType(String key) {
        Error e = new Error();
        SModule smodule = new SModule("temp");
        smodule.setComment("Module for transport planning\n*****\nSet start position manually\nSet finish position manually\n" +
                "*****\nSynthesize supervisor through guard extraction\nChoose gurads from allowed state set\n");

        ArrayList<String> sourcePos = new ArrayList<String>();
        ArrayList<String> destPos = new ArrayList<String>();
        ArrayList<String> processingLevel = new ArrayList<String>();
        //fill lists that are needed
        for (OperationData opData : productTypes.get(key)) {
            String data = opData.getDescription();
            if (!destPos.contains(ExtendedData.getDestPos(data))) {
                destPos.add(ExtendedData.getDestPos(data));
            }
            if (!sourcePos.contains(ExtendedData.getSourcePos(data))) {
                sourcePos.add(ExtendedData.getSourcePos(data));
            }
            if (!TypeVar.ED_PROCESSING_LEVEL_COUNTER_NO.equals(ExtendedData.getProcessingLevel(data)) &&
                    !processingLevel.contains(ExtendedData.getSourcePos(data))) {
                processingLevel.add(ExtendedData.getSourcePos(data));
            }
        }

        //Add variables----------------------------------------------------------
        Iterator<String> its;

        its = sourcePos.iterator();
        while (its.hasNext()) {
            smodule.addIntVariable(key + TypeVar.SEPARATION + its.next() + TypeVar.POS_PROCESS, 0, 1, 0, 0);
        }
        its = destPos.iterator();
        while (its.hasNext()) {
            smodule.addIntVariable(key + TypeVar.SEPARATION + its.next() + TypeVar.POS_MOVE, 0, 1, 0, 0);
        }
        its = processingLevel.iterator();
        while (its.hasNext()) {
            smodule.addIntVariable(key + TypeVar.SEPARATION + its.next() + TypeVar.PROCESSING_LEVEL, 0, TypeVar.PROCESSING_LEVEL_COUNT_LIMIT, 0, null);
        }//----------------------------------------------------------------------

        //Single Location automaton----------------------------------------------
        SEFA efa = new SEFA(key, smodule);
        efa.addState(TypeVar.LOCATION, true, true);
        //-----------------------------------------------------------------------

        //Generate merge transition if necessary---------------------------------
        for (String pos : destPos) {
            if (pos.equals(TypeVar.POS_MERGE)) {
                SEGA ega = new SEGA();
                ega.andGuard(key + TypeVar.SEPARATION + pos + TypeVar.POS_MOVE + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
                ega.addAction(key + TypeVar.SEPARATION + pos + TypeVar.POS_MOVE + TypeVar.EFA_MINUS_ONE);
                ega.addAction(key + TypeVar.SEPARATION + pos + TypeVar.POS_PROCESS + TypeVar.EFA_PLUS_ONE);
                String t = key + TypeVar.SEPARATION + pos;
                efa.addTransition(TypeVar.LOCATION, TypeVar.LOCATION, t, ega.getGuard(), ega.getAction());
            }
        }//----------------------------------------------------------------------

        //Transport transitions--------------------------------------------------
        //No transitions to or from outside production cell
        while (sourcePos.remove(TypeVar.POS_OUT)) {
        }
        while (destPos.remove(TypeVar.POS_OUT)) {
        }
        //No transport transitions to or from merge positions
        while (sourcePos.remove(TypeVar.POS_MERGE)) {
        }
        while (destPos.remove(TypeVar.POS_MERGE)) {
        }

        its = destPos.iterator();
        while (its.hasNext()) {
            String dest = its.next();
            Iterator<String> itt = sourcePos.iterator();
            while (itt.hasNext()) {
                String source = itt.next();
                if (!dest.equals(source)) {
                    SEGA ega = new SEGA();
                    ega.andGuard(key + TypeVar.SEPARATION + dest + TypeVar.POS_MOVE + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
                    ega.addAction(key + TypeVar.SEPARATION + dest + TypeVar.POS_MOVE + TypeVar.EFA_MINUS_ONE);
                    ega.addAction(key + TypeVar.SEPARATION + source + TypeVar.POS_PROCESS + TypeVar.EFA_PLUS_ONE);
                    String t = key + TypeVar.SEPARATION + dest + TypeVar.SEPARATION + TypeVar.TRANSPORT + TypeVar.SEPARATION + source;
                    efa.addTransition(TypeVar.LOCATION, TypeVar.LOCATION, t, ega.getGuard(), ega.getAction());
                }
            }
        }//----------------------------------------------------------------------

        //Process transitons-----------------------------------------------------
        //to map operation ID and source pos
        HashMap<String, String> opIDPosMap = new HashMap<String, String>(8);
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
            OperationData opData = (OperationData) model.getOperationRoot().getChildAt(i).getNodeData();
            opIDPosMap.put(Integer.toString(opData.getId()), ExtendedData.getSourcePos(opData.getDescription()));
        }

        //go through operations, add guards and actions
        for (OperationData opData : productTypes.get(key)) {
            String desc = opData.getDescription();
            SEGA ega = new SEGA();

            //basic guards and actions regardless of pre-conditions
            String sp = key + TypeVar.SEPARATION + ExtendedData.getSourcePos(desc);
            ega.andGuard(sp + TypeVar.POS_PROCESS + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO); //product in pos
            if (!TypeVar.ED_PROCESSING_LEVEL_COUNTER_NO.equals(ExtendedData.getProcessingLevel(desc))) {
                ega.andGuard(sp + TypeVar.PROCESSING_LEVEL + TypeVar.EFA_EQUAL + 0); //The pos should in most cases not have been used before
                ega.addAction(sp + TypeVar.PROCESSING_LEVEL + TypeVar.EFA_PLUS_ONE);
            }
            ega.addAction(sp + TypeVar.POS_PROCESS + TypeVar.EFA_MINUS_ONE);
            String dp = key + TypeVar.SEPARATION + ExtendedData.getDestPos(desc);
            ega.addAction(dp + TypeVar.POS_MOVE + TypeVar.EFA_PLUS_ONE);

            //guards (process level counter) based on pre-conditions
            if (opData.getRawPrecondition().isEmpty()) {
                log.info(opData.getName() + " has no preconditions to other operations.");
            } else if (!opData.getRawPrecondition().contains(TypeVar.SP_OR)) {
                HashMap<String, Integer> posCountMap = new HashMap<String, Integer>(8);
                String[] terms = opData.getRawPrecondition().replaceAll(" ", "").replaceAll("_", "").replaceAll("f", "").split(TypeVar.SP_AND);

                log.info("Precon for " + opData.getName() + " " + opData.getRawPrecondition().replaceAll(" ", "").replaceAll("_", "").replaceAll("f", ""));
                //create position hisogram for precondition
                for (int id = 0; id < terms.length; ++id) {
                    int count = 1;
                    if (posCountMap.containsKey(opIDPosMap.get(terms[id]))) {
                        count += posCountMap.get(opIDPosMap.get(terms[id]));
                    }
                    //log.info("Compare " + posCountMap.containsKey(opIDPosMap.get(terms[id])) + "|Count " + count);
                    posCountMap.put(opIDPosMap.get(terms[id]), count);
                }

                Iterator<String> keyIt = posCountMap.keySet().iterator();
                while (keyIt.hasNext()) {
                    String pos = keyIt.next();
                    ega.andGuard(key + TypeVar.SEPARATION + pos + TypeVar.PROCESSING_LEVEL + TypeVar.EFA_EQUAL + posCountMap.get(pos));
                    log.info(opData.getName() + " has pl counter " + pos + TypeVar.EFA_EQUAL + posCountMap.get(pos));
                }
            } else {
                e.error("Implentation does not support disjunction! Operation preconditions may not be correct!");
            }

            efa.addTransition(TypeVar.LOCATION, TypeVar.LOCATION, opData.getName(), ega.getGuard(), ega.getAction());
        }//----------------------------------------------------------------------

        smodule.DialogAutomataTransitions();
        e.printErrorList();
    }

    public void printProductTypes() {
        //in log
        log.info("--------------------------------");
        log.info("PRODUCT TYPES");
        Iterator<String> itKey = productTypes.keySet().iterator();
        while (itKey.hasNext()) {
            String key = itKey.next();
            Iterator<OperationData> itData = productTypes.get(key).iterator();
            log.info("PRODUCT TYPE: " + key);
            while (itData.hasNext()) {
                OperationData opData = itData.next();
                log.info(opData.getName() + ", id:" + opData.getId());
            }
        }
        log.info("--------------------------------");

        //as messageDialog
        String text = "PRODUCT TYPES\n";
        for ( String key : productTypes.keySet()) {
            text = text + "PRODUCT TYPE: " + key + "\n";
            int Count = 0;
            for  (OperationData opData : productTypes.get(key)) {
                text = text + opData.getName() + ", id:" + opData.getId();
                if(++Count==3) {
                    text = text + "\n";
                    Count = 0;
                } else {
                    text = text + " | ";
                }
            }
        }
        JOptionPane.showMessageDialog(null, text);
    }

    private void saveOpertions() {

        TreeNode[] op = new TreeNode[model.getOperationRoot().getChildCount()];
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
            op[i] = model.getOperationRoot().getChildAt(i);
        }
        model.saveOperationData(op);

    }

    /**
     * <b>User selects supervisor implemented as text-file.</b><br/>
     * The SP model is either updated with transport operations or
     * a new EFA module is created
     */
    public void updateModelAfterTransportPlanning() {
        Error e = new Error("Problems during update");
        JFileChooser fc = new JFileChooser("user.dir");
        //fc.setFileFilter(filter);
        int answer = fc.showOpenDialog(null);

        if (answer == JFileChooser.APPROVE_OPTION) {
            HashMap<String, String> nameGuardMap = new HashMap<String, String>();
            Boolean nameEguals4 = true;
            Boolean guardEquals1 = true;
            String productType = null;
            try {
                BufferedReader bis = new BufferedReader(new FileReader(fc.getSelectedFile()));
                while (bis.ready()) {
                    String row = bis.readLine();
                    //row := name # guard
                    if (row.split(TypeVar.DESC_KEYSEPARATION).length == 2) {
                        String name = row.split(TypeVar.DESC_KEYSEPARATION)[0];
                        String guard = row.split(TypeVar.DESC_KEYSEPARATION)[1].replaceAll(" ", "");
                        //name := productType _ sourcePos _ t/opID _ destPos (length==4)
                        //or
                        //name := productType _ sourcePos _ t _ destPos _ someGuard (length>4)
                        //guard equal to 0 <-> transition may never occur
                        if (name.split(TypeVar.SEPARATION).length >= 4 && !guard.equals("0")) {
                            nameGuardMap.put(name, guard);
                            productType = name.split(TypeVar.SEPARATION)[0];
                            if (name.split(TypeVar.SEPARATION).length > 4) {
                                nameEguals4 = false;
                            }
                            if (!guard.equals("1")) {
                                guardEquals1 = false;
                            }
                        }
                    } else {
                        e.error("Given textfile does not support the format: name # guard. No update is performed!");
                        break;
                    }
                }
                bis.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if (e.noErrors()) {
                log.info("How to update: nameEguals4: "+ nameEguals4 + " guardEquals1: " + guardEquals1);
                if (nameEguals4) {
                    if (guardEquals1) { //create ops simple transition names
                        updateModelAfterTransportPlanningCreateOperations(nameGuardMap);
                    } else { //create new EFA module
                        log.info("new EFA module");
                        updateModelAfterTransportPlanningNewEFAModule(nameGuardMap,productType);
                    }
                } else {
                    if (guardEquals1) { //create ops complex transition names
                        //Change this
                        //updateModelAfterTransportPlanningCreateOperations(nameGuardMap);
                    } else { //new guards created during second run. Something is strange
                        e.error("New guards created during second run. Something is strange!");
                    }
                }
            }
            e.printErrorList();
        }
    }

    /**
     * <b>Update SP model with transport operations</b><br/>
     * @param nameGuardMap name of transport operation and it's guard
     */
    private void updateModelAfterTransportPlanningCreateOperations(HashMap<String, String> nameGuardMap) {
        for (String name : nameGuardMap.keySet()) {
            String guard = nameGuardMap.get(name);
            String productType = TypeVar.ED_PRODUCT_TYPE + TypeVar.DESC_VALUESEPARATION + name.split(TypeVar.SEPARATION)[0];
            String opType = TypeVar.ED_OP_TYPE + TypeVar.DESC_VALUESEPARATION + TypeVar.ED_OP_TYPE_TRANSPORT;
            String sourcePos = TypeVar.ED_SOURCE_POS + TypeVar.DESC_VALUESEPARATION + name.split(TypeVar.SEPARATION)[1];
            String destPos = TypeVar.ED_DEST_POS + TypeVar.DESC_VALUESEPARATION + name.split(TypeVar.SEPARATION)[3];
            guard = TypeVar.ED_GUARD + TypeVar.DESC_VALUESEPARATION + guard;
            model.setCounter(model.getCounter() + 1);
            OperationData opData = new OperationData(name, model.getCounter());
            opData.setDescription(productType + " " + TypeVar.DESC_KEYSEPARATION + " " + opType + " " + TypeVar.DESC_KEYSEPARATION + " " + sourcePos + " " + TypeVar.DESC_KEYSEPARATION + " " + destPos + " " + TypeVar.DESC_KEYSEPARATION + " " + guard);
            model.getOperationRoot().insert(new TreeNode(opData));
            saveOpertions();
            log.info("Added operation: " + name);
        }
    }

    private void updateModelAfterTransportPlanningNewEFAModule(HashMap<String, String> nameGuardMap, String productType) {
        new EFAforTransport(model).transportPlanning(productType, nameGuardMap);
    }

    /**
     * Open dialog to save wmod files
     * @param moduleSubject the module to be saved
     */
    public static void saveWMODFile(ModuleSubject moduleSubject) {
        try {
            String filepath = "";
            JFileChooser fc = new JFileChooser("C:\\Documents and Settings\\EXJOBB SOCvision\\Desktop");
            int fileResult = fc.showSaveDialog(null);
            if (fileResult == JFileChooser.APPROVE_OPTION) {
                filepath = fc.getSelectedFile().getAbsolutePath();
                File file = new File(filepath);
                file.createNewFile();
                moduleSubject.setName(file.getName().replaceAll(".wmod", ""));
                ModuleSubjectFactory factory = new ModuleSubjectFactory();
                // Save module to file
                JAXBModuleMarshaller marshaller = new JAXBModuleMarshaller(factory, CompilerOperatorTable.getInstance());
                marshaller.marshal(moduleSubject, file);
            }
        } catch (Exception t) {
            t.printStackTrace();
        }
    }

    /**
     * Dialog to select what product types to include in synthesis.<br/>
     * The selected types are translated into a wmod file.
     */
    public class GenerateModuleBasedOnSelectedProductTypes implements ActionListener {

        JFrame mainFrame = null;
        JButton checkButton = null;
        JButton contButton = null;
        JPanel buttonJp = null;
        JPanel jp = null;
        ArrayList<JCheckBox> bg = null;
        ArrayList<String> products = null;

        public GenerateModuleBasedOnSelectedProductTypes() {
            Dialog();
        }

        private void Dialog() {
            jp = new JPanel();
            jp.setLayout(new FlowLayout());
            bg = new ArrayList<JCheckBox>();
            Iterator<String> its = productTypes.keySet().iterator();
            while (its.hasNext()) {
                String text = its.next();
                JCheckBox rb = new JCheckBox(text);
                rb.addActionListener(this);
                rb.setSelected(true);
                jp.add(rb);
                bg.add(rb);
            }

            buttonJp = new JPanel();
            checkButton = new JButton("Check!");
            checkButton.addActionListener(this);
            buttonJp.add(checkButton);
            contButton = new JButton("Get transitions!");
            contButton.addActionListener(this);
            contButton.setEnabled(false);
            buttonJp.add(contButton);

            mainFrame = new JFrame("Product type selection");
            mainFrame.setAlwaysOnTop(true);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.getContentPane().add(new JLabel("Select products to include in supervisor synthesis:"), BorderLayout.NORTH);
            mainFrame.getContentPane().add(jp, BorderLayout.CENTER);
            mainFrame.getContentPane().add(buttonJp, BorderLayout.SOUTH);
            mainFrame.pack();
            mainFrame.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (checkButton == e.getSource()) {
                products = new ArrayList<String>();
                Iterator<JCheckBox> itb = bg.iterator();
                while (itb.hasNext()) {
                    JCheckBox jcb = itb.next();
                    if (jcb.isSelected()) {
                        products.add(jcb.getText());
                    }
                }
                if (!products.isEmpty()) {
                    contButton.setEnabled(true);
                }
            } else if (contButton == e.getSource()) {
                mainFrame.dispose();
                new EFAforSupervisor(products, model);
            } else {
                contButton.setEnabled(false);
            }
        }
    }
//    /**
//     * User may select what product types the tops to be removed belong to.
//     */
//    public class RemoveTransportOperations implements ActionListener {
//
//        JFrame mainFrame = null;
//        JButton removeButton = null;
//        JPanel buttonJp = null;
//        JPanel jp = null;
//        ArrayList<JCheckBox> bg = null;
//        ArrayList<String> products = null;
//
//        public RemoveTransportOperations() {
//            Dialog();
//        }
//
//        private void Dialog() {
//            jp = new JPanel();
//            jp.setLayout(new FlowLayout());
//            bg = new ArrayList<JCheckBox>();
//            Iterator<String> its = productTypes.keySet().iterator();
//            while (its.hasNext()) {
//                String text = its.next();
//                JCheckBox rb = new JCheckBox(text);
//                rb.setSelected(true);
//                jp.add(rb);
//                bg.add(rb);
//            }
//
//            buttonJp = new JPanel();
//            removeButton = new JButton("Remove!");
//            removeButton.addActionListener(this);
//            buttonJp.add(removeButton);
//
//            mainFrame = new JFrame("Remove transport operations from operation tree");
//
//            mainFrame.setLocationRelativeTo(null);
//            mainFrame.getContentPane().setLayout(new FlowLayout());
//            mainFrame.getContentPane().add(new JLabel("Select product types where top belong:"), BorderLayout.NORTH);
//            mainFrame.getContentPane().add(jp, BorderLayout.CENTER);
//            mainFrame.getContentPane().add(removeButton, BorderLayout.SOUTH);
//            mainFrame.pack();
//            mainFrame.setVisible(true);
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if (removeButton == e.getSource()) {
//                products = new ArrayList<String>();
//                Iterator<JCheckBox> itb = bg.iterator();
//                while (itb.hasNext()) {
//                    JCheckBox jcb = itb.next();
//                    if (jcb.isSelected()) {
//                        mainFrame.dispose();
//                        removeTopsToProductType(jcb.getText());
//                    }
//                }
//            }
//        }
//
//        /**
//         * To remove all transport op generated for product from op tree
//         * @param product
//         */
//        private void removeTopsToProductType(String product) {
//            TreeNode treeNode = model.getOperationRoot();
//            ArrayList<TreeNode> nodesToBeRemoved = new ArrayList<TreeNode>(treeNode.getChildCount());
//            for (int i = 0; i < treeNode.getChildCount(); ++i) {
//                OperationData opData = (OperationData) treeNode.getChildAt(i).getNodeData();
//                if (ExtendedData.getProductType(opData.getDescription()) != null &&
//                        ExtendedData.getOPType(opData.getDescription()) != null) {
//                    if (ExtendedData.getProductType(opData.getDescription()).equals(product) &&
//                            ExtendedData.getOPType(opData.getDescription()).equals(TypeVar.ED_OP_TYPE_TRANSPORT)) {
//                        nodesToBeRemoved.add(treeNode.getChildAt(i));
//                    }
//                }
//            }
//            for (TreeNode tn : nodesToBeRemoved) {
//                treeNode.remove(tn);
//            }
//        }
//    }
}
