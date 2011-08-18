//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2-70- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.15 at 04:02:06 em CEST 
//


package sequenceplanner.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idCounter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="GlobalProperties">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}globalProperty" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Resources">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}resource" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Liasons">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}liason" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Operations">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}operation" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="OperationViews" type="{}viewType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Views">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="view" type="{}viewType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute ref="{}owner"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "idCounter",
    "globalProperties",
    "resources",
    "liasons",
    "operations",
    "views"
})
@XmlRootElement(name = "SequencePlannerProjectFile")
public class SequencePlannerProjectFile {

    protected int idCounter;
    @XmlElement(name = "GlobalProperties", required = true)
    protected SequencePlannerProjectFile.GlobalProperties globalProperties;
    @XmlElement(name = "Resources", required = true)
    protected SequencePlannerProjectFile.Resources resources;
    @XmlElement(name = "Liasons", required = true)
    protected SequencePlannerProjectFile.Liasons liasons;
    @XmlElement(name = "Operations", required = true)
    protected SequencePlannerProjectFile.Operations operations;
    @XmlElement(name = "Views", required = true)
    protected SequencePlannerProjectFile.Views views;
    @XmlAttribute(name = "owner")
    protected String owner;

    /**
     * Gets the value of the idCounter property.
     * 
     */
    public int getIdCounter() {
        return idCounter;
    }

    /**
     * Sets the value of the idCounter property.
     * 
     */
    public void setIdCounter(int value) {
        this.idCounter = value;
    }

    /**
     * Gets the value of the globalProperties property.
     * 
     * @return
     *     possible object is
     *     {@link SequencePlannerProjectFile.GlobalProperties }
     *     
     */
    public SequencePlannerProjectFile.GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    /**
     * Sets the value of the globalProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencePlannerProjectFile.GlobalProperties }
     *     
     */
    public void setGlobalProperties(SequencePlannerProjectFile.GlobalProperties value) {
        this.globalProperties = value;
    }

    /**
     * Gets the value of the resources property.
     * 
     * @return
     *     possible object is
     *     {@link SequencePlannerProjectFile.Resources }
     *     
     */
    public SequencePlannerProjectFile.Resources getResources() {
        return resources;
    }

    /**
     * Sets the value of the resources property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencePlannerProjectFile.Resources }
     *     
     */
    public void setResources(SequencePlannerProjectFile.Resources value) {
        this.resources = value;
    }

    /**
     * Gets the value of the liasons property.
     * 
     * @return
     *     possible object is
     *     {@link SequencePlannerProjectFile.Liasons }
     *     
     */
    public SequencePlannerProjectFile.Liasons getLiasons() {
        return liasons;
    }

    /**
     * Sets the value of the liasons property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencePlannerProjectFile.Liasons }
     *     
     */
    public void setLiasons(SequencePlannerProjectFile.Liasons value) {
        this.liasons = value;
    }

    /**
     * Gets the value of the operations property.
     * 
     * @return
     *     possible object is
     *     {@link SequencePlannerProjectFile.Operations }
     *     
     */
    public SequencePlannerProjectFile.Operations getOperations() {
        return operations;
    }

    /**
     * Sets the value of the operations property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencePlannerProjectFile.Operations }
     *     
     */
    public void setOperations(SequencePlannerProjectFile.Operations value) {
        this.operations = value;
    }

    /**
     * Gets the value of the views property.
     * 
     * @return
     *     possible object is
     *     {@link SequencePlannerProjectFile.Views }
     *     
     */
    public SequencePlannerProjectFile.Views getViews() {
        return views;
    }

    /**
     * Sets the value of the views property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequencePlannerProjectFile.Views }
     *     
     */
    public void setViews(SequencePlannerProjectFile.Views value) {
        this.views = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{}globalProperty" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "globalProperty"
    })
    public static class GlobalProperties {

        protected List<GlobalProperty> globalProperty;

        /**
         * Gets the value of the globalProperty property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the globalProperty property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGlobalProperty().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GlobalProperty }
         * 
         * 
         */
        public List<GlobalProperty> getGlobalProperty() {
            if (globalProperty == null) {
                globalProperty = new ArrayList<GlobalProperty>();
            }
            return this.globalProperty;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{}liason" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "liason"
    })
    public static class Liasons {

        protected List<Liason> liason;

        /**
         * Gets the value of the liason property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the liason property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLiason().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Liason }
         * 
         * 
         */
        public List<Liason> getLiason() {
            if (liason == null) {
                liason = new ArrayList<Liason>();
            }
            return this.liason;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{}operation" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="OperationViews" type="{}viewType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "operation",
        "operationViews"
    })
    public static class Operations {

        protected List<Operation> operation;
        @XmlElement(name = "OperationViews")
        protected List<ViewType> operationViews;

        /**
         * Gets the value of the operation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the operation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOperation().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Operation }
         * 
         * 
         */
        public List<Operation> getOperation() {
            if (operation == null) {
                operation = new ArrayList<Operation>();
            }
            return this.operation;
        }

        /**
         * Gets the value of the operationViews property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the operationViews property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOperationViews().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ViewType }
         * 
         * 
         */
        public List<ViewType> getOperationViews() {
            if (operationViews == null) {
                operationViews = new ArrayList<ViewType>();
            }
            return this.operationViews;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{}resource" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "resource"
    })
    public static class Resources {

        protected List<Resource> resource;

        /**
         * Gets the value of the resource property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resource property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResource().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Resource }
         * 
         * 
         */
        public List<Resource> getResource() {
            if (resource == null) {
                resource = new ArrayList<Resource>();
            }
            return this.resource;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="view" type="{}viewType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "view"
    })
    public static class Views {

        protected List<ViewType> view;

        /**
         * Gets the value of the view property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the view property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getView().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ViewType }
         * 
         * 
         */
        public List<ViewType> getView() {
            if (view == null) {
                view = new ArrayList<ViewType>();
            }
            return this.view;
        }

    }

}
