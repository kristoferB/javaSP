package sequenceplanner.gui.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.event.TreeModelListener;
import sequenceplanner.editor.EditorTreeModel;
import sequenceplanner.model.ConvertFromXML;
import sequenceplanner.model.ConvertToXML;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.utils.SPFileFilter;
import sequenceplanner.view.operationView.Constansts;
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
    //Main model for the project
    private Model model;

    /**
     * Constructor. Sets the main project model.
     */
    public GUIModel() {
        this.model = new Model();
        createNewOpView();
    }

    public Model getModel() {
        return model;
    }

    /**
     *
     * @return all OperationViews in a list.
     */
    public LinkedList<OperationView> getOperationViews() {
        return operationViews;
    }

    public void createNewOpView() {
        operationViews.addLast(new OperationView(this.model, "Opereration view " + (operationViews.size() + 1)));
    }


    public void createNewOpView(ViewData toOpen) {
        operationViews.addLast(new OperationView(this.model, toOpen));
    }
    
    public void createNewReView() {
        resourceView = new ResourceView(this.model, this.model.getResourceRoot(), "Resource view");

    }

    //Crude exit method?
    public void exit() {
        System.exit(0);
    }

    public EditorTreeModel getGlobalProperties() {
        return model.getGlobalProperties();
    }

    public void addTreeModelListener(TreeModelListener l) {
        model.addTreeModelListener(l);
    }

    /**
     * Adds all current operations to a new OperationView
     */
    public void addAllOperations() {
        OperationView ov = new OperationView(this.model, "Operation View");
        ov.open(this.model.getChildren(model.getOperationRoot()));
        operationViews.addLast(ov);
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

    public boolean openModel() {
        JFileChooser fc = new JFileChooser("user.dir");

        fc.setFileFilter(SPFileFilter.getInstance());
        int answer = fc.showOpenDialog(null);
                removeAllOpViews();

        if (answer == JFileChooser.APPROVE_OPTION) {
            openModel(fc.getSelectedFile());
            getModel().reloadNamesCache();
            try {

                ViewData toOpen = (ViewData) getModel().getViewRoot().getChildAt(0).getNodeData();
                if (operationViews.size() == 0) {
                    createNewOpView(toOpen);
                } else {
                    createNewOpView(toOpen);
                }
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

        ConvertFromXML con = new ConvertFromXML(getModel());
        setModel(con.convert(project));

        getModel().rootUpdated();

        return true;
    }



    public boolean saveModel(boolean saveAs) {

        if (projectFile == null && !saveAs) {
            saveAs = true;
        }

        if (saveAs) {
            String filepath = "";

            JFileChooser fc = new JFileChooser("user.dir");
            fc.setFileFilter(SPFileFilter.getInstance());

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


}
