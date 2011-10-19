/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.IO.XML;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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


/**
 *
 * @author kbe
 */
public class DOMParser {
    
    public DOMParser() {
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
    
    @Test
    public void parse() throws SAXException, IOException {
        //temp/sp.xml
       ObjectifyOperationsIntentionalOldModel obj = new ObjectifyOperationsIntentionalOldModel();
       //Set<ObjectifyXML> s = new HashSet<ObjectifyXML>(); s.add(obj);
       //XMLDOMParser xmlp = new  XMLDOMParser(s); 
       //Set models = xmlp.loadModelFromFile("temp/sp.xml") ;      
       
    }    
        
    
}
