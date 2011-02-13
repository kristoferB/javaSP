package sequenceplanner.gui.model;

import java.util.LinkedList;
import net.infonode.docking.util.AbstractViewMap;
import sequenceplanner.editor.EditorTreeModel;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.resourceView.ResourceView;

/**
 *Should hold info about all that is to be shown in the GUIView.
 * @author qw4z1
 */
public class GUIModel {
    private ResourceView resourceView;
    private LinkedList<OperationView> operationViews = new LinkedList();
    //Main model for the project
    private Model model;

    /**
     * Constructor. Sets the main project model.
     */
    public GUIModel(){
        this.model = new Model();
        createNewOpView();
    }
    public Model getModel() {
        return model;
    }

    /**
     *
     * @return all OperationViews in a list.
     */
    public LinkedList getOperationViews() {
        return operationViews;
    }

    public void createNewOpView(){
        operationViews.addLast(new OperationView(this.model,"Opereration view " + (operationViews.size()+1)));
    }

    public void createNewReView(){
        resourceView = new ResourceView(this.model,this.model.getResourceRoot(), "Resource view");

    }

    //Crude exit method?
    public void exit(){
        System.exit(0);
    }

    public EditorTreeModel getGlobalProperties(){
        return model.getGlobalProperties();
    }

    /**
     * Adds all current operations to a new OperationView
     */
    public void addAllOperations() {
        OperationView ov = new OperationView(this.model,"Operation View");
        ov.open(this.model.getChildren(model.getOperationRoot()));
        operationViews.addLast(ov);
    }

    public ResourceView getResourceView() {
        return resourceView;
    }

    public void createNewOpView(ViewData toOpen) {
        operationViews.addLast(new OperationView(this.model,toOpen));
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void removeAllOpViews() {
        operationViews.clear();
    }


}
