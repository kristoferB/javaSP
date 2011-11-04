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
    public boolean addModelToElement(Object model, Element e){
        Element element = e.getOwnerDocument().createElement(elementTag);
        e.appendChild(element);
        
        return false; //addSOPstoElement((Model) model, eRoot);
    }

    
    @Override
    protected boolean addElement(Element e, Model m){
        if (!e.getTagName().equals(objectTag)) return false;
        ISopNode sop = createSOP(e, m);
        System.out.println(sop.toString());
        return saveSOPToModel(sop,m);
        
    }
    

    private ISopNode createSOP(Element e, Model m) {       
        ISopNode root = new SopNode();
        for (Element child : getChildren(e)){
            ISopNode n = resolveSopNode(child, m);
            if (n != null) root.addNodeToSequenceSet(n);
        }
        
        return root;
        
    }

    private ISopNode resolveSopNode(Element e, Model m) {
        ISopNode node = null;
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
            ISopNode n = resolveSopNode(child, m);
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
    
    private ISopNode createSequenceNode(Element e, Model m) {
        ISopNode first = null;
        ISopNode last = null;
        for (Element child : getChildren(e)){
            ISopNode n = resolveSopNode(child, m);
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
    
    private boolean saveSOPToModel(ISopNode sop, Model m) {
        
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
        
        
//        SopNodeToolboxSetOfOperations toolbox = new SopNodeToolboxSetOfOperations();
//        
//        int id = m.newId();
//        ViewData vd = new ViewData("Intentional "+id,id);
//        OperationView ov = new OperationView(m, vd);
//        m.getViewRoot().insert(new TreeNode(vd));
//        
//        toolbox.drawNode(sop, ov.getGraph());
//        return true;
        
        
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private boolean addSOPstoElement(Model m, Element eRoot) {
        TreeNode viewRoot = m.getViewRoot();
        for (int i=0 ; i< viewRoot.getChildCount();i++){
            TreeNode n = viewRoot.getChildAt(i);
            if (n.getNodeData() instanceof ViewData){
                ViewData data = (ViewData) n.getNodeData();
                ISopNode sop = data.mSopNodeForGraphPlus.getRootSopNode(false);
                if (sop != null){
                    Element eSop = eRoot.getOwnerDocument().createElement(elementTag);
                    eRoot.appendChild(eSop);
                    convertSopToElement(sop,eSop);
                }
            }
        }        
        return true;
    }

    private void convertSopToElement(ISopNode sop, Element e) {
        if (sop == null || e == null) return;
        Element newElement = null;
        if (sop instanceof SopNode){
            newElement = e;
        }else if (sop instanceof SopNodeOperation){
            String opName = null;
            if (sop.getOperation() != null){
                opName = sop.getOperation().getName();
            } else opName = "noName";
            newElement = e.getOwnerDocument().createElement("opref");
            newElement.setAttribute("id", opName);           
            e.appendChild(newElement);
        } else {
            newElement = e.getOwnerDocument().createElement(sop.typeToString());
            e.appendChild(newElement);
        }
        
        for (ISopNode n : sop.getFirstNodesInSequencesAsSet()){
            convertSopToElement(n,newElement);
        }
        
    }

    
}
