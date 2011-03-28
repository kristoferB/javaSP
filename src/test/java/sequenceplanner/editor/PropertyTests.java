package sequenceplanner.editor;

import java.util.HashSet;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import static org.junit.Assert.*;

/**
 * To test properties
 * @author patrik
 */
public class PropertyTests {

    public PropertyTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void id85() {
        //Create data------------------------------------------------------------
        SP sp = new SP();
        sp.loadFromTemplateSOPXFile("resources/filesForTesting/fileForTesting.sopx");

        //Create properties
        GlobalProperty gpColor = new GlobalProperty("Color");
        Value blueValue = new Value("blue");
        gpColor.addValue(blueValue);
        Value redValue = new Value("red");
        gpColor.addValue(redValue);
        Value greenValue = new Value("green");
        gpColor.addValue(greenValue);

        GlobalProperty gpLetters = new GlobalProperty("Letters");
        Value aValue = new Value("A");
        gpLetters.addValue(aValue);
        Value bValue = new Value("B");
        gpLetters.addValue(bValue);
        Value cValue = new Value("C");
        gpLetters.addValue(cValue);
        Value dValue = new Value("D");
        gpLetters.addValue(dValue);
        
        //Create operations
        OperationData opA = sp.insertOperation();
        opA.setName("opA");
        final int idOpA = opA.getId();
        OperationData opB = sp.insertOperation();
        opB.setName("opB");
        final int idOpB = opB.getId();

        //Set letters A and B for operation A
        Set<Integer> opAValueSet = new HashSet<Integer>();
        opA.setProperty(aValue.getId(), true);
        opAValueSet.add(aValue.getId());
        opA.setProperty(bValue.getId(), true);
        opAValueSet.add(bValue.getId());
        opA.setProperty(gpLetters.getId(), true);
        opAValueSet.add(gpLetters.getId());

        //Set Color=red and Letters=C for operation B
        Set<Integer> opBValueSet = new HashSet<Integer>();
        opB.setProperty(redValue.getId(), true);
        opBValueSet.add(redValue.getId());
        opB.setProperty(gpColor.getId(), true);
        opBValueSet.add(gpColor.getId());
        opB.setProperty(cValue.getId(), true);
        opBValueSet.add(cValue.getId());
        opB.setProperty(gpLetters.getId(), true);
        opBValueSet.add(gpLetters.getId());

        //Save project
        sp.saveToSOPXFile("C:/Users/patrik/Desktop/result.sopx");

        //-----------------------------------------------------------------------

        //Open a new project and compare inserted data with opened data.---------
        SP sp2 = new SP();

        //Open project
        sp2.loadFromSOPXFile("C:/Users/patrik/Desktop/result.sopx");

        TreeNode td;
        td = sp2.getModel().getOperation(idOpA);
        assertTrue("Op A could not be opened!", td != null);
        OperationData opData = (OperationData) td.getNodeData();
        assertTrue("No properties loaded to opA",opData.getProperties().keySet().size()>0);
        for (final Integer i : opData.getProperties().keySet()) {
            assertTrue(opAValueSet.contains(i));
        }
    }

    /**
     * test of id 100
     */
//    @Test
    public void id100() {
        //Insert property (A) with name Adam
        //Insert property (B) with name Bertil
        //assertTrue(all property names are different);
        //Insert propery (C) with name empty
        //assertTrue(nbr of properties == 2)
        //Insert property (D) with name Ad
        //assertTrue(nbr of properties == 3)
        //Change name of B to empty
        //assertTrue(nbr of properties == 3)
        //assertTrue(A.getName.equals("Adam"))
        //assertTrue(B.getName.equals("Bertil"))
        //assertTrue(C.getName.equals("Ad"))
        //Change name of B to Adam
        //assertTrue(nbr of properties == 3)
        //assertTrue(A.getName.equals("Adam"))
        //assertTrue(B.getName.equals("Bertil"))
        //assertTrue(C.getName.equals("Ad"))
        //Insert value (1) with name ett to A
        //Insert value (2) with name två to A
        //assertTrue(A."nbr of values" == 2)
        //Change name of 1 to empty
        //assertTrue(A."nbr of values" == 2)
        //assertTrue(1.getName.equals("ett"))
        //assertTrue(2.getName.equals("två"))
        //Change name of 1 to två
        //assertTrue(A."nbr of values" == 2)
        //assertTrue(1.getName.equals("ett"))
        //assertTrue(2.getName.equals("två"))
        //Insert value (3) with name ett to B
        //assertTrue(B."nbr of values" == 1)
        //assertTrue(3.getName.equals("ett"))
        //assertTrue(A."nbr of values" == 2)
        //assertTrue(1.getName.equals("ett"))
        //assertTrue(2.getName.equals("två"))
        //Insert value (4) with name empty to C
        //assertTrue(C."nbr of values" == 0)
    }
}
