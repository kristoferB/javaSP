package sequenceplanner.IO.XML.IntentionalXML;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sequenceplanner.IO.XML.ObjectifyXML;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * This objectifier takes resource elements from an xml file from 
 * intentional software DWB, and adds the variables (not as resource) to the old SP model.
 * 
 * Currently all variables are added flat and not in a resource.
 * 
 * @author kbe
 */
public class ObjectifyResourceIntentionalOldModel extends AbstractObjectifyIntentionalOldModel {

    private static final String elementTag = "resources";
    private static final String rootTag = "assembly";
    private static final String objectTag = "resource";
    

    public ObjectifyResourceIntentionalOldModel() {
        super(rootTag,elementTag);
    }
        
    @Override
    public boolean addModelToElement(Object model, Element e){
        throw new UnsupportedOperationException("Not supported yet.");
    }
   
    
    @Override
    protected boolean addElement(Element e, Model m) {
        if (!e.getTagName().equals(objectTag)) return false;
        if (!e.hasAttribute("id")) return false;
        ResourceVariableData var = new ResourceVariableData(e.getAttribute("id"), m.newId());
        var.setType(ResourceVariableData.BINARY);
        var.setInitialValue(0);
        var.setMax(1);
        var.setMin(0);
        TreeNode variable = new TreeNode(var);
        m.insertChild(m.getResourceRoot(), variable);
        return true; 
    }
    
}
