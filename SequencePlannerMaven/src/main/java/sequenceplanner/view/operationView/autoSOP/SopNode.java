package sequenceplanner.view.operationView.autoSOP;

import java.util.ArrayList;
import java.lang.Math.*;
import sequenceplanner.model.data.OperationData;



/**
 *
 * @author kbe
 */
public class SopNode {

    public final static int OPERATION = 0;
    public final static int PARALLEL = 1;
    public final static int ALTERNATIVE = 2;
    private int nodeType = OPERATION;

    private static int uniqueIDCounter = 1;
    private int uniqueID = 0;

    private SopNode pred;
    private SopNode next;
    private ArrayList<SopNode> branches = new ArrayList<SopNode>();

    private OperationData data;

    private boolean isBranchSolved = false;


    public SopNode(OperationData data) {
        this.data = data;
        this.uniqueID = this.getUniqueId();
    }
    
    public SopNode(SopNode next, OperationData data) {
        this.next = next;
        this.data = data;
        this.uniqueID = this.getUniqueId();
    }

    public SopNode(int nodeType, ArrayList<SopNode> branches) {
        this.nodeType = nodeType;
        this.branches = branches;
        for (SopNode n : branches){
            if (n != null){
                n.setNext(this);
            }
        }
        this.uniqueID = this.getUniqueId();
    }

    private int getUniqueId(){
        uniqueIDCounter += 1;
        return uniqueIDCounter;
    }


    // Setters and getters below
    public SopNode getPred() {
        return pred;
    }

    public void setPred(SopNode pred) {
        this.pred = pred;
    }

    public SopNode getNext() {
        return next;
    }

    public void setNext(SopNode next) {
        this.next = next;
    }

    public ArrayList<SopNode> getBranches() {
        return branches;
    }

    public void setBranches(ArrayList<SopNode> branches) {
        this.branches = branches;
    }

    public int getNodeType(){
        return this.nodeType;
    }

    public OperationData getData(){
        return this.data;
    }

    public void setData(OperationData d){
        this.data = d;
    }

    public int getId(){
        if (this.isOperation() && this.data != null){
            return data.getId();
        } else if (this.isBranch()){
            return 0 - this.uniqueID;
        }

        return 0;
    }

    public String toString(){
        String result = new String();
        if (getNodeType() == OPERATION)
            result = "NodeType: Operation" + "\n";
        if (getNodeType() == PARALLEL)
            result = "NodeType: PARALLEL" + "\n";
        if (getNodeType() == ALTERNATIVE)
            result = "NodeType: ALTERNATIVE" + "\n";
        
        if (this.isOperation()){
            result += "id: " + this.data.getId()  + " ";
            result += "Name: " + data.getName() + " ";
        } else if (this.isBranch()){
            result += "ID = " + this.getId() + " ";
            result += "No of branches = " + this.getBranches().size()  + " ";
            result += "branch ids: [ ";
            for (SopNode n : this.getBranches()){
                result += n.getId() + ", ";
            }
            result += "] ";
            result += "SolvedBranch = " + this.isBranchSolved  + "\n";

        }

        if (this.getNext() != null){
            result += "NextID: " + this.getNext().getId() + " ";
        }
        if (this.getPred() != null){
            result += "PredID: " + this.getPred().getId() + " ";
        }

        return result + "\n";
    }



    public boolean isBranch(){
        return (nodeType==ALTERNATIVE) || (nodeType==PARALLEL);
    }
    
    public boolean isAlternative(){
        return this.nodeType==ALTERNATIVE;
    }
    
    public boolean isParallel(){
        return this.nodeType==PARALLEL;
    }

    public boolean isOperation(){
        return this.nodeType==OPERATION;
    }

    
    public boolean addBranchNode(SopNode n){
        if (this.isBranch()){
            this.branches.add(n);
            return true;
        } else return false;
    }

    public boolean isBranchSolved(){
        return this.isBranchSolved;
    }

    public void setBranchSolved(boolean b){
        this.isBranchSolved = b;
    }


    @Override
    public boolean equals(Object obj) {

        SopNode n = (obj instanceof SopNode) ? (SopNode) obj : null;

        if (n==null) return false;

        if (this.isOperation() && this.data != null && n.isOperation() && n.getData() != null){
            return this.getData().equals(n.getData());
        }
        if (this.isBranch() && n.isBranch()){
            return this.getId() == n.getId();
        }
        
        return this==obj;

    }

    @Override
    public int hashCode() {
        return this.getId();
    }


    

}
