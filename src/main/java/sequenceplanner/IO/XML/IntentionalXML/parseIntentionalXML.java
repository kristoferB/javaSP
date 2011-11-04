package sequenceplanner.IO.XML.IntentionalXML;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.IO.XML.ObjectifyXML;
import sequenceplanner.IO.XML.XMLDOMParser;
import sequenceplanner.model.Model;

/**
 *
 * @author kbe
 */
public class parseIntentionalXML {
    
    private final ObjectifyOperationsIntentionalOldModel op = new ObjectifyOperationsIntentionalOldModel();
    private final ObjectifyVariableIntentionalOldModel var = new ObjectifyVariableIntentionalOldModel();
    private final ObjectifySOPIntentionalOldModel sop = new ObjectifySOPIntentionalOldModel();
    private final ObjectifyResourceIntentionalOldModel res = new ObjectifyResourceIntentionalOldModel();
    private final Model oldModel;


    public parseIntentionalXML(String xmlPath, Set<Object> models){
        //parse for old Model:
        Set<ObjectifyXML> s = new HashSet<ObjectifyXML>(); 
        s.add(op); s.add(var);s.add(sop);s.add(res);
        XMLDOMParser xmlp = new  XMLDOMParser(s,models); 
        Set<Object> filledModels = xmlp.loadModelFromFile(xmlPath) ;      
       
        Model model = null;
        for (Object m : filledModels){
           if (m instanceof Model){
               model = (Model) m;
               break;
           }
        }
        oldModel = model;    
    }    
   
    
    public Model getModel(){
        return oldModel;
    }


    
    
    
    
    
       
            
            
}
