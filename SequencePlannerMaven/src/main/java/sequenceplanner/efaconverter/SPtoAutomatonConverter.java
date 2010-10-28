package sequenceplanner.efaconverter;

/**
 *
 * @author Carl Thorstensson, carl.thorstensson@gmail.com
 */
import java.util.ArrayList;

import java.util.LinkedList;
import net.sourceforge.waters.model.expr.EvalException;
import net.sourceforge.waters.model.marshaller.DocumentManager;
import net.sourceforge.waters.subject.module.ModuleSubject;

import org.apache.log4j.Logger;
import org.supremica.automata.Alphabet;
import org.supremica.automata.Arc;
import org.supremica.automata.Automata;
import org.supremica.automata.Automaton;
import org.supremica.automata.AutomatonType;
import org.supremica.automata.LabeledEvent;
import org.supremica.automata.Project;
import org.supremica.automata.State;
import org.supremica.automata.IO.ProjectBuildFromWaters;
import org.supremica.automata.algorithms.AutomataSynchronizer;
import org.supremica.automata.algorithms.scheduling.ModifiedAstar;
import org.supremica.automata.algorithms.scheduling.Scheduler;
import org.supremica.automata.algorithms.scheduling.SchedulingConstants;
import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.EGA;
import org.supremica.external.avocades.common.Module;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.OperationData.SeqCond;
import sequenceplanner.model.data.ResourceVariableData;

public class SPtoAutomatonConverter {


   //-----------------------------------------------------------------
   // Static variables.
   //-----------------------------------------------------------------
   /**
    * Automata with all cost automatons generated from the operations.
    */
   static Automata costAutomata;
   /**
    * An Automaton that is the synchronization of the DFAs that is created from the EFAs from the operations.
    */
   static Automaton efaAutomaton;
   /**
    * Automata with all DfAs generated from the EFAs from the operations.
    */
   static Automata efaAutomata;
   /**
    * Automata with all the cost automatons generated from the operations and the synchronized automaton from the EFAs.
    */
   static Automata totalAutomata;
   /**
    * The optimal automaton for the model.
    */
   static Automaton optimizedAutomaton;
   /**
    * The module that include all EFAs.
    */
   static Module module;
   /**
    * True if the EFAs are created with reset transitions.
    */
   static boolean includeReset;
   /**
    * True if cost automatons are created.
    */
   static boolean shouldOptimize;
   /**
    * The Sequence Planner model given by the user to the constructor
    */
   static Model model;
   /**
    * The name of the project
    */
   static String projectName;
   /**
    * Logger for handling errors
    */
   static Logger logger = Logger.getLogger(SPtoAutomatonConverter.class);



   //-----------------------------------------------------------------
   // Constructor.
   //-----------------------------------------------------------------
   /**
    * Constructor for the generation of EFAs and TDFAs from Sequence Planner.
    * Includes the option to generate the optimal path through the model.
    * <br><br>
    * The algorithm has five possible options for the user to choose between. Different option makes it possible
    * to get different results from the algorithms. All get-methods will always return something but the return will
    * be empty or wrong if the wrong option is chosen for this constructor.  <br> <br>
    *
    * All calculations are ordered from the constructor depending on the chosen option.
    * The user only has to use the get-methods shown under each option below to get the
    * output after the constructor is initialized.
    * <br> <br>
    * <b>- The options -</b> <br> <br>
    * <b><i>Option ONE: For optimization</i></b><br>
    * EFAs, <i>getModule()</i>  <br>Cost automata, <i>getCostAutomata()</i><br> <br>
    *
    * <b><i>Option TWO: For reset</i></b><br>
    * EFAs, <i>getModule()</i> <br> <br>
    *
    * <b><i>Option THREE: For optimization</i></b> <br>
    * EFAs, <i>getModule()</i> <br>DFAs from the EFA, <i>getEFAautomata()</i>
    * <br>Synchronized DFA from EFA, <i>getSynchronizedEFAautomaton</i>
    * <br>Cost automata, <i>getCostAutomata()</i>
    * <br>Total automata, <i>getTotalAutomata()</i><br> <br>
    *
    * <b><i>Option FOUR: For reset</i></b><br>
    * EFAs, <i>getModule()</i> <br>DFAs from the EFA, <i>getEFAautomata()</i>
    * <br>Synchronized DFA from EFA <i>getSynchronizedEFAautomaton</i> <br><br>
    *
    * <b><i>Option FIVE: For optimization</i></b><br>
    * EFAs, <i>getModule()</i> <br>DFAs from the EFA, <i>getEFAautomata()</i>
    * <br>Synchronized DFA from EFA, <i>getSynchronizedEFAautomaton</i> <br>Cost automata, <i>getCostAutomata()</i>
    * <br>Total automata, <i>getTotalAutomata()</i> <br>Optimized automaton, <i>getOptimizedAutomaton()</i>
    * <br>Total cycle time, <i>getTotalCycleTime()</i> <br> <br>
    * <br>
    * @param m <Model> the Sequence Planner model to perform calculations on.
    * @param option <Integer> the option chosen by the user, 1, 2, 3, 4 or 5.
    * If an other options is chosen nothing will happen and all returns will be meaningsless.
    * @throws Exception
    */
   public SPtoAutomatonConverter(Model m, int option) throws Exception {

      model = m;
      projectName = "Project";
      module = new Module(projectName, false);

      costAutomata = new Automata();
      costAutomata.setName("Cost Automata for " + projectName);

      efaAutomata = new Automata();
      efaAutomata.setName("EFA automata for " + projectName);

      efaAutomaton = new Automaton("Synchronized EFA automaton for " + projectName);
      efaAutomaton.setType(AutomatonType.SPECIFICATION);

      totalAutomata = new Automata();
      totalAutomata.setName("Total automata for " + projectName);


      if (option == EFAVariables.OPTION_ONE) {
         // For optimization:
         // Creates EFAs (module) and the cost automata.
         includeReset = false;
         shouldOptimize = true;

         // EFAs
         initiateResources(model.getResourceRoot());
         createOperations(model.getOperationRoot());


      } else if (option == EFAVariables.OPTION_TWO) {
         // For reset:
         // Creates EFAs (module).
         includeReset = true;
         shouldOptimize = false;

         // EfAs
         initiateResources(model.getResourceRoot());
         createOperations(model.getOperationRoot());


      } else if (option == EFAVariables.OPTION_THREE) {
         // For optimization:
         // Creates EFAs (module), DFAs from the EFA (automata), synchronized DFA from EFA (automaton),
         // cost automata and total automata.
         includeReset = false;
         shouldOptimize = true;

         // EFAs
         initiateResources(model.getResourceRoot());
         createOperations(model.getOperationRoot());

         moduleToDFA();
         efaAutomaton = AutomataSynchronizer.synchronizeAutomata(efaAutomata);

         totalAutomata.addAutomata(costAutomata);
         totalAutomata.addAutomaton(efaAutomaton);


      } else if (option == EFAVariables.OPTION_FOUR) {
         // For reset:
         // Create EFAs (module), DFAs from the EFA (automata) and synchronized DFA from EFA (automaton).
         includeReset = true;
         shouldOptimize = false;

         // EFAs
         initiateResources(model.getResourceRoot());
         createOperations(model.getOperationRoot());

         moduleToDFA();
         efaAutomaton = AutomataSynchronizer.synchronizeAutomata(efaAutomata);


      } else if (option == EFAVariables.OPTION_FIVE) {
         // For optimization:
         // Creates EFAs (module), DFAs from the EFA (automata),
         // synchronized DFA from EFA (automaton), cost automata,
         // total automata  and optimized automata

         includeReset = false;
         shouldOptimize = true;

         // EFAs
         initiateResources(model.getResourceRoot());
         createOperations(model.getOperationRoot());

         moduleToDFA();
         efaAutomaton = AutomataSynchronizer.synchronizeAutomata(efaAutomata);

         totalAutomata.addAutomata(costAutomata);
         totalAutomata.addAutomaton(efaAutomaton);


      } else {
         // Invalid option.
      }
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

   /**
    * Returns the automata who model all the operations' costs
    *
    * @return an automata with automatons modeling the costs
    *
    */
   public Automata getCostAutomata() {
      return costAutomata;
   }

   /**
    * Returns a automaton synchronized from all EFAs.
    *
    * @return an automaton synchronized from the EFAs
    *
    */
   public Automaton getSynchronizedEFAautomaton() {
      return efaAutomaton;
   }

   /**
    * Returns a automata with all cost automaton and the synchronized EFA automaton.
    *
    * @return an automata with all TDFA and the synchronized DFA.
    *
    */
   public Automata getTotalAutomata() {

      return totalAutomata;
   }

   /**
    * Returns the optimal automaton which represents the optimal path through the model.
    *
    * @return an optimal automaton.
    *
    */
   public Automaton getOptimizedAutomaton() {
      return optimizedAutomaton;
   }

   /**
    * Returns the automata with all operation automatons converted from EFA.
    *
    * @return an automata with all DFAs.
    *
    */
   public Automata getEFAautomata() {
      return efaAutomata;
   }

   /**
    * Gets the total cycle time for an optimized automaton.
    *
    * @return a double with the total cycle time for the model.
    *
    */
   public double getTotalCycleTime() {
      double cycleTime = 0;

      String initialStateName = optimizedAutomaton.getInitialState().getName();

      int index = initialStateName.lastIndexOf(EFAVariables.CYCLE_TIME_STRING);

      if (index > -1) {
         String theCycleTime = initialStateName.substring(index + EFAVariables.CYCLE_TIME_STRING.length());
         cycleTime = Double.parseDouble(theCycleTime);
      }

      return cycleTime;
   }


   //-----------------------------------------------------------------
   // Methods for calculation.
   //-----------------------------------------------------------------
   /**
    * Creates EFA variables for all variables connected to this resource.
    * If the resource have sub resources those resources are checked for variables too.
    *
    * @param resource (TreeNode) a node from the resource tree in the model
    *
    */
   protected void initiateResources(TreeNode resource) {

      if (resource.getChildCount() > 0) {
         for (int i = 0; i < resource.getChildCount(); i++) {
            TreeNode subObject = (TreeNode) resource.getChildAt(i);

            if (Model.isResource(subObject.getNodeData())) {
               // It is a resource
               initiateResources(subObject);

            } else if (Model.isVariable(subObject.getNodeData())){
               // It is a variable
               String varName = this.createVarName(subObject);
               int minValue = ((ResourceVariableData) (subObject.getNodeData())).getMin();
               int maxValue = ((ResourceVariableData) (subObject.getNodeData())).getMax();
               int initValue = ((ResourceVariableData) (subObject.getNodeData())).getInitialValue();

               EFA variableEFA = new EFA(varName + EFAVariables.VARIABLE_DUMMY_POSTFIX, module);
               variableEFA.addIntegerVariable(varName, minValue, maxValue, initValue, null);
            }
         }
      }
   }

   /**
    * Creates an automata modeling the costs for the operation.
    *
    * @param operation <TreeNode> the operation with a cost
    *
    */
   protected void createCostAutomaton(TreeNode operation) {

      OperationData operationData = (OperationData) operation.getNodeData();
      String operationName = EFAVariables.OPERATION_NAME_PREFIX + operation.getId();
      String costAutomatonName = operationName + EFAVariables.COST_AUTOMATA_POSTFIX;
      Automaton costAutomaton = new Automaton(costAutomatonName);
      costAutomaton.setType(AutomatonType.PLANT);

      // Add start and stop events to the alphabet.
      LabeledEvent start;
      LabeledEvent stop;

//		if(operationData.getStartEvent().length() > 0){
//			start = new LabeledEvent(operationData.getStartEvent());
//		} else{
//			start = new LabeledEvent(EFAVariables.EFA_START_EVENT_PREFIX + operation.getId());
//		}
//
//		if(operationData.getStopEvent().lenght() > 0){
//			stop = new LabeledEvent(operationData.getStartEvent());
//		} else{
//			stop = new LabeledEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + operation.getId());
//		}

      start = new LabeledEvent(EFAVariables.EFA_START_EVENT_PREFIX + operationName);
      stop = new LabeledEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + operationName);

      Alphabet theAlphabet = costAutomaton.getAlphabet();
      theAlphabet.addEvent(start);
      theAlphabet.addEvent(stop);

      // Create and add three states.
      State initState = new State(costAutomatonName + EFAVariables.STATE_INITIAL_POSTFIX);
      initState.setInitial(true);
      initState.setAccepting(true);
      initState.setCost(0);
      costAutomaton.addState(initState);

      State executionState = new State(costAutomatonName + EFAVariables.STATE_EXECUTION_POSTFIX);
      executionState.setInitial(false);
      executionState.setAccepting(false);
      executionState.setCost(operationData.getCost());
      costAutomaton.addState(executionState);

      State endState = new State(costAutomatonName + EFAVariables.STATE_FINAL_POSTFIX);
      endState.setInitial(false);
      endState.setAccepting(true);
      endState.setCost(0);
      costAutomaton.addState(endState);

      // Add transitions
      costAutomaton.addArc(new Arc(initState, executionState, start));
      costAutomaton.addArc(new Arc(executionState, endState, stop));

      costAutomata.addAutomaton(costAutomaton);
   }

   /**
    * Convert a precondition to a EFA guard and a list of EFA actions. It checks if the precondition is written in a correct way.
    * The rules are: <br><br>
    * 	* Operations may not include the names for AND, OR, =, (, ) or _i, _e, _f. The expression may include blanks.<br><br>
    * 	* Resource booking/unbooking is only allowed outside all parenthesis and with only ANDs outside the parenthesis.<br><br>
    * 	* An operation has to be written with name + the state, e.g. 'Operation1_f' for final state of Operation1.<br><br>
    * 	* The user may NOT write own expressions on the form Variable2==3, but no checking is done if the expression is written correctly.<br><br>
    *
    *
    * @param precondition <String> the precondition to convert to guard and actions.
    * @param operation <TreeNode> the operation that the precondition belongs to. Is only used for
    * 			feedback to the user if the a precondition is written incorrect.
    *
    * @return an object[2] with object[0]<String> = guard as string and
    * 			object[1]<ArrayList<String>> = arraylist of actions as strings.
    *
    */
   protected Object[] createGuardAndAction(String precondition, TreeNode operation) {

      // Two methods that loops the string. The first one remove all the blanks and check the parenthesis sum.
      // The second one remove unnecessary parenthesis. They are both necessary for this method.
      precondition = removeBlanks(precondition);

      // For checking if the precondition is correct.
      int counter = 0;
      boolean includesResources = false;
      boolean andOutsideParenthesis = false;
      boolean orOutsideParenthesis = false;
      boolean allResourcesOK = true;

      // 2 fields for help in the generation of a guard
      int operationStart = 0;
      String expression;

      // The guard that is built and will be returned if correctly written.
      String guard = "";

      // The list of actions that is built and will be returned if correctly written.
      ArrayList<String> actions = new ArrayList<String>();

      // The object that will hold the guard in place 0 and the list of actions on plate 1.
      Object guardAndAction[] = new Object[2];

      // Loops the precondition.
      //      System.out.println(precondition);
      for (int i = 0; i < precondition.length(); i++) {

         // if (. This can only start an expression.
         if (precondition.charAt(i) == '(') {
            counter++;
            operationStart = i + 1;
            guard = guard + '(';
         }

         // if AND, OR or )
         //if(precondition.substring(i).length() > Math.max(EFAVariables.SP_AND.length(), EFAVariables.SP_OR.length())){
         if (precondition.substring(i).length() > -1) {

            if (precondition.substring(i, i + EFAVariables.SP_AND.length()).compareTo(EFAVariables.SP_AND) == 0 ||
                  precondition.substring(i, i + EFAVariables.SP_OR.length()).compareTo(EFAVariables.SP_OR) == 0 || precondition.charAt(i) == ')' || i == precondition.length() - 1) {

               // & and | end an operation name and begin a new one. ) only ends one.
               if (i >= operationStart) {

                  // expression is the expression to interpret.
                  // Can be an operation, an expression added by user or a resource booking.
                  //expression = precondition.substring(operationStart, i);

                  if (i == precondition.length() - 1 && precondition.charAt(i) != ')') {
                     expression = precondition.substring(operationStart);
                  } else {
                     expression = precondition.substring(operationStart, i);
                  }
                  //       System.out.println(expression);
                  if (expression.length() > 1) {

                     // if a resource booking
                     if (expression.substring(expression.length() - EFAVariables.SP_BOOK_RESOURCE.length()).
                           compareTo(EFAVariables.SP_BOOK_RESOURCE) == 0) {
                        // A resource booking
                        includesResources = true;
                        if (counter > 0) {
                           allResourcesOK = false;
                        }
                        String resourceName = expression.substring(0, expression.length() - EFAVariables.SP_BOOK_RESOURCE.length());

                        Integer resourceId = new Integer(resourceName);

                        if (model.getNode(resourceId) != null) {
                           // The resource exists.

                           // Comment KB. This is really weird Here two constants are compared
                           // which is always true...
                           if (EFAVariables.EFA_BOOK.compareTo(EFAVariables.EFA_PLUS_ONE) == 0) {
                              guard = guard + this.createVarName(resourceId.intValue()) + "<" +
                                    ((ResourceVariableData) (model.getResource(resourceId).getNodeData())).getMax();
                           } else {
                              guard = guard + this.createVarName(resourceId.intValue()) + ">" +
                                    ((ResourceVariableData) (model.getResource(resourceId).getNodeData())).getMin();
                           }

                           String action = this.createVarName(resourceId.intValue()) + EFAVariables.EFA_BOOK;
                           actions.add(action);

                        } else {
                           // The resource doesn't exist.
                           logger.error("The resource with id: " + resourceName + " does not exist and can not be booked in: " + operation.getId() + ".");

                        }

                     // if a resource unbooking
                     } else if (expression.substring(expression.length() - EFAVariables.SP_UNBOOK_RESOURCE.length()).
                           compareTo(EFAVariables.SP_UNBOOK_RESOURCE) == 0) {
                        // A resource unbooking
                        includesResources = true;
                        if (counter > 0) {
                           allResourcesOK = false;
                        }
                        String resourceName = expression.substring(0, expression.length() - EFAVariables.SP_UNBOOK_RESOURCE.length());

                        Integer resourceId = new Integer(resourceName);

                        if (model.getResource(resourceId) != null) {
                           // The resource exists.

                           if (EFAVariables.EFA_UNBOOK.compareTo(EFAVariables.EFA_MINUS_ONE) == 0) {
                              guard = guard + this.createVarName(resourceId.intValue()) + ">" +
                                    ((ResourceVariableData) (model.getResource(resourceId).getNodeData())).getMin();
                           } else {
                              guard = guard + this.createVarName(resourceId.intValue()) + "<" +
                                    ((ResourceVariableData) (model.getResource(resourceId).getNodeData())).getMax();
                           }

                           String action = this.createVarName(resourceId.intValue()) + EFAVariables.EFA_UNBOOK;
                           actions.add(action);

                        } else {
                           // The resource doesn't exist.
                           logger.error("The resource with id: " + resourceName + " does not exist and can not be unbooked in: " + operation.getId() + ".");
                        }


                     // if an operation initial state
                     } else if (expression.substring(expression.length() - EFAVariables.STATE_INITIAL_POSTFIX.length()).
                           compareTo(EFAVariables.STATE_INITIAL_POSTFIX) == 0) {
                        // An operation_i precon
                        String operationName = expression.substring(0, expression.length() - EFAVariables.STATE_INITIAL_POSTFIX.length());
                        Integer operationId = new Integer(operationName);

                        if (model.getOperation(operationId) != null) {
                           // The operation exists.
                           guard = guard + this.createVarName(operationId.intValue()) + "==" + EFAVariables.VARIABLE_INITIAL_STATE;
                        } else {
                           // The operation doesn't exist.
                           logger.error("The operation with id: " + operationName + " (initial state)" + " does not exist and can not be refered to in: " + operation.getId() + ".");
                        }


                     // if an operation execution state
                     } else if (expression.substring(expression.length() - EFAVariables.STATE_EXECUTION_POSTFIX.length()).
                           compareTo(EFAVariables.STATE_EXECUTION_POSTFIX) == 0) {
                        // An operation_e precon
                        String operationName = expression.substring(0, expression.length() - EFAVariables.STATE_EXECUTION_POSTFIX.length());
                        Integer operationId = new Integer(operationName);

                        if (model.getOperation(operationId) != null) {
                           // The operation exists.
                           guard = guard + this.createVarName(operationId.intValue()) + "==" + EFAVariables.VARIABLE_EXECUTION_STATE;
                        } else {
                           // The operation doesn't exist.
                           logger.error("The operation with id: " + operationName + " (execution state)" + " does not exist and can not be refered to in: " + operation.getId() + ".");
                        }


                     // if an operation final state
                     } else if (expression.substring(expression.length() - EFAVariables.STATE_FINAL_POSTFIX.length()).
                           compareTo(EFAVariables.STATE_FINAL_POSTFIX) == 0) {
                        // An operation_f precon

                        String operationName = expression.substring(0, expression.length() - EFAVariables.STATE_FINAL_POSTFIX.length());
                        Integer operationId = new Integer(operationName);

                        if (model.getOperation(operationId) != null) {
                           // The operation exists.
                           guard = guard + this.createVarName(operationId.intValue()) + "==" + EFAVariables.VARIABLE_FINAL_STATE;
                        } else {
                           // The operation doesn't exist.
                           logger.error("The operation with id: " + operationName + " (final state)" + " does not exist and can not be refered to in: " + operation.getId() + ".");
                        }


                     } else {
                        for (int k = 0; k < expression.length(); k++) {

                           String varID = "";
                           String value = "";
                           String sign = "";
                           boolean isAction = false;
                           if (expression.substring(k, k + 1).equals("=")) {

                              if (expression.substring(k - 1, k).equals("+")) {
                                 varID = expression.substring(0, k - 1);
                                 value = expression.substring(k + 1);
                                 sign = "+=";
                                 isAction = true;
                              } else if (expression.substring(k - 1, k).equals("-")) {
                                 varID = expression.substring(0, k - 1);
                                 value = expression.substring(k + 1);
                                 sign = "-=";
                                 isAction = true;
                              } else if (expression.substring(k + 1, k + 2).equals("=")) {
                                 varID = expression.substring(0, k);
                                 value = expression.substring(k + 2);
                                 sign = "==";
                              } else if (expression.substring(k - 1, k).equals("<")) {
                                 varID = expression.substring(0, k);
                                 value = expression.substring(k + 1);
                                 sign = "<=";
                              } else if (expression.substring(k - 1, k).equals(">")) {
                                 varID = expression.substring(0, k);
                                 value = expression.substring(k + 1);
                                 sign = ">=";
                              } else{
                                 varID = expression.substring(0, k);
                                 value = expression.substring(k + 1);
                                 sign = "=";
                                 isAction = true;
                              }
                           } else if (expression.substring(k, k + 1).equals("<")) {
                              if (!expression.substring(k + 1, k + 2).equals("=")) {
                                 varID = expression.substring(0, k);
                                 value = expression.substring(k + 1);
                                 sign = "<";
                              }
                           } else if (expression.substring(k, k + 1).equals(">")) {
                              if (!expression.substring(k + 1, k + 2).equals("=")) {
                                 varID = expression.substring(0, k);
                                 value = expression.substring(k + 1);
                                 sign = ">";
                              }
                           }

                           if (!varID.equals("")){
                              String statment = this.createVarName(Integer.valueOf(varID).intValue()) + sign + value;
                              if (isAction){
                                  actions.add(statment) ;
                              } else {
                                  guard = guard + statment;
                              }
                           }
                        }
                     }
                  }
               }
               operationStart = i + 1;
            }


            // 3 if for adding AND, OR or ) after the guard. Also keep track of operatiors outside paranthesis.
            if (precondition.substring(i, i + EFAVariables.SP_AND.length()).compareTo(EFAVariables.SP_AND) == 0) {
               if (counter == 0) {
                  andOutsideParenthesis = true;
               }
               guard = guard + EFAVariables.EFA_AND;
            }

            if (precondition.substring(i, i + EFAVariables.SP_OR.length()).compareTo(EFAVariables.SP_OR) == 0) {
               if (counter == 0) {
                  orOutsideParenthesis = true;
               }
               guard = guard + EFAVariables.EFA_OR;
            }

         } // end of size-if.
         if (precondition.charAt(i) == ')') {
            counter--;
            guard = guard + ")";
         }



      } // End of precondition loop

      // Check if the expression is written correctly and return guard and actions.
      if (includesResources == true) {
         if ((andOutsideParenthesis == true) && (orOutsideParenthesis == false)) {
            if (allResourcesOK == true) {
               // A correct precondition with resource booking and only ANDs outside parenthesis.
               guardAndAction[0] = guard;
               guardAndAction[1] = actions;
               return guardAndAction;

            } else {
               // An incorrect precondition. Give feedback to user.
               logger.error("An incorrect resource booking/unbooking in operation: " + operation.getId() + ".\nOnly resource booking/unbooking outside parenthesis in preconditions is allowed.\nPlease correct the preconditions in " + operation.getId() + " and try again.");
               return null;
            }

         } else if ((andOutsideParenthesis == false) && (orOutsideParenthesis == false)) {
            // A correct precondition with only a resource booking.
            guardAndAction[0] = guard;
            guardAndAction[1] = actions;
            return guardAndAction;

         } else if ((andOutsideParenthesis == false) && (orOutsideParenthesis == true)) {
            // An incorrect precondition. Give feedback to user.
            logger.error("An incorrect resource booking/unbooking in operation: " + operation.getId() + ".\nOnly resource booking/unbooking with only AND outside parethesis is allowed.\nPlease correct the preconditions in " + operation.getId() + " and try again.");
            return null;

         } else if ((andOutsideParenthesis == true) && (orOutsideParenthesis == true)) {
            // An incorrect precondition. Give feedback to user.
            logger.error("An incorrect resource booking/unbooking in operation: " + operation.getId() + ".\nOnly resource booking/unbooking with only AND outside parethesis is allowed.\nPlease correct the preconditions in " + operation.getId() + " and try again.");
            return null;

         } else {
            //Should never happen
            return null;
         }

      } else {
         // A correct precondition with no resource booking.
         guardAndAction[0] = guard;
         guardAndAction[1] = actions;
         return guardAndAction;

      }
   }

   /**
    * Creates an EFA for the new operation and iterate for all its suboperations.
    *
    * @param operation (TreeNode) the operation to convert to EFA
    * @param parentInvariant (String) the invariant for the parent of the operation.
    *
    */
   protected void newOperation(TreeNode operation, String parentInvariant) {

      OperationData operationData = (OperationData) operation.getNodeData();

      if (shouldOptimize) {
//         if (operationData.getCost() > 0) {
//            // Only operations with a cost should be modeled with a cost automaton.
//            createCostAutomaton(operation);
//         }
      }
      String operationName = this.createOpName(operationData);
      String operationVarName = this.createOpVarName(operationData);
      String EFAname = operationName;

      // Create and add the EFA
      EFA efa = new EFA(EFAname, module);
      module.addAutomaton(efa);

      // EFA states
      String startState = operationVarName + EFAVariables.STATE_INITIAL_POSTFIX;
      String executionState = operationVarName + EFAVariables.STATE_EXECUTION_POSTFIX;
      String endState = operationVarName + EFAVariables.STATE_FINAL_POSTFIX;

      efa.addState(startState, true, true);
      efa.addState(executionState);
      if (includeReset) {
         efa.addState(endState);
      } else {
         efa.addAcceptingState(endState);
      }

      // EFA Event Guard Action
      EGA startEGA = new EGA();
      EGA stopEGA = new EGA();
      String startGuard = "";
      String stopGuard = "";

      // Add start and stop events to the EGAs
//		if(operationData.getStartEvent().length() > 0){
//			startEGA.setEvent(operationData.getStartEvent());
//		} else{
//			startEGA.setEvent(EFAVariables.EFA_START_EVENT_PREFIX + operation.getId());
//		}
//
//		if(operationData.getStopEvent().lenght() > 0){
//			stopEGA.setEvent(operationData.getStopEvent());
//		} else{
//			stopEGA.setEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + operation.getId());
//		}

      startEGA.setEvent(EFAVariables.EFA_START_EVENT_PREFIX + operationVarName);
      stopEGA.setEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + operationVarName);

      // Create a variable to track the active state of the OP
      EFA integerEFA = new EFA(operationVarName + EFAVariables.VARIABLE_DUMMY_POSTFIX, module);
      integerEFA.addIntegerVariable(operationVarName, 0, 2, 0, 0);

      String variableStartAction = operationVarName + "=" + EFAVariables.VARIABLE_EXECUTION_STATE;
      String variableStopAction = operationVarName + "=" + EFAVariables.VARIABLE_FINAL_STATE;

      startEGA.addAction(variableStartAction);
      stopEGA.addAction(variableStopAction);

      // A guard to synch with parent operation if the operation is not a preoperation

      if (operation.getParent().getId() > 4) {
         if (!operationData.isPreoperation()) {
            startGuard = this.createOpVarName(operation.getParent().getNodeData()) + "==" + EFAVariables.VARIABLE_EXECUTION_STATE;
         }
      } else {
         startGuard = projectName + "==" + EFAVariables.VARIABLE_EXECUTION_STATE;
      }

      // Adding the preconditions.
      // TODO se över den här

//		if (operationData.getRawSequenceCondition().length() > 0) {
//
//            Object guardAndAction[] = createGuardAndAction(
//					operationData.getRawSequenceCondition(), operation);
//			String guard = (String)guardAndAction[0];
//			ArrayList actions = (ArrayList)guardAndAction[1];
//
//            if(startGuard.length() > 0){
//                startGuard = startGuard + EFAVariables.EFA_AND + guard;
//            }else{
//                startGuard = guard;
//            }
//
//            for(int i=0; i<actions.size(); i++){
//                startEGA.addAction((String) actions.get(i));
//            }
//
//		}


      String precon = operationData.getRawPrecondition();

      System.out.println("Operation: " + operation.getId() + " precon: " + precon);

      if (precon.length() > 0) {
         System.out.println("Lengt: " + precon.length() + " Precon: "+ precon);
         Object guardAndAction[] = createGuardAndAction(
               precon, operation);
         String guard = (String) guardAndAction[0];
         ArrayList actions = (ArrayList) guardAndAction[1];

         if (startGuard.length() > 0) {
            startGuard = startGuard + EFAVariables.EFA_AND + guard;
         } else {
            startGuard = guard;
         }

         for (int i = 0; i < actions.size(); i++) {
            startEGA.addAction((String) actions.get(i));
         }

      }


//		if (operationData.getPrecondition().getInterLocking().length() > 0) {
//			Object guardAndAction[] = createGuardAndAction(
//					operationData.getPrecondition().getInterLocking(), operation);
//			String guard = (String)guardAndAction[0];
//			startGuard = startGuard + EFAVariables.EFA_AND + guard;
//		}

//		if (operationData.getPrecondition().getResourceAllocation().length() > 0) {
//			Object guardAndAction[] = createGuardAndAction(
//					operationData.getPrecondition().getResourceAllocation(), operation);
//			String guard = (String)guardAndAction[0];
//			ArrayList actions = (ArrayList)guardAndAction[1];
//			startGuard = startGuard + EFAVariables.EFA_AND + guard;
//			for(int i=0; i<actions.size(); i++){
//				startEGA.addAction((String) actions.get(i));
//			}
//		}

//		if (operationData.getPrecondition().getAction().size() > 0) {
//			ArrayList actions = operationData.getPrecondition().getAction();
//			// TODO: how will the action look like? want an arraylist with actions, but may have to convert the language.
//			// ska actions alltid avslutas med ";"???
//			for(int i=0; i<actions.size(); i++){
//				startEGA.addAction((String) actions.get(i));
//			}
//		}


      // Adding the postconditions.
      // TODO: se över den här

      String postcon = operationData.getRawPostcondition();

      if (postcon.length() > 0) {
         Object guardAndAction[] = createGuardAndAction(
               postcon, operation);
         String guard = (String) guardAndAction[0];
         ArrayList actions = (ArrayList) guardAndAction[1];

         if (stopGuard.length() > 0) {
            stopGuard = stopGuard + EFAVariables.EFA_AND + guard;
         } else {
            stopGuard = guard;
         }

         for (int i = 0; i < actions.size(); i++) {
            stopEGA.addAction((String) actions.get(i));
         }
      }

//		if (operationData.getPostcondition().getAction().size() > 0) {
//			ArrayList actions = operationData.getPostcondition().getAction();
//			// TODO: how will the action look like? want an arraylist with actions, but may have to convert the language.
//			// ska actions alltid avslutas med ";"???
//			for(int i=0; i<actions.size(); i++){
//				stopEGA.addAction((String) actions.get(i));
//			}
//		}

      // Add invariant inherited from its parent.
      String operationInvariant = "";

      String invariant = operationData.getInvariant();
      if (invariant.length() > 0) {
         Object guardAndAction[] = createGuardAndAction(
               invariant, operation);
         String invariantGuard = (String) guardAndAction[0];

         if (startGuard.length() > 0) {
            startGuard = startGuard + EFAVariables.EFA_AND + invariantGuard;
         } else {
            startGuard = invariantGuard;
         }

         if (stopGuard.length() > 0) {
            stopGuard = stopGuard + EFAVariables.EFA_AND + invariantGuard;
         } else {
            stopGuard = invariantGuard;
         }

         operationInvariant = parentInvariant + invariantGuard;

      } else {
         operationInvariant = parentInvariant;
      }

      // TODO: implement reset events
//		if(includeReset){
//			ArrayList<Reset> resets = operationData.getReset();
//
//			if(resets.size() > 0){
//				for(int i=0; i<resets.size(); i++){
//					EGA resetEGA = new EGA();
//
//					Reset reset = resets.get(i);
//
//					Object guardAndAction[] = createGuardAndAction(
//							reset.getResetCondition, operation);
//					String guard = (String)guardAndAction[0];
//					ArrayList<String> actions = (ArrayList)guardAndAction[1];
//					startGuard = startGuard + EFAVariables.EFA_AND + guard;
//
//					for(int a=0; a<actions.size(); a++){
//						resetEGA.addAction((String) actions.get(i));
//					}
//
//					efa.addTransition(startState, startState,
//							reset.getEvent(), guard,
//							resetEGA.getAction());
//					efa.addTransition(endState, startState,
//							reset.getEvent(), guard,
//							resetEGA.getAction());
//				}
//			}
//		}

      /////Actions
      String[] actions = operationData.getRawActions();


      for (int i = 0; i < actions.length; i++) {
         String expression = actions[i];

         for (int k = 0; k < expression.length(); k++) {

            if (expression.substring(k, k + 1).equals("=")) {
               String varID = "";
               String value = "";
               String sign = "";
               if (expression.substring(k - 1, k).equals("+")) {
                  varID = expression.substring(0, k - 1);
                  value = expression.substring(k + 1);
                  sign = "+=";
               } else if (expression.substring(k - 1, k).equals("-")) {
                  varID = expression.substring(0, k - 1);
                  value = expression.substring(k + 1);
                  sign = "-=";
               } else {
                  varID = expression.substring(0, k);
                  value = expression.substring(k + 1);
                  sign = "=";
               }

               String action = this.createVarName(Integer.valueOf(varID).intValue()) + sign + value;
               if (!action.equals(""))
                   startEGA.addAction(action);

               break;


            }
         }
      }



      ////ACTIONS

      ArrayList<Integer> listOfChildren = new ArrayList<Integer>();

      if (operation.getChildCount() > 0) {
         // Loop the children operations
         for (int i = 0; i < operation.getChildCount(); i++) {
            TreeNode subOperation = (TreeNode) operation.getChildAt(i);
            OperationData subOpData = (OperationData) subOperation.getNodeData();

            listOfChildren.add(subOpData.getId());
         }
      }

      for (int i = 0; i < listOfChildren.size(); i++) {
         TreeNode subOp = (TreeNode) model.getOperation(listOfChildren.get(i));
         boolean write = true;
         for (int j = 0; j < listOfChildren.size(); j++) {
            TreeNode otherOp = (TreeNode) model.getOperation(listOfChildren.get(j));

            OperationData data = (OperationData) otherOp.getNodeData();

            LinkedList<LinkedList<SeqCond>> seqcon = data.getSequenceCondition();

            for (int k = 0; k < seqcon.size(); k++) {
               for (int l = 0; l < seqcon.get(k).size(); l++) {
                  SeqCond s = seqcon.get(k).get(l);
                  if (s.id == subOp.getId() && s.state == 2) {
                     write = false;
                  }
               }
            }

         }
         if (write) {
            OperationData subOpData = (OperationData) subOp.getNodeData();
            // If the suboperation is not a postoperation a stopguard should be added to this operation
            if (!subOpData.isPostoperation()) {
                if (stopGuard.length() > 0) {
                    stopGuard = stopGuard + EFAVariables.EFA_AND + this.createOpVarName(subOp.getNodeData()) + "==" + EFAVariables.VARIABLE_FINAL_STATE;
                } else {
                    stopGuard = this.createOpVarName(subOp.getNodeData()) + "==" + EFAVariables.VARIABLE_FINAL_STATE;
                }
            }
         }


         // Create a new operation.
         newOperation(subOp, operationInvariant);

      }

      if (startGuard.length() == 0) {
         startGuard = projectName + "==" + EFAVariables.VARIABLE_EXECUTION_STATE;
      }




      // Create new operations if this operation has children.
//		if (operation.getChildCount() > 0){
//			// Loop the children operations
//			for (int i = 0; i < operation.getChildCount(); i++){
//				TreeNode subOperation = (TreeNode) operation.getChildAt(i);
//                OperationData subOpData = (OperationData) subOperation.getNodeData();
//				// If the suboperation is not a postoperation a stopguard should be added to this operation
//				if(!subOpData.isPostoperation()){
//                    if(stopGuard.length() > 0){
//						stopGuard = stopGuard + EFAVariables.EFA_AND
//                                + EFAVariables.OPERATION_NAME_PREFIX + subOperation.getId()
//							+ EFAVariables.VARIABLE_NAME_POSTFIX + "=="
//							+ EFAVariables.VARIABLE_FINAL_STATE;
//                    }else{
//                        stopGuard = EFAVariables.OPERATION_NAME_PREFIX + subOperation.getId()
//							+ EFAVariables.VARIABLE_NAME_POSTFIX + "=="
//							+ EFAVariables.VARIABLE_FINAL_STATE;
//                    }
//				}
//				// Create a new operation.
//				newOperation(subOperation, operationInvariant);
//			}
//		}
      //add transitions
      efa.addTransition(startState, executionState,
            startEGA.getEvent(),
            startGuard,
            startEGA.getAction());

      efa.addTransition(executionState, endState,
            stopEGA.getEvent(),
            stopGuard,
            stopEGA.getAction());

   }

   /**
    * Initialize the creation of EFAs and creates a project EFA to model the execution of
    * the project. The reson for the project EFA is to give the optimization algorithm the
    * object to get the project EFA to be finished.
    *
    * @param the root of the operation tree
    */
   protected void createOperations(TreeNode operation) {

      String operationName = projectName;
      String EFAname = operationName + EFAVariables.COST_STRING + "0.0";

      // Create and add the EFA
      EFA efa = new EFA(EFAname, module);
      module.addAutomaton(efa);

      // EFA states
      String startState = operationName + EFAVariables.STATE_INITIAL_POSTFIX;
      String executionState = operationName + EFAVariables.STATE_EXECUTION_POSTFIX;
      String endState = operationName + EFAVariables.STATE_FINAL_POSTFIX;

      efa.addInitialState(startState);
      efa.addState(executionState);
      efa.addAcceptingState(endState);

      // EFA Event Guard Action
      EGA startEGA = new EGA();
      EGA stopEGA = new EGA();
      String startGuard = "";
      String stopGuard = "";

      startEGA.setEvent(EFAVariables.EFA_START_EVENT_PREFIX + operationName);
      stopEGA.setEvent(EFAVariables.EFA_STOP_EVENT_PREFIX + operationName);

      // Create a variable to track the active state of the OP
      String varName = operationName;
      EFA integerEFA = new EFA(operationName + EFAVariables.VARIABLE_DUMMY_POSTFIX, module);
      integerEFA.addIntegerVariable(varName, 0, 2, 0, 0);

      String variableStartAction = varName + "=" + EFAVariables.VARIABLE_EXECUTION_STATE;
      String variableStopAction = varName + "=" + EFAVariables.VARIABLE_FINAL_STATE;

      startEGA.addAction(variableStartAction);
      stopEGA.addAction(variableStopAction);




      ArrayList<Integer> listOfChildren = new ArrayList<Integer>();

      if (operation.getChildCount() > 0) {
         // Loop the children operations
         for (int i = 0; i < operation.getChildCount(); i++) {
            TreeNode subOperation = (TreeNode) operation.getChildAt(i);
            OperationData subOpData = (OperationData) subOperation.getNodeData();

            listOfChildren.add(subOpData.getId());
         }
      }

      for (int i = 0; i < listOfChildren.size(); i++) {
         TreeNode subOp = (TreeNode) model.getOperation(listOfChildren.get(i));
         boolean write = true;
         for (int j = 0; j < listOfChildren.size(); j++) {
            TreeNode otherOp = (TreeNode) model.getOperation(listOfChildren.get(j));

            OperationData data = (OperationData) otherOp.getNodeData();

            LinkedList<LinkedList<SeqCond>> seqcon = data.getSequenceCondition();

            for (int k = 0; k < seqcon.size(); k++) {
               for (int l = 0; l < seqcon.get(k).size(); l++) {
                  SeqCond s = seqcon.get(k).get(l);
                  if (s.id == subOp.getId() && s.state == 2) {
                     write = false;
                  }
               }
            }
         }

         if (write) {

            // If the suboperation is not a postoperation a stopguard should be added to this operation

            if (stopGuard.length() > 0) {
               stopGuard = stopGuard + EFAVariables.EFA_AND + this.createOpVarName(subOp.getNodeData()) + "==" + EFAVariables.VARIABLE_FINAL_STATE;
            } else {
               stopGuard = this.createOpVarName(subOp.getNodeData()) + "==" + EFAVariables.VARIABLE_FINAL_STATE;
            }
         }
         // Create a new operation.
         newOperation(subOp, "");
      }





















//		// Create new operations if this operation has children.
//		if (operation.getChildCount() > 0){
//			// Loop the children operations
//			for (int i = 0; i < operation.getChildCount(); i++){
//				TreeNode subOperation = (TreeNode) operation.getChildAt(i);
//                OperationData subOpData = (OperationData) subOperation.getNodeData();
//				// If the suboperation is not a postoperation a stopguard should be added to this operation
//				if(!subOpData.isPostoperation()){
//                    if(stopGuard.length() > 0){
//						stopGuard = stopGuard + EFAVariables.EFA_AND
//                                + EFAVariables.OPERATION_NAME_PREFIX + subOperation.getId()
//							+ EFAVariables.VARIABLE_NAME_POSTFIX + "=="
//							+ EFAVariables.VARIABLE_FINAL_STATE;
//                    }else{
//                        stopGuard = EFAVariables.OPERATION_NAME_PREFIX + subOperation.getId()
//							+ EFAVariables.VARIABLE_NAME_POSTFIX + "=="
//							+ EFAVariables.VARIABLE_FINAL_STATE;
//                    }
//				}
//				// Create a new operation.
//				newOperation(subOperation, "");
//			}
//		}
      //add transitions
      efa.addTransition(startState, executionState,
            startEGA.getEvent(),
            startGuard,
            startEGA.getAction());

      efa.addTransition(executionState, endState,
            stopEGA.getEvent(),
            stopGuard,
            stopEGA.getAction());

   }

   /**
    * Convert the module with the EFAs to a Supremica project and puts all automatons in a automata	 *
    *
    */
   protected void moduleToDFA() throws EvalException {

      ModuleSubject moduleSubject = module.getModule();
      moduleSubject.setName("Sequence Planner to EFA output");

      DocumentManager doc = new DocumentManager();
      ProjectBuildFromWaters projectBuilder = new ProjectBuildFromWaters(doc);

      Project efaProject = projectBuilder.build((ModuleSubject) moduleSubject);

      for (int i = 0; i < efaProject.nbrOfAutomata(); i++) {
         efaAutomata.addAutomaton(efaProject.getAutomatonAt(i));
      }

   }

   /**
    * Return a string without unnecessary parenthesis in the beginning and the end.
    *
    * @param indata <String> the string to fix.
    *
    * @return a string without the parenthesis.
    *
    */
   protected String removeParenthesis(String indata) {
      if (indata == null) {
         return null;
      }
      int counter = 0;
      int min = 1000;

      boolean paranthesis = false;
      while (min > 0) {
         boolean ANDOR = false;
         counter = 0;
         for (int i = 0; i < indata.length(); i++) {
            if (indata.charAt(i) == '(') {
               counter++;
               paranthesis = true;
            }
            if (indata.charAt(i) == ')') {
               counter--;
            }

            if (indata.charAt(i) == '&') {
               if (counter < min) {
                  min = counter;
                  ANDOR = true;
               }
            }
            if (indata.charAt(i) == '|') {
               if (counter < min) {
                  min = counter;
                  ANDOR = true;
               }
            }
         }


         if (ANDOR == true) {
            if (min > 0) {
               indata = indata.substring(1, indata.length() - 1);
            }
         } else if (paranthesis == true) {
            // There is no & or | in string but still parenthesis.
            indata = indata.substring(1, indata.length() - 1);
         } else {
            break;
         }
      }
      return indata;
   }

   /**
    * Return a string without blanks.
    *
    * @param indata <String> the string to fix.
    *
    * @return a string without blanks.
    *
    */
   protected String removeBlanks(String indata) {
      int counter = 0;
      String newString = "";
      for (int i = 0; i < indata.length(); i++) {
         if (indata.charAt(i) != ' ') {
            newString = newString + indata.substring(i, i + 1);
         }
         if (indata.charAt(i) == '(') {
            counter++;
         }
         if (indata.charAt(i) == ')') {
            counter--;
         }
      }
      if (counter == 0) {
         return newString;
      } else {
         return null;
      }
   }

   /**
    * Creates an automata representing the optimal (lowest cost) path through the inputed automata
    *
    * @return optimizedAutomaton is an automaton representing the optimal path through the
    * system with EFAs with costs.
    *
    */
   public Automaton optimizeAutomata(Automata totalAutomata) {

      try {
         Scheduler sched = new ModifiedAstar(totalAutomata,
               SchedulingConstants.ONE_PRODUCT_RELAXATION, false, true, false);

         // Start the scheduling thread
         sched.startSearchThread();

         // Wait for the Scheduler to become stopped (finished)
         while (!sched.isStopped()) {
            Thread.sleep(10);
         }

         // Get the resulting scheduled automaton
         optimizedAutomaton = sched.getSchedule();

      } catch (Exception e) {
      }

      return optimizedAutomaton;
   }


   private String createOpName(OperationData od){
       return od.getName().replace(' ', '_') + EFAVariables.ID_PREFIX + od.getId() + EFAVariables.COST_STRING + od.getCost();
   }

   private String createOpVarName(Data od){
       return od.getName().replace(' ', '_') + EFAVariables.ID_PREFIX + od.getId();
   }

   private String createVarName(TreeNode var){
       return EFAVariables.VARIABLE_NAME_PREFIX + var.getNodeData().getName().replace(' ', '_') + EFAVariables.ID_PREFIX + var.getId();
   }

   private String createVarName(int id){
      TreeNode n = model.getNode(Integer.valueOf(id).intValue());
      String result = "";
      if (n != null){
        if (Model.isOperation(n.getNodeData())){
            result = this.createOpVarName((OperationData) n.getNodeData());
        } else if (Model.isVariable(n.getNodeData())) {
            result = this.createVarName(n);
        }
      }
      return result;

   }
}


