package sequenceplanner.algorithms.visualization;

import java.util.Map;
import java.util.Set;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.OperationData;

/**
 * To find relations between a set of operations given in a {@link ISopNode}.<br/>
 * The relations should be found with respect to: <br/>
 * 1) some superset of operations also given as a {@link ISopNode}.<br/>
 * 2) some set \subset superset of operations that has to finish also given as a {@link ISopNode}.<br/>
 * @author patrik
 */
public class RelationsForOperationSet {

    private ISupremicaInteractionForVisualization formalMethods;
    private RelationContainer mRC = null;

    public RelationsForOperationSet(final RelationContainer iRC) {
        setmRC(iRC);
        formalMethods = new SupremicaInteractionForVisualization();
    }

    /**
     *
     * @return 0 = error occurred, 1 = no supervisor exists, 2 = ok
     */
    public int run() {
        if (mRC == null) {
            System.out.println("Problem with Relation Container!");
            return 0;
        }
        //Translate operations to EFA
        ModuleSubject ms = formalMethods.getModuleSubject(mRC.getOsetSopNode(), mRC.getOfinishsetSopNode());
        if (ms == null) {
            System.out.println("Problem with translation from op to efa!");
            return 0;
        }

        //flatten out (EFA->DFA, Module -> Automata)
        Automata automata = formalMethods.flattenOut(ms);
        if (automata == null) {
            System.out.println("Problem with flatten out!");
            return 0;
        }

        System.out.println("start synthesis");

//        saveFormalModel("C:/Users/patrik/Desktop/beforeSynthesis.wmod");

        //synthesis
        Automaton automaton = formalMethods.synthesize(automata);
        if (automaton == null) {
            System.out.println("Problem with synthesis!");
            return 0;
        }

        System.out.println("end synthesis");

        //Check if supervisor exists
        if (automaton.nbrOfStates() == 0) {
            System.out.println("No supervisor found :( Specifications are to strict! \n 1) Modifiy conditions \n 2) Modifiy what operations that have to finish");
            return 1;
        }

        //Get states where each event is enabled
        Map<String, Set<String>> eventStateSpaceMap = formalMethods.getStateSpaceForEventSetMap(automaton);

        //Relation identification
        RelationIdentification ri = new RelationIdentification(automaton, mRC, eventStateSpaceMap);
        if (!ri.run()) {
            System.out.println("Problem with relation identification!");
            return 0;
        }

        printRelations();

        return 2;
    }

    public boolean saveFormalModel(final String iPath) {
        return formalMethods.saveSupervisorAsWmodFile(iPath);
    }

    public void printRelations() {
        Set<OperationData> setToPrint = new SopNodeToolboxSetOfOperations().getOperations(mRC.getOsubsetSopNode(),false);
        for (final OperationData opDataExternal :setToPrint) {
            System.out.println("--------------------------------");
            for (final OperationData  opDataInternal : setToPrint) {
                final String externalOpName = opDataExternal.getName();
                final String internalOpName = opDataInternal.getName();

                System.out.println(externalOpName + " has relation " +
                        RelateTwoOperations.relationIntegerToString(mRC.getOperationRelationMap(opDataExternal).get(opDataInternal), " ", " ") +
                        " to " + internalOpName);
            }
        }
        System.out.println("--------------------------------");
    }

    public RelationContainer getmRC() {
        return mRC;
    }

    public void setmRC(RelationContainer mRC) {
        this.mRC = mRC;
    }


}
