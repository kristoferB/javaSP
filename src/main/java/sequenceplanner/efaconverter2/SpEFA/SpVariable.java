
package sequenceplanner.efaconverter2.SpEFA;

/**
 *
 * @author kbe
 */
public class SpVariable {

    private String name;
    private int min;
    private int max;
    private int init;

    public SpVariable(String name, int min, int max, int init) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.init = init;
    }

    public int getInit() {
        return init;
    }

    public void setInit(int init) {
        this.init = init;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    

}
