package sequenceplanner.efaconverter2.SpEFA;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kbe
 */
public class SpEFAutomata {

    private Map<String, SpEFA> automatons;
    private Map<String, SpEvent> alphabet;
    private Map<String, SpVariable> variables;
    private String name;
    private String EFA_PROJECT;

    public SpEFAutomata(String iName) {
        this.automatons = new HashMap<String, SpEFA>();
        this.alphabet = new HashMap<String, SpEvent>();
        this.variables = new HashMap<String, SpVariable>();
        this.name = iName;
        this.EFA_PROJECT = "";
    }
    
    public SpEFAutomata() {
        this("SequencePlanner Model");
    }
    
    public Collection<SpEvent> getAlphabet() {
        return alphabet.values();
    }

    public void addEvent(SpEvent event) {
        this.alphabet.put(event.getName(), event);
    }

    public Collection<SpEFA> getAutomatons() {
        return automatons.values();
    }

    public void addAutomaton(SpEFA automaton) {
       this.automatons.put(automaton.getName(), automaton);
       for (SpEvent e : automaton.getAlphabet()){
           this.alphabet.put(e.getName(), e);
       }
    }

    public Collection<SpVariable> getVariables() {
        return variables.values();
    }

    public void addVariable(SpVariable variables) {
        this.variables.put(variables.getName(), variables);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void removeAutomaton(String name){
        automatons.remove(name);
        updateAlphabet();
    }
    
    public void updateAlphabet(){
        alphabet.clear();
        for(SpEFA efa : automatons.values())
            for(SpEvent e : efa.getAlphabet())
                alphabet.put(e.getName(), e);        
    }
    public void removeVariable(String name){
        variables.remove(name);
    }
    
    public SpEFA getSpEFA(String name){
        return automatons.get(name);
    }
    
    public SpVariable getSpVariable(String name){
        return variables.get(name);
    }
    
    public void setEFAProjectName(String name){
        EFA_PROJECT = name;
    }
    
    public String getEFAProjectName(){
        return EFA_PROJECT;
    }
    
}
