package sequenceplanner.multiproduct.InfoInResources;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
class Operation extends AOperation {

    Matcher matcher;

    public Operation(OperationData iOperationData) {
        super(iOperationData);
    }

    Set<String> getPredecessors() {
        final Set<String> returnSet = new HashSet<String>();
        final Map<ConditionData, Map<ConditionType, Condition>> map = mOperationData.getConditions();

        for (final ConditionData cd : map.keySet()) {
            for (final ConditionType ct : map.get(cd).keySet()) {
//                System.out.println(map.get(cd));
                if (ct.toString().equals(ConditionType.PRE.toString())) {
                    final Condition condition = map.get(cd).get(ct);
//                    System.out.println("guard: " + condition.getGuard());
                    if (condition.hasGuard()) {
                        final ConditionExpression guard = condition.getGuard();
                        returnSet.addAll(getOperationsFromCondition(guard));
                    }
                }
            }
        }
        return returnSet;
    }

    private Set<String> getOperationsFromCondition(final ConditionExpression iConditionExpression) {
        final Set<String> returnSet = new HashSet<String>();

        if (iConditionExpression != null) {
            final List<ConditionElement> ceList = iConditionExpression.getConditionElements();
            for (final ConditionElement conditionElement : ceList) {
                if (conditionElement.isStatment()) {
                    final ConditionStatement cs = (ConditionStatement) conditionElement;
                    if (cs.getValue().equals("2") && cs.getOperator().toString().equals(ConditionStatement.Operator.Equal.toString())) {
                        final String variable = cs.getVariable().replaceAll("id", "");
                        returnSet.add(variable);
                    }
                } else if (conditionElement.isExpression()) {
                    final ConditionExpression ce = (ConditionExpression) conditionElement;
                    returnSet.addAll(getOperationsFromCondition(ce));
                }
            }
        }

        return returnSet;
    }


    ConditionElement inOperation() {
        if (mResourceSet.size() == 1) {
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
