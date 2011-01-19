/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.globalProperties;

/**
 *
 * @author Evelina
 */
public class GlobPropM {

    private GlobalProperty[] globalProperties;

    public void addProperty(String prop, String[] name){

        GlobalProperty gb = new GlobalProperty(prop, name);

    }

}