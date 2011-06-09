/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter2;

import org.supremica.external.avocades.common.Module;
import sequenceplanner.efaconverter2.EFA.DefaultEFAutomata;


/**
 *
 * @author shoaei
 */
public interface IEFAConverter {
    
    public DefaultEFAutomata getEFAutomata();
    
    public Module getModule();

}
