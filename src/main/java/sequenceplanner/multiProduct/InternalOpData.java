package sequenceplanner.multiProduct;

import java.util.HashMap;
import org.apache.log4j.Logger;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class InternalOpData {

    static Logger log = Logger.getLogger(InternalOpData.class);
    private static Error e = new Error(InternalOpData.class.toString());
    private static HashMap<String, String> dataMap;
    private OperationData opData;
    String preconditionForView = "";
    String postconditionForView = "";

    public InternalOpData(OperationData opData) {
        this.opData = opData;
    }

    private static void divideDescription(String description) {
        dataMap = new HashMap<String, String>();
        //log.info(description);
        String[] desc = description.replaceAll(" ", "").split(TypeVar.DESC_KEYSEPARATION);
        for (int i = 0; i < desc.length; ++i) {
            String[] data = desc[i].replaceAll(" ", "").split(TypeVar.DESC_VALUESEPARATION);
            if (data.length == 2) {
                if (dataMap.containsKey(data[0])) {
                    log.error("Metadata " + data[0] + " appears more than one time! Stored data is overwritten!");
                    e.error("Metadata " + data[0] + " appears more than one time! Stored data is overwritten!");
                }
                dataMap.put(data[0], data[1]);
            } else {
                log.error("Metadata is not in order! KEY" + TypeVar.DESC_VALUESEPARATION + "value " + TypeVar.DESC_KEYSEPARATION + " KEY" + TypeVar.DESC_VALUESEPARATION + "value " + TypeVar.DESC_KEYSEPARATION + " ...");
                e.error("Metadata is not in order! KEY" + TypeVar.DESC_VALUESEPARATION + "value " + TypeVar.DESC_KEYSEPARATION + " KEY" + TypeVar.DESC_VALUESEPARATION + "value " + TypeVar.DESC_KEYSEPARATION + " ...");
            }
        }
    }

    protected String get(String type) {
        divideDescription(opData.getDescription());
        //log.info(type + " is set to: " + dataMap.get(type));
        return dataMap.get(type);
    }

    public String getProductType() {
        return get(TypeVar.ED_PRODUCT_TYPE);
    }

    public String getOPType() {
        return get(TypeVar.ED_OP_TYPE);
    }

    public String getSourcePos() {
        return get(TypeVar.ED_SOURCE_POS);
    }

    public Boolean sourcePosIsReal() {
        return posIsReal(getSourcePos());
    }

    public Boolean sourcePosIsMergePos() {
        return posIsMergePos(getSourcePos());
    }

    public Boolean sourcePosIsOutPos() {
        return posIsOutPos(getSourcePos());
    }

    public String getDestPos() {
        return get(TypeVar.ED_DEST_POS);
    }

    public Boolean destPosIsReal() {
        return posIsReal(getDestPos());
    }
    
    public Boolean destPosIsMergePos() {
        return posIsMergePos(getDestPos());
    }

    public Boolean destPosIsOutPos() {
        return posIsOutPos(getDestPos());
    }

    public static Boolean posIsMergePos(String pos) {
        if (pos.equals(TypeVar.POS_MERGE)) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean posIsOutPos(String pos) {
        if (pos.equals(TypeVar.POS_OUT)) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean posIsReal(String pos) {
        if (pos.contains(TypeVar.POS_MERGE) || pos.contains(TypeVar.POS_OUT)) {
        //if (pos.contains(TypeVar.POS_OUT)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean hasSinglePos() {
        if (getSourcePos().equals(getDestPos())) {
            return true;
        } else {
            return false;
        }
    }

    public String getProcessingLevel() {
        return get(TypeVar.ED_PROCESSING_LEVEL_COUNTER);
    }

    public String getOperationCount() {
        return get(TypeVar.ED_OPERATION_COUNTER);
    }

    public boolean hasOperationCountNo() {
        if (TypeVar.ED_OPERATION_COUNTER_NO.equals(getOperationCount())) {
            return true;
        } else {
            return false;
        }
    }

    public String getMerge() {
        return get(TypeVar.ED_MERGE);
    }

    public String getGuard() {
        return get(TypeVar.ED_GUARD);
    }

    public String getOrder() {
        return get(TypeVar.ED_ORDER);
    }

    public OperationData getOpData() {
        return opData;
    }

    public String getName() {
        return opData.getName();
    }

    public Integer getId() {
        return opData.getId();
    }

    public String getRawPrecondition() {
        return opData.getRawPrecondition();
    }

    public String getRawPostcondition() {
        return opData.getRawPostcondition();
    }

}
