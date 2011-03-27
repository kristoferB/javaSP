package sequenceplanner.editor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
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
        SP sp = new SP();
        sp.loadFromTemplateSOPXFile("resources/filesForTesting/fileForTesting.sopx");

        //Create properties
        GlobalProperty gpColor = new GlobalProperty("Color");
        gpColor.addValue("blue");
        gpColor.addValue("red");
        gpColor.addValue("green");
        GlobalProperty gpLetters = new GlobalProperty("Letters");
        gpLetters.addValue("A");
        gpLetters.addValue("B");
        gpLetters.addValue("C");
        gpLetters.addValue("D");

        //Create operations
        OperationData opA = sp.insertOperation();
        opA.setName("opA");
        OperationData opB = sp.insertOperation();
        opB.setName("opB");

        //Set letters A and B for operation A
        opA.savePropertySetting(((Value) gpLetters.getValue(gpLetters.indexOfValue("A"))).getId(), true);
        opA.savePropertySetting(((Value) gpLetters.getValue(gpLetters.indexOfValue("B"))).getId(), true);
        opA.savePropertySetting(gpLetters.getId(), true);

        //Set Color=red and Letters=C for operation B
        opB.savePropertySetting(((Value) gpColor.getValue(gpColor.indexOfValue("red"))).getId(), true);
        opB.savePropertySetting(gpColor.getId(), true);
        opB.savePropertySetting(((Value) gpLetters.getValue(gpLetters.indexOfValue("C"))).getId(), true);
        opB.savePropertySetting(gpLetters.getId(), true);
        
        sp.saveToSOPXFile("C:/Users/patrik/Desktop/result.sopx");
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
