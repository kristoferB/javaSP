package sequenceplanner.multiProduct;

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
        andGuard(from + TypeVar.EFA_STRICTLY_LARGER_THAN_ZERO);
        addAction(from + TypeVar.EFA_MINUS_ONE);
        addAction(to + TypeVar.EFA_PLUS_ONE);
    }
}
