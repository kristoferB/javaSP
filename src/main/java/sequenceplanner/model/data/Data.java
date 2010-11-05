package sequenceplanner.model.data;

import java.util.Hashtable;

/**
 *
 * @author Erik Ohlson
 */
public class Data  implements Cloneable {

    public static final int FOLDER=0;
    public static final int OPERATION=1;
    public static final int RESOURCE=2;
    public static final int RESOURCE_VARIABLE=3;
    public static final int LIASON=4;
    public static final int VIEW=5;

    private String name;
    private int id;
    private int type;

    private boolean copy = false;

    Hashtable<String, String> attributes;

    public Data(String name, int id) {
       this(name, FOLDER, id);
    }

    public Data(String name, int type, int id) {
        this.name = name;
        this.id = id;
        attributes = new Hashtable<String, String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

       public void setCopy(boolean copy) {
       this.copy = copy;
   }

       public boolean getCopy() {
           return copy;
       }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAttribute(String key) {
        String ret = attributes.get(key);
        if (ret == null) {
            return "";
        }

        return "";
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

   @Override
   public Object clone() {
      return this;
   }

   @Override
   public String toString() {
      return "Id: " + Integer.toString(id) + " Name: " + name;
   }



    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Data) {
            Data t = (Data) obj;

           return getName().equals(t.getName()) && getId() == t.getId();
    
        }

        return false;
    }


}
