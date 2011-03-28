//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2.3-3- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.28 at 07:52:24 em CEST 
//


package sequenceplanner.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for operationData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operationData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="isPreoperation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isPostoperation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="RealizedBy" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Accomplishes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="preSequenceCondtions" type="{}conditions"/>
 *         &lt;element name="preResurceBooking" type="{}bookings"/>
 *         &lt;element name="preActions" type="{}actions"/>
 *         &lt;element name="sequenceInvariants" type="{}conditions"/>
 *         &lt;element name="actionInvariants" type="{}actions"/>
 *         &lt;element name="properties" type="{}properties"/>
 *         &lt;element name="postSequenceCondtions" type="{}conditions"/>
 *         &lt;element name="postResurceBooking" type="{}bookings"/>
 *         &lt;element name="postActions" type="{}actions"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operationData", propOrder = {
    "description",
    "isPreoperation",
    "isPostoperation",
    "cost",
    "realizedBy",
    "accomplishes",
    "preSequenceCondtions",
    "preResurceBooking",
    "preActions",
    "sequenceInvariants",
    "actionInvariants",
    "properties",
    "postSequenceCondtions",
    "postResurceBooking",
    "postActions"
})
public class OperationData {

    @XmlElement(required = true)
    protected String description;
    protected boolean isPreoperation;
    protected boolean isPostoperation;
    protected double cost;
    @XmlElement(name = "RealizedBy")
    protected int realizedBy;
    @XmlElement(name = "Accomplishes")
    protected int accomplishes;
    @XmlElement(required = true)
    protected Conditions preSequenceCondtions;
    @XmlElement(required = true)
    protected Bookings preResurceBooking;
    @XmlElement(required = true)
    protected Actions preActions;
    @XmlElement(required = true)
    protected Conditions sequenceInvariants;
    @XmlElement(required = true)
    protected Actions actionInvariants;
    @XmlElement(required = true)
    protected Properties properties;
    @XmlElement(required = true)
    protected Conditions postSequenceCondtions;
    @XmlElement(required = true)
    protected Bookings postResurceBooking;
    @XmlElement(required = true)
    protected Actions postActions;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the isPreoperation property.
     * 
     */
    public boolean isIsPreoperation() {
        return isPreoperation;
    }

    /**
     * Sets the value of the isPreoperation property.
     * 
     */
    public void setIsPreoperation(boolean value) {
        this.isPreoperation = value;
    }

    /**
     * Gets the value of the isPostoperation property.
     * 
     */
    public boolean isIsPostoperation() {
        return isPostoperation;
    }

    /**
     * Sets the value of the isPostoperation property.
     * 
     */
    public void setIsPostoperation(boolean value) {
        this.isPostoperation = value;
    }

    /**
     * Gets the value of the cost property.
     * 
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the value of the cost property.
     * 
     */
    public void setCost(double value) {
        this.cost = value;
    }

    /**
     * Gets the value of the realizedBy property.
     * 
     */
    public int getRealizedBy() {
        return realizedBy;
    }

    /**
     * Sets the value of the realizedBy property.
     * 
     */
    public void setRealizedBy(int value) {
        this.realizedBy = value;
    }

    /**
     * Gets the value of the accomplishes property.
     * 
     */
    public int getAccomplishes() {
        return accomplishes;
    }

    /**
     * Sets the value of the accomplishes property.
     * 
     */
    public void setAccomplishes(int value) {
        this.accomplishes = value;
    }

    /**
     * Gets the value of the preSequenceCondtions property.
     * 
     * @return
     *     possible object is
     *     {@link Conditions }
     *     
     */
    public Conditions getPreSequenceCondtions() {
        return preSequenceCondtions;
    }

    /**
     * Sets the value of the preSequenceCondtions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Conditions }
     *     
     */
    public void setPreSequenceCondtions(Conditions value) {
        this.preSequenceCondtions = value;
    }

    /**
     * Gets the value of the preResurceBooking property.
     * 
     * @return
     *     possible object is
     *     {@link Bookings }
     *     
     */
    public Bookings getPreResurceBooking() {
        return preResurceBooking;
    }

    /**
     * Sets the value of the preResurceBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bookings }
     *     
     */
    public void setPreResurceBooking(Bookings value) {
        this.preResurceBooking = value;
    }

    /**
     * Gets the value of the preActions property.
     * 
     * @return
     *     possible object is
     *     {@link Actions }
     *     
     */
    public Actions getPreActions() {
        return preActions;
    }

    /**
     * Sets the value of the preActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Actions }
     *     
     */
    public void setPreActions(Actions value) {
        this.preActions = value;
    }

    /**
     * Gets the value of the sequenceInvariants property.
     * 
     * @return
     *     possible object is
     *     {@link Conditions }
     *     
     */
    public Conditions getSequenceInvariants() {
        return sequenceInvariants;
    }

    /**
     * Sets the value of the sequenceInvariants property.
     * 
     * @param value
     *     allowed object is
     *     {@link Conditions }
     *     
     */
    public void setSequenceInvariants(Conditions value) {
        this.sequenceInvariants = value;
    }

    /**
     * Gets the value of the actionInvariants property.
     * 
     * @return
     *     possible object is
     *     {@link Actions }
     *     
     */
    public Actions getActionInvariants() {
        return actionInvariants;
    }

    /**
     * Sets the value of the actionInvariants property.
     * 
     * @param value
     *     allowed object is
     *     {@link Actions }
     *     
     */
    public void setActionInvariants(Actions value) {
        this.actionInvariants = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the postSequenceCondtions property.
     * 
     * @return
     *     possible object is
     *     {@link Conditions }
     *     
     */
    public Conditions getPostSequenceCondtions() {
        return postSequenceCondtions;
    }

    /**
     * Sets the value of the postSequenceCondtions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Conditions }
     *     
     */
    public void setPostSequenceCondtions(Conditions value) {
        this.postSequenceCondtions = value;
    }

    /**
     * Gets the value of the postResurceBooking property.
     * 
     * @return
     *     possible object is
     *     {@link Bookings }
     *     
     */
    public Bookings getPostResurceBooking() {
        return postResurceBooking;
    }

    /**
     * Sets the value of the postResurceBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bookings }
     *     
     */
    public void setPostResurceBooking(Bookings value) {
        this.postResurceBooking = value;
    }

    /**
     * Gets the value of the postActions property.
     * 
     * @return
     *     possible object is
     *     {@link Actions }
     *     
     */
    public Actions getPostActions() {
        return postActions;
    }

    /**
     * Sets the value of the postActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Actions }
     *     
     */
    public void setPostActions(Actions value) {
        this.postActions = value;
    }

}
