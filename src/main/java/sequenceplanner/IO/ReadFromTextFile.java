package sequenceplanner.IO;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author patrik
 */
public class ReadFromTextFile extends AWriteReadTextFile {

    public final static String ID = "ID";
    public final static String NAME = "NAME";
    public final static String PRECON = "PRECON";
    public final static String POSTCON = "POSTCON";
    public final static String DESCRIPTION = "DESCRIPTION";
    public final static String DOMAIN = "DOMAIN";
    public final static String INIT = "INIT";
    private final Model mModel;
    private final Set<ADataFromFile> mDataInFile = new HashSet<ADataFromFile>();
    private final Map<String, List<String>> mIdPropertyMap = new HashMap<String, List<String>>();
    private final Map<String, Integer> mExternalInternalIdMap = new HashMap<String, Integer>();

    public ReadFromTextFile(String iReadFromFile, String iWriteToFile, Model iModel) {
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
            return false;
        }

        if (!addDataToModel()) {
            return false;
        }

        return true;
    }

    public void printInfo() {
        for (final String s : mIdPropertyMap.keySet()) {
            System.out.println(mIdPropertyMap.get(s).toString());
        }
    }

    public boolean addDataToModel() {
        if (mDataInFile.isEmpty()) {
            System.out.println("No operations or signals to create!");
            return false;
        }

        //Create resource for signals
        final ResourceData rd = new ResourceData("r", Model.newId());
        final TreeNode newResource = new TreeNode(rd);
        mModel.insertChild(mModel.getResourceRoot(), newResource);

        //Create operations and signals
        for (final ADataFromFile data : mDataInFile) {
            final String name = data.mName;
            final int newId = Model.newId();

            if (data instanceof OperationDataFromFile) {
                final TreeNode tn = mModel.createModelOperationNode(name, newId);
                final OperationData opData = (OperationData) tn.getNodeData();
            }

            if (data instanceof SignalDataFromFile) {
                final SignalDataFromFile localData = (SignalDataFromFile) data;
                final ResourceVariableData rvd = new ResourceVariableData(name, newId);

                final Integer init = Integer.getInteger(localData.mInit);
                if (init == null) {
                    System.out.println("Not right init value for: " + data.mName);
                    return false;
                }
                rvd.setInitialValue(init);

                final String[] minMax = localData.mDomain.split("..");
                if (minMax.length != 2) {
                    return false;
                }
                final Integer min = Integer.getInteger(minMax[0]);
                final Integer max = Integer.getInteger(minMax[1]);
                if (min == null || max == null) {
                    System.out.println("Not right domain value for: " + data.mName);
                    return false;
                }
                rvd.setMin(min);
                rvd.setMax(max);

                mModel.insertChild(newResource, new TreeNode(rvd));
            }

            mExternalInternalIdMap.put(data.mId, newId);
        }

        //Set conditions
        for (final String key : mIdPropertyMap.keySet()) {
            final OperationData opData = (OperationData) mModel.getOperation(mExternalInternalIdMap.get(key)).getNodeData();

            final String precondition = mIdPropertyMap.get(key).get(1);
            setCondition(opData, precondition, true);

            final String postcondition = mIdPropertyMap.get(key).get(2);
            setCondition(opData, postcondition, false);
        }

        //Create variables for signals
        workWithSignals();

        return true;
    }

    private boolean workWithSignals() {

        final ResourceData rd = new ResourceData("r", -1);
        Model.giveId(rd);
        final TreeNode newResource = new TreeNode(rd);
        mModel.insertChild(mModel.getResourceRoot(), newResource);

        final Map<String, ResourceVariableData> signalVariableMap = new HashMap<String, ResourceVariableData>();
        for (final String key : mIdPropertyMap.keySet()) {
            final OperationData opData = (OperationData) mModel.getOperation(mExternalInternalIdMap.get(key)).getNodeData();
            final String description = mIdPropertyMap.get(key).get(3);
            if (!description.equals("null")) {

                final String[] conjunctionSplit = description.split("AND");
                String newDescription = "";
                for (String signal : conjunctionSplit) {
                    signal = signal.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(" ", "");

                    ResourceVariableData rvd = null;
                    if (signalVariableMap.containsKey(signal)) {
                        rvd = signalVariableMap.get(signal);
                    } else {
                        rvd = new ResourceVariableData(signal, -1);
                        Model.giveId(rvd);
                        mModel.insertChild(newResource, new TreeNode(rvd));
                        signalVariableMap.put(signal, rvd);
                    }

                    if (newDescription.length() > 0) {
                        newDescription += "AND";
                    }
                    newDescription += rvd.getId();
                }

                opData.setDescription(newDescription);
            }
        }
        return true;
    }

    private boolean setCondition(final OperationData iOpData, String iCondition, final boolean isPrecondition) {
        iCondition = iCondition.replaceAll(" ", "");

        if (iCondition.equals("null")) {
            return true;
        }

        final LinkedList<LinkedList<OperationData.SeqCond>> llAND = new LinkedList<LinkedList<OperationData.SeqCond>>();
        final String[] conjunctionSplit = iCondition.split("AND");
        for (final String disjunction : conjunctionSplit) {

            final LinkedList<OperationData.SeqCond> llOR = new LinkedList<OperationData.SeqCond>();
            final String[] disjunctionSplit = disjunction.split("OR");
            for (final String term : disjunctionSplit) {

                final String[] termSplit = term.split(",");
                if (termSplit.length != 2) {
                    return false;
                }

                if (!mExternalInternalIdMap.containsKey(termSplit[0])) {
                    System.out.println("Syntax error for condition in operation:" + iOpData.getName());
                    return false;
                }

                final int id = mExternalInternalIdMap.get(termSplit[0]);
                final int location = Integer.parseInt(termSplit[1]);
                final OperationData.SeqCond sq = new OperationData.SeqCond(id, location);

                llOR.add(sq);
            }
            llAND.add(llOR);
        }

        if (isPrecondition) {
            iOpData.setSequenceCondition(llAND);
        } else {
            iOpData.setPSequenceCondition(llAND);
        }

        return true;
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
                final String propKey = splitProperty[0];
                final String propValue = splitProperty[1];
                if (!localPropertySetMap.containsKey(propKey)) {
                    localPropertySetMap.put(propKey, new HashSet<String>());
                }

                localPropertySetMap.get(propKey).add(propValue);
            }

            final int typeOfData = checkDataFromFile(localPropertySetMap);

            if (typeOfData == 0) {
                System.out.println("Problem with line: " + line);
                return false;
            }

            final String id = localPropertySetMap.get(ID).iterator().next();
            final String name = localPropertySetMap.get(NAME).iterator().next();
            final String desc = localPropertySetMap.get(DESCRIPTION).iterator().next();

            ADataFromFile returnData;

            //operation
            if (typeOfData == 1) {
                final Set<String> preSet = localPropertySetMap.get(PRECON);
                final Set<String> postSet = localPropertySetMap.get(POSTCON);
                returnData = new OperationDataFromFile(id, name, desc, preSet, postSet);
            }

            if (typeOfData == 2) {
                final String domain = localPropertySetMap.get(DOMAIN).iterator().next();
                final String init = localPropertySetMap.get(INIT).iterator().next();
                returnData = new SignalDataFromFile(id, name, desc, domain, init);
            }

            mDataInFile.add(returnData);

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
