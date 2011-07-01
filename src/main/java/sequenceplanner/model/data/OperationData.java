   // KB 100629
// We must rewrite all these data classes! We should introduce a class
// that is used both for pre, post and restconditions. There should be
// no difference! They should include both guards and actions.
// We should also use interfaces for all datastructures to be able to
// use injection!
package sequenceplanner.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.DataToConditionHelper;

import sequenceplanner.efaconverter.EFAVariables;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;

/**
 *
 * @author erik
 */
public class OperationData extends Data {

    public static final String OPERATION_NAME = "name";
    public static final String OP_PRECONDITION = "precond";
    public static final String OP_POSTCONDITION = "postcond";
    public static final String OP_COST = "cost";
    public static final String OP_PREOPERATION = "preoperation";
    public static final String OP_POSTOPERATION = "postoperation";
    public static final String OP_INVARIANT = "invariant";
    public static final String OP_DESCRIPTION = "descr";
    public static final String OP_REALIZEDBY = "realized";
    public static final String OP_ACCOMPLISHES = "acc";
    private Map<String, String> preference;
    public static final int RESOURCE_BOOK = 1;
    public static final int RESOURCE_UNBOOK = 0;
    public static final int ACTION_ADD = 0;
    public static final int ACTION_DEC = 1;
    public static final int ACTION_EQ = 2;
    //Maps the pre and post conditions according to ConditionType, Condition
    //String should be name of OperationView
    //Holds the sequencecondition for this operation
    LinkedList<LinkedList<SeqCond>> sequenceCondition;
    LinkedList<Integer[]> resourceBooking;
    //PostCondtions
    LinkedList<LinkedList<SeqCond>> pSequenceCondition;
    LinkedList<Integer[]> pResourceBooking;
    //Invariants
    LinkedList<LinkedList<SeqCond>> seqInvariant;
    //Actions
    LinkedList<Action> actions;
    //Properties (Key = id, value = selected for operation)
    HashMap<Integer, Boolean> propertySettings;
    private Map<String, Map<ConditionType, Condition>> globalConditions;
    private Map<ConditionType, Condition> localConditions;

    //OperationData newOp = new OperationData(OP,model.getNewId());
    public OperationData(String name, int id) {
        super(name, id);
        preference = Collections.synchronizedMap(new HashMap<String, String>());

        globalConditions = Collections.synchronizedMap(new HashMap<String, Map<ConditionType, Condition>>());
        localConditions = new HashMap<ConditionType, Condition>();
        //Resource booking
        sequenceCondition = new LinkedList<LinkedList<SeqCond>>();
        resourceBooking = new LinkedList<Integer[]>();

        //Postconditions
        pSequenceCondition = new LinkedList<LinkedList<SeqCond>>();
        pResourceBooking = new LinkedList<Integer[]>();

        //Invariants
        seqInvariant = new LinkedList<LinkedList<SeqCond>>();

        //Actions
        actions = new LinkedList<Action>();

        //Properties
        propertySettings = new HashMap<Integer, Boolean>();
    }

    public void setConditions(Map<ConditionType, Condition> conditionMap, String operationViewName) {
        this.globalConditions.put(operationViewName, conditionMap);
        System.out.println(globalConditions);
        //this.setChanged();
//        this.notifyObservers(this);
    }


    private void setValue(String key, String value) {
        if (key != null && value != null) {
            preference.put(key, value);
        } else {
            System.out.println("Error in UserFile: You shall not pass null values for key " + key);
        }
    }

    public Map<String, String> getPreferences() {
        return preference;
    }

    private String getValue(String key) {
        String s = preference.get(key);

        return s == null ? "" : s;
    }

    public void setPrecondition(String value) {
        setValue(OP_PRECONDITION, value);
    }

    public String getPrecondition() {
        return getValue(OP_PRECONDITION);
    }

    public void setPostcondition(String value) {
        setValue(OP_POSTCONDITION, value);
    }

    public String getPostcondition() {
        return getValue(OP_POSTCONDITION);
    }

    public void setInvariant(String value) {
        setValue(OP_INVARIANT, value);
    }

    public String getInvariant() {
        return getValue(OP_INVARIANT);
    }

    public void setDescription(String value) {
        setValue(OP_DESCRIPTION, value);
    }

    public String getDescription() {
        return getValue(OP_DESCRIPTION);
    }

    public void setPreoperation(boolean isPreoperation) {
        setValue(OP_PREOPERATION, Boolean.toString(isPreoperation));
    }

    public boolean isPreoperation() {
        return Boolean.parseBoolean(getValue(OP_PREOPERATION));
    }

    public void setPostoperation(boolean isPostoperation) {
        setValue(OP_POSTOPERATION, Boolean.toString(isPostoperation));
    }

    public boolean isPostoperation() {
        return Boolean.parseBoolean(getValue(OP_POSTOPERATION));
    }

    public void setCost(double cost) {
        setValue(OP_COST, Double.toString(cost));
    }

    public double getCost() {
        try {
            return Double.parseDouble(getValue(OP_COST));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setRealizedBy(int id) {
        setValue(OP_REALIZEDBY, Integer.toString(id));
    }

    public int getRealizedBy() {
        try {
            return Integer.parseInt(getValue(OP_REALIZEDBY));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setAccomplishes(int id) {
        setValue(OP_ACCOMPLISHES, Integer.toString(id));
    }

    public int getAccomplishes() {
        try {
            return Integer.parseInt(getValue(OP_ACCOMPLISHES));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // -----------------------
    // TO HANDLE SEQUENCECONDITIONS
    /**
     * Set sequence preconditions
     * @param cond 
     */
    public void setSequenceCondition(LinkedList<LinkedList<SeqCond>> cond) {
        this.sequenceCondition = cond;

    }

    public LinkedList<LinkedList<SeqCond>> getSequenceCondition() {
        return sequenceCondition;
    }

    public LinkedList<Action> getActions() {
        return actions;
    }

    public void setActions(LinkedList<Action> actions) {
        this.actions = actions;
    }

    public LinkedList<LinkedList<SeqCond>> getSeqInvariant() {
        return seqInvariant;
    }

    public void setSeqInvariant(LinkedList<LinkedList<SeqCond>> invariant) {
        this.seqInvariant = invariant;
    }

    public LinkedList<Integer[]> getPResourceBooking() {
        return pResourceBooking;
    }

    public void setPResourceBooking(LinkedList<Integer[]> pResourceBooking) {
        this.pResourceBooking = pResourceBooking;
    }

    /**
     * Get sequence postconditions
     * @return Linkedlist likedlists containing sequencepostconditions
     */
    public LinkedList<LinkedList<SeqCond>> getPSequenceCondition() {
        return pSequenceCondition;
    }

    /**
     * Set sequence postconditions
     * @param cond 
     */
    public void setPSequenceCondition(LinkedList<LinkedList<SeqCond>> pSequenceCondition) {
        this.pSequenceCondition = pSequenceCondition;
        DataToConditionHelper.extractPost(this);
    }

    //TO HANDLE PROPERTY SETTINGS
    public void setProperty(int id, boolean selected) {
        propertySettings.put(id, selected);
    }

    public void setProperties(HashMap<Integer, Boolean> propertySettings) {
        this.propertySettings = propertySettings;

    }

    public HashMap<Integer, Boolean> getProperties() {
        return propertySettings;
    }

    public boolean isPropertySet(int id) {
        boolean res = false;
        if (propertySettings.containsKey(id)) {
            res = propertySettings.get(id);
        }
        return res;
    }

    public boolean isSequence(int id) {

        for (LinkedList<SeqCond> linkedList : sequenceCondition) {
            if (linkedList.size() == 1 && linkedList.getFirst().id == id && linkedList.getFirst().state == 2) {
                return true;
            }
        }

        return false;
    }

    public boolean isPredecessor(int id) {
        for (LinkedList<SeqCond> linkedList : sequenceCondition) {
            for (SeqCond sc : linkedList) {
                if (sc.isOperationCheck() && sc.id == id && sc.state == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPredecessorsOR(int id, int id2) {
        for (LinkedList<SeqCond> linkedList : sequenceCondition) {
            boolean foundID = false, foundID2 = false;
            for (SeqCond sc : linkedList) {
                foundID = (sc.isOperationCheck() && sc.id == id && sc.state == 2) ? true : foundID;
                foundID2 = (sc.isOperationCheck() && sc.id == id2 && sc.state == 2) ? true : foundID2;
            }
            if (foundID && foundID2) {
                return true;
            }
        }
        return false;
    }

    public boolean isPredecessorsAND(int id, int id2) {
        boolean foundID = false, foundID2 = false;
        for (LinkedList<SeqCond> linkedList : sequenceCondition) {
            for (SeqCond sc : linkedList) {
                if (sc.isOperationCheck() && sc.id == id && sc.state == 2) {
                    foundID = true;
                    break;
                } else if (sc.isOperationCheck() && sc.id == id2 && sc.state == 2) {
                    foundID2 = true;
                    break;
                }
            }
        }
        if (foundID && foundID2) {
            return true;
        } else {
            return false;
        }
    }

    public LinkedList<LinkedList<Integer>> getPredecessors() {
        LinkedList<LinkedList<Integer>> result = new LinkedList<LinkedList<Integer>>();
        for (LinkedList<SeqCond> linkedList : sequenceCondition) {
            LinkedList<Integer> orPred = new LinkedList<Integer>();
            for (SeqCond sc : linkedList) {
                if (sc.isOperationCheck() && sc.state == 2) {
                    orPred.add(sc.id);
                }
            }
            if (!orPred.isEmpty()) {
                result.add(orPred);
            }
        }
        return result;
    }

    public boolean addAnd(int id, int state) {
        return addAnd(new SeqCond(id, state));
    }

   public boolean addPAnd(int id, int state) {
      return addPAnd(new SeqCond(id, state));
   }

    public boolean removeAnd(int id, int state) {
        return removeAnd(new SeqCond(id, state));
    }

   public boolean removePAnd(int id, int state) {
      return removePAnd(new SeqCond(id, state));
   }

    public boolean removeAnd(SeqCond and) {
        for (Iterator<LinkedList<SeqCond>> it = sequenceCondition.iterator(); it.hasNext();) {
            LinkedList<SeqCond> list = it.next();
            if (list.size() == 1 && list.getFirst().id == and.id && list.getFirst().state == and.state) {
                sequenceCondition.remove(list);
                return true;
            }
        }
        return false;
    }

   public boolean removePAnd(SeqCond and) {
      for (Iterator<LinkedList<SeqCond>> it = pSequenceCondition.iterator(); it.hasNext();) {
         LinkedList<SeqCond> list = it.next();
         if (list.size() == 1 && list.getFirst().id == and.id && list.getFirst().state == and.state) {
            pSequenceCondition.remove(list);
            return true;
         }
      }
      return false;
   }
   
    public boolean addAnd(SeqCond and) {
        for (Iterator<LinkedList<SeqCond>> it = sequenceCondition.iterator(); it.hasNext();) {
            LinkedList<SeqCond> list = it.next();
            if (list.size() == 1 && list.getFirst().id == and.id && list.getFirst().state == and.state) {
                return false;
            }
        }
        LinkedList<SeqCond> s = new LinkedList<SeqCond>();
        s.add(and);

        sequenceCondition.add(s);
        return true;
    }

   public boolean addPAnd(SeqCond and) {
      for (Iterator<LinkedList<SeqCond>> it = pSequenceCondition.iterator(); it.hasNext();) {
         LinkedList<SeqCond> list = it.next();
         if (list.size() == 1 && list.getFirst().id == and.id && list.getFirst().state == and.state) {
            return false;
         }
      }
      LinkedList<SeqCond> s = new LinkedList<SeqCond>();
      s.add(and);

      pSequenceCondition.add(s);
      return true;
   }
   
    public boolean addOr(LinkedList<SeqCond> or) {
        sequenceCondition.add(or);
        return true;
    }

   public boolean addPOr(LinkedList<SeqCond> or) {
      pSequenceCondition.add(or);
      return true;
   }

    public boolean addResourceBooking(int resource) {
        if (!isResourceBooked(resource)) {
            resourceBooking.add(new Integer[]{resource, RESOURCE_BOOK});
        }
        return false;
    }

   public boolean addPResourceBooking(int resource) {
      if (!isResourceBooked(resource)) {
         pResourceBooking.add(new Integer[]{resource, RESOURCE_BOOK});
      }
      return false;
   }

    public boolean removeResourceBooking(int resource) {
        Integer[] rem = null;

        for (Integer[] bo : resourceBooking) {
            if (bo[0] == resource && bo[1] == RESOURCE_BOOK) {
                rem = bo;
                break;
            }
        }

        resourceBooking.remove(rem);

        return false;
    }

   public boolean removePResourceBooking(int resource) {
      Integer[] rem = null;

      for (Integer[] bo : pResourceBooking) {
         if (bo[0] == resource && bo[1] == RESOURCE_BOOK) {
            rem = bo;
            break;
         }
      }

      pResourceBooking.remove(rem);

      return false;
   }

    public boolean isResourceBooked(int resource) {

        for (Integer[] bo : resourceBooking) {
            if (bo[0] == resource && bo[1] == RESOURCE_BOOK) {
                return true;
            }
        }

        return false;
    }

    public LinkedList<Integer[]> getResourceBooking() {
        return resourceBooking;
    }

    public void setResourceBooking(LinkedList<Integer[]> l) {
        resourceBooking = l;
    }

    @Override
    public String toString() {
        return getValue(OPERATION_NAME);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        } else if (obj instanceof OperationData) {
            OperationData data = (OperationData) obj;


            // Check all string preferences
            // -------------------------------
            if (isPreferencesEqual(preference, data.getPreferences()) //Check preconditions
                    && isResourceBookingEqual(data.getResourceBooking(), resourceBooking) && isPreconditionEqual(data.getSequenceCondition(), sequenceCondition) //Check postconditions
                    && isResourceBookingEqual(data.getPResourceBooking(), pResourceBooking) && isPreconditionEqual(data.getPSequenceCondition(), pSequenceCondition) //Check invariant
                    && isPreconditionEqual(data.getSeqInvariant(), seqInvariant) && isListEqual(data.getActions(), actions)) {


                return true;
            }
        }

        return false;
    }

    protected boolean isPreconditionEqual(LinkedList<LinkedList<SeqCond>> one, LinkedList<LinkedList<SeqCond>> two) {

        if (one.size() != two.size()) {
            return false;
        }

        for (LinkedList<SeqCond> listOne : one) {
            boolean present = false;

            for (LinkedList<SeqCond> listTwo : two) {
                if (isListEqual(listOne, listTwo)) {
                    present = true;
                    break;
                }
            }

            if (!present) {
                return false;
            }

        }

        return true;
    }

    public boolean isListEqual(LinkedList one, LinkedList two) {
        if (one.size() != two.size()) {
            return false;
        }

        for (Object s1 : one) {
            boolean present = false;

            for (Object s2 : two) {
                if (s1.equals(s2)) {
                    present = true;
                    break;
                }
            }

            if (!present) {
                return false;
            }
        }

        return true;
    }

    protected boolean isPreferencesEqual(Map<String, String> one, Map<String, String> two) {

        Set<String> keys = one.keySet();

        if (two.size() != one.size()) {
            return false;
        }

        for (String key : keys) {
            String s1 = one.get(key);
            String s2 = two.get(key);

            if (s1 != null && s2 != null) {
                if (!s1.equals(s2)) {
                    return false;
                }
            } else if (s1 != s2) {
                return false;
            }
        }
        return true;
    }

    public boolean isResourceBookingEqual(LinkedList<Integer[]> one, LinkedList<Integer[]> two) {
        if (one.size() != two.size()) {
            return false;
        }

        for (Integer[] intRes : one) {
            boolean present = false;

            for (Integer[] intRes2 : two) {
                if ((intRes[0] == intRes2[0]) && intRes[1] == intRes2[1]) {
                    present = true;
                }
            }

            if (!present) {
                return false;
            }
        }

        return true;
    }

    /**
     * Clone everything except id that is set to -1.
     * @return
     */
    @Override
    public Object clone() {
        OperationData ret = new OperationData(this.getName(), getId());

        if (getCopy()) {
            ret.setId(Model.newId());
            ret.setName(ret.getName() + "_" + ret.getId());
        }


        Set<String> keys = preference.keySet();

        for (String key : keys) {
            ret.setValue(key, getValue(key));
        }

        //Copy preconditions
//        sequenceCondition


        ret.setSequenceCondition(cloneSequenceConditions(sequenceCondition));
        ret.setPSequenceCondition(cloneSequenceConditions(pSequenceCondition));

        ret.setSeqInvariant(cloneSequenceConditions(seqInvariant));

        ret.setResourceBooking(cloneResources(resourceBooking));
        ret.setPResourceBooking(cloneResources(pResourceBooking));

        ret.setActions(cloneActions(actions));
        ret.setProperties(clonePropertySettings(propertySettings));

        return ret;
    }

    protected LinkedList<LinkedList<SeqCond>> cloneSequenceConditions(LinkedList<LinkedList<SeqCond>> seq) {
        LinkedList<LinkedList<SeqCond>> tempSeq = new LinkedList<LinkedList<SeqCond>>();
        for (LinkedList<SeqCond> linkedList : seq) {
            LinkedList<SeqCond> l = new LinkedList<SeqCond>();
            for (SeqCond seqCond : linkedList) {
                l.add((SeqCond) seqCond.clone());
            }
            tempSeq.add(l);
        }

        return tempSeq;
    }

    protected LinkedList<Integer[]> cloneResources(LinkedList<Integer[]> res) {
        LinkedList<Integer[]> tempRes = new LinkedList<Integer[]>();
        for (Integer[] integers : res) {
            Integer[] tempI = new Integer[2];
            tempI[0] = integers[0];
            tempI[1] = integers[1];
            tempRes.add(tempI);
        }
        return tempRes;
    }

    protected LinkedList<Action> cloneActions(LinkedList<Action> act) {
        LinkedList<Action> tempRes = new LinkedList<Action>();

        for (Action action : act) {
            tempRes.add((Action) action.clone());
        }
        return tempRes;
    }

    protected HashMap<Integer, Boolean> clonePropertySettings(HashMap<Integer, Boolean> properties) {
        HashMap<Integer, Boolean> tempRes = new HashMap<Integer, Boolean>();

        for (Integer id : properties.keySet()) {
            tempRes.put(id, (Boolean) properties.get(id));
        }
        return tempRes;
    }

    /**
     *
     * @return Precondtion (SequenceCondition + ResourceBooking) with id instead of name.
     */
    public String getRawPrecondition() {
        return getRawSequenceCondition(sequenceCondition, resourceBooking);
    }

    /**
     *
     * @return Postcondition (SequenceCondition + ResourceBooking) with id instead of name.
     */
    public String getRawPostcondition() {
        return getRawSequenceCondition(pSequenceCondition, pResourceBooking);
    }

    /**
     *
     * @return Invariant (SequenceCondition + ResourceBooking) with id instead of name.
     */
    public String getRawInvariant() {
        return getRawSequenceCondition(seqInvariant, null);
    }

    public String[] getRawActions() {

        ArrayList<String> result = new ArrayList<String>();

        for (Action action : actions) {
            result.add(Integer.toString(action.id)
                    + Model.getActionSetType(action.state)
                    + Integer.toString(action.value));
        }

        return result.toArray(new String[0]);
    }

    public String getRawSequenceCondition(LinkedList<LinkedList<SeqCond>> sequenceCond, LinkedList<Integer[]> resource) {
        String pre = "";

        for (LinkedList<SeqCond> linkedList : sequenceCond) {
            pre = pre.isEmpty() ? pre : pre + " " + EFAVariables.SP_AND + " ";
            pre += getRawOr(linkedList);
        }


        if (resource != null) {
            for (Iterator it = resource.iterator(); it.hasNext();) {
                Integer[] out = (Integer[]) it.next();


                pre = pre.isEmpty() ? pre : pre + " " + EFAVariables.SP_AND + " ";
                pre += out[0] + Model.getResourceEnding(out[1]);

            }
        }

        return pre;
    }

    public String getRawOr(LinkedList<SeqCond> s) {
        String out = "";

        if (s.size() == 1) {
            SeqCond seqCond = s.getFirst();

            if (seqCond.isOperationCheck()) {
                out += Integer.toString(seqCond.id) + Model.getOperationEnding(seqCond.state);
            } else if (seqCond.isVariableCheck()) {
                out += Integer.toString(seqCond.id) + Model.getVariabelCheck(seqCond.state) + Integer.toString(seqCond.value);
            }


        } else if (s.size() > 1) {
            out += "(";
            for (Iterator<SeqCond> it = s.iterator(); it.hasNext();) {
                SeqCond seqCond = it.next();

                if (seqCond.isOperationCheck()) {
                    out += Integer.toString(seqCond.id) + Model.getOperationEnding(seqCond.state);
                } else if (seqCond.isVariableCheck()) {
                    out += Integer.toString(seqCond.id) + Model.getVariabelCheck(seqCond.state) + Integer.toString(seqCond.value);
                }

                out = it.hasNext() ? out + EFAVariables.SP_OR : out;

            }
            out += ")";
        }

        return out;
    }

    //************************************************************************
    // New methods by KB 100629
    // We must rewrite all these data Classes! We should introduce a class
    // that is used both for pre, post and restconditions. There should be
    // no difference! They should include both guards and actions
    /**
     * Returns a Set of id of the variables used as guards in the precondition
     */
    public Set<Integer> getPreCondVariableGuards() {
        HashSet<Integer> ids = new HashSet<Integer>();

        for (LinkedList<SeqCond> preCond : this.sequenceCondition) {
            for (SeqCond s : preCond) {
                if (s.isVariableCheck()) {
                    ids.add(s.id);
                }
            }
        }

        // The Integer[] stores the resource booking in the form
        // id[0] stores id, id[1] = 1 booking resource, id[1]=0 unbooking
        // Observe that these variables are both guards and actions!
        for (Integer[] id : this.resourceBooking) {
            ids.add(id[0]);
        }

        return ids;
    }

    /**
     * Returns a Set of id of the variables used as action in the precondition
     *
     */
    public Set<Integer> getPreCondVariableActions() {
        HashSet<Integer> ids = new HashSet<Integer>();

        for (Action action : this.actions) {
            ids.add(action.id);
        }

        // The Integer[] stores the resource booking in the form
        // id[0] stores id, id[1] = 1 booking resource, id[1]=0 unbooking
        // Observe that these variables are both guards and actions!
        for (Integer[] id : this.resourceBooking) {
            ids.add(id[0]);
        }

        return ids;
    }

    /**
     * Returns a Set of id of the variables used as guards in the postcondition
     */
    public Set<Integer> getPostCondVariableGuards() {
        HashSet<Integer> ids = new HashSet<Integer>();

        for (LinkedList<SeqCond> preCond : this.pSequenceCondition) {
            for (SeqCond s : preCond) {
                if (s.isVariableCheck()) {
                    ids.add(s.id);
                }
            }
        }

        for (Integer[] id : this.pResourceBooking) {
            ids.add(id[0]);
        }

        return ids;
    }

    /**
     * Returns a Set of id of the variables used as actions in the postcondition
     * In  version SP1 actions can not be assigned to postcondition! Will change!
     * This method therefore only returns resource booking.
     *
     */
    public Set<Integer> getPostCondVariableActions() {
        HashSet<Integer> ids = new HashSet<Integer>();

        for (Integer[] id : this.pResourceBooking) {
            ids.add(id[0]);
        }

        return ids;
    }

    public Set<Integer> getPreCondOperations() {
        HashSet<Integer> ids = new HashSet<Integer>();

        for (LinkedList<SeqCond> preCond : this.sequenceCondition) {
            for (SeqCond s : preCond) {
                if (s.isOperationCheck()) {
                    ids.add(s.id);
                }
            }
        }
        return ids;
    }

    public Set<Integer> getPostCondOperations() {
        HashSet<Integer> ids = new HashSet<Integer>();

        for (LinkedList<SeqCond> preCond : this.pSequenceCondition) {
            for (SeqCond s : preCond) {
                if (s.isOperationCheck()) {
                    ids.add(s.id);
                }
            }
        }
        return ids;
    }

    /**
     * Checks if this operation is relating to the the state (init=0, exe=1, fin=2)
     * of the operation with id == id.
     * @param id The id of the other operation
     * @return True if this operation is relating to the execute state of the other op.
     */
    public boolean isRelatingToState(int id, int state) {
        for (LinkedList<SeqCond> linkedList : sequenceCondition) {
            for (SeqCond sc : linkedList) {
                if (sc.isOperationCheck() && sc.id == id && sc.state == state) {
                    return true;
                }
            }
        }
        for (LinkedList<SeqCond> linkedList : pSequenceCondition) {
            for (SeqCond sc : linkedList) {
                if (sc.isOperationCheck() && sc.id == id && sc.state == state) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, Map<ConditionType, Condition>> getGlobalConditions() {
        return globalConditions;
    }
    

    //************************************************************************
    // Se Model.getVariabelCheck for state info on variables
    public static class SeqCond implements Cloneable {

        final public int id;
        final public int state;
        public int value;
        //Variable or Other resource
        final private int type;

        public SeqCond(int id, int state) {
            this.type = 0;
            this.id = id;
            this.state = state;
        }

        //Variable
        public SeqCond(int id, int equal, int value) {
            this.type = 1;
            this.id = id;
            this.state = equal;
            this.value = value;
        }

        public boolean isOperationCheck() {
            return type == 0;
        }

        public boolean isVariableCheck() {
            return this.type == 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SeqCond) {
                SeqCond t = (SeqCond) obj;

                if (t.id == id && t.state == state) {

                    if ((isVariableCheck() && t.isVariableCheck() && value == t.value) || (isOperationCheck() && t.isOperationCheck())) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Object clone() {

            if (isVariableCheck()) {
                return new SeqCond(id, state, value);
            } else if (isOperationCheck()) {
                return new SeqCond(id, state);
            } else {
                return null;
            }

        }
    }

    public static class Action implements Cloneable {

        final public int id;
        // The state is 0 -> +=, 1-> -=, 2-> =
        // These are controlled by constant at the top of this file (
        // (ACTION_ADD, ...) I (kb) think they are related to a dropbox in
        // the gui!
        // Use Model.getActionSetType(state) to get correct.
        final public int state;
        final public int value;

        public Action(int id, int state, int value) {
            this.id = id;
            this.state = state;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Action) {
                Action t = (Action) obj;

                if (t.id == id && t.state == state && t.value == value) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object clone() {
            return new Action(id, state, value);
        }
    }
}
