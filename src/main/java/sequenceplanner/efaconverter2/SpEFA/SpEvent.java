
package sequenceplanner.efaconverter2.SpEFA;

/**
 *
 * @author kbe
 */
public class SpEvent {

    private String eventName;
    private boolean controllable;

    public SpEvent(String eventName) {
        this.eventName = eventName;
        controllable = true;
    }

    public SpEvent(String eventName, boolean controllable) {
        this.eventName = eventName;
        this.controllable = controllable;
    }

    public boolean isControllable() {
        return controllable;
    }

    public void setControllable(boolean controllable) {
        this.controllable = controllable;
    }

    public String getName() {
        return eventName;
    }

    public void setName(String eventName) {
        this.eventName = eventName;
    }







}
