/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.Module;
import sequenceplanner.efaconverter.efamodel.SpEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.efaconverter.efamodel.SpLocation;
import sequenceplanner.efaconverter.efamodel.SpTransition;
import sequenceplanner.efaconverter.efamodel.SpVariable;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author shoaei
 * Some methods are copied from convertSeqToEFA class
 */
public class OperationToEFA{
    TreeNode operation;

    public OperationToEFA(TreeNode operation){
        this.operation = operation;
    }

    private String getName(TreeNode operation){
        return ((OperationData)operation.getNodeData()).getName();
    }


    // Create Supremica module
//    public Module createSupremicaModule(){
//        // Create ExtendedAutomata
//        Module module = new Module(getName(operation), false);
//         for (SpVariable v : spAutomata.getVariables()){
//            createVariableEFA(v,module);
//            // If this efa is added to the model two efa will be saved.
//        }
//         // konvertera till automata
//        for (SpEFA efa : spAutomata.getAutomatons()){
//            module.addAutomaton(createExtendedAutomata(efa,module));
//        }
//        return module;
//    }
//
//    private EFA createVariableEFA(SpVariable variable, Module m){
//        EFA efaVar = new EFA(variable.getName(),m);
//        efaVar.addIntegerVariable(
//                variable.getName(),
//                variable.getMin(),
//                variable.getMax(),
//                variable.getInit(),
//                variable.getInit());
//        return efaVar;
//    }
//
//    private EFA createExtendedAutomata(SpEFA efa, Module m){
//        EFA extendedAutomata = new EFA(efa.getName(),m);
//        SpLocation current = efa.getInitialLocation();
//        extendedAutomata.addInitialState(current.getName());
//        while(current.hasOutTransition()){
//            SpTransition trans = null;
//            for (SpTransition t : current.getOutTransitions()){
//                trans = t;
//                break;
//            }
//            if (trans == null) break;
//            SpLocation next = trans.getTo();
//            if (next == null) break;
//            extendedAutomata.addState(next.getName());
//            extendedAutomata.addTransition(current.getName(),
//                                           next.getName(),
//                                           trans.getEventLabel(),
//                                           trans.getGuard(),
//                                           trans.getAction());
//            current = next;
//        }
//        return extendedAutomata;
//    }

}
