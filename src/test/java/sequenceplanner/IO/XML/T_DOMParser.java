/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.IO.XML;

import sequenceplanner.IO.XML.IntentionalXML.ObjectifyVariableIntentionalOldModel;
import sequenceplanner.IO.XML.IntentionalXML.ObjectifyOperationsIntentionalOldModel;
import sequenceplanner.IO.XML.IntentionalXML.ObjectifySOPIntentionalOldModel;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.model.SOP.SopNodeOperation;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;


/**
 *
 * @author kbe
 */
public class T_DOMParser {
    
    public T_DOMParser() {
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
    
    private ISopNode createTestSOP(Model m){
        ISopNode sn = new SopNode();
        List<TreeNode> allOp = m.getAllOperations();
        Set<TreeNode> someOp = new HashSet<TreeNode>();
        Random random = new Random();

        for (int i = 0; i<10 ; i++){
            int r = random.nextInt(allOp.size());
            someOp.add(allOp.get(r));
        }
               
        
        ISopNode last = null;
        ISopNode first = null;
        for (TreeNode n : someOp){
            if (n.getNodeData() instanceof OperationData){
                ISopNode opNode = new SopNodeOperation((OperationData) n.getNodeData());
                if (last == null){ 
                    last = opNode; 
                    first = opNode;
                }
                else {
                    last.setSuccessorNode(opNode);
                    last = opNode;
                }
            }
        }
        sn.addNodeToSequenceSet(first);
        
        
        return sn;
    }
    

    
    
    @Test
    public void parse() throws SAXException, IOException {
        //temp/sp.xml
       ObjectifyOperationsIntentionalOldModel op = new ObjectifyOperationsIntentionalOldModel();
       ObjectifyVariableIntentionalOldModel var = new ObjectifyVariableIntentionalOldModel();
       ObjectifySOPIntentionalOldModel sop = new ObjectifySOPIntentionalOldModel();
       Set<ObjectifyXML> s = new HashSet<ObjectifyXML>(); 
       s.add(op); s.add(var);s.add(sop);
       XMLDOMParser xmlp = new  XMLDOMParser(s,new HashSet<Object>()); 
       Set<Object> models = xmlp.loadModelFromFile("temp/sp.xml") ;      
       
       Model model = null;
       for (Object m : models){
           if (m instanceof Model){
               model = (Model) m;
           }
       }
       
//       ISopNode top = createTestSOP(model);
//       System.out.println(top.toString());
//       
//       ConditionsFromSopNode condGenerator = new ConditionsFromSopNode(top);
//       ConditionData cd = new ConditionData("testSOP");
//       merger(condGenerator.getmOperationConditionMap(),cd);
//       
//       sequenceplanner.general.SP sp= new sequenceplanner.general.SP();
//       Model temp = sp.getModel();
//       temp = model;
//       sp.visualizeGUI();
               
       
    }    
    
    private void merger(Map<OperationData, Map<ConditionsFromSopNode.ConditionType, Condition>> map, ConditionData cd){
        for (OperationData od : map.keySet()){
            od.setConditions(cd, map.get(od));
        }
    }
        
    
}
