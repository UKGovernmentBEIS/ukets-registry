package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import jakarta.xml.bind.annotation.*;

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
 *       &lt;/sequence>
 *       &lt;attribute name="transactionNumber" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="[A-Z]{2,3}[1-9][0-9]{0,14}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
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
        "replacement"
})
@XmlRootElement(name = "CorrectiveReplacement")
public class CorrectiveReplacement {

    @XmlElement(name = "RequirementForReplacement", required = true)
    protected RequirementForReplacement requirementForReplacement;
    @XmlElement(name = "Replacement", required = true)
    protected Replacement replacement;
    @XmlAttribute(name = "transactionNumber", required = true)
    protected String transactionNumber;

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
     * Gets the value of the transactionNumber property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransactionNumber() {
        return transactionNumber;
    }

    /**
     * Sets the value of the transactionNumber property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransactionNumber(String value) {
        this.transactionNumber = value;
    }

}