package sequenceplanner.multiproduct.InfoInResources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionStatement;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.algorithms.ConditionsFromSopNode.ConditionType;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ViewData;

/**
*
* @author patrik
*/
public class SupervisorFromOperationsBasedOnSingleTransition extends ASupervisorFromOperationsBasedOnSingleTransition{

    private final String productTypePattern = "(P\\d{1})";
    private final String resourcePattern = "(r[A-Z]\\d{1})";

    public SupervisorFromOperationsBasedOnSingleTransition(Model iModel) {
        super(iModel);
    }

    @Override
    String getProductType(OperationData opData) {
        final String opDataId = Integer.toString(opData.getId());
        final Map<ViewData,ISopNode> sopRootSopNodeMap = mModel.getAllSOPs();
        for(final ViewData key : sopRootSopNodeMap.keySet()) {
            for(final OperationData localOpData : new SopNodeToolboxSetOfOperations().getOperations(sopRootSopNodeMap.get(key), true)) {
                final String localOpDataId = Integer.toString(localOpData.getId());
                if(localOpDataId.equals(opDataId)) {
                    return key.getName();
                }
            }
        }
        return null;
    }

    @Override
    public Set<String> getResources(OperationData opData) {
        final Set<String> returnSet = new HashSet<String>();
        final Matcher matcher = Pattern.compile(resourcePattern).matcher(opData.getDescription());
        while(matcher.find()) {
//            System.out.println(matcher.group().substring(1));
            returnSet.add(matcher.group().substring(1));
        }
        return returnSet;
    }

    @Override
    OperationData createLastData(AOperation iOperation) {
        final OperationData opData = iOperation.mOperationData;
        final OperationData newOpData = new OperationData(opData.getName()+"_", Model.newId());

        //Add Precondition
        final ConditionStatement cs = new ConditionStatement(Integer.toString(opData.getId()), ConditionStatement.Operator.Equal, "2");
        final ConditionExpression ce = new ConditionExpression(cs);
        final Condition condition = new Condition();
        condition.setGuard(ce);
        final Map<ConditionType,Condition> map = new HashMap<ConditionType, Condition>();
        map.put(ConditionType.PRE, condition);
        newOpData.setConditions(new ConditionData(opData.getName()+"_"), map);

        return newOpData;
    }




}