package sequenceplanner.IO.EFA;

import java.util.Set;

/**
 * To be used in {@link ModuleBase}.<br/>
 * @author patrik
 */
public interface IVariable {

    String getVarLabel();

    String getVarLowerBound();

    String getVarUpperBound();

    String getVarInitValue();

    Set<String> getVarMarkedValues();
}
