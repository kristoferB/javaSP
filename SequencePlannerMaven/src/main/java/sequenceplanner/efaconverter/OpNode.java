
package sequenceplanner.efaconverter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import sequenceplanner.model.TreeNode;

/**
 * This class is used when identifying relations among operations
 * Should be integrated in the operation model in the future!
 *
 * @author kbe
 */
public class OpNode {

    private String name;
    private int id;
    private TreeNode operation;
    private HashSet<OpNode> preRelatesToOperations;
    private HashSet<OpNode> relatedByOperations;
    private HashSet<OpNode> relatedByOperationsExecute;
    private HashSet<OpNode> relatedByOperationsInit;
    private HashSet<OpNode> relatedByOperationsFinished;
    private HashSet<VarNode> preGuardVariables;
    private HashSet<VarNode> preActionVariables;
    private HashSet<OpNode> postRelatesToOperations;
    private HashSet<VarNode> postGuardVariables;
    private HashSet<VarNode> postActionVariables;

    private HashSet<OpNode> children;
    private OpNode parent = null;

    private OpNode predecessor = null;
    private OpNode successor = null;

    // These are used to check if the op can be collabsed in the seq EFA
    private boolean hasSeqLocalPostCond = false;
    private boolean hasSeqLocalPreCond = false;
    private boolean isParentSeqLocal = false;


    public OpNode(String name, int id, TreeNode operation) {
        this.name = name;
        this.operation = operation;
        this.id = id;

        preRelatesToOperations = new HashSet<OpNode>();
        relatedByOperations = new HashSet<OpNode>();
        relatedByOperationsExecute = new HashSet<OpNode>();
        relatedByOperationsFinished = new HashSet<OpNode>();
        relatedByOperationsInit = new HashSet<OpNode>();
        preGuardVariables = new HashSet<VarNode>();
        preActionVariables = new HashSet<VarNode>();
        postRelatesToOperations = new HashSet<OpNode>();
        postGuardVariables = new HashSet<VarNode>();
        postActionVariables = new HashSet<VarNode>();

        children = new HashSet<OpNode>();

    }

    public String getName(){
        return this.name;
    }

    // Should only be used by the modelParser
    public TreeNode getTreeNode(){
        return operation;
    }

    public OpNode getParent() {
        return parent;
    }

    public void setParent(OpNode parent) {
        this.parent = parent;
    }

    public boolean hasParent(){
        return this.parent != null;
    }

    public void addPreRelatesToOperations(Set<OpNode> ops){
        for (OpNode op : ops){
            addPreRelatesToOperation(op);
        }
    }

    public void addPreRelatesToOperation(OpNode op){
        this.preRelatesToOperations.add(op);
    }

    public void addPostRelatesToOperations(Set<OpNode> ops){
        for (OpNode op : ops){
            addPostRelatesToOperation(op);
        }
    }

    public void addPostRelatesToOperation(OpNode op){
        this.postRelatesToOperations.add(op);
    }

    public void addRelatedByOperations(Set<OpNode> ops){
        for (OpNode op : ops){
            addRelatedByOperation(op);
        }
    }

    public void addRelatedByOperation(OpNode op){
        this.relatedByOperations.add(op);
    }

    public void addRelatedByOperationExecute(OpNode op){
        this.relatedByOperationsExecute.add(op);
        addRelatedByOperation(op);
        if (this.equals(op.getParent())){
            this.children.add(op);
        }
    }

   public void addRelatedByOperationExecute(Set<OpNode> ops){
        for (OpNode op : ops){
            addRelatedByOperationExecute(op);
        }
    }

    public void addRelatedByOperationInit(OpNode op) {
        this.relatedByOperationsInit.add(op);
        addRelatedByOperation(op);
    }

    public void addRelatedByOperationInit(Set<OpNode> ops) {
        for (OpNode op : ops) {
            addRelatedByOperationInit(op);
        }
    }

    public void addRelatedByOperationFinished(OpNode op) {
        this.relatedByOperationsFinished.add(op);
        addRelatedByOperation(op);
    }

    public void addRelatedByOperationFinished(Set<OpNode> ops) {
        for (OpNode op : ops) {
            addRelatedByOperationFinished(op);
        }
    }

    public void addPreGuardVariables(Set<VarNode> vars){
        for (VarNode var : vars){
            this.preGuardVariables.add(var);
        }
    }

    public void addPreGuardVariable(VarNode var){
        this.preGuardVariables.add(var);
    }
    
    public void addPostGuardVariables(Set<VarNode> vars){
        for (VarNode var : vars){
            this.postGuardVariables.add(var);
        }
    }

    public void addPostGuardVariable(VarNode var){
        this.postGuardVariables.add(var);
    }

    public void addPreActionVariables(Set<VarNode> vars){
        for (VarNode var : vars){
            this.preActionVariables.add(var);
        }
    }

    public void addPreActionVariable(VarNode var){
        this.preActionVariables.add(var);
    }

    public void addPostActionVariables(Set<VarNode> vars){
        for (VarNode var : vars){
            this.postActionVariables.add(var);
        }
    }

    public void addPostActionVariable(VarNode var){
        this.postActionVariables.add(var);
    }



    public Set<VarNode> getActionVariables() {
        Set<VarNode> allVar = new  HashSet<VarNode>();
        allVar.addAll(preActionVariables);
        allVar.addAll(postActionVariables);
        return allVar;
    }

    public Set<VarNode> getPreActionVariables() {
        return preActionVariables;
    }

    public Set<VarNode> getPostActionVariables() {
        return postActionVariables;
    }

    public Set<VarNode> getGuardVariables() {
        Set<VarNode> allVar = new  HashSet<VarNode>();
        allVar.addAll(preGuardVariables);
        allVar.addAll(postGuardVariables);
        return allVar;
    }

    public Set<VarNode> getPreGuardVariables() {
        return preGuardVariables;
    }

    public Set<VarNode> getPostGuardVariables() {
        return postGuardVariables;
    }

    public Set<OpNode> getRelatesToOperations() {
        Set<OpNode> allVar = new  HashSet<OpNode>();
        allVar.addAll(preRelatesToOperations);
        allVar.addAll(postRelatesToOperations);
        return allVar;
    }
    
    public Set<OpNode> getPreRelatesToOperations() {
        return preRelatesToOperations;
    }

    public Set<OpNode> getPostRelatesToOperations() {
        return postRelatesToOperations;
    }


    public Set<OpNode> getRelatedByOperations() {
        return this.relatedByOperations;
    }

    public Set<OpNode> getRelatedByOperationsExecute() {
        return this.relatedByOperationsExecute;
    }

    public Set<OpNode> getRelatedByOperationsInit() {
        return this.relatedByOperationsInit;
    }

    public Set<OpNode> getRelatedByOperationsFinished() {
        return this.relatedByOperationsFinished;
    }

    public OpNode getPredecessor() {
        return predecessor;
    }

    public boolean hasPredecessor() {
        return predecessor != null;
    }

    public void setPredecessor(OpNode preceding) {
        this.predecessor = preceding;
    }

    public OpNode getSuccessor() {
        return successor;
    }

    public boolean hasSuccessor() {
        return successor != null;
    }

    public void setSuccessor(OpNode succeeding) {
        this.successor = succeeding;
    }

    public boolean hasSeqLocalPostCond() {
        return hasSeqLocalPostCond;
    }

    public void setHasSeqLocalPostCond(boolean hasSeqLocalPostCond) {
        this.hasSeqLocalPostCond = hasSeqLocalPostCond;
    }

    public boolean hasSeqLocalPreCond() {
        return hasSeqLocalPreCond;
    }

    public void setHasSeqLocalPreCond(boolean hasSeqLocalPreCond) {
        this.hasSeqLocalPreCond = hasSeqLocalPreCond;
    }

    public boolean isIsParentSeqLocal() {
        return isParentSeqLocal;
    }

    public void setIsParentSeqLocal(boolean isParentSeqLocal) {
        this.isParentSeqLocal = isParentSeqLocal;
    }

    public int getId() {
        return id;
    }

    public String getStringId() {
        return Integer.toString(id);
    }

    public HashSet<OpNode> getChildren() {
        return children;
    }

    public boolean hasChildren(){
        return !children.isEmpty();
    }


    

    @Override
    public String toString(){
        String s = new String(name);

        s = isParentSeqLocal
                ? s + "_pL:"
                : s + ":";

        s = hasSeqLocalPreCond
                ? s + "Lpre{"
                : s + "pre{";

        s = s + toStringRel(getPreRelatesToOperations(),getPreGuardVariables(),getPreActionVariables());

        s = hasSeqLocalPostCond
                ? s + "} Lpost{"
                : s + "} post{";

        s = s + toStringRel(getPostRelatesToOperations(),getPostGuardVariables(),getPostActionVariables());
        s = s + '}';
        
        return s;
    }

    private String toStringRel(Set<OpNode> operations, Set<VarNode> guards, Set<VarNode> actions){
        String s = new String();
        Iterator<OpNode> iOp = operations.iterator();
        while(iOp.hasNext()){
            OpNode n = iOp.next();
            s = iOp.hasNext() ? s + n.getName() + ',' : s + n.getName();
        }

        Iterator<VarNode> iGuard = guards.iterator();
        s = iGuard.hasNext() ? s + '[' : s;
        while(iGuard.hasNext()){
            VarNode n = iGuard.next();
            s = iGuard.hasNext() ? s + n.getName() + ',' : s + n.getName() + ']';
        }

        Iterator<VarNode> iAction = actions.iterator();
        s = iAction.hasNext() ? s + '/' : s;
        while(iAction.hasNext()){
            VarNode n = iAction.next();
            s = iAction.hasNext() ? s + n.getName() + ',' : s + n.getName();
        }

        return s;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OpNode other = (OpNode) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + this.id;
        return hash;
    }



    




}
