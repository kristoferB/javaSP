/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author kbe
 */
public class SpLocation {

    String locationName;
    Set<SpTransition> inTransitions;
    Set<SpTransition> outTransitions;

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

    @Override
    public String toString(){
        return this.locationName;
    }


}
