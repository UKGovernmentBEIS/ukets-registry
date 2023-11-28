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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}PreviousCP"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}AnnualReplacements"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}TotalTxOrEvent"/>
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
        "previousCP",
        "annualReplacements",
        "totalTxOrEvent"
})
@XmlRootElement(name = "Table5b")
public class Table5B {

    @XmlElement(name = "PreviousCP", required = true)
    protected PreviousCP previousCP;
    @XmlElement(name = "AnnualReplacements", required = true)
    protected AnnualReplacements annualReplacements;
    @XmlElement(name = "TotalTxOrEvent", required = true)
    protected TotalTxOrEvent totalTxOrEvent;

    /**
     * Gets the value of the previousCP property.
     *
     * @return
     *     possible object is
     *     {@link PreviousCP }
     *
     */
    public PreviousCP getPreviousCP() {
        return previousCP;
    }

    /**
     * Sets the value of the previousCP property.
     *
     * @param value
     *     allowed object is
     *     {@link PreviousCP }
     *
     */
    public void setPreviousCP(PreviousCP value) {
        this.previousCP = value;
    }

    /**
     * Gets the value of the annualReplacements property.
     *
     * @return
     *     possible object is
     *     {@link AnnualReplacements }
     *
     */
    public AnnualReplacements getAnnualReplacements() {
        return annualReplacements;
    }

    /**
     * Sets the value of the annualReplacements property.
     *
     * @param value
     *     allowed object is
     *     {@link AnnualReplacements }
     *
     */
    public void setAnnualReplacements(AnnualReplacements value) {
        this.annualReplacements = value;
    }

    /**
     * Gets the value of the totalTxOrEvent property.
     *
     * @return
     *     possible object is
     *     {@link TotalTxOrEvent }
     *
     */
    public TotalTxOrEvent getTotalTxOrEvent() {
        return totalTxOrEvent;
    }

    /**
     * Sets the value of the totalTxOrEvent property.
     *
     * @param value
     *     allowed object is
     *     {@link TotalTxOrEvent }
     *
     */
    public void setTotalTxOrEvent(TotalTxOrEvent value) {
        this.totalTxOrEvent = value;
    }

}
