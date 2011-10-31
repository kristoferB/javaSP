package sequenceplanner.IO.XML.IntentionalXML;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sequenceplanner.IO.XML.ObjectifyXML;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionOperator;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.visualization.algorithms.ISupremicaInteractionForVisualization.Type;

/**
 * This objectifier takes operation elements from an xml file from 
 * intentional software DWB, and adds the operations to the old SP model.
 * 
 * TODO: When a new model is created, this class will be divided into one general
 * part for the XML-structure, and one specific for each model.
 * 
 * Currently all operations are added flat whithout hierarcy!
 * 
 * @author kbe
 */
public class ObjectifyOperationsIntentionalOldModel implements ObjectifyXML {

    private static final String elementTag = "operation";
    private static final String rootTag = "operations";
    private static final Class model = Model.class;
    
    private final ConditionData condDataType = new  ConditionData("DWB"); 

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
    public Element addModelToDocument(Object m, Document d) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        OperationData od = new OperationData(e.getAttribute("id"),m.newId());
      
        parseOperationContent(e,od,m); 
        m.createModelOperationNode(od);
       
        return true;
    }

    
    private boolean parseOperationContent(Element e, OperationData od, Model m) {
        if (e==null || od == null) return false;
        
        for (Element child : getChildren(e)){
            if (child.getTagName().equals("precondition")||
                        child.getTagName().equals("postcondition") ||
                        child.getTagName().equals("preaction")||
                        child.getTagName().equals("postaction")){
                    
                    appendCondition(child,od,m);
             }
            // find more content here
        }

        return true;
    }

    private void appendCondition(Element e, OperationData od, Model m) {
        Map<ConditionData, Map<ConditionType, Condition>> conds = od.getConditions();
        if (!conds.containsKey(condDataType)){
            Map<ConditionType,Condition> newCond = new HashMap<ConditionType,Condition>();
            newCond.put(ConditionsFromSopNode.ConditionType.PRE, new Condition());
            newCond.put(ConditionsFromSopNode.ConditionType.POST, new Condition());
            conds.put(condDataType, newCond);
        }
        
        Map<ConditionType,Condition> parserCondition = conds.get(condDataType);
        ConditionExpression expr = null;
        ConditionOperator.Type type = ConditionOperator.Type.AND;
        if (e.getTagName().equals("precondition"))
            expr = parserCondition.get(ConditionsFromSopNode.ConditionType.PRE).getGuard();            
        else if (e.getTagName().equals("postcondition"))
            expr = parserCondition.get(ConditionsFromSopNode.ConditionType.POST).getGuard(); 
        else if (e.getTagName().equals("preaction")){
            expr = parserCondition.get(ConditionsFromSopNode.ConditionType.PRE).getAction();
            type = ConditionOperator.Type.SEMIKOLON;
        }else if (e.getTagName().equals("postaction")){
            expr = parserCondition.get(ConditionsFromSopNode.ConditionType.POST).getAction(); 
            type = ConditionOperator.Type.SEMIKOLON;
        }
        if (expr != null){
            for (Element child : getChildren(e)){
                expr.appendElement(type, getConditionElement(child,m));
            }
        }
  
    }

    private ConditionElement getConditionElement(Element e, Model m) {   
        String variable = "";
        String value = "";
        ConditionStatement.Operator op = ConditionStatement.Operator.Equal;
        
        if (e.getTagName().equals("Ge")){
            op = ConditionStatement.Operator.GreaterEq;
            for (Element child : getChildren(e)){
                if (child.getTagName().equals("variableref")){
                    variable = getVarId(child.getAttribute("id"),m);
                }
                if (child.getTagName().equals("double")){
                    value = child.getAttribute("value");
                }
            }      
        } else if (e.getTagName().equals("Assign")){
            op = ConditionStatement.Operator.Assign;
            for (Element child : getChildren(e)){
                if (child.getTagName().equals("variableref")){
                    variable = getVarId(child.getAttribute("id"),m);
                }                
                if (child.getTagName().equals("Plus")){
                    String plusString = "";
                    for (Element p : getChildren(child)){
                        if (p.getTagName().equals("variableref") && p.hasAttribute("id")){
                            if (!plusString.equals("")) plusString += "+";
                            plusString += getVarId(p.getAttribute("id"),m);
                        }
                    } 
                    value = plusString;
                }
            }      
        }
        
        if (variable.equals("") || value.equals("")) return null;    
        return new ConditionStatement(variable,op, value); 
    }
    
    private List<Element> getChildren(Element e){
        List<Element> set = new LinkedList<Element>();
        if (e==null) return set;
        NodeList list = e.getChildNodes();
        for (int i=0 ; i<list.getLength() ; i++){
            if (list.item(i) instanceof Element)
                set.add((Element) list.item(i));
        }
        
        return set;
    }
    
    private String getVarId(String variableName,Model m){               
        for (TreeNode n : m.getAllVariables()){
            if (n.getNodeData() instanceof ResourceVariableData){
                if (n.getNodeData().getName().equals(variableName)){
                    return Type.OPERATION_VARIABLE_PREFIX.toString() + n.getNodeData().getId();
                }
            }
        }
        return variableName;                                
    }


    





    
}
