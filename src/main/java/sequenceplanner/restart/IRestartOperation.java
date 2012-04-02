package sequenceplanner.restart;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author patrik
 */
public interface IRestartOperation {

    enum Property {

        NAME,
        HAS_TO_FINISH,
        EXCLUDE_MAP, CONTAINS, NOT_CONTAINS, NOT_ONLY_CONTAINS,
        EXTRA_START_GUARD, EXTRA_START_ACTION, EXTRA_FINISH_GUARD, EXTRA_FINISH_ACTION;
    }

    String getName();

    boolean hasToFinish();

    Map<Property, Set<Set<IRestartOperation>>> getExcludeMap();

    String getExtraStartGuard();

    String getExtraStartAction();

    String getExtraFinishGuard();

    String getExtraFinishAction();
}
