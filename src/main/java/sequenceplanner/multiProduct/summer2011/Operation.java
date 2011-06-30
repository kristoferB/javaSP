package sequenceplanner.multiProduct.summer2011;

import java.util.Set;
import java.util.regex.Matcher;
import sequenceplanner.condition.ConditionElement;
import sequenceplanner.condition.ConditionStatement;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
class Operation extends AOperation{

    Matcher matcher;

    public Operation(OperationData iOperationData) {
        super(iOperationData);
    }

    @Override
    Set<AOperation> getPredecessors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    ConditionElement inOperation() {
        if(mResourceSet.size()==1) {

        }
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
