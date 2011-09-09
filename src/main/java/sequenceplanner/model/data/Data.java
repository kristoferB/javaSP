package sequenceplanner.model.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 *
 * @author Erik Ohlson
 */
public class Data extends Observable implements Cloneable {

    public enum Type {

        NAME("name"), DESC("description");
        private final String type;

        Type(String iType) {
            this.type = iType;
        }

        @Override
        public String toString() {
            return type;
        }
    };
    public static final int FOLDER = 0;
    public static final int OPERATION = 1;
    public static final int RESOURCE = 2;
    public static final int RESOURCE_VARIABLE = 3;
    public static final int LIASON = 4;
    public static final int VIEW = 5;

    
    private int id;
    private boolean copy = false;
    private Map<Type, String> mAttributes;

    public Data(String name, int id) {
        mAttributes = new HashMap<Type, String>();
        this.id = id;
        setName(name);
        setDescription("");
    }

    public String getName() {
        return getAttribute(Type.NAME);
    }

    public void setName(String name) {
        setAttribute(Type.NAME, name);
        setChanged();
        notifyObservers(name);
    }

    public String getDescription() {
        return getAttribute(Type.DESC);
    }

    public void setDescription(String iDescription) {
        setAttribute(Type.DESC, iDescription);
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

    public String getAttribute(Type iType) {
        final String ret = mAttributes.get(iType);
        if (ret == null) {
            return "";
        }
        return ret;
    }

    public void setAttribute(Type iType, String value) {
        mAttributes.put(iType, value);
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public String toString() {
        return "Id: " + Integer.toString(id) + " Name: " + getName();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Data) {
            Data t = (Data) obj;

            return getName().equals(t.getName()) && getId() == t.getId();

        }

        return false;
    }
}
