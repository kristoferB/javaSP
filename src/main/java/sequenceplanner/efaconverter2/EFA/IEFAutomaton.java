
package sequenceplanner.efaconverter2.EFA;

import java.util.LinkedList;

/**
 *
 * @author Mohammad Reza Shoaei
 * @version 21062011
 */

public interface IEFAutomaton {

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

}
