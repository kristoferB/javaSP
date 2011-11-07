package sequenceplanner.IO.XML;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author kbe
 */
public class XMLDOMSaver {
    private String rootElement;
    private Set<ObjectifyXML> objectifiers;
    private Map<String,Object> modelTypes; // Object should change to a model interface!

    public XMLDOMSaver(Set<ObjectifyXML> objectifiers, Set<Object> models) {
        this.objectifiers = objectifiers;
        modelTypes = new  HashMap<String,Object>();
        
        for (Object model : models){
            modelTypes.put(model.getClass().getName(),model);
        }
        
        for (ObjectifyXML o : objectifiers){
            if (rootElement == null){
                rootElement = o.getRootTag();
            } else {
                if (!rootElement.equals(o.getRootTag())){
                    // mayeb throw exception
                    System.out.println("Can only save one root tah at the time");
                    this.objectifiers.remove(o);
                }
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
    
    public boolean saveXMLFile(String fileToSave){        
        try {
            // Fix uri and name later. Should be input to the method.
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation().createDocument(null, null, null);
        
            if (d==null) return false;
            populateDocument(d);

            Source source = new DOMSource(d);
            File file = new File(fileToSave);
            if (!file.exists()) file.createNewFile();
            Result result = new StreamResult(file);

            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.METHOD, "xml");
            xformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            xformer.transform(source, result);
            
        } catch (IOException ex) {
            Logger.getLogger(XMLDOMSaver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XMLDOMSaver.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (TransformerException ex) {
            Logger.getLogger(XMLDOMSaver.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLDOMSaver.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }

    private void populateDocument(Document d) {
        Element e = d.createElement(this.rootElement);
        d.appendChild(e);
        for (ObjectifyXML o : this.objectifiers){
            o.addModelToElement(this.modelTypes.get(o.getModelClass().getName()), e);
        }
        
    }
    
    
    
    
}
