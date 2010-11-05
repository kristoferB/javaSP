
package sequenceplanner.efaconverter;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 *
 * This class creates a set of sequences and identifies if operations
 * and variables are local in a sequence. This class can be used before generating
 * EFA to remove parallelism.
 *
 * Left to do: Identify when children are only local to its parent sequence.
 * The problem is that if the children are local to the seq, all the other
 * operations must be checked again to see if they have variables that are
 * local to its sequence including the children. Need probably to reverse order.
 *
 * Comment: In Sp2 we need to have a better separation between
 * the model and other parts of the code. If the model are changed
 *  everything will change.
 * We should use an interface where info are retrived independent of the
 * structure of model.
 *
 *
 * @author kbe
 */
public class OperationSequencer {

    private final ModelParser modelparser;

    private final Logger log = Logger.getLogger(OperationSequencer.class);
    
    /*
     * The constructor.
     */
    public OperationSequencer(ModelParser spModelParser) {
        this.modelparser = spModelParser;
    }

    /**
     * Creates sequences of the operations in Model. Also identifies if
     * operations and variables are local to that sequence
     * @param spModel
     * @return
     */
    public Set<OpNode> sequenceOperations(){
        Set<OpNode> tops = createSequences();        
        tops = findLocalSeqRelations(tops);
        printSequences(tops);
        return tops;
    }


    /*
     * Create operation sequences and returns the first operation
     * in each sequence.
     * The operations and variables must be loaded into variables and
     * operations
     *
     */
    private Set<OpNode> createSequences(){
        populateNodes();

        HashSet<OpNode> seq = new HashSet<OpNode>();
        Set<OpNode> lastInSeq = findLastOperations(modelparser.getOperations());
        if (lastInSeq.isEmpty()){
            // Add test for circularity in the future!
        	log.warn("The model is empty or circular relations");
            return new HashSet<OpNode>();
        }

        // Maybe change this to recursive?
        while (!lastInSeq.isEmpty()){
            OpNode node = null;
            for (OpNode n : lastInSeq){
               node = n;
               break; // take only one!
            }
            if (node != null){
                lastInSeq.remove(node);
                seq.add(sequenceser(node, lastInSeq));
            }
        }

        return seq;
    }

    /*
     * Goes through each operation and add what other operation
     * and variables it is related to. Also add itself to other operations
     * and variables if it is related to it.
     * The operations and variables must be loaded into variables and
     * operations!
     *
     */
    private void populateNodes(){
        for (OpNode node : modelparser.getOperations()){
            Set<OpNode> preRelatedToOperations = modelparser.getPreRelatedToOperations(node);
            Set<VarNode> preGuardVars = modelparser.getPreRelatedToGuardVariables(node);
            Set<VarNode> preActionVars = modelparser.getPreRelatedToActionVariables(node);
            Set<OpNode> postRelatedToOperations = modelparser.getPostRelatedToOperations(node);
            Set<VarNode> postGuardVars = modelparser.getPostRelatedToGuardVariables(node);
            Set<VarNode> postActionVars = modelparser.getPostRelatedToActionVariables(node);
            Set<OpNode> children = modelparser.getChildren(node);

            node.addPreRelatesToOperations(preRelatedToOperations);
            node.addPreGuardVariables(preGuardVars);
            node.addPreActionVariables(preActionVars);
            node.addPostRelatesToOperations(postRelatedToOperations);
            node.addPostGuardVariables(postGuardVars);
            node.addPostActionVariables(postActionVars);

            for (OpNode child : children) {
                child.addPreRelatesToOperation(node);
                child.addRelatedByOperationFinished(node);
                node.addRelatedByOperationExecute(child);
                node.addPostRelatesToOperation(child);
            }

            for (OpNode n : node.getRelatesToOperations()){
                n.addRelatedByOperation(node);
            }
            for (VarNode n : node.getGuardVariables()){
                n.addGetterOperation(node);
            }
            for (VarNode n : node.getActionVariables()){
                n.addSetterOperation(node);
            }
        }

        // A clean-Up round 
        for (OpNode node : modelparser.getOperations()){
            node.addRelatedByOperationExecute(modelparser.getOperationsRelatedToExecute(node));
            node.addRelatedByOperationInit(modelparser.getOperationsRelatedToInit(node));
            node.addRelatedByOperationFinished(modelparser.getOperationsRelatedToFinished(node));
        }
    }

    /**
     * Check all operations if they are last in sequence, i.e that no
     * other operations are relating to that operations finished location. This method assume that
     * populateNodes have been executed.
     *
     * @return The last operation nodes
     */
    private Set<OpNode> findLastOperations(Collection<OpNode> theOperations){
        HashSet<OpNode> lastNodes = new HashSet<OpNode>();
        for (OpNode n : theOperations){
            if (n.getRelatedByOperationsFinished().isEmpty()){
                lastNodes.add(n);
            } else {
                boolean onlyParent = true;
                OpNode parent = modelparser.getParent(n);
                for (OpNode relOp : n.getRelatedByOperations()){
                    if (!relOp.equals(parent)){
                        onlyParent = false;
                        break;
                    }
                }
                if (onlyParent) lastNodes.add(n);
            }
        }
        return lastNodes;
    }

    /**
     * Identifies sequences by starting with @param operation and checking its
     * predecessors. If more then one predecessor is found, one of them are
     * picked and the reset are added to the lastInSeq Set.
     * An operation can only be included in one sequence
     * @param operation The bottom operation in a sequence
     * @param lastInSeq A set where new operations will be added if they are not included in the sequence
     * @return Will return the topNode of the sequence.
     */
    private OpNode sequenceser(OpNode operation, Set<OpNode> lastInSeq){
        OpNode top = findTop(operation);
        boolean findTop = false;
        while (!findTop){
            Set<OpNode> predisar = modelparser.getPrecedingOperations(top);
            Set<OpNode> sometimeInSeqPreds = modelparser.getSometimesInSeq(top, predisar);
            if (predisar.isEmpty()){
                findTop = true;
                break;
            }
            // Do not include sometimes in sequence in the staright sequencese (OR)
            if (!sometimeInSeqPreds.isEmpty()){
                 lastInSeq.addAll(sometimeInSeqPreds);
                 predisar.removeAll(sometimeInSeqPreds);
            }

            for (OpNode n : predisar) {
                // Adding the first predecessor
                if (!top.hasPredecessor()) {
                    if (!n.hasSuccessor()){
                        top.setPredecessor(n);
                        n.setSuccessor(top);                        
                    }
                } else {
                    if (!n.hasSuccessor() || !n.hasPredecessor()){
                        lastInSeq.add(n);
                    }
                }
            }
            if (top.hasPredecessor()) {
                top = findTop(top.getPredecessor());
            } else {
                findTop = true;
                break;
            }

        }
       return top;
    }

    /**
     * Finds the first (top) operation in a seqeunce from operaiton op
     * @param op The startoperation
     * @return The top (first) operation in the sequence
     */
    private OpNode findTop(OpNode op){
        return reqFindTop(op, new HashSet<OpNode>());
    }

    private OpNode reqFindTop(OpNode op, Set<OpNode> findOps){
        if (op.hasPredecessor()){
            if (!findOps.add(op.getPredecessor())){
                return op;
            }
            return reqFindTop(op.getPredecessor(), findOps);
        } else
            return op;
    }


    /**
     * Finds operation states and variables that are local in a sequence and marks the nodes
     * so that they are omitted when the EFA is generated.
     * @param topNodes All the first nodes un the sequences
     * @return Returns same Set but with new properties in the nodes.
     */
    private Set<OpNode> findLocalSeqRelations(Set<OpNode> topNodes){
        Set<Set<OpNode>> seqMap = createSequenceGroups(topNodes);

        for (Set<OpNode> map : seqMap){
            for (OpNode operation : map){
                boolean seqLocalPreCond = true;
                boolean seqLocalPostCond = true;
                // Iterates through operation relations and checks if they are local in the sequence
                seqLocalPreCond = isOperationCondSeqLocal(operation.getRelatedByOperationsInit(),map);
                seqLocalPostCond = isOperationCondSeqLocal(operation.getRelatedByOperationsExecute(),map);
                seqLocalPostCond = seqLocalPostCond && isOperationCondSeqLocal(operation.getRelatedByOperationsFinished(),map);

                seqLocalPreCond = seqLocalPreCond && isOperationCondSeqLocal(operation.getPreRelatesToOperations(),map);
                seqLocalPostCond = seqLocalPostCond && isOperationCondSeqLocal(operation.getPostRelatesToOperations(),map);

                seqLocalPreCond =  seqLocalPreCond && isOperationCondSeqLocal(operation.getPreActionVariables(), true, map);
                seqLocalPostCond = seqLocalPostCond && isOperationCondSeqLocal(operation.getPostActionVariables(), true, map);

                seqLocalPreCond = seqLocalPreCond && isOperationCondSeqLocal(operation.getPreGuardVariables(), false, map);
                seqLocalPostCond = seqLocalPostCond && isOperationCondSeqLocal(operation.getPostGuardVariables(), false, map);

                operation.setHasSeqLocalPreCond(seqLocalPreCond);
                operation.setHasSeqLocalPostCond(seqLocalPostCond);
            }
        }
        // Hidden unused variables
        for (VarNode vn : modelparser.getVariables()){
            if (vn.getGetters().isEmpty() && vn.getSetters().isEmpty()){
                vn.setHidden(true);
            }

            // Comment on features. kb 100703
            // We should implement some smart test to check if a variable is changed to a specific value that
            // an operation has in its guard. If that is not the case it will be a trivial deadlock or similiar
            // This means that a modeling error has been made.
            // But it should not be done here!
        }

        return topNodes;
    }

    private boolean isOperationCondSeqLocal(Set<OpNode> relatedOperations, Set<OpNode> sequence){
        for (OpNode n : relatedOperations){
            if (!sequence.contains(n)){
                return false;
            }
        }
        return true;
    }

    private boolean isOperationCondSeqLocal(Set<VarNode> relatedVariables, boolean isAction, Set<OpNode> sequence){
        for (VarNode v : relatedVariables) {
            v.setHidden(false);
            for (OpNode n : v.getSetters()) {
                if (!sequence.contains(n)) return false;
            }

            boolean isHidden = true;
            for (OpNode n : v.getGetters()) {
                if (!sequence.contains(n)) {
                    if (isAction){
                        return false;
                    }
                    isHidden = false;
                }
            }
            v.setHidden(isHidden);
        }
        return true;
    }


    private Set<Set<OpNode>> createSequenceGroups(Set<OpNode> topNodes){
        Set<Set<OpNode>> seqMap = new HashSet<Set<OpNode>>();

        // Create a hashmap per sequence containing all op in that seq.
        for (OpNode node : topNodes){
            Set<OpNode> map = reqMapCreater(node, new HashSet<OpNode>());
            seqMap.add(map);
        }

        // A test to check if the same op is in multiple sequences!
        // This should not be possible.
        // should be placed in a unitTest! Remove later.
        for (Set<OpNode> map : seqMap){
            for (Set<OpNode> map2 : seqMap){
                if (map.equals(map2)) break;
                for (OpNode n : map){
                    if (map2.contains(n)){
                        log.info("An operation in multiple sequence!");
                        log.info("Duplicated operation: " + n.getName());
                        assert(true);
                    }
                }
            }
        }

        return seqMap;
    }

    private Set<OpNode> reqMapCreater(OpNode node, Set<OpNode> map){

        // A small test, should be placed in a unitTest! Remove later
        if (map.contains(node)){
        	log.info("A sequnece contains the same operation! Should not be possible");
        	log.info("Duplicated operation: " + node.getName());
            assert(true);
        }

        map.add(node);
        if (node.hasSuccessor()){
            return reqMapCreater(node.getSuccessor(), map);
        } else {
            return map;
        }
    }


    private void printSequences(Set<OpNode> tops){
        int i = 1;
        for (OpNode n : tops){
            System.out.println("--------------------------------------");
            System.out.println("Sequence no: " + Integer.toString(i));
            i++;
            System.out.println(reqSeq(n));
        }
        System.out.println("--------------------------------------");
        System.out.println("Variables");
        for (VarNode var: modelparser.getVariables()){
            System.out.println(var.toString());
        }
    }

    private String reqSeq(OpNode n){
        if (n.hasSuccessor()){
            return n.toString() + " -> " + reqSeq(n.getSuccessor());
        }else
            return n.toString();
    }

}
