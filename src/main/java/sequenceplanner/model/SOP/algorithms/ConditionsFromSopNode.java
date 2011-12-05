package sequenceplanner.model.SOP.algorithms;

import sequenceplanner.model.SOP.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionOperator;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.model.data.OperationData;

/**
 * Creates conditions for {@link OperationData}s based on {@link SopNode} parameter used at constructor call.<br/>
 * The result is to be found with method getmOperationConditionMap().<br/>
 * @author patrik
 */
public class ConditionsFromSopNode {

    public enum ConditionType {

        PRE("pre"),
        POST("post");
        private final String mType;

        ConditionType(String iType) {
            this.mType = iType;
        }

        @Override
        public String toString() {
            return mType;
        }
    };
    private final ISopNodeToolbox mSopNodeToolbox = new SopNodeToolboxSetOfOperations();
    private final HashMap<OperationData, Map<ConditionType, Condition>> mOperationConditionMap = new HashMap<OperationData, Map<ConditionType, Condition>>(); //{pre,post}

    public ConditionsFromSopNode(final SopNode iRoot) {
        run(iRoot);
    }

    /**
     * External key: operation object<br/>
     * External value: internal map<br/>
     * Internal key: {@link ConditionsFromSopNode}.ConditionType.PRE/POST<br/>
     * Internal value: {@link Condition}
     * @return
     */
    public Map<OperationData, Map<ConditionType, Condition>> getmOperationConditionMap() {
        return mOperationConditionMap;
    }

    public boolean run(final SopNode iRoot) {
        if (!loopNode(iRoot)) {
            return false;
        }
        return true;
    }

    /**
     * Each {@link SopNode} in each sequence in the iRoot parameter is examined.<br/>
     * Conditions are added based on node type.<br/>
     * Children to each node are called recursively.<br/>
     * Conditions to possible successor node are added.<br/>
     * @param iRoot node whos child nodes should be examined
     * @return true if ok else false
     */
    private boolean loopNode(final SopNode iRoot) {
        for (SopNode node : iRoot.getFirstNodesInSequencesAsSet()) {

            //Successor(s)-------------------------------------------------------
            while (node != null) {

                //Add condition based on node type-------------------------------
                if (!nodeTypeToCondition(node)) {
                    return false;
                }
                //---------------------------------------------------------------

                //Go through children--------------------------------------------
                if (!loopNode(node)) {
                    return false;
                }
                //---------------------------------------------------------------

                final SopNode successorNode = node.getSuccessorNode();
                if (successorNode != null) {

                    //Add condition from node to successor node------------------
                    //Get condition for when node is finished.
                    //E.g. operation case: node has type operation -> condition == operations has to be finished.
                    //E.g. alternative case: node has type alternative -> condition == disjuction between last operation in each sequence for node.
                    final ConditionExpression condition = new ConditionExpression();
                    if (!getFinishConditionForNode(node, condition)) {
                        return false;
                    }

                    //To capture e.g. a parallel node without any child nodes.
                    try {
                        condition.clone();
                    } catch (NullPointerException e) {
                        System.out.println("Graph is not complete. No conditions are calculated!");
                        return false;
                    }

                    //Get the set of operations that occurs first in successor node.
                    //E.g. operation case: successor node has type operation -> operation set == the operation itself.
                    //E.g. alternative case: successor node has type alternative -> operation set == the first operation in each sequence for successor node.
                    final Set<OperationData> operationSet = new HashSet<OperationData>();
                    if (!findFirstOperationsForNode(successorNode, operationSet)) {
                        return false;
                    }

                    //Add condition to operation in set
                    for (final OperationData opData : operationSet) {
                        final ConditionExpression ce = (ConditionExpression) condition.clone();
                        andToOperationConditionMap(opData, ConditionType.PRE, ce);
                    }
                    //-----------------------------------------------------------
                }

                //Update for next round
                node = successorNode;
            }//------------------------------------------------------------------
        }
        return true;
    }

    /**
     * Add conditions to operations if parameter iNode has node type:<br/>
     * operation SOP, alternative, or arbitrary order.<br/>
     * No conditions are added if any operation is parameter iNode has node type:<br/>
     * operation, parallel, or SOP.<br/>
     * @param iNode to look at
     * @return true if ok else false
     */
    private boolean nodeTypeToCondition(final SopNode iNode) {

        if (iNode instanceof SopNodeOperation) {
            if (iNode.sequenceSetIsEmpty()) {
                //do nothing
            } else { //node is operation with child operations
                final SopNode parentNode = iNode; //is an operation
                final OperationData parentOperation = iNode.getOperation();

                //Precondition for child operations
                for (final SopNode childNode : mSopNodeToolbox.getNodes(parentNode, true)) { //Take all child operations, not only the first ones
                    if (childNode instanceof SopNodeOperation) {
                        final OperationData childOperation = childNode.getOperation();
                        //set relation between parent and child operations
//                        andToOperationConditionMap(parentOperation, ConditionType.PRE, childOperation, "0");
//                        andToOperationConditionMap(parentOperation, ConditionType.POST, childOperation, "2");
                        andToOperationConditionMap(childOperation, ConditionType.PRE, parentOperation, "1");
//                        andToOperationConditionMap(childOperation, ConditionType.POST, parentOperation, "1");
                    }
                }

                //Postcondition for parent operation
                final ConditionExpression postCondition = new ConditionExpression();
                for (final SopNode node : parentNode.getFirstNodesInSequencesAsSet()) {
                    //Get condition for when sequence that starts with node is finished
                    final SopNode lastNode = mSopNodeToolbox.getBottomSuccessor(node);
                    final ConditionExpression finishCondition = new ConditionExpression();
                    getFinishConditionForNode(lastNode, finishCondition);
                    if (postCondition.isEmpty()) {
                        postCondition.changeExpressionRoot(finishCondition);
                    } else {
                        postCondition.appendElement(ConditionOperator.Type.AND, finishCondition);
                    }
                }
                andToOperationConditionMap(parentOperation, ConditionType.POST, postCondition);
            }
        } else if (iNode instanceof SopNodeAlternative) {
            //find operations that are first in each sequence.
            final Map<SopNode, Set<OperationData>> nodeOperationSetMap = new HashMap<SopNode, Set<OperationData>>();
            for (final SopNode node : iNode.getFirstNodesInSequencesAsSet()) {
                final Set<OperationData> operationSet = new HashSet<OperationData>();
                findFirstOperationsForNode(node, operationSet);
                nodeOperationSetMap.put(node, operationSet);
            }
            //add condition
            for (final SopNode altNode : iNode.getFirstNodesInSequencesAsSet()) {
                final Set<SopNode> nodesInAlternativeSet = iNode.getFirstNodesInSequencesAsSet();

                for (final SopNode otherNode : nodesInAlternativeSet) {
                    for (final OperationData altOperation : nodeOperationSetMap.get(altNode)) {
                        for (final OperationData otherOperation : nodeOperationSetMap.get(otherNode)) {
                            //add precondition to altOperation that otherOperation has to be _i
                            if (!otherNode.equals(altNode)) {
                                andToOperationConditionMap(altOperation, ConditionType.PRE, otherOperation, "0");
                            }
                        }
                    }
                }
            }
        } else if (iNode instanceof SopNodeArbitrary) {
            //find conditions for each sequence in iNode when it is initial or finished
            final Map<SopNode, ConditionExpression> sequenceConditionMap = new HashMap<SopNode, ConditionExpression>();

            for (final SopNode node : iNode.getFirstNodesInSequencesAsSet()) {

                final ConditionExpression startCondition = new ConditionExpression();

                //Get startCondition for sequence that starts with node to be in it's initial location.
                final Set<OperationData> firstOperationSet = new HashSet<OperationData>();
                findFirstOperationsForNode(node, firstOperationSet);
                for (final OperationData opData : firstOperationSet) {

                    ConditionStatement cs = createConditionStatment(opData, "0");
                    if (startCondition.isEmpty()) {
                        startCondition.changeExpressionRoot(cs);
                    } else {
                        startCondition.appendElement(ConditionOperator.Type.AND, cs);
                    }

                }

                //Get condition for when sequence that starts with node is finished
                final SopNode lastNode = mSopNodeToolbox.getBottomSuccessor(node);
                final ConditionExpression finishCondition = new ConditionExpression();
                getFinishConditionForNode(lastNode, finishCondition);

                //Merge the two conditions
                final ConditionExpression mergedCondition = new ConditionExpression(startCondition);
                mergedCondition.appendElement(ConditionOperator.Type.OR, finishCondition);

                //Store for later use
                sequenceConditionMap.put(node, mergedCondition);
            }
            //add condition
            for (final SopNode thisSequenceNode : iNode.getFirstNodesInSequencesAsSet()) {

                final Set<OperationData> firstOperationSet = new HashSet<OperationData>();
                findFirstOperationsForNode(thisSequenceNode, firstOperationSet);
                for (final OperationData opData : firstOperationSet) {
                    for (final SopNode otherSequenceNode : sequenceConditionMap.keySet()) {
                        if (otherSequenceNode != thisSequenceNode) {
                            andToOperationConditionMap(opData, ConditionType.PRE, sequenceConditionMap.get(otherSequenceNode));
                        }
                    }
                }
            }
        } else if (iNode instanceof SopNodeParallel) {
            //do nothing
        } else if (iNode instanceof SopNodeEmpty) {
            //do nothing
        } else {
            System.out.println("nodeTypeToCondition node type found is that known");
            //return false;
        }

        return true;
    }

    /**
     * Adds first operations in parameter iNode to parameter returnSet.<br/>
     * If iNode has node type operation this single operation is added to returnSet.<br/>
     * Else if iNode has node type parallel, alternative, or arbitrary order,<br/>
     * then this method is called recursively for all first nodes in the sequence set.<br/>
     * @param iNode
     * @param returnSet
     * @return true if ok else false
     */
    private boolean findFirstOperationsForNode(final SopNode iNode, Set<OperationData> returnSet) {

        if (iNode instanceof SopNodeOperation) {
            returnSet.add(iNode.getOperation());
        } else if (iNode instanceof SopNodeEmpty || iNode instanceof SopNodeAlternative || iNode instanceof SopNodeArbitrary || iNode instanceof SopNodeParallel) {
            for (final SopNode node : iNode.getFirstNodesInSequencesAsSet()) {
                if (!findFirstOperationsForNode(node, returnSet)) {
                    return false;
                }
            }
        } else {
            System.out.println("findFirstOperationsForNode Node type is not known");
            return false;
        }
        return true;
    }

    /**
     * If iNode has node type operation then parameter returnCondition == the iNode operation has to finish.<br/>
     * Else returnCondition == X of recursive calls to this method with the first node in sequence set for iNode as parameter.<br/>
     * Where X == conjunction if iNode has node type parallel or arbitrary order.<br/>
     * Where X == disjunction if iNode has node type alternative.<br/>
     * @param iNode
     * @param returnCondition
     * @return true if ok else false
     */
    private boolean getFinishConditionForNode(final SopNode iNode, final ConditionExpression returnCondition) {

        if (iNode instanceof SopNodeOperation) {
            final OperationData opData = iNode.getOperation();
            final ConditionStatement cs = createConditionStatment(opData, "2");
            returnCondition.changeExpressionRoot(cs);

        } else if (iNode instanceof SopNodeEmpty || iNode instanceof SopNodeAlternative || iNode instanceof SopNodeArbitrary || iNode instanceof SopNodeParallel) {

            for (final SopNode node : iNode.getFirstNodesInSequencesAsSet()) {
                final SopNode lastNode = mSopNodeToolbox.getBottomSuccessor(node);
                final ConditionExpression localReturnCondition = new ConditionExpression();
                getFinishConditionForNode(lastNode, localReturnCondition);

                if (returnCondition.isEmpty()) {
                    returnCondition.changeExpressionRoot(localReturnCondition);
                } else {
                    if (iNode instanceof SopNodeEmpty || iNode instanceof SopNodeArbitrary || iNode instanceof SopNodeParallel) {
                        returnCondition.appendElement(ConditionOperator.Type.AND, localReturnCondition);
                    } else {// iNode instanceof SopNodeAlternative
                        returnCondition.appendElement(ConditionOperator.Type.OR, localReturnCondition);
                    }
                }
            }
        } else {
            System.out.println("getFinishConditionForNode Node type is not known");
            return false;
        }
        return true;
    }

    private ConditionStatement createConditionStatment(final OperationData iOperation, final String iValue) {
        return new ConditionStatement("id" + Integer.toString(iOperation.getId()), ConditionStatement.Operator.Equal, iValue);
    }

    private void andToOperationConditionMap(final OperationData iAddToOperation, final ConditionType iConditionType, final OperationData iOperationAsElement, final String iValue) {
        final ConditionStatement cs = createConditionStatment(iOperationAsElement, iValue);
        andToOperationConditionMap(iAddToOperation, iConditionType, cs);
    }

    private void andToOperationConditionMap(final OperationData iAddToOperation, final ConditionType iConditionType, final ConditionElement iConditionExpression) {

        //First time for operation?
        if (!mOperationConditionMap.containsKey(iAddToOperation)) {
            mOperationConditionMap.put(iAddToOperation, new HashMap<ConditionType, Condition>());
        }
        final Map<ConditionType, Condition> typeConditionMap = mOperationConditionMap.get(iAddToOperation);

        //First time for condition type?
        if (!typeConditionMap.containsKey(iConditionType)) {
            typeConditionMap.put(iConditionType, new Condition());
        }

        final Condition condition = typeConditionMap.get(iConditionType);

        final ConditionExpression ce = condition.getGuard();

        if (ce.isEmpty()) {
            ce.changeExpressionRoot(iConditionExpression);
        } else {
            ce.appendElement(ConditionOperator.Type.AND, iConditionExpression);
        }

    }

    public void printOperationsWithConditions() {
        for (final OperationData opData : mOperationConditionMap.keySet()) {
            final Map<ConditionType, Condition> typeConditionMap = mOperationConditionMap.get(opData);

            String s = "";
            s += opData.getName() + " ";
            int length = s.length();

            s += String.format("%1$#" + 6 + "s", "pre: ");
            if (typeConditionMap.containsKey(ConditionType.PRE)) {
                final String preCond = typeConditionMap.get(ConditionType.PRE).getGuard().toString();
                s += preCond;
            }

            if (typeConditionMap.containsKey(ConditionType.POST)) {
                s += "\n";
                length += 6;
                s += String.format("%1$#" + length + "s", "post: ");
                final String postCond = typeConditionMap.get(ConditionType.POST).getGuard().toString();
                s += postCond;
            }

            System.out.println("ConditionsFromSopNode " + s);
        }
    }
}
