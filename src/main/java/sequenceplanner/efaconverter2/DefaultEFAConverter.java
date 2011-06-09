/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2;

import java.util.Iterator;
import org.supremica.external.avocades.common.Module;
import sequenceplanner.efaconverter2.EFA.*;
import sequenceplanner.efaconverter2.SpEFA.*;
import sequenceplanner.efaconverter2.condition.*;

/**
 *
 * @author shoaei
 */
public class DefaultEFAConverter {
    
    private SpEFAutomata sp;
    private DefaultEFAutomata automata;
    private Module module;
    
    public DefaultEFAConverter(SpEFAutomata sp){
        this.sp = sp;
        automata = new DefaultEFAutomata(sp.getName());
        module = new Module("Model for " + sp.getName(), false);
    }
    
    public boolean convert(){
        boolean result = false;
        for(SpVariable spv : sp.getVariables()){
            DefaultEFAutomaton var = new DefaultEFAutomaton(createVariableName(spv.getName()), module);
            var.addVariable(spv.getMin(), spv.getMax(), spv.getInit());
        }
        
        for(SpEFA spefa : sp.getAutomatons()){
            String efaName = createEFAName(spefa.getName());
            String varName = createVariableName(efaName);
            DefaultEFAutomaton efa = new DefaultEFAutomaton(efaName, module);
            DefaultEFAutomaton var = new DefaultEFAutomaton(varName, module);
            var.addVariable(0, spefa.getLocations().size() - 1, 0);
            
//            for(SpLocation spl : spefa.getLocations()){
//                efa.addLocation(spl.getName(), spl.isAccepting(), spl.isInitialLocation());
//            }
            
            for(SpEvent spe : spefa.getAlphabet())
                efa.addEvent(spe.getName(), spe.isControllable());
            
            int count = 0;
            for(Iterator<SpTransition> itr = spefa.iterateSequenceTransitions(); itr.hasNext();){
                SpTransition tran = itr.next();
                efa.addLocation(tran.getFrom().getName(), tran.getFrom().isAccepting(), tran.getFrom().isInitialLocation());
                
                if(!itr.hasNext())
                    efa.addLocation(tran.getTo().getName(), tran.getTo().isAccepting(), tran.getTo().isInitialLocation());
                
                String guard = parsGuards(tran.getConditionGuard());
                String action = parsActions(tran.getConditionAction());
                String actionLocation = varName + ConditionStatment.Operator.Equal + Integer.toString(count++) + EFAVariables.EFA_ACTION_DIVIDER;
                action += actionLocation;

                efa.addTransition(tran.getFrom().getName(), 
                                  tran.getTo().getName(), 
                                  tran.getEvent().getName(), 
                                  guard, 
                                  action);
            }
            module.addAutomaton(efa.getAutomaton());
        }
        return result;
    }

    private String parsGuards(ConditionExpression cex){
        String result = "(";
        for (ConditionElement e : cex){
            if(e.isExpression())
               result += parsGuards((ConditionExpression)e);
            result += e;
            if(e.hasNextOperator())
                result += e.getNextOperator();
        }
        return result + ")";
    }
    
    private String parsActions(ConditionExpression cex){
        String result = "";
        for (ConditionElement e : cex){
            if(e.isExpression())
               result += parsActions((ConditionExpression)e);
            result += e + EFAVariables.EFA_ACTION_DIVIDER;
        }
        return result;
    }
    
    private String createEFAName(String iOperationName) {
        return EFAVariables.OPERATION_NAME_PREFIX + iOperationName;
    }

    private String createVariableName(String iVariableName) {
        return EFAVariables.VARIABLE_NAME_PREFIX + iVariableName;
    }
    
}
