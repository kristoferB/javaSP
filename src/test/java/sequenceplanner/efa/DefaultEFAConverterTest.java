/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efa;

import org.supremica.external.avocades.common.Module;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequenceplanner.efaconverter.efamodel.DefaultEFAConverter;
import sequenceplanner.efaconverter.efamodel.SpEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.efaconverter.efamodel.SpEvent;
import sequenceplanner.efaconverter.efamodel.SpLocation;
import static org.junit.Assert.*;

/**
 *
 * @author Mohammad Reza
 */
public class DefaultEFAConverterTest {
    
    static SpEFAutomata automata;
    
    public DefaultEFAConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        automata = new SpEFAutomata("Test EFA");
        SpEFA efa = new SpEFA("Test EFA");
        
        SpLocation iL = new SpLocation("5_i");
        iL.setInitialLocation();
        iL.setAccepting();
        
        SpLocation eL = new SpLocation("5_e");
        SpLocation fL = new SpLocation("5_f");
        fL.setAccepting();
        
        SpEvent startE = new SpEvent("start", true);
        SpEvent stopE = new SpEvent("stop", true);
        
        efa.addTransition(iL.getName(), eL.getName(), startE.getName(), "V_Op6==0", "V_Op5=1");
        efa.addTransition(eL.getName(), fL.getName(), startE.getName(), "", "V_Op5=2");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void convert(){
    }
}
