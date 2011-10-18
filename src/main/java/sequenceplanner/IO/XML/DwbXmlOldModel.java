package sequenceplanner.IO.XML;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sequenceplanner.model.Model;

/**
 *
 * XMLParser to load an Intentional workbench XML-file
 * and to file the old Model
 * 
 * @author kbe
 */
public class DwbXmlOldModel {

    Map<String,Set<ObjectifyXML>> elementTypes;
    
   
    public DwbXmlOldModel(Set<ObjectifyXML> objectifiers) {        
        elementTypes = new HashMap<String,Set<ObjectifyXML>>();
        for (ObjectifyXML o : objectifiers){
            if (elementTypes.containsKey(o.getElementTag())){
                
            } else {
                
            }
        }
    }
    
    public Model loadModelFromFile(String path){
        Document d;
        try{
            d = parse(path);
        } catch(Exception ex){
            System.out.println(ex.toString());
            return null;
        }       
        return populateModel(d);
    }
    
    
    private Document parse(String path) throws SAXException, IOException, ParserConfigurationException {        
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("temp/sp.xml"));  
    }
    
    
    
    private Model populateModel(Document d){
        m = new Model(); // global variabel
        addNodesToModel("operations", "operation", d);
        addNodesToModel("resources", "resource", d);
        addNodesToModel("seams", "seams", d);
        addNodesToModel("views", "view", d);       
        return m;
    }
   
    private void addElement(Element e){
        if (e.getTagName().equals("operation")){
            addOperationToModel(e);
        } else if (e.getTagName().equals("resource")){
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
   
    
    
    private void addNodesToModel(String rootTag, String nodeTag, Document d) {
        Node opRoot = getRootElement(d, rootTag);
        if (opRoot != null)
            reqAdd(nodeTag, opRoot.getChildNodes());       
    }
    
    private boolean reqAdd(String nodeType, NodeList nodes){
        if (nodes == null || nodes.getLength() == 0) return false;
        
        for (int i = 0 ; i<nodes.getLength();i++){
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element e = (Element) nodes.item(i);
                if (e.getTagName().equals(nodeType)){
                    addElement(e);
                    reqAdd(nodeType, e.getChildNodes());
                }
            }
        }       
        return true;
    }    
    
    // Will only return the first root if more are availible!!
    private Node getRootElement(Document d, String tagName){
        NodeList rootList = d.getElementsByTagName(tagName);
        if (rootList.getLength() == 0) 
            return null;
        
        return rootList.item(0);
    }

    
    
    
    
    private void addOperationToModel(Element e) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    
    
}
