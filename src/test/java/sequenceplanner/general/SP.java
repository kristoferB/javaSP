package sequenceplanner.general;

import java.io.File;
import javax.swing.JOptionPane;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.SequencePlanner;
import static org.junit.Assert.*;

/**
 * Help-class for testing.<br\>
 * Creates an instance of SP.<br\>
 * @author patrik
 */
public class SP {

    /**
     * Hold info about all that is shown in the GUIView.<br/>
     * see {@link GUIModel guiModel}
     */
    private GUIModel mGUIModel;
    /**
     * Main view class for the gui package.<br/>
     * see {@link GUIView guiView}
     */
    private GUIView mGUIView;
    /**
     * Main controller in the GUI package.<br/>
     * see {@link GUIController guiController}
     */
    private GUIController mGUIController;
    /**
     * This class will be specialized to keep operations, liasons and resources.<br/>
     * see {@link Model model}
     */
    private Model mModel;

    public SP() {
        mGUIModel = new GUIModel();
        mModel = mGUIModel.getModel();
    }

    /**
     * To init view and controller fields<br/>
     */
    public void initViewAndController() {
        mGUIView = new GUIView(mGUIModel);
        mGUIController = new GUIController(mGUIModel, mGUIView);
    }

    /**
     * @return pointer to the {@link GUIModel} for this SP object
     */
    public GUIModel getGUIModel() {
        return mGUIModel;
    }

    /**
     * @return pointer to the {@link Model} for this SP object
     */
    public Model getModel() {
        return mModel;
    }

    /**
     * @return pointer to the {@link GUIView} for this SP object
     */
    public GUIView getGUIView() {
        return mGUIView;
    }

    /**
     * @return pointer to the {@link GUIController} for this SP object
     */
    public GUIController getGUIController() {
        return mGUIController;
    }

    /**
     * Loads {@link Model model} from .sopx-file. Uses xml unmarshaller in {@link GUIController guiController}.<br/>
     * @param nameOfFile name of .sopx-file
     */
    public void loadFromSOPXFile(String nameOfFile) {
        assertTrue(mGUIModel.openModel(new File(nameOfFile)));
    }

    /**
     * Loads {@link Model model} from a template.sopx-file.<br/>
     * @param nameOfFile name of template.sopx-file
     */
    public void loadFromTemplateSOPXFile(String nameOfFile) {
        loadFromSOPXFile(SequencePlanner.class.getResource(nameOfFile).getFile());
    }

    /**
     * Saves {@link Model model} to .sopx-file. Uses xml marshaller in {@link GUIController guiController}.<br/>
     * @param nameOfFile name of .sopx-file
     */
    public void saveToSOPXFile(String nameOfFile) {
        assertTrue(mGUIModel.saveModelToFile(nameOfFile) instanceof File);
    }

    /**
     * Ugly fix to see current layout (window arrangement).
     */
    public void visualizeGUI() {
        GUIView view = new GUIView(mGUIModel);
        JOptionPane.showMessageDialog(view, "Hello world");
    }

    /**
     * Inserts a new operation as child to root of operation<br/>
     * Default name is "OP" + id<br/>
     * @return the created operation as {@link OperationData}
     */
    public OperationData insertOperation() {
        final Integer count = mModel.getCounter();
        return insertOperation("OP" + count);
    }

    /**
     * Inserts a new operation as child to root of operation
     * @param iName Name for operation
     * @return the created operation as {@link OperationData}
     */
    public OperationData insertOperation(final String iName) {
        Integer idCounter = getUpdatedIdCount();
        OperationData opData = new OperationData(iName, idCounter);
        TreeNode[] toAdd = new TreeNode[1];
        toAdd[0] = new TreeNode(opData);
        mModel.saveOperationData(toAdd);
        return opData;
    }

    /**
     * Updates idcounter with one and returns this new value.
     * @return the new value
     */
    public Integer getUpdatedIdCount() {
        Integer idCount = mModel.getCounter();
        mModel.setCounter(idCount + 1);
        return idCount;
    }
}
