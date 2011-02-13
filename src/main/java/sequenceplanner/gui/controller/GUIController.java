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
        addListeners();


    }
    private void addListeners(){
        guiView.addCreateOPL(new CreateOpListener());
        guiView.addCreateRVL(new CreateRVListener());
        guiView.addExitL(new ExitListener());
        guiView.addPrefL(new PrefListener());
        guiView.addAddCellsL(new AddAllListener());
        guiView.addOpenL(new OpenListener());
        guiView.addSaveL(new SaveListener());
        guiView.addSaveAsL(new SaveAsListener());
        guiView.addCloseL(new CloseListener());
        guiView.addSaveEFAoL(new SaveEFAoListener());
        guiView.addSaveEFArL(new SaveEFArListener());
        guiView.addSaveCostL(new SaveCostListener());
        guiView.addSaveOptAutomataL(new SaveOptimalListener());
        guiView.addIdentifyRL(new IdentifyListener());
        guiView.addPrintProdTypesL(new PrintProductListener());
        guiView.addEFAForTransL(new EFAForTListener());
        guiView.addUpdateModelL(new UpdateModelListener());
        guiView.addEFAForMPL(new EFAForMPListener());
        guiView.addEditorListener();
    }
 //Listener classes

    private class CreateOpListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            guiModel.createNewOpView();
            guiView.addNewOpTab();
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
            guiView.showPrefPane();
            
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
    class SaveAsListener implements ActionListener{

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
