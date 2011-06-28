package sequenceplanner.view.operationView.autoSOP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import sequenceplanner.model.data.OperationData;

/**
 *
 * @author kbe
 */
public class SequenceCreator {

    HashMap<Integer,OperationData> operations;
    HashMap<Integer,SopNode> solvedBranches;

    public SequenceCreator() {
        operations = new HashMap<Integer,OperationData>();
        solvedBranches = new HashMap<Integer,SopNode>();
    }


    /**
    * Makes a sequence tree of SopNodes. Start at a root operation
    * and parse through the operations in the HashMap
    * @param OperationData rootOperation
    * @param HashMap<Integer,OperationData> operations   All the operations with id as key
    * @return SopNode       Which is the root Node
    */
    public SopNode getSequence(OperationData rootOperation,
                               HashMap<Integer,OperationData> operations){

        // Create the tree
        this.operations = operations;
        SopNode root = this.createSopNode(rootOperation);
        this.sequnenceParser(root);

        branchSolver(root);
        removeDuplicate(root);
        
        // Draw seq:
        this.printSeq(root);
        return root;
    }



    /**
    * Recursive function to create the sequnces
    * @param SopNode n      The current node
    */
    private void sequnenceParser(SopNode n){
    if (!operations.isEmpty() && n != null){
        if (n.isOperation()){
            sequnenceParser(createPredNode(n));
        } else if (n.isBranch() && n.getBranches() != null){
            for (SopNode bn : n.getBranches()){
                sequnenceParser(bn);
            }
        }

        }
    }


    /**
    * Create the predecessor SopNode by chechking n's precondition
    * @param SopNode n      The current node
    */
    private SopNode createPredNode(SopNode n){
        if (n.isOperation() && n.getData() != null){
            LinkedList<LinkedList<OperationData>> pred =
                               getAvailiblePred(n.getData().getPredecessors());

            if (!this.checkIfCircular(n, pred)){
                if (pred.size() == 1){
                    if (pred.getFirst().size() == 1){
                        n.setPred(this.createSopNode(pred.getFirst().getFirst()));
                        n.getPred().setNext(n);
                    } else if (pred.getFirst().size() > 1){
                        n.setPred(this.createSopNodeAltBranch(pred.getFirst()));
                        n.getPred().setNext(n);
                    } else {
                        n.setPred(null);
                    }

                } else if (pred.size() > 1){
                    n.setPred(this.createSopNodeParallelBranch(pred));
                    n.getPred().setNext(n);
                } else {
                    n.setPred(null);
                }

                return n.getPred();
            }

        }
        return null;
    }

    /**
    * Translate a precondition to only relevant operations
    * i.e. operations included in the view.
    * @param LinkedList<LinkedList<Integer>> pred      The current precondition id
    */
    private LinkedList<LinkedList<OperationData>>
            getAvailiblePred(LinkedList<LinkedList<Integer>> pred){

        LinkedList<LinkedList<OperationData>> result = new LinkedList<LinkedList<OperationData>>();

        for (LinkedList<Integer> andList : pred){
            LinkedList<OperationData> or = new LinkedList<OperationData>();
            for (Integer i : andList){
                if (this.operations.containsKey(i)){
                    or.add(this.operations.get(i));
                }
            }
            if (!or.isEmpty()) result.add(or);
        }

        return result;
    }

    /**
    * Retrives the predecessors and check if the already exits in n's path back
    * to root
    * @param SopNode n      Current node
    * @param LinkedList<LinkedList<Integer>> pred     Current precondition
    */
    private boolean checkIfCircular(SopNode n, LinkedList<LinkedList<OperationData>> pred){
        ArrayList<OperationData> newOps = new ArrayList<OperationData>();
        for (LinkedList<OperationData> ld : pred){
            for (OperationData d : ld){
                newOps.add(d);
            }
        }
        boolean result = false;
        for (OperationData d : newOps){
            result = result || isCircular(n,d);
        }
        return result;
    }

    /**
    * Recursive search if a predecessor already is included in path
    * @param SopNode n      Current node
    * @param OperationData newOp     Added predecessor
    */
    private boolean isCircular(SopNode n, OperationData newOp){
        SopNode next = n.getNext();
        if (next != null){
            if (next.isOperation() && next.getData().equals(newOp)){
                return true;
            } else {
                return isCircular(next,newOp);
            }
        }
        return false;
    }

    /**
    * Duplicate operation may exist in the solution
    * If found they are removed
    * @param SopNode n      The root node.
    */
    private void removeDuplicate(SopNode root){
        HashMap<Integer, SopNode> nodes = new HashMap<Integer, SopNode>();
        duplicateRemover(root,nodes);
    }

    /**
    * Recursive search for duplicates
    * @param SopNode n      The root node.
    * @param HashMap<Integer, SopNode> nodes    To save all operations for faster search
    */
    private boolean duplicateRemover(SopNode n,  HashMap<Integer, SopNode> nodes){
        if (n == null) return false;
        if (n.isBranch()){
            for (SopNode br : n.getBranches())
                duplicateRemover(br, nodes);
            return duplicateRemover(n.getPred(),nodes);
        }
        boolean dupl = nodes.containsKey(n.getId());
        if (dupl && n.getNext() != null){
            n.getNext().setPred(null);
            n.setPred(null);
            return true;
        }
        if (!dupl){
            nodes.put(n.getId(), n);
            return duplicateRemover(n.getPred(),nodes);
        }
        return false;
    }


    /**
    * Recursive search first branch in tree which will be solved.
    * First method in branch solving
    * @param SopNode n      The root node.
    */
    private void branchSolver(SopNode root){
        if (root != null && root.isBranch()){
            solveBranch(root);
        } else if (root != null && root.isOperation()){
            branchSolver(root.getPred());
        }
    }

    /**
    * Solves a branch, recursive, by searching for when the branches meets.
    * The meeting operation node will be predecessor to the branch node
    * and removed from the child branches.
    * If a new branch is found, that is solved first.
    * If the branches do not complete, all operations will be
    * included in that branch, with an "open start".
    * @param SopNode branch      The branch node
    */
    private boolean solveBranch(SopNode branch){
        if (branch == null) return false;
                
        if (needBranchRemoval(branch)) return false;

        if (branch.isBranchSolved()) return true;

        //if (solvedBranches.containsKey(branch.getId())){
        //    branch = solvedBranches.get(branch.getId());
        //} else {

            ArrayList<ArrayList<SopNode>> allBranches = new ArrayList<ArrayList<SopNode>>();

            for (SopNode b : branch.getBranches()){
                ArrayList<SopNode> bTrace = new ArrayList<SopNode>();
                parseBranch(b, bTrace);
                allBranches.add(bTrace);
            }

            ArrayList<SopNode> firstBranch = allBranches.remove(0);
            boolean found = true;
            int id = 0;
            SopNode mergeNode = firstBranch.get(0);
            for (SopNode n : firstBranch){
                mergeNode = n;
                found = true;
                for (ArrayList<SopNode> list : allBranches){
                    if (list.contains(n)){
                        found = true && found;
                        id = n.getId();
                    } else {
                        found = false;
                        id = 0;
                    }
                }
                if (found == true && id != 0) break;
            }

            if (found == true && id != 0){
                if (mergeNode.getNext() != null)
                    if (mergeNode.getNext().equals(branch)){
                        branch.getBranches().remove(0);
                    } else
                        mergeNode.getNext().setPred(null);
                for (ArrayList<SopNode> list : allBranches){
                    SopNode mergeInB = list.get(list.indexOf(mergeNode));
                    if (mergeInB.getNext() != null && mergeInB.getNext().equals(branch)){
                        branch.getBranches().remove(list);
                    } else if (mergeInB.getNext() != null)
                        mergeInB.getNext().setPred(null);
                }

                branch.setPred(mergeNode);
                mergeNode.setNext(branch);
                if (! needBranchRemoval(branch)){
                    branch.setBranchSolved(true);
                }


            } else {
                branch.setPred(null);

                if (! needBranchRemoval(branch)){
                    branch.setBranchSolved(true);
                }
                
            }
        //}
        return true;
    }


    /**
    * Recursive adding of the operations in a branch. If a new branch is found it will be solved.
    * First method in branch solving
    * @param SopNode n      The root node.
    */
    private void parseBranch(SopNode n, ArrayList<SopNode> branch){
        if (n != null && n.isOperation()){
            branch.add(n);
            parseBranch(n.getPred(),branch);
        } else if (n != null && n.isBranch()){
            solveBranch(n);
            branch.add(n);
            parseBranch(n.getPred(),branch);
        }
    }


    private boolean needBranchRemoval(SopNode branch){

        if (branch == null) return true;
        if (branch.getBranches().size()>1) return false;

        if (branch.getBranches().size() == 1){
            ArrayList<SopNode> subBranch = new ArrayList<SopNode>();
            parseBranch(branch.getBranches().get(0),subBranch);

            if (subBranch.get(0) != null){
                subBranch.get(0).setNext(branch.getNext());
                if (branch.getNext() != null){
                    branch.getNext().setPred(subBranch.get(0));
                }
            }
            int i = subBranch.size()-1;
            while (i >= 0){
                if (subBranch.get(i) != null){
                    subBranch.get(i).setPred(branch.getPred());
                    if (branch.getPred() != null){
                        branch.getPred().setNext(subBranch.get(i));
                    }
                    break;
                }
                i -= 1;
            }

            branch.setPred(null);
            branch.setNext(null);
            return true;

        } else if (branch.getBranches().isEmpty()){
            if (branch.getNext() != null){
                branch.getNext().setPred(branch.getPred());
            }
            if (branch.getPred() != null){
                branch.getPred().setNext(branch.getNext());
            }

            branch.setPred(null);
            branch.setNext(null);
            return true;
        }

        return false;
    }

    /**
    * Prints a sequence tree. Only for test
    * @param SopNode n    The root operation
    */
    private void printSeq(SopNode n){
        if (n != null){
            System.out.println(n.toString());
            if (n.isOperation()){
                printSeq(n.getPred());
            } else if (n.isBranch()){
                System.out.println("\n Branching Start \n");
                for (SopNode br : n.getBranches()){
                    printSeq(br);
                }
                System.out.println("\n Branching Stop \n");
                printSeq(n.getPred());
            }
        } else System.out.println("\n Null reached \n");

    }





    public SopNode createSopNode(OperationData d){
        return new SopNode(d);
    }

    private SopNode createSopNodeAltBranch(LinkedList<OperationData> pred){
        ArrayList<SopNode> orNodes = new ArrayList<SopNode>();
        if (pred != null && pred.size() > 1){
            for (OperationData d : pred){
                orNodes.add(createSopNode(d));
            }
        } else return null;
        
        return new SopNode(SopNode.ALTERNATIVE, orNodes);
    }


    private SopNode createSopNodeParallelBranch(LinkedList<LinkedList<OperationData>> pred){
        ArrayList<SopNode> andNodes = new ArrayList<SopNode>();
        if (pred != null && pred.size() > 1){
            for (LinkedList<OperationData> ld : pred){
                if (ld.size() == 1){
                    andNodes.add(this.createSopNode(ld.getFirst()));
                } else if (ld.size() > 1){
                    andNodes.add(this.createSopNodeAltBranch(ld));
                } 
            }
        } else return null;

        return new SopNode(SopNode.PARALLEL, andNodes);
    }

}
