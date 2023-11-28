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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Additions"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}Subtractions"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}StartingValueEnum" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "additions",
        "subtractions"
})
@XmlRootElement(name = "StartingValue")
public class StartingValue {

    @XmlElement(name = "Additions", required = true)
    protected Additions additions;
    @XmlElement(name = "Subtractions", required = true)
    protected Subtractions subtractions;
    @XmlAttribute(name = "name", required = true)
    protected StartingValueEnum name;

    /**
     * Gets the value of the additions property.
     *
     * @return
     *     possible object is
     *     {@link Additions }
     *
     */
    public Additions getAdditions() {
        return additions;
    }

    /**
     * Sets the value of the additions property.
     *
     * @param value
     *     allowed object is
     *     {@link Additions }
     *
     */
    public void setAdditions(Additions value) {
        this.additions = value;
    }

    /**
     * Gets the value of the subtractions property.
     *
     * @return
     *     possible object is
     *     {@link Subtractions }
     *
     */
    public Subtractions getSubtractions() {
        return subtractions;
    }

    /**
     * Sets the value of the subtractions property.
     *
     * @param value
     *     allowed object is
     *     {@link Subtractions }
     *
     */
    public void setSubtractions(Subtractions value) {
        this.subtractions = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link StartingValueEnum }
     *
     */
    public StartingValueEnum getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link StartingValueEnum }
     *
     */
    public void setName(StartingValueEnum value) {
        this.name = value;
    }

}
