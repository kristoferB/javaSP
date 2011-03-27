package sequenceplanner.OPsAsExcelToSupervisor;

import org.junit.Test;
import sequenceplanner.IO.excel.*;
import sequenceplanner.SequencePlanner;
import sequenceplanner.multiProduct.OperationResourceDataStructure;
import sequenceplanner.multiProduct.SupervisorFromExcelFile;
import static org.junit.Assert.*;

/**
 * Tests taking operations and resources described in excel file and gererate .wmod file.<br/>
 * {@link Excel} -> {@link SheetTable} -> {@link OperationResourceDataStructure}<br/>
 * Method calls in {@link SupervisorFromExcelFile}
 * @author patrik
 */
public class ExcelSupervisor {

    @Test
    public void test1() {
        SupervisorFromExcelFile s = new SupervisorFromExcelFile(
                SequencePlanner.class.getResource("resources/filesForTesting/ExcelTestIndata_ForJUnitTests.xls").getFile());
        assertTrue(s.runPart1());

        assertTrue(s.runPart2());
    }

//    @Test
    public void algorithmTestOnParse() {
        //Will break if data in excel file breaks syntax rules!

        //Parse file...
        final SupervisorFromExcelFile s = new SupervisorFromExcelFile(
                SequencePlanner.class.getResource("resources/filesForTesting/ExcelTestIndata_ForJUnitTests.xls").getFile());

        //...and run part 1 (from excel file to internal data structure)
        assertTrue(s.runPart1());

        //Check correctness of data----------------------------------------------
        final OperationResourceDataStructure data = s.getDataStructure();

        //Resources
        for (final OperationResourceDataStructure.Resource r : data.mResourceSet) {

            //Test M2
            if (r.mName.equals("m2")) {
                System.out.println("Test resource m2");
                assertTrue("initValue", r.mInitValue.equals("läge1"));
                assertTrue("nbr of values", r.mValueLL.size() == 3);
                assertTrue("via resources for m2.läge1",r.mValueViaMap.get(1) == null); //No via resources for m2.läge1
                assertTrue(r.mValueViaMap.get(2).keySet().size() == 1); //o1 is single via resource for m2.läge2
                final OperationResourceDataStructure.Resource o1 = data.getResourceInSet("o1");
                assertTrue(r.mValueViaMap.get(2).get(o1).size() == 1);
                assertTrue(r.mValueViaMap.get(2).get(o1).contains(o1.mValueLL.indexOf("m2:läge1_m2:läge2::m3:taken")));
            }

            //Test o1
            if (r.mName.equals("o1")) {
                System.out.println("Test resource o1");
                assertTrue("nbr of values", r.mValueLL.size() == 3);
                assertTrue(r.mValueLL.contains("empty"));
                assertTrue(r.mValueLL.contains("m2:läge1_m2:läge2::m3:taken"));
                assertTrue(r.mValueLL.contains("m1:empty::m2:2_m1:taken::m3:taken"));
            }
        }

        //Operations
        for (final OperationResourceDataStructure.Operation op : data.mOperationSet) {

            if(op.mName.equals("op1")) {
                System.out.println("Test operation op1");
                assertTrue(op.mSourceResourceMap.keySet().size() == 2);
                final OperationResourceDataStructure.Resource m2 = data.getResourceInSet("m2");
                assertTrue(op.mSourceResourceMap.get(m2).equals(2));
                final OperationResourceDataStructure.Resource m3 = data.getResourceInSet("m3");
                assertTrue(op.mDestResourceMap.get(m3).equals(m3.mValueLL.indexOf("taken")));
                final OperationResourceDataStructure.Resource o1 = data.getResourceInSet("o1");
                assertTrue(op.mViaResourceMap.containsKey(o1));
                assertTrue(op.mViaResourceMap.keySet().size() == 2);
                assertTrue(op.mViaResourceMap.get(o1).equals(o1.mValueLL.indexOf("m1:empty::m2:2_m1:taken::m3:taken")));

                //extra
                assertTrue(op.mExtraStartConditionMap.keySet().size() == 2);
                assertTrue(op.mExtraStartConditionMap.get("guard").equals("guardex"));
                assertTrue(op.mExtraFinishConditionMap.keySet().size() == 1);
                assertTrue(op.mExtraFinishConditionMap.get("action").equals("actiontyp"));
            }

            if(op.mName.equals("12open")) {
                System.out.println("Test operation 12open");
                //extra
                assertTrue(op.mExtraFinishConditionMap.keySet().size() == 1);
                assertTrue(op.mExtraFinishConditionMap.get("guard").equals("guard1"));
            }
        }
        //-----------------------------------------------------------------------
    }
}
