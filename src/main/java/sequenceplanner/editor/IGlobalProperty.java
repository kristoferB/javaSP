package sequenceplanner.editor;

/**
 * Interface for a global property
 *
 * @author Evelina
 */
public interface IGlobalProperty {

    public String getName();

    public void setName(String n);

    public int getId();

    public Value getValue(int i);

    public void setValue(int i, Object value);

    public void addValue(Object value);

    public void removeValue(int i);

    public int getNumberOfValues();

    public int indexOfValue(Value value);

}
