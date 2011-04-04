package sequenceplanner.multiProduct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.efaconverter.SEFA;
import sequenceplanner.efaconverter.SEGA;
import sequenceplanner.efaconverter.SModule;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 * Class with methods to translate all operations in root to flower EFA.<br/>
 * One variable {0,1,2} for each operation. Value 2 is single marked value.<br/>
 * Example of method calls:<br/>
 * testIDs();<br/>
 * startToGetOperations();<br/>
 * mSModule.saveToWMODFile("C:/result.wmod");<br/>
 * @author patrik
 */
public class OperationToEFA {

    SModule mSModule = new SModule("PPU riveting");
    Map<Integer, String> mOperationIdNameMap = new HashMap<Integer, String>();
    Model mModel;
    Map<Integer, String> mIdNameMap;

    public OperationToEFA(Model iModel) {
        mModel = iModel;
    }

    public void getAllOperationIds(final TreeNode iTree, final int iRootId, Set<Integer> ioOperations) {
        for (int i = 0; i < iTree.getChildCount(); ++i) {
//            mOperationIdNameMap.add(((OperationData) iTree.getChildAt(i).getNodeData()).getId());
            mOperationIdNameMap.put(((OperationData) iTree.getChildAt(i).getNodeData()).getId(),
                    ((OperationData) iTree.getChildAt(i).getNodeData()).getName());
            getAllOperationIds(iTree.getChildAt(i), iRootId, ioOperations);
        }
    }

    public void startToGetOperations() {
        getOperations(mModel.getOperationRoot(), mModel.getResourceRoot().getId());
    }

    private String variableName(final String iName, final String iId) {
        return iName + "_" + iId + "_var";
    }

    public void getOperations(final TreeNode iTree, final int iRootId) {
        for (int i = 0; i < iTree.getChildCount(); ++i) {
            OperationData iOpData = (OperationData) iTree.getChildAt(i).getNodeData();

            final String name = iOpData.getName();
            final String id = Integer.toString(iOpData.getId());

            SEFA sefa = new SEFA(name + "_" + id, mSModule);
            sefa.addState("pm", true, true);

            mSModule.addIntVariable(variableName(name, id), 0, 2, 0, 2);

            SEGA sega;
            //Start event
            sega = new SEGA("e_" + name + "_" + id + "_start");
            sega.andGuard(variableName(name, id) + "==0");
            addGuardBasedOnSPpreCondition(iOpData.getRawPrecondition(), sega);
            sega.addAction(variableName(name, id) + "=1");
            sefa.addStandardSelfLoopTransition(sega);
            //Finish event
            sega = new SEGA("e_" + name + "_" + id + "_finish");
            sega.andGuard(variableName(name, id) + "==1");
            addGuardBasedOnSPpreCondition(iOpData.getRawPostcondition(), sega);
            sega.addAction(variableName(name, id) + "=2");
            sefa.addStandardSelfLoopTransition(sega);

            getOperations(iTree.getChildAt(i), iRootId);

        }
    }

    private void addGuardBasedOnSPpreCondition(final String iCondition, SEGA ioEga) {


        //add precondition to guard
        if (!iCondition.equals("")) {
            String guardPreCon = iCondition; //Example of raw precondition 18_f A (143_iV19_f)

            //Change all ID to cID
            for (final Integer i : mOperationIdNameMap.keySet()) {
                guardPreCon = guardPreCon.replaceAll(i.toString(), variableName(mOperationIdNameMap.get(i), i.toString()));
            }

            guardPreCon = ioEga.guardFromSPtoEFASyntaxTranslation(guardPreCon);

            ioEga.andGuard(guardPreCon);
            System.out.println("and guard: " + guardPreCon);
        }
    }

    /**
     * Method in this class can't handle IDs that are suffix or prefix to each other, e.g. 18 and 118
     * @return true if IDs are ok else false
     */
    public boolean testIDs() {

        Set<Integer> operationIds = new HashSet<Integer>();

        getAllOperationIds(mModel.getOperationRoot(), mModel.getResourceRoot().getId(), operationIds);

        String test = "";

        //Add values and test
        for (final Integer id : operationIds) {
            if (test.contains(id.toString())) {
                return false;
            } else {
                test = test + id + "_";
            }
        }
        //Test on each value on complete string
        for (final Integer id : operationIds) {
            if (test.contains(id.toString())) {
                //All ids occur atleast once in string test. Remove this and check again.s
                final String test2 = test.replaceFirst(id.toString(), "");
                if (test2.contains(id.toString())) {
                    return false;
                }
            }
        }

        return true;
    }
}
