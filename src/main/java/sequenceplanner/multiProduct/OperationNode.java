/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.multiProduct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.CellFactory;

/**
 *
 * @author patrik
 */
public class OperationNode {

    final public static String OPERATION = "OPERATION";
    final public static String PARALLEL = "PARALLEL";
    final public static String ALTERNATIVE = "ALTERNATIVE";
    final public static String ARBITRARY = "ARBITRARY";
    OperationNode head = null;
    OperationNode tail = null;
    Integer id = null;
    String type = "";
    Cell cell = null;
    Set<OperationNode> children = new HashSet<OperationNode>();
    HashMap<String, String> guardMap = new HashMap<String, String>(1);
    InternalOpData iData = null;

    public OperationNode() {
    }

    public String getFirstGuard() {
        if (guardMap.containsKey("pre")) {
            String[] guard = guardMap.get("pre").split(TypeVar.EFA_AND);
            System.out.println("guard size: " + guard.length);
            String newGuard = "";
            if (guard.length > 1) {
                for (int i = 1; i < guard.length; ++i) {
                    if (!newGuard.isEmpty()) {
                        newGuard = newGuard + TypeVar.EFA_AND;
                    }
                    newGuard = newGuard + guard[i];
                }
            }
            guardMap.put("pre", newGuard);

            return guard[0];
        } else {
            return "";
        }
    }

    public boolean hasGuard(String guardType) {
        if (guardMap.containsKey(guardType)) {
            if (guardMap.get(guardType).length() > 1) {
                return true;
            }
        }
        return false;
    }

    public Cell setCell() {
        cell = CellFactory.getInstance().getOperation("operation");
        //Data d = (Data) opCell.getValue();
        cell.setValue(iData.getOpData());
        return cell;
    }
}
