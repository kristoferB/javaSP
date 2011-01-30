package sequenceplanner.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;

/**
 *Main controller in the GUI package. Listens for changes calls from the view,
 * changes the model accordingly and finally tells the view to show the updated
 * model.
 * @author qw4z1
 */
public class GUIController {

    //Instances of the model and view.
    private GUIModel guiModel;
    private GUIView guiView;

    public GUIController(GUIModel m, GUIView v) {
        guiModel = m;
        guiView = v;
        guiView.addCOPL(new CreateOpListener());

    }

    private class CreateOpListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            guiModel.createNewOpView();
            guiView.updateViews();
        }
    }


}
