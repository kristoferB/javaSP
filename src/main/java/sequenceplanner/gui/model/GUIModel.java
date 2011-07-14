package sequenceplanner.gui.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import sequenceplanner.model.ConvertFromXML;
import sequenceplanner.model.ConvertToXML;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.utils.SPFileFilter;
import sequenceplanner.view.operationView.Constants;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.resourceView.ResourceView;
import sequenceplanner.xml.SequencePlannerProjectFile;

/**
 *Should hold info about all that is to be shown in the GUIView.
 * @author qw4z1
 */
public class GUIModel {

    //Project file if project is saved so far.
    private File projectFile;
    private ResourceView resourceView;
    private LinkedList<OperationView> operationViews = new LinkedList();
    public String path = "user.dir";
    //Main model for the project
    private Model model;

    /**
     * Constructor. Sets the main project model.
     */
    public GUIModel() {
        this.model = new Model();
    }

    public Model getModel() {
        return model;
    }

    public String getProjectName() {
        if (projectFile != null) {
            String name = projectFile.getName();
            if (name.endsWith(Constants.FILEFORMAT)) {
                final int length = name.lastIndexOf(Constants.FILEFORMAT);
                name = name.substring(0, length);
            }
            return name;
        }
        return "";
    }

    /**
     *
     * @return all OperationViews in a list.
     */
    public LinkedList<OperationView> getOperationViews() {
        return operationViews;
    }

    /**
     * Use this method!!
     * @param toOpen
     * @return
     */
    public OperationView createNewOpView(ViewData toOpen) {
        final OperationView opView = new OperationView(this.model, toOpen);
        operationViews.addLast(opView);
        return opView;
    }

    public ResourceView createNewReView() {
        resourceView = new ResourceView(this.model, this.model.getResourceRoot(), "Resource view");
        return resourceView;
    }

    //Crude exit method?
    public void exit() {
        System.exit(0);
    }

    public ResourceView getResourceView() {
        return resourceView;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void removeAllOpViews() {
        operationViews.clear();
    }

    /**
     * User dialog to select project to open.<br/>
     * @return {@link SequencePlannerProjectFile} or null if problem with xml-parse
     */
    public SequencePlannerProjectFile openModel() {
        JFileChooser fc = new JFileChooser(path);

        //Remember path for next time a FileChooser is opened
        path = fc.getCurrentDirectory().getPath();

        fc.setFileFilter(SPFileFilter.getInstance());
        int answer = fc.showOpenDialog(null);

        if (answer == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            projectFile = file;
            return openModel(file);
        }
        return null;
    }

    /**
     * Parse xml file based on schema.<br/>
     * Schema -> JAXB -> sequenceplanner.xml.<br/>
     * @param inputFile
     * @return {@link SequencePlannerProjectFile} or null if problem with xml-parse
     */
    public SequencePlannerProjectFile openModel(File inputFile) {

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(SequencePlannerProjectFile.class.getPackage().getName());
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            final SequencePlannerProjectFile project = (SequencePlannerProjectFile) unmarshaller.unmarshal(inputFile);

            return project;

        } catch (javax.xml.bind.JAXBException ex) {
            java.util.logging.Logger.getLogger("global").log(
                    java.util.logging.Level.SEVERE, null, ex); // NOI18N
        } catch (ClassCastException ex) {
            System.out.println("Class Cast Error in openModel");
        }
        return null;
    }

    /**
     * Save project.<br/>
     * Project is saved to an earlier set project if parameter <p>saveAs</p>
     * is set to false and an earlier project exist.<br/>
     * Otherwise is the user given a FileChooser in order to choose project to open.
     * @param saveAs true = Show FileChooser,
     * @return true if ok else false
     */
    public boolean saveModel(boolean saveAs) {

        //Check if project has been saved before.
        if (projectFile == null) {
            saveAs = true;
        }

        if (saveAs) {
            String filepath = "";

            JFileChooser fc = new JFileChooser(path);
            fc.setFileFilter(SPFileFilter.getInstance());

            int fileResult = fc.showSaveDialog(null);

            //User aborted?
            if (fc.getSelectedFile() == null) {
                return false;
            }
            path = fc.getSelectedFile().getPath();

            if (fileResult == JFileChooser.APPROVE_OPTION) {
                filepath = fc.getSelectedFile().getAbsolutePath();

                filepath = filepath.endsWith(Constants.FILEFORMAT) ? filepath
                        : filepath + Constants.FILEFORMAT;

                if (filepath.endsWith(Constants.FILEFORMAT)) {

                    projectFile = saveModelToFile(filepath);
                    if (projectFile != null) {
                        return true;
                    }
                }
            }
        } else {
            return saveModelToFile(projectFile);
        }

        return false;
    }

    /**
     * Not used
     */
    public void saveBackup() {
        if (projectFile != null) {
            String path = projectFile.getParent();
            path = path + File.separatorChar + "backup";

            File f = new File(path);
            f.mkdir();

            Calendar c = Calendar.getInstance();
            String date = c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + "" + c.get(Calendar.MINUTE) + "" + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND);

            path = path + File.separatorChar + projectFile.getName() + "_" + date + Constants.FILEFORMAT;
            saveModelToFile(path);
        }
    }

    public File saveModelToFile(String filepath) {
        final File file = new File(filepath);

        try {
            file.createNewFile();
            if (saveModelToFile(file)) {
                return file;
            }

        } catch (IOException ex) {
            System.out.println("File save error\n " + ex.getMessage());
        }
        return null;
    }

    public boolean saveModelToFile(File file) {
        System.out.println("");
        ConvertToXML converter = new ConvertToXML(getModel());
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

    public OperationView getOperationViews(ViewData data) {
        for (OperationView op : operationViews) {
            if (op.getName() == null ? data.getName() == null : op.getName().equals(data.getName())) {
                return op;
            }
        }
        return new OperationView(this.model, data);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
