package sequenceplanner.IO.XML.IntentionalXML;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class ObjectifyOperationsIntentionalOldModel extends AbstractObjectifyIntentionalOldModel{

    private static final String elementTag = "operations";
    private static final String rootTag = "assembly";
    private static final String objectTag = "operation";
    
    private final ConditionData condDataType = new ConditionData("DWB"); 

    public ObjectifyOperationsIntentionalOldModel() {
        super(rootTag,elementTag);
    }
        
    @Override
    public boolean addModelToElement(Object model, Element e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
        
    @Override
    protected boolean addElement(Element e, Model m){
        if (!e.getTagName().equals(objectTag)) return false;
        if (!e.hasAttribute("id")) return false;
        OperationData od = new OperationData(e.getAttribute("id"),m.newId());
        
        if (e.hasAttribute("seam")){
            od.hasToFinish = true;
            od.seam = e.getAttribute("seam");
        }
        od.resource = e.getAttribute("resource");
      
        parseOperationContent(e,od,m); 
        m.createModelOperationNode(od);
       
        return true;
    }

    
    private boolean parseOperationContent(Element e, OperationData od, Model m) {        
        for (Element child : getChildren(e)){
            if (child.getTagName().equals("precondition"))
                appendCondition(child,od,m,ConditionType.PRE,true,false);
            else if (child.getTagName().equals("postcondition"))
                appendCondition(child,od,m,ConditionType.POST,true,false);
            else if (child.getTagName().equals("preaction"))
                appendCondition(child,od,m,ConditionType.PRE,false,true);
            else if (child.getTagName().equals("postaction"))
                appendCondition(child,od,m,ConditionType.POST,false,true);
            
             }
            // find more content here
        
        return true;
    }
    
    
    
    private boolean appendCondition(Element e,
                                    OperationData od, 
                                    Model m, 
                                    ConditionType condType,
                                    boolean guard,
                                    boolean action)
    {
        if ((guard && action) || (!guard && !action)) return false;
        
        Condition condition = new Condition();   
        if (guard) condition.setGuard(createExpression(e,m));
        else if (action) condition.setAction(createExpression(e,m));
        
        ConditionData typeName = null;
        if (!e.getAttribute("name").isEmpty()) typeName = new ConditionData(e.getAttribute("name"));
        else typeName = condDataType;
        od.addCondition(typeName, condType, condition);
           
        return true;
    }



    
    private String getVarId(String variableName,Model m){               
        for (TreeNode n : m.getAllVariables()){
            if (n.getNodeData() instanceof ResourceVariableData){
                if (n.getNodeData().getName().equals(variableName)){
                    return Type.OPERATION_VARIABLE_PREFIX.toString() + n.getNodeData().getId();
                }
            }
        }
        for (TreeNode n : m.getAllOperations()){
            if (n.getNodeData() instanceof OperationData){
                if (n.getNodeData().getName().equals(variableName)){
                    return Type.OPERATION_VARIABLE_PREFIX.toString() + n.getNodeData().getId();
                }
            }
        }
        return variableName;                                
    }

    private ConditionExpression createExpression(Element e, Model m) {
        ConditionExpression expr = new ConditionExpression();

        ConditionOperator.Type operator = ConditionOperator.Type.AND;
        if (e.getTagName().equals("OR")) operator = ConditionOperator.Type.OR;
        
        for (Element child : getChildren(e)){
            if (child.getTagName().equals("AND") || child.getTagName().equals("OR")){
                expr.appendElement(operator, createExpression(child,m));
            } else{
                ConditionStatement.Operator op = ConditionStatement.Operator.Equal;
                if (child.getTagName().equals("Le")) op = ConditionStatement.Operator.LessEq;
                else if (child.getTagName().equals("Lt")) op = ConditionStatement.Operator.Less;
                else if (child.getTagName().equals("Ge")) op = ConditionStatement.Operator.GreaterEq;
                else if (child.getTagName().equals("Gt")) op = ConditionStatement.Operator.Greater;    
                else if (child.getTagName().equals("Ne")) op = ConditionStatement.Operator.NotEqual;
                else if (child.getTagName().equals("Assign")){
                    op = ConditionStatement.Operator.Assign;
                    operator = ConditionOperator.Type.SEMIKOLON;
                }
                
                String variable = "";
                String value = "";
                for (Element node : getChildren(child)){
                    if (variable.isEmpty()){
                        variable = getStatment(node,m);
                    } else if (value.isEmpty()){
                        value = getStatment(node,m);
                    }
                }
                if (!variable.equals("") && !value.equals("")){
                    expr.appendElement(operator, new ConditionStatement(variable,op, value));                     
                } 
            }
        }
        return expr;
    }
    
    private String getStatment(Element e, Model m){
        if (e.getTagName().equals("variableref")){
            return getVarId(e.getAttribute("id"),m);
        }
        if (e.getTagName().equals("double")){
            return e.getAttribute("value");
        }
        if (e.getTagName().equals("Plus")){
            String plusString = "";
            for (Element p : getChildren(e)){
                if (!plusString.equals("")) plusString += "+";
                plusString += getStatment(p,m);
            }           
            return plusString;
        }
        if (e.getTagName().equals("Minus")){
            String minusString = "";
            for (Element p : getChildren(e)){
                if (!minusString.equals("")) minusString += "-";
                minusString += getStatment(p,m);
            }           
            return minusString;
        }
        
        return "";
    }


    
    
    
    

    private void appendCondition(Element e, OperationData od, Model m) {
        Map<ConditionData, Map<ConditionType, Condition>> conds = od.getConditions();
        if (!conds.containsKey(condDataType)){
            Map<ConditionType,Condition> newCond = new HashMap<ConditionType,Condition>();
            newCond.put(ConditionType.PRE, new Condition());
            newCond.put(ConditionType.POST, new Condition());
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
        
        // Remove empty
        Condition pre = parserCondition.get(ConditionType.PRE);
        Condition post = parserCondition.get(ConditionType.POST);       
        if (pre!=null && !pre.hasGuard() && !pre.hasAction())  parserCondition.remove(ConditionType.PRE);
        if (post!=null && !post.hasGuard() && !post.hasAction())  parserCondition.remove(ConditionType.POST);
    }

    


    private ConditionElement getConditionElement(Element e, Model m) {   
        String variable = "";
        String value = "";
        ConditionStatement.Operator op = ConditionStatement.Operator.Equal;
        
        if (e.getTagName().equals("Ge")){
            op = ConditionStatement.Operator.LessEq;
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
    



    
}
