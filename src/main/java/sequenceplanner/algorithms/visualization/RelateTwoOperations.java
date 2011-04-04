package sequenceplanner.algorithms.visualization;

import sequenceplanner.efaconverter.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds the relation between two {@link IROperation}s of node type OPERATION.
 * @author patrik
 */
public class RelateTwoOperations implements IRelateTwoOperations{

    private Set<String> setI = new HashSet<String>(1);
    private Set<String> setE = new HashSet<String>(1);
    private Set<String> setF = new HashSet<String>(1);
    private Set<String> setIF = new HashSet<String>(2);
    private Set<String> setIEF = new HashSet<String>(3);
    private ROperation mOperation1 = null;
    private ROperation mOperation2 = null;
    private Set<String> mOperation1up = null;
    private Set<String> mOperation2up = null;
    private Set<String> mOperation1down = null;
    private Set<String> mOperation2down = null;

    public RelateTwoOperations() {
        initSets();
    }

    @Override
    public void setOperationPair(IROperation iOperation1, IROperation iOperation2) {
        this.mOperation1 = (ROperation) iOperation1;
        this.mOperation2 = (ROperation) iOperation2;

        mOperation1up = mOperation1.getmEventOperationLocationSetMap().get("up").get(iOperation2);
        mOperation2up = mOperation2.getmEventOperationLocationSetMap().get("up").get(iOperation1);
        mOperation1down = mOperation1.getmEventOperationLocationSetMap().get("down").get(iOperation2);
        mOperation2down = mOperation2.getmEventOperationLocationSetMap().get("down").get(iOperation1);
    }

    /**
     * To get the relation between the two {@link RVNode}s given in the constructor
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
        }
//        System.out.println(mOperation1.mOpNode.getName() + relationIntegerToString(9, " ", " ") + mOperation2.mOpNode.getName());
        return OTHER;
    }

    public static String relationIntegerToString(final Integer iRelation, final String iPrefix, final String iSufix) {
        String returnString = "";

        if (iPrefix != null) {
            returnString += iPrefix;
        }

        switch (iRelation) {
            case 0:
                returnString += ">"; break;
            case 1:
                returnString += "<"; break;
            case 2:
                returnString += ">~"; break;
            case 3:
                returnString += "~<"; break;
            case 4:
                returnString += "||"; break;
            case 5:
                returnString += "+"; break;
            case 6:
                returnString += "(+)"; break;
            case 7:
                returnString += "["; break;
            case 8:
                returnString += "]"; break;
            default:
                returnString += "^"; break;
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
