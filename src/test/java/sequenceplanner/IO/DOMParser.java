/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.IO;

import java.io.File;
import java.io.IOException;
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
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document doc = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new File("temp/sp.xml"));

        } catch (Exception ex) {
            Logger.getLogger(DOMParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (doc!=null){
            visit(doc,0);
        }
        

    
    
    }
    
        public void visit(Node node, int level)
	{
		NodeList nl = node.getChildNodes();
		
		for(int i=0, cnt=nl.getLength(); i<cnt; i++)
		{
                    if (nl.item(i)== null) break;
                    System.out.println("level:" + level);
                    NamedNodeMap nm = nl.item(i).getAttributes();
                    if (nm != null){
                        for (int j = 0; j > nm.getLength();j++){
                            System.out.println(nm.item(j).get);
                        }
                    }
                    
                        System.out.println(nl.item(i).getLocalName());
                        System.out.println(nl.item(i).getNodeName());
                        System.out.println(nl.item(i).getNodeValue());
			
			visit(nl.item(i), level+1);
		}
	}
    
}
