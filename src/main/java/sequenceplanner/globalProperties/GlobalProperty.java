/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.globalProperties;

/**
 *
 * @author Evelina
 */
public class GlobalProperty {
    
    private String name;
    private String[] values;
    
    GlobalProperty(String n, String[] val){
        name = n;
        values = val;
    }
    
    public String getName(){
        return name;
    }
    
    public String[] getValues(){
        return values;
    }

}
