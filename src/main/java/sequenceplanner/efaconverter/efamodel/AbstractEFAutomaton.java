/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.LinkedList;
import org.supremica.automata.ExtendedAutomaton;
import org.supremica.external.avocades.common.Module;
import sequenceplanner.model.data.OperationData.Action;
import sequenceplanner.model.data.OperationData.SeqCond;

/**
 *
 * @author shoaei
 */
abstract class AbstractEFAutomaton{

    private Module module;
    private LinkedList<String> events;
    private LinkedList<String> locations;
    private ExtendedAutomaton exAutomaton;

    public AbstractEFAutomaton(String name, Module automata){
        exAutomaton = new ExtendedAutomaton(name, automata, true);
        module = automata;
        locations = new LinkedList<String>();
        events = new LinkedList<String>();
    }

    public AbstractEFAutomaton(String name){
        this(name, null);
    }


    public void addLocation(String name){
        this.addLocation(name, false, false);
    }

    public void addInitialLocation(String name){
       this.addLocation(name, false, true);
    }

    public void addInitialLocation(String name, boolean accepting){
       this.addLocation(name, accepting, true);
    }

    public void addAcceptingLocation(String name){
       this.addLocation(name, true, false);
    }

    public void addLocation(String name, boolean accepting, boolean initial){
       //check in data
       if(name == null){
           return;
       }else if(name.length() == 0){
           return;
       }

       //check if we already added this state
       if(locationExist(name)){
           return;
       }

       //add new state
       exAutomaton.addState(name, accepting, initial);
       locations.add(name);
    }

    public void addEvent(String event){
       //check in data
       if(event == null || event.length() == 0){
           return;
       }
       //parse event
       //events are separated with ";". For example, event1;event2;
       String[] es = event.split(";");

       //add new event to automata
       for(int i = 0; i < es.length; i++){
           if(!eventExist(es[i]) && !es[i].isEmpty()){
                   if(module != null)
                       module.addEvent(es[i]);
                   events.add(es[i]);
           }
       }
    }

    public void addEvent(String event, String kind){

       //check in data
       if(event == null ){
           return;
       }else if(event.length() == 0){
           return;
       }

       //parse event
       //events are separated whit ; tex event1;event2
       String[] es = event.split(";");

       //add new event to automata
       for(int i = 0; i < es.length; i++){
           if(!eventExist(es[i])){
                   if(module != null)
                       module.addEvent(es[i], kind);
                   events.add(es[i]);
           }
       }
    }

    public void addTransition(String source, String target, String event, String guard, String action){
       this.addEvent(event);

       //event, guard and action must ends whit ;
       if(event.length() > 0 && !event.endsWith(";")){
           event = event.concat(";");
       }

       if(action.length() > 0 && !action.endsWith(";")){
           action = action.concat(";");
       }

       //super
       exAutomaton.addTransition(source,target,event,guard,action);
    }

    public boolean eventExist(String event) {
        return events.contains(event);
    }

    public boolean locationExist(String location){
        return locations.contains(location);
    }

    public ExtendedAutomaton getExtendedAutomaton(){
        return exAutomaton;
    }

    abstract void addLocation(SpLocation location);
    abstract void addEvent(SpEvent event);
    abstract void addTransition(SpTransition transition);
    abstract void addTransition(SpEvent event, SpLocation source, SpLocation target);
    abstract void addTransition(SpEvent event,
                                SpLocation source,
                                SpLocation target,
                                LinkedList<LinkedList<SeqCond>> seqConditions,
                                LinkedList<Action> actions);
    abstract void addTransition(SpEvent event,
                                SpLocation source,
                                SpLocation target,
                                String rawConditions,
                                String[] rawActions);
}
