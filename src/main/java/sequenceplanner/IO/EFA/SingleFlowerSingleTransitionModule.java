package sequenceplanner.IO.EFA;

/**
 * Extension to Supremica interface.<br/>
 * A {@link ModuleBase} object is given as parameter in constructor, this object in translated into a single flower and a set of variables.<br/>
 * @author patrik
 */
public class SingleFlowerSingleTransitionModule extends AModule {

    private final SEFA mSingleEFA;

    public SingleFlowerSingleTransitionModule(String iModuleName, String iModuleComment, ModuleBase iModuleBase) {
        super(iModuleName, iModuleComment);

        mSingleEFA = new SEFA("MartinFabian", getAvocadesModule());
        mSingleEFA.addState(SEFA.SINGLE_LOCATION_NAME, true, true);

        for (final IVariable var : iModuleBase.getVariableSet()) {
            addIntVariable(var);
        }

        for (final Transition trans : iModuleBase.getTransitionSet()) {
            mSingleEFA.addStandardSelfLoopTransition(trans);
        }
    }
}
