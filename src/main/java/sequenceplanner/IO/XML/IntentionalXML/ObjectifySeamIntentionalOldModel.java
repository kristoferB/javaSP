package sequenceplanner.IO.XML.IntentionalXML;

import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Element;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.product.Seam;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * This objectifier takes seam elements from an xml file from 
 * intentional software DWB, and adds them to the old SP model.

 * 
 * @author kbe
 */
public class ObjectifySeamIntentionalOldModel extends AbstractObjectifyIntentionalOldModel {

    private static final String elementTag = "seams";
    private static final String rootTag = "assembly";
    private static final String objectTag = "seam";
    

    public ObjectifySeamIntentionalOldModel() {
        super(rootTag,elementTag);
    }
        
    @Override
    protected boolean createElements(Model model, Element e){
        throw new UnsupportedOperationException("Not supported yet.");
    }
   
    
    @Override
    protected boolean addElement(Element e, Model m) {
        if (!e.getTagName().equals(objectTag)) return false;
        if (!e.hasAttribute("id")) return false;
        
        Set<String> blocks = getBlocks(e);
        addBlockVariable(blocks,m);
        m.seams.add(new Seam(e.getAttribute("id"),blocks, this.createCompleteCondition(e, m)));
        return true; 
    }
    
    private Set<String> getBlocks(Element e){
        Set<String> blocks = new HashSet<String>();
        for (Element child : getChildren(e)){
            if (child.getTagName().equals("blockref")){
                if (!child.getAttribute("block").isEmpty())
                    blocks.add(child.getAttribute("block"));
            }
        }
        return blocks;
    }

    private void addBlockVariable(Set<String> blocks, Model m) {
        for (String block : blocks){
            if (!blockExists(block,m)){
                ResourceVariableData var = new ResourceVariableData(block, m.newId());
                var.setType(ResourceVariableData.BINARY);
                var.setInitialValue(0);
                var.setMax(1);
                var.setMin(0);
                TreeNode variable = new TreeNode(var);
                m.insertChild(m.getResourceRoot(), variable);
            }
        }
    }

    private boolean blockExists(String blockName, Model m) {
        for (TreeNode n : m.getAllVariables()){            
                if (n.getNodeData().getName().equals(blockName))
                    return true;            
        }
        return false;
    }
    
    private ConditionExpression createCompleteCondition(Element e, Model m){
        for (Element child : getChildren(e)){
            if (child.getTagName().equals("completecondition")){
                return ObjectifyIntentionalExpressions.INSTANCE.createExpression(child, m);
            }
        }
        return new ConditionExpression();
    }
    
}
