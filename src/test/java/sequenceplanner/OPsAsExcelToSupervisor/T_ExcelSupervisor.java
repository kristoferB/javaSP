package sequenceplanner.OPsAsExcelToSupervisor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
//import sequenceplanner.IO.excel.*;
//import sequenceplanner.SequencePlanner;
//import sequenceplanner.multiProduct.OperationResourceDataStructure;
//import sequenceplanner.multiProduct.ProbeForFlowerStructure;
//import sequenceplanner.multiProduct.SupervisorFromExcelFile;
import static org.junit.Assert.*;
//
///**
// * Tests taking operations and resources described in excel file and gererate .wmod file.<br/>
// * {@link Excel} -> {@link SheetTable} -> {@link OperationResourceDataStructure}<br/>
// * Method calls in {@link SupervisorFromExcelFile}
// * @author patrik
// */
public class T_ExcelSupervisor {

    public T_ExcelSupervisor() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public static void dummy() {

    }
//
////    @Test
//    public void test1() {
//        SupervisorFromExcelFile s = new SupervisorFromExcelFile(
//                SequencePlanner.class.getResource("resources/filesForTesting/ExcelTestIndata_ForJUnitTests.xls").getFile(),
//                "C:/Users/patrik/Desktop/resultEFA.wmod");
//        assertTrue(s.runPart1());
//        assertTrue(s.runPart2());
//    }
//
////    @Test
//    public void test2() {
//        SupervisorFromExcelFile s = new SupervisorFromExcelFile("Z:/FLEXA/FlowerTests/OperationsFromExcel_SmallFlexaCell_SingleProduct.xls",
//                "C:/Users/patrik/Desktop/resultEFA.wmod");
//        assertTrue(s.runPart1());
//        assertTrue(s.runPart2());
//    }
//
//    @Test
//    public void testEFAProbe() {
//        final String filePathToZhennan = "C:/Users/patrik/Documents/My Dropbox/TempAutomation/Zhennan/FlowerTests/";
//
//        SupervisorFromExcelFile s = new SupervisorFromExcelFile("Z:/FLEXA/FlowerTests/OperationsFromExcel_SmallFlexaCell_SingleProduct.xls",
//                filePathToZhennan + "FlowerTest_OneBigFlower.wmod");
//
//        ProbeForFlowerStructure probe = new ProbeForFlowerStructure();
//        s.setmProbe(probe);
//
//        assertTrue(s.runPart1());
//        assertTrue(s.runPart2());
//
//        assertTrue(probe.createModuleType1(filePathToZhennan + "FlowerTest_FlowerSetBasedOnSetResourceInAction.wmod"));
//        assertTrue(probe.createModuleType2(filePathToZhennan + "FlowerTest_FlowerSetBasedOnFirstSetResourceInAction.wmod"));
//        assertTrue(probe.createModuleType4(filePathToZhennan + "FlowerTest_FlowerSetBasedOnVariables.wmod"));
//    }
//
////    @Test
//    public void generateSmallExampleForPSOP_SingleProduct_TwoMachines_OneBuffer() {
//        final String filePathToFolder = "Z:/FLEXA/HV_language/SingleProduct_TwoMachines_OneBuffer/";
//        final String fileNameOfExcelModel = "InitialModel_SingleProduct_TwoMachines_OneBuffer.xls";
//
//        SupervisorFromExcelFile s = new SupervisorFromExcelFile(filePathToFolder + fileNameOfExcelModel,
//                filePathToFolder + "OneBigFlower.wmod");
//
//        ProbeForFlowerStructure probe = new ProbeForFlowerStructure();
//        s.setmProbe(probe);
//
//        assertTrue(s.runPart1());
//        assertTrue(s.runPart2());
//
//        assertTrue(probe.createModuleType3(filePathToFolder + "EachOperationIsAnEFA_.wmod"));
//
//        assertTrue(probe.createPSOPSequence(filePathToFolder + "PSOP_Sequence.txt", "Single product two machines", 45, 25, 25));
//
//    }
//
////    @Test
//    public void generateSmallExampleForPSOP_TwoProducts_TwoMachines_OneBuffer() {
//        final String filePathToFolder = "Z:/FLEXA/HV_language/TwoProducts_TwoMachines_OneBuffer/";
//        final String fileNameOfExcelModel = "InitialModel_TwoProducts_TwoMachines_OneBuffer.xls";
//
//        SupervisorFromExcelFile s = new SupervisorFromExcelFile(filePathToFolder + fileNameOfExcelModel,
//                filePathToFolder + "OneBigFlower.wmod");
//
//        ProbeForFlowerStructure probe = new ProbeForFlowerStructure();
//        s.setmProbe(probe);
//
//        assertTrue(s.runPart1());
//        assertTrue(s.runPart2());
//
//        assertTrue(probe.createModuleType3(filePathToFolder + "EachOperationIsAnEFA.wmod"));
//
//        assertTrue(probe.createPSOPSequence(filePathToFolder + "PSOP_Sequence.txt", "Two products two machines", 80, 23, 23));
//    }
//
////    @Test
//    public void algorithmTestOnParse() {
//        //Will break if data in excel file breaks syntax rules!
//
//        //Parse file...
//        final SupervisorFromExcelFile s = new SupervisorFromExcelFile(
//                SequencePlanner.class.getResource("resources/filesForTesting/ExcelTestIndata_ForJUnitTests.xls").getFile(),
//                "C:/Users/patrik/Desktop/resultEFA.wmod");
//
//        //...and run part 1 (from excel file to internal data structure)
//        assertTrue(s.runPart1());
//
//        //Check correctness of data----------------------------------------------
//        final OperationResourceDataStructure data = s.getDataStructure();
//
//        //Resources
//        for (final OperationResourceDataStructure.Resource r : data.mResourceSet) {
//
//            //Test M2
//            if (r.mName.equals("m2")) {
//                System.out.println("Test resource m2");
//                assertTrue("initValue", r.mInitValue.equals("läge1"));
//                assertTrue("nbr of values", r.mValueLL.size() == 3);
//                assertTrue("via resources for m2", r.mViaResourceValueMap.keySet().size() == 1); //o1 is single via resource for m2
//                final OperationResourceDataStructure.Resource o1 = data.getResourceInSet("o1");
//                assertTrue(r.mViaResourceValueMap.get(o1).contains(o1.mValueLL.indexOf("m2:läge1_m2:läge2::m3:taken")));
//            }
//
//            //Test o1
//            if (r.mName.equals("o1")) {
//                System.out.println("Test resource o1");
//                assertTrue("nbr of values", r.mValueLL.size() == 3);
//                assertTrue(r.mValueLL.contains("empty"));
//                assertTrue(r.mValueLL.contains("m2:läge1_m2:läge2::m3:taken"));
//                assertTrue(r.mValueLL.contains("m1:empty::m2:2_m1:taken::m3:taken"));
//            }
//        }
//
//        //Operations
//        for (final OperationResourceDataStructure.Operation op : data.mOperationSet) {
//
//            if (op.mName.equals("op1")) {
//                System.out.println("Test operation op1");
//                assertTrue(op.mSourceResourceMap.keySet().size() == 2);
//                final OperationResourceDataStructure.Resource m2 = data.getResourceInSet("m2");
//                assertTrue(op.mSourceResourceMap.get(m2).equals(2));
//                final OperationResourceDataStructure.Resource m3 = data.getResourceInSet("m3");
//                assertTrue(op.mDestResourceMap.get(m3).equals(m3.mValueLL.indexOf("taken")));
//                final OperationResourceDataStructure.Resource o1 = data.getResourceInSet("o1");
//                assertTrue(op.mViaResourceMap.containsKey(o1));
//                assertTrue(op.mViaResourceMap.keySet().size() == 2);
//                assertTrue(op.mViaResourceMap.get(o1).equals(o1.mValueLL.indexOf("m1:empty::m2:2_m1:taken::m3:taken")));
//
//                //extra
//                assertTrue(op.mExtraStartConditionMap.keySet().size() == 2);
//                assertTrue(op.mExtraStartConditionMap.get("guard").equals("guardex==0"));
//                assertTrue(op.mExtraFinishConditionMap.keySet().size() == 1);
//                assertTrue(op.mExtraFinishConditionMap.get("action").equals("actiontyp+=10"));
//            }
//
//            if (op.mName.equals("12open")) {
//                System.out.println("Test operation 12open");
//                //extra
//                assertTrue(op.mExtraFinishConditionMap.keySet().size() == 1);
//                assertTrue(op.mExtraFinishConditionMap.get("guard").equals("guard1>22"));
//            }
//        }
//        //-----------------------------------------------------------------------
//    }
}
