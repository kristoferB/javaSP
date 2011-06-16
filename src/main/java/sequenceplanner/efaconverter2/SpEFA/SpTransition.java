
package sequenceplanner.efaconverter2.SpEFA;

import sequenceplanner.efaconverter2.condition.Condition;
import sequenceplanner.efaconverter2.condition.ConditionExpression;


/**
 *
 * @author kbe
 */
public class SpTransition implements Cloneable{

    SpEvent event;
    String guard = new String();
    String action = new String();
    Condition condition;
    SpLocation from;
    SpLocation to;


    public SpTransition(SpEvent eventLabel,SpLocation from, SpLocation to, Condition transitionCondition) {
        this.event = eventLabel;
        this.from = from;
        this.to = to;
        condition = transitionCondition;
        this.guard = condition.getGuard().toString();
        this.action = condition.getAction().toString();
    }   

    public SpTransition(String eventLabel, String from, String to, String guard, String action) {
        this(new SpEvent(eventLabel),new SpLocation(from), new SpLocation(to), new Condition());
        this.action = action;
        this.guard = guard;
    }
   
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public SpLocation getFrom() {
        return from;
    }

    public void setFrom(SpLocation from) {
        this.from = from;
    }

    public SpLocation getTo() {
        return to;
    }

    public void setTo(SpLocation to) {
        this.to = to;
    }

    public String getEventLabel() {
        return event.getName();
    }

    public void setEventLabel(String eventLabel) {
        this.event = new SpEvent(eventLabel);
    }

    public SpEvent getEvent(){
        return event;
    }

    public void setEvent(SpEvent event){
        this.event = event;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public ConditionExpression getConditionGuard(){
        return condition.getGuard();
    }

    public ConditionExpression getConditionAction(){
        return condition.getAction();
    }

    @Override
    public String toString(){
        return '<' + this.from.toString() + ',' + this.getEventLabel() + ',' + this.to.toString() + '>';
    }

    @Override
    public SpTransition clone(){
        return new SpTransition(this.event, this.from, this.to, this.condition);
    }
    
    public boolean equal(Object obj){
        if(obj instanceof SpTransition){
            SpTransition t = (SpTransition)obj;
            if(this.from.equals(t.getFrom())
                    && this.to.equals(t.getTo())
                    && this.condition.equals(t.getCondition()))
                return true;
        }
        return false;
    }

}
