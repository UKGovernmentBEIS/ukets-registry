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
        "additions",
        "subtractions"
})
@XmlRootElement(name = "CorrectiveTransaction")
public class CorrectiveTransaction {

    @XmlElement(name = "Additions", required = true)
    protected Additions additions;
    @XmlElement(name = "Subtractions", required = true)
    protected Subtractions subtractions;
    @XmlAttribute(name = "transactionNumber", required = true)
    protected String transactionNumber;

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
