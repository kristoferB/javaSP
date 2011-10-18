package sequenceplanner.IO.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author kbe
 */
public class AObjectifyDOMXML implements ObjectifyXML {

    @Override
    public String getRootTag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getElementTag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addElementToModel(Element e, Object model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Element addModelToDocument(Object m, Document d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
