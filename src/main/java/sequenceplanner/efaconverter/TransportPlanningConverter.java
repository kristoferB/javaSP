package sequenceplanner.efaconverter;

/**
 *
 * @author Patrik Magnusson
 */
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.EGA;
import org.supremica.external.avocades.common.Module;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceVariableData;

public class TransportPlanningConverter {

    //-----------------------------------------------------------------
    // Static variables.
    //-----------------------------------------------------------------
    /**
     * The module that include all EFAs.
     */
    static Module module;
    /**
     * The Sequence Planner model given by the user to the constructor
     */
    static Model model;
    /**
     * The name of the project
     */
    static String projectName;
    /**
     * The EFA used in methods
     */
    static EFA efa;
    /**
     * The minPosition
     */
    static int minPos;
    /**
     * The maxPosition
     */
    static int maxPos;
    /**
     * Logger for handling errors
     */
    static Logger log = Logger.getLogger(TransportPlanningConverter.class);
    //Names
    final static String SEPARATION = "_";
    final static String POS = "Pos";
    final static String LIMIT = "Limit";
    final static String COUNTER = "Count" + SEPARATION;
    final static String READY_TO_MOVE = "Moveable" + SEPARATION;
    final static String READY_TO_PROCESS = "Processable" + SEPARATION;
    final static String YES = "1";
    final static String NO = "0";
    final static String TRANSPORT = "t";
    final static String LOCATION = "S0";

    //-----------------------------------------------------------------
    // Constructor.
    //-----------------------------------------------------------------
    /**
     *
     * @throws Exception
     */
    public TransportPlanningConverter(Model m) throws Exception {
        model = m;
        projectName = "Project";
        module = new Module(projectName, false);
        efa = new EFA("product", module);
        minPos = 1;
        maxPos = 1;

        //Create variables
        createVariables(model.getResourceRoot());

        //createOperations(model.getOperationRoot());
        createSingleLocationEFA();
        createTransportTransitions();
        createProcessTransitions(model.getOperationRoot());


    }

    //-----------------------------------------------------------------
    // Return methods.
    //-----------------------------------------------------------------
    /**
     * Return the module with EFAs. The EFAs looks different depending on the options
     * given to the constructor.
     *
     * @return a Module with all EFAs
     */
    public Module getModule() {
        return module;
    }

    //-----------------------------------------------------------------
    // Methods for calculation.
    //-----------------------------------------------------------------
    protected void createVariables(TreeNode resource) {

        for (int i = 0; i < resource.getChildCount(); ++i) {
            TreeNode subObject = (TreeNode) resource.getChildAt(i);

            if (Model.isResource(subObject.getNodeData())) {
                //is resource
                createVariables(subObject);
            } else if (Model.isVariable(subObject.getNodeData())) {
                //is variable
                String varName = subObject.getNodeData().getName();
                int minValue = ((ResourceVariableData) (subObject.getNodeData())).getMin();
                int maxValue = ((ResourceVariableData) (subObject.getNodeData())).getMax();
                maxPos = maxValue;
                int initValue = ((ResourceVariableData) (subObject.getNodeData())).getInitialValue();

                //Positions
                EFA variableEFA = new EFA(POS, module);
                variableEFA.addIntegerVariable(POS, minValue, maxValue, initValue, null);

                //Limit
                variableEFA = new EFA(LIMIT, module);
                variableEFA.addIntegerVariable(LIMIT, 0, 1, 0, null);

                //create additional variables
                for (int pos = minValue; pos <= maxValue; ++pos) {
                    //Counter
                    varName = COUNTER + pos;
                    variableEFA = new EFA(varName, module);
                    variableEFA.addIntegerVariable(varName, 0, 3, 0, null);
                    //Movable
                    varName = READY_TO_MOVE + pos;
                    variableEFA = new EFA(varName, module);
                    variableEFA.addIntegerVariable(varName, 0, 1, 0, null);
                    //Processable
                    varName = READY_TO_PROCESS + pos;
                    variableEFA = new EFA(varName, module);
                    if (pos == 0) {
                        variableEFA.addIntegerVariable(varName, 0, 1, 1, null);
                    } else {
                        variableEFA.addIntegerVariable(varName, 0, 1, 0, null);
                    }
                }

            }

        }
    }

    protected void createSingleLocationEFA() {

        module.addAutomaton(efa);

        // Add the single state
        efa.addState(LOCATION, true, true);
    }

    protected void createTransportTransitions() {

        for (int sourcePos = minPos; sourcePos <= maxPos; ++sourcePos) {
            for (int destPos = minPos; destPos <= maxPos; ++destPos) {
                if (sourcePos != destPos) {
                    EGA ega = new EGA();
                    ega.andGuard(POS + EFAVariables.EFA_EQUAL + sourcePos);
                    ega.andGuard(READY_TO_MOVE + sourcePos + EFAVariables.EFA_EQUAL + YES);
                    ega.addAction(POS + EFAVariables.EFA_SET + destPos);
                    ega.addAction(READY_TO_MOVE + sourcePos + EFAVariables.EFA_SET + NO);
                    ega.addAction(READY_TO_PROCESS + destPos + EFAVariables.EFA_SET + YES);
                    efa.addTransition(LOCATION, LOCATION, sourcePos + TRANSPORT + destPos, ega.getGuard(), ega.getAction());
                }
            }
        }
    }

    protected void createProcessTransitions(TreeNode operation) {

        //to map op ID and source pos
        HashMap<String, String> opIDPosMap = new HashMap<String, String>(8);
        for (int i = 0; i < operation.getChildCount(); ++i) {
            OperationData opData = (OperationData) ((TreeNode) operation.getChildAt(i)).getNodeData();
            opIDPosMap.put(Integer.toString(opData.getId()), Double.toString(opData.getCost()).substring(0, 1));
        }

        for (int i = 0; i < operation.getChildCount(); ++i) {
            TreeNode op = (TreeNode) operation.getChildAt(i);
            OperationData opData = (OperationData) op.getNodeData();
            String opName = opData.getName();
            String prePos = Double.toString(opData.getCost()).substring(0, 1);
            String postPos = Double.toString(opData.getCost()).substring(2, 3);

            EGA ega = new EGA();
            ega.andGuard(POS + EFAVariables.EFA_EQUAL + prePos);
            ega.andGuard(READY_TO_PROCESS + prePos + EFAVariables.EFA_EQUAL + YES);
            ega.andGuard(READY_TO_MOVE + prePos + EFAVariables.EFA_EQUAL + NO);

            ega.addAction(READY_TO_PROCESS + prePos + EFAVariables.EFA_SET + NO);
            ega.addAction(COUNTER + prePos + EFAVariables.EFA_PLUS_ONE);
            ega.addAction(POS + EFAVariables.EFA_SET + postPos);
            ega.addAction(READY_TO_MOVE + postPos + EFAVariables.EFA_SET + YES);

            if (prePos.equals("0")) {
                ega.andGuard(LIMIT + EFAVariables.EFA_STRICTLY_LESS_THAN + 1);
                ega.addAction(LIMIT + EFAVariables.EFA_PLUS_ONE);
            }

//            if (postPos.equals("0")) {
//                ega.addAction(LIMIT + EFAVariables.EFA_MINUS_ONE);
//            }

            if (opData.getRawPrecondition().isEmpty()) {
                log.info(opName + " has no preconditions to other operations.");
            } else if (!opData.getRawPrecondition().contains(EFAVariables.SP_OR)) {
                HashMap<String, Integer> posCountMap = new HashMap<String, Integer>(8);
                String[] terms = opData.getRawPrecondition().replaceAll(" ", "").replaceAll("_", "").replaceAll("f", "").split(EFAVariables.SP_AND);

                log.info("Precon for " + opName + " " +opData.getRawPrecondition().replaceAll(" ", "").replaceAll("_", "").replaceAll("f", ""));
                //create position hisogram for precondition
                for (int id = 0; id < terms.length; ++id) {
                    int count = 1;
                    if (posCountMap.containsKey(opIDPosMap.get(terms[id]))) {
                        count += posCountMap.get(opIDPosMap.get(terms[id]));
                    }
                    log.info("Compare " + posCountMap.containsKey(opIDPosMap.get(terms[id])) + "|Count "+ count);
                    posCountMap.put(opIDPosMap.get(terms[id]), count);
                }

                Iterator<String> keyIt = posCountMap.keySet().iterator();
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    ega.andGuard(COUNTER + key + EFAVariables.EFA_EQUAL + posCountMap.get(key));
                    log.info(opName + " hash " + key + EFAVariables.EFA_EQUAL + posCountMap.get(key));
                }
            }
            else {
                log.error("Implentation does not support disjunction! Operation preconditions are not correct!");
            }

            efa.addTransition(LOCATION, LOCATION, opName, ega.getGuard(), ega.getAction());

        }
    }
}


