package sequenceplanner.multiProduct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import org.apache.log4j.Logger;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;
import sequenceplanner.view.operationView.graphextension.SPGraph;
import sequenceplanner.view.operationView.graphextension.SPGraphModel;

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

    /**
     * Fill <i>productTypes</i> based on operations in SP
     */
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

    public void printProductTypes() {
        //in console
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
        for (String key : productTypes.keySet()) {
            text = text + "PRODUCT TYPE: " + key + "\n";
            int Count = 0;
            for (OperationData opData : productTypes.get(key)) {
                text = text + opData.getName() + ", id:" + opData.getId();
                if (++Count == 3) {
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
     * The SP model is either updated with transport operations and the
     * position instances required or a new EFA module is created.
     */
    public void updateModelAfterTransportPlanning() {
        Error e = new Error("Problems during update");
        JFileChooser fc = new JFileChooser("user.dir");
        //fc.setFileFilter(filter);
        int answer = fc.showOpenDialog(null);

        if (answer == JFileChooser.APPROVE_OPTION) {
            HashMap<String, String> nameGuardMap = new HashMap<String, String>();
            HashMap<String, String> nameGuardOneMap = new HashMap<String, String>();
            String productType = null;
            String opType = null;
            try {
                BufferedReader bis = new BufferedReader(new FileReader(fc.getSelectedFile()));
                while (bis.ready()) {
                    String row = bis.readLine();
                    //row := name # guard
                    if (row.split(TypeVar.DESC_KEYSEPARATION).length == 2) {
                        String name = row.split(TypeVar.DESC_KEYSEPARATION)[0];
                        String guard = row.split(TypeVar.DESC_KEYSEPARATION)[1].replaceAll(" ", "");
                        opType = name.split(TypeVar.SEPARATION)[2];
                        productType = name.split(TypeVar.SEPARATION)[0];
                        if (!guard.equals("0")) {
                            nameGuardMap.put(name, guard);
                            if (guard.equals("1")) {
                                nameGuardOneMap.put(name, guard);
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
                log.info("How to update: ");
                if (nameGuardMap.size() == nameGuardOneMap.size() || opType.startsWith("s")) {
                    log.info("Create new operations");
                    updateModelAfterTransportPlanningCreateOperations(nameGuardOneMap, productType);
                } else {
                    log.info("Create a new module");
                    updateModelAfterTransportPlanningNewEFAModule(nameGuardMap, productType);
                }
            }
            e.printErrorList();
        }
    }

    /**
     * <b>Update SP model with transport operations</b><br/>
     * New operations are added as child operations to parent operations.
     * The parent operations has name "ProductType_for_synthesis".
     * @param nameGuardMap name of transport operation and it's guard
     */
    private void updateModelAfterTransportPlanningCreateOperations(HashMap<String, String> nameGuardMap, String pType) {
        //Add parent operation
        model.setCounter(model.getCounter() + 1);
        OperationData data = new OperationData(pType + "_for_synthesis", model.getCounter());
        data.setDescription(TypeVar.ED_PRODUCT_TYPE + TypeVar.DESC_VALUESEPARATION + pType);
        TreeNode parentOp = new TreeNode(data);
        model.getOperationRoot().insert(parentOp);

        HashSet<String> newNameSet = new HashSet<String>(nameGuardMap.size());
        simplifyPositionNames(nameGuardMap.keySet(), newNameSet);

        Set<OperationData> opDatas = new HashSet<OperationData>();
        //Add children
        for (String name : newNameSet) {
            String productType = TypeVar.ED_PRODUCT_TYPE + TypeVar.DESC_VALUESEPARATION + pType;
//            String opType = TypeVar.ED_OP_TYPE + TypeVar.DESC_VALUESEPARATION + TypeVar.ED_OP_TYPE_TRANSPORT;
            String sourcePos = TypeVar.ED_SOURCE_POS + TypeVar.DESC_VALUESEPARATION + name.split(TypeVar.SEPARATION)[0];
            String destPos = TypeVar.ED_DEST_POS + TypeVar.DESC_VALUESEPARATION + name.split(TypeVar.SEPARATION)[2];
            model.setCounter(model.getCounter() + 1);
            OperationData opData = new OperationData(name, model.getCounter());
            opDatas.add(opData);
            opData.setDescription(productType + " " + TypeVar.DESC_KEYSEPARATION + " " + sourcePos + " " + TypeVar.DESC_KEYSEPARATION + " " + destPos);
            parentOp.insert(new TreeNode(opData));
            saveOpertions();

            log.info("Added operation: " + name);
        }
    }

    /**
     * Method to simplify variable/position instance names. The event names for transitions returned from Supremica
     * are based on the global stated where its transition guard is fulfilled.<br/>
     * The new names have the format: "product:position:count".
     * @param oldNameset events from supremica
     * @param newNameSet events according to the new format
     */
    private void simplifyPositionNames(Set<String> oldNameSet, HashSet<String> newNameSet) {
        HashMap<String, String> oldNewMap = new HashMap<String, String>();
        HashMap<String, Integer> positionHistogramMap = new HashMap<String, Integer>();

        for (String name : oldNameSet) {
            String productType = name.split(TypeVar.SEPARATION)[0];
            String sourcePos = name.split(TypeVar.SEPARATION)[1].replaceAll(TypeVar.POS_MOVE, "").replaceAll(TypeVar.POS_PROCESS, "");
            String sourcePosBase = sourcePos.split(":")[0];

            String opType = name.split(TypeVar.SEPARATION)[2];

            String destPos = name.split(TypeVar.SEPARATION)[3].replaceAll(TypeVar.POS_MOVE, "").replaceAll(TypeVar.POS_PROCESS, "");
            String destPosBase = destPos.split(":")[0];

            String sPos = helpMetodTosimplifyPositionNames(positionHistogramMap, sourcePosBase, oldNewMap, sourcePos, productType);

            if (opType.startsWith("s")) {
                opType = opType.substring(1);
            }

            String dPos = sPos;
            if (!sourcePos.equals(destPos)) {
                dPos = helpMetodTosimplifyPositionNames(positionHistogramMap, destPosBase, oldNewMap, destPos, productType);

                //To capture process operations where dest position is refined compared to souce position. The physical positions is although the same.
                if (!opType.equals(TypeVar.TRANSPORT) && !opType.contains(TypeVar.ED_MERGE) && !name.contains(TypeVar.POS_OUT) && !name.contains(TypeVar.POS_MERGE)) {
                    opType = TypeVar.ED_MERGE + "op" + opType;
                }
            }

            newNameSet.add(sPos + TypeVar.SEPARATION + opType + TypeVar.SEPARATION + dPos);
        }
    }

    private String helpMetodTosimplifyPositionNames(HashMap<String, Integer> positionHistogramMap, String posBase, HashMap<String, String> oldNewMap, String pos, String productType) {
        if (!oldNewMap.containsKey(pos)) {
            if (!positionHistogramMap.containsKey(posBase)) {
                positionHistogramMap.put(posBase, 1);
            }
            int count = positionHistogramMap.get(posBase);
            oldNewMap.put(pos, productType + ":" + posBase + ":" + count);
            positionHistogramMap.put(posBase, ++count);
        }
        return oldNewMap.get(pos);
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

}
