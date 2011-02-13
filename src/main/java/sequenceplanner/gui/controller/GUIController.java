package sequenceplanner.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.ConvertFromXML;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.xml.SequencePlannerProjectFile;

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
    
    // Filefilter for the project
    private static final FileFilter filter = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".sopx")
                    || f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Sequence Planner Project File";
        }
    };

    public GUIController(GUIModel m, GUIView v) {
        guiModel = m;
        guiView = v;
        addListeners();


    }

    private void addListeners() {
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

    //File menu listenrs
    private class CreateOpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.createNewOpView();
            guiView.addNewOpTab();
        }
    }

    class CreateRVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.createNewReView();
            guiView.addResourceView();
        }
    }

    class ExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.exit();
        }
    }
    //Edit menu listeners

    class PrefListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiView.showPrefPane();

        }
    }

    class AddAllListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.addAllOperations();
            guiView.addNewOpTab();


        }
    }
    //Project menu listeners

    class OpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            openModel();
        }
    }

    class SaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SaveAsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    //Convert menu listeners

    class SaveEFAoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SaveEFArListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SaveCostListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SaveOptimalListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class IdentifyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    //MP menu listeners
    class PrintProductListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class EFAForTListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class UpdateModelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class EFAForMPListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    /**
     * Opens a filechooser and lets the user select a previously created project
     * to open.
     * @return
     */
    private boolean openModel() {
        JFileChooser fc = new JFileChooser("user.dir");

        fc.setFileFilter(filter);
        int answer = fc.showOpenDialog(null);

        if (answer == JFileChooser.APPROVE_OPTION) {
            guiView.closeAllViews();
            openModel(fc.getSelectedFile());
            guiModel.getModel().reloadNamesCache();
            try {
                ViewData toOpen = (ViewData) guiModel.getModel().getViewRoot().getChildAt(0).getNodeData();
                guiModel.createNewOpView(toOpen);
                guiView.addNewOpTab();

            } catch (ClassCastException e) {
                System.out.println("Could not cast first child of viewroot to viewData");
            }
            return true;
        }
        return false;
    }

    private boolean openModel(File inputFile) {

        SequencePlannerProjectFile project = null;

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(SequencePlannerProjectFile.class.getPackage().getName());
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            project = (SequencePlannerProjectFile) unmarshaller.unmarshal(inputFile);

        } catch (javax.xml.bind.JAXBException ex) {
            java.util.logging.Logger.getLogger("global").log(
                    java.util.logging.Level.SEVERE, null, ex); // NOI18N
            return false;
        } catch (ClassCastException ex) {
            System.out.println("Class Cast Error in openModel");
            return false;
        }

        ConvertFromXML con = new ConvertFromXML(guiModel.getModel());
        guiModel.setModel(con.convert(project));

        guiModel.getModel().rootUpdated();

        return false;
    }
}
