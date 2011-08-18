package sequenceplanner.weightNonBlocking;

/**
 *
 * @author patrik
 */
public class Seam {

    public Block toAdd;
    public Block addTo;

    public Seam(Block toAdd, Block addTo) {
        this.toAdd = toAdd;
        this.addTo = addTo;
    }

    public String eventLabel() {
        return "e_" + name() + "_";
    }

    public String name() {
        return (toAdd.mName + "_to_" + addTo.mName).replaceAll(" ", "");
    }

    public String executed() {
        return "v_" + name() + "_executed";
    }
}
