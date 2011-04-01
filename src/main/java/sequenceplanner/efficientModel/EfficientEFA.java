/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.sat4j.reader.AAGReader;
import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.Module;
import sequenceplanner.condition.Condition;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.efaconverter.efamodel.*;
import sequenceplanner.efaconverter.convertSeqToEFA;
import sequenceplanner.model.Model;
import sequenceplanner.model.data.OperationData.Action;
/**
 *
 * @author shoaei
 */
public class EfficientEFA {

    static Logger log = Logger.getLogger(EfficientEFA.class);
    private Model model;
    private List<TreeNode> operationList;
    private List<List<String>> paths;
    private Module module;

    private static String OPLOCSEP_CHAR = "_";
    private static String INIT = "_i";
    private static String EXEC = "_e";
    private static String FINI = "_f";

    public EfficientEFA(Model model, List<List<String>> paths){
        this.model = model;
        this.paths = paths;
        operationList = new ArrayList<TreeNode>();
        this.module = new Module("EfficientModel", false);
        init();
    }

    public void generateEFA(){

        SpEFAutomata spEFAutomata = createSpEFAutomata(operationList);
        SpEFAutomata spSynchEFA = synchEFAutomata(spEFAutomata, paths);
        reduceOrderSpEFA(spSynchEFA);
        convertSPToEFA(spSynchEFA, module);
    }

    private OperationData getOperationData(String opID) {
        OperationData opData = null;
        for(TreeNode node : operationList)
            if(node.getId() == Integer.parseInt(opID))
                opData = (OperationData) node.getNodeData();
        return opData;
    }

    private void init() {
        // Get all operations in the model
        for (int i = 0; i < model.getOperationRoot().getChildCount(); ++i){
            DFS(model.getOperationRoot().getChildAt(i));
        }
    }

        private void DFS(TreeNode node){
        if(node.getChildCount() > 0){
            for (int i = 0; i < node.getChildCount(); ++i){
                DFS(node.getChildAt(i));
            }
        }
        operationList.add(node);
    }

    private SpEFAutomata createSpEFAutomata(List<TreeNode> operationList) {
        SpEFAutomata spEFAutomata = new SpEFAutomata();
        for(TreeNode op : operationList){
            SpEFA spEFA = createSpEFA(op);
            spEFAutomata.addAutomaton(spEFA);
            for(SpEvent e : spEFA.getAlphabet())
                spEFAutomata.addEvent(e);
        }
        return spEFAutomata;
    }

    private SpEFAutomata synchEFAutomata(SpEFAutomata spEFAutomata, List<List<String>> paths) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void reduceOrderSpEFA(SpEFAutomata spSynchEFA) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void convertSPToEFA(SpEFAutomata spSynchEFA, Module module) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private SpEFA createSpEFA(TreeNode op) {
        String opName = "Op"+op.getId();
        SpEFA spEFA = new SpEFA(opName+OPLOCSEP_CHAR+"EFA");
        OperationData opData = (OperationData)op.getNodeData();

        SpLocation l_Init = new SpLocation(opName+INIT);
        SpLocation l_Exec = new SpLocation(opName+EXEC);
        SpLocation l_Fini = new SpLocation(opName+FINI);

        SpEvent e_Start = new SpEvent("Start"+OPLOCSEP_CHAR+opName, true);
        SpEvent e_Stop = new SpEvent("Stop"+OPLOCSEP_CHAR+opName, true);

        SpTransition spIETransition = new SpTransition(e_Start.getName(), l_Init, l_Exec, getPreCondition(opData.getRawPrecondition()));
        SpTransition spEFTransition = new SpTransition(e_Stop.getName(), l_Exec, l_Fini, getPostCondition(opData.getRawPostcondition()));

        spEFA.addTransition(spIETransition);
        spEFA.addTransition(spEFTransition);

        return spEFA;
    }

    private Condition getPreCondition(String rawPrecondition) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Condition getPostCondition(String rawPostcondition) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



}
