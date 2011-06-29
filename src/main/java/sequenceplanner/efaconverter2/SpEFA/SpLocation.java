
package sequenceplanner.efaconverter2.SpEFA;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author kbe
 * @author Mohammad Reza Shoaei
 * @version 21062011
 */

public class SpLocation {

    private String locationName;
    private Set<SpTransition> inTransitions;
    private Set<SpTransition> outTransitions;
    private boolean isInitialLocation = false;
    private boolean isAccepting = false;
    private int value;
    private boolean visited = false;

    public SpLocation(String locationName) {
        this.locationName = locationName;
        inTransitions = new HashSet<SpTransition>();
        outTransitions = new HashSet<SpTransition>();
        value = -1;
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

    public void clearAccepting(){
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
    
    public void setValue(int value){
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public void setVisited(){
        this.visited = true;
    }
    
    public void clearVisited(){
        this.visited = false;
    }
    
    public boolean isVisited(){
        return this.visited;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj != null && this.getClass() == obj.getClass())
            if(this.locationName.equals(((SpLocation)obj).getName()))
                return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.locationName != null ? this.locationName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        return this.locationName;
    }


}
