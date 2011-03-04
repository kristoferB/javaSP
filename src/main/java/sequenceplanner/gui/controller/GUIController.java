package sequenceplanner.gui.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import sequenceplanner.gui.model.GUIModel;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.ConvertFromXML;
import sequenceplanner.model.ConvertToXML;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.Constansts;
import sequenceplanner.xml.SequencePlannerProjectFile;

/**
 *Main controller in the GUI package. Listens for changes calls from the view,
 * changes the model accordingly and finally tells the view to show the updated
 * model.
 * @author qw4z1
 */
public class GUIController {

    //Project file if project is saved so far.
    File projectFile;
    //Instances of the model and view.
    private GUIModel guiModel;
    private GUIView guiView;
    // Filefilter for the project
    private static final FileFilter filter = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".sopx") || f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Sequence Planner Project File";
        }
    };

    public GUIController(GUIModel m, GUIView v) {
        guiModel = m;
        guiView = v;
        guiModel.createNewOpView();
        addNewOpTab();
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

    //private methods
    private void addNewOpTab(){
         guiView.addNewOpTab(guiModel.getOperationViews().getLast().toString(),(Component) guiModel.getOperationViews().getLast());
    }

    //File menu listenrs
    private class CreateOpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiModel.createNewOpView();
            addNewOpTab();
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
            addNewOpTab();


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
            saveModel(false);
        }
    }

    class SaveAsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            saveModel(false);
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
            openModel(fc.getSelectedFile());
            guiModel.getModel().reloadNamesCache();
            try {
                ViewData toOpen = (ViewData) guiModel.getModel().getViewRoot().getChildAt(0).getNodeData();
                guiModel.removeAllOpViews();
                guiModel.createNewOpView(toOpen);
                addNewOpTab();

            } catch (ClassCastException e) {
                System.out.println("Could not cast first child of viewroot to viewData");
            }
            return true;
        }
        return false;
    }

    public boolean openModel(File inputFile) {

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

        return true;
    }

    private boolean saveModel(boolean saveAs) {

        if (projectFile == null && !saveAs) {
            saveAs = true;
        }

        if (saveAs) {
            String filepath = "";

            JFileChooser fc = new JFileChooser("user.dir");
            fc.setFileFilter(filter);

            int fileResult = fc.showSaveDialog(null);

            if (fileResult == JFileChooser.APPROVE_OPTION) {
                filepath = fc.getSelectedFile().getAbsolutePath();

                filepath = filepath.endsWith(Constansts.FILEFORMAT) ? filepath
                        : filepath + Constansts.FILEFORMAT;

                if (filepath.endsWith(Constansts.FILEFORMAT)) {

                    projectFile = saveModelToFile(filepath);
                    return true;
                }
            }
        } else {
            return saveModelToFile(projectFile);
        }

        return false;
    }

    public void saveBackup() {
        if (projectFile != null) {
            String path = projectFile.getParent();
            path = path + File.separatorChar + "backup";

            File f = new File(path);
            f.mkdir();

            Calendar c = Calendar.getInstance();
            String date = c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + "" + c.get(Calendar.MINUTE) + "" + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND);

            path = path + File.separatorChar + projectFile.getName() + "_" + date + Constansts.FILEFORMAT;
            saveModelToFile(path);
        }
    }

    public File saveModelToFile(String filepath) {
        File file = new File(filepath);

        try {
            file.createNewFile();
            saveModelToFile(file);
            return file;

        } catch (IOException ex) {
            System.out.println("File save error\n " + ex.getMessage());
            return null;
        }
    }

    public boolean saveModelToFile(File file) {
        ConvertToXML converter = new ConvertToXML(guiModel.getModel());
        SequencePlannerProjectFile project = converter.convert();

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(project.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING,
                    "UTF-8"); // NOI18N
            marshaller.setProperty(
                    javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            marshaller.marshal(project, new FileOutputStream(file));
            return true;

        } catch (javax.xml.bind.JAXBException ex) {
            throw new RuntimeException("File save error\n " + ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
