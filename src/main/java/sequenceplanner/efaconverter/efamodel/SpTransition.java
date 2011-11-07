
package sequenceplanner.efaconverter.efamodel;

import sequenceplanner.datamodel.condition.Condition;
import sequenceplanner.datamodel.condition.ConditionExpression;

/**
 *
 * @author kbe
 */
public class SpTransition {

    SpEvent event;
    String guard = new String();
    String action = new String();
    Condition condition;
    SpLocation from;
    SpLocation to;


    public SpTransition(String eventLabel,SpLocation from, SpLocation to) {
        this.event = new SpEvent(eventLabel);
        this.from = from;
        this.to = to;
        condition = new Condition();
    }

    public SpTransition(String eventLabel,SpLocation from, SpLocation to,Condition transitionCondition) {
        this.event = new SpEvent(eventLabel);
        this.from = from;
        this.to = to;
        condition = transitionCondition;
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



}
