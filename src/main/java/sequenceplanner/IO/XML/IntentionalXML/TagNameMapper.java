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
