/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import java.util.LinkedList;
import org.supremica.external.avocades.common.Module;

/**
 *
 * @author shoaei
 */
public interface IEFAutomata extends Iterable<IEFAutomaton> {

    public boolean addEFAutomaton(IEFAutomaton iAutomaton);

    public void addEvent(String iEvent);

    public void addEvent(String iName, String iKind);

    public LinkedList<String> getAlphabet();

    public IEFAutomaton getEFAutomaton(String iName);

    public void addEFAutomata(IEFAutomata iAutomata);

    public boolean addIntegralVariable(String iName, int iMin, int iMax, int iInitialValue);

    public Module getModule();

}
