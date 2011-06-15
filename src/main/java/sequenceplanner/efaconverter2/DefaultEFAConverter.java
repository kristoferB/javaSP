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
            DefaultEFAutomaton var = new DefaultEFAutomaton(createVariableName(spv.getName()), automata);
            var.addVariable(spv.getMin(), spv.getMax(), spv.getInit());
        }
        
        for(SpEFA spefa : sp.getAutomatons()){
            String efaName = createEFAName(spefa.getName());
            String varName = createVariableName(efaName);
            DefaultEFAutomaton efa = new DefaultEFAutomaton(efaName, automata);
            DefaultEFAutomaton var = new DefaultEFAutomaton(varName, automata);
            var.addVariable(0, spefa.getLocations().size() - 1, 0);
            
            for(SpLocation spl : spefa.getLocations()){
                efa.addLocation(createEFAName(spl.getName()), spl.isAccepting(), spl.isInitialLocation());
            }
            
            for(SpEvent spe : spefa.getAlphabet())
                efa.addEvent(createEventName(spe.getName()), spe.isControllable());
            
            int count = 0;
            for(Iterator<SpTransition> itr = spefa.iterateSequenceTransitions(); itr.hasNext();){
                SpTransition tran = itr.next();
                
                String guard = parsGuards(tran.getConditionGuard());
                String action = parsActions(tran.getConditionAction());
                String actionLocation = varName + ConditionStatment.Operator.Assign + Integer.toString(++count) + EFAVariables.EFA_ACTION_DIVIDER;
//                if(tran.getTo().getValue() < 0 && tran.getTo().getValue() != count)
//                    throw new SecurityException("DefaultEFAConverter class: < "+ tran.getTo().getName() + " -- "+ tran.getTo().getValue() + "," + count +" >location value is not equal to counter value. Converter consistency error.");
                
                action += actionLocation;

                efa.addTransition(createEFAName(tran.getFrom().getName()), 
                                  createEFAName(tran.getTo().getName()), 
                                  createEventName(tran.getEvent().getName()), 
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
                result += statmentToString((ConditionStatment)e);

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
            result += statmentToString((ConditionStatment)e) + EFAVariables.EFA_ACTION_DIVIDER;
        }
        return result;
    }
    
    private String createEFAName(String iOperationName) {
        return EFAVariables.OPERATION_NAME_PREFIX + iOperationName;
    }

    private String createVariableName(String iVariableName) {
        return EFAVariables.VARIABLE_NAME_PREFIX + iVariableName;
    }

    private String createLocVarName(String iOperationName) {
        return createVariableName(createEFAName(iOperationName));
    }
    
    private String statmentToString(ConditionStatment e){
        String result = "";
        if (isOperation(e.getVariable()))
            result = createLocVarName(e.getVariable()) + e.getOperator() + e.getValue();
        else
            result = createVariableName(e.getVariable()) + e.getOperator() + e.getValue();
        
        return result;
    }
    
    private boolean isOperation(String id){
        boolean result = false;
        for(SpEFA efa : sp.getAutomatons())
            if(efa.getName().equals(id))
                result = true;
        return result;
    }
    @Override
    public DefaultEFAutomata getEFAutomata(){
        return automata;
    }
    
    @Override
    public Module getModule(){
        return automata.getThisModule();
    }

    private String createEventName(String name) {
        String result = "";
        if (name.contains("Start_"))
            result =  name.replace("Start_", "Start_" + EFAVariables.OPERATION_NAME_PREFIX);
        else if (name.contains("Stop_"))
                result =  name.replace("Stop_", "Stop_" + EFAVariables.OPERATION_NAME_PREFIX);
        return result;
    }
}
