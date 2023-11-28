package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Replacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.RequirementForReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;

import javax.xml.bind.annotation.*;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}RequirementForReplacement"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Replacement"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}Cancellation"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}TransactionOrEventTypeEnum" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "requirementForReplacement",
        "replacement",
        "cancellation"
})
@XmlRootElement(name = "TransactionOrEventType")
public class TransactionOrEventType {

    @XmlElement(name = "RequirementForReplacement", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected RequirementForReplacement requirementForReplacement;
    @XmlElement(name = "Replacement", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected Replacement replacement;
    @XmlElement(name = "Cancellation", required = true)
    protected Cancellation cancellation;
    @XmlAttribute(name = "name", required = true)
    protected TransactionOrEventTypeEnum name;

    /**
     * Gets the value of the requirementForReplacement property.
     *
     * @return
     *     possible object is
     *     {@link RequirementForReplacement }
     *
     */
    public RequirementForReplacement getRequirementForReplacement() {
        return requirementForReplacement;
    }

    /**
     * Sets the value of the requirementForReplacement property.
     *
     * @param value
     *     allowed object is
     *     {@link RequirementForReplacement }
     *
     */
    public void setRequirementForReplacement(RequirementForReplacement value) {
        this.requirementForReplacement = value;
    }

    /**
     * Gets the value of the replacement property.
     *
     * @return
     *     possible object is
     *     {@link Replacement }
     *
     */
    public Replacement getReplacement() {
        return replacement;
    }

    /**
     * Sets the value of the replacement property.
     *
     * @param value
     *     allowed object is
     *     {@link Replacement }
     *
     */
    public void setReplacement(Replacement value) {
        this.replacement = value;
    }

    /**
     * Gets the value of the cancellation property.
     *
     * @return
     *     possible object is
     *     {@link Cancellation }
     *
     */
    public Cancellation getCancellation() {
        return cancellation;
    }

    /**
     * Sets the value of the cancellation property.
     *
     * @param value
     *     allowed object is
     *     {@link Cancellation }
     *
     */
    public void setCancellation(Cancellation value) {
        this.cancellation = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link TransactionOrEventTypeEnum }
     *
     */
    public TransactionOrEventTypeEnum getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link TransactionOrEventTypeEnum }
     *
     */
    public void setName(TransactionOrEventTypeEnum value) {
        this.name = value;
    }

}
