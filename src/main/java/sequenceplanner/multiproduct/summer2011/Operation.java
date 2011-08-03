package sequenceplanner.multiproduct.summer2011;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionElement;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionStatement;
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

    @Override
    Set<String> getPredecessors() {
        final Set<String> returnSet = new HashSet<String>();
        final Map<ConditionData, Map<ConditionType, Condition>> map = mOperationData.getConditions();

        for (final ConditionData cd : map.keySet()) {
            for (final ConditionType ct : map.get(cd).keySet()) {
//                System.out.println(map.get(cd));
                if (ct.toString().equals(ConditionType.PRE.toString())) {
                    final Condition condition = map.get(cd).get(ct);
                    if (condition.hasGuard()) {
                        final ConditionExpression guard = condition.getGuard();
                        final List<ConditionElement> ceList = guard.getConditionElements();
                        for (final ConditionElement ce : ceList) {
                            if (ce.isStatment()) {
                                final ConditionStatement cs = (ConditionStatement) ce;
                                if (cs.getValue().equals("2") && cs.getOperator().toString().equals(ConditionStatement.Operator.Equal.toString())) {
                                    final String variable = cs.getVariable().replaceAll("id", "");
                                    returnSet.add(variable);
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnSet;
    }

    @Override
    ConditionElement inOperation() {
        if (mResourceSet.size() == 1) {
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
