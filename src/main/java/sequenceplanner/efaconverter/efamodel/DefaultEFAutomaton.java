/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.supremica.automata.ExtendedAutomaton;
import org.supremica.external.avocades.common.Module;

/**
 *
 * @author shoaei
 */
public class DefaultEFAutomaton extends ExtendedAutomaton {

    private Module module;
    private LinkedList<String> events;
    private LinkedList<String> locations;
    private LinkedList<LinkedList<String>> transitions;
    private ExtendedAutomaton automaton;
    private String name;



    public DefaultEFAutomaton(String iName, Module iModule){
        super(iName, iModule, true);
        this.module = iModule;
        this.name = iName;
        locations = new LinkedList<String>();
        events = new LinkedList<String>();
        transitions = new LinkedList<LinkedList<String>>();
    }

    public DefaultEFAutomaton(String iName, IEFAutomata iAutomata){
        this(iName, iAutomata.getModule());
    }

    public void addLocation(String iName){
        this.addLocation(iName, false, false);
    }

    public void addInitialLocation(String iName){
       this.addLocation(iName, false, true);
    }

    public void addInitialLocation(String iName, boolean isAccepting){
       this.addLocation(iName, isAccepting, true);
    }

    public void addAcceptingLocation(String iName){
       this.addLocation(iName, true, false);
    }

    public void addLocation(String iName, boolean isAccepting, boolean isInitial){
       //check in data
       if(iName == null){
           return;
       }else if(iName.length() == 0){
           return;
       }

       //check if we already added this state
       if(locationExist(iName)){
           return;
       }

       //add new state
       super.addState(iName, isAccepting, isInitial);
       locations.add(iName);
    }

    public void addEvent(String iEvent){
       //check in data
       if(iEvent == null || iEvent.length() == 0){
           return;
       }
       //parse event
       //events are separated with ";". For example, event1;event2;
       String[] es = iEvent.split(";");

       //add new event to automata
       for(int i = 0; i < es.length; i++){
           if(!eventExist(es[i]) && !es[i].isEmpty()){
                   module.addEvent(es[i]);
                   events.add(es[i]);
           }
       }
    }

    public void addEvent(String iEvent, String iKind){

       //check in data
       if(iEvent == null ){
           return;
       }else if(iEvent.length() == 0){
           return;
       }

       //parse event
       //events are separated whit ; tex event1;event2
       String[] es = iEvent.split(";");

       //add new event to automata
       for(int i = 0; i < es.length; i++){
           if(!eventExist(es[i])){
                   module.addEvent(es[i], iKind);
                   events.add(es[i]);
           }
       }
    }

    @Override
    public void addTransition(String iSource, String iTarget, String iEvent, String iGuard, String iAction){
       this.addEvent(iEvent);

       //event, guard and action must ends with ";"
       if(iEvent.length() > 0 && !iEvent.endsWith(";")){
           iEvent = iEvent.concat(";");
       }

       if(iAction.length() > 0 && !iAction.endsWith(";")){
           iAction = iAction.concat(";");
       }

       LinkedList<String> transition = new LinkedList<String>();
       transition.add(iSource);
       transition.add(iTarget);
       transition.add(iEvent);
       transition.add(iGuard);
       transition.add(iAction);
       transitions.add(transition);
       super.addTransition(iSource,iTarget,iEvent,iGuard,iAction);
    }

    public boolean eventExist(String iEvent) {
        return events.contains(iEvent);
    }

    public boolean locationExist(String iLocation){
        return locations.contains(iLocation);
    }

    public LinkedList<String> getLocations() {
        return locations;
    }

    public LinkedList<String> getEvents() {
        return events;
    }

    public LinkedList<LinkedList<String>> getTransitions() {
        return transitions;
    }

    public String getName() {
        return name;
    }

    public LinkedList<String[]> regEx(String iPattern, String iText){
        Pattern p = Pattern.compile(iPattern);
        Matcher m = p.matcher(iText);
        LinkedList<String[]> groups = new LinkedList<String[]>();
        while (m.find()) {
            String[] group = new String[m.groupCount()+1];
            if(m.groupCount() == 0){
                group[1]=m.group();
            }else{
                for(int i=1; i<=m.groupCount(); i++){
                    group[i] = m.group(i);
                }
            }
            groups.add(group);
        }

        return groups;
    }
}
