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
@XmlRootElement(name = "TotalTxOrEvent")
public class TotalTxOrEvent {

    @XmlElement(name = "RequirementForReplacement", required = true)
    protected RequirementForReplacement requirementForReplacement;
    @XmlElement(name = "Replacement", required = true)
    protected Replacement replacement;

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

}
