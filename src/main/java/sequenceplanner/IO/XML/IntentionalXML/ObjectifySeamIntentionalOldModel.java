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
    private static final String rootTag = "ProcessPlanForExport";
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
        if (!e.getTagName().toLowerCase().equals(objectTag.toLowerCase())) return false; 
        String id = getName(e); if (id.isEmpty()) return false;
        
        Set<String> blocks = getBlocks(e);
        addBlockVariable(blocks,m);
        m.seams.add(new Seam(getName(e),blocks, this.createCompleteCondition(e, m)));
        return true; 
    }
    
    private Set<String> getBlocks(Element e){
        Set<String> blocks = new HashSet<String>();
        for (Element child : getChildren(e)){
            if (child.getTagName().toLowerCase().equals("blockrefs")){
                for (Element bref : getChildren(child)){
                    if (bref.getTagName().toLowerCase().equals("blockref")){
                        String target = getTarget(bref);
                        if (!target.isEmpty()){
                            blocks.add(target);
                        }
                    }
                }                             
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
                TagNameMapper.INSTANCE.addTageNameType(block, "block");
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
            if (child.getTagName().toLowerCase().equals("completecondition")){
                return ObjectifyIntentionalExpressions.INSTANCE.createExpression(child, m);
            }
        }
        return new ConditionExpression();
    }
    
}
