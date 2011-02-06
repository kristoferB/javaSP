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
public class GlobalProperty implements IGlobalProperty{
    
    private String name;
    private LinkedList<String> values = new LinkedList();

    public GlobalProperty(String n){
        name = n;
    }

    public GlobalProperty(String n, LinkedList<String> val){
        name = n;
        values = val;
    }

    @Override
    public String getName(){
        return name;
    }
    
    @Override
    public void setName(String n){
        name = n;
    }

    @Override
    public String getValue(int i){
        return values.get(i);
    }

    @Override
    public void setValue(int i, Object value){
        if(value instanceof String){
            values.set(i, (String) value);
        }
    }
    
    @Override
    public void addValue(Object value){
        if(value instanceof String){
            values.add((String) value);
        }
    }

    @Override
    public void removeValue(int i){
        values.remove(i);
    }

    @Override
    public int getNumberOfValues() {
        return values.size();
    }

    @Override
    public int indexOfValue(Object o) {
        for(int i = 0; i < values.size(); i++){
            if(o.equals((Object) values.get(i))){
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString(){
        return getName();
    }

}
