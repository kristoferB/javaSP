/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.multiProduct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.EGA;
import org.supremica.external.avocades.common.Module;
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

    public Module transportPlanningProductType(String key) {
        Module module = new Module(key, false);
        Iterator<OperationData> itData = productTypes.get(key).iterator();
        ArrayList<String> sourcePos = new ArrayList<String>();
        ArrayList<String> destPos = new ArrayList<String>();
        ArrayList<String> processingLevel = new ArrayList<String>();

        //Create lists that are needed
        while (itData.hasNext()) {
            OperationData opData = itData.next();
            String data = opData.getDescription();
            destPos.add(ExtendedData.getDestPos(data));
            sourcePos.add(ExtendedData.getSourcePos(data));
            if (!TypeVar.ED_PROCESSING_LEVEL_COUNTER_NO.equals(ExtendedData.getProcessingLevel(data))) {
                processingLevel.add(ExtendedData.getSourcePos(data));
            }
        }

        //Generate EFA module
        EFA varEfa = new EFA(key, module);
        //Variables
        Iterator<String> its = sourcePos.iterator();
        while (its.hasNext()) {
            varEfa.addIntegerVariable(key + TypeVar.SEPARATION + its.next() + TypeVar.POS_PROCESS, 0, 1, 0, null);
        }
        its = destPos.iterator();
        while (its.hasNext()) {
            varEfa.addIntegerVariable(key + TypeVar.SEPARATION + its.next() + TypeVar.POS_MOVE, 0, 1, 0, null);
        }
        its = processingLevel.iterator();
        while (its.hasNext()) {
            varEfa.addIntegerVariable(key + TypeVar.SEPARATION + its.next() + TypeVar.PROCESSING_LEVEL, 0, 5, 0, null);
        }

        //Single Location automaton
        EFA efa = new EFA(key, module);
        module.addAutomaton(efa);
        efa.addState(TypeVar.LOCATION, true, true);
        //Transport transitions
        sourcePos.remove(TypeVar.POS_OUT);
        destPos.remove(TypeVar.POS_OUT);
        its = destPos.iterator();
        while (its.hasNext()) {
            String dest = its.next();
            Iterator<String> itt = sourcePos.iterator();
            while (itt.hasNext()) {
                String source = itt.next();
                if (!dest.equals(source)) {
                    EGA ega = new EGA();
                    ega.andGuard(key + TypeVar.SEPARATION + dest + TypeVar.POS_MOVE + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
                    ega.addAction(key + TypeVar.SEPARATION + dest + TypeVar.POS_MOVE + TypeVar.EFA_MINUS_ONE);
                    ega.addAction(key + TypeVar.SEPARATION + source + TypeVar.POS_PROCESS + TypeVar.EFA_PLUS_ONE);
                    String t = key + TypeVar.SEPARATION + dest + TypeVar.SEPARATION + TypeVar.TRANSPORT + TypeVar.SEPARATION + source;
                    efa.addTransition(TypeVar.LOCATION, TypeVar.LOCATION, t, ega.getGuard(), ega.getAction());
                }
            }
        }
        //Process transitons
        //to map op ID and source pos
        HashMap<String, String> opIDPosMap = new HashMap<String, String>(8);
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
            OperationData opData = (OperationData) model.getOperationRoot().getChildAt(i).getNodeData();
            opIDPosMap.put(Integer.toString(opData.getId()), ExtendedData.getSourcePos(opData.getDescription()));
        }
        itData = productTypes.get(key).iterator();
        while (itData.hasNext()) {
            OperationData opData = itData.next();
            String desc = opData.getDescription();
            EGA ega = new EGA();
            String sp = key + TypeVar.SEPARATION + ExtendedData.getSourcePos(desc);
            ega.andGuard(sp + TypeVar.POS_PROCESS + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
            if (!TypeVar.ED_PROCESSING_LEVEL_COUNTER_NO.equals(ExtendedData.getProcessingLevel(desc))) {
                ega.addAction(sp + TypeVar.PROCESSING_LEVEL + TypeVar.EFA_PLUS_ONE);
            }
            ega.addAction(sp + TypeVar.POS_PROCESS + TypeVar.EFA_MINUS_ONE);
            String dp = key + TypeVar.SEPARATION + ExtendedData.getDestPos(desc);
            ega.addAction(dp + TypeVar.POS_MOVE + TypeVar.EFA_PLUS_ONE);

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
                    log.info(opData.getName() + " hash " + pos + TypeVar.EFA_EQUAL + posCountMap.get(pos));
                }
            } else {
                log.error("Implentation does not support disjunction! Operation preconditions may not be correct!");
            }

            efa.addTransition(TypeVar.LOCATION, TypeVar.LOCATION, opData.getName(), ega.getGuard(), ega.getAction());
        }

        return module;
    }

    public void printProductTypes() {
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
    }

    private void saveOpertions() {

        TreeNode[] op = new TreeNode[model.getOperationRoot().getChildCount()];
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i) {
            op[i] = model.getOperationRoot().getChildAt(i);
        }
        model.saveOperationData(op);

    }

    public void updateModelAfterTransportPlanning() {
        JFileChooser fc = new JFileChooser("user.dir");
        //fc.setFileFilter(filter);
        int answer = fc.showOpenDialog(null);

        if (answer == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedReader bis = new BufferedReader(new FileReader(fc.getSelectedFile()));
                while (bis.ready()) {
                    String row = bis.readLine();
                    if (row.split(TypeVar.DESC_KEYSEPARATION).length == 2) {
                        String name = row.split(TypeVar.DESC_KEYSEPARATION)[0];
                        String guard = row.split(TypeVar.DESC_KEYSEPARATION)[1].replaceAll(" ", "");
                        if (name.split(TypeVar.SEPARATION).length == 4 && !guard.equals("0")) {
                            String productType = TypeVar.ED_PRODUCT_TYPE + TypeVar.DESC_VALUESEPARATION + name.split(TypeVar.SEPARATION)[0];
                            String opType = TypeVar.ED_OP_TYPE + TypeVar.DESC_VALUESEPARATION + TypeVar.ED_OP_TYPE_TRANSPORT;
                            String sourcePos = TypeVar.ED_SOURCE_POS + TypeVar.DESC_VALUESEPARATION +name.split(TypeVar.SEPARATION)[1];
                            String destPos = TypeVar.ED_DEST_POS + TypeVar.DESC_VALUESEPARATION +name.split(TypeVar.SEPARATION)[3];
                            guard = TypeVar.ED_GUARD + TypeVar.DESC_VALUESEPARATION + guard;
                            model.setCounter(model.getCounter() + 1);
                            OperationData opData = new OperationData(name, model.getCounter());
                            opData.setDescription(productType + " " + TypeVar.DESC_KEYSEPARATION + " " + opType
                                    + " " + TypeVar.DESC_KEYSEPARATION + " " + sourcePos
                                    + " " + TypeVar.DESC_KEYSEPARATION + " " + destPos
                                    + " " + TypeVar.DESC_KEYSEPARATION + " " + guard);
                            model.getOperationRoot().insert(new TreeNode(opData));
                            saveOpertions();
                            log.info("Added operation: " + name);
                        }
                    }
                }
                bis.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public Module EFAForMultiProductSupervisor() {
        String[] products = {"P1","P5"};
        return new EFAforSupervisor(products,model).getModule();
    }
}
