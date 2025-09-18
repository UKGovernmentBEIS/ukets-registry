package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalUnitQty;

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
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:2.0:0.0}AnnualRetirements"/>
 *         &lt;element ref="{urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0}TotalUnitQty"/>
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
        "annualRetirements",
        "totalUnitQty"
})
@XmlRootElement(name = "Table5e")
public class Table5E {

    @XmlElement(name = "AnnualRetirements", required = true)
    protected AnnualRetirements annualRetirements;
    @XmlElement(name = "TotalUnitQty", namespace = "urn:KyotoProtocol:RegistrySystem:SEF:1.0:0.0", required = true)
    protected TotalUnitQty totalUnitQty;

    /**
     * Gets the value of the annualRetirements property.
     *
     * @return
     *     possible object is
     *     {@link AnnualRetirements }
     *
     */
    public AnnualRetirements getAnnualRetirements() {
        return annualRetirements;
    }

    /**
     * Sets the value of the annualRetirements property.
     *
     * @param value
     *     allowed object is
     *     {@link AnnualRetirements }
     *
     */
    public void setAnnualRetirements(AnnualRetirements value) {
        this.annualRetirements = value;
    }

    /**
     * Gets the value of the totalUnitQty property.
     *
     * @return
     *     possible object is
     *     {@link TotalUnitQty }
     *
     */
    public TotalUnitQty getTotalUnitQty() {
        return totalUnitQty;
    }

    /**
     * Sets the value of the totalUnitQty property.
     *
     * @param value
     *     allowed object is
     *     {@link TotalUnitQty }
     *
     */
    public void setTotalUnitQty(TotalUnitQty value) {
        this.totalUnitQty = value;
    }

}
