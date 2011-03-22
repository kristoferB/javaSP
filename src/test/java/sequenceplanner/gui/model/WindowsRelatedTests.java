package sequenceplanner.gui.model;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.general.SP;
import sequenceplanner.model.data.ViewData;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class WindowsRelatedTests {

    //Instance of SP
    SP mSP = new SP();

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
        mSP.loadFromTemplateSOPXFile("resources/filesForTesting/fileForTesting.sopx");
    }

//    /**
//     * test of id 99 (manually testing is also needed)
//     */
//    @Test
//    public void id99() {
//        int childWindows = windows.getStatistics(WindowInfoWrapper.NBR_OF_OPEN);
//        //Close/Hide object root window
//        //Close/Hide sop root window
//        assertTrue("Can't close windows!", windows.getStatistics(WindowInfoWrapper.NBR_OF_OPEN) == (childWindows - 2));
//        //Open/unhide sop root window
//        //Open/unhide object root window
//        assertTrue("Can't open windows!", windows.getStatistics(WindowInfoWrapper.NBR_OF_OPEN) == childWindows);
//    }
    /**
     * test of id 88<br/>
     * Opens all SOP views for a project and checks that they are added to correct root window.<br/>
     * The views are opened two times in order to check that a view can not be added mutiple times.<br/>
     */
    @Test
    public void id88() {
        int nbrOfSOPViewsInOperationRootWindowAtState = mSP.getGUIView().getSOPViewMap().getViewCount();

        for (int i = 0; i < mSP.getModel().getViewRoot().getChildCount(); ++i) {
            ViewData vd = (ViewData) mSP.getModel().getViewRoot().getChildAt(i).getNodeData();
            mSP.getGUIController().addNewOpTab(vd);
        }

        for (int i = 0; i < mSP.getModel().getViewRoot().getChildCount(); ++i) {
            ViewData vd = (ViewData) mSP.getModel().getViewRoot().getChildAt(i).getNodeData();
            mSP.getGUIController().addNewOpTab(vd);
        }

        int nbrOfSOPViewsInOperationRootWindow = mSP.getGUIView().getSOPViewMap().getViewCount();
        System.out.println("nbrOfSOPViewsInOperationRootWindow: " + nbrOfSOPViewsInOperationRootWindow);

        int nbrOfSOPViewsInViewRootTree = mSP.getModel().getViewRoot().getChildCount();
        System.out.println("nbrOfSOPViewsInViewRootTree: " + nbrOfSOPViewsInViewRootTree);

        assertTrue((nbrOfSOPViewsInOperationRootWindow - nbrOfSOPViewsInOperationRootWindowAtState) == nbrOfSOPViewsInViewRootTree);

    }
}
