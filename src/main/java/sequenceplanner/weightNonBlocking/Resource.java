package sequenceplanner.weightNonBlocking;

/**
 * Internal model of resource with lift capacity
 * @author patrik
 */
public class Resource {

    public Double mPayload;

    public Resource(double mPayload) {
        this.mPayload = new Double(mPayload);
    }
}
