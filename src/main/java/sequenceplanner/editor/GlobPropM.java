package sequenceplanner.editor;


/**
 *
 * @author Evelina
 */
public class GlobPropM {

    private EditorTreeModel globalProperties;

    /*
    public void addProperty(String prop, String[] name){

        GlobalProperty gp = new GlobalProperty(prop, name);
        globalProperties.add(gp);

    }
     *
     * */
    public GlobPropM(){
        globalProperties = new EditorTreeModel();
    }

    public EditorTreeModel getGlobalProperties(){

        return globalProperties;

    }

}