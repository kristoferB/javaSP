package sequenceplanner.IO.XML.IntentionalXML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sequenceplanner.IO.XML.ObjectifyXML;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * This objectifier takes variables elements from an xml file from 
 * intentional software DWB, and adds the variables to the old SP model.
 * 
 * Currently all variables are added flat and not in a resource.
 * 
 * @author kbe
 */
public class ObjectifyVariableIntentionalOldModel implements ObjectifyXML {

    private static final String elementTag = "variable";
    private static final String rootTag = "variables";
    private static final Class model = Model.class;
    

    public ObjectifyVariableIntentionalOldModel() {
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
    public Element addModelToDocument(Object m, Document d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addElementToModel(Element e, Object model) {
        if (!(this.model.isInstance(model))) return false;       
        if (!(e.getTagName().equals(elementTag))) return false;
        
        // Add check of XML document structure so it matches expected...
                
        return addVariable(e,(Model) model);
    }
    
    private boolean addVariable(Element e, Model m){
        if (e.getAttribute("id").equals("") || 
            e.getAttribute("init").equals("") ||
            e.getAttribute("min").equals("") ||
            e.getAttribute("max").equals("")
                )
            return false;
        
        int init,min,max = 0;
        try{
            init = Integer.parseInt(e.getAttribute("init"));
            min = Integer.parseInt(e.getAttribute("min"));
            max = Integer.parseInt(e.getAttribute("max"));
            
        } catch(NumberFormatException exept){
            return false;
        }
         
        ResourceVariableData var = new ResourceVariableData(e.getAttribute("id"), m.newId());
        var.setType(ResourceVariableData.INTEGER);
        var.setInitialValue(init);
        var.setMax(max);
        var.setMin(min);
        TreeNode variable = new TreeNode(var);
        m.insertChild(m.getResourceRoot(), variable);
        return true;          
    }
    
    
    



    
}
