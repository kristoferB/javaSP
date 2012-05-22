/* 
   Copyright (c) 2012, Kristofer Bengtsson, Sekvensa AB, Chalmers University of Technology
   Developed with the sponsorship of the Defense Advanced Research Projects Agency (DARPA).
   Permission is hereby granted, free of charge, to any person obtaining a copy of this data, including any
   software or models in source or binary form, specifications, algorithms, and documentation (collectively
   "the Data"), to deal in the Data without restriction, including without limitation the rights to use, copy,
   modify, merge, publish, distribute, sublicense, and/or sell copies of the Data, and to permit persons to
   whom the Data is furnished to do so, subject to the following conditions:
   The above copyright notice and this permission notice shall be included in all copies or substantial
   portions of the Data.
   THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
   INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
   PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS,
   SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
   OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR
   OTHER DEALINGS IN THE DATA.
*/

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
        if (e.getTagName().toLowerCase().equals("or")) operator = ConditionOperator.Type.OR;
        
        for (Element child : getChildren(e)){
            if (child.getTagName().toLowerCase().equals("and") || child.getTagName().toLowerCase().equals("or")){
                expr.appendElement(operator, createExpression(child,m));
            } else{
                ConditionStatement.Operator op = ConditionStatement.Operator.Equal;
                if (child.getTagName().toLowerCase().equals("le")) op = ConditionStatement.Operator.LessEq;
                else if (child.getTagName().toLowerCase().equals("lt")) op = ConditionStatement.Operator.Less;
                else if (child.getTagName().toLowerCase().equals("ge")) op = ConditionStatement.Operator.GreaterEq;
                else if (child.getTagName().toLowerCase().equals("gt")) op = ConditionStatement.Operator.Greater;    
                else if (child.getTagName().toLowerCase().equals("ne")) op = ConditionStatement.Operator.NotEqual;
                else if (child.getTagName().toLowerCase().equals("assign")){
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
        if (e.getTagName().toLowerCase().equals("variableref")){
            return getVarId(getTarget(e),m);
        }
        if (e.getTagName().toLowerCase().equals("intlit")){ 
            if (e.getTextContent().isEmpty()) return "1";
            else return e.getTextContent();
        }
        if (e.getTagName().toLowerCase().equals("true")){ // hack
            return "1";
        }
        if (e.getTagName().toLowerCase().equals("int")){
            return e.getAttribute("value");
        }
        if (e.getTagName().toLowerCase().equals("double")){
            return e.getAttribute("value");
        }
        if (e.getTagName().toLowerCase().equals("Plus")){
            String plusString = "";
            for (Element p : getChildren(e)){
                if (!plusString.equals("")) plusString += "+";
                plusString += getStatment(p,m);
            }           
            return plusString;
        }
        if (e.getTagName().toLowerCase().equals("Minus")){
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
    
        private String getTarget(Element e){
        for (Element child : getChildren(e)){
            if (child.getTagName().toLowerCase().equals("target")){
                for (Element name : getChildren(child)){
                    return name.getTagName();                 
                }
            }
        }
        return "";
    }
    
}
