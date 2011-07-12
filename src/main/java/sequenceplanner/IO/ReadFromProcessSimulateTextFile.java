package sequenceplanner.IO;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.condition.AStringToConditionParser;
import sequenceplanner.condition.ActionAsTextInputToConditionParser;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.GuardAsTextInputToConditionParser;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * To read from a text file
 * @author patrik
 */
public class ReadFromProcessSimulateTextFile extends AWriteReadTextFile {

    //Key words to look for in text file-----------------------------------------
    public final static String ID = "ID";
    public final static String NAME = "NAME";
    public final static String PRECON = "PRECON";
    public final static String POSTCON = "POSTCON";
    public final static String DESCRIPTION = "DESCRIPTION";
    public final static String DOMAIN = "DOMAIN";
    public final static String INIT = "INIT";
    private final Model mModel;
    private final Set<ADataFromFile> mDataInFile = new HashSet<ADataFromFile>();
    private final Map<String, Integer> mExternalInternalIdMap = new HashMap<String, Integer>();

    public ReadFromProcessSimulateTextFile(String iReadFromFile, String iWriteToFile, Model iModel) {
        super(iReadFromFile, null);
        this.mModel = iModel;
    }

    @Override
    void whatToWriteToFile(BufferedWriter iOut) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean run() {

        if (!readFromFile()) {
            System.out.println("Can not read file!");
            return false;
        }

        if (!splitLines()) {
            System.out.println("Can not read line!");
            return false;
        }

        if (!addDataToModel()) {
            System.out.println("Can not parse data to Model!");
            return false;
        }

        return true;
    }

    private void setDescription(final Data iAddToData, final String iDescription) {
        if (iDescription != null && !iDescription.equals("null")) {
            iAddToData.setDescription(iDescription);
        }
    }

    public boolean addDataToModel() {
        if (mDataInFile.isEmpty()) {
            System.out.println("No operations or signals to create!");
            return false;
        }

        //Create resource for signals
        final ResourceData rd = new ResourceData("r", Model.newId());
        rd.setDescription(".. to store signals read from file");
        final TreeNode newResource = new TreeNode(rd);
        mModel.insertChild(mModel.getResourceRoot(), newResource);

        //Create operations and signals
        final Map<ADataFromFile, OperationData> dataOperationMap = new HashMap<ADataFromFile, OperationData>();
        for (final ADataFromFile data : mDataInFile) {
            //Init---------------------------------------------------------------
            if (data.mName == null || data.mId == null) {
                System.out.println("Problem with name: " + data.mName + " or id: " + data.mId);
                return false;
            }
            final String name = data.mName;
            final int idInModel = Model.newId();

            //Operations---------------------------------------------------------
            if (data instanceof OperationDataFromFile) {
                final TreeNode tn = mModel.createModelOperationNode(name, idInModel);
                final OperationData opData = (OperationData) tn.getNodeData();
                setDescription(opData, data.mDescription);

                dataOperationMap.put(data, opData);
            }

            //Signals------------------------------------------------------------
            if (data instanceof SignalDataFromFile) {
                final ResourceVariableData rvd = getVariable(data, name, idInModel);
                if (rvd == null) {
                    System.out.println("Problem with variable, name: " + data.mName + " or id: " + data.mId);
                    return false;
                }

                //Insert in Model------------------------------------------------
                mModel.insertChild(newResource, new TreeNode(rvd));
            }

            mExternalInternalIdMap.put(data.mId, idInModel);
        }

        //Set conditions
        for (final ADataFromFile data : mDataInFile) {
            if (data instanceof OperationDataFromFile) {
                final OperationDataFromFile operation = (OperationDataFromFile) data;
                final OperationData opData = dataOperationMap.get(data);

                //Add preconditions
                for (String con : operation.mPreConditionSet) {
                    if (!con.equals("null")) {
                        if(!addCondtion(con, opData, ConditionType.PRE)) {
                            return false;
                        }
                    }
                }

                //Add postconditions
                for (String con : operation.mPostConditionSet) {
                    if (!con.equals("null")) {
                        if(!addCondtion(con, opData, ConditionType.POST)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean addCondtion(String iCondition, final OperationData iOpData, final ConditionType iConditionType) {
        //Find if condition points to other operations or signals or a mix
        final int conditionType = conditionType(iCondition);
        String conditionTypeString = "";
        if (conditionType == -1) {
            System.out.println("Condition " + iCondition + " is not right");
            return false;
        } else if (conditionType == 0) {
            conditionTypeString = "Mix";
        } else if (conditionType == 1) {
            conditionTypeString = "Operations";
        } else if (conditionType == 2) {
            conditionTypeString = "Signals";
        }

        //Change id in file to id in Model
        iCondition = changeIds(iCondition);

        //In order to add a unique number to each condition
        final int number = iOpData.getGlobalConditions().size();

        //Test to parse condition as guard
        AStringToConditionParser parser = new GuardAsTextInputToConditionParser();
        ConditionExpression ce = parser.run(iCondition);
        if (ce != null) {
            //Add condition if parse was ok
            final Condition conditon = new Condition();
            conditon.setGuard(ce);

            final Map<ConditionType, Condition> map = new HashMap<ConditionType, Condition>();
            map.put(iConditionType, conditon);

            iOpData.getGlobalConditions().put(conditionTypeString + number, map);
            return true;
        }
        //Test parse condition as action
        parser = new ActionAsTextInputToConditionParser();
        ce = parser.run(iCondition);
        if (ce != null) {
            //Add condition if parse was ok
            final Condition conditon = new Condition();
            conditon.setAction(ce);

            final Map<ConditionType, Condition> map = new HashMap<ConditionType, Condition>();
            map.put(iConditionType, conditon);

            iOpData.getGlobalConditions().put(conditionTypeString + number, map);
            return true;
        }

        System.out.println("Condition " + iCondition + " for operation " + iOpData.getName() + " is not right!");
        return false;
    }

    /**
     * To determine if parameter <p>iCondition</p> has to do with: <br/>
     * operations, signals, or a mix.
     * @param iCondition
     * @return -1= error, 0=a mix, 1=operations, 2=signals
     */
    private int conditionType(final String iCondition) {
        int returnInt = -1;
        for (final ADataFromFile data : mDataInFile) {
            final String id = data.mId;

            if (iCondition.contains(id)) {
                if (data instanceof OperationDataFromFile) {
                    if (returnInt == 2) {
                        return 0;
                    }
                    returnInt = 1;
                }
                if (data instanceof SignalDataFromFile) {
                    if (returnInt == 1) {
                        return 0;
                    }
                    returnInt = 2;
                }
            }

        }
        return returnInt;
    }

    private ResourceVariableData getVariable(final ADataFromFile iData, final String iName, final int iIdInModel) {
        final SignalDataFromFile localData = (SignalDataFromFile) iData;
        final ResourceVariableData rvd = new ResourceVariableData(iName, iIdInModel);

        //Init-----------------------------------------------------------
        final Integer init = Integer.parseInt(localData.mInit);
        if (init == null) {
            System.out.println("Not right init value for: " + iData.mName);
            return null;
        }
        rvd.setInitialValue(init);

        //Domain---------------------------------------------------------
        final String[] minMax = localData.mDomain.split("\\.\\.");
        if (minMax.length != 2) {
            System.out.println("Not right domain value for: " + iData.mName + " length: " + minMax.length);
            return null;
        }
        final Integer min = Integer.parseInt(minMax[0].replaceAll(" ", ""));
        final Integer max = Integer.parseInt(minMax[1].replaceAll(" ", ""));
        if (min == null || max == null) {
            System.out.println("Not right domain value for: " + iData.mName);
            return null;
        }
        rvd.setMin(min);
        rvd.setMax(max);

        //Desc-----------------------------------------------------------
        setDescription(rvd, iData.mDescription);

        return rvd;
    }

    private String changeIds(String iCondition) {
        for (final String oldId : mExternalInternalIdMap.keySet()) {
            iCondition = iCondition.replaceAll(oldId, "id" + mExternalInternalIdMap.get(oldId).toString());
        }
        return iCondition;
    }

    public boolean splitLines() {

        if (mReadLineSet == null || mReadLineSet.isEmpty()) {
            System.out.println("No lines to read from in file!");
            return false;
        }
        for (final String line : mReadLineSet) {
            final String[] splitLine = line.split("::");
            final Map<String, Set<String>> localPropertySetMap = new HashMap<String, Set<String>>();
            for (final String property : splitLine) {
                final String[] splitProperty = property.split(":");
                if (splitProperty.length != 2) {
                    System.out.println("Syntax is not right for property: " + property + " at line: " + line);
                    return false;
                }
                final String propKey = splitProperty[0].replaceAll(" ", "");
                final String propValue = splitProperty[1];
                if (!localPropertySetMap.containsKey(propKey)) {
                    localPropertySetMap.put(propKey, new HashSet<String>());
                }

                localPropertySetMap.get(propKey).add(propValue);
            }

            //Syntax in this line ok?
            final int typeOfData = checkDataFromFile(localPropertySetMap);

            if (typeOfData == 0) {
                System.out.println("Problem with line: " + line);
                return false;
            }

            final String id = localPropertySetMap.get(ID).iterator().next().replaceAll(" ", "");
            final String name = localPropertySetMap.get(NAME).iterator().next();
            final String desc = localPropertySetMap.get(DESCRIPTION).iterator().next();

            ADataFromFile returnData = null;

            //operation
            if (typeOfData == 1) {
                final Set<String> preSet = localPropertySetMap.get(PRECON);
                final Set<String> postSet = localPropertySetMap.get(POSTCON);
                returnData = new OperationDataFromFile(id, name, desc, preSet, postSet);
            }

            if (typeOfData == 2) {
                final String domain = localPropertySetMap.get(DOMAIN).iterator().next().replaceAll(" ", "");
                final String init = localPropertySetMap.get(INIT).iterator().next().replaceAll(" ", "");
                returnData = new SignalDataFromFile(id, name, desc, domain, init);
            }

            if (returnData != null) {
                mDataInFile.add(returnData);
            }

        }

        return true;
    }

    /**
     * Check what type of data that is on the line in the text file
     * @param iMap
     * @return 0 data not correct, 1 operation, 2 signal
     */
    private int checkDataFromFile(final Map<String, Set<String>> iMap) {
        if (!iMap.containsKey(ID) || !iMap.containsKey(NAME) || !iMap.containsKey(DESCRIPTION)) {
            return 0;
        }
        if (iMap.get(ID).size() != 1 || iMap.get(NAME).size() != 1 || iMap.get(DESCRIPTION).size() != 1) {
            return 0;
        }
        if (iMap.containsKey(PRECON) && iMap.containsKey(POSTCON)) {
            return 1;
        }
        if (iMap.containsKey(DOMAIN) && iMap.containsKey(INIT)) {
            return 2;
        }
        return 0;
    }

    private abstract class ADataFromFile {

        public final String mId;
        public final String mName;
        public final String mDescription;

        public ADataFromFile(String mId, String mName, String mDescription) {
            this.mId = mId;
            this.mName = mName;
            this.mDescription = mDescription;
        }
    }

    private class OperationDataFromFile extends ADataFromFile {

        public final Set<String> mPreConditionSet;
        public final Set<String> mPostConditionSet;

        public OperationDataFromFile(String mId, String mName, String mDescription, Set<String> mPreConditionSet, Set<String> mPostConditionSet) {
            super(mId, mName, mDescription);
            this.mPreConditionSet = mPreConditionSet;
            this.mPostConditionSet = mPostConditionSet;
        }
    }

    private class SignalDataFromFile extends ADataFromFile {

        public final String mDomain;
        public final String mInit;

        public SignalDataFromFile(String mId, String mName, String mDescription, String mDomain, String mInit) {
            super(mId, mName, mDescription);
            this.mDomain = mDomain;
            this.mInit = mInit;
        }
    }
}
