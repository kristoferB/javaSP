/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2;

import java.util.Iterator;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sequenceplanner.efaconverter2.efamodel.SpEFA;
import sequenceplanner.efaconverter2.efamodel.SpEFAutomata;
import sequenceplanner.efaconverter2.efamodel.SpTransition;
import sequenceplanner.efaconverter2.efamodel.SpVariable;
import sequenceplanner.general.SP;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;

/**
 *
 * @author shoaei
 */
public class ModelParserTest {
    
    static SP mSP;
    static Model model;
    
    public ModelParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        mSP = new SP();
        mSP.loadFromSOPXFile("src/main/resources/sequenceplanner/resources/filesForTesting/testOperationSequences.sopx");
        model = mSP.getModel();
        TreeNode root = model.getOperationRoot();
        OperationData rootData = new OperationData(root.toString(), root.getId());
        rootData.addAnd(1006, 2);
        rootData.addAnd(1009, 2);
        rootData.addAnd(1008, 2);
        rootData.addAnd(1015, 2);
        rootData.addAnd(1017, 2);
        LinkedList<SeqCond> s = new LinkedList<SeqCond>();
        s.add(new SeqCond(1012, 2));
        s.add(new SeqCond(1011, 2));
        rootData.addOr(s);
        root.setNodeData(rootData);

        rootData.setPSequenceCondition(rootData.getSequenceCondition());
        
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
        System.out.println("getSpEFAutomata");
        ModelParser instance = new ModelParser(model);
        SpEFAutomata result = instance.getSpEFAutomata();
        assertEquals(20, result.getAutomatons().size());
        assertEquals(23, result.getVariables().size());
        
        for(SpEFA a : result.getAutomatons()){
            if(a.getName().equals("OP_1023")){
                for(Iterator<SpTransition> itr = a.iterateTransitions(); itr.hasNext();){
                    System.out.println("-----------------");
                    SpTransition tran = itr.next();
                    System.out.println(tran.getCondition().toString());
                }
            }
        }
        
        
//        OperationData rootData = (OperationData) spModel.getOperationRoot().getNodeData();
//        LinkedList<LinkedList<SeqCond>> pSequenceCondition = rootData.getPSequenceCondition();
//        System.out.println("-----------------");
//        System.out.println(pSequenceCondition.size());
//        for(LinkedList<SeqCond> seq : pSequenceCondition){
//            System.out.println("******************");
//            for(SeqCond s : seq)
//                System.out.println(s.id + " --- "+s.state);
//        }
    }
}
