package sequenceplanner.multiProduct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.IO.excel.SheetTable;
import sequenceplanner.efaconverter.SEGA;

/**
 *
 * @author patrik
 */
public class OperationResourceDataStructure {

    public Set<Resource> mResourceSet = null;
    public Set<Operation> mOperationSet = null;

    public OperationResourceDataStructure() {
        mResourceSet = new HashSet<Resource>();
        mOperationSet = new HashSet<Operation>();
    }

    public Resource getResourceInSet(final String iResourceName) {
        return getResourceInSet(iResourceName, mResourceSet);
    }

    /**
     * Get a {@link Resource} in set based on name.<br/>
     * @param iResourceName the name of the resource
     * @param iSet the set to look in
     * @return the {@link Resource} or null if no resource in set has iResouceName
     */
    public static Resource getResourceInSet(final String iResourceName, final Set<Resource> iSet) {
        for (final Resource r : iSet) {
            if (r.mName.equals(iResourceName)) {
                return r;
            }
        }
        return null;
    }

    public boolean addResourceWithValues(Map<String, List<Object>> iNameTableMap) {
        for (final String name : iNameTableMap.keySet()) {
            final Resource r = new Resource();
            mResourceSet.add(r);
            r.mName = name;
            r.mVarName = name;

            //Check the list
            List<Object> al = iNameTableMap.get(name);
            if (al.size() != 2 || !(al.get(0) instanceof String) || !(al.get(1) instanceof SheetTable)) {
                System.out.println("addResourceWithValues | error");
                return false;
            }

            //Add values given initially in excel file
            final SheetTable st = (SheetTable) al.get(1);
            for (int i = 0; i < st.getNbrOfRows(); ++i) {
                final String valueName = st.getCellValue(i, 0);
                if (!r.addValue(valueName)) {
                    return false;
                }
            }
            //Add start value
            final String s = (String) al.get(0);
            if (!r.setInitValue(s)) {
                return false;
            }

            System.out.println(name);
        }

        return true;
    }

    public boolean addOperation(final Set<Resource> iResourceSet, final Map<String, String> iDataMap) {

        //Check indata
        if (iDataMap.size() != 6 || !iDataMap.containsKey("name") || !iDataMap.containsKey("source") || !iDataMap.containsKey("dest") ||
                !iDataMap.containsKey("via") || !iDataMap.containsKey("extraSC") || !iDataMap.containsKey("extraFC")) {
            return false;
        }

        //Example:
        //iDataMap.get("source"): T2=4,T3=1
        //iDataMap.get("via"): R1,R2
        //iDataMap.get("dest"): T2=1,T3=2
        Operation op = new Operation();
        mOperationSet.add(op);

        //Set op name (The name is just for the user, and should never be used internally for e.g. comparison)
        op.mName = iDataMap.get("name");

        //Fill op.mSourceResourceMap based on iDataMap.get("source"). Also check that resource exists with given values.
        //For example op.mSourceResourceMap: {[T2,4],[T3,1]}
        final String sourceCondition = iDataMap.get("source");
        if (!addConditionToResourceIntegerMap(sourceCondition, op.mSourceResourceMap, iResourceSet)) {
            return false;
        }

        //Fill op.mDestResourceMap based on iDataMap.get("dest"). Also check that resource exists with given values.
        //For example op.mSourceResourceMap: {[T2,1],[T3,2]}
        final String destCondition = iDataMap.get("dest");
        if (!addConditionToResourceIntegerMap(destCondition, op.mDestResourceMap, iResourceSet)) {
            return false;
        }

        //Two things:
        //1. Create via variable. Also check that resource in iDataMap.get("via") exists.
        //2. Fill op.mViaResourceMap based on iDataMap.get("via"). Also check that resource exists with given value.
        //1. For example:
        //   String viaVarName = T2:4::T3:1_T2:1::T3:2
        //   R1.mValueLL.addLast(viaVarName)
        //   R2.mValueLL.addLast(viaVarName)
        //2. For exmaple:
        //op.mViaResourceMap: {[R1,R1.mValueLL.indexOf(viaVarName)],R2,R2.mValueLL.indexOf(viaVarName)]}
        String viaVarName = sourceCondition.replaceAll("=", ":").replaceAll(",", "::");
        viaVarName += "_" + destCondition.replaceAll("=", ":").replaceAll(",", "::");
        final String viaCondition = iDataMap.get("via");

        if (!addConditionToResourceIntegerMap(viaCondition, op.mViaResourceMap, iResourceSet)) {
            return false;
        }

        if (!createViaVariableAndFillOperationViaMap(viaVarName, op.mViaResourceMap)) {
            return false;
        }

        //Update source resource with via variables
        //For example:
        //T2.mValueViaMap.get(4).get(R1).add(R1.mValueLL.indexOf(viaVarName))
        //T2.mValueViaMap.get(4).get(R2).add(R2.mValueLL.indexOf(viaVarName))
        //T3.mValueViaMap.get(1).get(R1).add(R1.mValueLL.indexOf(viaVarName))
        //T3.mValueViaMap.get(1).get(R2).add(R2.mValueLL.indexOf(viaVarName))
        if (!updateSomeResourceWithSomeValues(op.mSourceResourceMap, op.mViaResourceMap)) {
            return false;
        }

        //Update dest resource with via variables
        //For example:
        //T2.mValueViaMap.get(1).get(R1).add(R1.mValueLL.indexOf(viaVarName))
        //T2.mValueViaMap.get(1).get(R2).add(R2.mValueLL.indexOf(viaVarName))
        //T3.mValueViaMap.get(2).get(R1).add(R1.mValueLL.indexOf(viaVarName))
        //T3.mValueViaMap.get(2).get(R2).add(R2.mValueLL.indexOf(viaVarName))
        if(!updateSomeResourceWithSomeValues(op.mDestResourceMap, op.mViaResourceMap)) {
            return false;
        }

        //Add extra guard and action info to op
        //An example of a start condition (using a vaiable var):
        //var<2/var++ (guard: var<2, action: var++)
        if (!addExtraConditions(op.mExtraStartConditionMap, iDataMap.get("extraSC"))) {
            return false;
        }
        if (!addExtraConditions(op.mExtraFinishConditionMap, iDataMap.get("extraFC"))) {
            return false;
        }

        return true;
    }

    private boolean addExtraConditions(final Map<String, String> iMap, final String iCondition) {
        //a<2/a=1 | guard and action
        //a==4 | guard
        // /b-- | action
        final String[] conditionSplit = iCondition.split("/");

        //Check
        if (conditionSplit.length < 0 || conditionSplit.length > 2) {
            return false;
        }

        //Add conditions
        if (!conditionSplit[0].equals("")) {
            iMap.put("guard", conditionSplit[0]);
        }
        if (conditionSplit.length == 2) {
            iMap.put("action", conditionSplit[1]);
        }

        return true;
    }

    /**
     * To update "source" or "dest" resource with "via" value.<br/>
     * @param iMapResource {@link Operation}.mDestResourceMap or {@link Operation}.mSourceResourceMap
     * @param iMapValue {@link Operation}.mViaResourceMap
     * @return always true
     */
    private boolean updateSomeResourceWithSomeValues(final Map<Resource,Integer> iMapResource, final Map<Resource,Integer> iMapValue) {
        for (Resource resource : iMapResource.keySet()) {
            for (Resource valueResource : iMapValue.keySet()) {
                final Integer valueResourceValue = iMapValue.get(valueResource);
                resource.addToViaResourceValueMap(valueResource, valueResourceValue);
            }
        }
        return true;
    }

    private boolean addConditionToResourceIntegerMap(final String iCondition, final Map<Resource, Integer> iMap, final Set<Resource> iResourceSet) {
        if (iCondition.equals("")) {
            System.out.println("Given condition, " + iCondition + ", is empty!");
            return false;
        }

        for (final String condition : iCondition.split(",")) {

            String resource = condition;
            Integer valueAsInt = null;
            String valueAsString = "";

            if (condition.contains("=")) {
                final String[] condSplit = condition.split("=");
                if (condSplit.length != 2) {
                    System.out.println("Given condition, " + iCondition + ", has wrong syntax!");
                    return false;
                }
                resource = condSplit[0];
                valueAsString = condSplit[1];
            }

            //get resource from set
            final Resource r = OperationResourceDataStructure.getResourceInSet(resource, iResourceSet);
            if (r == null) {
                System.out.println("Given condition, " + iCondition + ", contains resource, " + resource + ", not in resource set!");
                return false;
            }

            if (iMap.containsKey(r)) {
                System.out.println("Value for resource, " + resource + ", given multiple times in the same condition!");
                return false;
            }

            //Mapping of value as text to integer
            if (!valueAsString.equals("")) {
                if (r.mValueLL.contains(valueAsString)) {
                    valueAsInt = Integer.valueOf(r.mValueLL.indexOf(valueAsString));
                } else {
                    valueAsInt = Integer.parseInt(valueAsString);
                }
                if (valueAsInt < 0 || valueAsInt > r.mValueLL.size() - 1) {
                    System.out.println("Given condition, " + iCondition + ", contains value for resource, " + resource + ", not among resource values!");
                    return false;
                }
            }

//            System.out.println("addConditionToResourceIntegerMap | " + "Resource: " + r.mName + ", value: " + valueAsInt);
            iMap.put(r, valueAsInt);
        }
        return true;
    }

    private boolean createViaVariableAndFillOperationViaMap(final String iViaVarName, final Map<Resource, Integer> iMap) {
        if (iViaVarName.equals("")) {
            return false;
        }

        for (final Resource r : iMap.keySet()) {
            //add value to resource
            r.mValueLL.add(iViaVarName);

            //add value to operation
            iMap.put(r, Integer.valueOf(r.mValueLL.indexOf(iViaVarName)));
        }
        return true;
    }

    public class GeneralDataStructureObject {

        public String mName;
    }

    public class Resource extends GeneralDataStructureObject {

        public String mVarName;
//        public Map<Integer, Map<Resource, Set<Integer>>> mValueViaMap = null;
        /**
         * ASSUMES SINGLE CAPACITY RESOURCE!!!!
         * Resources with values that either enter or exit from this resource.<br/>
         * I.e. The system can not be in this resource at the same time as one of the resoruces in keyset has a value in their set of values.<br/>
         */
        public Map<Resource, Set<Integer>> mViaResourceValueMap = null;
        /**
         * Values for the resource.<br/>
         * The integer value for a value is the same as position in list.<br/>
         */
        public LinkedList<String> mValueLL = null;
        /**
         * Inititial and single marked value for the resource.
         */
        public String mInitValue = "";

        public Resource() {
            mValueLL = new LinkedList<String>();
            mViaResourceValueMap = new HashMap<Resource, Set<Integer>>();
        }

        public boolean addToViaResourceValueMap(final Resource iViaResource, final Integer iViaResourceValue) {
            if (iViaResourceValue < 0 || iViaResourceValue > (iViaResource.mValueLL.size() - 1)) {
                return false;
            }

            //Check if this is first time and take action if it is
            if (!mViaResourceValueMap.containsKey(iViaResource)) {
                mViaResourceValueMap.put(iViaResource, new HashSet<Integer>());
            }

            //Add new via value
            mViaResourceValueMap.get(iViaResource).add(iViaResourceValue);

            return true;
        }

//        public boolean addToValueViaMap(final Integer iValue, final Resource iViaResource, final Integer iViaResourceValue) {
//            if (iValue < 0 || iValue > (mValueLL.size() - 1)) {
//                return false;
//            }
//            if (iViaResourceValue < 0 || iViaResourceValue > (iViaResource.mValueLL.size() - 1)) {
//                return false;
//            }
//
//            //Check if this is first time and take action if it is
//            Map<Resource, Set<Integer>> map = mValueViaMap.get(iValue);
//            if (map == null) {
////                System.out.println("addToValueViaMap | iValue: " + iValue + ", resource: " + mName + " is null");
//                map = new HashMap<Resource, Set<Integer>>();
//            }
//            if (!map.containsKey(iViaResource)) {
//                map.put(iViaResource, new HashSet<Integer>());
//            }
//
//            map.get(iViaResource).add(iViaResourceValue);
//            mValueViaMap.put(iValue, map);
//
//            return true;
//        }
        /**
         * A a value to list of values.<br/>
         * @param iValueName the value to add
         * @return true if added, false if value already in the list
         */
        public boolean addValue(String iValueName) {
            if (mValueLL.contains(iValueName)) {
                System.out.println("Resource " + mName + " already has a value with name " + iValueName);
                return false;
            }
            mValueLL.addLast(iValueName);
            return true;
        }

        public boolean setInitValue(final String iInitValueName) {
            if (iInitValueName.equals("")) {
                System.out.println("Resource " + mName + " has not been given an init/marked value!");
                return false;
            }
            if (!mValueLL.contains(iInitValueName)) {
                System.out.println("Resource " + mName + " does not have the given init/marked value, " + iInitValueName + ", among it's values!");
                return false;
            }

            mInitValue = iInitValueName;
            return true;
        }

        @Override
        public String toString() {
            String returnString = mName + ": {";
            for (String s : mValueLL) {
                if (!mValueLL.getFirst().equals(s)) {
                    returnString += ",";
                }
                returnString += "s";
            }
            return returnString + "}";
        }
    }

    public class Operation extends GeneralDataStructureObject {

        /**
         * E.g. a keyset={"guard","action"}, To add e.g. limit for product.
         */
        public Map<String, String> mExtraStartConditionMap;
        /**
         * E.g. a keyset={"guard","action"}, To add e.g. limit for product.
         */
        public Map<String, String> mExtraFinishConditionMap;
        /**
         * The operation can start when the resources in keyset has values given as valueset.<br/>
         */
        public Map<Resource, Integer> mSourceResourceMap;
        /**
         * The operation uses the resources in keyset with values in valueset only in execution location.<br/>
         */
        public Map<Resource, Integer> mViaResourceMap;
        /**
         * The operation finishes with the resources in keyset set to valueset.<br/>
         */
        public Map<Resource, Integer> mDestResourceMap;

        public Operation() {
            mSourceResourceMap = new HashMap<Resource, Integer>();
            mDestResourceMap = new HashMap<Resource, Integer>();
            mViaResourceMap = new HashMap<Resource, Integer>();
            mExtraStartConditionMap = new HashMap<String, String>();
            mExtraFinishConditionMap = new HashMap<String, String>();
        }

        public void printResourceIntegerMap(Map<Resource, Integer> iMap) {
            for (Resource r : iMap.keySet()) {
                System.out.println("printResourceIntegerMap| " + "Resource: " + r.mName + ", Value: " + iMap.get(r).toString());
            }
        }

        public void startGuard(SEGA ioSEGA) {
            //Include set to enter resources that has been given a fixt value.<br/>
            //E.g. robot2==0, then a later entry robot2!=3 makes no difference.
            Set<Resource> fixValueResoureSet = new HashSet<Resource>();

            //in X
            //loop mSourceResourceMap. Set key.mVarName == value
            for (final Resource r : mSourceResourceMap.keySet()) {
                final String varName = r.mVarName;
                final Integer value = mSourceResourceMap.get(r);
                ioSEGA.andGuard(varName + "==" + value);
                fixValueResoureSet.add(r);
            }

            //Include temp set in order to perform set minus without affecting the orignal set.
            Set<Resource> subSet;

            //CHANGE WHEN INCLUDE RESOURCES WITH CAPACITY GREATER THAN 1
            //Resources in Z has enough capacity right now (This step makes no sense when all resources has capacity =1. It is included for later extension.)
            //loop mViaResourceMap keyset. Set key.mVarName <max capacity if key not in keyset for mSourceResourceMap
            subSet = new HashSet<Resource>(mViaResourceMap.keySet());
            subSet.removeAll(mSourceResourceMap.keySet());
            subSet.removeAll(fixValueResoureSet);
            for (final Resource r : subSet) {
                final String varName = r.mVarName;
                final Integer value = 0;
                ioSEGA.andGuard(varName + "==" + value);
                fixValueResoureSet.add(r);
            }

            //CHANGE WHEN INCLUDE RESOURCES WITH CAPACITY GREATER THAN 1
            //Resources in Y has enough capacity right now
            //loop mDestResourceMap keyset. Set key.mVarName ==0 if key not in keyset for mSourceResourceMap
            subSet = new HashSet<Resource>(mDestResourceMap.keySet());
            subSet.removeAll(mSourceResourceMap.keySet());
            subSet.removeAll(fixValueResoureSet);
            for (final Resource r : subSet) {
                final String varName = r.mVarName;
                final Integer value = 0;
                ioSEGA.andGuard(varName + "==" + value);
                fixValueResoureSet.add(r);
            }


            //CHANGE CHAGNE CHANGE NEED TO ONLY ADD EACH GUARD ONE TIME IT CAN APPER IN ALL DEST RESOURCES...
            //via for Y is not taken
            //loop mDestResourceMap keyset. Set key.mViaResourceValueMap loop key'.mVarName != value' (key' and value' in mViaResourceValueMap in key resource.
            for (final Resource r : mDestResourceMap.keySet()) {
                final Map<Resource, Set<Integer>> viaResourceIntegerMap = r.mViaResourceValueMap;
                subSet = new HashSet<Resource>(viaResourceIntegerMap.keySet());
                subSet.removeAll(fixValueResoureSet);
                for (final Resource rIn : subSet) {
                    final String varName = rIn.mVarName;
                    for (final Integer valueIn : viaResourceIntegerMap.get(rIn)) {
                        ioSEGA.andGuard(varName + "!=" + valueIn);
                    }
                }
            }

            //Limit
            //mExtraStartConditionMap.get("guard") using addExtraCondtion()
            addExtraCondtion(mExtraStartConditionMap, "guard", ioSEGA);
        }

        public void startAction(SEGA ioSEGA) {
            //set Z
            //loop mViaResourceMap keyset. Set key.mVarName = value
            for (final Resource r : mViaResourceMap.keySet()) {
                final String varName = r.mVarName;
                final Integer value = mViaResourceMap.get(r);
                ioSEGA.addAction(varName + "=" + value);
            }

            //Include temp set in order to perform set minus without affecting the orignal set.
            Set<Resource> subSet;

            //unbook X
            //loop mSourceResourceMap keyset. Set key.mVarName = 0, minus resources set to use in Z and Y
            subSet = new HashSet<Resource>(mSourceResourceMap.keySet());
            subSet.removeAll(mViaResourceMap.keySet());
            subSet.removeAll(mDestResourceMap.keySet());
            for (final Resource r : subSet) {
                final String varName = r.mVarName;
                final Integer value = 0;
                ioSEGA.addAction(varName + "=" + value);
            }

            //Limit
            //mExtraStartConditionMap.get("action")
            addExtraCondtion(mExtraStartConditionMap, "action", ioSEGA);
        }

        public void finishGuard(SEGA ioSEGA) {
            //in Z
            //loop mViaResourceMap keyset. Set key.mVarName == value
            for (final Resource r : mViaResourceMap.keySet()) {
                final String varName = r.mVarName;
                final Integer value = mViaResourceMap.get(r);
                ioSEGA.andGuard(varName + "==" + value);
            }

            //mExtraFinishConditionMap.get("guard")
            addExtraCondtion(mExtraFinishConditionMap, "guard", ioSEGA);
        }

        public void finishAction(SEGA ioSEGA) {
            //set Y
            //loop mDestResourceMap keyset. Set key.mVarName = value
            for (final Resource r : mDestResourceMap.keySet()) {
                final String varName = r.mVarName;
                final Integer value = mDestResourceMap.get(r);
                ioSEGA.addAction(varName + "=" + value);
            }

            //Include temp set in order to perform set minus without affecting the orignal set.
            Set<Resource> subSet;

            //unbook Z
            //loop mViaResourceMap keyset. Set key.mVarName = 0, minus resources set to use in Y
            subSet = new HashSet<Resource>(mViaResourceMap.keySet());
            subSet.removeAll(mDestResourceMap.keySet());
            for (final Resource r : subSet) {
                final String varName = r.mVarName;
                final Integer value = 0;
                ioSEGA.addAction(varName + "=" + value);
            }
            
            //Limit
            //mExtraFinishConditionActionMap.get("action")
            addExtraCondtion(mExtraFinishConditionMap, "action", ioSEGA);
        }

        private boolean addExtraCondtion(final Map<String, String> iMap, final String iKey, SEGA ioSEGA) {
            if (iMap != null) {
                if (iMap.containsKey(iKey)) {
                    if (iKey.equals("guard")) {
                        ioSEGA.andGuard(iMap.get(iKey));
                    } else { //"action"
                        ioSEGA.addAction(iMap.get(iKey));
                    }
                }
            }

            return true;
        }
    }
}
