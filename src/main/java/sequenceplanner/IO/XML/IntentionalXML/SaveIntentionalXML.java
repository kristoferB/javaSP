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
import sequenceplanner.IO.XML.ObjectifyXML;
import sequenceplanner.IO.XML.XMLDOMSaver;
import sequenceplanner.model.Model;

/**
 *
 * @author kbe
 */
public class SaveIntentionalXML {
    
    //private final ObjectifyOperationsIntentionalOldModel op = new ObjectifyOperationsIntentionalOldModel();
    //private final ObjectifyVariableIntentionalOldModel var = new ObjectifyVariableIntentionalOldModel();
    private final ObjectifySOPIntentionalOldModel sop = new ObjectifySOPIntentionalOldModel();
    private final ObjectifyOperationsIntentionalOldModel op = new ObjectifyOperationsIntentionalOldModel();


    public SaveIntentionalXML(String fileToSave, Object model){
        //parse for old Model:
        Set<ObjectifyXML> s = new HashSet<ObjectifyXML>(); 
        s.add(sop); s.add(op);
        Set<Object> models = new HashSet<Object>(); models.add(model);
        XMLDOMSaver xmls = new  XMLDOMSaver(s,models); 
        xmls.saveXMLFile(fileToSave);  
    }    
   
    


    
    
    
    
    
       
            
            
}
