package sequenceplanner.efaconverter.efamodel.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.ConditionOperator;
import sequenceplanner.datamodel.condition.ConditionStatement;
import sequenceplanner.efaconverter.efamodel.SpEFA;
import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.efaconverter.efamodel.SpEvent;
import sequenceplanner.efaconverter.efamodel.SpLocation;
import sequenceplanner.efaconverter.efamodel.SpTransition;
import sequenceplanner.efaconverter.efamodel.SpVariable;

/**
 * OLD! NOT WORKING. REMOVE
 * @author kbe
 */
public class SpEFASync {

    private SpEFAutomata automata;

    private HashMap<String, SpSyncLocation> locationsToCheck;
    private HashMap<String, SpSyncLocation> checkedLocations;



    public SpEFASync(SpEFAutomata automata) {
        this.automata = automata;
        this.locationsToCheck = new HashMap<String, SpSyncLocation>();
        this.checkedLocations = new HashMap<String, SpSyncLocation>();
    }

    /**
     *
     * @return
     */
    public SpEFA syncAutomata(){
        SpEFA synken = new SpEFA("Synk");
        HashMap<String, String> initLocalLocations = new HashMap<String, String>();
        for (SpEFA a : automata.getAutomatons()){
            initLocalLocations.put(a.getName(), a.getInitialLocation().getName());
        }
        String initStateName = createLocationName(initLocalLocations);
        SpSyncLocation initLocation = new SpSyncLocation(initStateName);
        initLocation.setLocalLocations(initLocalLocations);
        initLocation.setVariableValues(createInitVariableMap(automata));

        synken.addLocation(initLocation, true);
        locationsToCheck.put(initStateName, initLocation);

        while (!this.locationsToCheck.isEmpty()){
            SpSyncLocation location = null;
            for (SpSyncLocation l : locationsToCheck.values()){
                location = l;
                break;
            }
            if (location == null) break;

            createLocationsAndTransitions(identifyEnabledEvents(location),location,synken);


        }


        return synken;
    }
    
    private Map<String,String> createInitVariableMap(SpEFAutomata efa){
        Map<String,String> variables = new HashMap<String,String>();
        for (SpVariable v : efa.getVariables()){
            variables.put(v.getName(), Integer.toString(v.getInit()));
        }        
        return variables;
    }


    private Map<String,Map<String,SpTransition>> identifyEnabledEvents(SpSyncLocation location){
        // map<eventName, map<localEFAName, outTransition>>
        Map<String,Map<String,SpTransition>> enabledEvents = new HashMap<String,Map<String,SpTransition>>();
        Set<String> disabledEvents = new HashSet<String>();

        // Identify all enabled events
        for (SpEFA localEFA : automata.getAutomatons()){
            SpLocation localLocation = localEFA.getLocation(location.getLocalLocations().get(localEFA.getName()));
            Set<String> localEnabledEvents = new HashSet<String>();
            Map<String,String> varList = new HashMap<String,String>(location.getLocalLocations());
            varList.putAll(location.getVariableValues());
            // Check all outgoing events in the local EFA
            for (SpTransition outTrans : localLocation.getOutTransitions()){
                if (!disabledEvents.contains(outTrans.getEventLabel()) && evaluateGuard(outTrans.getCondition(),varList)){
                    localEnabledEvents.add(outTrans.getEventLabel());
                    if (enabledEvents.get(outTrans.getEventLabel()) != null){
                        enabledEvents.get(outTrans.getEventLabel()).put(localEFA.getName(), outTrans);
                    } else {
                        HashMap<String,SpTransition> efaLoc = new HashMap<String,SpTransition>();
                        efaLoc.put(localEFA.getName(), outTrans);
                        enabledEvents.put(outTrans.getEventLabel(), efaLoc);
                    }
                }
            }
            // Disable all events included in the local EFA alphabet but
            // not in current local location.
            for (SpEvent e : localEFA.getAlphabet()){
                if (!localEnabledEvents.contains(e.getName())){
                    disabledEvents.add(e.getName());
                }
            }
        }
        // Remove events that this local EFA has disabled.
        for (String dE : disabledEvents){
            if (enabledEvents.containsKey(dE)){
                enabledEvents.remove(dE);
            }
        }

        return enabledEvents;

    }


    private void createLocationsAndTransitions(Map<String,Map<String,SpTransition>> enabledEvents,
                                      SpSyncLocation location,
                                      SpEFA synken)
    {

        for (String event : enabledEvents.keySet()){
            boolean eventExists = false;
            // Check if this event already is created (if the location is revisited)
            for (SpTransition t :location.getOutTransitions()){
                eventExists = eventExists || t.getEventLabel().equals(event);
                if (eventExists) break;
            }
            if (!eventExists) {
                // Create location or get previous location
                String nextLocationName = getNextLocationName(enabledEvents.get(event), location);
                SpSyncLocation newLocation = null;
                boolean isACheckedLocation = false;
                if (this.checkedLocations.containsKey(nextLocationName)) {
                    newLocation = checkedLocations.remove(nextLocationName);
                    isACheckedLocation = true;
                } else if (this.locationsToCheck.containsKey(nextLocationName)) {
                    newLocation = locationsToCheck.remove(nextLocationName);
                } else {
                    newLocation = new SpSyncLocation(nextLocationName);
                }

                Condition transitionCondition = newTransitionCondition(enabledEvents.get(event));
                newLocation.setVariableValues(getUpdatedVariableValues(transitionCondition,location.getVariableValues()));
                if (newLocation.getLocalLocations().isEmpty())
                    newLocation.setLocalLocations(updateLocalLocations(enabledEvents.get(event)));

                SpTransition newTransition = new SpTransition(event, location, newLocation,transitionCondition);
                synken.addTransition(newTransition);

                if (!newLocation.hasMultipleValues() && isACheckedLocation){
                    this.checkedLocations.put(newLocation.getName(), newLocation);
                } else {
                    locationsToCheck.put(newLocation.getName(), newLocation);
                }

                this.checkedLocations.put(location.getName(), location);


            }

        }
    }


    private String getNextLocationName(Map<String,SpTransition> transitions, SpSyncLocation currentLocation){
        Map<String, String> local = new HashMap<String, String>(currentLocation.getLocalLocations());
        for (String efa : transitions.keySet()){
            SpTransition t = transitions.get(efa);
            SpLocation l = t.getTo();
            if (l != null){
                local.put(efa, l.getName());
            }
        }
        return createLocationName(local);
    }

    private Condition newTransitionCondition(Map<String,SpTransition> localTransitions){
        Condition cond = new Condition();
        for (SpTransition trans : localTransitions.values()){
            if (!cond.getGuard().containsElement(trans.getConditionGuard()))
                cond.getGuard().appendElement(ConditionOperator.Type.AND, trans.getConditionGuard());
            if (!cond.getAction().containsElement(trans.getConditionAction()))
                cond.getAction().appendElement(ConditionOperator.Type.AND, trans.getConditionAction());
        }
        return cond;
    }


    private Map<String,String> updateLocalLocations(Map<String,SpTransition> local){
        Map<String,String> result = new HashMap<String, String>();
        for (String localEFA : local.keySet()){
            result.put(localEFA, local.get(localEFA).getTo().getName());
        }
        return result;
    }



    private String createLocationName(Map<String, String> locations){
        String s = new String();       
        for ( Iterator<String> i = locations.keySet().iterator(); i.hasNext();){
            String efaName = i.next();
            s += locations.get(efaName); // efaName + ":" + locations.get(efaName);
            if (i.hasNext()) s += "||";
        }
        return s;
    }
    
    
    private boolean evaluateGuard(Condition c,Map<String,String> variableValues){
        if (variableValues == null || variableValues.isEmpty()) return true;
        if (c.getGuard().isEmpty()) return true;
        return reqGuardEvaluater(c.getGuard(),variableValues);
    }

    private Map<String, String> getUpdatedVariableValues(Condition c,Map<String, String> varibleValues){
        if (varibleValues == null) return new HashMap<String, String>();

        Map<String,String> newVars = variableUpdater(c.getAction(),varibleValues);
        

        return newVars;
    }
    
    
        private boolean reqGuardEvaluater(ConditionElement element, Map<String,String> variableValues){
        if (element == null) return true;
        boolean elementBoolean = true;
        if (element.isExpression()){
            ConditionExpression ce = (ConditionExpression) element;
            elementBoolean = reqGuardEvaluater(ce.getExpressionRoot(), variableValues);
        }else if (element.isStatement()){
            elementBoolean = validateStatment((ConditionStatement)element,variableValues);
        }
        if (element.hasNextElement()){
            if (element.getNextOperator().isOperationType(ConditionOperator.Type.AND)){
                elementBoolean = elementBoolean && reqGuardEvaluater(element.getNextElement(),variableValues);
            } else if (element.getNextOperator().isOperationType(ConditionOperator.Type.OR)){
                elementBoolean = elementBoolean || reqGuardEvaluater(element.getNextElement(),variableValues);
            }
        }
        return elementBoolean;
    }



    private boolean validateStatment(ConditionStatement statment, Map<String,String> variableValues){
        String var = statment.getVariable();
        if (variableValues.containsKey(var)){
            //return statment.evaluate(variableValues.get(var));
        }
        return true;
    }

    private Map<String, String> variableUpdater(ConditionExpression ce, Map<String,String> oldVars){
        Map<String, String> newVars = new HashMap<String, String>(oldVars);
        for (ConditionElement e : ce) {
            if (e.isExpression()) {
                newVars.putAll(variableUpdater((ConditionExpression) e, oldVars));
            } else if (e.isStatement()){
                ConditionStatement cs =  (ConditionStatement) e;
                if (newVars.containsKey(cs.getVariable())){
                    //newVars.put(cs.getVariable(), cs.getNewVariableValue(newVars.get(cs.getValue())));
                }
            }
        }
        return newVars;
    }


}
