
package sequenceplanner.efaconverter2.EFA;

import org.supremica.external.avocades.common.Module;

/**
 *
 * @author Mohammad Reza Shoaei
 * @version 21062011
 */
public interface IEFAConverter {
    
    public DefaultEFAutomata getEFAutomata();
    
    public Module getModule();

}
