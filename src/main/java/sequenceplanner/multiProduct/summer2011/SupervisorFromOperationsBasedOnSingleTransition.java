package sequenceplanner.multiProduct.summer2011;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.OperationData;

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
        final Matcher matcher = Pattern.compile(productTypePattern).matcher(opData.getName());

        if(matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Override
    Set<String> getResources(OperationData opData) {
        final Set<String> returnSet = new HashSet<String>();
        final Matcher matcher = Pattern.compile(resourcePattern).matcher(opData.getName());
        while(matcher.find()) {
            System.out.println(matcher.group().substring(1));
            returnSet.add(matcher.group().substring(1));
        }
        return returnSet;
    }


}
