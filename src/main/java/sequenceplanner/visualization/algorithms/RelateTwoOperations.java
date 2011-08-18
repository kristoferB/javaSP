package sequenceplanner.visualization.algorithms;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.model.data.OperationData;

/**
 * Finds the relation between two {@link OperationData} operations.
 * @author patrik
 */
public class RelateTwoOperations implements IRelateTwoOperations {

    private Set<String> setI = new HashSet<String>(1);
    private Set<String> setE = new HashSet<String>(1);
    private Set<String> setF = new HashSet<String>(1);
    private Set<String> setIF = new HashSet<String>(2);
    private Set<String> setIEF = new HashSet<String>(3);
    private Set<String> mOperation1up = null;
    private Set<String> mOperation2up = null;
    private Set<String> mOperation1down = null;
    private Set<String> mOperation2down = null;

    public RelateTwoOperations() {
        initSets();
    }

    @Override
    public void setOperationPair(final IRelationContainer iRC, final OperationData iOpData1, final OperationData iOpData2) {
        mOperation1up = iRC.getEventOperationLocationSetMap(iOpData1).get(ISupremicaInteractionForVisualization.Type.EVENT_UP.toString()).get(iOpData2);
        mOperation2up = iRC.getEventOperationLocationSetMap(iOpData2).get(ISupremicaInteractionForVisualization.Type.EVENT_UP.toString()).get(iOpData1);
        mOperation1down = iRC.getEventOperationLocationSetMap(iOpData1).get(ISupremicaInteractionForVisualization.Type.EVENT_DOWN.toString()).get(iOpData2);
        mOperation2down = iRC.getEventOperationLocationSetMap(iOpData2).get(ISupremicaInteractionForVisualization.Type.EVENT_DOWN.toString()).get(iOpData1);
    }

    /**
     * To get the relation between the two {@link IROperation)s given.
     * @return an {@link Integer} 0-9 for the relation
     */
    @Override
    public Integer getOperationRelation() {
        if (compareToSetQuartet(setF, setF, setI, setI)) {
//            System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(0, " ", " ") + mOperation2.mOpNode.getName());
            return ALWAYS_IN_SEQUENCE_12;
        } else if (compareToSetQuartet(setI, setI, setF, setF)) {
//            System.out.println(mOperation2.mOpNode.getName() + relationIntegerToString(0, " ", " ") + mOperation1.mOpNode.getName());
            return ALWAYS_IN_SEQUENCE_21;
        } else if (compareToSetQuartet(setIF, setIF, setI, setI)) {
//            System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(2, " ", " ") + mOperation2.mOpNode.getName());
            return SOMETIMES_IN_SEQUENCE_12;
        } else if (compareToSetQuartet(setI, setI, setIF, setIF)) {
//            System.out.println(mOperation2.mOpNode.getName() + relationIntegerToString(2, " ", " ") + mOperation1.mOpNode.getName());
            return SOMETIMES_IN_SEQUENCE_21;
        } else if (compareToSetQuartet(setIEF, setIEF, setIEF, setIEF)) {
//            System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(4, " ", " ") + mOperation2.mOpNode.getName());
            return PARALLEL;
        } else if (compareToSetQuartet(setI, setI, setI, setI)) {
//            System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(5, " ", " ") + mOperation2.mOpNode.getName());
            return ALTERNATIVE;
        } else if (compareToSetQuartet(setIF, setIF, setIF, setIF)) {
//            System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(6, " ", " ") + mOperation2.mOpNode.getName());
            return ARBITRARY_ORDER;
        } else if (compareToSetQuartet(setE, setE, setI, setF)) {
//            System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(7, " ", " ") + mOperation2.mOpNode.getName());
            return HIERARCHY_12;
        } else if (compareToSetQuartet(setI, setF, setE, setE)) {
//            System.out.println(mOperation2.mOpNode.getName() + relationIntegerToString(7, " ", " ") + mOperation1.mOpNode.getName());
            return HIERARCHY_21;
        } else if (compareToSetQuartet(setE, setE, setI, setIF)) {
            return SOMETIMES_IN_HIERARCHY_12;
        } else if (compareToSetQuartet(setI, setIF, setE, setE)) {
            return SOMETIMES_IN_HIERARCHY_21;
        }
//        System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(9, " ", " ") + mOperation2.mOpNode.getName());
        return OTHER;
    }

    /**
     * Relation as Integer -> relation as sign.<br/>
     * @param iRelation
     * @param iPrefix
     * @param iSufix
     * @return iPrefix + iRelation + iSufix
     */
    public static String relationIntegerToString(final Integer iRelation, final String iPrefix, final String iSufix) {
        String returnString = "";

        if (iPrefix != null) {
            returnString += iPrefix;
        }

        switch (iRelation) {
            case 0:
                returnString += ">";
                break;
            case 1:
                returnString += "<";
                break;
            case 2:
                returnString += ">~";
                break;
            case 3:
                returnString += "~<";
                break;
            case 4:
                returnString += "||";
                break;
            case 5:
                returnString += "+";
                break;
            case 6:
                returnString += "(+)";
                break;
            case 7:
                returnString += "[";
                break;
            case 8:
                returnString += "]";
                break;
            case 9:
                returnString += "~[";
                break;
            case 10:
                returnString += "]~";
                break;
            default:
                returnString += "^";
                break;
        }

        if (iSufix != null) {
            returnString += iSufix;
        }

        return returnString;
    }

    private boolean compareToSetQuartet(final Set iRefSet1, final Set iRefSet2, final Set iRefSet3, final Set iRefSet4) {
        return compareSetToRefSet(mOperation2up, iRefSet1) & compareSetToRefSet(mOperation2down, iRefSet2) &
                compareSetToRefSet(mOperation1up, iRefSet3) & compareSetToRefSet(mOperation1down, iRefSet4);
    }

    private boolean compareSetToRefSet(final Set iSetToCompare, final Set iReferenceSet) {
        if (!(iReferenceSet.size() == iSetToCompare.size())) {
            return false;
        }
        Set<String> copyOfRefSet = new HashSet<String>(iReferenceSet);
        if (copyOfRefSet.retainAll(iSetToCompare)) {
            return false;
        }
        return true;
    }

    private void initSets() {
        String nbr;
        nbr = "0"; //initial location
        setI.add(nbr);
        setIF.add(nbr);
        setIEF.add(nbr);
        nbr = "1"; //execution location
        setE.add(nbr);
        setIEF.add(nbr);
        nbr = "2";//finish location
        setF.add(nbr);
        setIF.add(nbr);
        setIEF.add(nbr);
    }
}
