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
    private GUIModel guiModel;
    /**
     * Main view class for the gui package.<br/>
     * see {@link GUIView guiView}
     */
    private GUIView guiView;
    /**
     * Main controller in the GUI package.<br/>
     * see {@link GUIController guiController}
     */
    private GUIController guiController;
    /**
     * This class will be specialized to keep operations, liasons and resources.<br/>
     * see {@link Model model}
     */
    private Model model;

    public SP() {
        guiModel = new GUIModel();
        model = guiModel.getModel();
        guiView = new GUIView(guiModel);
        guiController = new GUIController(guiModel, guiView);
    }

    /**
     * @return {@link GUIModel guiModel}
     */
    public GUIModel getGUIModel() {
        return guiModel;
    }

    /**
     * @return {@link Model model}
     */
    public Model getModel() {
        return model;
    }

    /**
     * Loads {@link Model model} from .sopx-file. Uses xml unmarshaller in {@link GUIController guiController}.<br/>
     * @param nameOfFile name of .sopx-file
     */
    public void loadFromSOPXFile(String nameOfFile) {
        assertTrue(guiModel.openModel(new File(nameOfFile)));
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
        assertTrue(guiModel.saveModelToFile(nameOfFile) instanceof File);
    }

    /**
     * Ugly fix to see current layout (window arrangement).
     */
    public void visualizeGUI() {
        GUIView view = new GUIView(guiModel);
        JOptionPane.showMessageDialog(view, "Hello world");
    }

    /**
     * Inserts a new operation as child to root of operation-
     * @return the created operation as {@link OperationData}
     */
    public OperationData insertOperation() {
        Integer idCounter = getUpdatedIdCount();
        OperationData opData = new OperationData("OP" + idCounter, idCounter);
        model.getOperationRoot().insert(new TreeNode(opData));
        return opData;
    }

    public Integer getUpdatedIdCount() {
        Integer idCount = model.getCounter();
        model.setCounter(idCount+1);
        return idCount;
    }
}
