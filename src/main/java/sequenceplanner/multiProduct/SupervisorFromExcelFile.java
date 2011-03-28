//package sequenceplanner.multiProduct;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import sequenceplanner.IO.excel.Excel;
//import sequenceplanner.IO.excel.SheetTable;
//import sequenceplanner.efaconverter.SEFA;
//import sequenceplanner.efaconverter.SEGA;
//import sequenceplanner.efaconverter.SModule;
//
///**
// *
// * @author patrik
// */
//public class SupervisorFromExcelFile {
//
//    private String mExcelFilePath;
//    private Map<String, SheetTable> mSheetMap = null;
//    private final static String mNameOfResourceSheet = "Resources"; //Name of sheet with resources
//    private OperationResourceDataStructure mData;
//    private SModule mSModule;
//    private SEFA mSEFA;
//
//    public SupervisorFromExcelFile(String iExcelFilePath) {
//        mExcelFilePath = iExcelFilePath;
//        mData = new OperationResourceDataStructure();
//    }
//
//    public OperationResourceDataStructure getDataStructure() {
//        return mData;
//    }
//
//    /**
//     * From excel file to internal data structure
//     * @return ture if ok else false
//     */
//    public boolean runPart1() {
//        if (!initExcelFile()) {
//            System.out.println("Problem to init excel file");
//            return false;
//        }
//
//        if (!parseResourcesFromExcelObject()) {
//            System.out.println("Problem to parse resources from excel file");
//            return false;
//        }
//
//        if (!parseOperationsFromExcelObject()) {
//            System.out.println("Problem to parse operations from excel file");
//            return false;
//        }
//
//        return true;
//    }
//
//    public boolean runPart2() {
//        if (!initModule()) {
//            System.out.println("Problem to init module!");
//            return false;
//        }
//
//        if (!createVariablesForResources()) {
//            System.out.println("Problem to create variables for resources in module!");
//            return false;
//        }
//
//        if (!createTransitionsForOperations()) {
//            System.out.println("Problem to create transitions for operations in module!");
//            return false;
//        }
//
//        if (!saveWMODFile()) {
//            System.out.println("Problem to save module as WMOD file!");
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean saveWMODFile() {
//        if (mSModule == null) {
//            return false;
//        }
//
//        if (!mSModule.saveToWMODFile("C:/Users/patrik/Desktop/resultEFA.wmod")) {
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean createTransitionsForOperations() {
//        if (mSModule == null) {
//            return false;
//        }
//
//        int opNbr = 1000;
//
//        for (final OperationResourceDataStructure.Operation op : mData.mOperationSet) {
//            SEGA sega; //Help class used during transition creation
//            String eventLabel = "";
//            ++opNbr;
//
//            //Operation start transition-----------------------------------------
//            //Create eventLabel
//            eventLabel = "s_" + opNbr; //s for start
//            if (!op.mName.equals("")) {
//                eventLabel += "_" + op.mName;
//            }
//
//            //Create "event guard action" for transition
//            sega = new SEGA(eventLabel);
//
//            op.startGuard(sega);
//            op.startAction(sega);
////            System.out.println("Event: " + eventLabel);
////            System.out.println("Guard: " + sega.getGuard());
////            System.out.println("Action: " + sega.getAction());
//
//            //Add transition
//            mSEFA.addStandardSelfLoopTransition(sega);
//            //-------------------------------------------------------------------
//
//            //Operation finish transition----------------------------------------
//            //Create eventLabel
//            eventLabel = "f_" + opNbr; //f for finish
//            if (!op.mName.equals("")) {
//                eventLabel += "_" + op.mName;
//            }
//
//            //Create "event guard action" for transition
//            sega = new SEGA(eventLabel);
//
//            op.finishGuard(sega);
//            op.finishAction(sega);
////            System.out.println("Event: " + eventLabel);
////            System.out.println("Guard: " + sega.getGuard());
////            System.out.println("Action: " + sega.getAction());
//
//            //Add transition
//            mSEFA.addStandardSelfLoopTransition(sega);
//            //-------------------------------------------------------------------
//
//        }
//
//        return true;
//    }
//
//    private boolean initModule() {
//        //Init module
//        mSModule = new SModule("ExcelSupervisor");
//        mSModule.setComment("Enhanced resources!");
//
//        //Init single location (initial and marked)
//        mSEFA = new SEFA("SingleLocationEFA", mSModule);
//        mSEFA.addState("pm", true, true);
//
//        return true;
//    }
//
//    private boolean createVariablesForResources() {
//        if (mSModule == null) {
//            return false;
//        }
//
//        for (final OperationResourceDataStructure.Resource r : mData.mResourceSet) {
//
//            final String varName = r.mVarName;
//            final int lowerBound = 0;
//            final int upperBound = r.mValueLL.size() - 1;
//            final int initialValue = r.mValueLL.indexOf(r.mInitValue);
//            final Integer markedValue = r.mValueLL.indexOf(r.mInitValue);
//
//            mSModule.addIntVariable(varName, lowerBound, upperBound, initialValue, markedValue);
//
//        }
//
//        return true;
//    }
//
//    private boolean parseOperationsFromExcelObject() {
//        if (mSheetMap == null) {
//            return false;
//        }
//        if (mData == null) {
//            return false;
//        }
//
//        //Get all operation sheets (all sheets except resource sheet)
//        Set<String> operationSheetSet = mSheetMap.keySet();
//        operationSheetSet.remove(mNameOfResourceSheet);
//
//        System.out.println("Start to add operations...");
//        //loop sheets with operations
//        for (final String sheetName : operationSheetSet) {
//            final SheetTable st = mSheetMap.get(sheetName);
//
//            //check that st follows operation sheet standard
//            if (st.getNbrOfColumns() < 4 || st.getNbrOfColumns() > 6) {
//                System.out.println("Sheet " + sheetName + " is not following the operation sheet standard!");
//                return false;
//            }
//
//            //loop operations in sheet
//            for (int rowIndex = 1; rowIndex < st.getNbrOfRows(); ++rowIndex) {
//                //To send to mData
//                final Map<String, String> opDataMap = new HashMap<String, String>(6);
//
//                //fill opDataMap
//                final String opName = st.getCellValue(rowIndex, 0);
//                opDataMap.put("name", opName);
//                final String sourceCondition = st.getCellValue(rowIndex, 1);
//                opDataMap.put("source", sourceCondition);
//                final String viaCondition = st.getCellValue(rowIndex, 2);
//                opDataMap.put("via", viaCondition);
//                final String destCondition = st.getCellValue(rowIndex, 3);
//                opDataMap.put("dest", destCondition);
//                final String extraStartCondition = st.getCellValue(rowIndex, 4);
//                opDataMap.put("extraSC", extraStartCondition);
//                final String extraFinishCondition = st.getCellValue(rowIndex, 5);
//                opDataMap.put("extraFC", extraFinishCondition);
//
//                if (!mData.addOperation(mData.mResourceSet, opDataMap)) {
//                    System.out.println("error in sheet: " + sheetName + ", row: " + ++rowIndex);
//                    return false;
//                }
//            }
//        }
//        System.out.println("...finsihed with addition of operations!");
//        return true;
//    }
//
//    private boolean parseResourcesFromExcelObject() {
//        if (mSheetMap == null) {
//            return false;
//        }
//        if (!mSheetMap.containsKey(mNameOfResourceSheet)) {
//            System.out.println("No " + mNameOfResourceSheet + " sheet in file!");
//            return false;
//        }
//        final SheetTable st = mSheetMap.get(mNameOfResourceSheet);
//
//        if (mData == null) {
//            return false;
//        }
//
//        //To send to mData
//        final Map<String, List<Object>> resourceMap = new HashMap<String, List<Object>>();
//
//        //fill resourceMap
//        for (int colIndex = 0; colIndex < st.getNbrOfColumns(); colIndex += 3) {
//            final String resourceName = st.getCellValue(0, colIndex);
//            final List resourceDataList = new ArrayList(2);
//            final String initValue = st.getCellValue(1, colIndex);
//            resourceDataList.add(initValue);
//            final SheetTable resourceInitValues = st.getSubSheetTable(3, st.getNbrOfRowsInCol(colIndex) - 1, colIndex, colIndex);
//            resourceDataList.add(resourceInitValues);
//
//            if (resourceMap.containsKey(resourceName)) {
//                System.out.println("Resouce " + resourceName + " given multipe times in file!");
//                return false;
//            }
//            resourceMap.put(resourceName, resourceDataList);
//
////            System.out.println(resourceName);
////            System.out.println(resourceInitValues.toString());
//        }
//
//        System.out.println("Start to add resources...");
//        if (!mData.addResourceWithValues(resourceMap)) {
//            return false;
//        }
//        System.out.println(" ...finsihed with addition of resources!");
//
//        return true;
//    }
//
//    private boolean initExcelFile() {
//        Excel excel = new Excel(mExcelFilePath);
//        if (!excel.init()) {
//            return false;
//        }
////        System.out.println(excel.toString());
//        mSheetMap = excel.getSheets();
//        return true;
//    }
//}
