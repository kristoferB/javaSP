package sequenceplanner.efaconverter;

import java.util.Map;
import java.util.Set;
import org.supremica.external.avocades.common.EGA;
import sequenceplanner.algorithms.visualization.ISupremicaInteractionForVisualization;
import sequenceplanner.condition.Condition;
import sequenceplanner.model.SOP.ConditionsFromSopNode;
import sequenceplanner.model.data.OperationData;

/**
 * Has to do with EFA. Should be merged with the general EFA conversion classes...
 * Help class for creation of transitions with event, guards and actions
 * @author patrik
 */
public class SEGA extends EGA {

    public SEGA() {
        super();
    }

    public SEGA(String event) {
        super(event);
    }

    /**
     * To book <i>to</i> and unbook <i>from</i> if object is in <i>from</i><br/>
     * @param from object is here
     * @param to object should go here
     */
    public void addBasicPositionBookAndUnbook(String from, String to) {
        if (from.length() > "".length()) {
            andGuard(from + ">0");
            addAction(from + "-1");
        }
        if (to.length() > "".length()) {
            addAction(to + "+1");
        }
    }

    public String guardFromSPtoEFASyntaxTranslation(String ioGuard) {
        //Change all _i to ==0
        ioGuard = ioGuard.replaceAll("_i", "==0");
        //Change all _e to ==1
        ioGuard = ioGuard.replaceAll("_e", "==1");
        //Change all _f to ==2
        ioGuard = ioGuard.replaceAll("_f", "==2");
        //Change all A to &
        ioGuard = ioGuard.replaceAll("A", "&");
        //Change all V to |
        ioGuard = ioGuard.replaceAll("V", "|");

        return ioGuard;
    }

//    public void addGuardBasedOnSPCondition(String iCondition, String iOpVariablePrefix, ModelParser iModelParser) {
//        //Example of raw precondition 18_f A (143_iV19_f)
//
//        //add precondition to guard
//        if (!iCondition.equals("")) {
//
//            //Change all ID to iOpVariablePrefix+ID
//            for (OpNode opNode : iModelParser.getOperations()) {
//                iCondition = iCondition.replaceAll(opNode.getStringId(), iOpVariablePrefix + opNode.getStringId());
//            }
//
//            iCondition = guardFromSPtoEFASyntaxTranslation(iCondition);
//
//            andGuard(iCondition);
//        }
//    }
    public void addGuardBasedOnSPCondition(String iCondition, final String iOpVariablePrefix, final Set<Integer> iSet) {
        //Example of raw precondition 18_f A (143_iV19_f)

        //add precondition to guard
        if (!iCondition.equals("")) {

            //Change all ID to iOpVariablePrefix+ID
            for (final Integer i : iSet) {
                iCondition = iCondition.replaceAll(Integer.toString(i), iOpVariablePrefix + Integer.toString(i));
            }

            iCondition = guardFromSPtoEFASyntaxTranslation(iCondition);

            andGuard(iCondition);
        }
    }

    /**
     * Ands all conditions for parameter <p>iOpData</p> to this transition.<br/>
     * @param iOpData
     * @param iConditionType is given as {@link ConditionsFromSopNode.ConditionType}
     * @param iType is given as {@link ISupremicaInteractionForVisualization.Type}
     */
    public void addCondition(final OperationData iOpData, final ConditionsFromSopNode.ConditionType iConditionType, final ISupremicaInteractionForVisualization.Type iType, final Set<String> iConditionsToInclude) {
        final Map<String, Map<ConditionsFromSopNode.ConditionType, Condition>> map = iOpData.getGlobalConditions();
        for (final String sop : map.keySet()) {
            if (allowedNamePrefixForCondition(sop, iConditionsToInclude)) {
                final Map<ConditionsFromSopNode.ConditionType, Condition> innerMap = map.get(sop);

                if (innerMap.containsKey(iConditionType)) {

                    if (iType.equals(ISupremicaInteractionForVisualization.Type.LOOK_FOR_GUARD)) {
                        final String condition = "(" + innerMap.get(iConditionType).getGuard().toString() + ")";
                        if (!condition.equals("()")) {
                            andGuard(condition);
                        }
                    } else if (iType.equals(ISupremicaInteractionForVisualization.Type.LOOK_FOR_ACTION)) {
                        final String condition = innerMap.get(iConditionType).getAction().toString();
                        if (!condition.equals("()")) {
                            addAction(condition);
                        }
                    }
                }
            }
        }
    }

    private boolean allowedNamePrefixForCondition(final String iCondition, final Set<String> iConditionsToInclude) {
        for (final String condition : iConditionsToInclude) {
            if (iCondition.equals(condition)) {
                return true;
            }
        }
        return false;
    }
}

