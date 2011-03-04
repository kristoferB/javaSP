package sequenceplanner.editor;

import java.util.LinkedList;
import sequenceplanner.model.Model;

/**
 * Describes a global property with arbitrary number of text values
 *
 * @author Evelina
 */
public class GlobalProperty implements IGlobalProperty{

    // The name of the property
    private String name;
    private int id;
    // The values of the property
    private LinkedList<Value> values = new LinkedList();

    public GlobalProperty(String n){
        name = n;
        id = Model.newPropertyId();
    }

    public GlobalProperty(String n, LinkedList<String> val){
        name = n;
        id = Model.newPropertyId();
        for(int i = 0; i < val.size(); i++){
            Value v = new Value(val.get(i));
            values.add(v);
        }
    }

    public int getId(){
        return id;
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
    public Value getValue(int i){
        return values.get(i);
    }

    @Override
    public void setValue(int i, Object value){
        if(value instanceof String){
            values.get(i).setName((String) value);
        }
        if(value instanceof Value){
            values.add(i, (Value) value);
        }
    }
    
    @Override
    public void addValue(Object value){
        if(value instanceof String){
            Value v = new Value((String) value);
            values.add(v);
        }
        if(value instanceof Value){
            values.add((Value) value);
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
