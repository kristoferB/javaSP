
package sequenceplanner.efaconverter2;

import sequenceplanner.efaconverter2.export.DefaultExport;
import sequenceplanner.efaconverter2.SpEFA.DefaultModelParser;
import sequenceplanner.efaconverter2.EFA.DefaultEFAConverter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sequenceplanner.efaconverter2.SpEFA.SpEFA;
import sequenceplanner.efaconverter2.SpEFA.SpEFAutomata;
import sequenceplanner.efaconverter2.SpEFA.SpLocation;
import sequenceplanner.efaconverter2.SpEFA.SpTransition;
import sequenceplanner.efaconverter2.SpEFA.SpVariable;
import sequenceplanner.efaconverter2.condition.ConditionOperator;
import sequenceplanner.efaconverter2.condition.ConditionStatment;
import sequenceplanner.efaconverter2.reduction.Reduction;
import sequenceplanner.efaconverter2.reduction.RelationGraph;
import sequenceplanner.general.SP;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;

/**
 *
 * @author Mohammad Reza Shoaei
 * @version 21062011
 */

public class ModelParserTest {
    
    
    public ModelParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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

    /**
     * Test of getSpEFAutomata method, of class ModelParser.
     */
    @Test
    public void testGetSpEFAutomata() {
        SP mSP = new SP();
        mSP.loadFromSOPXFile("src/main/resources/sequenceplanner/resources/filesForTesting/PPU_NEW_v2_LongSeq.sopx");
        Model model = mSP.getModel();        
        
        DefaultModelParser parser = new DefaultModelParser(model);
        SpEFAutomata auto = parser.getSpEFAutomata();
        SpEFA e17 = auto.getSpEFA("17");
        SpEFA e14 = auto.getSpEFA("14");
        SpEFA e11 = auto.getSpEFA("11");
        SpEFA e8 = auto.getSpEFA("8");
        SpEFA e21 = auto.getSpEFA("21");
        SpEFA e22 = auto.getSpEFA("22");
        
        SpTransition t17 = e17.iterateSequenceTransitions().next();
        SpTransition t14 = e14.iterateSequenceTransitions().next();
        SpTransition t11 = e11.iterateSequenceTransitions().next();
        SpTransition t8 = e8.iterateSequenceTransitions().next();
        SpTransition t21 = e21.iterateSequenceTransitions().next();
        SpTransition t22 = e22.iterateSequenceTransitions().next();
        
        t17.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e21.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        t14.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e21.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        t11.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e21.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        t8.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e21.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e17.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e14.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e11.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e8.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(1)));
        
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e17.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(2)));
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e14.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(2)));
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e11.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(2)));
        t21.getConditionGuard().appendElement(ConditionOperator.Type.AND, 
                                                new ConditionStatment(e8.getName(), 
                                                ConditionStatment.Operator.NotEqual, 
                                                Integer.toString(2)));
        

        DefaultEFAConverter converter2 = new DefaultEFAConverter(auto);
        DefaultExport export2 = new DefaultExport(converter2.getModule());
        export2.save();

        Reduction reduce = new Reduction(auto);
        DefaultEFAConverter converter = new DefaultEFAConverter(reduce.getReducedModel());
        DefaultExport export = new DefaultExport(converter.getModule());
        export.save();
        assertEquals(true,true);
    }
}
