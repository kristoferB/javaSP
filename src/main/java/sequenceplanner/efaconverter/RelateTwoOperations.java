package sequenceplanner.efaconverter;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds the relation between two {@link RVNode}s of node type OPERATION.
 * @author patrik
 */
public class RelateTwoOperations {

    final public static Integer ALWAYS_IN_SEQUENCE_12 = 0;
    final public static Integer ALWAYS_IN_SEQUENCE_21 = 1;
    final public static Integer SOMETIMES_IN_SEQUENCE_12 = 2;
    final public static Integer SOMETIMES_IN_SEQUENCE_21 = 3;
    final public static Integer PARALLEL = 4;
    final public static Integer ALTERNATIVE = 5;
    final public static Integer ARBITRARY_ORDER = 6;
    final public static Integer HIERARCHY_12 = 7;
    final public static Integer HIERARCHY_21 = 8;
    final public static Integer OTHER = 9;
    private Set<String> setI = new HashSet<String>(1);
    private Set<String> setE = new HashSet<String>(1);
    private Set<String> setF = new HashSet<String>(1);
    private Set<String> setIF = new HashSet<String>(2);
    private Set<String> setIEF = new HashSet<String>(3);
    private RVNode mRvNode1 = null;
    private RVNode mRvNode2 = null;
    private Set<String> mRvNode1up = null;
    private Set<String> mRvNode2up = null;
    private Set<String> mRvNode1down = null;
    private Set<String> mRvNode2down = null;


    public RelateTwoOperations(RVNode iRvNode1, RVNode iRvNode2) {
        this.mRvNode1 = iRvNode1;
        this.mRvNode2 = iRvNode2;

        mRvNode1up = iRvNode1.mEventOperationLocationSetMap.get("up").get(iRvNode2);
        mRvNode2up = iRvNode2.mEventOperationLocationSetMap.get("up").get(iRvNode1);
        mRvNode1down = iRvNode1.mEventOperationLocationSetMap.get("down").get(iRvNode2);
        mRvNode2down = iRvNode2.mEventOperationLocationSetMap.get("down").get(iRvNode1);

        initSets();
    }

    /**
     * To get the relation between the two {@link RVNode}s given in the constructor
     * @return an {@link Integer} 0-9 for the relation
     */
    public Integer getOperationRelation() {
        if (compareToSetQuartet(setF, setF, setI, setI)) {
            System.out.println(mRvNode1.mOpNode.getName() + " > " + mRvNode2.mOpNode.getName());
            return ALWAYS_IN_SEQUENCE_12;
        } else if (compareToSetQuartet(setI, setI, setF, setF)) {
            System.out.println(mRvNode2.mOpNode.getName() + " > " + mRvNode1.mOpNode.getName());
            return ALWAYS_IN_SEQUENCE_21;
        } else if (compareToSetQuartet(setIF, setIF, setI, setI)) {
            System.out.println(mRvNode1.mOpNode.getName() + " >~ " + mRvNode2.mOpNode.getName());
            return SOMETIMES_IN_SEQUENCE_12;
        } else if (compareToSetQuartet(setI, setI, setIF, setIF)) {
            System.out.println(mRvNode2.mOpNode.getName() + " >~ " + mRvNode1.mOpNode.getName());
            return SOMETIMES_IN_SEQUENCE_21;
        } else if (compareToSetQuartet(setIEF, setIEF, setIEF, setIEF)) {
            System.out.println(mRvNode1.mOpNode.getName() + " || " + mRvNode2.mOpNode.getName());
            return PARALLEL;
        } else if (compareToSetQuartet(setI, setI, setI, setI)) {
            System.out.println(mRvNode1.mOpNode.getName() + " + " + mRvNode2.mOpNode.getName());
            return ALTERNATIVE;
        } else if (compareToSetQuartet(setIF, setIF, setIF, setIF)) {
            System.out.println(mRvNode1.mOpNode.getName() + " (+) " + mRvNode2.mOpNode.getName());
            return ARBITRARY_ORDER;
        } else if (compareToSetQuartet(setE, setE, setI, setF)) {
            System.out.println(mRvNode1.mOpNode.getName() + " [ " + mRvNode2.mOpNode.getName());
            return HIERARCHY_12;
        } else if (compareToSetQuartet(setI, setF, setE, setE)) {
            System.out.println(mRvNode2.mOpNode.getName() + " [ " + mRvNode1.mOpNode.getName());
            return HIERARCHY_21;
        }
        System.out.println(mRvNode1.mOpNode.getName() + " ^ " + mRvNode2.mOpNode.getName());
        return OTHER;
    }

    private boolean compareToSetQuartet(Set iRefSet1, Set iRefSet2, Set iRefSet3, Set iRefSet4) {
        return compareSetToRefSet(mRvNode1up, iRefSet1) & compareSetToRefSet(mRvNode1down, iRefSet2) &
                compareSetToRefSet(mRvNode2up, iRefSet3) & compareSetToRefSet(mRvNode2down, iRefSet4);
    }

    private boolean compareSetToRefSet(Set iSetToCompare, Set iReferenceSet) {
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
