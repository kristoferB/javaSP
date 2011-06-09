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
public class DefaultEFAConverter implements IEFAConverter{
    
    private SpEFAutomata sp;
    private DefaultEFAutomata automata;
    
    public DefaultEFAConverter(SpEFAutomata sp){
        this.sp = sp;
        automata = new DefaultEFAutomata(sp.getName());
        convert();
    }
    
    private boolean convert(){
        boolean result = false;
        for(SpVariable spv : sp.getVariables()){
            DefaultEFAutomaton var = new DefaultEFAutomaton(spv.getName(), automata);
            var.addVariable(spv.getMin(), spv.getMax(), spv.getInit());
        }
        
        for(SpEFA spefa : sp.getAutomatons()){
            String efaName = spefa.getName();
            String varName = createVariableName(efaName);
            DefaultEFAutomaton efa = new DefaultEFAutomaton(efaName, automata);
            DefaultEFAutomaton var = new DefaultEFAutomaton(varName, automata);
            var.addVariable(0, spefa.getLocations().size() - 1, 0);
            
            for(SpLocation spl : spefa.getLocations()){
                efa.addLocation(spl.getName(), spl.isAccepting(), spl.isInitialLocation());
            }
            
            for(SpEvent spe : spefa.getAlphabet())
                efa.addEvent(spe.getName(), spe.isControllable());
            
            int count = 0;
            for(Iterator<SpTransition> itr = spefa.iterateSequenceTransitions(); itr.hasNext();){
                SpTransition tran = itr.next();
                
                String guard = parsGuards(tran.getConditionGuard());
                String action = parsActions(tran.getConditionAction());
                String actionLocation = varName + ConditionStatment.Operator.Assign + Integer.toString(++count) + EFAVariables.EFA_ACTION_DIVIDER;
                action += actionLocation;

                efa.addTransition(tran.getFrom().getName(), 
                                  tran.getTo().getName(), 
                                  tran.getEvent().getName(), 
                                  guard, 
                                  action);
            }
            automata.addEFAutomaton(efa);
            
            if(((sp.getAutomatons().size() * 2) + sp.getVariables().size()) == automata.getEFAutomatons().size())
                result = true;
        }
        return result;
    }

    private String parsGuards(ConditionExpression cex){
        if (cex.isEmpty())
            return "";
        
        String result = "(";
        for (ConditionElement e : cex){

            if(e.isExpression())
               result += parsGuards((ConditionExpression)e);

            if(e.isStatment())
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
    
    @Override
    public DefaultEFAutomata getEFAutomata(){
        return automata;
    }
    
    @Override
    public Module getModule(){
        return automata.getThisModule();
    }
}
