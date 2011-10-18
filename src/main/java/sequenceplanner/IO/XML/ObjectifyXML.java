package sequenceplanner.IO.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface to support different elementtypes and models when parsing
 * an XML - file.  
 * 
 * 
 * @author kbe
 */
public interface ObjectifyXML {
    
    public String getRootTag();
    
    public String getElementTag();
    
    public boolean addElementToModel(Element e, Object model);
    
    public Element addModelToDocument(Object m, Document d);
    
    
}
