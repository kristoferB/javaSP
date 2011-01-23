package sequenceplanner.gui.model;

import java.util.LinkedList;
import net.infonode.docking.util.AbstractViewMap;

/**
 *Should hold info about all that is to be shown in the GUIView.
 * @author qw4z1
 */
public class GUIModel {

    LinkedList operationViews = new LinkedList();


    public GUIModel(){

    }

    public AbstractViewMap getViewMap() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createNewOpView(){
        throw new UnsupportedOperationException("Not yet implemented");

    }

}
