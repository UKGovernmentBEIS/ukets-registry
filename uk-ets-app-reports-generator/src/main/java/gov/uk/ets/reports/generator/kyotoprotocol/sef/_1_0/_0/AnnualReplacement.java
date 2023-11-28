package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

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
 *       &lt;/sequence>
 *       &lt;attribute name="year">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;minInclusive value="2008"/>
 *             &lt;maxInclusive value="2023"/>
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
@XmlRootElement(name = "AnnualReplacement")
public class AnnualReplacement {

    @XmlElement(name = "RequirementForReplacement", required = true)
    protected RequirementForReplacement requirementForReplacement;
    @XmlElement(name = "Replacement", required = true)
    protected Replacement replacement;
    @XmlAttribute(name = "year")
    protected Short year;

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
     * Gets the value of the year property.
     *
     * @return
     *     possible object is
     *     {@link Short }
     *
     */
    public Short getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     *
     * @param value
     *     allowed object is
     *     {@link Short }
     *
     */
    public void setYear(Short value) {
        this.year = value;
    }

}