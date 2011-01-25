/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import java.util.LinkedList;

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