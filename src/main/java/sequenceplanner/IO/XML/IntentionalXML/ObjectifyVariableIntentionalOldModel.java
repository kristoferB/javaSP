package sequenceplanner.IO.XML.IntentionalXML;

import java.util.Set;
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
public class ObjectifyVariableIntentionalOldModel extends AbstractObjectifyIntentionalOldModel {

    private static final String elementTag = "variables";
    private static final String rootTag = "ProcessPlanForExport";
    private static final String objectTag = "variable";
    private static final int maxForInt = 1000000;
    

    public ObjectifyVariableIntentionalOldModel() {
        super(rootTag,elementTag);
    }
        
    
    @Override
    protected boolean createElements(Model model, Element e){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean addElement(Element e, Model m){
        if (!e.getTagName().toLowerCase().equals(objectTag.toLowerCase())) return false;
        String id = getName(e); if (id.isEmpty()) return false;
    
        String value = e.getAttribute("value");
        int init = 0;
        int min  = 0;
        int max = 1;
        Integer Type = ResourceVariableData.BINARY;
       
        if (!value.equals("")){
            try{
                float f = Float.parseFloat(value);
                init = Math.round(f);
                max = maxForInt;   
                Type = ResourceVariableData.INTEGER;
            } catch(NumberFormatException exept){
                return false;
            }
        }
                  
        ResourceVariableData var = new ResourceVariableData(id, m.newId());
        var.setType(Type);
        var.setInitialValue(init);
        var.setMax(max);
        var.setMin(min);
        TreeNode variable = new TreeNode(var);
        m.insertChild(m.getResourceRoot(), variable);
        
        TagNameMapper.INSTANCE.addTageNameType(id, objectTag.toLowerCase());
        return true;          
    }
    
}
