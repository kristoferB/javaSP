package sequenceplanner.IO.XML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
    
    // Map<rootTags, Map<elementTag, set<ObjectifyXML>>>
    private Map<String,Map<String,Set<ObjectifyXML>>> tagMap; 
    // Map<Model class name, model instance>
    // Object should change to a model interface!
    private Map<String,Object> modelTypes; 
    
   
    public XMLDOMParser(Set<ObjectifyXML> objectifiers, Set<Object> models) {  
        tagMap = new HashMap<String,Map<String,Set<ObjectifyXML>>>();
        modelTypes = new  HashMap<String,Object>();
        
        if (models != null){
            for (Object model : models)
                modelTypes.put(model.getClass().getName(),model);        
        }
        
        
        for (ObjectifyXML o : objectifiers){                
            Map<String,Set<ObjectifyXML>> rT = tagMap.get(o.getRootTag());
            if (rT == null){
                rT = new HashMap<String,Set<ObjectifyXML>>();
                tagMap.put(o.getRootTag(), rT);
            }                       
            
            Set<ObjectifyXML> sO = rT.get(o.getElementTag());
            if (sO == null){
                sO = new HashSet<ObjectifyXML>();
                rT.put(o.getElementTag(), sO);
            }
            sO.add(o);                   
                                  
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
    
    public Set<Object> loadModelFromFile(String path){
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
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));  
    }
    
 
    private Set<Object> populateModels(Document d){ 
        Element root = d.getDocumentElement();
        
        Deque<Element> stack = new ArrayDeque();
        for (Element e : getChildren(root)){
            stack.push(e);
        }
        
        while (!stack.isEmpty()){
            Element e = stack.pop();
            if (!addElementToModels(root,e))
                for (Element c : getChildren(e)) stack.push(c);
        }
                   
        if (modelTypes.values() != null){
            return new HashSet<Object>(modelTypes.values());
        }
        return new HashSet<Object>(); 
    }
    
    private boolean addElementToModels(Element root, Element e){
        if (root == null | e == null) return false;
        
        Map<String,Set<ObjectifyXML>> rT = tagMap.get(root.getTagName());
        if (rT == null) return false;         
            
        Set<ObjectifyXML> sO = rT.get(e.getTagName().toLowerCase());
        if (sO == null) return false;
        
        for (ObjectifyXML o : sO){
            if (o.validateRootTag(root))
                o.addElementToModel(e, modelTypes.get(o.getModelClass().getName()));
        }
        return true;
    }
    
    
    private List<Element> getChildren(Node e){
        List<Element> children = new LinkedList<Element>();
        if (e == null) return children;
        NodeList list = e.getChildNodes();
        for (int i=list.getLength()-1 ; i>=0 ; i--){
            if (list.item(i) instanceof Element)
                children.add((Element) list.item(i));
        }
        
        return children;
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
