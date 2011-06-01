package sequenceplanner.efaconverter2.efamodel;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author kbe
 */
public class SpLocation {

    private String locationName;
    private Set<SpTransition> inTransitions;
    private Set<SpTransition> outTransitions;
    private boolean isInitialLocation = false;
    private boolean isAccepting = false;

    public SpLocation(String locationName) {
        this.locationName = locationName;
        inTransitions = new HashSet<SpTransition>();
        outTransitions = new HashSet<SpTransition>();
    }

    public Set<SpTransition> getInTransitions() {
        return inTransitions;
    }

    public void addInTransition(SpTransition inTransition) {
        this.inTransitions.add(inTransition);
    }

    public String getName() {
        return locationName;
    }

    public void setName(String locationName) {
        this.locationName = locationName;
    }

    public Set<SpTransition> getOutTransitions() {
        return outTransitions;
    }

    public void addOutTransition(SpTransition outTransition) {
        this.outTransitions.add(outTransition);
    }

    public boolean hasOutTransition(){
        return !outTransitions.isEmpty();
    }

    public boolean hasInTransition(){
        return !inTransitions.isEmpty();
    }
    
    public void setAccepting(){
        this.isAccepting = true;
    }

    public void setNotAccepting(){
        this.isAccepting = false;
    }
    
    public void setInitialLocation(){
        this.isInitialLocation = true;
    }

    public void setNotInitialLocation(){
        this.isInitialLocation = false;
    }

    public boolean isAccepting(){
        return this.isAccepting;
    }
    
    public boolean isInitialLocation(){
        return isInitialLocation;
    }
    
    @Override
    public String toString(){
        return this.locationName;
    }


}
