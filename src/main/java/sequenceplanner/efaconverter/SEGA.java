package sequenceplanner.efaconverter;

import org.supremica.external.avocades.common.EGA;

/**
 * Help class for creation of transitions with event, guards and actions
 * @author patrik
 */
public class SEGA extends EGA {

    public SEGA() {
        super();
    }

    public SEGA(String event) {
        super(event);
    }

    /**
     * To book <i>to</i> and unbook <i>from</i> if object is in <i>from</i><br/>
     * @param from object is here
     * @param to object should go here
     */
    public void addBasicPositionBookAndUnbook(String from, String to) {
        if (from.length() > "".length()) {
            andGuard(from + ">0");
            addAction(from + "-1");
        }
        if (to.length() > "".length()) {
            addAction(to + "+1");
        }
    }
}

