package sequenceplanner.gui.model;

import java.util.LinkedList;
import net.infonode.docking.util.AbstractViewMap;
import sequenceplanner.editor.EditorTreeModel;
import sequenceplanner.model.Model;
import sequenceplanner.view.operationView.OperationView;

/**
 *Should hold info about all that is to be shown in the GUIView.
 * @author qw4z1
 */
public class GUIModel {

    private LinkedList<OperationView> operationViews = new LinkedList();
    //Main model for the project
    private Model model;

    public LinkedList getOperationViews() {
        return operationViews;
    }



    public GUIModel(){
        this.model = new Model();
    }

    public AbstractViewMap getViewMap() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createNewOpView(){
        operationViews.addLast(new OperationView(this.model,"nama"));
        System.out.println(operationViews.toString());
    }

    public void createNewReView(){
        throw new UnsupportedOperationException("Not yet implemented");

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


}
