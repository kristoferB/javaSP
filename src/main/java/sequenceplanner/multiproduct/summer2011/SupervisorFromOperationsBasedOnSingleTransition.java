package sequenceplanner.multiproduct.summer2011;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sequenceplanner.model.Model;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
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


}