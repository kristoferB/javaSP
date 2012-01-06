package sequenceplanner.IO.XML.IntentionalXML;

import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionOperator;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;
import sequenceplanner.visualization.algorithms.ISupremicaInteractionForVisualization.Type;

/**
 *
 * @author kbe
 */
public enum ObjectifyIntentionalExpressions {
    INSTANCE;
    
    public ConditionExpression createExpression(Element e, Model m) {
        ConditionExpression expr = new ConditionExpression();

        ConditionOperator.Type operator = ConditionOperator.Type.AND;
        if (e.getTagName().equals("OR") || e.getTagName().equals("Or")) operator = ConditionOperator.Type.OR;
        
        for (Element child : getChildren(e)){
            if (child.getTagName().equals("AND") || child.getTagName().equals("OR") ||
                child.getTagName().equals("And") || child.getTagName().equals("Or")    ){
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
        if (e.getTagName().equals("int")){
            return e.getAttribute("value");
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
    
}
