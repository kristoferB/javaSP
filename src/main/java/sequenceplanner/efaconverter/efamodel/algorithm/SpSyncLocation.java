package sequenceplanner.efaconverter.efamodel.algorithm;

import java.util.HashMap;
import java.util.Map;

import sequenceplanner.condition.ConditionStatement;
import sequenceplanner.efaconverter.efamodel.SpLocation;
import sequenceplanner.efaconverter.efamodel.SpTransition;

/**
 *
 * @author kbe
 */
public class SpSyncLocation extends SpLocation {
    
    private Map<String,String> variableValues;
    private Map<String,String> localLocations;
    private Map<String,SpTransition> possibleGuardedOutTransitions;
    private boolean multipleValues = false;

    /**
     *
     * @param locationName
     */
    public SpSyncLocation(String locationName) {
        super(locationName);
        this.variableValues = new HashMap<String, String>();
        this.localLocations = new HashMap<String, String>();
        this.possibleGuardedOutTransitions = new HashMap<String, SpTransition>();
    }

    /**
     *
     * @return
     */
    public Map<String, SpTransition> getPossibleGuardedOutTransitions() {
        return possibleGuardedOutTransitions;
    }

    /**
     *
     * @param possibleGuardedOutTransitions
     */
    public void setPossibleGuardedOutTransitions(Map<String, SpTransition> possibleGuardedOutTransitions) {
        this.possibleGuardedOutTransitions.putAll(possibleGuardedOutTransitions);
    }

    /**
     *
     * @param name
     * @param trans
     */
    public void addPossibleGuardedOutTransition(String name, SpTransition trans){
        this.possibleGuardedOutTransitions.put(name, trans);
    }

    /**
     *
     * @return
     */
    public Map<String, String> getLocalLocations() {
        return localLocations;
    }

    /**
     *
     * @param syncEFALocations
     */
    public void setLocalLocations(Map<String, String> syncEFALocations) {
        this.localLocations.putAll(syncEFALocations);
    }

    /**
     *
     * @param name
     * @param location
     */
    public void addLocalLocation(String name, String location) {
        this.localLocations.put(name, location);
    }

    /**
     *
     * @return
     */
    public Map<String, String> getVariableValues() {
        return variableValues;
    }


    /**
     * Adds the variables in the Map where Map<String, String> should store
     * the <variable name, variable value>.
     * @param variableValues A Map where the key is the variableName and the
     * value is the variable value (both as strings even if the value is an int).
     * @return true if the value was added correct, false if a value already
     * exists and ConditionStatement.MULTIPLE_VALUES was added instead.
     */
    public boolean setVariableValues(Map<String, String> variableValues) {
        if (variableValues == null) return true;
        boolean result = true;
        for (String varName : variableValues.keySet()){
            result = addVariableValue(varName,variableValues.get(varName)) && result;
        }
        return result;
    }


    /**
     * The method adds a variable and its value to This location. If the location
     * already has the variable, but with another value, the value will be changed
     * to the constant {%link ConditionStatement.MULTIPLE_VALUES} and the method
     * will return false.
     * @param variableName The name of the variable
     * @param variableValue The value of the variable in this location
     * @return true if the value was added correct, false if a value already
     * exists and ConditionStatement.MULTIPLE_VALUES was added instead.
     */
    public boolean addVariableValue(String variableName, String variableValue) {
        if (variableName == null || variableValue == null)
            throw new NullPointerException();
        if (this.variableValues.containsKey(variableName)) {
            if (!this.variableValues.get(variableName).equals(variableValue)) {
                this.variableValues.put(variableName, ConditionStatement.MULTIPLE_VALUES);
                this.multipleValues = true;
                return false;
            }
            return true;
        } else {
            this.variableValues.put(variableName, variableValue);
            return true;
        }
    }

    /**
     *
     * @param variableName
     * @param variableValue
     * @return
     */
    public boolean hasSameVariableValue(String variableName, String variableValue){
        return hasSameValue(variableName,variableValue,this.variableValues);
    }

    /**
     *
     * @param values
     * @return
     */
    public boolean hasSameVariableValues(Map<String, String> values){
        return hasSameValues(values,this.variableValues);
    }

    /**
     *
     * @param efaName
     * @param locationName
     * @return
     */
    public boolean hasSameLocalLocation(String efaName, String locationName){
        return hasSameValue(efaName, locationName, this.localLocations);
    }

    /**
     *
     * @param efaLocations
     * @return
     */
    public boolean hasSameLocalLocations(Map<String, String> efaLocations){
        return hasSameValues(efaLocations,this.localLocations);
    }

    public boolean hasMultipleValues(){
        return this.hasMultipleValues();
    }



    private boolean hasSameValue(String name, String value, Map<String,String> localValues){
        return value.equals(localValues.get(name));
    }

    private boolean hasSameValues(Map<String, String> values, Map<String, String> localValues){
        for (String vName : values.keySet()){
            if (localValues.containsKey(vName)){
                if (!values.get(vName).equals(localValues.get(vName))){
                    return false;
                }
            }
        }
        return true;
    }







}
