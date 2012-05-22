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

/*
 * This is a quick fix to handle product locking. 
 * Its not thread safe!!
 */
package sequenceplanner.IO.optimizer;

import java.util.*;

/**
 *
 * @author kbe
 */
public enum ProductLocker {
    INSTANCE;
    
    private Map<String,Set<String>> seamBlockMap;
    private Map<String, PointerPos> blockPosition = new HashMap<String,PointerPos>();
    private List<String> locker = new LinkedList<String>();
    private boolean isCleard = false;
    
    public void initializeLocker(Map<String,Set<String>> seamBlockMap){
        this.seamBlockMap = seamBlockMap; // should be made a defensive copy!
        this.blockPosition.clear();
        this.locker.clear();
        
        for (Set<String> blocks : this.seamBlockMap.values()){
            for (String block : blocks){
                if (!blockPosition.containsKey(block)){
                    locker.add("");
                    PointerPos pp = new PointerPos(locker.size()-1); 
                    blockPosition.put(block, pp);
                }
            }
        }
        isCleard = true;
    }
    
    public void reset(){
        if (!isCleard) initializeLocker(this.seamBlockMap);
    }
       
    public boolean isSeamAvailible(String seam){
        if (seam.isEmpty()) return true;
        Set<String> blocks = this.seamBlockMap.get(seam);
        for (String block : blocks){
            if (!locker.get(bPos(block)).isEmpty()) 
                return false;
        }
        return true;
    }
    
    public boolean lockSeam(String seam, String operationID){
        if (seam.isEmpty()) return true;
        isCleard = false;
        if (!isSeamAvailible(seam)) return false;
        
        Set<String> blocks = this.seamBlockMap.get(seam);
        for (String block : blocks){
            locker.set(bPos(block), operationID);
        }                                
        return true;
    }
    
    public boolean unLockSeam(String seam, String operationID){
        if (seam.isEmpty()) return true;
        Set<String> blocks = this.seamBlockMap.get(seam);
        PointerPos newPP = null;
        for (String block : blocks){
            if (!locker.get(bPos(block)).equals(operationID)) return false; 
            if (newPP == null) newPP = blockPosition.get(block);
            else if (bPos(block) < newPP.pos) newPP = blockPosition.get(block);
        }                               
        
        for (String block : blocks){
            locker.set(bPos(block), ""); // unlock
            blockPosition.get(block).setMaster(newPP);
        }
        
        return true;
    }
    
    private int bPos(String block){
        return blockPosition.get(block).getPos();
    }
    
    private class PointerPos{
        private int pos;
        private PointerPos master = null;
        PointerPos(int pos){
            this.pos = pos;
        }
        
        int getPos(){
            if (master == null) return pos;
            return master.getPos();
        }
        
        void setMaster(PointerPos pp){
            if (pp.getPos() == pos) return;
            if (pp.getPos()>pos) pp.setMaster(this);
            else if (master == null && pp.getPos()<pos) master = pp;
            else if (master.getPos()< pp.getPos()) pp.setMaster(master);
            else if (master.getPos()> pp.getPos()){
                master.setMaster(pp); master = pp;
            }
        }
        
        @Override
        public String toString(){
            return Integer.toString(getPos());
        }
    }
}
