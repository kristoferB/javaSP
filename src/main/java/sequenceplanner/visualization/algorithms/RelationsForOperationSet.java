package sequenceplanner.visualization.algorithms;

import java.util.Map;
import java.util.Set;
import net.sourceforge.waters.subject.module.ModuleSubject;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.SOP.ISopNode;
import sequenceplanner.model.SOP.algorithms.SopNodeToolboxSetOfOperations;
import sequenceplanner.model.data.ConditionData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;

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
    private String mWmodPath = "";

    public RelationsForOperationSet(final RelationContainer iRC, final String iWmodPath, final Set<ConditionData> iConditionsToInclude, final Set<ResourceVariableData> resources) {
        setmRC(iRC);
        mWmodPath = iWmodPath;
        formalMethods = new SupremicaInteractionForVisualization(iConditionsToInclude, resources);
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
        final ModuleSubject ms = formalMethods.getModuleSubject(mRC.getOsetSopNode(), mRC.getOfinishsetSopNode());
        if (ms == null) {
            GUIView.printToConsole("Problem with translation from op to efa!");
            System.out.println("Problem with translation from op to efa!");
            return 0;
        }

        //flatten out (EFA->DFA, Module -> Automata)
        final Automata automata = formalMethods.flattenOut(ms);
        if (automata == null) {
            GUIView.printToConsole("Problem with flatten out!");
            System.out.println("Problem with flatten out!");
            return 0;
        }

        System.out.println("start synthesis");

        saveFormalModel(mWmodPath);

        //synthesis
        final Automaton automaton = formalMethods.synthesize(automata);
        if (automaton == null) {
            GUIView.printToConsole("Problem with synthesis!");
            System.out.println("Problem with synthesis!");
            return 0;
        }
        
        saveFormalModel("D:/");

        System.out.println("end synthesis");

        //Check if supervisor exists
        if (automaton.nbrOfStates() == 0) {
            GUIView.printToConsole("No supervisor found :( Specifications are to strict! \n 1) Modifiy conditions \n 2) Modifiy what operations that have to finish");
            System.out.println("No supervisor found :( Specifications are to strict! \n 1) Modifiy conditions \n 2) Modifiy what operations that have to finish");
            return 1;
        }

        System.out.println("Creation of eventStateSpaceMap started");

        //Get states where each event is enabled
        final Map<String, Set<String>> eventStateSpaceMap = formalMethods.getStateSpaceForEventSetMap(automaton);

        System.out.println("Creation of eventStateSpaceMap finished");

        //Relation identification
        final RelationIdentification ri = new RelationIdentification(automaton, mRC, eventStateSpaceMap);
        if (!ri.run()) {
            GUIView.printToConsole("Problem with relation identification!");
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
        Set<OperationData> setToPrint = new SopNodeToolboxSetOfOperations().getOperations(mRC.getOsubsetSopNode(), false);
        for (final OperationData opDataExternal : setToPrint) {
            System.out.println("--------------------------------");
            for (final OperationData opDataInternal : setToPrint) {
                if (opDataExternal.getId() != opDataInternal.getId()) {
                    final String externalOpName = opDataExternal.getName();
                    final String internalOpName = opDataInternal.getName();
                    final int relationInt = mRC.getOperationRelationMap(opDataExternal).get(opDataInternal);

                    System.out.println(externalOpName + " has relation " +
                            RelateTwoOperations.relationIntegerToString(relationInt, " ", " ") +
                            " to " + internalOpName);

                    //Print location sets----------------------------------------
                    if ((relationInt == IRelateTwoOperations.OTHER)) {
                        System.out.print(printLocationSet(externalOpName, "u", internalOpName, mRC.getEventOperationLocationSetMap(opDataExternal).get(ISupremicaInteractionForVisualization.Type.EVENT_UP.toString()).get(opDataInternal)));
                        System.out.print("| ");
                        System.out.print(printLocationSet(externalOpName, "d", internalOpName, mRC.getEventOperationLocationSetMap(opDataExternal).get(ISupremicaInteractionForVisualization.Type.EVENT_DOWN.toString()).get(opDataInternal)));
                        System.out.print("| ");
                        System.out.print(printLocationSet(internalOpName, "u", externalOpName, mRC.getEventOperationLocationSetMap(opDataInternal).get(ISupremicaInteractionForVisualization.Type.EVENT_UP.toString()).get(opDataExternal)));
                        System.out.print("| ");
                        System.out.print(printLocationSet(internalOpName, "d", externalOpName, mRC.getEventOperationLocationSetMap(opDataInternal).get(ISupremicaInteractionForVisualization.Type.EVENT_DOWN.toString()).get(opDataExternal)));
                        System.out.print("\n");
                    }
                    //-----------------------------------------------------------
                }
            }
        }
        System.out.println("--------------------------------");
    }

    private String printLocationSet(final String iOpWithEvent, final String iEvent, final String iOpWithLocations, final Set<String> iLocationSet) {
        String returnString = "";
        returnString += iOpWithEvent + "" + iEvent;
        returnString += " " + iOpWithLocations + ":";
        if (iLocationSet == null) return returnString;
        for (final String s : iLocationSet) {
            returnString += s;
        }
        returnString += " ";
        return returnString;
    }

    public RelationContainer getmRC() {
        return mRC;
    }

    public void setmRC(RelationContainer mRC) {
        this.mRC = mRC;
    }
}
