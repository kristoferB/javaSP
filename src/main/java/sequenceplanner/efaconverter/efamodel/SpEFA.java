/* Comment kb 100705
 * Had to create my own simplified EFA structure since the supremica version do not work!
 * I think it is a good idea anyway to have a clear interface to supremica.
 * 
 * This version handles two different types of guard and actions, one with 
 * strings (used when converting to supremica EFA, and one with Condition. 
 * Should be changed to only Condition!
 *
 *
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is a Extended Finite Automaton model. It consist of a set
 * of locations, a set of transistions 
 * @author kbe
 */
public class SpEFA {

    String efaName;
    SpLocation initialLocation;
    HashMap<String,SpLocation> locations;
    HashMap<String,SpTransition> transitions;
    Set<SpEvent> alphabet;


    public SpEFA(String name, String initialLocation) {
        init(name);
        setInitialLocation(initialLocation);
    }

    public SpEFA(String name){
        init(name);
    }

    private void init(String name){
        this.efaName = name;
        locations = new HashMap<String, SpLocation>();
        transitions = new HashMap<String, SpTransition>();
        alphabet = new HashSet<SpEvent>();
    }

    public SpLocation getInitialLocation(){
        return initialLocation;
    }

    public final void setInitialLocation(String name){
        SpLocation l = locations.get(name);
        if (l != null){
            initialLocation = l;
        } else{
            initialLocation = addLocation(name);
        }
    }

    public String getName(){
        return efaName;
    }

    public SpLocation addLocation(String locationName){
        if (!locations.containsKey(locationName)){
            SpLocation location = new SpLocation(locationName);
            addLocation(location);
        }
        return locations.get(locationName);
    }

    public void addLocation(SpLocation location){
        if (location == null) return;
        if (!locations.containsKey(location.getName())) locations.put(location.getName(), location);
    }

    public void addLocation(SpLocation location, boolean initial){
        addLocation(location);
        if (initial) this.initialLocation = location;
    }
    
    // These addTransition should be handled in a better way! Fix later!

    public SpTransition addTransition(SpLocation from, SpLocation to, String event){
        SpTransition trans = new SpTransition(event, from, to);
        this.alphabet.add(trans.getEvent());
        from.addOutTransition(trans);
        to.addInTransition(trans);
        addLocation(from);
        addLocation(to);
        return trans;
    }

    public SpTransition addTransition(String from, String to, String event, String guard, String action){
        SpLocation fromL = addLocation(from);
        SpLocation toL = addLocation(to);
        SpTransition trans = new SpTransition(event, fromL, toL);
        this.alphabet.add(trans.getEvent());
        fromL.addOutTransition(trans);
        toL.addInTransition(trans);
        trans.setGuard(guard);
        trans.setAction(action);
        return trans;
    }
    
    public void addTransition(SpTransition transition){
        transition = validateTransition(transition);
        this.alphabet.add(transition.getEvent());
        addLocation(transition.getFrom());
        addLocation(transition.getTo());
    }

    // This method tries to keep the EFA consistent. But this must be fixed in a better way
    // redesign the EFA consitency evaluation.
    private SpTransition validateTransition(SpTransition transition){
         if (transition.getFrom() == null || transition.getTo() == null)
            throw new NullPointerException();
        if (locations.containsKey(transition.getFrom().getName()))
            transition.setFrom(locations.get(transition.getFrom().getName()));
        if (locations.containsKey(transition.getTo().getName()))
            transition.setTo(locations.get(transition.getTo().getName()));
         // skipp this test for now, fix later if problems are found.
//         for (SpTransition t : transition.getFrom().getOutTransitions()){
//             if (t.getEventLabel().equals(transition.getEventLabel())){
//                 if (t != transition){
//                     
//                 }
//             }
//         }
         
         return transition;
    }



    public SpLocation getLocation(String name){
        return this.locations.get(name);
    }

    public Set<SpEvent> getAlphabet(){
        return this.alphabet;
    }

    public Iterator<SpLocation> iterateLocations(){
        return locations.values().iterator();
    }

    @Override
    public String toString(){
        return this.getName();
    }



}
