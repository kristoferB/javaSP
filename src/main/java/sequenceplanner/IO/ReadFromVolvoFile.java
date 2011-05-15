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
public class ReadFromVolvoFile extends AWriteReadTextFile {

    public final static String ID = "ID";
    public final static String NAME = "NAME";
    public final static String PRECON = "PRECON";
    public final static String POSTCON = "POSTCON";
    public final static String DESCRIPTION = "DESCRIPTION";
    private Model mModel;
    private final Set<String> mLineSet = new HashSet<String>();
    private final Map<String, List<String>> mIdPropertyMap = new HashMap<String, List<String>>();
    private final Map<String, Integer> mExternalInternalIdMap = new HashMap<String, Integer>();

    public ReadFromVolvoFile(String iReadFromFile, String iWriteToFile, Model iModel) {
        super(iReadFromFile, null);
        mModel = iModel;
    }

    @Override
    void whatToDoWithLine(String iLine) {
        mLineSet.add(iLine);
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

        if (!createOperations()) {
            return false;
        }

        return true;
    }

    public void printInfo() {
        for (final String s : mIdPropertyMap.keySet()) {
            System.out.println(mIdPropertyMap.get(s).toString());
        }
    }

    public boolean createOperations() {
        if (mIdPropertyMap.isEmpty()) {
            System.out.println("No operations to create!");
            return false;
        }

        //Create operations

        for (final String key : mIdPropertyMap.keySet()) {
            final String name = mIdPropertyMap.get(key).get(0);

            final OperationData opData = new OperationData(name, -1);
            Model.giveId(opData);
            TreeNode[] toAdd = new TreeNode[1];
            toAdd[0] = new TreeNode(opData);
            mModel.saveOperationData(toAdd);

            mExternalInternalIdMap.put(key, opData.getId());
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
            final String[] disjunctionSplit = disjunction.split(",");
            if (disjunctionSplit.length != 2) {
                return false;
            }

            final int id = mExternalInternalIdMap.get(disjunctionSplit[0]);
            final int location = Integer.parseInt(disjunctionSplit[1]);
            final OperationData.SeqCond sq = new OperationData.SeqCond(id, location);

            final LinkedList<OperationData.SeqCond> llOR = new LinkedList<OperationData.SeqCond>();
            llOR.add(sq);
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
        if (mLineSet.isEmpty()) {
            System.out.println("No lines to read from in file!");
            return false;
        }
        for (final String line : mLineSet) {
            final String[] splitLine = line.split("::");
            if (splitLine.length != 5) {
                System.out.println("Syntax is not right for line: " + line);
                return false;
            }
            final Map<String, String> localPropertyMap = new HashMap<String, String>();
            for (final String property : splitLine) {
                final String[] splitProperty = property.split(":");
                if (splitProperty.length != 2) {
                    System.out.println("Syntax is not right for property: " + property + " at line: " + line);
                    return false;
                }
                final String propKey = splitProperty[0];
                final String propValue = splitProperty[1];
                localPropertyMap.put(propKey, propValue);
            }

            //ID
            if (!localPropertyMap.containsKey(ID)) {
                System.out.println("Line has no " + ID + ": " + line);
                return false;
            }
            mIdPropertyMap.put(localPropertyMap.get(ID), new ArrayList<String>());
            final List<String> localList = mIdPropertyMap.get(localPropertyMap.get(ID));

            //NAME
            if (!localPropertyMap.containsKey(NAME)) {
                System.out.println("Line has no " + NAME + ": " + line);
                return false;
            }
            localList.add(localPropertyMap.get(NAME));

            //PRECON
            if (!localPropertyMap.containsKey(PRECON)) {
                System.out.println("Line has no " + PRECON + ": " + line);
                return false;
            }
            localList.add(localPropertyMap.get(PRECON));

            //POSTCON
            if (!localPropertyMap.containsKey(POSTCON)) {
                System.out.println("Line has no " + POSTCON + ": " + line);
                return false;
            }
            localList.add(localPropertyMap.get(POSTCON));

            //DESCRIPTION
            if (!localPropertyMap.containsKey(DESCRIPTION)) {
                System.out.println("Line has no " + DESCRIPTION + ": " + line);
                return false;
            }
            localList.add(localPropertyMap.get(DESCRIPTION));

        }

        return true;
    }
}
