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
import sequenceplanner.model.SOP.algorithms.ISopNodeToolbox;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;


/**
 * This objectifier takes variables elements from an xml file from 
 * intentional software DWB, and adds the variables to the old SP model.
 * 
 * Currently all variables are added flat and not in a resource.
 * 
 * @author kbe
 */
public class ObjectifySOPIntentionalOldModel implements ObjectifyXML {

    private static final String elementTag = "spec";
    private static final String rootTag = "specs";
    private static final Class model = Model.class;
    

    public ObjectifySOPIntentionalOldModel() {
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
        ISopNode sop = createSOP(e, (Model) model);
        //System.out.println(sop);
        return saveSOPToModel(sop, (Model) model);
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
            Map<ConditionType, Condition> cond = map.get(node.getNodeData());
            if (cond != null){
                ((OperationData) node.getNodeData()).getConditions().put(new ConditionData("IDW_SOP"), cond);
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
    
    
    private List<Element> getChildren(Element e){
        List<Element> children = new LinkedList<Element>();
        if (e == null) return children;
        NodeList list = e.getChildNodes();
        for (int i=0 ; i<list.getLength() ; i++){
            if (list.item(i) instanceof Element)
                children.add((Element) list.item(i));
        }
        
        return children;
    }



    



    
}
