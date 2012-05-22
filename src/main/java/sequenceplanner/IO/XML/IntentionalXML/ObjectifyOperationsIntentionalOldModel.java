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

import java.util.List;
import org.w3c.dom.Element;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;

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

    @Override
    protected boolean createElements(Model model, Element e){
        Element element = e.getOwnerDocument().createElement(elementTag);
        e.appendChild(element);
        return addOperationsToElement(element, model);
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
            
            else if (child.getTagName().equals("timecost")){
                int cost;
                try{
                    cost = Integer.parseInt(child.getAttribute("value"));
                } catch (NumberFormatException ex){
                    cost = -1;
                }
                od.timecost = cost;
            }}
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
        if (guard) condition.setGuard(ObjectifyIntentionalExpressions.INSTANCE.createExpression(e,m));
        else if (action) condition.setAction(ObjectifyIntentionalExpressions.INSTANCE.createExpression(e,m));
        
        ConditionData typeName = null;
        if (!e.getAttribute("name").isEmpty()) typeName = new ConditionData(e.getAttribute("name"));
        else typeName = condDataType;
        od.addCondition(typeName, condType, condition);
           
        return true;
    }

    private boolean addOperationsToElement(Element e, Model m) {
        List<TreeNode> ops = m.getAllOperations();
        if (ops.isEmpty()) return false;
        for (TreeNode node : ops){
           if (node != null && (node.getNodeData() instanceof OperationData)){
                Element eOp = e.getOwnerDocument().createElement(objectTag);
                e.appendChild(eOp);
                convertOpToElement((OperationData) node.getNodeData(),eOp);
            } 
        }       
        return true;
    }

    private void convertOpToElement(OperationData data, Element eOp) {
        eOp.setAttribute("name", data.getName());
        
        Element id = eOp.getOwnerDocument().createElement("id");
        eOp.appendChild(id);
        id.setAttribute("idisa", data.guid);
        
        Element schedule = eOp.getOwnerDocument().createElement("schedule");
        eOp.appendChild(schedule);
        
        
            Element starttime = schedule.getOwnerDocument().createElement("starttime");
            schedule.appendChild(starttime);
            starttime.setAttribute("value", data.startTime);
        
            Element stoptime = schedule.getOwnerDocument().createElement("stoptime");
            schedule.appendChild(stoptime);
            stoptime.setAttribute("value", data.stopTime);
        
    }

    
    
    
}
