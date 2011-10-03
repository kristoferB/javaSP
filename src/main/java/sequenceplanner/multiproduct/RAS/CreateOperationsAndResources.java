package sequenceplanner.multiproduct.RAS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sequenceplanner.IO.excel.Excel;
import sequenceplanner.IO.excel.SheetTable;
import sequenceplanner.algorithm.AAlgorithm;
import sequenceplanner.expression.Clause;
import sequenceplanner.expression.Literal;
import sequenceplanner.expression.Literal.LiteralOperator;

/**
 * To parse operations and peripheral things from excel file.<br/>
 *
 * Interpretation:--------------------<br/>
 * A row is an operation if the cell in the first column starts with o:<br/>
 * o: operation name.<br/>
 * pre: precondition to other operations. Supports disjuntive normal form. Operations has to be finished.<br/>
 * r: what resources to use. Supports conjuntion of resource. Syntax: r:resource1==2&resource3==1 means 2 instances of resource1 and 1 instance of resource3.<br/>
 * act: general action that will be added to transition. Supports conjunction of actions. Syntax: act:Var1-=1;Var4=4.<br/>
 * gua: general guard that will be added to transition. Supports conjunction of guards. Syntax: gua:Var1==1;Var4>3. (it should be ; and not &)<br/>
 * extra: to add attributes to operation. Suffix will work as key in attribute list for operation, value is true. Syntax: extra:key -> attributeMap.put(key,true).
 * This can be used if operation should have some other behaviour then normal.<br/>
 *
 * A row is a resource if the cell in the first column starts with r:<br/>
 * r: resource name.<br/>
 * cell in 2nd column: upperBound for resource variable -> domain 0..upperBound<br/>
 * cell in 3nd column: initial and single marked value for resource variable<br/>
 *
 * A row is a variable if the cell in the first column starts with v:<br/>
 * v: variable name.<br/>
 * cell in 2nd column: upperBound for variable variable -> domain 0..upperBound<br/>
 * cell in 3nd column: initial and single marked value for variable variable<br/>
 * Should probably include cell for lower value and separte initial and marked value.<br/>
 * -----------------------------------<br/>
 * @author patrik
 */
public class CreateOperationsAndResources extends AAlgorithm {

    final private Set<Operation> mOperationSet;
    final private Set<Variable> mVariableSet; //To store "extra" variables defined in Excel file
    private String mFilePath = "";
    private Map<String, SheetTable> mSheetTableMap;
    private final String needToStart = "^";
    private final String any = "(.*)";
    private final String intValue = "(\\d{1,})";

    public CreateOperationsAndResources(String iThreadName) {
        super(iThreadName);
        mOperationSet = new HashSet<Operation>();
        mVariableSet = new HashSet<Variable>();
    }

    @Override
    public void init(List<Object> iList) {
        mFilePath = (String) iList.get(0);
    }

    @Override
    public void run() {

        getStatus("Parse excel file...");
        if (!parseExcelFile()) {
            return;
        }

        getStatus("Create operations...");
        if (!OperationCreation()) {
            return;
        }

        final List<Object> returnList = new ArrayList<Object>();
        returnList.add(mOperationSet);
        returnList.add(mVariableSet);
        fireFinishedEvent(returnList);
    }

    private boolean parseExcelFile() {
        final Excel excel = new Excel(mFilePath);
        if (!excel.runParse()) {
            return false;
        }
        mSheetTableMap = excel.getSheets();
//        System.out.println(excel.toString());
        return true;
    }

    /**
     * Two loops:<br/>
     * 1: create operations, resources, and "extra" variables.<br/>
     * 2: add conditions to operations.<br/>
     * @return true if ok else false
     */
    private boolean OperationCreation() {
        final Map<String, Operation> labelOperationMap = new HashMap<String, Operation>();
        final Map<String, Resource> labelResourceMap = new HashMap<String, Resource>();
        Matcher matcher;

        //Create operations and resouces-----------------------------------------
        //-----------------------------------------------------------------------
        for (final String sheet : mSheetTableMap.keySet()) {
            final SheetTable st = mSheetTableMap.get(sheet);
            for (int row = 0; row < st.getNbrOfRows(); row++) {
                //Operation------------------------------------------------------
                matcher = Pattern.compile(needToStart + "o:" + any).matcher(st.getCellValue(row, 0));
                if (matcher.find()) {
                    final String label = matcher.group(1);
                    labelOperationMap.put(label, new Operation(label));
                }//--------------------------------------------------------------

                //Resource-------------------------------------------------------
                matcher = Pattern.compile(needToStart + "r:" + any).matcher(st.getCellValue(row, 0));
                if (matcher.find()) {
                    final String label = matcher.group(1);

                    matcher = Pattern.compile(intValue).matcher(st.getCellValue(row, 1));
                    if (!matcher.find()) {
                        fireNewMessageEvent("Resource not right in sheet " + sheet + " row " + (row + 1));
                        return false;
                    }
                    final String upperBound = st.getCellValue(row, 1);

                    matcher = Pattern.compile(intValue).matcher(st.getCellValue(row, 2));
                    if (!matcher.find()) {
                        fireNewMessageEvent("Resource not right in sheet " + sheet + " row " + (row + 1));
                        return false;
                    }
                    final String initMarked = st.getCellValue(row, 2);

                    if (Integer.valueOf(upperBound) < Integer.valueOf(initMarked)) {
                        fireNewMessageEvent("Resource values not right in sheet " + sheet + " row " + (row + 1) + "; initMarked value greater than upper bound value");
                        return false;
                    }

                    labelResourceMap.put(label, new Resource(label, upperBound, initMarked));
                }//--------------------------------------------------------------

                //Variable------------------------------------------------------
                matcher = Pattern.compile(needToStart + "v:" + any).matcher(st.getCellValue(row, 0));
                if (matcher.find()) {
                    final String label = matcher.group(1);

                    matcher = Pattern.compile(intValue).matcher(st.getCellValue(row, 1));
                    if (!matcher.find()) {
                        fireNewMessageEvent("Variable not right in sheet " + sheet + " row " + (row + 1));
                        return false;
                    }
                    final String upperBound = st.getCellValue(row, 1);

                    matcher = Pattern.compile(intValue).matcher(st.getCellValue(row, 2));
                    if (!matcher.find()) {
                        fireNewMessageEvent("Variable not right in sheet " + sheet + " row " + (row + 1));
                        return false;
                    }
                    final String initMarked = st.getCellValue(row, 2);

                    if (Integer.valueOf(upperBound) < Integer.valueOf(initMarked)) {
                        fireNewMessageEvent("Variable values not right in sheet " + sheet + " row " + (row + 1) + "; initMarked value greater than upper bound value");
                        return false;
                    }

                    mVariableSet.add(new Variable(label, upperBound, initMarked));
                }//--------------------------------------------------------------
            }
        }//----------------------------------------------------------------------
        //-----------------------------------------------------------------------

        //Add clauses for operations---------------------------------------------
        //-----------------------------------------------------------------------
        for (final String sheet : mSheetTableMap.keySet()) {
            final SheetTable st = mSheetTableMap.get(sheet);
            for (int row = 0; row < st.getNbrOfRows(); row++) {
                //Operation
                matcher = Pattern.compile(needToStart + "o:" + any).matcher(st.getCellValue(row, 0));
                if (matcher.find()) {
                    final String operationLabel = matcher.group(1);
                    final Clause resourceClause = new Clause();
                    final List<Clause> preconditionOperationSet = new ArrayList<Clause>();
                    final Map<String, Object> attributeMap = new HashMap<String, Object>();
                    for (int col = 1; col < st.getNbrOfColumns(); col++) {

                        //Resources conjunction----------------------------------
                        matcher = Pattern.compile(needToStart + "r:" + any).matcher(st.getCellValue(row, col));
                        if (matcher.find()) {
                            final String[] literalStrings = matcher.group(1).split("&");
                            for (final String literalString : literalStrings) {
                                matcher = Pattern.compile(any + "==" + intValue).matcher(literalString);
                                if (matcher.find()) {
                                    final Resource resource = labelResourceMap.get(matcher.group(1));
                                    if (resource == null) {
                                        fireNewMessageEvent("Resource for operation " + operationLabel + " not right in sheet " + sheet + " row " + (row + 1) + " col " + Excel.columnLetterEquivalent(col));
                                        return false;
                                    }
                                    final Literal literal = new Literal(resource, LiteralOperator.Equal, Integer.valueOf(matcher.group(2)));
                                    resourceClause.addLiteral(literal);
                                } else {
                                    fireNewMessageEvent("Literal " + literalString + " for operation " + operationLabel + " not right in sheet " + sheet + " row " + (row + 1) + " col " + Excel.columnLetterEquivalent(col));
                                    return false;
                                }
                            }
                        }//------------------------------------------------------

                        //Precondition operations--------------------------------
                        matcher = Pattern.compile(needToStart + "pre:" + any).matcher(st.getCellValue(row, col));
                        if (matcher.find()) {
//                            System.out.println(matcher.group(1));
                            final String[] clauseStrings = matcher.group(1).split("\\|");
                            for (final String clauseString : clauseStrings) {
//                                System.out.println(clauseString);
                                final Clause operationClause = new Clause();
                                final String[] literalStrings = clauseString.split("&");
                                for (final String literalString : literalStrings) {
//                                    System.out.println(literalString);
                                    matcher = Pattern.compile(any + "==" + intValue).matcher(literalString);
                                    if (matcher.find()) {
                                        final Operation operation = labelOperationMap.get(matcher.group(1));
                                        if (operation == null) {
                                            fireNewMessageEvent("Operation in precondition for operation " + operationLabel + " not right in sheet " + sheet + " row " + (row + 1) + " col " + Excel.columnLetterEquivalent(col));
                                            return false;
                                        }
                                        final Literal literal = new Literal(operation, LiteralOperator.Equal, Integer.valueOf(matcher.group(2)));
                                        operationClause.addLiteral(literal);
                                    } else {
                                        fireNewMessageEvent("Literal " + literalString + " for operation " + operationLabel + " not right in sheet " + sheet + " row " + (row + 1) + " col " + Excel.columnLetterEquivalent(col));
                                        return false;
                                    }
                                }
                                preconditionOperationSet.add(operationClause);
                            }
                        }//------------------------------------------------------

                        //Extra actions------------------------------------------
                        extraGuardsActions("act:", st.getCellValue(row, col), Operation.EXTRA_ACTIONS, attributeMap);
                        //-------------------------------------------------------

                        //Extra guards-------------------------------------------
                        extraGuardsActions("gua:", st.getCellValue(row, col), Operation.EXTRA_GUARDS, attributeMap);
                        //-------------------------------------------------------

                        //Extra--------------------------------------------------
                        matcher = Pattern.compile(needToStart + "extra:" + any).matcher(st.getCellValue(row, col));
                        if (matcher.find()) {
                            final String key = matcher.group(1);
                            attributeMap.put(key, true);
                        }//------------------------------------------------------
                    }

                    //Add clauses to operation object----------------------------
//                    System.out.println(resourceClause);
//                    System.out.println(preconditionOperationSet);
                    final Operation op = labelOperationMap.get(operationLabel);
                    if (preconditionOperationSet.isEmpty()) {
                        op.mPreOperationDNFClauseList.add(new Clause());
                    } else {
                        for (final Clause cluase : preconditionOperationSet) {
                            op.mPreOperationDNFClauseList.add(cluase);
                        }
                    }
                    for (final String key : attributeMap.keySet()) {
                        op.setAttribute(key, attributeMap.get(key));
                    }
                    op.mResourceConjunction = resourceClause;
                    mOperationSet.add(op);
                    //-----------------------------------------------------------
                }
            }
        }//----------------------------------------------------------------------
        //-----------------------------------------------------------------------
        return true;
    }

    private void extraGuardsActions(final String iPrefix, final String iCellValue, final String iKey, final Map<String, Object> ioMap) {
        Matcher matcher = Pattern.compile(needToStart + iPrefix + any).matcher(iCellValue);
        if (matcher.find()) {
            final String action = matcher.group(1);
            String valueAction = "";
            if (ioMap.containsKey(iKey)) {
                valueAction = ((String) ioMap.get(iKey)) + ";";
            }
            valueAction += action;
            ioMap.put(iKey, valueAction);
        }
    }

    private void dummyOperationCreation() {
        final Integer finished = new Integer(2);
        final Integer requiresOne = new Integer(1);
        Literal resourceLiteral;
        Clause resourceClause;

        Resource fixTable = new Resource("FixTable", "1", "1");
        resourceLiteral = new Literal(fixTable, LiteralOperator.Equal, requiresOne);
        resourceClause = new Clause();
        resourceClause.addLiteral(resourceLiteral);
        Operation op1 = createOperation("fixate", new Clause(), resourceClause);

        Literal literal2 = new Literal(op1, LiteralOperator.Equal, finished);
        Clause clause2 = new Clause();
        clause2.addLiteral(literal2);
        Resource millingResource = new Resource("MillingMachine", "2", "2");
        resourceLiteral = new Literal(millingResource, LiteralOperator.Equal, requiresOne);
        resourceClause = new Clause();
        resourceClause.addLiteral(resourceLiteral);
        Operation op2 = createOperation("milling", clause2, resourceClause);

        Literal literal3 = new Literal(op2, LiteralOperator.Equal, finished);
        Clause clause3 = new Clause();
        clause3.addLiteral(literal3);
        resourceLiteral = new Literal(fixTable, LiteralOperator.Equal, requiresOne);
        resourceClause = new Clause();
        resourceClause.addLiteral(resourceLiteral);
        Operation op3 = createOperation("unFixate", clause3, resourceClause);

        Literal literal4 = new Literal(op3, LiteralOperator.Equal, finished);
        Clause clause4 = new Clause();
        clause4.addLiteral(literal4);
        Operation op4 = createOperation("unBook", clause4, new Clause());
        op4.setAttribute(Operation.NO_RESOURCE_BOOKING, true);

    }

    private Operation createOperation(final String iLabel, final Clause iPreOperationDNFClause, final Clause iResourceConjuctionClause) {
        final Operation op = new Operation(iLabel);

//        //Creates a subOp that is more or less a clone of op
//        final Operation subOp = new Operation(iLabel);
//        op.mSubOperationSet.add(subOp);

        //Add an empty clause for no predecessor operations
        op.mPreOperationDNFClauseList.add(iPreOperationDNFClause);

        //No resources needed
        op.mResourceConjunction = iResourceConjuctionClause;

        mOperationSet.add(op);
        return op;
    }
}
