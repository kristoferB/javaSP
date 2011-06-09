/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter2.EFA;

import java.util.LinkedList;

/**
 *
 * @author shoaei
 */
public interface IEFAutomaton {

    public enum EFAType {
        ExtendedFiniteAutomaton,
        LocationVariable,
        ResourceVariable,
        LiaisonVariable
    }
    
    public void addLocation(String iName);

    public void addInitialLocation(String iName);

    public void addInitialLocation(String iName, boolean isAccepting);

    public void addAcceptingLocation(String name);

    public void addLocation(String iName, boolean isAccepting, boolean isInitial);

    public void addEvent(String iEvent);

    public void addEvent(String iEvent, String iKind);

    public void addTransition(String iSource, String iTarget, String iEvent, String iGuard, String iAction);

    public LinkedList<String> getLocations();

    public LinkedList<String> getEvents();

    public LinkedList<LinkedList<String>> getTransitions();

    public boolean eventExist(String iEvent);

    public boolean locationExist(String iLocation);

    public String getName();

    public EFAType getType(IEFAutomaton iAutomaton);

}
