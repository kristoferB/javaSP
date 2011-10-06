package sequenceplanner.general;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import sequenceplanner.gui.controller.GUIController;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.SequencePlanner;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.xml.SequencePlannerProjectFile;
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
        final SequencePlannerProjectFile project = mGUIModel.openModel(new File(nameOfFile));
        assertTrue(project != null);
        assertTrue(getGUIController() != null);
        getGUIController().openModel(project);
    }

    /**
     * Loads {@link Model model} from a template.sopx-file.<br/>
     * @param nameOfFile name of template.sopx-file
     */
    public void loadFromTemplateSOPXFile(String nameOfFile) {
        initViewAndController();
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
        final OperationData opData = insertOperation("OP");
        int count = opData.getId();
        opData.setName("OP" + count);
        return opData;
    }

    /**
     * Inserts a new operation as child to root of operation
     * @param iName Name for operation
     * @return the created operation as {@link OperationData}
     */
    public OperationData insertOperation(final String iName) {
        final TreeNode tn = mModel.createModelOperationNode();
        final OperationData opData = (OperationData) tn.getNodeData();
        return opData;
    }

    /**
     * Adds parameter <code>iOpBefore</code> as precondition to parameter <code>iOpAfter</code>.<br/>
     * <code>iOpBefore</code> has to be finished before <code>iOpAfter</code> can start.
     * @param iOpBefore
     * @param iOpAfter
     */
    public static void addSequentialPreCondition(final OperationData iOpBefore, final OperationData iOpAfter) {
        final ConditionStatement cs = new ConditionStatement("id"+Integer.toString(iOpBefore.getId()), ConditionStatement.Operator.Equal, "2");
        final ConditionExpression ce = new ConditionExpression(cs);
        final Condition condition = new Condition();
        condition.setGuard(ce);
        final Map<ConditionType,Condition> map = new HashMap<ConditionType, Condition>();
        map.put(ConditionType.PRE, condition);
        iOpAfter.setConditions(new ConditionData(iOpBefore.getName()+"_"), map);
    }
}
