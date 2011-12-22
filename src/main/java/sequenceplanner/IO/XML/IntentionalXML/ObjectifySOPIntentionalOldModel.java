package sequenceplanner.IO.XML.IntentionalXML;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sequenceplanner.IO.XML.ObjectifyXML;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionOperator;
import sequenceplanner.datamodel.condition.ConditionOperator.Type;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.datamodel.product.Seam;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.*;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;


/**
 * This objectifier takes variables elements from an xml file from 
 * intentional software DWB, and adds the variables to the old SP model.
 * 
 * Currently all variables are added flat and not in a resource.
 * 
 * @author kbe
 */
public class ObjectifySOPIntentionalOldModel extends AbstractObjectifyIntentionalOldModel {

    private static final String elementTag = "specs";
    private static final String rootTag = "assembly";
    private static final String objectTag = "spec";

    public ObjectifySOPIntentionalOldModel() {
        super(rootTag,elementTag);
    }
        
    
    @Override
    protected boolean createElements(Model model, Element e){
        Element element = e.getOwnerDocument().createElement(elementTag);
        e.appendChild(element);
        return addSOPstoElement(element, model);
    }

    
    @Override
    protected boolean addElement(Element e, Model m){
        if (!e.getTagName().equals(objectTag)) return false;
        SopNode sop = createSOP(e, m);
        //System.out.println(sop.toString());
        return saveSOPToModel(sop,m);
        
    }
    

    private SopNode createSOP(Element e, Model m) {       
        SopNode root = new SopNodeEmpty();
        for (Element child : getChildren(e)){
            SopNode n = resolveSopNode(child, m);
            if (n != null) root.addNodeToSequenceSet(n);
        }
        
        return root;
        
    }

    private SopNode resolveSopNode(Element e, Model m) {
        SopNode node = null;
        if (e.getTagName().equals("sequence")){
            return createSequenceNode(e, m);
        } else if (e.getTagName().equals("parallel")){
            node = new SopNodeParallel();
        } else if (e.getTagName().equals("alternative")){
            node = new SopNodeAlternative();
        } else if (e.getTagName().equals("arbitrary")){
            node = new SopNodeArbitrary();
        } else if (e.getTagName().equals("opref")){
            OperationData data = getOperationData(e.getAttribute("id"),m);
            if (data != null) 
                node = new SopNodeOperation(data);
        }
        
        for (Element child : getChildren(e)){
            SopNode n = resolveSopNode(child, m);
            if (n != null) node.addNodeToSequenceSet(n);
        }
        
        return node;
    }
    
    private OperationData getOperationData(String name,Model m){
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                if (n.getNodeData().getName().equals(name))
                    return (OperationData) n.getNodeData();
            }
        }
        return null;
    }
    
    private SopNode createSequenceNode(Element e, Model m) {
        SopNode first = null;
        SopNode last = null;
        for (Element child : getChildren(e)){
            SopNode n = resolveSopNode(child, m);
            if (n != null){
                if (first == null){
                    first = n; last = n;
                } else {
                    last.setSuccessorNode(n);
                    last = n;
                }                
            }
        }
        return first;
    }
    
    private boolean saveSOPToModel(SopNode sop, Model m) {
        m.sops.add(sop);
        
        ConditionsFromSopNode cfsn = new ConditionsFromSopNode(sop);
        Map<OperationData, Map<ConditionType, Condition>> map = cfsn.getmOperationConditionMap();
        for (TreeNode node : m.getAllOperations()){
            Map<ConditionType, Condition> cond = map.get((OperationData)node.getNodeData());
            if (cond != null){
                ((OperationData) node.getNodeData()).addCondition(
                        new ConditionData("IDW_SOP"), 
                        ConditionType.PRE,
                        cond.get(ConditionType.PRE));
                ((OperationData) node.getNodeData()).addCondition(
                        new ConditionData("IDW_SOP"), 
                        ConditionType.POST,
                        cond.get(ConditionType.POST));
                }
        }
        
        
        return true;
        
       
    }
    
    private boolean addSOPstoElement(Element eRoot, Model m) {
        if (m.sops.isEmpty()) return false;
        for (SopNode sop : m.sops){
           if (sop != null){
                Element eSop = eRoot.getOwnerDocument().createElement(objectTag);
                eRoot.appendChild(eSop);
                convertSopToElement(sop,eSop);
            } 
        }       
        return true;
    }

    private void convertSopToElement(SopNode sop, Element e) {
        if (sop == null || e == null) return;
        Element insert = e;
        Element newElement = null;
        
        if (sop.getSuccessorNode() != null && !e.getTagName().equals("sequence")){
            newElement = e.getOwnerDocument().createElement("sequence");  
            e.appendChild(newElement);
            insert = newElement;           
        }
        
        if (sop instanceof SopNodeEmpty){
            newElement = insert;
        } else if (sop instanceof SopNodeOperation){
            String opName = null;
            if (sop.getOperation() != null){
                opName = sop.getOperation().getName();
            } else opName = "noName";
            newElement = insert.getOwnerDocument().createElement("opref");
            newElement.setAttribute("id", opName);           
            insert.appendChild(newElement);
        } else if (sop instanceof SopNodeAlternative){
            newElement = insert.getOwnerDocument().createElement("alternative");
            insert.appendChild(newElement);
        } else if (sop instanceof SopNodeParallel){
            newElement = insert.getOwnerDocument().createElement("parallel");
            insert.appendChild(newElement);
        } else if (sop instanceof SopNodeArbitrary){
            newElement = insert.getOwnerDocument().createElement("arbitrary");
            insert.appendChild(newElement);
        }                
        
        if (!sop.sequenceSetIsEmpty() && sop.getFirstNodesInSequencesAsSet().size() > 1){
            Element parra = newElement.getOwnerDocument().createElement("sequence");  
            newElement.appendChild(parra);
            newElement = parra;  
        }
        
        for (SopNode n : sop.getFirstNodesInSequencesAsSet()){
            convertSopToElement(n,newElement);
        }

        if (sop.getSuccessorNode() != null){
            convertSopToElement(sop.getSuccessorNode(),insert);
        }
        
    }



    
}
