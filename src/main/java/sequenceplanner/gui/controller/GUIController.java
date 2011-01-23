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

    public GUIController(GUIModel m, GUIView v){
        guiModel = m;
        guiView = v;

        guiView.addCreateOpViewListener(new CreateOpViewListener());

    }




    // Inner listener classes
    class CreateOpViewListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }


    }
}
