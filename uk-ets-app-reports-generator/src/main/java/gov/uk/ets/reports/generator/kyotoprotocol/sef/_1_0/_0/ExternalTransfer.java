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
 *       &lt;attribute name="registry" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="2"/>
 *             &lt;maxLength value="3"/>
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
        "additions",
        "subtractions"
})
@XmlRootElement(name = "ExternalTransfer")
public class ExternalTransfer {

    @XmlElement(name = "Additions", required = true)
    protected Additions additions;
    @XmlElement(name = "Subtractions", required = true)
    protected Subtractions subtractions;
    @XmlAttribute(name = "registry", required = true)
    protected String registry;

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
     * Gets the value of the registry property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRegistry() {
        return registry;
    }

    /**
     * Sets the value of the registry property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRegistry(String value) {
        this.registry = value;
    }

}
