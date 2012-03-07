/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.IO.XML.IntentionalXML;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kbe
 */
public enum TagNameMapper {
    INSTANCE;
    
    private Map<String,String> tagNameMap = new HashMap<String,String>();
    
    public String getTagType(String tagName){
        return tagNameMap.get(tagName.toLowerCase());
    }
    
    protected void addTageNameType(String tagName, String tagType){
        tagType = tagType.toLowerCase(); tagName = tagName.toLowerCase();
        if (tagNameMap.containsKey(tagName)) return;
        
        if (tagNameMap.containsKey(tagType)){
            tagType = tagNameMap.get(tagType);
        }
        tagNameMap.put(tagName, tagType);
    }
    
}
