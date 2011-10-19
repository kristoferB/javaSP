package sequenceplanner.IO.XML;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * XMLParser to load an XML-file as a document. ObjectifXML classes are added
 * in constructor that translate the element into a data model.
 *
 * 
 * @author kbe
 */
public class XMLDOMParser {

    Map<String,Set<ObjectifyXML>> elementTypes;
    Map<String,Object> modelTypes; // Object should change to a model interface!
    
   
    public XMLDOMParser(Set<ObjectifyXML> objectifiers) {        
        elementTypes = new HashMap<String,Set<ObjectifyXML>>();
        modelTypes = new  HashMap<String,Object>();
        for (ObjectifyXML o : objectifiers){
            if (elementTypes.containsKey(o.getElementTag())){
                if (elementTypes.get(o.getElementTag()) != null){
                    elementTypes.get(o.getElementTag()).add(o);
                }
            } else {
                Set<ObjectifyXML> so = new HashSet<ObjectifyXML>(); so.add(o);
                elementTypes.put(o.getElementTag(), so);
            }
            
            if (!modelTypes.containsKey(o.getModelClass().getName())){                
                try {
                    modelTypes.put(o.getModelClass().getName(), o.getModelClass().newInstance());
                } catch (InstantiationException ex) {
                    Logger.getLogger(XMLDOMParser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(XMLDOMParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public Set loadModelFromFile(String path){
        Document d;
        try{
            d = parse(path);
        } catch(Exception ex){
            System.out.println(ex.toString());
            return null;
        }       
        return populateModels(d);
    }
    
    
    private Document parse(String path) throws SAXException, IOException, ParserConfigurationException {        
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("temp/sp.xml"));  
    }
    
 
    private Set populateModels(Document d){        
        for (String tag : this.elementTypes.keySet()){
            NodeList nl = d.getElementsByTagName(tag);
            for (ObjectifyXML o : elementTypes.get(tag)){
                Object model = modelTypes.get(o.getModelClass().getName());
                if (model != null){
                    for (int i=0 ; i<nl.getLength();i++){
                        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE){
                            o.addElementToModel((Element) nl.item(i), model);
                        }
                    }
                }
            }
        }
        if (modelTypes.values() != null){
            return new HashSet(modelTypes.values());
        }
        return new HashSet(); 
    }
   

    
/*    Old code, to be removed but saved here if to be used somewhere else...
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
*/    
    
    
}
