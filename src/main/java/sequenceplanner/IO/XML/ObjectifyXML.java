package sequenceplanner.IO.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface to support different elementtypes and models when parsing
 * an XML - file.  
 *  // Object should change to a model interface!
 * 
 * @author kbe
 */
public interface ObjectifyXML {
    
    public String getRootTag();
    
    public String getElementTag();
    
    public Class getModelClass(); 
    
    
    /**
     * This metod should parse the element and its content and add to model
     * should return false if not possible to add element,
     * @param e An element (could be null)
     * @param model the model (could be null)
     * @return True if success, false otherwise.
     */
    public boolean addElementToModel(Element e, Object model);
    
    public Element addModelToDocument(Object m, Document d); 
    
    
}
