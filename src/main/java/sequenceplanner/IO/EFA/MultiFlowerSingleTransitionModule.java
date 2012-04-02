package sequenceplanner.IO.EFA;

import java.util.Map;

/**
 * Extension to Supremica interface.<br/>
 * A {@link Map} of {@link ModuleBase} objects is given as parameter in constructor.<br/>
 * Each {@link ModuleBase} value in the map is translated into a flower.<br/>
 * Each flower gets the key as EFA name.<br/>
 * The union of all variables are added to the module.<br/>
 * @author patrik
 */
public class MultiFlowerSingleTransitionModule extends AModule {

    public MultiFlowerSingleTransitionModule(String iModuleName, String iModuleComment, final Map<String, ModuleBase> iModuleBaseMap) {
        super(iModuleName, iModuleComment);

        for (final String mbKey : iModuleBaseMap.keySet()) {
            final ModuleBase mb = iModuleBaseMap.get(mbKey);
            final SEFA mEFA = new SEFA(mbKey, getAvocadesModule());
            mEFA.addState(SEFA.SINGLE_LOCATION_NAME, true, true);

            for (final IVariable var : mb.getVariableSet()) {
                addIntVariable(var);
            }

            for (final Transition trans : mb.getTransitionSet()) {
                mEFA.addStandardSelfLoopTransition(trans);
            }
        }
    }
}
