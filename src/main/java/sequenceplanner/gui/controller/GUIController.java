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

    class CreateRVListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            guiModel.createNewReView();
            guiView.updateViews();
        }
    }
    class ExitListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            guiModel.exit();
        }
    }
    class PrefListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

        }

    }
    class AddAllListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class OpenListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    class SaveListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class SaveAllListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class CloseListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class SaveEFAoListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class SaveEFArListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class SaveCostListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class SaveOptimalListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class IdentifyListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class PrintProductListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class EFAForTListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class UpdateModelListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    class EFAForMPListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
 

}
