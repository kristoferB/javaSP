package sequenceplanner.algorithm.labscheduler;

/**
 *
 * @author kbe
 */
public class Location {
    private static int idCounter = 0;
    private final int id = idCounter + 1;
    private final int clock;
    private final byte[] opsState;
    private final short[] varState;

    public Location(int clock, byte[] opsState, short[] varState) {
        this.clock = clock;
        this.opsState = opsState;
        this.varState = varState;
    }

    public int getClock() {
        return clock;
    }

    public int getId() {
        return id;
    }

    public byte[] getOpsState() {
        return opsState;
    }

    public short[] getVarState() {
        return varState;
    }
    
    

    
    
    
    
    
}
