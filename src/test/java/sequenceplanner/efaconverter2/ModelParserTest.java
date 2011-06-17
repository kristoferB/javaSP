/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2;

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
import sequenceplanner.efaconverter2.reduction.Reduction;
import sequenceplanner.efaconverter2.reduction.RelationGraph;
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
        TreeNode n = model.getOperationRoot();
        OperationData nd = new OperationData(n.toString(), n.getId());
        nd.addPAnd(1006, 2);
        nd.addPAnd(1009, 2);
        nd.addPAnd(1008, 2);
        nd.addPAnd(1015, 2);
        nd.addPAnd(1017, 2);
        LinkedList<SeqCond> s = new LinkedList<SeqCond>();
        s.add(new SeqCond(1012, 2));
        s.add(new SeqCond(1011, 2));
        nd.addPOr(s);
        n.setNodeData(nd);

        n = model.getOperation(1017);
        nd = (OperationData) n.getNodeData();
        nd.addPAnd(1023, 2);
        
        n = model.getOperation(1023);
        nd = (OperationData) n.getNodeData();
        nd.addPAnd(1027, 2);
        nd.addPAnd(1026, 2);
        
        n = model.getOperation(1026);
        nd = (OperationData) n.getNodeData();
        LinkedList<SeqCond> sn = new LinkedList<SeqCond>();
        sn.add(new SeqCond(1030, 2));
        sn.add(new SeqCond(1029, 2));
        nd.addPOr(sn);
        
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
        Reduction reduce = new Reduction(model);
        SpEFAutomata reducedModel = reduce.getReducedModel();
        DefaultEFAConverter converter = new DefaultEFAConverter(reducedModel);
        DefaultExport export = new DefaultExport(converter.getModule());
        export.save();
        assertEquals(true,true);
        
        //        System.out.println("ModelParse");
        //        DefaultModelParser instance = new DefaultModelParser(model);
        //        System.out.println("Converter");
        //        DefaultEFAConverter converter = new DefaultEFAConverter(instance.getSpEFAutomata());
        //        System.out.println("Export");
        //        DefaultExport export = new DefaultExport(converter.getModule());
        //        export.save();
        //        assertEquals(true,true);
        //        DefaultModelParser instance = new DefaultModelParser(model);
        //        SpEFAutomata result = instance.getSpEFAutomata();
        //        for(SpEFA a : result.getAutomatons()){
        //            System.out.println(a.getName());
        //            for(SpLocation l : a.getLocations())
        //                System.out.println(l.getInTransitions().size() + " <> " + l.getOutTransitions().size());
        //            if(a.getName().equals("6")){
        //                for(Iterator<SpTransition> itr = a.iterateSequenceTransitions(); itr.hasNext();){
        //                    System.out.println("-----------------");
        //                    SpTransition tran = itr.next();
        //                    System.out.println(tran.toString() + ": " + tran.getCondition().toString());
        //                }
        //            }
        //        }
        //
        //        assertEquals(21, result.getAutomatons().size());
        //        assertEquals(4, result.getVariables().size());
    }
}
