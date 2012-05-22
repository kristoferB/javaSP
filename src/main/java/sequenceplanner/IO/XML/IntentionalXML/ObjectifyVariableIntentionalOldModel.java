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

import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sequenceplanner.IO.XML.ObjectifyXML;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ResourceVariableData;

/**
 * This objectifier takes variables elements from an xml file from 
 * intentional software DWB, and adds the variables to the old SP model.
 * 
 * Currently all variables are added flat and not in a resource.
 * 
 * @author kbe
 */
public class ObjectifyVariableIntentionalOldModel extends AbstractObjectifyIntentionalOldModel {

    private static final String elementTag = "variables";
    private static final String rootTag = "ProcessPlanForExport";
    private static final String objectTag = "variable";
    private static final int maxForInt = 1000000;
    

    public ObjectifyVariableIntentionalOldModel() {
        super(rootTag,elementTag);
    }
        
    
    @Override
    protected boolean createElements(Model model, Element e){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean addElement(Element e, Model m){
        if (!e.getTagName().toLowerCase().equals(objectTag.toLowerCase())) return false;
        String id = getName(e); if (id.isEmpty()) return false;
    
        String value = e.getAttribute("value");
        int init = 0;
        int min  = 0;
        int max = 1;
        Integer Type = ResourceVariableData.BINARY;
       
        if (!value.equals("")){
            try{
                float f = Float.parseFloat(value);
                init = Math.round(f);
                max = maxForInt;   
                Type = ResourceVariableData.INTEGER;
            } catch(NumberFormatException exept){
                return false;
            }
        }
                  
        ResourceVariableData var = new ResourceVariableData(id, m.newId());
        var.setType(Type);
        var.setInitialValue(init);
        var.setMax(max);
        var.setMin(min);
        TreeNode variable = new TreeNode(var);
        m.insertChild(m.getResourceRoot(), variable);
        
        TagNameMapper.INSTANCE.addTageNameType(id, objectTag.toLowerCase());
        return true;          
    }
    
}
