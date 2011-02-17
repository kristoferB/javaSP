package sequenceplanner.gui.model;

import java.io.File;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.model.ConvertFromXML;
import sequenceplanner.SequencePlanner;
import sequenceplanner.xml.SequencePlannerProjectFile;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class WindowsRelatedTests {

    WindowInfoWrapper windows = new WindowInfoWrapper();
    //Instances of the model and view.
    GUIModel guiModel = new GUIModel();

    public WindowsRelatedTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void beforeTest() {
        openFile(SequencePlanner.class.getResource("resources/filesForTesting/fileForTesting.sopx").getFile());
//        openFile("C:/cygwin/home/patrik/Sequence-Planner/src/main/resources/sequenceplanner/resources/filesForTesting/fileForTesting.sopx");
        //reinitialize "windows"
    }

    /**
     * test of id 99 (manually testing is also needed)
     */
    @Test
    public void id99() {
        int childWindows = windows.getStatistics(WindowInfoWrapper.NBR_OF_OPEN);
        //Close/Hide object root window
        //Close/Hide sop root window
        assertTrue("Can't close windows!", windows.getStatistics(WindowInfoWrapper.NBR_OF_OPEN) == (childWindows - 2));
        //Open/unhide sop root window
        //Open/unhide object root window
        assertTrue("Can't open windows!", windows.getStatistics(WindowInfoWrapper.NBR_OF_OPEN) == childWindows);
    }

    /**
     * test of id 88
     */
    @Test
    public void id88() {

        for(WindowInfoWrapper window : windows.children) {
            //guiModel open window "window"
        }

        assertTrue("SOP views are not opened correct!",windows.getStatistics(windows.NBR_OF)==windows.getStatistics(windows.NBR_OF_OPEN));
    }

    public void openFile(String nameOfFile) {
        SequencePlannerProjectFile project = null;

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(SequencePlannerProjectFile.class.getPackage().getName());
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            project = (SequencePlannerProjectFile) unmarshaller.unmarshal(new File(nameOfFile));

        } catch (javax.xml.bind.JAXBException ex) {
            fail(ex.toString());

        } catch (ClassCastException ex) {
            fail(ex.toString());
        }

        ConvertFromXML con = new ConvertFromXML(guiModel.getModel());
        guiModel.setModel(con.convert(project));
        guiModel.getModel().rootUpdated();
    }
}