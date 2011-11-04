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
public class saveIntentionalXML {
    
    //private final ObjectifyOperationsIntentionalOldModel op = new ObjectifyOperationsIntentionalOldModel();
    //private final ObjectifyVariableIntentionalOldModel var = new ObjectifyVariableIntentionalOldModel();
    private final ObjectifySOPIntentionalOldModel sop = new ObjectifySOPIntentionalOldModel();


    public saveIntentionalXML(String fileToSave, Object model){
        //parse for old Model:
        Set<ObjectifyXML> s = new HashSet<ObjectifyXML>(); 
        s.add(sop);
        Set<Object> models = new HashSet<Object>(); models.add(model);
        XMLDOMSaver xmls = new  XMLDOMSaver(s,models); 
        xmls.saveXMLFile(fileToSave);  
    }    
   
    


    
    
    
    
    
       
            
            
}
