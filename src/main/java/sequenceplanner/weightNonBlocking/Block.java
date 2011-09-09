package sequenceplanner.weightNonBlocking;

/**
 * Internal model of Block.
 * @author patrik
 */
public class Block {

    public Double mWeight;
    public String mName;

    public Block(double mWeight, String mName) {
        this.mWeight = new Double(mWeight);
        this.mName = mName;
    }

    public String variable() {
        return ("v_" + mName).replaceAll(" ", "");
    }
}
