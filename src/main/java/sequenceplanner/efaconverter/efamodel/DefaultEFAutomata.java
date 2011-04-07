/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.supremica.automata.ExtendedAutomaton;
import org.supremica.external.avocades.common.Module;

/**
 *
 * @author shoaei
 */
public class DefaultEFAutomata implements IEFAutomata {

    private Module module;
    private HashMap<String, ExtendedAutomaton> automatons;

    public DefaultEFAutomata(String iName){
        module = new Module(iName, false);
        automatons = new HashMap<String, ExtendedAutomaton>();
    }

    @Override
    public void addEvent(String iEvent){
        module.addEvent(iEvent);
    }

    @Override
    public void addEvent(String iName, String iKind){
        module.addEvent(iName, iKind);
    }

    @Override
    public LinkedList<String> getAlphabet(){
        return (LinkedList<String>) module.getEvents();
    }

    @Override
    public void addEFAutomaton(IEFAutomaton iAutomaton) {
        automatons.put(iAutomaton.getName(), (ExtendedAutomaton)iAutomaton);
        module.addAutomaton((ExtendedAutomaton)iAutomaton);
    }

    @Override
    public IEFAutomaton addEFAVariable(String iName, int iMin, int iMax, int iInitialValue) {
        ExtendedAutomaton var = new ExtendedAutomaton(iName, module, true);
        var.addIntegerVariable(iName, iMin, iMin, iInitialValue, null);
        automatons.put(iName, var);
        module.addAutomaton(var);
        return (IEFAutomaton)var;
    }

    @Override
    public IEFAutomaton getEFAutomaton(String iName) {
        return (IEFAutomaton) automatons.get(iName);
    }

    @Override
    public Module getModule() {
        return module;
    }

    public void addEFAutomata(IEFAutomata iAutomata){
        for(String e : iAutomata.getAlphabet())
            this.addEvent(e);

        for(Iterator<IEFAutomaton> itr = iAutomata.iterator(); itr.hasNext();)
            this.addEFAutomaton(itr.next());
    }

    @Override
    public Iterator<IEFAutomaton> iterator() {
        return (Iterator<IEFAutomaton>)(IEFAutomaton) automatons.values().iterator();
    }

}
