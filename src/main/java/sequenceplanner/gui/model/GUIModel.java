package sequenceplanner.gui.model;

import java.util.LinkedList;
import net.infonode.docking.util.AbstractViewMap;
import sequenceplanner.model.Model;
import sequenceplanner.view.operationView.OperationView;

/**
 *Should hold info about all that is to be shown in the GUIView.
 * @author qw4z1
 */
public class GUIModel {

    private LinkedList operationViews = new LinkedList();
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



}
