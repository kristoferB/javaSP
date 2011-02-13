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
    }
    public Model getModel() {
        return model;
    }

    public LinkedList getOperationViews() {
        return operationViews;
    }





    public AbstractViewMap getViewMap() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createNewOpView(){
        operationViews.addLast(new OperationView(this.model,"Opereration view " + operationViews.size()));
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

    public void addAllOperations() {
        OperationView ov = new OperationView(this.model,"Operation View");
        ov.open(this.model.getChildren(model.getOperationRoot()));
        operationViews.addLast(ov);
        
			//	OperationView v = createOperationView("ViewAll");
			//	v.open(model.getChildren(model.getOperationRoot()));
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


}
