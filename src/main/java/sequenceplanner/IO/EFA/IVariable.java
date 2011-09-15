package sequenceplanner.IO.EFA;

import java.util.Set;

/**
 *
 * @author patrik
 */
public interface IVariable {

    String getVarLabel();

    String getVarLowerBound();

    String getVarUpperBound();

    String getVarInitValue();

    Set<String> getVarMarkedValues();
}
