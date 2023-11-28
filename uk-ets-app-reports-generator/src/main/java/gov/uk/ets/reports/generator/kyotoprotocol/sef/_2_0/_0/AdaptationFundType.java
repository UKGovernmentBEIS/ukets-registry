package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AmountTransferred"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AmountContributed"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AdaptationFundTypeEnum" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "amountTransferred",
        "amountContributed"
})
@XmlRootElement(name = "AdaptationFundType")
public class AdaptationFundType {

    @XmlElement(name = "AmountTransferred", required = true)
    protected AmountTransferred amountTransferred;
    @XmlElement(name = "AmountContributed", required = true)
    protected AmountContributed amountContributed;
    @XmlAttribute(name = "name", required = true)
    protected AdaptationFundTypeEnum name;

    /**
     * Gets the value of the amountTransferred property.
     *
     * @return
     *     possible object is
     *     {@link AmountTransferred }
     *
     */
    public AmountTransferred getAmountTransferred() {
        return amountTransferred;
    }

    /**
     * Sets the value of the amountTransferred property.
     *
     * @param value
     *     allowed object is
     *     {@link AmountTransferred }
     *
     */
    public void setAmountTransferred(AmountTransferred value) {
        this.amountTransferred = value;
    }

    /**
     * Gets the value of the amountContributed property.
     *
     * @return
     *     possible object is
     *     {@link AmountContributed }
     *
     */
    public AmountContributed getAmountContributed() {
        return amountContributed;
    }

    /**
     * Sets the value of the amountContributed property.
     *
     * @param value
     *     allowed object is
     *     {@link AmountContributed }
     *
     */
    public void setAmountContributed(AmountContributed value) {
        this.amountContributed = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link AdaptationFundTypeEnum }
     *
     */
    public AdaptationFundTypeEnum getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link AdaptationFundTypeEnum }
     *
     */
    public void setName(AdaptationFundTypeEnum value) {
        this.name = value;
    }

}
