package sequenceplanner.IO.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.OperationData;

/**
 * This objectifier takes operation elements from an xml file from 
 * intentional software DWB, and adds the operations to the old SP model.
 * 
 * Currently all operations are added flat whithout hierarcy!
 * 
 * @author kbe
 */
public class ObjectifyOperationsIntentionalOldModel implements ObjectifyXML {

    private static final String elementTag = "operation";
    private static final String rootTag = "operations";
    private static final Class model = Model.class;

    public ObjectifyOperationsIntentionalOldModel() {
    }
        
    
    @Override
    public String getRootTag() {
        return rootTag;
    }

    @Override
    public String getElementTag() {
        return elementTag;
    }

    @Override
    public Class getModelClass() {
        return model;
    }

    @Override
    public boolean addElementToModel(Element e, Object model) {
        if (!(this.model.isInstance(model))) return false;       
        if (!(e.getTagName().equals(elementTag))) return false;
        
        // Add check of XML document structure so it matches expected...
                
        return addOperation(e,(Model) model);
    }
    
    // Currently only adds all operation flat, i.e no hierarchy
    private boolean addOperation(Element e, Model m){
        
        if (!e.hasAttribute("id")) return false;
        
        //System.out.println(e.getAttribute("id"));
        OperationData od = new OperationData(e.getAttribute("id"),m.newId());
        m.createModelOperationNode(od);

        // add otherthings here...

        
        return true;
    }
    

    @Override
    public Element addModelToDocument(Object m, Document d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
