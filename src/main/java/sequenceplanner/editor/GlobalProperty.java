/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

/**
 *
 * @author Evelina
 */
public class GlobalProperty implements IGlobalProperty{
    
    private String name;
    private String[] values;
    
    GlobalProperty(String n, String[] val){
        name = n;
        values = val;
    }

    @Override
    public String getName(){
        return name;
    }
    
    @Override
    public String getValue(int i){
        return values[i];
    }

    @Override
    public int getNumberOfValues() {
        return values.length;
    }

    @Override
    public int indexOfValue(Object o) {
        for(int i = 0; i < values.length; i++){
            if(o.equals((Object) values[i])){
                return i;
            }
        }
        return -1;
    }

}
