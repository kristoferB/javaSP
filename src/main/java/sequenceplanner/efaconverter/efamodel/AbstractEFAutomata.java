/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.Iterator;
import java.util.LinkedList;
import org.supremica.automata.ExtendedAutomaton;
import org.supremica.external.avocades.common.Module;

/**
 *
 * @author shoaei
 */
abstract class AbstractEFAutomata {

    private Module module;
    private LinkedList<ExtendedAutomaton> exAutomata;
    private LinkedList<ExtendedAutomaton> variables;

    public AbstractEFAutomata(String name){
        module = new Module(name, false);
    }

    public void addExtendedAutomaton(ExtendedAutomaton EFAutomaton){
        exAutomata.add(EFAutomaton);
        module.addAutomaton(EFAutomaton);
    }

    public void addVariable(String name, int min, int max, int initialValue){
        ExtendedAutomaton var = new ExtendedAutomaton(name, module, true);
        var.addIntegerVariable(name, min, min, initialValue, null);
        variables.add(var);
        module.addAutomaton(var);
    }

    public void addEvent(String event){
        module.addEvent(event);
    }

    public void addEvent(String name, String kind){
        module.addEvent(name, kind);
    }

    public LinkedList<String> getAlphabet(){
        return (LinkedList<String>) module.getEvents();
    }

    public LinkedList<ExtendedAutomaton> getVariables(){
        return variables;
    }

    public Module getModule(){
        return module;
    }

    public Iterator<ExtendedAutomaton> exAutomataIterator(){
        return exAutomata.iterator();
    }

    public Iterator<ExtendedAutomaton> variablesIterator(){
        return variables.iterator();
    }

    abstract void addExtendedAutomaton(SpEFA spEFA);
    abstract void addVariable(SpVariable spVariable);
    abstract void addEvent(SpEvent spEvent);

    abstract LinkedList<SpEFA> getSpEFA();
    abstract LinkedList<SpVariable> getSpVariables();
    abstract LinkedList<SpEvent> getSpAlphabet();


}
