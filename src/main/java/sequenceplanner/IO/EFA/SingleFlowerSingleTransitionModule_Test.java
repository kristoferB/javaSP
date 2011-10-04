package sequenceplanner.IO.EFA;

import org.supremica.automata.Automaton;

/**
 * Extension to Supremica interface.<br/>
 * A {@link ModuleBase} object is given as parameter in constructor, this object in translated into a single flower and a set of variables.<br/>
 * Output:<br/>
 * 1. A .wmod file of the module.<br/>
 * 2. Guards extracted from BDD synthesis as System.out.print.<br/>
 * @author patrik
 */
public class SingleFlowerSingleTransitionModule_Test extends AModule {

    private final SEFA mSingleEFA;

    public SingleFlowerSingleTransitionModule_Test(String iModuleName, String iModuleComment, ModuleBase iModuleBase, ModuleBase iModuleBaseResources, String iFilePath) {
        super(iModuleName, iModuleComment);

        mSingleEFA = new SEFA("PetterFalkman", getAvocadesModule());
        mSingleEFA.addState(SEFA.SINGLE_LOCATION_NAME, true, true);

        for (final IVariable var : iModuleBase.getVariableSet()) {
            addIntVariable(var);
        }

        for (final Transition trans : iModuleBase.getTransitionSet()) {
            mSingleEFA.addStandardSelfLoopTransition(trans);
        }

        saveToWMODFile(iFilePath);

        final Automaton automaton = getMonolithicSupervisor(getDFA());

        final ModuleBase moduleBaseFromAutomaton = new ModuleBase();
        translateAutomatonToModuleBase(automaton, moduleBaseFromAutomaton, "product1");

        final ModuleBase moduleBaseFromAutomatonWithVariables = new ModuleBase();
        //Add product variables to module base
        for (final IVariable var : moduleBaseFromAutomaton.getVariableSet()) {
            moduleBaseFromAutomatonWithVariables.storeVariable(var);
        }
        //Add resource variables to module base
        for(final IVariable var : iModuleBaseResources.getVariableSet()) {
            moduleBaseFromAutomatonWithVariables.storeVariable(var);
        }
        //Add resource guard and action to module base
        for (final Transition trans : moduleBaseFromAutomaton.getTransitionSet()) {
            final Transition transCopy = getTransitionCopy(trans.getLabel(),iModuleBaseResources);
            transCopy.setStartLocation(trans.getStartLocation());
            transCopy.setFinishLocation(trans.getFinishLocation());
            moduleBaseFromAutomatonWithVariables.getTransitionSet().add(transCopy);
        }

        new SingleFlowerSingleTransitionModule("TestAutomatonToModuleBase", iModuleComment, moduleBaseFromAutomatonWithVariables, iFilePath);

    }

    private static Transition getTransitionCopy(final String iTransLabel, final ModuleBase iModuleBase) {
        for (final Transition trans : iModuleBase.getTransitionSet()) {
            if (trans.getLabel().equals(iTransLabel)) {
                return trans.copy();
            }
        }
        return null;
    }
}
