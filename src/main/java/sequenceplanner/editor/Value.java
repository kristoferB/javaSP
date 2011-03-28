package sequenceplanner.editor;

import sequenceplanner.model.Model;

/**
 * A value for a gloal property
 *
 * @author Evelina
 */
public class Value {
    private String name;
    private int id;

    public Value(String n){
        name = n;
        id = Model.newPropertyId();
    }
    public Value(int id, String n){
        name = n;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }

    public void setName(String n){
        name = n;
    }

    @Override
    public String toString(){
        return name;
    }
}
