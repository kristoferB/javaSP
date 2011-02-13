/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.multiProduct;

import java.util.HashMap;
import org.apache.log4j.Logger;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public class ExtendedData {

    static Logger log = Logger.getLogger(ExtendedData.class);
  
    private static HashMap<String, String> dataMap;

    public ExtendedData(String description) {
        divideDescription(description);
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
                }
                dataMap.put(data[0], data[1]);
            }
            else {
                log.error("Metadata is not in order! KEY"+TypeVar.DESC_VALUESEPARATION+"value "+TypeVar.DESC_KEYSEPARATION+" KEY"+TypeVar.DESC_VALUESEPARATION+"value "+TypeVar.DESC_KEYSEPARATION+" ...");
            }
        }
    }

    public static String getProductType(String description) {
        divideDescription(description);
        log.info(TypeVar.ED_PRODUCT_TYPE + " is set to: " + dataMap.get(TypeVar.ED_PRODUCT_TYPE));
        return dataMap.get(TypeVar.ED_PRODUCT_TYPE);
    }

    public static String getOPType(String description) {
        divideDescription(description);
        log.info(TypeVar.ED_OP_TYPE + " is set to: " + dataMap.get(TypeVar.ED_OP_TYPE));
        return dataMap.get(TypeVar.ED_OP_TYPE);
    }

    public static String getSourcePos(String description) {
        divideDescription(description);
        log.info(TypeVar.ED_SOURCE_POS + " is set to: " + dataMap.get(TypeVar.ED_SOURCE_POS));
        return dataMap.get(TypeVar.ED_SOURCE_POS);
    }

    public static String getDestPos(String description) {
        divideDescription(description);
        log.info(TypeVar.ED_DEST_POS + " is set to: " + dataMap.get(TypeVar.ED_DEST_POS));
        return dataMap.get(TypeVar.ED_DEST_POS);
    }

    public static String getProcessingLevel(String description) {
        divideDescription(description);
        log.info(TypeVar.ED_PROCESSING_LEVEL_COUNTER + " is set to: " + dataMap.get(TypeVar.ED_PROCESSING_LEVEL_COUNTER));
        return dataMap.get(TypeVar.ED_PROCESSING_LEVEL_COUNTER);
    }

    public static String getMerge(String description) {
        divideDescription(description);
        log.info(TypeVar.ED_MERGE + " is set to: " + dataMap.get(TypeVar.ED_MERGE));
        return dataMap.get(TypeVar.ED_MERGE);
    }

}
